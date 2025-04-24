package ui.newui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;

import ManageData.ManageBookData;
import Models.BookLibrary;
import Interface.*;
import Utils.AlertUtils;
import Interface.OnlyAlphabet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookController implements Initializable ,OnlyAlphabet, HasPreviousController<ManageBooksController> {
    @FXML
    private TextField id;         // Trường ID sách
    @FXML
    private TextField title;      // Trường tiêu đề sách
    @FXML
    private TextField author;     // Trường tác giả sách
    @FXML
    private TextField publisher;  // Trường nhà xuất bản
    @FXML
    private TextField quantity;   // Trường số lượng sách
    @FXML
    private JFXButton saveButton; // Nút lưu
    @FXML
    private JFXButton cancelButton; // Nút hủy

    private ManageBooksController manageBooksController; // Controller quản lý sách

    /**
     * Phương thức khởi tạo, thiết lập các sự kiện lắng nghe thay đổi trên các trường nhập liệu.
     * Khi có sự thay đổi trong trường, sẽ gọi hàm CheckField() để kiểm tra và điều chỉnh trạng thái của nút Lưu.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        saveButton.setDisable(true); // Khởi tạo nút lưu ở trạng thái bị vô hiệu hóa
        title.textProperty().addListener((ob, old, New) -> CheckField());
        id.textProperty().addListener((ob, old, New) -> CheckField());
        author.textProperty().addListener((ob, old, New) -> CheckField());
        publisher.textProperty().addListener((ob, old, New) -> CheckField());
        quantity.textProperty().addListener((ob, old, New) -> CheckField());
    }

    /**
     * Phương thức thêm sách vào hệ thống. Kiểm tra số lượng sách có phải là số nguyên không và thêm sách vào cơ sở dữ liệu.
     */
    @FXML
    private void addBook() {
        if(!isOnlyAlphabet(title.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "Title không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!isOnlyAlphabet(author.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "tên tác giả không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!isOnlyAlphabet(publisher.getText()))
        {
            AlertUtils.ShowAlert(AlertType.ERROR, "Nhà xuất bản không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!quantity.getText().trim().matches(".*\\d.*")){ // Kiểm tra quantity có chứa chữ
            AlertUtils.ShowAlert(AlertType.ERROR, "Số lượng sách là số nguyên dương");
            return;
        }
        int quantityValue = Integer.parseInt(quantity.getText().trim());
        if(quantityValue <1){
            AlertUtils.ShowAlert((AlertType.ERROR),"Số lượng sách là số nguyên dương" );
        }
        // Tạo đối tượng sách mới
        BookLibrary newBook = new BookLibrary(id.getText().trim(), title.getText().trim(), author.getText().trim(), publisher.getText().trim(), quantityValue, 0);

        // Thực hiện thêm sách trong một luồng riêng để không làm tắc nghẽn giao diện người dùng
        new Thread(() -> {
            if(ManageBookData.GetInstance().AddData(newBook)) {
                Platform.runLater(() -> {
                    AlertUtils.ShowAlert(AlertType.INFORMATION, "Success"); // Hiển thị thông báo thành công
                    manageBooksController.LoadData(manageBooksController.bookIdTextField.getText()); // Tải lại dữ liệu trong bảng sách
                });
            }
        }).start();
    }

    /**
     * Kiểm tra các trường nhập liệu có bị bỏ trống không. Nếu không có trường nào trống, kích hoạt nút Lưu.
     */
    private void CheckField() {
        if(title.getText().trim().isEmpty() || id.getText().trim().isEmpty() || author.getText().trim().isEmpty() ||
                publisher.getText().trim().isEmpty() || quantity.getText().trim().isEmpty()) {
            saveButton.setDisable(true); // Nếu có trường trống, vô hiệu hóa nút Lưu
        } else {
            saveButton.setDisable(false); // Nếu tất cả các trường đã được điền, kích hoạt nút Lưu
        }
    }

    /**
     * Phương thức hủy bỏ thao tác và đóng cửa sổ thêm sách.
     */
    @FXML
    private void cancelHandle() {
        // Đóng cửa sổ khi nhấn Cancel
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Thiết lập controller quản lý sách trước đó để có thể tải lại dữ liệu sau khi thêm sách mới.
     * @param previousController Controller trước đó
     */
    @Override
    public void SetController(ManageBooksController previousController) {
        this.manageBooksController = previousController;
    }
}
