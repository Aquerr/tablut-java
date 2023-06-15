package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.view.TablutGameGui;

import java.io.IOException;
import java.net.Socket;

public class TablutGameRemoteClient extends TablutGameOnline
{
    public TablutGameRemoteClient(TablutGameGui tablutGameGui)
    {
        super(tablutGameGui);
    }

    @Override
    public void close()
    {
        super.close();
    }

    public void connect(String ipAddress) throws IOException
    {
        lockBoard();
        this.connectedPlayer = new TablutMultiplayerConnection(new Socket(ipAddress, 28415));
        handleMessages();
    }
}
