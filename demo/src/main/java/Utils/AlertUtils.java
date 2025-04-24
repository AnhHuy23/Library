package Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertUtils {
    // Phương thức hiển thị thông báo (alert) với loại thông báo và nội dung
    static public void ShowAlert(AlertType alertType, String content) {
        // Kiểm tra nếu đang ở trong JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            // Tạo thông báo mới
            Alert newAlert = new Alert(alertType);
            newAlert.setContentText(content); // Cài đặt nội dung thông báo
            newAlert.showAndWait(); // Hiển thị thông báo và chờ người dùng đóng
        } else {
            // Nếu không phải JavaFX Application Thread (ví dụ, chạy trong thread khác)
            Platform.runLater(() -> {
                // Chạy đoạn mã trong JavaFX Application Thread
                Alert newAlert = new Alert(alertType);
                newAlert.setContentText(content); // Cài đặt nội dung thông báo
                newAlert.showAndWait(); // Hiển thị thông báo và chờ người dùng đóng
            });
        }
    }
}
