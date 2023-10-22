package io.github.aquerr.tablut.multiplayer.packet;

import org.json.JSONObject;

public interface Packet
{
    String getType();

    interface Mapper<T extends Packet>
    {
        T map(JSONObject jsonObject) throws MappingException;

        JSONObject map(Packet packet) throws MappingException;
    }
}
