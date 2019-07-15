package ai.difficulties;

import ai.AIConfiguration;
import ai.ReversiAI;
import game.core.PieceColor;
import game.core.ReversiBoard;
import util.MoveException;

import java.awt.*;
import java.util.Set;

/**
 * An ai that maximizes the number of pieces it flips in a given move.
 *
 * @author Brock Dyer.
 */
public class MaxFlipAI extends ReversiAI {

    /**
     * Used to keep track of what color this ai is playing as.
     */
    private PieceColor aiColor;

    /**
     * Construct parent.
     * Turn on debug output.
     *
     * @param DEBUG true if debug logs should be displayed.
     */
    public MaxFlipAI(boolean DEBUG){
        super(new ReversiBoard(), DEBUG);
    }

    @Override
    public Point determineMove(Set<Point> moveSet) {

        // Store the best move as we find it.
        Point bestMove = null;

        // A counter to record the highest number of pieces flipped.
        int numPieces = 0;

        System.out.println("AI: determining move...");

        for(Point p : moveSet){

            try {
                AIConfiguration test = new AIConfiguration(super.getBoard(), p);
                int testFlipped = test.getNumberFlipped();

                //System.out.print(testFlipped + " ");

                if(testFlipped > numPieces){
                    numPieces = testFlipped;
                    bestMove = p;
                }

            } catch (MoveException me){
                System.err.println(me.getMessage());
            }
        }

        //System.out.println("AI: decided on move (" + bestMove.x + ", " + bestMove.y + ") and flips " + numPieces);

        return bestMove;
    }
}
