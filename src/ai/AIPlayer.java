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
public class AIPlayer implements ReversiPlayer, ReversiObserver {

    private final int MOVE_DELAY = 1000;

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
    private PieceColor currentColor;

    /**
     * A flag to determine if it the ai's turn.
     */
    private boolean isMyTurn;

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

        this.currentColor = PieceColor.BLACK;
        this.ai.registerPlayerWithBoard(this);
    }

    /**
     * Determine if it is the human player's turn or the ai's turn.
     *
     * @return true if is the human's turn.
     */
    public boolean isHumanTurn() {
        return !isMyTurn;
    }

    @Override
    public void handle(ReversiEvent re) {
        int row = re.getRow();
        int col = re.getCol();
        PieceColor eventColor = re.getColor();

        Platform.runLater(() -> gui.updateBoard(row, col, eventColor));
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {

        //-------------- Handle the other player's move. ----------------------\\

        ai.makeMove(row, col);

        this.currentColor = MY_COLOR;
        this.isMyTurn = true;

        gui.updateScore(ai.getBlackScore(), ai.getWhiteScore());

        // Game win checking.
        if (ai.isGameOver()) {
            System.out.println("Game over. Person Move");
            ReversiPlayer.updateIndicatorWithGameResults(ai, gui);
            return;
        }

        gui.updateIndicatorLabel("The AI is deciding its move.");

        gui.removeOldMovesFromDisplay();

        //------------------ The ai move -------------------------------------\\

//        Point move = ai.getMove();
//
//        if (move == null) {
//            System.out.println("AI passed.");
//            pass();
//        } else {
//            try {
//                ai.makeMove(move.x, move.y);
//            } catch (MoveException me) {
//                System.err.println("AI tried to make an invalid move!");
//            }
//        }
//
//        if (ai.isGameOver()) {
//            System.out.println("Game Over, ai move");
//            ReversiPlayer.updateIndicatorWithGameResults(ai, gui);
//            return;
//        }
//
//        this.currentColor = OPPONENT_COLOR;
//        this.isMyTurn = false;
//
//        gui.updateScore(ai.getBlackScore(), ai.getWhiteScore());
//        gui.showAvailableMoves();
//
//        String opponentText = OPPONENT_COLOR.toString();
//        gui.updateIndicatorLabel(opponentText.substring(0, 1) +
//                opponentText.substring(1).toLowerCase() + "'s Turn");

    }

    @Override
    public String getColor() {
        return this.currentColor.toString();
    }

    @Override
    public Set<Point> getMoves() {
        return ai.getPossibleMoves();
    }

    @Override
    public void pass() {
        this.isMyTurn = false;
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
