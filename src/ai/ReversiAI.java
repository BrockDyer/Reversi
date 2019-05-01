package ai;

import game.core.ReversiBoard;
import game.core.ReversiGame;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Define the common functionality of a reversi ai.
 *
 * @author Brock Dyer.
 */
public abstract class ReversiAI extends ReversiGame {

    protected final ReversiBoard board;

    public ReversiAI(){
        super();
        this.board = super.getBoard();
    }

    /**
     * Get the move, as a point, that the ai will make.
     *
     * @return the point containing the row and column of the move.
     */
    public abstract Point getMove();

    /**
     * Get the move that flips the most number of opponent pieces.
     *
     * @return the point containing the row and column of the move that flips the most pieces.
     */
    protected Point getMyMoveWithMostFlips(){
        Point mostFlipsMove = null;

        int maxFlips = -1;

        for(Point p : super.getPossibleMoves()){
            int numFlips = board.findOpponentsToFlip(p.x, p.y).size();

            if(numFlips > maxFlips){
                mostFlipsMove = p;
                maxFlips = numFlips;
            }
        }

        return mostFlipsMove;
    }

}
