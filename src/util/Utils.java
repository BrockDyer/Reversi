package util;

import java.awt.*;
import java.util.Set;

/**
 * Utility methods.
 *
 * @author Brock Dyer.
 */
public class Utils {

    /**
     * Read a player's available moves from the string tokens received from the server.
     *
     * @param tokens an array of strings representing the individual data sent to the client.
     * @param moveSet the set to store the moves in.
     */
    public static void readMoveSet(String[] tokens, Set<Point> moveSet){
        moveSet.clear();

        for(int i = 1; i < tokens.length; i += 2){
            int row = Integer.parseInt(tokens[i]);
            int col = Integer.parseInt(tokens[i + 1]);
            moveSet.add(new Point(row, col));
        }
    }
}
