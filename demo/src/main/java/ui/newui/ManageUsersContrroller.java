package ui.newui;

import com.jfoenix.controls.JFXButton;
import ManageData.ManageBookBorrowingData;
import ManageData.ManageUserData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import Models.User;
import Utils.AlertUtils;
import Utils.SceneLoader;
import javafx.stage.Stage;

public class ManageUsersContrroller {
    // Nút thoát
    public JFXButton quitButton;

    // Các cột trong bảng hiển thị thông tin người dùng
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> serialNumber;

    @FXML
    private TableColumn<User,String> name;

    @FXML
    private TableColumn<User, String> idUser;

    @FXML
    private TableColumn<User, String> mobile;

    @FXML
    private TableColumn<User, String> email;

    // Trường tìm kiếm người dùng theo ID
    @FXML
    public TextField userIdTextField;

    // Biến lưu trữ người dùng được chọn
    private User SelectedUser;

    // Danh sách người dùng sẽ được hiển thị trong bảng
    private ObservableList<User> users = FXCollections.observableArrayList();

    // Phương thức khởi tạo controller
    @FXML
    public void initialize() {
        // Lắng nghe sự kiện chọn người dùng trong bảng
        tableView.getSelectionModel().selectedItemProperty().addListener((ob,ov,nv)->{
            SelectedUser = tableView.getSelectionModel().getSelectedItem(); // Lưu người dùng được chọn
        });

        // Lắng nghe sự kiện thay đổi giá trị trong ô tìm kiếm người dùng
        userIdTextField.textProperty().addListener((ob,ov,nv)->{
            LoadData(nv.trim()); // Tải dữ liệu người dùng khi nhập ID
        });

        // Thiết lập các cột trong bảng
        idUser.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        mobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Hiển thị số thứ tự cho các dòng trong bảng
        serialNumber.setCellValueFactory(param -> null);
        serialNumber.setCellFactory(param -> {
            return new javafx.scene.control.TableCell<User, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getTableRow().getIndex() + 1)); // Hiển thị số thứ tự cho mỗi dòng
                }
            };
        });

        // Tải dữ liệu người dùng khi bắt đầu
        LoadData(userIdTextField.getText());
    }

    // Tải dữ liệu người dùng từ cơ sở dữ liệu và hiển thị lên bảng
    public void LoadData(String id) {
        new Thread(()->{
            List<User> user = ManageUserData.GetInstance().GetAllData(id); // Lấy danh sách người dùng
            users = FXCollections.observableArrayList(user); // Chuyển danh sách thành ObservableList
            tableView.setItems(users); // Hiển thị danh sách lên bảng
        }).start();
    }

    // Xử lý sự kiện thêm người dùng
    @FXML
    void handleAddUser(ActionEvent event) {
        SceneLoader.loadSceneAndGetController("addUser.fxml").SetController(this); // Mở cửa sổ thêm người dùng
    }

    // Xử lý sự kiện sửa người dùng
    @FXML
    void handleEditUser(ActionEvent event) {
        SceneLoader.loadSceneAndGetController("editUser.fxml").SetController(this); // Mở cửa sổ chỉnh sửa người dùng
    }

    // Xử lý sự kiện xóa người dùng
    @FXML
    void handleClearUser() {
        if(SelectedUser != null){
            new Thread(()->{
                // Kiểm tra xem người dùng có sách chưa trả không
                int quantityBorrow = ManageBookBorrowingData.GetInstance().GetBorrowQuantity(SelectedUser.getId());
                if(quantityBorrow == -1){
                    AlertUtils.ShowAlert(AlertType.ERROR,"Có lỗi khi kiểm tra số lượng mượn trong Database");
                } else if(quantityBorrow > 0){
                    AlertUtils.ShowAlert(AlertType.ERROR,"Người dùng này có sách chưa trả");
                } else{
                    new Thread(()->{
                        // Xóa người dùng khỏi cơ sở dữ liệu và cập nhật lại bảng
                        if(!ManageUserData.GetInstance().DeleteData(SelectedUser.getId())){
                            AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi xoá người dùng trong Database");
                        } else{
                            AlertUtils.ShowAlert(AlertType.INFORMATION,"Success");
                            LoadData(userIdTextField.getText()); // Tải lại dữ liệu
                        }
                    }).start();
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
