package ManageData;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Models.BookBorrowing;
import Utils.AlertUtils;
import javafx.scene.control.Alert.AlertType;

/**
 * Lớp ManageBookBorrowingData chịu trách nhiệm quản lý dữ liệu liên quan đến
 * việc mượn sách từ cơ sở dữ liệu.
 * Sử dụng singleton để đảm bảo chỉ có một instance duy nhất của lớp.
 */
public class ManageBookBorrowingData {
    private static ManageBookBorrowingData instance; // Biến lưu instance duy nhất
    private final Object lock; // Đối tượng dùng để đồng bộ hoá
    private Connection connection; // Kết nối cơ sở dữ liệu

    /**
     * Constructor private để ngăn chặn việc khởi tạo đối tượng từ bên ngoài.
     * Thiết lập kết nối với cơ sở dữ liệu và khởi tạo đối tượng đồng bộ.
     */
    private ManageBookBorrowingData() {
        connection = DatabaseHandler.getInstance().getConnection();
        lock = new Object();
    }

    /**
     * Phương thức static để lấy instance duy nhất của lớp.
     *
     * @return instance của ManageBookBorrowingData
     */
    public static ManageBookBorrowingData GetInstance() {
        if (instance == null) {
            instance = new ManageBookBorrowingData();
        }
        return instance;
    }

    /**
     * Thêm dữ liệu mượn sách vào bảng BOOKBORROWING.
     *
     * @param bookBorrowing đối tượng chứa thông tin mượn sách
     * @return true nếu thêm thành công, ngược lại trả về false
     */
    public boolean AddData(BookBorrowing bookBorrowing) {
        synchronized (lock) { // Đảm bảo an toàn trong môi trường đa luồng
            if (checkDuplicate(bookBorrowing.getUser().getId(), bookBorrowing.getItem().getId())) {
                AlertUtils.ShowAlert(AlertType.ERROR, "Thành viên này đã mượn sách này");
                return false;
            }
            String qu = "INSERT INTO BOOKBORROWING (memberId,bookId,borrowDay,dueDay) VALUES (?,?,?,?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, bookBorrowing.getUser().getId());
                preparedStatement.setString(2, bookBorrowing.getItem().getId());
                preparedStatement.setDate(3, Date.valueOf(bookBorrowing.getBorrowDate()));
                preparedStatement.setDate(4, Date.valueOf(bookBorrowing.getDueDate()));
                if (preparedStatement.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Thêm dữ liệu bị lỗi Database");
            }
            return false;
        }
    }

    /**
     * Kiểm tra xem sách đã được mượn bởi thành viên hay chưa.
     *
     * @param userId ID của thành viên
     * @param bookId ID của sách
     * @return true nếu sách đã được mượn, ngược lại trả về false
     */
    private boolean checkDuplicate(String userId, String bookId) {
        synchronized (lock) {
            String qu = "SELECT COUNT(*) FROM BOOKBORROWING WHERE memberId = ? AND bookId = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, bookId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt(1) > 0;
            } catch (Exception e) {
                System.out.println("Kiểm tra trùng lặp bookBorrowing bị lỗi");
                return false;
            }
        }
    }

    /**
     * Lấy danh sách tất cả sách đã được mượn bởi một thành viên.
     *
     * @param userId ID của thành viên
     * @return danh sách các đối tượng BookBorrowing
     */
    public List<BookBorrowing> GetAllBookBorrowedByUser(String userId) {
        synchronized (lock) {
            List<BookBorrowing> bookBorrowings = new ArrayList<>();
            String qu = "SELECT * FROM BOOKBORROWING WHERE memberId = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String memberId = resultSet.getString(1);
                    String bookId = resultSet.getString(2);
                    LocalDate borrowDay = resultSet.getDate(3).toLocalDate();
                    LocalDate dueDay = resultSet.getDate(4).toLocalDate();
                    BookBorrowing bookBorrowing = new BookBorrowing(
                            ManageUserData.GetInstance().FetchData(memberId),
                            ManageBookData.GetInstance().FetchData(bookId),
                            borrowDay,
                            dueDay
                    );
                    bookBorrowings.add(bookBorrowing);
                }
            } catch (SQLException e) {
                System.out.println("Lấy danh sách sách mượn bị lỗi");
                e.printStackTrace();
            }
            return bookBorrowings;
        }
    }

    /**
     * Xoá thông tin mượn sách khỏi bảng BOOKBORROWING.
     *
     * @param bookBorrowing đối tượng chứa thông tin mượn sách cần xoá
     * @return true nếu xoá thành công, ngược lại trả về false
     */
    public boolean DeleteBookBorrowing(BookBorrowing bookBorrowing) {
        synchronized (lock) {
            String qu = "DELETE FROM BOOKBORROWING WHERE memberId = ? AND bookId = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, bookBorrowing.getUser().getId());
                preparedStatement.setString(2, bookBorrowing.getItem().getId());
                if (preparedStatement.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException e) {
                AlertUtils.ShowAlert(AlertType.ERROR, "Lỗi SQL khi xoá BookBorrowing");
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Lấy số lượng sách mà một thành viên đang mượn.
     *
     * @param userId ID của thành viên
     * @return số lượng sách đang mượn hoặc -1 nếu có lỗi
     */
    public int GetBorrowQuantity(String userId) {
        synchronized (lock) {
            String qu = "SELECT COUNT(*) FROM BOOKBORROWING WHERE memberId = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Lỗi khi lấy số lượng sách mà người dùng đang mượn");
                return -1;
            }
        }
    }

    /**
     * Lấy danh sách tất cả sách đang được mượn, lọc theo ID sách.
     *
     * @param bookIdFilter bộ lọc ID sách
     * @return danh sách các đối tượng BookBorrowing
     */
    public List<BookBorrowing> GetAllBookAreIssued(String bookIdFilter) {
        List<BookBorrowing> bookBorrowings = new ArrayList<>();
        String qu = "SELECT * FROM BOOKBORROWING WHERE bookId LIKE '" + bookIdFilter + "%'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(qu);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String memberId = resultSet.getString(1);
                String bookId = resultSet.getString(2);
                LocalDate borrowDay = resultSet.getDate(3).toLocalDate();
                LocalDate dueDay = resultSet.getDate(4).toLocalDate();
                BookBorrowing bookBorrowing = new BookBorrowing(
                        ManageUserData.GetInstance().FetchData(memberId),
                        ManageBookData.GetInstance().FetchData(bookId),
                        borrowDay,
                        dueDay
                );
                bookBorrowings.add(bookBorrowing);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách sách đang được phát hành");
            e.printStackTrace();
        }
        return bookBorrowings;
    }
}
