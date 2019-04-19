package network;

/**
 * The protocol strings for the networked version of the game.
 *
 * @author Brock Dyer.
 */
public enum ReversiProtocol {
    /**
     * Server to client.<br>
     * Sends a welcome message to the two clients. <br>
     * Usage: WELCOME (boolean) true if it is the receiving player's turn.
     */
    WELCOME,
    /**
     * Client to server.<br>
     * Sends a move request to the server.<br>
     * Usage: MOVE #row #col
     */
    MOVE,
    /**
     * Server to client.<br>
     * Tells a client that a move was made.<br>
     * Usage: MOVE_MADE #row #col
     */
    MOVE_MADE,
    /**
     * Client to server.<br>
     * Pass the current turn to the other player.<br>
     * Usage: PASS
     */
    PASS,
    /**
     * Server to client.<br>
     * Tell a client that they won the game.<br>
     * Usage: GAME_WON to the client that won the game.
     */
    GAME_WON,
    /**
     * Server to client.<br>
     *      Tell a client that they lost the game.<br>
     *      Usage: GAME_LOST to the client that lost the game.
     */
    GAME_LOST,
    /**
     * Server to client.<br>
     *      Tell a client that they tied the game.<br>
     *      Usage: GAME_TIED to the client that tied the game.
     */
    GAME_TIED,
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Save the current game to the file system the server is running on.<br>
     * Usage to server: SAVE (boolean)accept <br>
     * Usage to client: SAVE
     */
    SAVE,
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Load a game from the file system the server is running on.
     * Usage to server: LOAD (boolean) accept <br>
     * Usage to client: LOAD
     */
    LOAD,
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Restart the current game to the default state of the board.<br>
     * Usage to server: RESTART (boolean) accept <br>
     * Usage to client: RESTART
     */
    RESTART,
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Quit the game.<br>
     * Usage to server: QUIT (boolean) accept <br>
     * Usage to client: QUIT
     */
    QUIT

}
