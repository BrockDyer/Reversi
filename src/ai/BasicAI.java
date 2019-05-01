package ai;

import java.awt.*;

/**
 * The most basic reversi ai. It will play in the move that maximizes its points.
 *
 * @author Brock Dyer.
 */
public class BasicAI extends ReversiAI {

    @Override
    public Point getMove() {
        return super.getMyMoveWithMostFlips();
    }
}
