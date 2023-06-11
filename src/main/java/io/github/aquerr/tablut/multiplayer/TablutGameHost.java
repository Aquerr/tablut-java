package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.view.TablutGameGui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class TablutGameHost extends TablutGameOnline
{
    private static final int ONLINE_GAME_PORT = 28415;

    private ServerSocket serverSocket;

    private Thread serverSocketThread;

    public TablutGameHost(TablutGameGui tablutGameGui)
    {
        super(tablutGameGui);
    }

    public void hostGame() throws IOException
    {
        releaseConnections();
        startServerSocket();
        serverSocketThread = new Thread(() -> {
            try
            {
                waitForConnection();
                handleMessages();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        serverSocketThread.start();
    }

    private void startServerSocket() throws IOException
    {
        this.serverSocket = new ServerSocket(ONLINE_GAME_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                releaseConnections();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }));
    }

    private void waitForConnection() throws IOException
    {
        this.connectedPlayer = new TablutMultiplayerConnection(this.serverSocket.accept());
    }

    private void handleMessages()
    {
        this.connectedPlayerInputStreamThread = new Thread(() -> {
            try
            {
                InputStreamReader inputStreamReader = this.connectedPlayer.getInputStreamReader();

            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        this.connectedPlayerInputStreamThread.start();
    }

    private void releaseConnections() throws IOException
    {
        if (this.connectedPlayer != null)
        {
            this.connectedPlayer.close();
            this.connectedPlayer = null;
        }

        if (this.serverSocket != null)
        {
            this.serverSocket.close();
            this.serverSocket = null;
        }

        if (this.connectedPlayerInputStreamThread != null)
        {
            this.connectedPlayerInputStreamThread.interrupt();
        }
    }
}
