package io.github.aquerr.tablut.view;

import io.github.aquerr.tablut.BoardPosition;
import io.github.aquerr.tablut.TablutGame;
import io.github.aquerr.tablut.TablutKingPiece;
import io.github.aquerr.tablut.TablutPiece;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Optional;

import static io.github.aquerr.tablut.TablutBoard.TILE_SIZE;
import static io.github.aquerr.tablut.view.TablutBoardTileView.HighlightTileEventHandler.HIGHLIGHT_EFFECT;

public class TablutPieceView
{
    private final Circle circle;

    // For dragging
    private double lastX;
    private double lastY;

    private static final int BASE_TRANSLATE_Z = 10;
    public TablutPieceView(int row, int column, TablutPiece piece)
    {
        Color color = Color.GHOSTWHITE;
        if (piece.getSide() == TablutPiece.Side.BLACK)
        {
            color = Color.BLACK;
        }
        else if (piece instanceof TablutKingPiece)
        {
            color = Color.GRAY;
        }

        this.circle = new Circle((column - 1) * TILE_SIZE + (double) TILE_SIZE / 2,(row - 1) * TILE_SIZE + (double) TILE_SIZE / 2, (double) TILE_SIZE / 3);
        this.circle.setFill(color);
        this.circle.setTranslateZ(BASE_TRANSLATE_Z);

        this.lastY = this.circle.getCenterY();
        this.lastX = this.circle.getCenterX();

        setupDragEvents(this);
    }

    public Circle getCircle()
    {
        return circle;
    }

    public void setLastX(double lastX)
    {
        this.lastX = lastX;
    }

    public void setLastY(double lastY)
    {
        this.lastY = lastY;
    }

    private void setupDragEvents(TablutPieceView tablutPieceView)
    {
        circle.setOnMousePressed(mouseClickEvent -> {

            if (TablutGameGui.getGameGui().isLocked())
                return;

            // Highlight possible movements
            highlightPossibleMovements();
            System.out.println("Mouse Pressed at Tile X: " + this.circle.getCenterX() + " Y: " + this.circle.getCenterY() + " Mouse Pos: X: " + (mouseClickEvent.getX()) + " | Y: " + (mouseClickEvent.getY()));
        });

        circle.setOnMouseDragged(mouseEvent ->
        {
            if (TablutGameGui.getGameGui().isLocked())
                return;

            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            circle.setCenterX(mouseX);
            circle.setCenterY(mouseY);
            this.circle.setTranslateZ(20);
        });

        circle.setOnMouseReleased(mouseDragEvent ->
        {
            if (TablutGameGui.getGameGui().isLocked())
                return;

            this.circle.setTranslateZ(BASE_TRANSLATE_Z);

            unHighlightPossibleMovements();

            final int rectangleX = (int)circle.getCenterX();
            final int rectangleY = (int)circle.getCenterY();

            // Get tile the mouse is above
            final Optional<TablutBoardTileView> optionalTile = TablutGameGui.getGameGui().getIntersectingTile(rectangleX, rectangleY);
            if (optionalTile.isEmpty())
            {
                // Bring figure back to initial position
                restoreLastPosition();
                return;
            }

            System.out.println("Mouse Released at Tile: row=" + optionalTile.get().getRow() + " column=" + optionalTile.get().getColumn() + " Mouse Pos: X: " + (mouseDragEvent.getX()) + " | Y: " + (mouseDragEvent.getY()));

            final TablutBoardTileView newTile = optionalTile.get();
            final TablutBoardTileView lastTile = TablutGameGui.getGameGui().getIntersectingTile((int)lastX, (int)lastY).get();

            final BoardPosition fromPosition = BoardPosition.of(lastTile.getColumn(), lastTile.getRow());
            final BoardPosition toPosition = BoardPosition.of(newTile.getColumn(), newTile.getRow());

            // Check if chess figure can move to the tile the mouse is above.
            if (!TablutGameGui.getGameGui().getTablutGame().getTablutBoard().canMoveTo(fromPosition, toPosition))
            {
                // Bring figure back to initial position
                restoreLastPosition();
                return;
            }

            if (lastTile.getColumn() == newTile.getColumn() && lastTile.getRow() == newTile.getRow())
            {
                // Bring figure back to initial position
                restoreLastPosition();
                return;
            }

            TablutGameGui.getGameGui().getTablutGame().movePiece(fromPosition, toPosition);
        });

        tablutPieceView.getCircle().addEventHandler(MouseEvent.MOUSE_ENTERED, new TablutPieceView.HighlightEventHandler(circle, true));
        tablutPieceView.getCircle().addEventHandler(MouseEvent.MOUSE_EXITED, new TablutPieceView.HighlightEventHandler(circle, false));
    }

    private void highlightPossibleMovements()
    {
        System.out.println("Highlighting possible movements...");
        TablutGame game = TablutGameGui.getGameGui().getTablutGame();
        final TablutBoardTileView currentTile = game.getTablutBoardGui().getIntersectingTile((int)lastX, (int)lastY).get();
        final BoardPosition currentPosition = BoardPosition.of(currentTile.getColumn(), currentTile.getRow());
        for (final TablutBoardTileView tile : game.getTablutBoardGui().getBoardTileViews())
        {
            if (game.getTablutBoard().canMoveTo(currentPosition, BoardPosition.of(tile.getColumn(), tile.getRow())))
            {
                tile.getRectangle().setEffect(new Blend(BlendMode.EXCLUSION, tile.getRectangle().getEffect(), HIGHLIGHT_EFFECT.apply(tile.getRectangle())));
            }
        }
    }

    private void unHighlightPossibleMovements()
    {
        System.out.println("Hiding possible movements...");
        TablutGame game = TablutGameGui.getGameGui().getTablutGame();
        final TablutBoardTileView currentTile = game.getTablutBoardGui().getIntersectingTile((int)lastX, (int)lastY).get();
        final BoardPosition currentPosition = BoardPosition.of(currentTile.getColumn(), currentTile.getRow());
        for (final TablutBoardTileView tile : game.getTablutBoardGui().getBoardTileViews())
        {
            if (game.getTablutBoard().canMoveTo(currentPosition, BoardPosition.of(tile.getColumn(), tile.getRow())))
            {
                tile.getRectangle().setEffect(null);
            }
        }
    }

    private void restoreLastPosition()
    {
        circle.setCenterX(lastX);
        circle.setCenterY(lastY);
    }

    public static class HighlightEventHandler implements EventHandler<MouseEvent>
    {
        private final Circle circle;
        private final boolean enter;

        public HighlightEventHandler(final Circle circle, final boolean enter)
        {
            this.circle = circle;
            this.enter = enter;
        }

        @Override
        public void handle(MouseEvent event)
        {
            if (enter)
            {
                circle.setCursor(Cursor.HAND);
            }
            else
            {
                circle.setCursor(null);
            }
        }
    }
}
