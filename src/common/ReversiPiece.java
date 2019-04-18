package common;

/**
 * A piece on the Reversi
 *
 * @author Brock Dyer.
 */
public class ReversiPiece {

    /**
     * The color of this piece.
     */
    private PieceColor color;

    /**
     * Create a new piece with the specified color.
     *
     * @param color the color of the piece.
     */
    public ReversiPiece(PieceColor color) {
        this.color = color;
    }

    /**
     * Toggles the color of this piece.
     */
    public void toggle() {
        this.color = color == PieceColor.BLACK ? PieceColor.WHITE : PieceColor.BLACK;
    }

}
