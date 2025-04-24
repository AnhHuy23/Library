package ui.newui;

import com.jfoenix.controls.JFXButton;

import Utils.AlertUtils;
import Utils.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Thông tin đăng nhập mặc định
    private final String adminName = "admin"; // Tên đăng nhập mặc định
    private final String password = "123"; // Mật khẩu mặc định

    // Vị trí chuột để hỗ trợ di chuyển cửa sổ
    private double x = 0, y = 0;

    // Các trường nhập liệu và nút trong giao diện
    @FXML
    private TextField adminNameField; // Trường nhập tên đăng nhập

    @FXML
    private PasswordField passwordField; // Trường nhập mật khẩu

    @FXML
    private JFXButton loginButton; // Nút đăng nhập

    @FXML
    private AnchorPane sideBar; // Phần sidebar để kéo cửa sổ

    private Stage stage; // Cửa sổ chính của ứng dụng

    // Phương thức khởi tạo, được gọi khi controller được khởi tạo
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Xử lý sự kiện khi nhấn chuột lên phần sidebar để kéo cửa sổ
        sideBar.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX(); // Lưu vị trí X của chuột
            y = mouseEvent.getSceneY(); // Lưu vị trí Y của chuột
        });

        // Xử lý sự kiện kéo cửa sổ khi kéo phần sidebar
        sideBar.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - x); // Di chuyển cửa sổ theo hướng X
            stage.setY(mouseEvent.getScreenY() - y); // Di chuyển cửa sổ theo hướng Y
        });
    }

    // Phương thức thiết lập cửa sổ chính (Stage)
    public void setStage(Stage stage){
        this.stage = stage;
    }

    // Phương thức đóng cửa sổ
    @FXML
    public void closeScene() {
        stage.close(); // Đóng cửa sổ hiện tại
    }

    // Phương thức xử lý sự kiện khi nhấn nút Đăng nhập
    @FXML
    void handleLogin() {
        // Kiểm tra tên đăng nhập có trống không
        if(adminNameField.getText().isEmpty()){
            AlertUtils.ShowAlert(AlertType.ERROR, "Ko để trống tên đăng nhập"); // Hiển thị thông báo lỗi
            return;
        }

        // Kiểm tra mật khẩu có trống không
        if(passwordField.getText().isEmpty()){
            AlertUtils.ShowAlert(AlertType.ERROR, "Ko để trống mật khẩu"); // Hiển thị thông báo lỗi
            return;
        }

        // Kiểm tra tên đăng nhập có đúng không
        if(!adminNameField.getText().equals(adminName)){
            AlertUtils.ShowAlert(AlertType.ERROR, "sai tên đăng nhập"); // Hiển thị thông báo lỗi
            return;
        }

        // Kiểm tra mật khẩu có đúng không
        if(!passwordField.getText().equals(password)){
            AlertUtils.ShowAlert(AlertType.ERROR, "sai mật khẩu"); // Hiển thị thông báo lỗi
            return;
        }

        // Nếu tất cả thông tin hợp lệ, chuyển sang màn hình chính
        SceneLoader.loadSceneAndGetController("mainWindow.fxml").SetController(this); // Tải màn hình chính và thiết lập controller
    }
}
