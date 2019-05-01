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

    /**
     * Get the move, as a point, that the ai will make.
     *
     * @return the point containing the row and column of the move.
     */
    public abstract Point getMove();

    /**
     * Get the moves by the ai that will flip the most opponent pieces. The moves are represented by a point containing the
     * row and column of the move.
     *
     * @return the set of points of the moves that flip the most opponent pieces.
     */
    protected Set<Point> getMyMovesWithMostFlips(){

        Set<Point> mostFlipsMoves = new HashSet<>();
        int maxFlips = -1;

        ReversiBoard board = super.getBoard();

        for(Point p : super.getPossibleMoves()){
            int numFlips = board.findOpponentsToFlip(p.x, p.y).size();

            if(numFlips > maxFlips){
                mostFlipsMoves.add(p);
            }
        }

        return mostFlipsMoves;

    }

}
