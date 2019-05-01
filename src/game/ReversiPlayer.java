package game;

import util.MoveException;

import java.awt.*;
import java.util.Set;

/**
 * An interface for any type of player that wants to play reversi. <br>
 * This allows a network and local version of the game to use the same game logic.
 *
 * @author Brock Dyer.
 */
public interface ReversiPlayer {

    /**
     * Make a move.
     *
     * @param row the row to make the move in.
     * @param col the column to make a move in.
     * @throws MoveException thrown if the move requested was invalid.
     */
    void makeMove(int row, int col) throws MoveException;

    /**
     * Get the player's color.
     *
     * @return the string representation of the player's color.
     */
    String getColor();

    /**
     * Get a list of the location of every move this player has.
     *
     * @return the list of all possible move locations for this player.
     */
    Set<Point> getMoves();

    /**
     * Pass this player's turn to the opponent.
     */
    void pass();

    /**
     * Restart the game.<br>
     * If this is a networked game then both players must agree.
     */
    void restart();

    /**
     * Save the current game for later.<br>
     * If this is a networked game then both players must agree.
     *
     * @param filename the name of the file to save the game state to.
     */
    void save(String filename);

    /**
     * Load a game saved in the file system.<br>
     * If this is a networked game then both players must agree.
     *
     * @param filename the name of the file to load.
     */
    void load(String filename);

    /**
     * Quit the game. This forfeits the game for this player and the other player wins by default.
     */
    void quit();
}
