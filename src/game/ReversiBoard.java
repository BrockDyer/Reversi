package game;

import gui.events.ReversiEvent;
import game.observer.ReversiObserver;
import game.observer.ReversiSubscriber;
import util.MoveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the board.
 *
 * @author Brock Dyer.
 */
public class ReversiBoard implements ReversiSubscriber {

    /**
     * A 2D array of pieces that represents the board.
     */
    private ReversiPiece[][] board;

    /**
     * The list of objects that are observing this board.
     */
    private List<ReversiObserver> observers;

    /**
     * The color of the current player.
     */
    private PieceColor currentPlayer;

    /**
     * Create an empty board.
     */
    public ReversiBoard() {
        this.observers = new ArrayList<>();
    }

    /**
     * A helper method to setup the defualt board.
     */
    private void initBoard(){
        this.board = new ReversiPiece[8][8];
        board[3][3] = new ReversiPiece(PieceColor.BLACK);
        alertObservers(3, 3, PieceColor.BLACK, "Initial");

        board[3][4] = new ReversiPiece(PieceColor.WHITE);
        alertObservers(3, 4, PieceColor.WHITE, "Initial");

        board[4][3] = new ReversiPiece(PieceColor.WHITE);
        alertObservers(4, 3, PieceColor.WHITE, "Initial");

        board[4][4] = new ReversiPiece(PieceColor.BLACK);
        alertObservers(4, 4, PieceColor.BLACK, "Initial");

        this.currentPlayer = PieceColor.BLACK;
    }

    /**
     * Can be called by the game to reset this board to default state.
     */
    public void reset(){
        initBoard();
    }

    @Override
    public void register(ReversiObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void deregister(ReversiObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Alert all observers subscribing to this board that a change has been made.
     *
     * @param row the row of the change.
     * @param col the column of the change.
     * @param color the color of the piece that changed.
     */
    private void alertObservers(int row, int col, PieceColor color, String debug){
        for(ReversiObserver observer : observers){
            observer.handle(new ReversiEvent(row, col, color, debug));
        }
    }

    /**
     * Alert all observers subscribing to this board that a change has been made.
     *
     * @param row the row of the change.
     * @param col the column of the change.
     * @param color the color of the piece that changed.
     */
    private void alertObservers(int row, int col, PieceColor color){
        alertObservers(row, col, color, "");
    }


    /**
     * Change the turn to the other player.
     */
    public void changeTurn() {
        this.currentPlayer = currentPlayer == PieceColor.BLACK ? PieceColor.WHITE : PieceColor.BLACK;
    }

    /**
     * Get the color of the current player.
     *
     * @return the color of the current player
     */
    public PieceColor getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Get the piece at the specified position.
     *
     * @param row the row of the piece.
     * @param col the column of the piece.
     * @return the piece at the requested position, null if there is no piece there.
     */
    public ReversiPiece getPiece(int row, int col){
        return board[row][col];
    }

    /**
     * Make a move on the board.
     *
     * @param row the row to make the move in.
     * @param col the column to move in.
     */
    public void move(int row, int col) throws MoveException {

        if(row >= 0 && row < 8 && col >= 0 && col < 8){

            ReversiPiece piece = board[row][col];

            List<Map<ReversiPiece, int[]>> toFlip;

            if(piece == null && (toFlip = findOpponentsToFlip(row, col)).size() > 0){

                board[row][col] = new ReversiPiece(currentPlayer);
                alertObservers(row, col, board[row][col].getColor());

                for(Map<ReversiPiece, int[]> map : toFlip){
                    for(ReversiPiece key : map.keySet()){
                        int[] coords = map.get(key);
                        key.toggle();
                        alertObservers(coords[0], coords[1], key.getColor());
                    }
                }

                return;

            }

            throw new MoveException("Invalid move." + (piece != null ? " That space is occupied already." :
                    " That move will not flip any opponent pieces."));

        }

        throw new MoveException("Invalid move. Index of move was outside allowed range.");

    }


    /**
     * Find the pieces that will be flipped if a move is played in the specified row and column.
     *
     * @param row the row the move is being made in.
     * @param col the column the move is being made in.
     * @return an array list of pieces that should be flipped if a piece is played in the given row and col.
     */
    private List<Map<ReversiPiece, int[]>> findOpponentsToFlip(int row, int col) {

        List<Map<ReversiPiece, int[]>> toFlip = new ArrayList<>();
        Map<ReversiPiece, int[]> flippable;

        for(Compass dir : Compass.values()){
            flippable = searchDirection(row, col, dir);
            if(flippable.keySet().size() > 0){
                toFlip.add(flippable);
            }
        }

        return toFlip;
    }

    /**
     * Find all the pieces that can be flipped in one direction from the starting move.
     *
     * @param row the row of the move.
     * @param col the column of the move.
     * @param dir the compass direction to check in.
     * @return a list of pieces in that direction that should be flipped. An empty list if there are none.
     */
    private Map<ReversiPiece, int[]> searchDirection(int row, int col, Compass dir) {

        Map<ReversiPiece, int[]> toFlip = new HashMap<>();

        while ((row += dir.getX()) < 8 && row >= 0 && (col += dir.getY()) < 8 && col >= 0) {

            ReversiPiece dPiece = board[row][col];
            if (dPiece != null) {

                if (dPiece.getColor() != currentPlayer) {
                    toFlip.put(dPiece, new int[]{row, col});
                } else {
                    if (toFlip.size() != 0) {
                        return toFlip;
                    }

                }
            } else {

                toFlip.clear();
                return toFlip;

            }

        }

        return toFlip;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for(ReversiPiece[] row : board){

            for(ReversiPiece piece : row){
                sb.append("|");
                if(piece == null){
                    sb.append(" ");
                } else {
                    sb.append(piece.toString());
                }
            }

            sb.append("|");
            sb.append("\n");

        }

        return sb.toString();
    }

}
