package network.server;

import game.ReversiGame;
import network.Duplexer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * The server for the reversi game.
 *
 * @author Brock Dyer.
 */
public class ReversiServer implements Runnable{

    private final ReversiGame game;

    private Duplexer currentPlayer, otherPlayer;

    private boolean sentinel;

    public ReversiServer(Duplexer client1, Duplexer client2) {
        this.currentPlayer = client1;
        this.otherPlayer = client2;
        this.game = new ReversiGame();
    }

    @Override
    public void run() {

        while(sentinel){



        }
    }

    /**
     * Change the duplexer reference of currentPlayer to point to otherPlayer and vice-versa.
     */
    private void changeTurn(){
        Duplexer temp = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = temp;
    }

    /**
     * Start the server program.<br>
     *     The server will keep accept a pair of clients and start a game between those two.
     *     The server will then wait for that game to finish and accept another pair.
     *
     * @param args cmd-line args. Expects the port to run the server on.
     */
    public static void main(String[] args) {

        if(args.length != 1){
            System.out.println("Usage: java ReversiServer #port");
            System.exit(-1);
        } else {

            int port = Integer.parseInt(args[0]);
            try {
                ServerSocket serverSocket = new ServerSocket(port);

                boolean accept = true;
                Scanner scanner = new Scanner(System.in);

                while(accept) {

                    Duplexer client1 = new Duplexer(serverSocket.accept());
                    Duplexer client2 = new Duplexer(serverSocket.accept());

                    ReversiServer server = new ReversiServer(client1, client2);
                    Thread t = new Thread(server);
                    t.start();

                    try {
                        t.join();
                    } catch (InterruptedException ie){
                        ie.printStackTrace();
                    }

                    System.out.println("Start another game? y/n");
                    String in = scanner.nextLine();
                    accept = in.strip().equals("y");

                }

            } catch (IOException ioe){
                ioe.printStackTrace();
            }

        }
    }
}
