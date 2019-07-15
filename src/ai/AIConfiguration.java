package ai;

import game.core.PieceColor;
import game.core.ReversiBoard;
import game.core.ReversiPiece;
import util.MoveException;
import util.Utils;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * A move configuration for the ai to use to test moves without disturbing the actual game board.
 *
 * @author Brock Dyer.
 */
public class AIConfiguration {

    /**
     * The copy of the game board's underlying array.
     */
    private ReversiPiece[][] boardCopy;

    /**
     * The number of pieces flipped by this move configuration.
     */
    private int numberFlipped;

    public AIConfiguration(ReversiBoard board, Point move) throws MoveException {
        this.boardCopy = board.getCopyOfBoard();
        if(boardCopy[move.x][move.y] != null){
            throw new MoveException("AI tried to make a move in an occupied square.");
        }

        List<Map<ReversiPiece, int[]>> toFlip = Utils.findOpponentsToFlip(move.x, move.y, boardCopy, PieceColor.WHITE);

        if (toFlip.size() == 0) {
            throw new MoveException("Invalid ai move. That move will not flip any opponent pieces.");
        }

        numberFlipped = Utils.calcPiecesFlipped(toFlip);
    }

    /**
     * Get the number of pieces flipped by this move.
     *
     * @return the number of pieces flipped by making this move.
     */
    public int getNumberFlipped(){
        return numberFlipped;
    }
}
