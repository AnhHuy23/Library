package ui.newui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import ManageData.ManageUserData;
import Utils.AlertUtils;
import Interface.EmailValidator;
import Interface.MobileValidator;
import Interface.OnlyAlphabet;
import Interface.HasPreviousController;
import Models.User;
import javafx.stage.Stage;

public class EditUserController implements Initializable,MobileValidator,EmailValidator,OnlyAlphabet, HasPreviousController<ManageUsersContrroller> {

    // Các trường nhập liệu cho thông tin người dùng
    @FXML
    private TextField userIdField;    // Trường nhập ID người dùng
    @FXML
    private TextField nameField;      // Trường nhập tên người dùng
    @FXML
    private TextField mobileField;    // Trường nhập số điện thoại
    @FXML
    private TextField emailField;     // Trường nhập email

    // Các trường để hiển thị thông tin người dùng
    @FXML
    private Text nameText;            // Hiển thị tên người dùng
    @FXML
    private Text mobileText;          // Hiển thị số điện thoại người dùng
    @FXML
    private Text emailText;           // Hiển thị email người dùng

    // Các nút hành động
    @FXML
    private JFXButton saveButton;     // Nút lưu
    @FXML
    private JFXButton cancelButton;   // Nút hủy

    private User fetchedUser;         // Đối tượng người dùng được lấy từ cơ sở dữ liệu
    private ManageUsersContrroller manageUsersContrroller; // Controller của màn hình quản lý người dùng

    /**
     * Phương thức xử lý khi nhấn nút "Save".
     * Cập nhật thông tin người dùng trong cơ sở dữ liệu.
     */
    @FXML
    private void saveHandle() {
        if (!isValidGmail(emailField.getText())) {
            AlertUtils.ShowAlert(AlertType.ERROR, "Email không đúng định dạng"); // Kiểm tra email
            return;
        }
        if(!isOnlyAlphabet(nameField.getText())){
            AlertUtils.ShowAlert(AlertType.ERROR, "Tên không đúng định dạng"); // Kiểm tra email
            return;
        }
        if(!isValidMobile(mobileField.getText())){
            AlertUtils.ShowAlert(AlertType.ERROR, "Số điện thoại không đúng định dạng"); // Kiểm tra mobile
            return;
        }
        // Tạo đối tượng người dùng mới từ các trường nhập liệu
        User newUser = new User(userIdField.getText(), nameField.getText(), mobileField.getText(), emailField.getText());

        // Cập nhật thông tin người dùng trong cơ sở dữ liệu trên một thread mới
        new Thread(() -> {
            if (ManageUserData.GetInstance().updateUser(newUser)) {
                // Nếu cập nhật thành công, tải lại dữ liệu và hiển thị thông báo thành công
                manageUsersContrroller.LoadData(manageUsersContrroller.userIdTextField.getText());
                Platform.runLater(() -> {
                    AlertUtils.ShowAlert(AlertType.INFORMATION, "Success");
                });
            } else {
                // Nếu có lỗi khi cập nhật, hiển thị thông báo lỗi
                Platform.runLater(() -> {
                    AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi chỉnh sửa");
                });
            }
        }).start();
    }

    /**
     * Phương thức xử lý khi nhấn nút "Cancel".
     * Đóng cửa sổ chỉnh sửa người dùng.
     */
    @FXML
    private void cancelHandle() {
        // Đóng cửa sổ khi nhấn nút Cancel
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Thiết lập controller của màn hình trước (ManageUsersContrroller).
     * @param previousController Controller của màn hình quản lý người dùng.
     */
    @Override
    public void SetController(ManageUsersContrroller previousController) {
        this.manageUsersContrroller = previousController;
    }

    /**
     * Phương thức khởi tạo, thiết lập các hành động khi người dùng thay đổi giá trị trong các trường nhập.
     * Kiểm tra các trường nhập để kích hoạt hoặc vô hiệu hóa nút lưu.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        saveButton.setDisable(true); // Mặc định nút lưu bị vô hiệu hóa

        // Lắng nghe sự thay đổi của userIdField, khi thay đổi sẽ gọi phương thức RetrieveAndDisplayUserInfo
        userIdField.textProperty().addListener((ob, ov, nv) -> {
            RetrieveAndDisplayUserInfo(userIdField.getText());
        });

        // Lắng nghe sự thay đổi của các trường nhập thông tin người dùng
        nameField.textProperty().addListener((ob, old, nv) -> CheckField());
        emailField.textProperty().addListener((ob, old, nv) -> CheckField());
        mobileField.textProperty().addListener((ob, old, nv) -> CheckField());
    }

    /**
     * Kiểm tra các trường nhập thông tin người dùng, nếu tất cả các trường hợp hợp lệ, bật nút lưu.
     */
    private void CheckField() {
        // Nếu chưa lấy được thông tin người dùng hoặc có trường nào chưa nhập, vô hiệu hóa nút lưu
        if (fetchedUser == null) {
            saveButton.setDisable(true);
            return;
        }
        if (nameField.getText().trim().isEmpty() || mobileField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            saveButton.setDisable(true); // Nếu có trường nào trống, nút lưu bị vô hiệu hóa
        } else {
            saveButton.setDisable(false); // Nếu tất cả các trường hợp hợp lệ, bật nút lưu
        }
    }

    /**
     * Phương thức lấy thông tin người dùng từ cơ sở dữ liệu và hiển thị lên giao diện.
     * @param userId ID của người dùng cần tìm kiếm.
     */
    private void RetrieveAndDisplayUserInfo(String userId) {
        // Lấy thông tin người dùng từ cơ sở dữ liệu
        fetchedUser = ManageUserData.GetInstance().FetchData(userId);
        CheckField(); // Kiểm tra lại các trường nhập để cập nhật trạng thái của nút "Save"

        // Nếu không tìm thấy người dùng, hiển thị thông tin mặc định
        if (fetchedUser == null) {
            nameText.setText("...");
            mobileText.setText("...");
            emailText.setText("...");
        } else {
            // Nếu tìm thấy người dùng, hiển thị thông tin người dùng lên giao diện
            nameText.setText(fetchedUser.getName());
            mobileText.setText(fetchedUser.getMobile());
            emailText.setText(fetchedUser.getEmail());
        }
    }
}
