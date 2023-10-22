package io.github.aquerr.tablut.multiplayer.packet;

import io.github.aquerr.tablut.BoardPosition;
import org.json.JSONObject;

import java.util.Map;

import static java.util.Optional.ofNullable;

public class PacketAdapter
{
    private static final Map<String, Packet.Mapper<? extends Packet>> PACKET_MAPPER = Map.of(
            MovePiecePacket.class.getSimpleName(), new MovePiecePacket.Mapper(),
            WinPacket.class.getSimpleName(), new WinPacket.Mapper()
    );

    public static Packet parse(JSONObject jsonObject)
    {
        try
        {
            return ofNullable(PACKET_MAPPER.get(jsonObject.getString("type")))
                    .orElseThrow(() -> new IllegalStateException("Unrecognized packet type!"))
                    .map(jsonObject);
        }
        catch (MappingException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public static BoardPosition parseBoardPosition(JSONObject jsonObject)
    {
        return BoardPosition.of(jsonObject.getInt("column"), jsonObject.getInt("row"));
    }

    public static JSONObject toJSONObject(BoardPosition boardPosition)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("column", boardPosition.getColumn());
        jsonObject.put("row", boardPosition.getRow());
        return jsonObject;
    }

    public static JSONObject toJSONObject(Packet packet)
    {
        try
        {
            return ofNullable(PACKET_MAPPER.get(packet.getType()))
                    .orElseThrow(() -> new IllegalStateException("Unrecognized packet type!"))
                    .map(packet);
        }
        catch (MappingException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
