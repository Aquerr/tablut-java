package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.view.TablutGameGui;

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
}
