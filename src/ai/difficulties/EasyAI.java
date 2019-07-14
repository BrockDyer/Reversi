package ai.difficulties;

import ai.ReversiAI;
import game.core.ReversiBoard;

import java.awt.*;
import java.util.Set;

/**
 * A very basic and easy ai.
 *
 * @author Brock Dyer.
 */
public class EasyAI extends ReversiAI {

    public final boolean DEBUG;

    /**
     * Construct parent.
     */
    public EasyAI(){
        this(false);
    }

    /**
     * Construct parent.
     * Turn on debug output.
     *
     * @param debug true if debug logs should be displayed.
     */
    public EasyAI(boolean debug){
        super(new ReversiBoard());
        this.DEBUG = debug;
    }

    @Override
    public Point determineMove(Set<Point> moveSet) {
        return null;
    }
}
