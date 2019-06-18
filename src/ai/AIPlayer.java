package ai;

import game.ReversiPlayer;
import game.core.PieceColor;
import javafx.application.Platform;
import network.Duplexer;
import network.ReversiProtocol;
import util.MoveException;
import util.Utils;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Class purpose.
 *
 * @author Brock Dyer.
 */
public class AIPlayer implements ReversiPlayer, Runnable {

    /**
     * Used to communicate with the server.
     */
    private Duplexer duplexer;

    /**
     * A boolean flag. True if it is this player's turn to make a move.
     */
    private boolean isMyTurn;

    /**
     * The color of this player.
     */
    private PieceColor myColor;

    /**
     * A boolean flag to determine if the ai should keep listening for messages from the server.
     */
    private boolean sentinel;

    /**
     * The set of moves available to the player.
     */
    private Set<Point> moveSet;

    public AIPlayer(Socket socket) throws IOException {
        this.duplexer = new Duplexer(socket);
        this.isMyTurn = false;
        this.sentinel = true;
        this.moveSet = new HashSet<>();
        System.out.println("Constructed AI.");
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {
        moveSet.clear();
        duplexer.sendMessage(ReversiProtocol.MOVE + " " + row + " " + col);
    }

    @Override
    public String getColor() {
        return this.myColor.toString();
    }

    @Override
    public Set<Point> getMoves() {
        return this.moveSet;
    }

    @Override
    public void pass() {
        if(isMyTurn){
            duplexer.sendMessage(ReversiProtocol.PASS);
        }
    }

    @Override
    public void restart() {

    }

    @Override
    public void save(String filename) {

    }

    @Override
    public void load(String filename) {

    }

    @Override
    public void quit() {

    }

    @Override
    public void run() {

        while(sentinel){
            String fromServer;
            try {
                fromServer = duplexer.receiveMessage();
            } catch (NoSuchElementException nsee){
                sentinel = false;
                break;
            }

            String[] tokens = fromServer.split(" ");

            switch(tokens[0]){

                case ReversiProtocol.WELCOME:

                    if (tokens.length % 2 == 0) {

                        String turn = tokens[1];
                        if (turn.equals("true")) {
                            this.myColor = PieceColor.BLACK;

                        } else if (turn.equals("false")) {
                            this.myColor = PieceColor.WHITE;
                        } else {
                            System.err.println("Server sent invalid command parameter! Closing connection...");
                            sentinel = false;
                            break;
                        }

                        System.out.println(myColor.toString());

                    } else {
                        System.err.println("Server sent bad request! Closing connection...");
                        sentinel = false;
                    }

                    break;

                case ReversiProtocol.MAKE_MOVE:

                    if(tokens.length % 2 == 0){
                        System.err.println("Server sent bad request! Missing a row col pair. Closing connection...");
                        sentinel = false;
                        break;
                    }

                    this.isMyTurn = true;
                    Utils.readMoveSet(tokens, this.moveSet);

                    Point movePoint = null;
                    boolean tryMove = true;
                    while(tryMove) {
                        try {
                            // Determine AI move here.


                            makeMove(movePoint.x, movePoint.y);

                            tryMove = false;

                        } catch (MoveException me) {
                            System.out.println("AI move failed...");
                            System.err.println(me.getMessage());
                        }
                    }

                    break;

                case ReversiProtocol.PIECE_UPDATE:

                    // Update the AI's copy of the board, a piece is being flipped.

                    break;

                case ReversiProtocol.MOVE_MADE:

                    // Update the AI's copy of the board, a move was made.

                    break;

                case ReversiProtocol.GAME_WON:

                    // The ai has won the game.

                    break;

                case ReversiProtocol.GAME_TIED:

                    // A draw has occurred.

                    break;

                case ReversiProtocol.GAME_LOST:

                    // The ai has lost the game.

                    break;

                case ReversiProtocol.LOAD:

                    // Confirm the other player's request to load from a save.

                    break;

                case ReversiProtocol.SAVE:

                    // Confirm the other player's request to save the current game.

                    break;

                case ReversiProtocol.RESTART:

                    // Confirm the other player's request to restart the current game.

                    break;

                case ReversiProtocol.QUIT:

                    // Confirm the other player's request to quit the current game.

                    break;

                default:

                    System.err.println("Server sent an unknown request! Closing connection...");
                    sentinel = false;
                    break;
            }
        }
    }
}