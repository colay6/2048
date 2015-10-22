import java.util.Map;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class Piece extends StackPane{
    private IntegerProperty value;
    private Text valueText;
    private int xIndex;
    private int yIndex;
    private Rectangle background;
    private boolean alreadyMerged = false;
    private int fontSize;
    
    private static final int CELL_PADDING = 10;
    
    public Piece(double cellSize, int val, int font){
        fontSize = font;
        setupPieceLayout(cellSize, val);
    }
    
    public Piece(double x, double y, double cellSize, int val, int font){
        fontSize = font;
        setupPieceLayout(cellSize, val);
        this.setTranslateX(x);
        this.setTranslateY(y);
        
        xIndex = (int)((y-CELL_PADDING)/(cellSize+CELL_PADDING));
        yIndex = (int)((x-CELL_PADDING)/(cellSize+CELL_PADDING));
        
        playEntryAnimation();
    }
    
    private void setupPieceLayout(double cellSize, int val){
        value = new SimpleIntegerProperty(val);
        value.addListener((obs, oldValue, newValue) -> {
            if((int) newValue <= 2048){
                background.setFill(Display.pieceColor.get(newValue));
            }
            else{
                background.setFill(Color.BLACK);
            }
            
            if((int)newValue > 4){
                valueText.setFill(Color.WHITE);
            }
        });
        background = new Rectangle(cellSize, cellSize, Display.pieceColor.get(value.getValue()));
        background.setArcWidth(10);
        background.setArcHeight(10);
        valueText = new Text("" + value.getValue());
        valueText.setFill(Color.BLACK);
        valueText.setFont(new Font(fontSize));
        this.getChildren().addAll(background, valueText);
    }
    
    public void playEntryAnimation(){
        ScaleTransition st = new ScaleTransition(Duration.millis(200), this);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }
    
    private void playMergeAnimation(){
        ScaleTransition st = new ScaleTransition(Duration.millis(100), this);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
    
    public TranslateTransition movePiece(double[] location, int i, int j){
        xIndex = i;
        yIndex = j;
        TranslateTransition move = new TranslateTransition(Duration.millis(100), this);
        move.setFromX(this.getTranslateX());
        move.setFromY(this.getTranslateY());
        move.setToX(location[0]);
        move.setToY(location[1]);
        move.play();
//        this.setTranslateX(location[0]);
//        this.setTranslateY(location[1]);
        return move;
    }
    
    public boolean isAlreadyMerged(){
        return alreadyMerged;
    }
    
    public void setAlreadyMerged(boolean merge){
        alreadyMerged = merge;
    }
    
    public void setValue(int val){
        if(val!=value.getValue()){
            playMergeAnimation();
        }
        value.setValue(val);
        valueText.setText(""+val);
    }
    
    public int getValue(){
        return value.getValue();
    }
    
    public int getXIndex(){
        return xIndex;
    }
    
    public int getYIndex(){
        return yIndex;
    }
    
    public boolean isAtLocation(int x, int y){
        return x == xIndex && y == yIndex;
    }
    
    public Map<Integer, Color> getColorMap(){
        return Display.pieceColor;
    }
}
