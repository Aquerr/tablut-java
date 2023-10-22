package io.github.aquerr.tablut.multiplayer.packet;

import io.github.aquerr.tablut.BoardPosition;
import org.json.JSONObject;

import java.util.Objects;

import static io.github.aquerr.tablut.multiplayer.packet.PacketAdapter.parseBoardPosition;
import static io.github.aquerr.tablut.multiplayer.packet.PacketAdapter.toJSONObject;

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

    public static class Mapper implements Packet.Mapper<MovePiecePacket>
    {
        @Override
        public MovePiecePacket map(JSONObject jsonObject) throws MappingException
        {
            try
            {
                MovePiecePacket movePiecePacket = new MovePiecePacket();
                BoardPosition from = parseBoardPosition(jsonObject.getJSONObject("from"));
                BoardPosition to = parseBoardPosition(jsonObject.getJSONObject("to"));
                movePiecePacket.setFrom(from);
                movePiecePacket.setTo(to);
                return movePiecePacket;
            }
            catch (Exception exception)
            {
                throw new MappingException("Could not map JSONObject to MovePiecePacket. Reason: " + exception.getMessage());
            }
        }

        @Override
        public JSONObject map(Packet packet) throws MappingException
        {
            try
            {
                MovePiecePacket movePiecePacket = (MovePiecePacket) packet;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", packet.getType());
                jsonObject.put("from", toJSONObject(movePiecePacket.getFrom()));
                jsonObject.put("to", toJSONObject(movePiecePacket.getTo()));
                return jsonObject;
            }
            catch (Exception exception)
            {
                throw new MappingException("Could not map Packet to JSONObject. Reason: " + exception.getMessage());
            }
        }
    }
}
