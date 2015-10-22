import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;



public class Menu extends StackPane {
    private int myWidth = Main.STAGE_WIDTH;
    private int myHeight = Main.STAGE_HEIGHT;
    private IntegerProperty selectedBoard;
    private Button playButton;
    private List<Banner> banners;
    
    private static final int DEMO_BOARD_SIZE = 120;

    public Menu(){
        banners = new ArrayList<>();
        
        Rectangle menuBackground = new Rectangle(myWidth, myHeight, Color.BLACK);
        VBox menuLayout = new VBox(75);
        menuLayout.setAlignment(Pos.CENTER);
        
        Text title = new Text("2048");
        title.setFont(Font.font("Sans Serif", FontWeight.BOLD, 54));
        title.setFill(Color.WHITE);
        HBox boardTypes = new HBox(20);
        boardTypes.setAlignment(Pos.CENTER);
        
        selectedBoard = new SimpleIntegerProperty(4);
        selectedBoard.addListener((obs, oldValue, newValue)->{
            for(Node node: boardTypes.getChildren()){
                VBox type = (VBox) node;
                Group board = (Group) type.getChildren().get(0);
                Rectangle background = (Rectangle) board.getChildren().get(0);
                if(boardTypes.getChildren().indexOf(node) == (int) newValue - 3){
                    background.setStroke(Color.YELLOW);
                    background.setStrokeWidth(5);
                }
                else{
                    background.setStroke(null);
                }
            }
        });
        
        for(int n = 3; n <= 5; n++){
            VBox typeToDisplay = new VBox(10);
            Group displayBoard = new Group();
            Rectangle boardPlaceholder = new Rectangle(DEMO_BOARD_SIZE, DEMO_BOARD_SIZE, Color.TAN);
            boardPlaceholder.setArcWidth(10);
            boardPlaceholder.setArcHeight(10);
            displayBoard.getChildren().add(boardPlaceholder);

            int boardPadding = 4;
            double cellSize = (DEMO_BOARD_SIZE - (n+1)*boardPadding + 0.0)/n;
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    Rectangle cell = new Rectangle(cellSize, cellSize, Color.BEIGE);
                    cell.setArcWidth(5);
                    cell.setArcHeight(5);
                    cell.setTranslateX(boardPadding + i*(cellSize + boardPadding));
                    cell.setTranslateY(boardPadding + j*(cellSize + boardPadding));
                    displayBoard.getChildren().add(cell);
                }
            }
            
            if(n == selectedBoard.getValue()){
                boardPlaceholder.setStroke(Color.YELLOW);
                boardPlaceholder.setStrokeWidth(5);
            }
            int boardIndex = n;
            displayBoard.setOnMousePressed(e -> selectedBoard.setValue(boardIndex));
            
            Banner banner = new Banner();
            banners.add(banner);
            typeToDisplay.setAlignment(Pos.CENTER);
            typeToDisplay.getChildren().addAll(displayBoard, banner);
            
            boardTypes.getChildren().add(typeToDisplay);
        }
        
        playButton = new Button("Play");
        
        VBox gameModes = new VBox(10);
        gameModes.setAlignment(Pos.CENTER);
        Text label = new Text("Select a Board:");
        label.setFill(Color.WHITE);
        label.setFont(new Font(24));
        gameModes.getChildren().addAll(label, boardTypes);
        menuLayout.getChildren().addAll(title, gameModes, playButton);
        this.getChildren().addAll(menuBackground, menuLayout);
    }
    
    public void updateBanners(Map<Integer, Scoreboard> scoreboards){
        for(Integer type: scoreboards.keySet()){
            banners.get(type-3).setValue(scoreboards.get(type).getBestPiece());
        }
    }
    
    public Button getButton(){
        return playButton;
    }
    
    public int getBoardSelected(){
        return selectedBoard.getValue();
    }
}
