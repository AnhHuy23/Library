package ui.newui;

import com.jfoenix.controls.JFXButton;
import ManageData.ManageBookData;
import Models.BookLibrary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import Utils.AlertUtils;
import Utils.SceneLoader;
import javafx.stage.Stage;

public class ManageBooksController {

    // Nút thoát
    public JFXButton quitButton;

    // Các cột trong bảng hiển thị thông tin sách
    @FXML
    private TableView<BookLibrary> tableView;

    @FXML
    private TableColumn<BookLibrary, String> serialNumber;

    @FXML
    private TableColumn<BookLibrary, String> title;

    @FXML
    private TableColumn<BookLibrary, String> bookId;

    @FXML
    private TableColumn<BookLibrary, String> author;

    @FXML
    private TableColumn<BookLibrary, String> publisher;

    @FXML
    private TableColumn<BookLibrary, Integer> quantityInStock;

    @FXML
    private TableColumn<BookLibrary,Integer> releaseQuantity;

    // Trường tìm kiếm sách theo ID
    @FXML
    public TextField bookIdTextField;

    // Biến lưu trữ sách được chọn
    private BookLibrary SelectedBook;

    // Danh sách sách sẽ được hiển thị trong bảng
    private ObservableList<BookLibrary> bookLibraries = FXCollections.observableArrayList();

    // Phương thức khởi tạo controller
    @FXML
    public void initialize() {
        // Lắng nghe sự kiện chọn sách trong bảng
        tableView.getSelectionModel().selectedItemProperty().addListener((ob,ov,nv)->{
            tableView.getSelectionModel().getSelectedItem().showBookDetail(); // Hiển thị chi tiết sách khi chọn
            SelectedBook = tableView.getSelectionModel().getSelectedItem(); // Lưu sách được chọn
        });

        // Lắng nghe sự kiện thay đổi giá trị trong ô tìm kiếm sách
        bookIdTextField.textProperty().addListener((ob,ov,nv)->{
            LoadData(nv.trim()); // Tải dữ liệu sách khi nhập ID
        });

        // Thiết lập các cột trong bảng
        bookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        releaseQuantity.setCellValueFactory(new PropertyValueFactory<>("releaseQuantity"));
        quantityInStock.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Hiển thị số thứ tự cho các dòng trong bảng
        serialNumber.setCellValueFactory(param -> null);
        serialNumber.setCellFactory(param -> {
            return new TableCell<BookLibrary, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getTableRow().getIndex() + 1)); // Hiển thị số thứ tự cho mỗi dòng
                }
            };
        });

        // Tải dữ liệu sách khi bắt đầu
        LoadData("");
    }

    // Tải dữ liệu sách từ cơ sở dữ liệu và hiển thị lên bảng
    public void LoadData(String id) {
        new Thread(()->{
            List<BookLibrary> books = ManageBookData.GetInstance().GetAllData(id); // Lấy danh sách sách
            bookLibraries = FXCollections.observableArrayList(books); // Chuyển danh sách thành ObservableList
            tableView.setItems(bookLibraries); // Hiển thị danh sách lên bảng
        }).start();
    }

    // Xử lý sự kiện thêm sách
    @FXML
    void handleAddBook() {
        SceneLoader.loadSceneAndGetController("addBook.fxml").SetController(this); // Mở cửa sổ thêm sách
    }

    // Xử lý sự kiện sửa sách
    @FXML
    void handleEditBook() {
        SceneLoader.loadSceneAndGetController("editBook.fxml").SetController(this); // Mở cửa sổ chỉnh sửa sách
    }

    // Xử lý sự kiện xóa sách
    @FXML
    void handleClearBook() {
        if(SelectedBook != null){
            // Kiểm tra xem sách có đang được phát hành không, nếu có thì không cho xóa
            if(SelectedBook.getReleaseQuantity() > 0){
                AlertUtils.ShowAlert(Alert.AlertType.ERROR,"Sách đang được phát hành thu hồi trước khi xoá");
                return;
            }
            new Thread(()->{
                // Xóa sách khỏi cơ sở dữ liệu và cập nhật lại bảng
                if(ManageBookData.GetInstance().DeleteData(SelectedBook.getId())){
                    AlertUtils.ShowAlert(Alert.AlertType.INFORMATION,"Success");
                    LoadData(bookIdTextField.getText()); // Tải lại dữ liệu
                }
            }).start();
        }
    }

    // Xử lý sự kiện thoát cửa sổ
    @FXML
    void quitHandle(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow(); // Lấy cửa sổ hiện tại
        stage.close(); // Đóng cửa sổ
    }
}
