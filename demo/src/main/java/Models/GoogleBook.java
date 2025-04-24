package Models;

import java.util.List;

import Utils.SceneLoader;

/**
 * Lớp GoogleBook kế thừa từ BookLibrary, đại diện cho một cuốn sách lấy từ Google Books API.
 * Lớp này mở rộng thông tin về sách bao gồm tác giả, mô tả, và liên kết đến sách trên Google Books.
 */
public class GoogleBook extends BookLibrary {

    private String description; // Mô tả về cuốn sách

    /**
     * Phương thức trả về mô tả của cuốn sách.
     * @return Mô tả của cuốn sách.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Phương thức trả về liên kết hình ảnh thu nhỏ (thumbnail) của sách với kích thước mặc định.
     * @return URL của ảnh thu nhỏ sách.
     */
    public String getThumbnail() {
        return "http://books.google.com/books/content?id=" + getImagePath() + "&printsec=frontcover&img=1&zoom=" + imageSize + "&edge=curl&source=gbs_api";
    }

    /**
     * Phương thức trả về liên kết hình ảnh thu nhỏ (thumbnail) của sách với kích thước tùy chỉnh.
     * @param size Kích thước của hình ảnh (zoom).
     * @return URL của ảnh thu nhỏ sách với kích thước chỉ định.
     */
    public String getThumbnail(int size) {
        return "http://books.google.com/books/content?id=" + getImagePath() + "&printsec=frontcover&img=1&zoom=" + size + "&edge=curl&source=gbs_api";
    }

    private String link; // Liên kết đến trang sách trên Google Books

    /**
     * Phương thức trả về liên kết đến trang sách trên Google Books.
     * @return Liên kết đến trang sách trên Google Books.
     */
    public String getLink() {
        return link;
    }

    private int imageSize = 5; // Kích thước hình ảnh mặc định (5)

    /**
     * Phương thức thiết lập kích thước hình ảnh (zoom).
     * @param size Kích thước hình ảnh (zoom).
     */
    public void setImageSize(int size) {
        imageSize = size;
    }

    /**
     * Constructor khởi tạo một cuốn sách GoogleBook với thông tin chi tiết.
     * @param id ID của cuốn sách.
     * @param title Tiêu đề của cuốn sách.
     * @param authors Danh sách tác giả của cuốn sách.
     * @param publisher Nhà xuất bản của cuốn sách.
     * @param description Mô tả về cuốn sách.
     * @param link Liên kết đến trang sách trên Google Books.
     */
    public GoogleBook(String id, String title, List<String> authors, String publisher, String description, String link,String imagePath) {
        super(id, title, String.join(", ", authors), publisher,1, 0,imagePath); // Gọi constructor của lớp cha (BookLibrary)
        this.description = description;
        this.link = link;
    }
    public GoogleBook(String ISBN, String title, String author, String publisher, String description, String link,String imagePath,int quantity) {
        super(ISBN, title, author, publisher,quantity, 0,imagePath); // Gọi constructor của lớp cha (BookLibrary)
        this.description = description;
        this.link = link;
    }
    /**
     * Phương thức hiển thị chi tiết sách trong một cửa sổ mới.
     * Lớp con sẽ thực hiện việc hiển thị chi tiết thông qua SceneLoader.
     */
    @Override
    public void showBookDetail() {
        SceneLoader.loadSceneAndGetController("bookDetail.fxml").SetController(this);
    }
}
