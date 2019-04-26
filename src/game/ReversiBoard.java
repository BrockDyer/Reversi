package game;

import game.observer.ReversiObserver;
import game.observer.ReversiSubscriber;
import gui.events.ReversiEvent;
import util.MoveException;

import java.util.*;

/**
 * Represents the board.
 *
 * @author Brock Dyer.
 */
public class ReversiBoard implements ReversiSubscriber {

    /**
     * A 2D array of pieces that represents the board.
     */
    private ReversiPiece[][] board;

    /**
     * The list of objects that are observing this board.
     */
    private List<ReversiObserver> observers;

    /**
     * The color of the current player.
     */
    private PieceColor currentPlayer;

    /**
     * The number of white and black pieces on the board respectively.
     */
    private int numWhite, numBlack;

    /**
     * Create an empty board.
     */
    public ReversiBoard() {
        this.observers = new ArrayList<>();
    }

    /**
     * A helper method to setup the defualt board.
     */
    private void initBoard() {
        this.board = new ReversiPiece[8][8];
        board[3][3] = new ReversiPiece(PieceColor.BLACK);
        alertObservers(3, 3, PieceColor.BLACK, "Initial");

        board[3][4] = new ReversiPiece(PieceColor.WHITE);
        alertObservers(3, 4, PieceColor.WHITE, "Initial");

        board[4][3] = new ReversiPiece(PieceColor.WHITE);
        alertObservers(4, 3, PieceColor.WHITE, "Initial");

        board[4][4] = new ReversiPiece(PieceColor.BLACK);
        alertObservers(4, 4, PieceColor.BLACK, "Initial");

        this.currentPlayer = PieceColor.BLACK;
        this.numBlack = 2;
        this.numWhite = 2;
    }

    /**
     * Can be called by the game to reset this board to default state.
     */
    public void reset() {
        initBoard();
    }

    @Override
    public void register(ReversiObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void deregister(ReversiObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Alert all observers subscribing to this board that a change has been made.
     *
     * @param row   the row of the change.
     * @param col   the column of the change.
     * @param color the color of the piece that changed.
     */
    private void alertObservers(int row, int col, PieceColor color, String debug) {
        for (ReversiObserver observer : observers) {
            observer.handle(new ReversiEvent(row, col, color, debug));
        }
    }

    /**
     * Alert all observers subscribing to this board that a change has been made.
     *
     * @param row   the row of the change.
     * @param col   the column of the change.
     * @param color the color of the piece that changed.
     */
    private void alertObservers(int row, int col, PieceColor color) {
        alertObservers(row, col, color, "");
    }


    /**
     * Change the turn to the other player.
     */
    public void changeTurn() {
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
     * Get the piece at the specified position.
     *
     * @param row the row of the piece.
     * @param col the column of the piece.
     * @return the piece at the requested position, null if there is no piece there.
     */
    public ReversiPiece getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Make a move on the board.
     *
     * @param row the row to make the move in.
     * @param col the column to move in.
     */
    public void move(int row, int col) throws MoveException {

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            ReversiPiece piece = board[row][col];

            if (piece == null) {

                List<Map<ReversiPiece, int[]>> toFlip = findOpponentsToFlip(row, col);

                if (toFlip.size() == 0) {
                    throw new MoveException("Invalid move by " + currentPlayer +
                            ". That move will not flip any opponent pieces.");
                }

                board[row][col] = new ReversiPiece(currentPlayer);
                numWhite += currentPlayer == PieceColor.WHITE ? 1 : 0;
                numBlack += currentPlayer == PieceColor.BLACK ? 1 : 0;
                alertObservers(row, col, board[row][col].getColor());

                for (Map<ReversiPiece, int[]> map : toFlip) {
                    for (ReversiPiece key : map.keySet()) {
                        int[] coords = map.get(key);
                        if (currentPlayer == PieceColor.WHITE) {
                            numBlack--;
                            numWhite++;
                        } else {
                            numBlack++;
                            numWhite--;
                        }
                        key.toggle();
                        alertObservers(coords[0], coords[1], key.getColor());
                    }
                }

                return;

            }

            throw new MoveException("Invalid move by " + currentPlayer + ". That space is occupied already.");

        }

        throw new MoveException("Invalid move by " + currentPlayer + ". Index of move was outside allowed range.");

    }


    /**
     * Find the pieces that will be flipped if a move is played in the specified row and column.
     *
     * @param row the row the move is being made in.
     * @param col the column the move is being made in.
     * @return an array list of pieces that should be flipped if a piece is played in the given row and col.
     */
    private List<Map<ReversiPiece, int[]>> findOpponentsToFlip(int row, int col) {

        List<Map<ReversiPiece, int[]>> toFlip = new ArrayList<>();
        Map<ReversiPiece, int[]> flippable;

        for (Compass dir : Compass.values()) {

            flippable = searchDirection(row, col, dir);
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
    private Map<ReversiPiece, int[]> searchDirection(int row, int col, Compass dir) {

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
    public Set<int[]> getPossibleMoves() {
        Set<int[]> possibleMoves = new HashSet<>();

        for (int r = 0; r < board.length; r++) {

            ReversiPiece[] row = board[r];

            for (int c = 0; c < row.length; c++) {

                if (row[c] != null) {
                    possibleMoves.addAll(findMovesFromPiece(r, c));
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
    private Set<int[]> findMovesFromPiece(int row, int col) {

        Set<int[]> possibleMoves = new HashSet<>();

        // Return if the piece here is not the current player's color.
        if (board[row][col].getColor() != currentPlayer) {
            return possibleMoves;
        }

        for (Compass dir : Compass.values()) {
            int[] moveLoc = searchMoveDir(row, col, dir);
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
     * @return an integer array containing the row and col of the possible move.
     */
    private int[] searchMoveDir(int row, int col, Compass dir) {

        int couldFlip = 0;

        while ((row += dir.getX()) < 8 && row >= 0 && (col += dir.getY()) < 8 && col >= 0) {

            ReversiPiece dPiece = board[row][col];

            if (dPiece == null) {
                if (couldFlip != 0) {
                    return new int[]{row, col};
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

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (ReversiPiece[] row : board) {

            for (ReversiPiece piece : row) {
                sb.append("|");
                if (piece == null) {
                    sb.append(" ");
                } else {
                    sb.append(piece.toString());
                }
            }

            sb.append("|");
            sb.append("\n");

        }

        return sb.toString();
    }

    /**
     * Get the number of white pieces.
     *
     * @return the number of white pieces.
     */
    public int getNumWhite() {
        return numWhite;
    }

    /**
     * Get the number of black pieces.
     *
     * @return the number of black pieces.
     */
    public int getNumBlack() {
        return numBlack;
    }

}
