package common;

import util.MoveException;

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
     * The number of pieces each player has played on the board.
     */
    private int black, white;

    /**
     * Two passes in a row means that the game is over.
     */
    private int passCount;

    /**
     * A boolean flag for checking if the game is over. This will be used more in the network implementation.
     */
    private boolean gameOver;

    /**
     * Initialize the game state.
     */
    public ReversiGame() {
        this.board = new ReversiBoard();
        this.black = 2;
        this.white = 2;
        this.gameOver = false;
    }

    /**
     * Register a player with the board.
     *
     * @param player the player to register. Player must implement ReversiObserver.
     */
    public void registerPlayerWithBoard(ReversiPlayer player){
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
    public ReversiPiece getPieceAt(int row, int col){
        return board.getPiece(row, col);
    }

    /**
     * Used to determine the winner at the end of the game. <br>
     * This method will always return the string of the player with the highest score or "Draw"
     * if the score is tied.
     *
     * @return a string representing the player with the highest score or "Draw" if the game is tied.
     */
    private String getWinner() {

        if (black > white) {
            return "Black";
        } else if (black < white) {
            return "White";
        } else {
            return "Draw";
        }

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
    private boolean isGameOver() {
        return gameOver || black + white == numSquares || passCount >= 2;
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

        black += cp == PieceColor.BLACK ? 1 : 0;
        white += cp == PieceColor.WHITE ? 1 : 0;

        if (isGameOver()) {
            gameOver = true;
            // Do any game ending stuff here.
        }

        board.changeTurn();


    }

    /**
     * Pass the current player's turn to the other player.
     */
    public void pass() {
        passCount++;
        board.changeTurn();
    }

    /**
     * Restart the game with a new board.
     */
    public void restart() {
        this.board.reset();
    }

    /**
     * Load a saved common.Reversi game.
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
