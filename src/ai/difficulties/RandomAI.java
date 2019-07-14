package ai.difficulties;

import ai.ReversiAI;
import game.core.ReversiBoard;

import java.awt.*;
import java.util.Random;
import java.util.Set;

/**
 * An AI that makes a random move.
 *
 * @author Brock Dyer.
 */
public class RandomAI extends ReversiAI {

    /**
     * A boolean flag. True if debug info should be logged to console.
     */
    public final boolean DEBUG;

    /**
     * A random to generate a random integer.
     */
    private final Random random;

    /**
     * Construct parent.
     *
     * @param DEBUG true if debug info should be logged to console.
     */
    public RandomAI(boolean DEBUG){
        super(new ReversiBoard());
        this.DEBUG = DEBUG;
        this.random = new Random();
    }

    @Override
    public Point determineMove(Set<Point> moveSet) {

        int rand = random.nextInt(moveSet.size());
        int count = 0;

        for(Point p : moveSet){
            if(count == rand){
                return p;
            }
            count++;
        }

        return null;
    }
}
