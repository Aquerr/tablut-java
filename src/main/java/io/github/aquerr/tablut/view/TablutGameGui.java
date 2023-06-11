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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
        joinGame.setOnAction(actionEvent -> joinMultiplayerGame());
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
        //TODO: Display win message popup.
        setLocked(true);
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public boolean isLocked()
    {
        return locked;
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
        TablutGameHost tablutGameHost = new TablutGameHost(this);
        try
        {
            tablutGameHost.hostGame();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.tablutGame = tablutGameHost;
    }

    private void joinMultiplayerGame()
    {
        System.out.println("Showing the popup to enter the ip address.");
        TablutGameRemoteClient tablutGameRemoteClient = new TablutGameRemoteClient(this);
        this.tablutGame = tablutGameRemoteClient;
    }
}
