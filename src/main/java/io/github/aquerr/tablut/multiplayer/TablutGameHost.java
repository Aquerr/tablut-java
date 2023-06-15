package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.view.TablutGameGui;
import javafx.application.Platform;

import java.io.IOException;
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
        closeCleanup();
        startServerSocket();
        serverSocketThread = new Thread(() -> {
            try
            {
                waitForConnection();
                handleMessages();
                Platform.runLater(() -> TablutGameGui.getGameGui().getWaitingForPlayerPopup().hide());
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
                closeCleanup();
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

    private void closeCleanup() throws IOException
    {
        if (this.serverSocket != null)
        {
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }

    @Override
    public void close()
    {
        try
        {
            closeCleanup();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        super.close();
    }
}
