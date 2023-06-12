package io.github.aquerr.tablut.multiplayer;

import io.github.aquerr.tablut.BoardPosition;
import io.github.aquerr.tablut.TablutBoardSnapshot;
import io.github.aquerr.tablut.TablutGame;
import io.github.aquerr.tablut.TablutPiece;
import io.github.aquerr.tablut.view.TablutGameGui;

import java.io.IOException;

public abstract class TablutGameOnline extends TablutGame
{
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
        }
        catch (Exception exception)
        {
            // Rollback move...
            System.out.println("Undoing last move...");
            restore(tablutBoardSnapshot);
        }
    }

    public void handlePacket(Packet packet)
    {
        if (packet instanceof MovePiecePacket movePiecePacket)
        {
            handleMovePiecePacket(movePiecePacket);
        }
        else if (packet instanceof WinPacket winPacket)
        {
            displayWinMessageAndLockBoard();
        }
        else
        {
            throw new IllegalStateException("Received unknown packet: " + packet);
        }
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
            this.tablutGameGui.redrawBoard();

            // Check winner
            if (this.tablutBoard.checkBlackWin())
                winner = TablutPiece.Side.BLACK;
            else if (this.tablutBoard.checkWhiteWin())
                winner = TablutPiece.Side.WHITE;

            if (winner != null)
            {
                displayWinMessageAndLockBoard();
            }

            // Switch side
            switchMoveSide();
        }
        catch (Exception exception)
        {
            // Rollback move...
            System.out.println("Undoing last move...");
            restore(tablutBoardSnapshot);
        }
    }

    private void handleWinPacket(WinPacket winPacket)
    {
        displayWinMessageAndLockBoard();
    }

    private void sendPacket(Packet packet)
    {
        try
        {
            this.connectedPlayer.getPrintWriter().println(PacketAdapter.toJSONObject(packet).toString());
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
}
