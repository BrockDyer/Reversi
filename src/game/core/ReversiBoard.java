package game.core;

import game.observer.ReversiObserver;
import game.observer.ReversiSubscriber;
import gui.events.ReversiEvent;
import util.MoveException;
import util.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

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

                List<Map<ReversiPiece, int[]>> toFlip = Utils.findOpponentsToFlip
                        (row, col, this.board, this.currentPlayer);

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


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {

            for (int j = 0; j < board[i].length; j++) {
                sb.append("|");
                if (board[j][i] == null) {
                    sb.append(" ");
                } else {
                    sb.append(board[j][i].toString());
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

    /**
     * Create and get a copy of the underlying array of the board.
     *
     * @return a copy of the board's underlying array.
     */
    public ReversiPiece[][] getCopyOfBoard(){

        ReversiPiece[][] boardCopy = new ReversiPiece[board.length][];
        for(int i = 0; i < board.length; i++) {
            boardCopy[i] = board[i].clone();
        }

        return boardCopy;
    }

}
