package ai;

import game.ReversiPlayer;
import game.core.PieceColor;
import game.observer.ReversiObserver;
import gui.ReversiGUI;
import gui.events.ReversiEvent;
import javafx.application.Platform;
import util.MoveException;

import java.awt.*;
import java.util.Set;

/**
 * A player that uses the ai to make moves.
 *
 * @author Brock Dyer.
 */
public class AIPlayer implements ReversiPlayer, ReversiObserver, Runnable {

    /**
     * The ai this player uses to pick its moves.
     */
    private final ReversiAI ai;

    /**
     * The gui to display the game on.
     */
    private final ReversiGUI gui;

    /**
     * The color of the ai piece.
     */
    private final PieceColor MY_COLOR;

    /**
     * The color of the opponent piece.
     */
    private final PieceColor OPPONENT_COLOR;

    /**
     * The color of the current player's piece.
     */
    private PieceColor color;

    /**
     * A flag to determine if it the ai's turn.
     */
    private boolean isMyTurn;

    /**
     * A flag to determine if the ai should continue running.
     */
    private boolean sentinel;

    /**
     * Create the ai player with the specified ai.
     *
     * @param ai the ai this player will use to decide where it moves.
     */
    public AIPlayer(ReversiGUI gui, ReversiAI ai) {
        this.MY_COLOR = PieceColor.WHITE;
        this.OPPONENT_COLOR = PieceColor.BLACK;

        this.gui = gui;
        this.ai = ai;
        this.isMyTurn = false;
        this.sentinel = true;

        this.color = PieceColor.BLACK;
        this.ai.registerPlayerWithBoard(this);
    }

    @Override
    public void run() {

        while (sentinel) {

            synchronized (gui) {

                while (!isMyTurn) {
                    try {
                        gui.wait();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }

                Point move = ai.getMove();

                if (move == null) {
                    pass();
                } else {
                    try {
                        ai.makeMove(move.x, move.y);
                    } catch (MoveException me){
                        System.err.println("AI tried to make an invalid move! This is an error. Terminating ai");
                        sentinel = false;
                    }
                }

                if (ai.isGameOver()) {
                    Platform.runLater(() -> ReversiPlayer.updateIndicatorWithGameResults(ai, gui));
                    return;
                }

                Platform.runLater(() -> gui.updateScore(ai.getBlackScore(), ai.getWhiteScore()));
                Platform.runLater(gui::showAvailableMoves);

                String opponentText = OPPONENT_COLOR.toString();
                Platform.runLater(() -> gui.updateIndicatorLabel(opponentText.substring(0, 1) +
                        opponentText.substring(1).toLowerCase() + "'s Turn"));

                this.color = OPPONENT_COLOR;
                this.isMyTurn = false;

            }
        }

    }

    /**
     * Determine if it is the human player's turn or the ai's turn.
     *
     * @return true if is the human's turn.
     */
    public boolean isHumanTurn(){
        return !isMyTurn;
    }

    @Override
    public void handle(ReversiEvent re) {
        int row = re.getRow();
        int col = re.getCol();
        PieceColor color = re.getColor();

        Platform.runLater(() -> gui.updateBoard(row, col, color));
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {
        ai.makeMove(row, col);

        // Game win checking.
        if (ai.isGameOver()) {
            ReversiPlayer.updateIndicatorWithGameResults(ai, gui);
            sentinel = false;
            return;
        }

        gui.updateIndicatorLabel("The AI is deciding its move.");
        gui.updateScore(ai.getBlackScore(), ai.getWhiteScore());

        synchronized (gui) {
            gui.removeOldMovesFromDisplay();
            gui.notify();
        }

        this.color = MY_COLOR;
        this.isMyTurn = true;

    }

    @Override
    public String getColor() {
        return this.color.toString();
    }

    @Override
    public Set<Point> getMoves() {
        return ai.getPossibleMoves();
    }

    @Override
    public void pass() {
        ai.pass();
    }

    @Override
    public void restart() {
        ai.restart();
    }

    @Override
    public void save(String filename) {

    }

    @Override
    public void load(String filename) {

    }

    @Override
    public void quit() {

    }
}
