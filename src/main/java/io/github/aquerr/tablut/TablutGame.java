package io.github.aquerr.tablut;

import io.github.aquerr.tablut.view.TablutBoardGui;
import javafx.application.Application;
import javafx.stage.Stage;

public class TablutGame extends Application
{
    private final TablutBoard tablutBoard;
    private final TablutBoardGui tablutBoardGui;

    private TablutPiece.Side currentMoveSide = TablutPiece.Side.WHITE;
    private TablutPiece.Side winner = null;

    private static TablutGame INSTANCE = null;

    public static TablutGame getGame()
    {
        return INSTANCE;
    }

    public TablutGame()
    {
        if (INSTANCE != null)
            throw new IllegalStateException("The game is already started!");

        INSTANCE = this;
        this.tablutBoard = new TablutBoard();
        this.tablutBoardGui = new TablutBoardGui(this.tablutBoard);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.tablutBoard.setup();
        this.tablutBoardGui.setup(primaryStage);
    }

    public TablutPiece.Side getCurrentMoveSide()
    {
        return currentMoveSide;
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
            this.tablutBoardGui.redrawBoard();

            // TODO: Check winner and block board...
            if (this.tablutBoard.checkBlackWin())
                winner = TablutPiece.Side.BLACK;
            else if (this.tablutBoard.checkWhiteWin())
                winner = TablutPiece.Side.WHITE;

            if (winner != null)
            {
                displayWinMessageAndLockBoard();
            }

            TablutGame.getGame().switchMoveSide();
        }
        catch (Exception exception)
        {
            // Rollback move...
            System.out.println("Undoing last move...");
            restore(tablutBoardSnapshot);
        }
    }

    private void displayWinMessageAndLockBoard()
    {
        System.out.println("The winner is: " + winner.name());
        this.tablutBoardGui.displayWinMessageAndLockBoard();
    }

    private void restore(TablutBoardSnapshot snapshot)
    {
        this.tablutBoard.restore(snapshot);
        this.tablutBoardGui.redrawBoard();
    }

    public TablutBoard getTablutBoard()
    {
        return tablutBoard;
    }

    public TablutBoardGui getTablutBoardGui()
    {
        return tablutBoardGui;
    }
}
