package io.github.aquerr.tablut.view;

import io.github.aquerr.tablut.TablutBoard;
import io.github.aquerr.tablut.TablutGame;
import io.github.aquerr.tablut.TablutPiece;
import io.github.aquerr.tablut.localization.Localization;
import io.github.aquerr.tablut.multiplayer.TablutGameHost;
import io.github.aquerr.tablut.multiplayer.TablutGameOnline;
import io.github.aquerr.tablut.multiplayer.TablutGameRemoteClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static io.github.aquerr.tablut.TablutBoard.BOARD_SIZE;
import static io.github.aquerr.tablut.TablutBoard.TILE_SIZE;

public class TablutGameGui extends Application
{
    private static TablutGameGui INSTANCE = null;
    public static TablutGameGui getGameGui()
    {
        return INSTANCE;
    }

    private static final TablutBoardTileView[][] BOARD_TILES = new TablutBoardTileView[BOARD_SIZE][BOARD_SIZE];

    private static final Logger logger = Logger.getLogger(TablutGameGui.class.getName());
    private static final int WINDOW_HEIGHT = 680;
    private static final int WINDOW_WIDTH = 660;
    private static final int MENU_BAR_HEIGHT = 20;

    private TablutGame tablutGame;
    private Stage primaryStage;
    private VBox root;
    private Group mainGroup;
    private Scene mainScene;
    private Group boardGroup;
    private Group piecesGroup;


    private Popup waitingForPlayerPopup;

    private boolean locked = false;

    public TablutGameGui()
    {
        if (INSTANCE != null)
            throw new IllegalStateException("The game is already started!");

        INSTANCE = this;
    }

    public void setup(Stage primaryStage)
    {
        initialSetup(primaryStage);
    }

    public TablutGame getTablutGame()
    {
        return tablutGame;
    }

    private void initialSetup(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.root = new VBox();
        this.mainScene = new Scene(this.root, WINDOW_WIDTH, WINDOW_HEIGHT);

        this.mainGroup = new Group();
        this.mainGroup.prefWidth(WINDOW_WIDTH);
        this.mainGroup.prefHeight(WINDOW_HEIGHT);

        setupMenu();
        setupBoard();

        this.root.getChildren().add(this.mainGroup);
        VBox.setMargin(this.mainGroup, new Insets(60, 0, 0, 60));

        this.primaryStage.setTitle(Localization.translate("game.title"));
        this.primaryStage.setOnCloseRequest(windowEvent -> closeGame());

        this.primaryStage.setScene(this.mainScene);
        this.primaryStage.show();
    }

    private void setupBoard()
    {
        this.boardGroup = new Group();
        this.piecesGroup = new Group();
        this.mainGroup.getChildren().add(this.boardGroup);
        this.mainGroup.getChildren().add(this.piecesGroup);
        this.boardGroup.setTranslateX(60);
        this.boardGroup.setTranslateY(60);
        this.piecesGroup.setTranslateX(60);
        this.piecesGroup.setTranslateY(60);
        this.piecesGroup.setTranslateZ(10);
        drawBoardTilesAndPieces();
    }

    private void drawBoardTilesAndPieces()
    {
        Function<Color, Color> colorChanger = (color) -> color == Color.NAVAJOWHITE ? Color.SADDLEBROWN : Color.NAVAJOWHITE;
        Color color = Color.SADDLEBROWN;

        for (final TablutBoard.TablutBoardTile tile : this.tablutGame.getTablutBoard().getBoardTiles())
        {
            final int row = tile.getRow();
            final int column = tile.getColumn();
            final TablutBoardTileView tablutBoardTileView = new TablutBoardTileView(column, row, color);
            this.boardGroup.getChildren().add(tablutBoardTileView.getRectangle());
            BOARD_TILES[row - 1][column - 1] = tablutBoardTileView;
            color = colorChanger.apply(color);

            if (tile.getPiece().isEmpty())
                continue;

            TablutPiece piece = tile.getPiece().get();
            TablutPieceView tablutPieceView = new TablutPieceView(row, column, piece);
            this.piecesGroup.getChildren().add(tablutPieceView.getCircle());
            tablutBoardTileView.setTablutPieceView(tablutPieceView);
        }
    }

    private void setupMenu()
    {
        Menu gameMenu = new Menu(Localization.translate("menu.game"));

        MenuItem newGame = new MenuItem(Localization.translate("menu.single-player.new-game"));
        newGame.setOnAction(actionEvent -> this.tablutGame.restart());

        Menu singlePlayerMenu = new Menu(Localization.translate("menu.single-player"));
        singlePlayerMenu.getItems().add(newGame);

        Menu multiplayerMenu = new Menu(Localization.translate("menu.multi-player"));
        MenuItem hostGame = new MenuItem(Localization.translate("menu.multi-player.host-game"));
        hostGame.setOnAction(actionEvent -> hostMultiplayerGame());
        MenuItem joinGame = new MenuItem(Localization.translate("menu.multi-player.join-game"));
        joinGame.setOnAction(actionEvent -> showJoinIpAdressPopup());
        multiplayerMenu.getItems().addAll(hostGame, joinGame);

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem exit = new MenuItem(Localization.translate("menu.exit"));
        exit.setOnAction(actionEvent -> closeGame());
        gameMenu.getItems().addAll(singlePlayerMenu, multiplayerMenu, separatorMenuItem, exit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);
        menuBar.setPrefWidth(WINDOW_WIDTH);
        menuBar.setPrefHeight(MENU_BAR_HEIGHT);

        this.root.getChildren().add(menuBar);
    }

