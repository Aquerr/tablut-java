package io.github.aquerr.tablut.multiplayer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class TablutMultiplayerConnection implements AutoCloseable
{
    private final Socket socket;

    private PrintWriter printWriter;

    public TablutMultiplayerConnection(Socket socket)
    {
        this.socket = socket;
    }

    public InputStreamReader getInputStreamReader() throws IOException
    {
        return new InputStreamReader(socket.getInputStream());
    }

    public PrintWriter getPrintWriter() throws IOException
    {
        if (this.printWriter != null)
        {
            return this.printWriter;
        }
        else
        {
            this.printWriter = new PrintWriter(socket.getOutputStream());
        }
        return this.printWriter;
    }

    @Override
    public void close() throws IOException
    {
        this.printWriter = null;
        this.socket.close();
    }
}
