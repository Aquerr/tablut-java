package io.github.aquerr.tablut;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class TablutBoard
{
    public static final int BOARD_SIZE = 9;
    public static final int TILE_SIZE = 60;

    private final TablutBoardTile[][] board = new TablutBoardTile[BOARD_SIZE][BOARD_SIZE];

    public void setup()
    {
        for (int row = 1; row <= BOARD_SIZE; row++)
        {
            for (int column = 1; column <= BOARD_SIZE; column++)
            {
                TablutBoardTile tablutBoardTile = new TablutBoardTile(column, row);
                board[row - 1][column - 1] = tablutBoardTile;
            }
        }

        putPieceAtTile(1, 4, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(1, 5, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(1, 6, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(2, 5, new TablutPiece(TablutPiece.Side.BLACK));

        putPieceAtTile(4, 1, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(5, 1, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(6, 1, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(5, 2, new TablutPiece(TablutPiece.Side.BLACK));

        putPieceAtTile(4, 9, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(5, 9, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(6, 9, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(5, 8, new TablutPiece(TablutPiece.Side.BLACK));

        putPieceAtTile(9, 4, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(9, 5, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(9, 6, new TablutPiece(TablutPiece.Side.BLACK));
        putPieceAtTile(8, 5, new TablutPiece(TablutPiece.Side.BLACK));

        putPieceAtTile(3, 5, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(4, 5, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(5, 5, new TablutKingPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(6, 5, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(7, 5, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(5, 3, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(5, 4, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(5, 6, new TablutPiece(TablutPiece.Side.WHITE));
        putPieceAtTile(5, 7, new TablutPiece(TablutPiece.Side.WHITE));
    }

    public List<TablutBoardTile> getBoardTiles()
    {
        return Stream.of(this.board)
                .flatMap(Arrays::stream)
                .toList();
    }

    private void putPieceAtTile(int row, int column, TablutPiece tablutPiece)
    {
        if (row > BOARD_SIZE || row < 0 || column > BOARD_SIZE || column < 0)
        {
            throw new IllegalArgumentException("Out of board bounds");
        }
        TablutBoardTile tile = board[row - 1][column - 1];
        if (tile.getPiece().isPresent())
        {
            throw new IllegalStateException("Piece is already at tile.");
        }
        tile.setPiece(tablutPiece);
    }

    public TablutBoardTile getTileAt(int row, int column)
    {
        if (row > BOARD_SIZE || row < 0 || column > BOARD_SIZE || column < 0)
        {
            throw new IllegalArgumentException("Out of board bounds");
        }
        return board[row - 1][column - 1];
    }

    public boolean canMoveTo(BoardPosition from, BoardPosition to)
    {
        TablutBoardTile fromTile = getTileAt(from.getRow(), from.getColumn());
        TablutBoardTile toTile = getTileAt(to.getRow(), to.getColumn());

        if (toTile.getPiece().isPresent())
            return false;
        else if (fromTile.getPiece().isEmpty())
            return false;
        else if (to.getRow() != from.getRow() && to.getColumn() != from.getColumn())
            return false;
        else if (fromTile.getPiece().get().getSide() != TablutGame.getGame().getCurrentMoveSide())
            return false;
        else if (isPieceInWay(from, to))
            return false;

        return true;
    }

    private boolean isPieceInWay(BoardPosition from, BoardPosition to)
    {
        int fromColumn = Math.min(from.getColumn(), to.getColumn());
        int fromRow = Math.min(from.getRow(), to.getRow());
        int toColumn = Math.max(from.getColumn(), to.getColumn());
        int toRow = Math.max(from.getRow(), to.getRow());

        if (from.getColumn() == to.getColumn())
        {
            // Check Y axis
            for (int row = fromRow; row < toRow; row++)
            {
                if (from.getRow() == row)
                    continue;
                if (getTileAt(row, from.getColumn()).getPiece().isPresent())
                    return true;
            }
        }
        else if (from.getRow() == to.getRow())
        {
            // Check X axis
            for (int column = fromColumn; column < toColumn; column++)
            {
                if (from.getColumn() == column)
                    continue;
                if (getTileAt(from.getRow(), column).getPiece().isPresent())
                    return true;
            }
        }

        return false;
    }

    public void movePiece(BoardPosition from, BoardPosition to)
    {
        TablutBoardTile fromTile = getTileAt(from.getRow(), from.getColumn());
        TablutBoardTile toTile = getTileAt(to.getRow(), to.getColumn());

        doMove(fromTile, toTile);
        tryKillEnemyPieceAdjacentToTile(toTile);
    }

    public boolean checkWhiteWin()
    {
        TablutBoard.TablutBoardTile tile = getWhiteKingTile();
        BoardPosition kingPosition = BoardPosition.of(tile.getColumn(), tile.getRow());
        return isOnBoardEdge(kingPosition);
    }

    private boolean isOnBoardEdge(BoardPosition kingPosition)
    {
        return kingPosition.getColumn() == 1
                || kingPosition.getColumn() == BOARD_SIZE
                || kingPosition.getRow() == 1
                || kingPosition.getRow() == BOARD_SIZE;
    }

    public boolean checkBlackWin()
    {
        TablutBoard.TablutBoardTile tile = getWhiteKingTile();
        BoardPosition kingPosition = BoardPosition.of(tile.getColumn(), tile.getRow());

        boolean isBlackWin = true;

        // Check 4 sides of this tile.
        Direction[] directions = Direction.values();

        for (Direction direction : directions)
        {
            try
            {
                BoardPosition nextPosition = kingPosition.nextPosition(direction);
                TablutBoard.TablutBoardTile nextTile = getTileAt(nextPosition);
                if (nextTile.getPiece().isEmpty() || nextTile.getPiece().get().getSide() == TablutPiece.Side.WHITE)
                {
                    isBlackWin = false;
                    break;
                }
            }
            catch (Exception exception)
            {
                // Nothing... as the position is out of board.
            }
        }

        return isBlackWin;
    }

    private void tryKillEnemyPieceAdjacentToTile(TablutBoardTile tile)
    {
        // Check 4 sides of this tile.
        Direction[] directions = Direction.values();
        TablutPiece.Side currentSide = TablutGame.getGame().getCurrentMoveSide();
        BoardPosition currentTilePosition = BoardPosition.of(tile.getColumn(), tile.getRow());

        TablutBoardTile tileWithPieceToKill = null;

        for (Direction direction : directions)
        {
            try
            {
                BoardPosition nextPosition = currentTilePosition.nextPosition(direction);
                TablutBoardTile nextTile = getTileAt(nextPosition);
                if (nextTile.getPiece().isPresent() && !(nextTile.getPiece().get() instanceof TablutKingPiece) && nextTile.getPiece().get().getSide() != currentSide)
                {
                    TablutBoardTile anotherTile = getTileAt(nextPosition.nextPosition(direction));
                    if (anotherTile.getPiece().isPresent() && anotherTile.getPiece().get().getSide() == currentSide)
                    {
                        tileWithPieceToKill = nextTile;
                    }
                }
            }
            catch (Exception exception)
            {
                // Nothing... as the position is out of board.
            }
        }

        if (tileWithPieceToKill != null)
        {
            tileWithPieceToKill.setPiece(null);
        }
    }

    private TablutBoardTile getTileAt(BoardPosition boardPosition)
    {
        return getTileAt(boardPosition.getRow(), boardPosition.getColumn());
    }

    private void doMove(TablutBoardTile fromTile, TablutBoardTile toTile)
    {
        if (toTile.getPiece().isPresent())
        {
            throw new IllegalArgumentException(format("There is a piece at %s!", toTile));
        }
        else if (fromTile.getPiece().isEmpty())
        {
            throw new IllegalArgumentException("There is no piece at given position!");
        }
        else if (toTile.getRow() != fromTile.getRow() && toTile.getColumn() != fromTile.getColumn())
        {
            throw new IllegalArgumentException("Can't move to given position!");
        }
        else if (fromTile.getPiece().get().getSide() != TablutGame.getGame().getCurrentMoveSide())
        {
            throw new IllegalArgumentException("Wrong player!");
        }
        else if (isPieceInWay(BoardPosition.of(fromTile.getColumn(), fromTile.getRow()), BoardPosition.of(toTile.getColumn(), toTile.getRow())))
        {
            throw new IllegalArgumentException("Can't move to given position! Piece is in way.");
        }

        toTile.setPiece(fromTile.getPiece().get());
        fromTile.setPiece(null);
    }

    public TablutBoardSnapshot createSnapshot()
    {
        return TablutBoardSnapshot.of(this);
    }

    public void restore(TablutBoardSnapshot snapshot)
    {
        for (final TablutBoardTile tile : snapshot.getTiles())
        {
            int column = tile.getColumn();
            int row = tile.getRow();
            TablutBoardTile tablutBoardTile = new TablutBoardTile(column, row);
            tablutBoardTile.setPiece(tile.getPiece().orElse(null));
            board[row - 1][column - 1] = tablutBoardTile;
        }
    }

    public TablutBoardTile getWhiteKingTile()
    {
        return getBoardTiles().stream().filter(x -> x.getPiece().isPresent() && x.getPiece().get() instanceof TablutKingPiece).findFirst().get();
    }

    public static class TablutBoardTile
    {
        private final int column;
        private final int row;
        private TablutPiece tablutPiece;

        public TablutBoardTile(int column, int row)
        {
            this.column = column;
            this.row = row;
        }

        public Optional<TablutPiece> getPiece()
        {
            return Optional.ofNullable(tablutPiece);
        }

        public int getColumn()
        {
            return column;
        }

        public int getRow()
        {
            return row;
        }

        public void setPiece(TablutPiece tablutPiece)
        {
            this.tablutPiece = tablutPiece;
        }

        @Override
        public String toString()
        {
            return "TablutBoardTile{" +
                    "column=" + column +
                    ", row=" + row +
                    ", tablutPiece=" + tablutPiece +
                    '}';
        }
    }

}
