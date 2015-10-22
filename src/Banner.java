import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class Banner extends StackPane{
    private Text value;
    private Rectangle bannerBackground;
    public Banner(){
        bannerBackground = new Rectangle(75, 25, Color.GRAY);
        bannerBackground.setArcWidth(25);
        bannerBackground.setArcHeight(25);
        bannerBackground.setStroke(Color.WHITE);
        value = new Text("None");
        value.setFill(Color.WHITE);
        this.getChildren().addAll(bannerBackground, value);
    }
    
    public void setValue(int val){
        bannerBackground.setFill(Display.pieceColor.get(val));
        value.setText(val+"");
        if(val <= 4){
            value.setFill(Color.BLACK);
        }
        else{
            value.setFill(Color.WHITE);
        }
    }
}
