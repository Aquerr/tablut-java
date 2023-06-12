package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.BoardPosition;

import java.util.Objects;

public class MovePiecePacket implements Packet
{
    private final String type = MovePiecePacket.class.getSimpleName();

    private BoardPosition from;
    private BoardPosition to;

    public MovePiecePacket()
    {

    }

    public MovePiecePacket(BoardPosition from, BoardPosition to)
    {
        this.from = from;
        this.to = to;
    }

    public BoardPosition getFrom()
    {
        return from;
    }

    public void setFrom(BoardPosition from)
    {
        this.from = from;
    }

    public BoardPosition getTo()
    {
        return to;
    }

    public void setTo(BoardPosition to)
    {
        this.to = to;
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
        MovePiecePacket that = (MovePiecePacket) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(from, to);
    }

    @Override
    public String toString()
    {
        return "MovePiecePacket{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
