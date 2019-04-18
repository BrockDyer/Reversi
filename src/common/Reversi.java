package common;

import util.MoveException;

/**
 * Represent a local player of the common.Reversi game.
 *
 * @author Brock Dyer.
 */
public class Reversi implements ReversiPlayer {

    /**
     * The game logic.
     */
    private final ReversiGame game;

    /**
     * Start the game locally.
     */
    public Reversi() {
        this.game = new ReversiGame();
    }

    @Override
    public boolean isMyTurn() {
        /* This can always return true because this is the local version of the game. Both players are using the same
        GUI. Therefore this should always return true and let the board handle which color is supposed to be playing.
        */
        return true;
    }

    @Override
    public ReversiPiece checkPieceAt(int row, int col) {
        return game.getPieceAt(row, col);
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {
        game.makeMove(row, col);
    }

    @Override
    public void pass() {
        game.pass();
    }

    @Override
    public void restart() {
        // We can just restart here because this is a local game. The player can do what they want.
        game.restart();
    }

    @Override
    public void save(String filename) {

    }

    @Override
    public void load(String filename) {

    }

    @Override
    public void quit() {
        game.quit();
    }

}
