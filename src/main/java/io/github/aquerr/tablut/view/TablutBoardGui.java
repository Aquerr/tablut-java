package io.github.aquerr.tablut.view;

import io.github.aquerr.tablut.BoardPosition;
import io.github.aquerr.tablut.TablutBoard;
import io.github.aquerr.tablut.TablutPiece;
import io.github.aquerr.tablut.localization.Localization;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static io.github.aquerr.tablut.TablutBoard.BOARD_SIZE;
import static io.github.aquerr.tablut.TablutBoard.TILE_SIZE;

public class TablutBoardGui
{
    private static final TablutBoardTileView[][] BOARD_TILES = new TablutBoardTileView[BOARD_SIZE][BOARD_SIZE];

    private static final Logger logger = Logger.getLogger(TablutBoardGui.class.getName());
    private static final int WINDOW_HEIGHT = 680;
    private static final int WINDOW_WIDTH = 660;
    private static final int MENU_BAR_HEIGHT = 20;

    private final TablutBoard tablutBoard;
    private Stage primaryStage;
    private VBox root;
    private Group mainGroup;
    private Scene mainScene;
    private Group boardGroup;
    private Group piecesGroup;

    private boolean locked = false;

    public TablutBoardGui(TablutBoard tablutBoard)
    {
        this.tablutBoard = tablutBoard;
    }

    public void setup(Stage primaryStage)
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

        for (final TablutBoard.TablutBoardTile tile : this.tablutBoard.getBoardTiles())
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

        MenuItem exit = new MenuItem(Localization.translate("menu.exit"));
        exit.setOnAction(actionEvent -> closeGame());
        gameMenu.getItems().add(exit);

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
        System.exit(0);
    }

    private TablutBoardTileView getTileAt(BoardPosition position)
    {
        if (position.getRow() > BOARD_SIZE
                || position.getRow() < 0
                || position.getColumn() > BOARD_SIZE
                || position.getColumn() < 0)
        {
            throw new IllegalArgumentException("Out of board bounds");
        }
        return BOARD_TILES[position.getRow() - 1][position.getColumn() - 1];
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
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public boolean isLocked()
    {
        return locked;
    }
}
