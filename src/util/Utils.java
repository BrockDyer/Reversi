package util;

import game.core.Compass;
import game.core.PieceColor;
import game.core.ReversiPiece;

import java.awt.*;
import java.util.*;
import java.util.List;

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

    /**
     * Find the pieces that will be flipped if a move is played in the specified row and column.
     *
     * @param row the row the move is being made in.
     * @param col the column the move is being made in.
     * @return an array list of pieces that should be flipped if a piece is played in the given row and col.
     */
    public static List<Map<ReversiPiece, int[]>> findOpponentsToFlip(int row, int col, ReversiPiece[][] board,
                                                                     PieceColor currentPlayer) {

        List<Map<ReversiPiece, int[]>> toFlip = new ArrayList<>();
        Map<ReversiPiece, int[]> flippable;

        for (Compass dir : Compass.values()) {

            flippable = searchDirection(row, col, dir, board, currentPlayer);
            if (flippable.keySet().size() > 0) {
                toFlip.add(flippable);
            }

        }

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
    public static Map<ReversiPiece, int[]> searchDirection(int row, int col, Compass dir, ReversiPiece[][] board,
                                                           PieceColor currentPlayer) {

        Map<ReversiPiece, int[]> toFlip = new HashMap<>();

        while ((row += dir.getX()) < 8 && row >= 0 && (col += dir.getY()) < 8 && col >= 0) {

            ReversiPiece dPiece = board[row][col];
            if (dPiece != null) {

                if (dPiece.getColor() != currentPlayer) {
                    toFlip.put(dPiece, new int[]{row, col});
                } else {

                    if (toFlip.size() != 0) {
                        return toFlip;
                    } else {
                        toFlip.clear();
                        return toFlip;
                    }

                }
            } else {

                toFlip.clear();
                return toFlip;

            }

        }

        toFlip.clear();
        return toFlip;
    }

    /**
     * Get a set of the locations of all possible moves the current player can make.
     *
     * @return a set of integer arrays containing the row and col of a possible move. Empty set if no moves are
     * possible for the current player.
     */
    public static Set<Point> getPossibleMoves(ReversiPiece[][] board, PieceColor currentPlayer) {
        Set<Point> possibleMoves = new HashSet<>();

        for (int r = 0; r < board.length; r++) {

            ReversiPiece[] row = board[r];

            for (int c = 0; c < row.length; c++) {

                if (row[c] != null) {
                    possibleMoves.addAll(findMovesFromPiece(r, c, board, currentPlayer));
                }

            }
        }

        return possibleMoves;
    }

    /**
     * Find all possible moves for the current player from a given piece. Assumes the given location contains a piece.
     *
     * @param row the row of the piece to check.
     * @param col the column of the piece to check.
     * @return a set of the locations of all possible moves for the current player from the piece at the specified
     * location.
     */
    public static Set<Point> findMovesFromPiece(int row, int col, ReversiPiece[][] board,
                                                PieceColor currentPlayer) {

        Set<Point> possibleMoves = new HashSet<>();

        // Return if the piece here is not the current player's color.
        if (board[row][col].getColor() != currentPlayer) {
            return possibleMoves;
        }

        for (Compass dir : Compass.values()) {
            Point moveLoc = searchMoveDir(row, col, dir, board, currentPlayer);
            if (moveLoc != null) {
                possibleMoves.add(moveLoc);
            }
        }

        return possibleMoves;

    }

    /**
     * Search outward in all directions from the location given to find a possible location the current player
     * could move. Assumes that the piece at the location specified is the current player's color and is not empty.
     *
     * @param row the row to start at.
     * @param col the column to start at.
     * @param dir the direction to search in.
     * @return an point containing the row and col of the possible move.
     */
    public static Point searchMoveDir(int row, int col, Compass dir, ReversiPiece[][] board,
                                      PieceColor currentPlayer) {

        int couldFlip = 0;

        while ((row += dir.getX()) < 8 && row >= 0 && (col += dir.getY()) < 8 && col >= 0) {

            ReversiPiece dPiece = board[row][col];

            if (dPiece == null) {
                if (couldFlip != 0) {
                    return new Point(row, col);
                } else {
                    return null;
                }
            }

            if (dPiece.getColor() == currentPlayer) {
                return null;
            }

            couldFlip++;

        }

        return null;
    }
}