    public List<TablutBoardTileView> getBoardTileViews()
    {
        return Stream.of(BOARD_TILES)
                .flatMap(Arrays::stream)
                .toList();
    }

    public Optional<TablutBoardTileView> getIntersectingTile(int x, int y)
    {
        for (TablutBoardTileView tileView: getBoardTileViews())
        {
            if (tileView.getRectangle().intersects(x, y, TILE_SIZE, TILE_SIZE))
            {
                return Optional.of(tileView);
            }
        }
        return Optional.empty();
    }

    private void closeGame()
    {
        logger.info("Closing game...");
        Platform.exit();
        this.tablutGame.close();
    }

    public void redrawBoard()
    {
        this.boardGroup.getChildren().clear();
        this.mainGroup.getChildren().clear();
        this.piecesGroup.getChildren().clear();
        setupBoard();
    }

    public void displayWinMessageAndLockBoard()
    {
        setLocked(true);
        displayWinMessage();
    }

    private void displayWinMessage()
    {
        Popup popup = new Popup();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(2, 2, 2, 2));
        vBox.setStyle("-fx-border-width: 1px");
        vBox.setStyle("-fx-border-color: black");
        vBox.setBackground(new Background(new BackgroundFill(Color.NAVAJOWHITE, null, null)));
        Label label = new Label(this.getTablutGame().getWinner().orElse(null) + " won the game!");
        vBox.getChildren().add(label);
        popup.getContent().add(vBox);
        popup.setAutoHide(true);
        popup.show(this.primaryStage);
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public Popup getWaitingForPlayerPopup()
    {
        return waitingForPlayerPopup;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.tablutGame = new TablutGame(this);
        this.tablutGame.getTablutBoard().setup();
        setup(primaryStage);
    }

    private void hostMultiplayerGame()
    {
        System.out.println("Hosting a multiplayer game...");
        if (this.tablutGame instanceof TablutGameOnline)
        {
            this.tablutGame.close();
            this.tablutGame = null;
        }

        TablutGameHost tablutGameHost = new TablutGameHost(this);
        tablutGameHost.restart();
        try
        {
            tablutGameHost.hostGame();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.tablutGame = tablutGameHost;

        if (waitingForPlayerPopup == null)
        {
            waitingForPlayerPopup = new Popup();
            VBox vBox = new VBox();
            vBox.setSpacing(2.0);
            vBox.setPadding(new Insets(2, 2, 2, 2));
            vBox.setStyle("-fx-border-width: 1px");
            vBox.setStyle("-fx-border-color: black");
            vBox.setBackground(new Background(new BackgroundFill(Color.NAVAJOWHITE, null, null)));

            Label waitingLabel = new Label("Waiting for palyer to join...");
            vBox.getChildren().add(waitingLabel);

            waitingForPlayerPopup.getContent().add(vBox);
        }
        waitingForPlayerPopup.show(this.primaryStage);
    }

    private void showJoinIpAdressPopup()
    {
        System.out.println("Showing the popup to enter the ip address.");
        Popup popup = new Popup();

        VBox vBox = new VBox();
        vBox.setSpacing(2.0);
        vBox.setPadding(new Insets(2, 2, 2, 2));
        vBox.setStyle("-fx-border-width: 1px");
        vBox.setStyle("-fx-border-color: black");
        vBox.setBackground(new Background(new BackgroundFill(Color.NAVAJOWHITE, null, null)));

        TextField ipAddressTextField = new TextField();
        ipAddressTextField.prefWidth(200);

        Button connectButton = new Button("Connect");

        connectButton.setOnAction(actionEvent ->
        {
            System.out.println("Joining game...");
            joinOnlineGame(ipAddressTextField.getText());
            popup.hide();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(actionEvent ->
        {
            System.out.println("Hiding popup...");
            popup.hide();
        });

        HBox hBox = new HBox();
        Region region = new Region();
        hBox.getChildren().addAll(connectButton, region, cancelButton);
        HBox.setHgrow(region, Priority.ALWAYS);

        vBox.getChildren().addAll(ipAddressTextField, hBox);
        popup.getContent().add(vBox);

        popup.show(this.primaryStage);
    }

    private void joinOnlineGame(String ipAddress)
    {
        TablutGameRemoteClient tablutGameRemoteClient = new TablutGameRemoteClient(this);
        tablutGameRemoteClient.restart();
        try
        {
            tablutGameRemoteClient.connect(ipAddress);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.tablutGame = tablutGameRemoteClient;
    }
}
