package common;

import java.util.ArrayList;

/**
 * Represent the board.
 *
 * @author Brock Dyer.
 */
public class ReversiBoard {

    /**
     * A 2D array of pieces that represents the board.
     */
    private ReversiPiece[][] board;

    /**
     * The color of the current player.
     */
    private PieceColor currentPlayer;

    /**
     * Create an empty board.
     */
    public ReversiBoard() {
        this.board = new ReversiPiece[8][8];
        this.currentPlayer = PieceColor.BLACK;
    }

    /**
     * Change the turn to the other player.
     */
    private void changeTurn() {
        this.currentPlayer = currentPlayer == PieceColor.BLACK ? PieceColor.WHITE : PieceColor.BLACK;
    }

    /**
     * Get the color of the current player.
     *
     * @return the color of the current player
     */
    public PieceColor getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Make a move on the board.
     *
     * @param row the row to make the move in.
     * @param col the column to move in.
     * @return true if the move was successful.
     */
    public boolean move(int row, int col) {

        return false;
    }


    /**
     * Find the pieces that will be flipped if a move is played in the specified row and column.
     *
     * @param row the row the move is being made in.
     * @param col the column the move is being made in.
     * @return an array list of pieces that should be flipped if a piece is played in the given row and col.
     */
    private ArrayList<ReversiPiece> findOpponentsToFlip(int row, int col) {

        ArrayList<ReversiPiece> toFlip = new ArrayList<>();

        return toFlip;
    }


}
