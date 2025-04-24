package ui.newui;

import java.awt.*;
import java.net.URI;

import com.jfoenix.controls.JFXButton;

import Interface.HasPreviousController;
import ManageData.ManageBookData;
import Models.GoogleBook;
import Utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;


public class BookDetailController implements HasPreviousController<GoogleBook> {

    @FXML
    private JFXButton addButton;  // Nút thêm sách vào thư viện

    @FXML
    private Text authorText;      // Hiển thị tác giả của sách

    @FXML
    private JFXButton cancelButton; // Nút hủy

    @FXML
    private TextArea descriptionTextArea; // Khu vực mô tả sách

    @FXML
    private Hyperlink hyperLink;  // Liên kết tới sách trên Google Books

    @FXML
    private ImageView imageView;  // Hình ảnh bìa sách

    @FXML
    private Text titleText;       // Tiêu đề của sách

    @FXML
    private TextField isbnTextField;
    @FXML 
    private TextField quantityTextField;

    private GoogleBook googleBook; // Đối tượng sách Google Book hiện tại

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "Add".
     * Thêm sách vào thư viện nếu thành công.
     */
    @FXML
    void addHandle() {
        if(isbnTextField.getText().trim().isEmpty() || !isbnTextField.getText().trim().matches(".*\\d.*")){
            AlertUtils.ShowAlert(AlertType.ERROR,"ISBN sai định dạng");
            return;
        }
        if(!quantityTextField.getText().trim().matches(".*\\d.*")){ // Kiểm tra quantity có chứa chữ
            AlertUtils.ShowAlert(AlertType.ERROR, "Số lượng sách chỉ có thể chứa số");
            return;
        }
        int quantityValue = Integer.parseInt(quantityTextField.getText().trim());
        if(quantityValue<1){
            AlertUtils.ShowAlert(AlertType.ERROR, "Số lượng sách lớn hơn 0");
            return;
        }
        if(ManageBookData.GetInstance().isDuplicateGoogleBook(googleBook.getImagePath())){
            AlertUtils.ShowAlert(AlertType.ERROR,"sách này đã có");
            return;
        }
        GoogleBook newGoogleBook = new GoogleBook(isbnTextField.getText(),googleBook.getTitle(),googleBook.getAuthor(), googleBook.getPublisher(), googleBook.getDescription(), googleBook.getLink(),googleBook.getImagePath(),quantityValue);
        if (ManageBookData.GetInstance().AddData(newGoogleBook)) {
            AlertUtils.ShowAlert(AlertType.INFORMATION, "Success");
        }
    }

    /**
     * Phương thức xử lý sự kiện khi nhấn nút "Cancel".
     * // Xử lý sự kiện khi người dùng nhấn nút thoát.
     */
    @FXML
    void cancelHandle() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Thiết lập thông tin chi tiết sách từ đối tượng GoogleBook.
     * Gồm thông tin tác giả, tiêu đề, mô tả, hình ảnh bìa và liên kết.
     * @param googleBook Đối tượng sách GoogleBook cần hiển thị thông tin.
     */
    @Override
    public void SetController(GoogleBook googleBook) {
        this.googleBook = googleBook;

        // Hiển thị tác giả sách, nếu không có thông tin tác giả, hiển thị thông báo mặc định
        if (googleBook.getAuthor()!= null) {
            authorText.setText(googleBook.getAuthor());  // Nối tên tác giả bằng dấu phẩy
        } else {
            authorText.setText("Can't determine authors");  // Nếu không có thông tin tác giả
        }
        // Hiện thị tiêu đề sách
        titleText.setText(googleBook.getTitle());
        // Hiển thị hình ảnh bìa sách
        imageView.setImage(new Image(googleBook.getThumbnail()));

        // Hiển thị mô tả sách
        descriptionTextArea.setText(googleBook.getDescription());
        descriptionTextArea.setEditable(false);  // Không cho phép chỉnh sửa mô tả
        descriptionTextArea.setWrapText(true);   // Tự động xuống dòng khi văn bản quá dài
        descriptionTextArea.setPrefRowCount(5);  // Đặt số dòng hiển thị mặc định cho mô tả

        // Hiển thị liên kết đến trang sách trên Google Books
        hyperLink.setText(googleBook.getLink());

        // Khi nhấn vào liên kết, mở trang web sách trên Google Books
        hyperLink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(googleBook.getLink()));  // Mở trang web trong trình duyệt
            } catch (Exception e) {
                // Nếu không thể mở được liên kết, hiển thị thông báo lỗi
                AlertUtils.ShowAlert(AlertType.ERROR, "Can't open this link");
            }
        });
    }
}
