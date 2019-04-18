package client;

import common.ReversiPlayer;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * A GUI for the common.Reversi game.
 *
 * @author Brock Dyer.
 */
public class ReversiGUI extends Application {

    /**
     * The number of rows and columns in the board.
     */
    private final int boardSize = 8;

    /**
     * The player using this GUI.
     */
    private ReversiPlayer player;

    /**
     * Labels for the scores of the players.
     */
    private Label blackScore, whiteScore;
    /**
     * The control to display the board to the user.
     */
    private GridPane boardPane;

    /**
     * Initialize the state of the GUI.
     */
    public ReversiGUI(ReversiPlayer player) {
        this.player = player;
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
