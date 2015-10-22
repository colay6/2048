import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class Button extends StackPane{
    public Button(String text){
        Rectangle buttonBackground = new Rectangle(150, 50, Color.RED);
        buttonBackground.setArcWidth(10);
        buttonBackground.setArcHeight(10);
        buttonBackground.setStroke(Color.SALMON);
        buttonBackground.setStrokeWidth(2);
        
        Text buttonText = new Text(text);
        buttonText.setFont(Font.font("Sans Serif", FontWeight.BOLD, 24));
        buttonText.setFill(Color.WHITE);
        
        this.getChildren().addAll(buttonBackground, buttonText);
        
        this.setOnMouseEntered(e -> {
            buttonBackground.setFill(Color.SALMON);
            buttonBackground.setStroke(Color.RED);
        });
        this.setOnMouseExited(e -> {
            buttonBackground.setFill(Color.RED);
            buttonBackground.setStroke(Color.SALMON);
        });
    }
}
