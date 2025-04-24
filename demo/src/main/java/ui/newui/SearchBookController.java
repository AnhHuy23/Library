package ui.newui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import ManageData.ManageGoogleBookData;
import Models.GoogleBook;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SearchBookController implements Initializable {
    // Khai báo các thành phần giao diện
    @FXML
    public ListView<GoogleBook> listView; // Danh sách hiển thị sách
    @FXML
    public TextField nameBookField; // Ô tìm kiếm tên sách
    public JFXButton quitButton; // Nút thoát

    private Timeline searchDelayTimeline = new Timeline(); // Biến để xử lý việc tìm kiếm với độ trễ
    ObservableList<GoogleBook> googleBooks = FXCollections.observableArrayList(); // Danh sách sách Google tìm được

    // Phương thức khởi tạo controller
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Cấu hình độ trễ tìm kiếm (500ms)
        searchDelayTimeline.setCycleCount(1);

        // Lắng nghe sự kiện thay đổi trong ô tìm kiếm
        nameBookField.textProperty().addListener((ob, ov, nv) -> {
            synchronized(googleBooks){
                googleBooks.clear(); // Xóa danh sách sách cũ
            }
            listView.getItems().clear(); // Xóa kết quả cũ trong ListView

            if (nv.trim().isEmpty()) {
                return; // Nếu không có tên sách tìm kiếm, không làm gì
            }

            // Hủy bỏ tìm kiếm cũ nếu đang trong quá trình tìm kiếm
            if (searchDelayTimeline != null && searchDelayTimeline.getStatus() == Timeline.Status.RUNNING) {
                searchDelayTimeline.stop(); // Dừng lại tìm kiếm cũ
            }

            // Tạo timeline mới để trì hoãn việc tìm kiếm
            searchDelayTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(500), e -> {
                new Thread(() -> {
                    synchronized (googleBooks) {
                        // Tìm kiếm sách từ Google và thêm vào danh sách
                        googleBooks.addAll(ManageGoogleBookData.getInstance().searchBook(nameBookField.getText().trim()));
                    }
                    listView.getItems().clear(); // Xóa các mục cũ trong ListView
                    if (googleBooks.isEmpty()) {
                        // Nếu không có kết quả, có thể xử lý thêm (Ví dụ: thông báo không tìm thấy sách)
                    } else {
                        // Hiển thị sách trong ListView
                        listView.getItems().setAll(googleBooks);
                    }
                }).start();
            }));
            searchDelayTimeline.play(); // Chạy timeline để trì hoãn tìm kiếm
        });

        // Thiết lập CellFactory cho ListView để hiển thị các sách
        listView.setCellFactory(param -> new ListCell<GoogleBook>() {
            private final ImageView imageView = new ImageView(); // Hiển thị ảnh bìa sách
            private final Label titleLabel = new Label(); // Hiển thị tiêu đề sách
            private final HBox hbox = new HBox(imageView, titleLabel); // Đặt ảnh và tiêu đề vào một HBox

            {
                imageView.setFitWidth(50); // Đặt kích thước cho ảnh bìa
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true); // Giữ tỷ lệ ảnh
                hbox.setSpacing(10); // Khoảng cách giữa ảnh và tiêu đề

                // Xử lý sự kiện khi người dùng click vào HBox
                hbox.setOnMouseClicked(event -> {
                    if(getItem() != null) {
                        getItem().showBookDetail(); // Hiển thị chi tiết sách khi click vào sách
                    }
                });
            }

            // Phương thức cập nhật giao diện khi mỗi dòng thay đổi
            @Override
            protected void updateItem(GoogleBook book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setGraphic(null); // Nếu dòng rỗng, không hiển thị gì
                } else {
                    new Thread(() -> {
                        imageView.setImage(new Image(book.getThumbnail())); // Tải ảnh bìa sách
                        Platform.runLater(() -> {
                            titleLabel.setText(book.getTitle()); // Hiển thị tiêu đề sách
                            setGraphic(hbox); // Cập nhật giao diện
                        });
                    }).start();
                }
            }
        });
    }

    // Xử lý sự kiện khi người dùng nhấn nút thoát
    @FXML
    void quitHandle(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow(); // Lấy cửa sổ hiện tại
        stage.close(); // Đóng cửa sổ
    }
}
