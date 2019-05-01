package network.client;

import game.core.PieceColor;
import game.ReversiPlayer;
import gui.ReversiGUI;
import javafx.application.Platform;
import network.Duplexer;
import network.ReversiProtocol;
import util.MoveException;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

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
     * The set of moves available to the player.
     */
    private Set<Point> moveSet;

    /**
     * Create a new reversi client, this is a player.
     *
     * @param socket the socket to communicate with the server.
     * @throws IOException thrown if a duplexer cannot be constructed from the given socket.
     */
    public ReversiClient(Socket socket, ReversiGUI gui) throws IOException {
        this.moveSet = new HashSet<>();
        this.coms = new Duplexer(socket);
        this.gui = gui;
        this.sentinel = true;
        System.out.println("Constructed client");
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {

        String text = myColor == PieceColor.WHITE ? PieceColor.BLACK.toString() :
                PieceColor.WHITE.toString();
        Platform.runLater(() -> gui.updateIndicatorLabel(text.substring(0, 1) +
                text.substring(1).toLowerCase() + "'s Turn"));

        moveSet.clear();
        coms.sendMessage(ReversiProtocol.MOVE + " " + row + " " + col);

    }

    @Override
    public String getColor() {
        return myColor.toString();
    }

    @Override
    public void pass() {
        if (isMyTurn) {
            coms.sendMessage(ReversiProtocol.PASS);
        }

    }

    @Override
    public Set<Point> getMoves() {

        return this.moveSet;
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

    /**
     * Read this player's available moves from the string tokens received from the server.
     *
     * @param tokens an array of strings representing the individual data sent to the client.
     */
    private void readMoveSet(String[] tokens){

        this.moveSet.clear();

        for(int i = 1; i < tokens.length; i += 2){
            int row = Integer.parseInt(tokens[i]);
            int col = Integer.parseInt(tokens[i + 1]);
            this.moveSet.add(new Point(row, col));
        }

    }

    @Override
    public void run() {

        while (sentinel) {

            String fromServer;
            try {
                fromServer = coms.receiveMessage();
            } catch (NoSuchElementException nsee){
                Platform.runLater(() -> gui.updateIndicatorLabel("Server closed connection."));
                sentinel = false;
                break;
            }
            String[] tokens = fromServer.split(" ");

            //System.out.println(fromServer);

            switch (tokens[0]) {

                case ReversiProtocol.WELCOME:
                    if (tokens.length % 2 == 0) {

                        String turn = tokens[1];
                        if (turn.equals("true")) {
                            this.isMyTurn = true;
                            this.myColor = PieceColor.BLACK;

                        } else if (turn.equals("false")) {
                            this.isMyTurn = false;
                            this.myColor = PieceColor.WHITE;
                        } else {
                            System.err.println("Server sent invalid command parameter! Closing connection...");
                            Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                            sentinel = false;
                            break;
                        }

                        System.out.println(myColor.toString());

                    } else {
                        System.err.println("Server sent bad request! Closing connection...");
                        Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                        sentinel = false;
                    }
                    break;

                case ReversiProtocol.MAKE_MOVE:
                    if(tokens.length % 2 == 0){
                        System.err.println("Server sent bad request! Missing a row col pair. Closing connection...");
                        Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                        sentinel = false;
                        break;
                    }
                    this.isMyTurn = true;
                    Platform.runLater(() -> gui.updateIndicatorLabel("Your turn"));

                    readMoveSet(tokens);
                    Platform.runLater(gui::showAvailableMoves);
                    break;

                case ReversiProtocol.MOVE_MADE:

                    if (tokens.length != 3) {
                        System.err.println("Server sent bad request! Closing connection...");
                        Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                        sentinel = false;
                        break;
                    }

                    try {
                        int black = Integer.parseInt(tokens[1]);
                        int white = Integer.parseInt(tokens[2]);

                        Platform.runLater(() -> gui.updateScore(black, white));

                    } catch (NumberFormatException nfe){
                        System.err.println("Server sent bad packet! Closing connection...");
                        Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                        sentinel = false;
                    }

                    break;

                case ReversiProtocol.PIECE_UPDATE:

                    if (tokens.length == 4) {

                        try {
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
                                Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                                sentinel = false;
                                break;
                            }

                            gui.updateBoard(row, col, color);

                            if (color == myColor) {
                                isMyTurn = false;
                            }
                        } catch (NumberFormatException nfe){
                            System.err.println("Server sent bad packet!");
                            Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                            sentinel = false;
                        }

                    } else {
                        System.err.println("Server sent bad message! Closing connection...");
                        Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                        sentinel = false;
                    }
                    break;

                case ReversiProtocol.GAME_WON:

                    // Update the user's GUI to show that they won against their opponent.
                    Platform.runLater(() -> gui.updateIndicatorLabel("You won!"));
                    sentinel = false;

                    break;

                case ReversiProtocol.GAME_LOST:

                    // Update the user's GUI to show that they lost to their opponent.
                    Platform.runLater(() -> gui.updateIndicatorLabel("You lost!"));
                    sentinel = false;

                    break;

                case ReversiProtocol.GAME_TIED:

                    // Update the user's GUI to show that they tied with their opponent.
                    Platform.runLater(() -> gui.updateIndicatorLabel("It's a draw!"));
                    sentinel = false;

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
                    Platform.runLater(() -> gui.updateIndicatorLabel("Connection closed!"));
                    sentinel = false;
                    break;
            }

        }

        try {
            coms.close();
            System.out.println("Client duplexer closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
