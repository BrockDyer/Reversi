package ai;

import java.awt.*;
import java.util.Set;

/**
 * The most basic reversi ai. It will play in the move that maximizes its points.
 *
 * @author Brock Dyer.
 */
public class BasicAI extends ReversiAI {

    @Override
    public Point getMove() {
        Point move = null;
        Set<Point> moveSet = super.getMyMovesWithMostFlips();

        if(moveSet.size() != 0){
            move = (Point) moveSet.toArray()[0];
        }

        return move;
    }
}
