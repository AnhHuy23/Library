package Models;

import java.time.LocalDate;

/**
 * Lớp Transaction đại diện cho một giao dịch mượn sách của người dùng.
 * Lớp này sử dụng kiểu tổng quát (generic) để xử lý các đối tượng thuộc loại ItemLibrary (ví dụ như BookLibrary hoặc GoogleBook).
 * Mỗi giao dịch lưu trữ thông tin về người dùng, sách và ngày mượn.
 */
public class Transaction<T extends ItemLibrary> {
    private User user; // Người mượn sách
    protected T item; // Sách được mượn (loại T là lớp con của ItemLibrary)
    LocalDate borrowDate; // Ngày mượn sách

    /**
     * Constructor khởi tạo một giao dịch mượn sách.
     * @param user Người mượn sách.
     * @param item Sách được mượn.
     * @param borrowDate Ngày mượn sách.
     */
    public Transaction(User user, T item, LocalDate borrowDate) {
        this.user = user; // Gán người mượn sách
        this.item = item; // Gán sách được mượn
        this.borrowDate = borrowDate; // Gán ngày mượn sách
    }

    /**
     * Phương thức trả về người mượn sách.
     * @return Người mượn sách.
     */
    public User getUser() {
        return user;
    }

    /**
     * Phương thức trả về ngày mượn sách.
     * @return Ngày mượn sách.
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Phương thức trả về sách được mượn.
     * @return Sách được mượn.
     */
    public T getItem() {
        return item;
    }
}
