import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String args[]) {
        Application.launch(args);
    }

    @Override
    public void start(Stage new_stage) throws Exception {
        Stage stage = new_stage;
        Parent root = FXMLLoader.load(getClass().getResource("/com.app/application.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Banking System Server");
        stage.getIcons().add(new Image("/images/applicationIcon.png"));
        stage.setHeight(600);
        stage.setWidth(900);
        stage.show();
    }
}
