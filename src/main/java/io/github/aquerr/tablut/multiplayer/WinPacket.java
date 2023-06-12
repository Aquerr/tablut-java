package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.TablutPiece;

import java.util.Objects;

public class WinPacket implements Packet
{
    private final String type = MovePiecePacket.class.getSimpleName();

    private TablutPiece.Side side;

    public WinPacket()
    {

    }

    public WinPacket(TablutPiece.Side side)
    {
        this.side = side;
    }

    public TablutPiece.Side getSide()
    {
        return side;
    }

    public void setSide(TablutPiece.Side side)
    {
        this.side = side;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WinPacket winPacket = (WinPacket) o;
        return side == winPacket.side;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(side);
    }

    @Override
    public String toString()
    {
        return "WinPacket{" +
                "side=" + side +
                '}';
    }
}
