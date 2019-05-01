package gui;

import game.core.PieceColor;
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

import java.awt.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
     * The path to black's possible move overlay image.
     */
    private final String BLACK_MOVE_OVERLAY = "media/black_highlight.png";
    /**
     * The path to white's possible move overlay image.
     */
    private final String WHITE_MOVE_OVERLAY = "media/white_highlight.png";
    /**
     * The path to the blank overlay image.
     */
    private final String BLANK_OVERLAY = "media/blank.png";

    /**
     * The number of rows and columns in the board.
     */
    private final int BOARD_SIZE = 8;

    /**
     * The player using this GUI.
     */
    private ReversiPlayer player;

    /**
     * Labels for the scores of the players.
     */
    private Label blackScore, whiteScore;

    /**
     * Label for whose indicator.
     */
    private Label indicator;

    /**
     * The control to display the board to the user.
     */
    private GridPane boardPane;

    /**
     * The previous set of possible moves.
     */
    private Set<Point> lastMoveSet;

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

        this.lastMoveSet = new HashSet<>();

        for (int i = 0; i < BOARD_SIZE; i++) {

            for (int j = 0; j < BOARD_SIZE; j++) {

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

        this.blackScore = new Label("x 2");
        blackScore.setFont(scoreFont);
        blackScore.setTextFill(Color.IVORY);

        this.whiteScore = new Label("x 2");
        whiteScore.setFont(scoreFont);
        whiteScore.setTextFill(Color.IVORY);

        this.indicator = new Label("Black's Turn");
        indicator.setFont(scoreFont);
        indicator.setTextFill(Color.IVORY);

        String gameType = args.get(0);

        if (gameType.equals("client")) {
            if (args.size() != 3) {
                System.out.println("Usage: java ReversiGUI \"client\" hostname #port");
                System.exit(-1);
            }
            int port = Integer.parseInt(args.get(2));
            Socket socket = new Socket(args.get(1), port);

            this.player = new ReversiClient(socket, this);

            Thread t = new Thread((ReversiClient) this.player);
            t.setDaemon(true);
            t.start();

        } else if (gameType.equals("ai")) {
            // Setup ai here.

        } else {
            this.player = new Reversi(this);
            updateIndicatorLabel("Black's Turn");
            showAvailableMoves();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        HBox hBox = new HBox();
        ImageView blackIcon = new ImageView(new Image(this.getClass().getResourceAsStream(BLACK)));
        ImageView whiteIcon = new ImageView(new Image(this.getClass().getResourceAsStream(WHITE)));

        Region left = new Region();
        HBox.setHgrow(left, Priority.ALWAYS);
        Region right = new Region();
        HBox.setHgrow(right, Priority.ALWAYS);


        hBox.getChildren().addAll(blackIcon, blackScore, left, indicator, right, whiteIcon, whiteScore);
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

            lastMoveSet.remove(new Point(row, col));
            // Remove the old overlays.
            iterateMoveSet(lastMoveSet, BLANK_OVERLAY);

            try {
                player.makeMove(row, col);

            } catch (MoveException me) {
                System.out.println(me.getMessage());
                showAvailableMoves();
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

        int index = row * BOARD_SIZE + col;
        Button b = (Button) boardPane.getChildren().get(index);
        String pieceString = color == PieceColor.BLACK ? BLACK : WHITE;


        if (Platform.isFxApplicationThread()) {
            this.updateImage(b, pieceString);
        } else {
            Platform.runLater(() -> this.updateImage(b, pieceString));
        }
    }

    /**
     * Update the graphic of the specified button.
     *
     * @param b the button to set the image of.
     */
    private void updateImage(Button b, String imageString) {

        StackPane pieceImage;
        Node graphic = b.getGraphic();
        Image piece = new Image(this.getClass().getResourceAsStream(imageString));

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
    public void updateScore(int black, int white) {
        this.blackScore.setText("x " + black);
        this.whiteScore.setText("x " + white);
    }

    /**
     * Update the indicator indicator label.
     *
     * @param text the text to display on the indicator label.
     */
    public void updateIndicatorLabel(String text) {
        this.indicator.setText(text);
    }

    /**
     * Display the moves available to the player.
     */
    public void showAvailableMoves(){
        Set<Point> moveSet = this.player.getMoves();

        String color = player.getColor().equals("BLACK") ? BLACK_MOVE_OVERLAY : WHITE_MOVE_OVERLAY;
        iterateMoveSet(moveSet, color);

        this.lastMoveSet = moveSet;
    }


    private void iterateMoveSet(Set<Point> moveSet, String imageOverlay){
        for (Point loc : moveSet) {

            int row = loc.x, col = loc.y;

            Button b = (Button) boardPane.getChildren().get(row * BOARD_SIZE + col);

            updateImage(b, imageOverlay);
        }
    }
}
