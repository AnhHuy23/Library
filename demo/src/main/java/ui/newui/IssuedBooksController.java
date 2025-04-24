package ui.newui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import ManageData.ManageBookBorrowingData;
import Models.BookBorrowing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class IssuedBooksController implements Initializable {

    // Các thành phần giao diện
    public JFXButton quitButton;
    @FXML
    private TableColumn<BookBorrowing, String> author;
    @FXML
    private TableColumn<BookBorrowing, String> bookId;
    @FXML
    private TextField bookIdTextField;   // Trường tìm kiếm theo ID sách
    @FXML
    private TableColumn<BookBorrowing, String> issueTo;  // Cột "Người mượn"
    @FXML
    private TableColumn<BookBorrowing, String> serialNumber; // Cột số thứ tự
    @FXML
    private TableView<BookBorrowing> tableView; // Bảng hiển thị danh sách sách đã mượn
    @FXML
    private TableColumn<BookBorrowing, String> title; // Cột tên sách

    private ObservableList<BookBorrowing> bookBorrowings; // Dữ liệu sách mượn

    /**
     * Phương thức xử lý khi nhấn nút "Quit" (Thoát).
     * Đóng cửa sổ hiện tại.
     */
    @FXML
    void quitHandle(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close(); // Đóng cửa sổ khi nhấn nút Quit
    }

    /**
     * Phương thức khởi tạo, thiết lập các cột và dữ liệu ban đầu cho bảng.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Tải dữ liệu khi giao diện được khởi tạo
        LoadData(bookIdTextField.getText());

        // Thiết lập các cột trong bảng
        bookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        issueTo.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Cột số thứ tự: Hiển thị số thứ tự của từng dòng
        serialNumber.setCellValueFactory(param -> null);
        serialNumber.setCellFactory(param -> {
            return new TableCell<BookBorrowing, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getTableRow().getIndex() + 1));
                }
            };
        });

        // Lắng nghe sự thay đổi trong trường tìm kiếm bookIdTextField và cập nhật dữ liệu
        bookIdTextField.textProperty().addListener((ob, ov, nv) -> {
            LoadData(nv.trim());
        });
    }

    /**
     * Phương thức tải dữ liệu sách mượn từ cơ sở dữ liệu.
     * Dữ liệu được tải từ `ManageBookBorrowingData`.
     * @param userId ID người mượn để lọc các sách đã mượn của người đó.
     */
    private void LoadData(String userId) {
        new Thread(() -> {
            // Lấy danh sách sách đã mượn của người dùng từ cơ sở dữ liệu
            List<BookBorrowing> bookBorrowing = ManageBookBorrowingData.GetInstance().GetAllBookAreIssued(userId);
            // Chuyển danh sách thành ObservableList để hiển thị trên bảng
            bookBorrowings = FXCollections.observableArrayList(bookBorrowing);
            // Cập nhật bảng với dữ liệu mới
            tableView.setItems(bookBorrowings);
        }).start(); // Chạy trong thread mới để không làm gián đoạn giao diện người dùng
    }
}
