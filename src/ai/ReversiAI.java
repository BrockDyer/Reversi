package ai;

import game.core.ReversiBoard;

import java.awt.*;
import java.util.Set;

/**
 * A parent class for all reversi ai
 *
 * @author Brock Dyer.
 */
public abstract class ReversiAI {

    /**
     * A copy of the game board so that the ai can test moves.
     */
    private ReversiBoard board;

    /**
     * Initialize the ai.
     *
     * @param board a copy of the game board.
     */
    protected ReversiAI(ReversiBoard board){
        this.board = board;
    }

    /**
     * Determine the move the ai will make.
     *
     * @param moveSet the set of possible moves. This decreases the demand placed on the ai algorithm.
     * @return a point containing the row and column the ai is making its move in.
     */
    public abstract Point determineMove(Set<Point> moveSet);

    /**
     * Allows children to get an instance of the game board for move determination.
     *
     * @return the ai copy of the board.
     */
    protected ReversiBoard getBoard(){
        return this.board;
    }
}
