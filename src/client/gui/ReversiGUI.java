package client.gui;

import common.PieceColor;
import common.Reversi;
import common.ReversiPiece;
import common.ReversiPlayer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.MoveException;


/**
 * A GUI for the common.Reversi game.
 *
 * @author Brock Dyer.
 */
public class ReversiGUI extends Application {

    /**
     * The path to the empty square image.
     */
    private final String EMPTY = "media/tile.png";

    /**
     * The path to the black piece image.
     */
    private final String BLACK = "media/black.png";
    /**
     * The path to the white piece image.
     */
    private final String WHITE = "media/white.png";

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


    @Override
    public void init() throws Exception {

        this.player = new Reversi();

    }

    @Override
    public void start(Stage stage) throws Exception {

        boardPane = new GridPane();

        for (int i = 0; i < boardSize; i++) {

            for (int j = 0; j < boardSize; j++) {

                Button b = new Button();
                b.setPadding(Insets.EMPTY);
                b.setId("" + i + j);

                Image image = new Image(this.getClass().getResourceAsStream(EMPTY));
                b.setGraphic(new ImageView(image));

                b.setOnAction(this::handleMove);

                boardPane.add(b, i, j);
            }

        }


        this.blackScore = new Label("Player 1: 2");
        this.whiteScore = new Label("Player 2: 2");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(blackScore, whiteScore);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(boardPane, vBox);
        Scene scene = new Scene(hBox);

        stage.setTitle("Reversi");
        stage.setScene(scene);
        stage.show();

    }


    /**
     * Handle a button press from the board.
     *
     * @param e the ActionEvent that was fired.
     */
    private void handleMove(ActionEvent e){
        if(e.getSource() instanceof Button){

            Button b = (Button)e.getSource();
            int row = Integer.parseInt(b.getId().substring(0, 1));
            int col = Integer.parseInt(b.getId().substring(1, 2));

            try {
                player.makeMove(row, col);

                ReversiPiece piece = player.checkPieceAt(row, col);

                placePiece(b, piece.getColor() == PieceColor.BLACK ? BLACK : WHITE);

            } catch (MoveException me){
                System.out.println(me.getMessage());
            }
        }
    }

    /**
     * Place a piece on the GUI by updating the graphic of the specified button.
     *
     * @param b the button to set the image of.
     */
    private void placePiece(Button b, String pieceString) {

        StackPane pieceImage = new StackPane();
        Image piece = new Image(this.getClass().getResourceAsStream(pieceString));
        pieceImage.getChildren().addAll(b.getGraphic(), new ImageView(piece));

        b.setGraphic(pieceImage);

    }


}
