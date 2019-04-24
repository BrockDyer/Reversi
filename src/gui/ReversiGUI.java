package gui;

import game.PieceColor;
import game.Reversi;
import game.ReversiPlayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import network.client.ReversiClient;
import util.MoveException;

import java.net.Socket;
import java.util.List;


/**
 * A GUI for the reversi game.
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

    /**
     * The font to use to display the score.
     */
    private Font scoreFont;

    @Override
    public void init() throws Exception {

        // get the command line args
        List<String> args = getParameters().getRaw();

        scoreFont = new Font("Helvetica-Bold", 20);

        boardPane = new GridPane();

        for (int i = 0; i < boardSize; i++) {

            for (int j = 0; j < boardSize; j++) {

                Button b = new Button();
                b.setPadding(Insets.EMPTY);
                b.setId("" + i + j);

                Image image = new Image(this.getClass().getResourceAsStream(EMPTY));
                b.setGraphic(new ImageView(image));
                b.setFocusTraversable(false);

                b.setOnAction(this::handleMove);

                boardPane.add(b, i, j);
            }

        }

        this.blackScore = new Label("x 02");
        blackScore.setFont(scoreFont);
        blackScore.setTextFill(Color.IVORY);

        this.whiteScore = new Label("x 02");
        whiteScore.setFont(scoreFont);
        whiteScore.setTextFill(Color.IVORY);

        String gameType = args.get(0);

        if (gameType.equals("client")) {
            if (args.size() != 3) {
                System.out.println("Usage: java ReversiGUI \"client\" hostname #port");
                System.exit(-1);
            }
            int port = Integer.parseInt(args.get(2));
            Socket socket = new Socket(args.get(1), port);

            this.player = new ReversiClient(socket, this);

            Thread t = new Thread((ReversiClient)this.player);
            t.start();

        } else if (gameType.equals("ai")) {
            // Setup ai here.

        } else {
            this.player = new Reversi(this);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        HBox hBox = new HBox();
        ImageView blackIcon = new ImageView(new Image(this.getClass().getResourceAsStream(BLACK)));
        ImageView whiteIcon = new ImageView(new Image(this.getClass().getResourceAsStream(WHITE)));

        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);

        hBox.getChildren().addAll(blackIcon, blackScore, r, whiteIcon, whiteScore);
        hBox.setAlignment(Pos.CENTER);
        hBox.setBackground(new Background(new BackgroundFill(Color.SADDLEBROWN, CornerRadii.EMPTY, Insets.EMPTY)));


        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, boardPane);
        Scene scene = new Scene(vBox);

        stage.setTitle("Reversi");
        stage.setScene(scene);

        stage.show();

    }

    /**
     * The main method expects the host and port.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Usage: java ReversiGUI local");
            System.out.println("Usage: java ReversiGUI \"client\" hostname #port");
            System.out.println("Usage: java ReversiGUI ai");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }

    /**
     * Handle a button press from the board.
     *
     * @param e the ActionEvent that was fired.
     */
    private void handleMove(ActionEvent e) {
        if (e.getSource() instanceof Button) {

            Button b = (Button) e.getSource();
            int row = Integer.parseInt(b.getId().substring(0, 1));
            int col = Integer.parseInt(b.getId().substring(1, 2));

            try {
                player.makeMove(row, col);

            } catch (MoveException me) {
                System.out.println(me.getMessage());
            }
        }
    }

    /**
     * Update the given index on the board.
     *
     * @param row   the row of the piece to update.
     * @param col   the column of the piece to update.
     * @param color the color of the piece to update.
     */
    public void updateBoard(int row, int col, PieceColor color) {

        int index = row * boardSize + col;
        Button b = (Button) boardPane.getChildren().get(index);
        String pieceString = color == PieceColor.BLACK ? BLACK : WHITE;


        if (Platform.isFxApplicationThread()) {
            this.updatePiece(b, pieceString);
        } else {
            Platform.runLater(() -> this.updatePiece(b, pieceString));
        }
    }

    /**
     * Place a piece on the GUI by updating the graphic of the specified button.
     *
     * @param b the button to set the image of.
     */
    private void updatePiece(Button b, String pieceString) {

        StackPane pieceImage;
        Node graphic = b.getGraphic();
        Image piece = new Image(this.getClass().getResourceAsStream(pieceString));

        if (graphic instanceof StackPane) {
            pieceImage = (StackPane) graphic;
            pieceImage.getChildren().remove(1);
            pieceImage.getChildren().add(new ImageView(piece));
        } else {
            pieceImage = new StackPane();
            pieceImage.getChildren().addAll(graphic, new ImageView(piece));
        }

        b.setGraphic(pieceImage);

    }

    /**
     * Update the scores for each player.
     *
     * @param black the score for black.
     * @param white the score for white.
     */
    public void updateScore(int black, int white){
        this.blackScore.setText("x " + black);
        this.whiteScore.setText("x " + white);
    }

}
