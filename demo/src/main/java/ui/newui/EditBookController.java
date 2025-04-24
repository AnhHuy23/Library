package ui.newui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import ManageData.ManageBookData;
import Models.BookLibrary;
import Utils.AlertUtils;
import Interface.OnlyAlphabet;
import Interface.HasPreviousController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EditBookController implements Initializable,OnlyAlphabet, HasPreviousController<ManageBooksController> {

    @FXML
    private TextField bookId;  // Trường nhập ID sách
    @FXML
    private Text titleText;    // Hiển thị tiêu đề sách
    @FXML
    private TextField titleField; // Trường nhập tiêu đề sách
    @FXML
    private Text authorText;   // Hiển thị tác giả sách
    @FXML
    private TextField authorField; // Trường nhập tác giả sách
    @FXML
    private Text publisherText;  // Hiển thị nhà xuất bản
    @FXML
    private TextField publisherField; // Trường nhập nhà xuất bản
    @FXML
    private Text quantityText;   // Hiển thị số lượng sách
    @FXML
    private TextField quantityField; // Trường nhập số lượng sách
    @FXML
    private JFXButton saveButton; // Nút lưu
    @FXML
    private JFXButton cancelButton; // Nút hủy

    private BookLibrary fetchedBook; // Đối tượng sách được lấy từ cơ sở dữ liệu
    private ManageBooksController manageBooksController; // Controller của màn hình quản lý sách

    /**
     * Phương thức khởi tạo, thiết lập các hành động khi người dùng thay đổi giá trị trong các trường nhập.
     * Kiểm tra các trường nhập để kích hoạt hoặc vô hiệu hóa nút lưu.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        saveButton.setDisable(true); // Mặc định nút lưu bị vô hiệu hóa

        // Lắng nghe sự thay đổi của bookId, khi thay đổi sẽ gọi phương thức RetrieveAndDisplayBookInfo
        bookId.textProperty().addListener((ob, ov, nv) -> {
            RetrieveAndDisplayBookInfo(nv.trim());
        });

        // Lắng nghe sự thay đổi của các trường nhập thông tin sách
        titleField.textProperty().addListener((ob, old, nv) -> CheckField());
        authorField.textProperty().addListener((ob, old, nv) -> CheckField());
        publisherField.textProperty().addListener((ob, old, nv) -> CheckField());
        quantityField.textProperty().addListener((ob, old, nv) -> CheckField());
    }

    /**
     * Phương thức xử lý khi nhấn nút "Save".
     * Cập nhật thông tin sách trong cơ sở dữ liệu.
     */
    @FXML
    private void saveHandle() {
        if(!isOnlyAlphabet(titleField.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "Title không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!isOnlyAlphabet(authorField.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "tên tác giả không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!isOnlyAlphabet(publisherField.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "Nhà xuất bản không đúng định dạng"); // Kiểm tra tên
            return;
        }
        // Lấy giá trị số lượng sách từ trường nhập
        int quantityValue = Integer.parseInt(quantityField.getText().trim());
        if(!quantityField.getText().trim().matches(".*\\d.*")){ // Kiểm tra quantity có chứa chữ
            AlertUtils.ShowAlert(AlertType.ERROR, "Số lượng sách chỉ có thể chứa số");
            return;
        }
        // Tạo đối tượng sách mới với các thông tin đã nhập
        BookLibrary newBook = new BookLibrary(bookId.getText(), titleField.getText(), authorField.getText(),
                publisherField.getText(), quantityValue, 0);
//        if(newBook.getReleaseQuantity()>0)
//        {
//            AlertUtils.ShowAlert(AlertType.ERROR,"Sách đang được mượn");
//            return;
//        }
        // Thực hiện cập nhật sách trong cơ sở dữ liệu trong một thread mới
        new Thread(() -> {
            if (ManageBookData.GetInstance().updateBook(newBook)) {
                // Nếu cập nhật thành công, tải lại dữ liệu và hiển thị thông báo thành công
                manageBooksController.LoadData(manageBooksController.bookIdTextField.getText());
                AlertUtils.ShowAlert(AlertType.INFORMATION, "Success");
            } else {
                // Nếu có lỗi khi cập nhật, hiển thị thông báo lỗi
                AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi chỉnh sửa");
            }
        }).start();
    }

    /**
     * Phương thức lấy thông tin sách từ cơ sở dữ liệu và hiển thị lên giao diện.
     * @param bookId ID của sách cần tìm kiếm.
     */
    private void RetrieveAndDisplayBookInfo(String bookId) {
        new Thread(() -> {
            // Lấy thông tin sách từ cơ sở dữ liệu
            fetchedBook = ManageBookData.GetInstance().FetchData(bookId);
            CheckField();  // Kiểm tra lại các trường nhập để cập nhật trạng thái của nút "Save"

            // Nếu không tìm thấy sách, hiển thị thông tin mặc định
            if (fetchedBook == null) {
                titleText.setText("...");
                authorText.setText("...");
                quantityText.setText("...");
                publisherText.setText("...");
            } else {
                // Nếu tìm thấy sách, hiển thị thông tin sách lên giao diện
                authorText.setText(fetchedBook.getAuthor());
                titleText.setText(fetchedBook.getTitle());
                publisherText.setText(fetchedBook.getPublisher());
                quantityText.setText(String.valueOf(fetchedBook.getQuantityInStock()));
            }
        }).start();
    }

    /**
     * Kiểm tra các trường nhập thông tin sách, nếu tất cả các trường hợp hợp lệ, bật nút lưu.
     */
    private void CheckField() {
        // Nếu chưa lấy được thông tin sách hoặc có trường nào chưa nhập, vô hiệu hóa nút lưu
        if (fetchedBook == null) {
            saveButton.setDisable(true);
            return;
        }
        if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty()
                || publisherField.getText().trim().isEmpty() || quantityField.getText().trim().isEmpty()) {
            saveButton.setDisable(true);  // Nếu có trường nào trống, nút lưu bị vô hiệu hóa
        } else {
            saveButton.setDisable(false); // Nếu tất cả các trường hợp hợp lệ, bật nút lưu
        }
    }

    /**
     * Phương thức xử lý khi nhấn nút "Cancel".
     * Đóng cửa sổ chỉnh sửa sách.
     */
    @FXML
    private void cancelHandle() {
        // Đóng cửa sổ khi nhấn nút Cancel
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Thiết lập controller của màn hình trước (ManageBooksController).
     * @param previousController Controller của màn hình quản lý sách.
     */
    @Override
    public void SetController(ManageBooksController previousController) {
        this.manageBooksController = previousController;
    }
}
