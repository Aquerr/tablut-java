package io.github.aquerr.tablut;

import java.util.ArrayList;
import java.util.List;

public class TablutBoardSnapshot
{
    private final List<TablutBoard.TablutBoardTile> tiles;

    public static TablutBoardSnapshot of(TablutBoard tablutBoard)
    {
        List<TablutBoard.TablutBoardTile> tiles = new ArrayList<>();
        for (TablutBoard.TablutBoardTile tile : tablutBoard.getBoardTiles())
        {
            TablutBoard.TablutBoardTile newTile = new TablutBoard.TablutBoardTile(tile.getColumn(), tile.getRow());
            newTile.setPiece(tile.getPiece().orElse(null));
            tiles.add(newTile);
        }

        return new TablutBoardSnapshot(tiles);
    }

    private TablutBoardSnapshot(List<TablutBoard.TablutBoardTile> tiles)
    {
        this.tiles = List.copyOf(tiles);
    }

    public List<TablutBoard.TablutBoardTile> getTiles()
    {
        return tiles;
    }

    @Override
    public String toString()
    {
        return "TablutBoardSnapshot{" +
                "tiles=" + tiles +
                '}';
    }
}
