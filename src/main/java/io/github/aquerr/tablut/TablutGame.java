package io.github.aquerr.tablut;

import io.github.aquerr.tablut.multiplayer.TablutGameOnline;
import io.github.aquerr.tablut.view.TablutGameGui;

import java.util.Optional;

public class TablutGame
{
    protected final TablutBoard tablutBoard;
    protected final TablutGameGui tablutGameGui;

    protected TablutPiece.Side currentMoveSide = TablutPiece.Side.WHITE;
    protected TablutPiece.Side winner = null;

    public TablutGame(TablutGameGui tablutGameGui)
    {
        this.tablutBoard = new TablutBoard(this);
        this.tablutGameGui = tablutGameGui;
    }

    public TablutPiece.Side getCurrentMoveSide()
    {
        return currentMoveSide;
    }

    public Optional<TablutPiece.Side> getWinner()
    {
        return Optional.ofNullable(this.winner);
    }

    public void switchMoveSide()
    {
        this.currentMoveSide = this.currentMoveSide == TablutPiece.Side.WHITE ? TablutPiece.Side.BLACK : TablutPiece.Side.WHITE;
    }

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

    protected void displayWinMessageAndLockBoard()
    {
        System.out.println("The winner is: " + winner.name());
        this.tablutGameGui.displayWinMessageAndLockBoard();
    }

    protected void restore(TablutBoardSnapshot snapshot)
    {
        this.tablutBoard.restore(snapshot);
        this.tablutGameGui.redrawBoard();
    }

    public TablutBoard getTablutBoard()
    {
        return tablutBoard;
    }

    public TablutGameGui getTablutBoardGui()
    {
        return tablutGameGui;
    }

    public void restart()
    {
        this.winner = null;
        this.currentMoveSide = TablutPiece.Side.WHITE;
        this.tablutBoard.setup();
        this.tablutGameGui.redrawBoard();
    }

    public void close()
    {
        System.exit(0);
    }

    public boolean isOnline()
    {
        return this instanceof TablutGameOnline;
    }
}
