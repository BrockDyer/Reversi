package game;

import game.core.PieceColor;
import game.core.ReversiGame;
import game.observer.ReversiObserver;
import gui.ReversiGUI;
import gui.events.ReversiEvent;
import util.MoveException;

import java.awt.*;
import java.util.Set;

/**
 * Represent a local player of the reversi game.
 *
 * @author Brock Dyer.
 */
public class Reversi implements ReversiPlayer, ReversiObserver {

    /**
     * The game logic.
     */
    private final ReversiGame game;

    /**
     * The gui that this player is using.
     */
    private final ReversiGUI gui;

    /**
     * The color of the current player.
     */
    private PieceColor color;

    /**
     * Start the game locally.
     */
    public Reversi(ReversiGUI gui) {
        this.gui = gui;
        this.game = new ReversiGame();
        this.color = PieceColor.BLACK;
        this.game.registerPlayerWithBoard(this);
    }

    @Override
    public void handle(ReversiEvent re) {
        int row = re.getRow();
        int col = re.getCol();
        PieceColor color = re.getColor();

        gui.updateBoard(row, col, color);
    }

    @Override
    public void makeMove(int row, int col) throws MoveException {
        game.makeMove(row, col);

        this.color = color == PieceColor.BLACK ? PieceColor.WHITE : PieceColor.BLACK;

        gui.updateScore(game.getBlackScore(), game.getWhiteScore());

        // Game win checking here.
        if(game.isGameOver()){
            String winner = game.getWinner();
            gui.updateIndicatorLabel(winner);
            return;
        }

        String player = game.getPieceAt(row, col).getColor() == PieceColor.BLACK ?
                PieceColor.WHITE.toString() : PieceColor.BLACK.toString();
        gui.updateIndicatorLabel(player.substring(0, 1) + player.substring(1).toLowerCase() +
                "'s Turn");

        gui.showAvailableMoves();
    }

    @Override
    public String getColor() {
        return color.toString();
    }

    @Override
    public void pass() {
        game.pass();
    }

    @Override
    public Set<Point> getMoves() {
        return game.getPossibleMoves();
    }

    @Override
    public void restart() {
        // We can just restart here because this is a local game. The player can do what they want.
        game.restart();
    }

    @Override
    public void save(String filename) {

    }

    @Override
    public void load(String filename) {

    }

    @Override
    public void quit() {
        game.quit();
    }

}
