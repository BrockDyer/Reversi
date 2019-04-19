package network;

/**
 * The protocol strings for the networked version of the game.
 *
 * @author Brock Dyer.
 */
public interface ReversiProtocol {
    /**
     * Server to client.<br>
     * Sends a welcome message to the two clients. <br>
     * Usage: WELCOME (String) "true" if it is the receiving player's turn, "false" otherwise.
     */
    String WELCOME = "WELCOME";
    /**
     * Server to client.<br>
     * Tell the client it is their turn.<br>
     * Usage: MAKE_MOVE
     */
    String MAKE_MOVE = "MAKE_MOVE";
    /**
     * Client to server.<br>
     * Sends a move request to the server.<br>
     * Usage: MOVE #row #col
     */
    String MOVE = "MOVE";
    /**
     * Server to client.<br>
     * Tells a client that a move was made.<br>
     * Usage: PIECE_UPDATE #row #col PieceColor
     */
    String PIECE_UPDATE = "PIECE_UPDATE";
    /**
     * Client to server.<br>
     * Pass the current turn to the other player.<br>
     * Usage: PASS
     */
    String PASS = "PASS";
    /**
     * Server to client.<br>
     * Tell a client that they won the game.<br>
     * Usage: GAME_WON to the client that won the game.
     */
    String GAME_WON = "GAME_WON";
    /**
     * Server to client.<br>
     *      Tell a client that they lost the game.<br>
     *      Usage: GAME_LOST to the client that lost the game.
     */
    String GAME_LOST = "GAME_LOST";
    /**
     * Server to client.<br>
     *      Tell a client that they tied the game.<br>
     *      Usage: GAME_TIED to the client that tied the game.
     */
    String GAME_TIED = "GAME_TIED";
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Save the current game to the file system the server is running on.<br>
     * Usage to server: SAVE (String) true/false <br>
     * Usage to client: SAVE
     */
    String SAVE = "SAVE";
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Load a game from the file system the server is running on.
     * Usage to server: LOAD filename (String) true/false <br>
     * Usage to client: LOAD
     */
    String LOAD = "LOAD";
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Restart the current game to the default state of the board.<br>
     * Usage to server: RESTART filename (String) true/false <br>
     * Usage to client: RESTART
     */
    String RESTART = "RESTART";
    /**
     * Two-way.<br>
     * Both players must agree.<br>
     * Quit the game.<br>
     * Usage to server: QUIT (String) true/false <br>
     * Usage to client: QUIT
     */
    String QUIT = "QUIT";


}
