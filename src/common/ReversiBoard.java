package common;

import util.MoveException;

import java.util.ArrayList;
import java.util.List;

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
     */
    public void move(int row, int col) throws MoveException {

        if(row >= 0 && row < 8 && col >= 0 && col < 8){

            ReversiPiece piece = board[row][col];

            List<ReversiPiece> toFlip;

            if(piece == null && (toFlip = findOpponentsToFlip(row, col)).size() > 0){

                board[row][col] = new ReversiPiece(currentPlayer);

                for(ReversiPiece reversiPiece : toFlip){
                    reversiPiece.toggle();
                }

                changeTurn();
                return;

            }

            throw new MoveException("Invalid move." + (piece != null ? " That space is occupied already." :
                    " That move will not flip any opponent pieces."));

        }

        throw new MoveException("Invalid move. Index of move was outside allowed range.");

    }


    /**
     * Find the pieces that will be flipped if a move is played in the specified row and column.
     *
     * @param row the row the move is being made in.
     * @param col the column the move is being made in.
     * @return an array list of pieces that should be flipped if a piece is played in the given row and col.
     */
    private List<ReversiPiece> findOpponentsToFlip(int row, int col) {

        List<ReversiPiece> toFlip = new ArrayList<>();
        toFlip.addAll(searchDirection(row, col, Compass.N));
        toFlip.addAll(searchDirection(row, col, Compass.NE));
        toFlip.addAll(searchDirection(row, col, Compass.E));
        toFlip.addAll(searchDirection(row, col, Compass.SE));
        toFlip.addAll(searchDirection(row, col, Compass.S));
        toFlip.addAll(searchDirection(row, col, Compass.SW));
        toFlip.addAll(searchDirection(row, col, Compass.W));
        toFlip.addAll(searchDirection(row, col, Compass.NW));

        return toFlip;
    }

    /**
     * Find all the pieces that can be flipped in one direction from the starting move.
     *
     * @param row the row of the move.
     * @param col the column of the move.
     * @param dir the compass direction to check in.
     * @return a list of pieces in that direction that should be flipped. An empty list if there are none.
     */
    private List<ReversiPiece> searchDirection(int row, int col, Compass dir) {

        List<ReversiPiece> toFlip = new ArrayList<>();

        while ((row += dir.getX()) < 8 && row >= 0 && (col += dir.getY()) < 8 && col >= 0) {

            ReversiPiece dPiece = board[row][col];
            if (dPiece != null) {

                if (dPiece.getColor() != currentPlayer) {
                    toFlip.add(dPiece);
                } else {
                    if (toFlip.size() != 0) {
                        return toFlip;
                    }

                }
            } else {

                toFlip.clear();
                return toFlip;

            }

        }

        return toFlip;
    }

}
