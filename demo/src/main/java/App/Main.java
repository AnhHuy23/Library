package App;

import ManageData.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.newui.LoginController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        DatabaseHandler.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/newui/login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        controller.setStage(primaryStage);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}