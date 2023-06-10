package io.github.aquerr.tablut.view;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

import java.util.Optional;
import java.util.function.Function;

import static io.github.aquerr.tablut.TablutBoard.TILE_SIZE;

public class TablutBoardTileView
{
    private int column;
    private int row;
    private final Rectangle rectangle;

    private TablutPieceView tablutPieceView;

    public TablutBoardTileView(int column, int row, Color color)
    {
        this.column = column;
        this.row = row;
        this.rectangle = new Rectangle(column * TILE_SIZE - TILE_SIZE, row * TILE_SIZE - TILE_SIZE, TILE_SIZE, TILE_SIZE);
        this.rectangle.setFill(color);
        this.rectangle.setStrokeType(StrokeType.CENTERED);

//        rectangle.addEventHandler(MouseEvent.MOUSE_ENTERED, new TablutBoardTileView.HighlightTileEventHandler(rectangle, true));
//        rectangle.addEventHandler(MouseEvent.MOUSE_EXITED, new TablutBoardTileView.HighlightTileEventHandler(rectangle, false));

        rectangle.setStroke(Color.BLACK);
    }

    public void setTablutPieceView(TablutPieceView tablutPieceView)
    {
        this.tablutPieceView = tablutPieceView;
    }

    public int getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public Rectangle getRectangle()
    {
        return rectangle;
    }

    public Optional<TablutPieceView> getTablutPieceView()
    {
        return Optional.ofNullable(tablutPieceView);
    }

    public static class HighlightTileEventHandler implements EventHandler<MouseEvent>
    {
        public static final Function<Rectangle, Effect> HIGHLIGHT_EFFECT = shape ->  new ColorInput(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight(), Color.LIGHTSTEELBLUE);

        private final Rectangle shape;
        private final boolean enter;

        public HighlightTileEventHandler(final Rectangle shape, final boolean enter)
        {
            this.shape = shape;
            this.enter = enter;
        }

        @Override
        public void handle(MouseEvent event)
        {
            if (enter)
            {
                shape.setCursor(Cursor.HAND);
                shape.setEffect(new Blend(BlendMode.ADD, shape.getEffect(), HIGHLIGHT_EFFECT.apply(shape)));
            }
            else
            {
                shape.setCursor(null);
                shape.setEffect(null);
            }
        }
    }
}
