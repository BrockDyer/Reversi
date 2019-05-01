package network.server;

import game.PieceColor;
import game.ReversiGame;
import game.observer.ReversiObserver;
import gui.events.ReversiEvent;
import network.Duplexer;
import network.ReversiProtocol;
import util.MoveException;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Set;

/**
 * The server for the reversi game.
 *
 * @author Brock Dyer.
 */
public class ReversiServer implements ReversiObserver, Runnable {

    /**
     * The instance of the game logic.
     */
    private final ReversiGame game;

    /**
     * Duplexers to communicate with the two clients.
     */
    private Duplexer currentPlayer, otherPlayer;

    /**
     * The color of the current player.
     */
    private PieceColor currentColor;

    /**
     * A boolean flag to determine when the server thread should stop looping.
     */
    private boolean sentinel;

    /**
     * Create the server.
     *
     * @param client1 the first client.
     * @param client2 the second client.
     */
    public ReversiServer(Duplexer client1, Duplexer client2) {
        this.currentPlayer = client1;
        this.otherPlayer = client2;
        this.game = new ReversiGame();

        this.game.registerPlayerWithBoard(this);

        this.currentColor = PieceColor.BLACK;
        this.sentinel = true;
    }

    @Override
    public void handle(ReversiEvent re) {
        int row = re.getRow();
        int col = re.getCol();
        PieceColor color = re.getColor();

        currentPlayer.sendMessage(ReversiProtocol.PIECE_UPDATE + " " + row + " " + col + " " + color);
        otherPlayer.sendMessage(ReversiProtocol.PIECE_UPDATE + " " + row + " " + col + " " + color);
    }

    /**
     * Turn the set of moves available to the current player into a string formatted for the transmission protocol.
     *
     * @return the formatted string representation of the set.
     */
    private String moveSetMsg(){

        Set<Point> moveSet = game.getPossibleMoves();

        StringBuilder sb = new StringBuilder();
        for(Point p : moveSet){
            sb.append(" ");
            sb.append(p.x);
            sb.append(" ");
            sb.append(p.y);
        }

        return sb.toString();

    }

