package io.github.aquerr.tablut.multiplayer.packet;

import io.github.aquerr.tablut.TablutPiece;
import org.json.JSONObject;

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

    public static class Mapper implements Packet.Mapper<WinPacket>
    {
        @Override
        public WinPacket map(JSONObject jsonObject) throws MappingException
        {
            try
            {
                WinPacket winPacket = new WinPacket();
                winPacket.setSide(TablutPiece.Side.valueOf(jsonObject.getString("side")));
                return winPacket;
            }
            catch (Exception exception)
            {
                throw new MappingException("Could not map JSONObject to WinPacket. Reason: " + exception.getMessage());

            }
        }

        @Override
        public JSONObject map(Packet packet) throws MappingException
        {
            try
            {
                WinPacket winPacket = (WinPacket) packet;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", packet.getType());
                jsonObject.put("side", winPacket.getSide().name());
                return jsonObject;
            }
            catch (Exception exception)
            {
                throw new MappingException("Could not map Packet to JSONObject. Reason: " + exception.getMessage());
            }
        }
    }
}
