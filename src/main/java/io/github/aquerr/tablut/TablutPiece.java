package io.github.aquerr.tablut;

public class TablutPiece
{
    private final Side side;

    public TablutPiece(Side side)
    {
        this.side = side;
    }

    public Side getSide()
    {
        return side;
    }

    public enum Side
    {
        WHITE,
        BLACK;
    }
}