    @Override
    public void run() {

        while (sentinel) {

            currentPlayer.sendMessage(ReversiProtocol.MAKE_MOVE + moveSetMsg());

            String response = currentPlayer.receiveMessage();
            String[] tokens = response.split(" ");
            String[] responseTokens;

            switch (tokens[0]) {

                case ReversiProtocol.MOVE:

                    try {
                        int row = Integer.parseInt(tokens[1]);
                        int col = Integer.parseInt(tokens[2]);
                        game.makeMove(row, col);

                        currentPlayer.sendMessage(ReversiProtocol.MOVE_MADE + " " + game.getBlackScore() +
                                " " + game.getWhiteScore());
                        otherPlayer.sendMessage(ReversiProtocol.MOVE_MADE + " " + game.getBlackScore() +
                                " " + game.getWhiteScore());

                        // Do game end condition checking.
                        if(game.isGameOver()){
                            String winner = game.getWinner().toLowerCase();
                            if(winner.contains("black")){
                                if(currentColor == PieceColor.BLACK){
                                    currentPlayer.sendMessage(ReversiProtocol.GAME_WON);
                                    otherPlayer.sendMessage(ReversiProtocol.GAME_LOST);
                                } else {
                                    currentPlayer.sendMessage(ReversiProtocol.GAME_LOST);
                                    otherPlayer.sendMessage(ReversiProtocol.GAME_WON);
                                }
                            } else if(winner.contains("white")){
                                if(currentColor == PieceColor.WHITE){
                                    currentPlayer.sendMessage(ReversiProtocol.GAME_WON);
                                    otherPlayer.sendMessage(ReversiProtocol.GAME_LOST);
                                } else {
                                    currentPlayer.sendMessage(ReversiProtocol.GAME_LOST);
                                    otherPlayer.sendMessage(ReversiProtocol.GAME_WON);
                                }
                            } else {
                                currentPlayer.sendMessage(ReversiProtocol.GAME_TIED);
                                otherPlayer.sendMessage(ReversiProtocol.GAME_TIED);
                            }

                            sentinel = false;

                        }

                        changeTurn();

                    } catch (MoveException me) {
                        System.out.println(me.getMessage());
                        break;
                    } catch (NumberFormatException nfe) {
                        System.out.println("Bad response from client! Closing connections...");
                        sentinel = false;
                    }

                    break;

                case ReversiProtocol.PASS:
                    game.pass();
                    break;

                case ReversiProtocol.SAVE:
                    otherPlayer.sendMessage(ReversiProtocol.SAVE);

                    responseTokens = otherPlayer.receiveMessage().split(" ");
                    if (responseTokens.length == 3) {
                        if (responseTokens[2].equals("true")) {
                            // Save the game.
                        }
                    } else {
                        System.err.println("Bad response from client! Closing connections...");
                        sentinel = false;
                    }

                    break;

                case ReversiProtocol.LOAD:
                    otherPlayer.sendMessage(ReversiProtocol.LOAD);

                    responseTokens = otherPlayer.receiveMessage().split(" ");
                    if (responseTokens.length == 3) {
                        if (responseTokens[2].equals("true")) {
                            // Load the game.
                        }
                    } else {
                        System.err.println("Bad response from client! Closing connections...");
                        sentinel = false;
                    }
                    break;

                case ReversiProtocol.QUIT:
                    otherPlayer.sendMessage(ReversiProtocol.QUIT);

                    responseTokens = otherPlayer.receiveMessage().split(" ");
                    if (responseTokens.length == 2) {
                        if (responseTokens[1].equals("true")) {
                            game.quit();
                            sentinel = false;
                        }
                    } else {
                        System.err.println("Bad response from client! Closing connections...");
                        sentinel = false;
                    }
                    break;

                case ReversiProtocol.RESTART:
                    otherPlayer.sendMessage(ReversiProtocol.RESTART);

                    responseTokens = otherPlayer.receiveMessage().split(" ");
                    if (responseTokens.length == 2) {
                        if (responseTokens[1].equals("true")) {
                            game.restart();
                            sentinel = false;
                        }
                    } else {
                        System.err.println("Bad response from client! Closing connections...");
                        sentinel = false;
                    }
                    break;

                default:
                    System.err.println("Unknown request from client! Closing connections...");
                    sentinel = false;
                    break;
            }

        }

        try {
            currentPlayer.close();
            otherPlayer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the duplexer reference of currentPlayer to point to otherPlayer and vice-versa.
     */
    private void changeTurn() {
        Duplexer temp = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = temp;
        currentColor = currentColor == PieceColor.BLACK ? PieceColor.WHITE : PieceColor.BLACK;
    }

    /**
     * Start the server program.<br>
     * The server will keep accept a pair of clients and start a game between those two.
     * The server will then wait for that game to finish and accept another pair.
     *
     * @param args cmd-line args. Expects the port to run the server on.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java ReversiServer #port");
            System.exit(-1);
        } else {

            int port = Integer.parseInt(args[0]);
            System.out.println("Starting server on port " + port);

            try {
                ServerSocket serverSocket = new ServerSocket(port);

                boolean accept = true;
                Scanner scanner = new Scanner(System.in);

                while (accept) {

                    Duplexer client1 = new Duplexer(serverSocket.accept());
                    System.out.println("Client 1 connected...");
                    client1.sendMessage(ReversiProtocol.WELCOME + " true");

                    Duplexer client2 = new Duplexer(serverSocket.accept());
                    System.out.println("Client 2 connected...");
                    client2.sendMessage(ReversiProtocol.WELCOME + " false");

                    ReversiServer server = new ReversiServer(client1, client2);
                    System.out.println("Starting game...");
                    Thread t = new Thread(server);
                    t.start();

                    try {
                        t.join();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                    System.out.println("Start another game? y/n");
                    String in = scanner.nextLine();
                    accept = in.strip().equals("y");

                }

                serverSocket.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }
}
