package ai;

import game.core.ReversiGame;

import java.awt.*;


/**
 * Define the common functionality of a reversi ai.
 *
 * @author Brock Dyer.
 */
public abstract class ReversiAI extends ReversiGame {

    private final ReversiGame game;

    public ReversiAI(ReversiGame game) {
        this.game = game;
    }

    /**
     * Get the move, as a point, that the ai will make.
     *
     * @return the point containing the row and column of the move.
     */
    public abstract Point getMove();

    /**
     * Get the move by the ai that will flip the most opponent pieces. The move is represented by a point containing the
     * row and column of the move.
     *
     * @return the point of the move that flips the most opponent pieces.
     */
    protected Point getMyMoveWithMostFlips(){

        Point mostFlipsMove = null;
        int maxFlips = -1;

        for(Point p : super.getPossibleMoves()){

        }

        return mostFlipsMove;

    }

}
