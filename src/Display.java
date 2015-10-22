import java.util.HashMap;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class Display {
    private Group myRoot;
    
    private Board board;
    private Menu menu;
    private VBox arrangement;
    
    private int myWidth = Main.STAGE_WIDTH;
    private int myHeight = Main.STAGE_HEIGHT;
    private static final int LAYOUT_PADDING = 10;
    private static final int PADDING = 20;
    private static final int HEADER_PADDING = 10;
    
    public static Map<Integer, Color> pieceColor;
    private Map<Integer, Scoreboard> scoreboards;
    
    public Scene initialize(){
        myRoot = new Group();
        
        pieceColor = new HashMap<>();
        pieceColor.put(2, Color.web("#CCFF99"));
        pieceColor.put(4, Color.web("#99FFCC"));
        pieceColor.put(8, Color.web("#19E8E8"));
        pieceColor.put(16, Color.web("#00E6B8"));
        pieceColor.put(32, Color.web("#5CE65C"));
        pieceColor.put(64, Color.web("#197519"));
        pieceColor.put(128, Color.web("#2E8AE6"));
        pieceColor.put(256, Color.web("#005CE6"));
        pieceColor.put(512, Color.web("#000099"));
        pieceColor.put(1024, Color.web("#000066"));
        pieceColor.put(2048, Color.web("#000000"));
        
        StackPane display = new StackPane();
        Rectangle background = new Rectangle(myWidth, myHeight, Color.WHITE);
        display.getChildren().add(background);
        scoreboards = new HashMap<>();
        
        menu = new Menu();
        menu.getButton().setOnMouseClicked(e -> {
            display.getChildren().remove(arrangement);
            arrangement = new VBox(LAYOUT_PADDING);
            HBox layout = new HBox(HEADER_PADDING);

            Scoreboard scoreboard;
            if(scoreboards.get(menu.getBoardSelected()) == null){
                scoreboard = new Scoreboard();
                scoreboards.put(menu.getBoardSelected(), scoreboard);
            }
            else{
                scoreboard = scoreboards.get(menu.getBoardSelected());
                scoreboard.resetScore();
            }
            
            //Setup board
            board = new Board(myWidth-PADDING, menu.getBoardSelected(), scoreboard);
            board.isLost().addListener((obs, oldValue, newValue)->{
                if(newValue){
                    setupLossScreen(display, scoreboard);
                }
            });
            
            StackPane helpButton = setupHelpButton();
            helpButton.setOnMouseClicked(e3 -> {
                StackPane helpPanel = new StackPane();
                Rectangle helpPanelBackground = new Rectangle(300, 300);
                helpPanelBackground.setOpacity(0.8);
                helpPanelBackground.setArcWidth(20);
                helpPanelBackground.setArcHeight(20);
                VBox panelLayout = new VBox(70);
                panelLayout.setAlignment(Pos.CENTER);
                VBox helpTextLayout = new VBox(10);
                helpTextLayout.setAlignment(Pos.CENTER);
                Text title = new Text("HOW TO PLAY");
                title.setFill(Color.WHITE);
                title.setFont(new Font(30));
                Text helpText = new Text("Use your arrow keys to move the tiles. When two tiles with the same number touch, they merge into one!");
                helpText.setFill(Color.WHITE);
                helpText.setWrappingWidth(200);
                helpText.setTextAlignment(TextAlignment.JUSTIFY);
                
                Text tip = new Text("That number in the top left shows the highest piece you've gotten on the current board.");
                tip.setFill(Color.WHITE);
                tip.setWrappingWidth(200);
                tip.setTextAlignment(TextAlignment.JUSTIFY);
                
                VBox footer = new VBox(10);
                footer.setAlignment(Pos.CENTER);
                StackPane closeButton = new StackPane();
                Rectangle buttonBackground = new Rectangle(80, 30, Color.DARKBLUE);
                buttonBackground.setArcWidth(30);
                buttonBackground.setArcHeight(30);
                Text close = new Text("Close");
                close.setFill(Color.WHITE);
                closeButton.getChildren().addAll(buttonBackground, close);
                closeButton.setOnMouseClicked(closed -> {
                    ScaleTransition closeAway = new ScaleTransition(Duration.millis(200), helpPanel);
                    closeAway.setFromX(1.0);
                    closeAway.setFromY(1.0);
                    closeAway.setToX(0);
                    closeAway.setToY(0);
                    closeAway.play();
                    closeAway.setOnFinished(finished -> {
                        display.getChildren().remove(helpPanel);
                    });
                });
                Text credits = new Text("Developed by Kevin He");
                credits.setFont(Font.font("Comic Sans", 10));
                credits.setFill(Color.WHITE);
                footer.getChildren().addAll(closeButton, credits);
                
                helpTextLayout.getChildren().addAll(title, helpText, tip);
                panelLayout.getChildren().addAll(helpTextLayout, footer);
                helpPanel.getChildren().addAll(helpPanelBackground, panelLayout);
                display.getChildren().add(helpPanel);
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), helpPanel);
                scale.setFromX(0);
                scale.setFromY(0);
                scale.setToX(1.2);
                scale.setToY(1.2);
                ScaleTransition bounce = new ScaleTransition(Duration.millis(200), helpPanel);
                bounce.setFromX(1.2);
                bounce.setFromY(1.2);
                bounce.setToX(1);
                bounce.setToY(1);
                SequentialTransition show = new SequentialTransition(scale,bounce);
                show.play();
            });
            
            StackPane menuButton = setupMenuButton();
            menuButton.setOnMouseClicked(e2 -> {
                display.getChildren().add(menu);
                menu.updateBanners(scoreboards);
                FadeTransition fade = new FadeTransition(Duration.millis(300), menu);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();
            });
            
            //Wire up the restart button to the current board
            StackPane restartButton = setupRestartButton();
            restartButton.setOnMouseClicked(e1 -> {
                board.restartGame();
                scoreboard.resetScore();
            });
            
            //Wire up the move counter
            StackPane moveCountDisplay = setupMoveCountDisplay();
            
            HBox buttonLayout = new HBox(HEADER_PADDING);
            buttonLayout.setAlignment(Pos.CENTER);
            buttonLayout.getChildren().addAll(menuButton, restartButton, helpButton);
            
            layout.getChildren().addAll(scoreboard, moveCountDisplay);
            layout.setAlignment(Pos.CENTER);
            
            arrangement.setAlignment(Pos.TOP_CENTER);
            arrangement.setTranslateY(LAYOUT_PADDING);
            arrangement.getChildren().addAll(layout, board, buttonLayout);
            
            display.getChildren().add(1, arrangement);
            FadeTransition fade = new FadeTransition(Duration.millis(500), menu);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.play();
            fade.setOnFinished(ae -> {
                display.getChildren().remove(menu);
            });
        });
        display.getChildren().add(menu);
        
        myRoot.getChildren().add(display);
        Scene scene = new Scene(myRoot, myWidth, myHeight, Color.WHITE);
        scene.setOnKeyPressed(e -> handleKeyPress(e));
        return scene;
    }
    
    private StackPane setupHelpButton(){
        Text help = new Text("?");
        help.setFont(Font.font("Comic Sans", FontWeight.BOLD, 20));
        return setupButton(help);
    }
    
    private StackPane setupMenuButton(){
        VBox menuStack = new VBox(2);
        for(int i = 0; i <= 2; i++){
            Rectangle line = new Rectangle(20, 3);
            line.setArcWidth(3);
            line.setArcHeight(3);
            menuStack.getChildren().add(line);
        }
        menuStack.setAlignment(Pos.CENTER);
        return setupButton(menuStack);
    }
    
    private StackPane setupRestartButton(){
        ImageView restart = new ImageView(new Image("images/tryagain.png"));
        restart.setFitWidth(30);
        restart.setFitHeight(30);
        restart.setPreserveRatio(true);
        restart.setSmooth(true);
        return setupButton(restart);
    }
    
    private StackPane setupButton (Node buttonContent) {
        StackPane button = new StackPane();
        Rectangle background = new Rectangle(30, 30, Color.TAN);
        background.setArcWidth(20);
        background.setArcHeight(20);
        button.getChildren().addAll(background, buttonContent);
        return button;
    }

    /**
     * @param display
     * @param scoreboard
     */
    private void setupLossScreen (StackPane display, Scoreboard scoreboard) {
        StackPane loseScreen = new StackPane();
        Rectangle loseBackground = new Rectangle(Main.STAGE_WIDTH, Main.STAGE_HEIGHT, Color.BLACK);
        loseBackground.setOpacity(0.8);
        VBox loseLayout = new VBox(40);
        loseLayout.setAlignment(Pos.CENTER);
        Text loseText = new Text("You Lose");
        loseText.setFont(Font.font("Sans Serif", FontWeight.BOLD, 42));
        loseText.setFill(Color.WHITE);
        
        StackPane playAgainButton = new Button("Play Again");
        playAgainButton.setOnMouseClicked(e -> {
            board.restartGame();
            display.getChildren().remove(loseScreen);
            scoreboard.resetScore();
        });
        loseLayout.getChildren().addAll(loseText, playAgainButton);
        
        loseScreen.getChildren().addAll(loseBackground, loseLayout);
        display.getChildren().add(loseScreen);
        
        PauseTransition pause = new PauseTransition(Duration.millis(1000));
        FadeTransition fade = new FadeTransition(Duration.millis(1000), loseScreen);
        fade.setFromValue(0);
        fade.setToValue(1);
        SequentialTransition st = new SequentialTransition(pause, fade);
        st.play();
    }

    private StackPane setupMoveCountDisplay () {
        StackPane moveCountDisplay = new StackPane();
        Rectangle moveCountBackground = new Rectangle(100, 50, Color.BLACK);
        moveCountBackground.setOpacity(0.8);
        moveCountBackground.setArcWidth(20);
        moveCountBackground.setArcHeight(20);
        VBox moveDisplayLayout = new VBox(2);
        moveDisplayLayout.setAlignment(Pos.CENTER);
        Text moveLabel = new Text("Moves");
        moveLabel.setFill(Color.WHITE);
        moveLabel.setFont(new Font(14));
        Text moveCount = new Text("0");
        moveCount.setFill(Color.WHITE);
        moveCount.setFont(Font.font("Sans Serif", FontWeight.BOLD, 20));
        moveDisplayLayout.getChildren().addAll(moveLabel, moveCount);
        moveCountDisplay.getChildren().addAll(moveCountBackground, moveDisplayLayout);
        
        board.getMoveCounter().addListener((obs, oldValue, newValue) -> {
            moveCount.setText(newValue + "");
            ScaleTransition pop = new ScaleTransition(Duration.millis(50), moveCount);
            pop.setFromX(1);
            pop.setFromY(1);
            pop.setToX(1.2);
            pop.setToY(1.2);
            pop.setAutoReverse(true);
            pop.setCycleCount(2);
            pop.play();
        });
        return moveCountDisplay;
    }
    
    private void handleKeyPress(KeyEvent e){
        KeyCode keyPressed = e.getCode();
        if(keyPressed == KeyCode.UP || keyPressed == KeyCode.W){
            board.shiftUp();
        }
        if(keyPressed == KeyCode.DOWN || keyPressed == KeyCode.S){
            board.shiftDown();
        }
        if(keyPressed == KeyCode.LEFT || keyPressed == KeyCode.A){
            board.shiftLeft();
        }
        if(keyPressed == KeyCode.RIGHT || keyPressed == KeyCode.D){
            board.shiftRight();
        }
    }
}
