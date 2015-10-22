import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class Scoreboard extends HBox{
    private int score = 0;
    private int bestPieceValue = 2;
    private Piece bestPiece;
    private Text scoreText;
    private StackPane scoreDisplay;
    private static final int PADDING = 10;
    public Scoreboard(){
        super(PADDING);
        this.setAlignment(Pos.CENTER);
        
        bestPiece = new Piece(50, bestPieceValue, 16);
        Rectangle piece = (Rectangle) bestPiece.getChildren().get(0);
        piece.setArcWidth(20);
        piece.setArcHeight(20);
        bestPiece.setValue(bestPieceValue);
        
        scoreDisplay = new StackPane();
        Rectangle scoreboard = new Rectangle(310, 50, Color.BLACK);
        scoreboard.setOpacity(0.8);
        scoreboard.setArcWidth(20);
        scoreboard.setArcHeight(20);
        scoreText = new Text(score + "");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(new Font(30));
        scoreDisplay.getChildren().addAll(scoreboard, scoreText);
        
        this.getChildren().addAll(bestPiece, scoreDisplay);
    }
    
    public void checkBestPiece(int value){
        if(value > bestPieceValue){
            bestPieceValue = value;
            bestPiece.setValue(bestPieceValue);
        }
    }
    
    public int getBestPiece(){
        return bestPieceValue;
    }
    
    public void updateScore(int value){
        score += value;
        scoreText.setText(score + "");
        Text update = new Text("+" + value);
        update.setFill(Color.WHITE);
        update.setFont(new Font(14));
        
        scoreDisplay.getChildren().add(update);
        
        FadeTransition ft = new FadeTransition(Duration.millis(600), update);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), update);
        tt.setFromX(80);
        tt.setFromY(-10);
        tt.setToX(80);
        tt.setToY(30);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
        
        pt.setOnFinished(e->{
            scoreDisplay.getChildren().remove(update);
        });
    }
    
    public void resetScore(){
        score = 0;
        scoreText.setText("" + score);
    }
}
