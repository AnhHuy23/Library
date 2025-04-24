package ui.newui;

import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import ManageData.DatabaseHandler;
import ManageData.ManageBookBorrowingData;
import ManageData.ManageBookData;
import ManageData.ManageUserData;
import Models.BookBorrowing;
import Models.BookLibrary;
import Models.User;
import Utils.MailSender;
import Interface.*;
import Utils.AlertUtils;
import Utils.SceneLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class MainWindowController implements Initializable, HasPreviousController<LoginController> {

    // Các trường giao diện
    @FXML
    public TextField bookIdTextField1;
    @FXML
    public Text bookText;
    @FXML
    public Text authorText;
    @FXML
    public Text quantityInStock;
    @FXML
    public TextField userIdTextField1;
    @FXML
    public Text userNameText;
    @FXML
    public Text mobileText;
    @FXML
    public Text emailText;
    @FXML
    public TextField userIdTextField2;
    @FXML
    private Button quitButton;
    @FXML
    private TableView<BookBorrowing> tableView;
    @FXML
    private TableColumn<BookBorrowing, String> serialNumber;
    @FXML
    private TableColumn<BookLibrary, String> bookId;
    @FXML
    private TableColumn<BookLibrary, String> title;
    @FXML
    private TableColumn<BookLibrary, String> author;
    @FXML
    private TableColumn<BookLibrary, LocalDate> borrowDate;
    @FXML
    private TableColumn<BookLibrary, LocalDate> dueDate;

    // Các biến lưu trữ thông tin về sách và người dùng
    private ObservableList<BookBorrowing> bookBorrowings;
    private BookLibrary fetchedBook;
    private User fetchedUser;
    private BookBorrowing selectedBookBorrowing;
    private LoginController loginController;

    // Khởi tạo giao diện và thiết lập các sự kiện
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        returnButton.setDisable(true); // Tắt nút trả sách khi chưa chọn sách

        // Thiết lập các cột trong bảng
        bookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        borrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        // Hiển thị số thứ tự cho mỗi dòng trong bảng
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

        // Lắng nghe sự kiện chọn sách trong bảng để kích hoạt hoặc tắt nút trả sách
        tableView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) { // Có sách được chọn
                returnButton.setDisable(false);
                selectedBookBorrowing = tableView.getSelectionModel().getSelectedItem();
            } else { // Không có sách nào được chọn
                returnButton.setDisable(true);
            }
        });

        // Lắng nghe sự kiện thay đổi ID người dùng và ID sách để tải dữ liệu tương ứng
        userIdTextField2.textProperty().addListener((ob, ov, nv) -> {
            LoadData(nv.trim());
        });
        bookIdTextField1.textProperty().addListener((ob, ov, nv) -> {
            RetrieveAndDisplayBookInfo(nv.trim());
        });
        userIdTextField1.textProperty().addListener((ob, ov, nv) -> {
            RetrieveAndDisplayUserInfo(nv.trim());
        });
    }

    // Tải danh sách sách đã mượn của người dùng
    private void LoadData(String userId) {
        new Thread(() -> {
            List<BookBorrowing> bookBorrowing = ManageBookBorrowingData.GetInstance().GetAllBookBorrowedByUser(userId);
            bookBorrowings = FXCollections.observableArrayList(bookBorrowing);
            tableView.setItems(bookBorrowings);
        }).start();
    }

    // Xử lý sự kiện tìm kiếm sách
    @FXML
    void handleSearchBook() {
        SceneLoader.LoadScene("searchBook.fxml");
    }

    // Xử lý sự kiện quản lý người dùng
    @FXML
    void handleManageUsers() {
        SceneLoader.LoadScene("manageUsers.fxml");
    }

    // Xử lý sự kiện quản lý sách
    @FXML
    void handleManageBooks() {
        SceneLoader.LoadScene("manageBooks.fxml");
    }

    // Xử lý sự kiện sách đã mượn
    @FXML
    void handleIsuedBooks(ActionEvent event) {
        SceneLoader.LoadScene("issuedBooks.fxml");
    }

    // Xử lý sự kiện mượn sách
    @FXML
    void handleIssue() {
        if (fetchedBook == null) {
            AlertUtils.ShowAlert(AlertType.ERROR, "Tìm sách phù hợp trước");
            return;
        }
        if (fetchedUser == null) {
            AlertUtils.ShowAlert(AlertType.ERROR, "Tìm người dùng phù hợp trước");
            return;
        }
        if (fetchedBook.getQuantityInStock() == 0) {
            AlertUtils.ShowAlert(AlertType.ERROR, "Trong kho đã hết sách");
            return;
        }
        if (fetchedBook != null && fetchedUser != null) {
            new Thread(() -> {
                BookBorrowing newBookBorrowing = new BookBorrowing(fetchedUser, fetchedBook, LocalDate.now(), LocalDate.now().plusDays(30));
                UpdateBookBorringAndBookStock(newBookBorrowing);
                MailSender.getInstance().sendEmail(fetchedUser.getEmail(), newBookBorrowing);
            }).start();
        }
    }

    // Xử lý sự kiện trả sách
    @FXML
    private JFXButton returnButton;

    @FXML
    private void returnHandle() {
        new Thread(() -> {
            DeleteBookBorrowingAndUpdateBookStock(selectedBookBorrowing);
        }).start();
    }

    // Xử lý sự kiện thoát ứng dụng
    @FXML
    private void handleQuit() {
        // Lấy Stage từ nút hiện tại
        Stage stage = (Stage) quitButton.getScene().getWindow();
        Stage newStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            LoginController controller = loader.getController();
            controller.setStage(newStage);
            scene.setFill(Color.TRANSPARENT);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.setScene(scene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.close();
    }

    // Cập nhật thông tin mượn sách và kho sách
    private void UpdateBookBorringAndBookStock(BookBorrowing newBookBorrowing) {
        try {
            DatabaseHandler.getInstance().getConnection().setAutoCommit(false);
            if (ManageBookBorrowingData.GetInstance().AddData(newBookBorrowing) && ManageBookData.GetInstance().UpdateBookStock(fetchedBook.getId(), 1)) {
                Platform.runLater(() -> {
                    AlertUtils.ShowAlert(AlertType.INFORMATION, "Sucess");
                    LoadData(userIdTextField2.getText());
                });
            }
        } catch (SQLException e) {
            try {
                DatabaseHandler.getInstance().getConnection().rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                DatabaseHandler.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Hiển thị thông tin sách khi nhập ID sách
    private void RetrieveAndDisplayBookInfo(String bookId) {
        new Thread(() -> {
            fetchedBook = ManageBookData.GetInstance().FetchData(bookId);
            if (fetchedBook == null) {
                bookText.setText("...");
                authorText.setText("...");
                quantityInStock.setText("...");
            } else {
                bookText.setText(fetchedBook.getTitle());
                authorText.setText(fetchedBook.getAuthor());
                quantityInStock.setText(String.valueOf(fetchedBook.getQuantityInStock()));
            }
        }).start();
    }

    // Hiển thị thông tin người dùng khi nhập ID người dùng
    private void RetrieveAndDisplayUserInfo(String userId) {
        new Thread(() -> {
            fetchedUser = ManageUserData.GetInstance().FetchData(userId);
            if (fetchedUser == null) {
                userNameText.setText("...");
                mobileText.setText("...");
                emailText.setText("...");
            } else {
                userNameText.setText(fetchedUser.getName());
                mobileText.setText(fetchedUser.getMobile());
                emailText.setText(fetchedUser.getEmail());
            }
        }).start();
    }

    // Xóa thông tin mượn sách và cập nhật kho sách
    private void DeleteBookBorrowingAndUpdateBookStock(BookBorrowing bookBorrowing) {
        try {
            DatabaseHandler.getInstance().getConnection().setAutoCommit(false);
            if (ManageBookBorrowingData.GetInstance().DeleteBookBorrowing(bookBorrowing) && ManageBookData.GetInstance().UpdateBookStock(bookBorrowing.getBookId(), -1)) {
                AlertUtils.ShowAlert(AlertType.INFORMATION, "Sucess");
                LoadData(userIdTextField2.getText());
            }
        } catch (SQLException e) {
            try {
                DatabaseHandler.getInstance().getConnection().rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                DatabaseHandler.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Thiết lập Controller trước đó (LoginController)
    @Override
    public void SetController(LoginController previousController) {
        loginController = previousController;
        loginController.closeScene();
    }
}
