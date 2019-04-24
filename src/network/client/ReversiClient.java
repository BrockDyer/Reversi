package network.client;

import game.PieceColor;
import game.ReversiPlayer;
import gui.ReversiGUI;
import javafx.application.Platform;
import network.Duplexer;
import network.ReversiProtocol;
import util.MoveException;

import java.io.IOException;
import java.net.Socket;

/**
 * A client in the networked version of the reversi game.
 *
 * @author Brock Dyer.
 */
public class ReversiClient implements ReversiPlayer, Runnable {

    /**
     * A duplexer to send and receive messages from the server.
     */
    private final Duplexer coms;

    /**
     * An instance of the GUI that this player is using.
     */
    private final ReversiGUI gui;

    /**
     * A boolean flag to determine if the client should keep listening for messages from the server.
     */
    private boolean sentinel;

    /**
     * A boolean flag. True if it is this player's turn to make a move.
     */
    private boolean isMyTurn;

    /**
     * The color of this player.
     */
    private PieceColor myColor;

    /**
     * Create a new reversi client, this is a player.
     *
     * @param socket the socket to communicate with the server.
     * @throws IOException thrown if a duplexer cannot be constructed from the given socket.
     */
    public ReversiClient(Socket socket, ReversiGUI gui) throws IOException {
        this.coms = new Duplexer(socket);
        this.gui = gui;
        this.sentinel = true;
        System.out.println("Constructed client");
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {

        Platform.runLater(() -> gui.updateTurn(myColor == PieceColor.WHITE ? PieceColor.BLACK.toString() :
                PieceColor.WHITE.toString()));
        coms.sendMessage(ReversiProtocol.MOVE + " " + row + " " + col);

    }

    @Override
    public void pass() {
        if (isMyTurn) {
            coms.sendMessage(ReversiProtocol.PASS);
        }

    }

    @Override
    public void restart() {

        coms.sendMessage(ReversiProtocol.RESTART);

    }

    @Override
    public void save(String filename) {

        coms.sendMessage(ReversiProtocol.SAVE + " " + filename);

    }

    @Override
    public void load(String filename) {

        coms.sendMessage(ReversiProtocol.LOAD + " " + filename);

    }

    @Override
    public void quit() {
        coms.sendMessage(ReversiProtocol.QUIT);
    }

    @Override
    public void run() {

        while (sentinel) {


            String fromServer = coms.receiveMessage();
            String[] tokens = fromServer.split(" ");

            //System.out.println(fromServer);

            switch (tokens[0]) {

                case ReversiProtocol.WELCOME:
                    if (tokens.length == 2) {

                        String turn = tokens[1];
                        if (turn.equals("true")) {
                            this.isMyTurn = true;
                            this.myColor = PieceColor.BLACK;
                        } else if (turn.equals("false")) {
                            this.isMyTurn = false;
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
                    this.isMyTurn = true;
                    Platform.runLater(() -> gui.updateTurn(myColor.toString()));
                    break;

                case ReversiProtocol.MOVE_MADE:

                    if(tokens.length != 3){
                        System.err.println("Server sent bad request! Closing connection...");
                        sentinel = false;
                        break;
                    }

                    int black = Integer.parseInt(tokens[1]);
                    int white = Integer.parseInt(tokens[2]);

                    Platform.runLater(() -> gui.updateScore(black, white));

                    break;

                case ReversiProtocol.PIECE_UPDATE:

                    if (tokens.length == 4) {

                        int row = Integer.parseInt(tokens[1]);
                        int col = Integer.parseInt(tokens[2]);
                        String pieceColor = tokens[3];
                        PieceColor color;
                        if (pieceColor.equals("BLACK")) {
                            color = PieceColor.BLACK;
                        } else if (pieceColor.equals("WHITE")) {
                            color = PieceColor.WHITE;
                        } else {
                            System.err.println("Server sent invalid color! Closing connection...");
                            sentinel = false;
                            break;
                        }

                        gui.updateBoard(row, col, color);

                        if (color == myColor) {
                            isMyTurn = false;
                        }

                    } else {
                        System.err.println("Server sent bad message! Closing connection...");
                        sentinel = false;
                    }
                    break;

                case ReversiProtocol.GAME_WON:

                    // Update the user's GUI to show that they won against their opponent.

                    break;

                case ReversiProtocol.GAME_LOST:

                    // Update the user's GUI to show that they lost to their opponent.

                    break;

                case ReversiProtocol.GAME_TIED:

                    // Update the user's GUI to show that they tied with their opponent.

                    break;

                case ReversiProtocol.SAVE:

                    // Get confirmation from the user to save the current game.

                    break;

                case ReversiProtocol.LOAD:

                    // Get confirmation from user to load a new game.

                    break;

                case ReversiProtocol.RESTART:

                    // Get confirmation from user to restart the game.

                    break;

                case ReversiProtocol.QUIT:

                    // Get confirmation from user to quit the game.

                    break;

                default:
                    System.err.println("Server sent an unknown request! Closing connection...");
                    sentinel = false;
                    break;
            }

        }

        try {
            coms.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
