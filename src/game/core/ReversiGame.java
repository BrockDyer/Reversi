package game.core;

import game.observer.ReversiObserver;
import util.MoveException;
import util.Utils;

import java.awt.*;
import java.util.Set;

/**
 * Holds the core logic of the reversi game. <br>
 * Checks win conditions.<br>
 * Handles invalid moves.
 *
 * @author Brock Dyer.
 */
public class ReversiGame {

    /**
     * The board to play on.
     */
    private ReversiBoard board;

    /**
     * The total number of spaces available for play.
     */
    private final int numSquares = 64;

    /**
     * A boolean flag for checking if the game is over. This will be used more in the network implementation.
     */
    private boolean gameOver;

    /**
     * The number of times a pass has been made. A pass is made if the current player cannot play on the board.
     * If 2 consecutive passes are made then the game is over.
     */
    private int passCount;

    /**
     * Initialize the game state.
     */
    public ReversiGame() {
        this.board = new ReversiBoard();
        this.gameOver = false;
    }

    /**
     * Get the board being played on.
     */
    protected ReversiBoard getBoard(){
        return this.board;
    }

    /**
     * Register a player with the board.
     *
     * @param player the player to register. Player must implement ReversiObserver.
     */
    public void registerPlayerWithBoard(ReversiObserver player) {
        this.board.register(player);
        board.reset();
    }

    /**
     * Get the piece at the specified position.
     *
     * @param row the row the piece is in.
     * @param col the column the piece is in.
     * @return the piece at that position, null if there is no piece there.
     */
    public ReversiPiece getPieceAt(int row, int col) {
        return board.getPiece(row, col);
    }

    /**
     * Used to determine the winner at the end of the game. <br>
     * This method will always return the string of the player with the highest score or "Draw"
     * if the score is tied.
     *
     * @return a string representing the player with the highest score or "Draw" if the game is tied.
     */
    public String getWinner() {

        int nb = board.getNumBlack();
        int nw = board.getNumWhite();

        return nb == nw ? "Draw" : (nb > nw ? "Black Won!" : "White Won!");

    }

    /**
     * Check if the game is over.
     * <p>
     * This can happen in two ways. The boolean flag gameOver is true, or all the spaces on the board have been
     * played in.
     * </p>
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Get the score for black.
     *
     * @return black's score.
     */
    public int getBlackScore() {
        return board.getNumBlack();
    }

    /**
     * Get the score for white.
     *
     * @return white's score.
     */
    public int getWhiteScore() {
        return board.getNumWhite();
    }

    /**
     * Attempt to make a move on the board.
     *
     * @param row the row to make the move in.
     * @param col the column to make the move in.
     */
    public void makeMove(int row, int col) throws MoveException {

        board.move(row, col);

        passCount = 0;

        PieceColor cp = board.getCurrentPlayer();

        if (board.getNumBlack() + board.getNumWhite() == numSquares) {
            gameOver = true;
        }

        board.changeTurn();

        int numMoves = Utils.getPossibleMoves(board.getCopyOfBoard(), board.getCurrentPlayer()).size();

        if (numMoves == 0) {
            pass();
        }

    }

    /**
     * Pass the current player's turn to the other player.
     */
    public void pass() {
        passCount++;

        if (passCount == 2) {
            gameOver = true;
        }

        board.changeTurn();
    }

    /**
     * Get a list of possible moves for the current player.
     *
     * @return a list of integer arrays containing the row and column of every possible move for the current player.
     * Empty list if no moves are possible.
     */
    public Set<Point> getPossibleMoves() {
        return Utils.getPossibleMoves(board.getCopyOfBoard(), board.getCurrentPlayer());
    }

    /**
     * Restart the game with a new board.
     */
    public void restart() {
        this.board.reset();
    }

    /**
     * Load a saved reversi game.
     *
     * @param filename the name of the board file.
     */
    public void loadGame(String filename) {

    }

    /**
     * Save the current game to the file system.
     *
     * @param filename the name of the file to save the game to.
     */
    public void saveGame(String filename) {

    }

    /**
     * Set the gameOver flag to true.
     */
    public void quit() {
        this.gameOver = true;
    }
}
