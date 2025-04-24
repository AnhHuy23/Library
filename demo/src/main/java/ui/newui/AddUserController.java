package ui.newui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;

import ManageData.ManageUserData;
import Models.User;
import Interface.HasPreviousController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import Utils.AlertUtils;
import Interface.EmailValidator;
import Interface.MobileValidator;
import Interface.OnlyAlphabet;
import javafx.stage.Stage;

public class AddUserController implements Initializable,MobileValidator,EmailValidator,OnlyAlphabet, HasPreviousController<ManageUsersContrroller> {

    @FXML
    private TextField id;           // Trường ID người dùng
    @FXML
    private TextField name;         // Trường tên người dùng
    @FXML
    private TextField mobile;  // Trường số điện thoại
    @FXML
    private TextField email;        // Trường email
    @FXML
    private JFXButton saveButton;   // Nút lưu
    @FXML
    private JFXButton cancelButton; // Nút hủy

    private ManageUsersContrroller manageUsersContrroller; // Controller quản lý người dùng

    /**
     * Phương thức khởi tạo, thiết lập các sự kiện lắng nghe thay đổi trên các trường nhập liệu.
     * Khi có sự thay đổi trong trường, sẽ gọi hàm CheckField() để kiểm tra và điều chỉnh trạng thái của nút Lưu.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        saveButton.setDisable(true); // Khởi tạo nút lưu ở trạng thái bị vô hiệu hóa
        name.textProperty().addListener((ob, old, New) -> CheckField());
        id.textProperty().addListener((ob, old, New) -> CheckField());
        mobile.textProperty().addListener((ob, old, New) -> CheckField());
        email.textProperty().addListener((ob, old, New) -> CheckField());
    }

    /**
     * Phương thức xử lý sự kiện khi nhấn nút Lưu.
     * Kiểm tra định dạng email trước khi lưu thông tin người dùng.
     */
    @FXML
    protected void SaveHandle() {
        if (!isValidGmail(email.getText())) {
            AlertUtils.ShowAlert(AlertType.ERROR, "Email không đúng định dạng"); // Kiểm tra email
            return;
        }
        if(!isOnlyAlphabet(name.getText())){
            AlertUtils.ShowAlert(AlertType.ERROR, "Tên không đúng định dạng"); // Kiểm tra tên
            return;
        }
        if(!isValidMobile(mobile.getText())){
            AlertUtils.ShowAlert(AlertType.ERROR, "Số điện thoại không đúng định dạng"); // Kiểm tra mobile
            return;
        }
        // Tạo đối tượng người dùng mới
        User newUser = new User(id.getText().trim(), name.getText().trim(), mobile.getText().trim(), email.getText().trim());

        // Thêm người dùng vào hệ thống và thông báo thành công
        if (ManageUserData.GetInstance().AddData(newUser)) {
            AlertUtils.ShowAlert(AlertType.INFORMATION, "Success");
            manageUsersContrroller.LoadData(manageUsersContrroller.userIdTextField.getText()); // Tải lại dữ liệu người dùng
        }
    }

    /**
     * Phương thức hủy bỏ thao tác và đóng cửa sổ thêm người dùng.
     */
    @FXML
    protected void Cancle() {

        // Đây có thể là một đoạn mã để xử lý hủy bỏ, nhưng hiện tại không có mã cụ thể.
    }

    /**
     * Kiểm tra các trường nhập liệu có bị bỏ trống không.
     * Nếu không có trường nào trống, kích hoạt nút Lưu.
     */
    private void CheckField() {
        // Kiểm tra nếu có trường nào trống thì vô hiệu hóa nút Lưu
        if (name.getText().trim().isEmpty() || id.getText().trim().isEmpty() || mobile.getText().trim().isEmpty() || email.getText().trim().isEmpty()) {
            saveButton.setDisable(true);
            return;
        }
        saveButton.setDisable(false); // Kích hoạt nút Lưu nếu tất cả trường đã được điền
    }

    /**
     * Phương thức hủy bỏ thao tác và đóng cửa sổ khi nhấn Cancel.
     */
    @FXML
    private void cancelHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close(); // Đóng cửa sổ khi nhấn nút Cancel
    }

    /**
     * Thiết lập controller quản lý người dùng trước đó để có thể tải lại dữ liệu sau khi thêm người dùng mới.
     * @param previousController Controller quản lý người dùng trước đó
     */
    @Override
    public void SetController(ManageUsersContrroller previousController) {
        this.manageUsersContrroller = previousController;
    }

}
