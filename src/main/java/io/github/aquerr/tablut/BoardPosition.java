package io.github.aquerr.tablut;

import java.util.Objects;

import static io.github.aquerr.tablut.TablutBoard.BOARD_SIZE;

public class BoardPosition
{
    private int column;
    private int row;

    public static BoardPosition of(int column, int row)
    {
        if (row > BOARD_SIZE || row < 0 || column > BOARD_SIZE || column < 0)
        {
            throw new IllegalArgumentException("Out of board bounds");
        }

        return new BoardPosition(column, row);
    }

    private BoardPosition(int column, int row)
    {
        this.column = column;
        this.row = row;
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return column;
    }

    public BoardPosition nextPosition(Direction direction)
    {
        if (direction == Direction.UP)
            return of(this.column, this.row + 1);
        else if (direction == Direction.RIGHT)
            return of(this.column + 1, this.row);
        else if (direction == Direction.DOWN)
            return of(this.column, this.row - 1);
        else // Left
            return of(this.column - 1, this.row);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardPosition that = (BoardPosition) o;
        return column == that.column && row == that.row;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(column, row);
    }

    @Override
    public String toString()
    {
        return "BoardPosition{" +
                "column=" + column +
                ", row=" + row +
                '}';
    }
}
