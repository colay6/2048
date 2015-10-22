import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    public static final int STAGE_WIDTH = 500;
    public static final int STAGE_HEIGHT = 600;
    
    @Override
    public void start (Stage stage) throws Exception {
        stage.setTitle("2048");
        stage.setResizable(false);
        Display display = new Display();
        Scene scene = display.initialize();
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args){
        launch(args);
    }
}
