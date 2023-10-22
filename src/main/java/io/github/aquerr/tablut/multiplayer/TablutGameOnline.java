package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.BoardPosition;
import io.github.aquerr.tablut.TablutBoardSnapshot;
import io.github.aquerr.tablut.TablutGame;
import io.github.aquerr.tablut.TablutPiece;
import io.github.aquerr.tablut.multiplayer.packet.MovePiecePacket;
import io.github.aquerr.tablut.multiplayer.packet.Packet;
import io.github.aquerr.tablut.multiplayer.packet.PacketAdapter;
import io.github.aquerr.tablut.multiplayer.packet.WinPacket;
import io.github.aquerr.tablut.view.TablutGameGui;
import javafx.application.Platform;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public abstract class TablutGameOnline extends TablutGame
{
    private final Map<Class<? extends Packet>, Consumer<Packet>> PACKET_HANDLERS = Map.of(
            MovePiecePacket.class, (packet) -> handleMovePiecePacket((MovePiecePacket) packet),
            WinPacket.class, (packet) -> displayWinMessageAndLockBoard()
    );

    protected TablutMultiplayerConnection connectedPlayer;
    protected Thread connectedPlayerInputStreamThread;

    protected TablutGameOnline(TablutGameGui tablutGameGui)
    {
        super(tablutGameGui);
    }

    @Override
    public void movePiece(BoardPosition from, BoardPosition to)
    {
        if (winner != null)
            return;

        TablutBoardSnapshot tablutBoardSnapshot = this.tablutBoard.createSnapshot();

        try
        {
            // Update game logic
            this.tablutBoard.movePiece(from, to);

            // Update gui
            this.tablutGameGui.redrawBoard();

            // Check winner
            if (this.tablutBoard.checkBlackWin())
                winner = TablutPiece.Side.BLACK;
            else if (this.tablutBoard.checkWhiteWin())
                winner = TablutPiece.Side.WHITE;

            sendPacket(new MovePiecePacket(from, to));

            if (winner != null)
            {
                displayWinMessageAndLockBoard();
                sendPacket(new WinPacket(currentMoveSide));
            }

            // Switch side
            switchMoveSide();
            lockBoard();
        }
        catch (Exception exception)
        {
            // Rollback move...
            System.out.println(exception.getMessage());
            System.out.println("Undoing last move...");
            restore(tablutBoardSnapshot);
        }
    }

    protected void lockBoard()
    {
        this.tablutGameGui.setLocked(true);
    }

    protected void unlockBoard()
    {
        this.tablutGameGui.setLocked(false);
    }

    public void handlePacket(Packet packet)
    {
        ofNullable(PACKET_HANDLERS.get(packet.getClass()))
                .orElseThrow(() -> new IllegalStateException("Received unknown packet: " + packet))
                .accept(packet);
    }

    private void handleMovePiecePacket(MovePiecePacket movePiecePacket)
    {
        if (winner != null)
            return;

        TablutBoardSnapshot tablutBoardSnapshot = this.tablutBoard.createSnapshot();

        try
        {
            // Update game logic
            this.tablutBoard.movePiece(movePiecePacket.getFrom(), movePiecePacket.getTo());

            // Update gui
            Platform.runLater(this.tablutGameGui::redrawBoard);

            // Check winner
            if (this.tablutBoard.checkBlackWin())
                winner = TablutPiece.Side.BLACK;
            else if (this.tablutBoard.checkWhiteWin())
                winner = TablutPiece.Side.WHITE;

            if (winner != null)
            {
                Platform.runLater(this::displayWinMessageAndLockBoard);
            }

            // Switch side
            switchMoveSide();
            unlockBoard();
        }
        catch (Exception exception)
        {
            // Rollback move...
            System.out.println("Undoing last move...");
            restore(tablutBoardSnapshot);
        }
    }

    private void sendPacket(Packet packet)
    {
        try
        {
            this.connectedPlayer.getPrintWriter().println(PacketAdapter.toJSONObject(packet));
            this.connectedPlayer.getPrintWriter().flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()
    {
        if (this.connectedPlayer != null)
        {
            try
            {
                this.connectedPlayer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            this.connectedPlayer = null;
        }

        if (this.connectedPlayerInputStreamThread != null)
        {
            this.connectedPlayerInputStreamThread.interrupt();
            this.connectedPlayerInputStreamThread = null;
        }

        super.close();
    }

    protected void handleMessages()
    {
        this.connectedPlayerInputStreamThread = new Thread(() -> {
            try
            {
                BufferedReader bufferedReader = this.connectedPlayer.getBufferedReader();
                String stringJson = "";
                while ((stringJson = bufferedReader.readLine()) != null)
                {
                    System.out.println("Received Message: " + stringJson);
                    handlePacket(PacketAdapter.parse(new JSONObject(stringJson)));
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        this.connectedPlayerInputStreamThread.start();
    }

    public TablutMultiplayerConnection getConnectedPlayer()
    {
        return connectedPlayer;
    }
}
