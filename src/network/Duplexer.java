package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A two-way communication system between sockets. Uses a text-based protocol to communicate.
 *
 * @author Brock Dyer.
 */
public class Duplexer implements AutoCloseable{

    /**
     * The socket to communicate over.
     */
    private Socket socket;
    /**
     * The writer to write text to the output stream of the socket.
     */
    private PrintWriter writer;
    /**
     * A scanner to read text input from the socket's input stream.
     */
    private Scanner scanner;

    /**
     * Creates a new Duplexer.
     *
     * @param socket the socket to communicate over.
     *
     * @throws IOException if something goes wrong initializing io.
     */
    public Duplexer(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.scanner = new Scanner(socket.getInputStream());
    }

    /**
     * Send a message over the output stream.
     *
     * @param msg the message to send.
     */
    public void sendMessage(String msg){
        writer.println(msg);
        writer.flush();
    }

    /**
     * Receive a message from the input stream. This call will block the execution of the thread from which it was
     * called until it receives a message.
     *
     * @return the message received.
     */
    public String receiveMessage(){
        return scanner.nextLine();
    }

    @Override
    public void close() throws Exception {
        writer.close();
        scanner.close();
        socket.close();
    }
}
