package client.gui.events;

import common.PieceColor;

/**
 * Define the functionality of all reversi events.
 *
 * @author Brock Dyer.
 */
public class ReversiEvent {

    private int row, col;
    private PieceColor color;

    private String debug;

    /**
     * Construct a ReversiEvent object.
     *
     * @param row the row of the event.
     * @param col the column of the event.
     * @param color the color of the piece involved with the event.
     * @param debug the debug information for this event.
     */
    public ReversiEvent(int row, int col, PieceColor color, String debug) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.debug = debug;
    }

    /**
     * Construct a ReversiEvent object.
     *
     * @param row the row of the event.
     * @param col the column of the event.
     * @param color the color of the piece involved with the event.
     */
    public ReversiEvent(int row, int col, PieceColor color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    /**
     * Get the row the event was in.
     *
     * @return the row of the event.
     */
    public int getRow(){
        return this.row;
    }

    /**
     * Get the column the event was in.
     *
     * @return the column of the event.
     */
    public int getCol(){
        return this.col;
    }

    /**
     * Get the color of the piece played.
     *
     * @return the color of the piece.
     */
    public PieceColor getColor(){
        return this.color;
    }

    /**
     * Print out debug information for the event.
     */
    public void debug(){
        System.out.println(debug);
    }
}
