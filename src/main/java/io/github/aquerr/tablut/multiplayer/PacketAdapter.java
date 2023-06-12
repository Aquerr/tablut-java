package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.BoardPosition;
import io.github.aquerr.tablut.TablutPiece;
import org.json.JSONObject;

public class PacketAdapter
{
    public static Packet parse(JSONObject jsonObject)
    {
        if (jsonObject.getString("type").equals(MovePiecePacket.class.getSimpleName()))
        {
            MovePiecePacket movePiecePacket = new MovePiecePacket();
            BoardPosition from = parseBoardPosition(jsonObject.getJSONObject("from"));
            BoardPosition to = parseBoardPosition(jsonObject.getJSONObject("to"));
            movePiecePacket.setFrom(from);
            movePiecePacket.setTo(to);
            return movePiecePacket;
        }
        else if (jsonObject.getString("type").equals(WinPacket.class.getSimpleName()))
        {
            WinPacket winPacket = new WinPacket();
            winPacket.setSide(TablutPiece.Side.valueOf(jsonObject.getString("side")));
            return winPacket;
        }
        throw new IllegalArgumentException("Unrecognized packet type!");
    }

    private static BoardPosition parseBoardPosition(JSONObject jsonObject)
    {
        return BoardPosition.of(jsonObject.getInt("column"), jsonObject.getInt("row"));
    }

    private static JSONObject toJSONObject(BoardPosition boardPosition)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("column", boardPosition.getColumn());
        jsonObject.put("row", boardPosition.getRow());
        return jsonObject;
    }

    public static JSONObject toJSONObject(Packet packet)
    {
        if (packet instanceof MovePiecePacket movePiecePacket)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", movePiecePacket.getType());
            jsonObject.put("from", toJSONObject(movePiecePacket.getFrom()));
            jsonObject.put("to", toJSONObject(movePiecePacket.getTo()));
            return jsonObject;
        }
        else if (packet instanceof WinPacket winPacket)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", winPacket.getType());
            jsonObject.put("side", winPacket.getSide().name());
            return jsonObject;
        }

        throw new IllegalArgumentException("Unrecognized packet type!");
    }
}
