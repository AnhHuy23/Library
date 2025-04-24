package Models;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * Lớp BookLibrary đại diện cho một cuốn sách trong thư viện, kế thừa từ lớp ItemLibrary.
 * Chứa các thông tin về sách như tác giả, nhà xuất bản, số lượng trong kho và số lượng sách đang mượn.
 */
public class BookLibrary extends ItemLibrary {
    private String author; // Tên tác giả của sách
    private String publisher; // Nhà xuất bản của sách
    private int quantityInStock; // Số lượng sách có trong kho
    private int releaseQuantity; // Số lượng sách đã được mượn
    private String imagePath;

    /**
     * Constructor khởi tạo đối tượng BookLibrary.
     * @param id ID của sách.
     * @param title Tiêu đề của sách.
     * @param author Tên tác giả của sách.
     * @param publisher Nhà xuất bản của sách.
     * @param quantityInStock Số lượng sách có trong kho.
     * @param releaseQuantity Số lượng sách đã được mượn.
     */
    public BookLibrary(String id, String title, String author, String publisher, int quantityInStock, int releaseQuantity) {
        super(id, title); // Gọi constructor của lớp cha ItemLibrary để khởi tạo ID và tiêu đề sách
        this.author = author;
        this.publisher = publisher;
        this.quantityInStock = quantityInStock;
        this.releaseQuantity = releaseQuantity;
        this.imagePath = "";
    }
    public BookLibrary(String id, String title, String author, String publisher, int quantityInStock, int releaseQuantity,String imagePath) {
        super(id, title); // Gọi constructor của lớp cha ItemLibrary để khởi tạo ID và tiêu đề sách
        this.author = author;
        this.publisher = publisher;
        this.quantityInStock = quantityInStock;
        this.releaseQuantity = releaseQuantity;
        this.imagePath=imagePath;
    }

    // Các phương thức getter để lấy thông tin chi tiết của sách
    public String getAuthor() {
        return author; // Lấy tên tác giả
    }

    public String getPublisher() {
        return publisher; // Lấy tên nhà xuất bản
    }

    public int getQuantityInStock() {
        return quantityInStock; // Lấy số lượng sách có trong kho
    }

    public int getReleaseQuantity() {
        return releaseQuantity; // Lấy số lượng sách đã được mượn
    }
    public String getImagePath()
    {
        return imagePath;
    }
    /**
     * Hiển thị chi tiết thông tin sách trong một cửa sổ dialog.
     */
    public void showBookDetail() {
        Dialog<Void> dialog = new Dialog<>(); // Tạo một dialog mới
        dialog.setTitle("Thông tin sách"); // Tiêu đề dialog

        // Tạo một GridPane để bố trí giao diện
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10)); // Thiết lập padding cho grid
        gridPane.setHgap(10); // Thiết lập khoảng cách ngang giữa các phần tử
        gridPane.setVgap(10); // Thiết lập khoảng cách dọc giữa các phần tử

        // Ảnh bìa sách
        ImageView imageView = new ImageView();
        try {
            // Thử tải ảnh bìa từ Google Books API dựa trên ID sách
            new Thread(()->{
                imageView.setImage(new Image("http://books.google.com/books/content?id=" + getImagePath() + "&printsec=frontcover&img=1&zoom=" + "5" + "&edge=curl&source=gbs_api"));
            }).start();
        } catch (Exception e) {
            // Nếu có lỗi, hiển thị ảnh placeholder
            imageView.setImage(new Image("placeholder.png"));
        }
        imageView.setFitHeight(150); // Thiết lập chiều cao ảnh
        imageView.setFitWidth(100); // Thiết lập chiều rộng ảnh
        gridPane.add(imageView, 0, 0, 1, 5); // Thêm ảnh vào GridPane

        // Thông tin sách
        gridPane.add(new Text("Tiêu đề:"), 1, 0); // Thêm text "Tiêu đề:"
        gridPane.add(new Text(getTitle()), 2, 0); // Hiển thị tiêu đề sách

        gridPane.add(new Text("Tác giả:"), 1, 1); // Thêm text "Tác giả:"
        gridPane.add(new Text(getAuthor()), 2, 1); // Hiển thị tên tác giả

        gridPane.add(new Text("Nhà xuất bản:"), 1, 2); // Thêm text "Nhà xuất bản:"
        gridPane.add(new Text(getPublisher()), 2, 2); // Hiển thị nhà xuất bản

        gridPane.add(new Text("Số lượng trong kho:"), 1, 3); // Thêm text "Số lượng trong kho:"
        gridPane.add(new Text(String.valueOf(getQuantityInStock())), 2, 3); // Hiển thị số lượng trong kho

        gridPane.add(new Text("Số lượng đang mượn:"), 1, 4); // Thêm text "Số lượng đang mượn:"
        gridPane.add(new Text(String.valueOf(getReleaseQuantity())), 2, 4); // Hiển thị số lượng đang mượn

        // Đưa giao diện GridPane vào dialog
        dialog.getDialogPane().setContent(gridPane);

        // Thêm nút đóng dialog
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        // Hiển thị dialog
        dialog.showAndWait();
    }
}
