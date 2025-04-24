package Utils;

import java.io.IOException;

import Interface.HasPreviousController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Lớp tiện ích dùng để load các Scene trong ứng dụng
public final class SceneLoader {

    // Phương thức này dùng để load một Scene mới và hiển thị trên cửa sổ Stage mới
    public static void LoadScene(String sceneName) {
        try {
            // Tải FXML tương ứng với sceneName từ thư mục ui/newui
            FXMLLoader fxmlLoader = new FXMLLoader(SceneLoader.class.getResource("/ui/newui/" + sceneName));
            Parent root = fxmlLoader.load();

            // Sử dụng Platform.runLater để đảm bảo thao tác giao diện được thực hiện trên thread JavaFX
            Platform.runLater(() -> {
                Stage newStage = new Stage(); // Tạo một Stage mới
                newStage.setScene(new Scene(root)); // Gán Scene đã tải vào Stage
                newStage.show(); // Hiển thị Stage mới
            });
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi nếu không thể tải scene
        }
    }

    // Phương thức này dùng để load Scene mới, lấy controller của scene tiếp theo và set cho nó controller của scene hiện tại
    public static <CurrentController, NextController extends HasPreviousController<CurrentController>> NextController loadSceneAndGetController(String sceneName) {
        try {
            // Tải FXML tương ứng với sceneName từ thư mục ui/newui
            FXMLLoader fxmlLoader = new FXMLLoader(SceneLoader.class.getResource("/ui/newui/" + sceneName));
            Parent root = fxmlLoader.load();

            // Sử dụng Platform.runLater để đảm bảo thao tác giao diện được thực hiện trên thread JavaFX
            Platform.runLater(() -> {
                Stage newStage = new Stage(); // Tạo một Stage mới
                newStage.setScene(new Scene(root)); // Gán Scene đã tải vào Stage
                newStage.show(); // Hiển thị Stage mới
            });

            // Lấy controller của scene vừa load và trả về
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi nếu không thể tải scene
        }
        return null; // Nếu không thành công, trả về null
    }
}
