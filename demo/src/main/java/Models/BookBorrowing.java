package Models;

import java.time.LocalDate;

/**
 * Lớp BookBorrowing biểu diễn thông tin về việc mượn sách, kế thừa từ lớp Transaction.
 * @param <BookLibrary> Loại sách trong thư viện.
 */
public class BookBorrowing extends Transaction<BookLibrary> {
    private LocalDate dueDate; // Ngày trả sách dự kiến

    /**
     * Constructor khởi tạo đối tượng BookBorrowing.
     * @param user Đối tượng User mượn sách.
     * @param bookLibrary Đối tượng BookLibrary đại diện cho sách được mượn.
     * @param borrowDay Ngày mượn sách.
     * @param dueDate Ngày trả sách dự kiến.
     */
    public BookBorrowing(User user, BookLibrary bookLibrary, LocalDate borrowDay, LocalDate dueDate) {
        super(user, bookLibrary, borrowDay); // Gọi constructor của lớp cha Transaction
        this.dueDate = dueDate;
    }

    /**
     * Lấy ngày trả sách dự kiến.
     * @return Ngày trả sách dự kiến.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Lấy mã định danh (ID) của sách được mượn.
     * @return ID của sách.
     */
    public String getBookId() {
        return getItem().getId(); // Gọi phương thức getItem() từ lớp cha Transaction
    }

    /**
     * Lấy tiêu đề của sách được mượn.
     * @return Tiêu đề của sách.
     */
    public String getTitle() {
        return getItem().getTitle(); // Gọi phương thức getItem() từ lớp cha Transaction
    }

    /**
     * Lấy tên tác giả của sách được mượn.
     * @return Tên tác giả của sách.
     */
    public String getAuthor() {
        return getItem().getAuthor(); // Gọi phương thức getItem() từ lớp cha Transaction
    }

    /**
     * Lấy mã định danh (ID) của người dùng mượn sách.
     * @return ID của người dùng.
     */
    public String getUserId() {
        return getUser().getId(); // Gọi phương thức getUser() từ lớp cha Transaction
    }
}
