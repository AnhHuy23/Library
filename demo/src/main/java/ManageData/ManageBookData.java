package ManageData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.BookLibrary;
import Utils.AlertUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Lớp ManageBookData chịu trách nhiệm quản lý dữ liệu sách trong cơ sở dữ liệu.
 * Sử dụng Singleton để đảm bảo chỉ có một instance của lớp này.
 */
public final class ManageBookData extends ManageEntityData<BookLibrary> {
    private static ManageBookData instance; // Singleton instance

    /**
     * Constructor private để ngăn chặn việc khởi tạo từ bên ngoài.
     */
    private ManageBookData() {}

    /**
     * Lấy instance duy nhất của lớp ManageBookData.
     *
     * @return instance của ManageBookData.
     */
    public static ManageBookData GetInstance() {
        if (instance == null) {
            instance = new ManageBookData();
        }
        return instance;
    }

    /**
     * Thêm sách mới vào cơ sở dữ liệu.
     *
     * @param bookLibrary đối tượng sách cần thêm.
     * @return true nếu thêm thành công, ngược lại false.
     */
    @Override
    public boolean AddData(BookLibrary bookLibrary) {
        synchronized (lock) { // Đồng bộ để đảm bảo thread-safe
            if (checkIdDuplicate(bookLibrary.getId())) {
                AlertUtils.ShowAlert(AlertType.ERROR, "ID đã tồn tại");
                return false;
            }
            // Câu lệnh SQL thêm sách mới
            String qu = "INSERT INTO BOOK (id, title, author, publisher, quantity_inStock,release_quantity,image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, bookLibrary.getId());
                preparedStatement.setString(2, bookLibrary.getTitle());
                preparedStatement.setString(3, bookLibrary.getAuthor());
                preparedStatement.setString(4, bookLibrary.getPublisher());
                preparedStatement.setInt(5, bookLibrary.getQuantityInStock());
                preparedStatement.setInt(6, bookLibrary.getReleaseQuantity());
                preparedStatement.setString(7,bookLibrary.getImagePath());
                if (preparedStatement.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi thêm sách");
        return false;
    }

    /**
     * Lấy danh sách tất cả các sách từ cơ sở dữ liệu, có thể áp dụng bộ lọc theo ID.
     *
     * @param fetchId giá trị để lọc theo ID sách (phần đầu ID).
     * @return danh sách các đối tượng BookLibrary.
     */
    public List<BookLibrary> GetAllData(String fetchId) {
        synchronized (lock) { // Đồng bộ để thread-safe
            List<BookLibrary> books = new ArrayList<>();
            String qu = "SELECT * FROM BOOK WHERE id LIKE '" + fetchId + "%'";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String title = resultSet.getString(2);
                    String author = resultSet.getString(3);
                    String publisher = resultSet.getString(4);
                    int quantityInStock = resultSet.getInt(5);
                    int releaseQuantity = resultSet.getInt(6);
                    String imagePath = resultSet.getString(7);
                    BookLibrary newBook = new BookLibrary(id, title, author, publisher, quantityInStock, releaseQuantity, imagePath);
                    books.add(newBook);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Có lỗi khi lấy danh sách sách");
            }
            return books;
        }
    }

    /**
     * Kiểm tra ID sách có bị trùng lặp trong cơ sở dữ liệu không.
     *
     * @param id ID sách cần kiểm tra.
     * @return true nếu ID đã tồn tại, ngược lại false.
     */
    @Override
    protected boolean checkIdDuplicate(String id) {
        String qu = "SELECT COUNT(*) FROM BOOK WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(qu);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (Exception e) {
            System.out.println("Kiểm tra trùng lặp ID sách bị lỗi");
            return true;
        }
    }

    /**
     * Lấy thông tin sách từ cơ sở dữ liệu theo ID.
     *
     * @param id ID của sách.
     * @return đối tượng BookLibrary nếu tìm thấy, ngược lại null.
     */
    @Override
    public BookLibrary FetchData(String id) {
        synchronized (lock) {
            String qu = "SELECT * FROM BOOK WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return new BookLibrary(
                            id,
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getInt(5),
                            resultSet.getInt(6)
                    );
                } else {
                    return null;
                }
            } catch (SQLException e) {
                Alert newAlert = new Alert(AlertType.ERROR, "Lỗi tìm kiếm sách");
                newAlert.showAndWait();
                return null;
            }
        }
    }

    /**
     * Cập nhật số lượng sách tồn kho và đã phát hành.
     *
     * @param bookId ID sách cần cập nhật.
     * @param changeQuantity số lượng thay đổi.
     * @return true nếu cập nhật thành công, ngược lại false.
     */
    public Boolean UpdateBookStock(String bookId, int changeQuantity) {
        synchronized (lock) {
            String qu = "UPDATE BOOK SET release_quantity = release_quantity + ?, quantity_inStock = quantity_inStock - ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setInt(1, changeQuantity);
                preparedStatement.setInt(2, changeQuantity);
                preparedStatement.setString(3, bookId);
                int rowUpdated = preparedStatement.executeUpdate();
                if (rowUpdated > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi cập nhật số lượng sách");
            return false;
        }
    }

    /**
     * Cập nhật thông tin sách trong cơ sở dữ liệu.
     *
     * @param newBook đối tượng chứa thông tin sách cần cập nhật.
     * @return true nếu cập nhật thành công, ngược lại false.
     */
    public boolean updateBook(BookLibrary newBook) {
        synchronized (lock) {
            String qu = "UPDATE BOOK SET title = ?, author = ?, publisher = ?, quantity_inStock = ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, newBook.getTitle());
                preparedStatement.setString(2, newBook.getAuthor());
                preparedStatement.setString(3, newBook.getPublisher());
                preparedStatement.setInt(4, newBook.getQuantityInStock());
                preparedStatement.setString(5, newBook.getId());
                if (preparedStatement.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Có lỗi khi cập nhật sách");
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Xoá sách khỏi cơ sở dữ liệu.
     *
     * @param bookId ID của sách cần xoá.
     * @return true nếu xoá thành công, ngược lại false.
     */
    @Override
    public boolean DeleteData(String bookId) {
        synchronized (lock) {
            String qu = "DELETE FROM BOOK WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, bookId);
                int deletedRow = preparedStatement.executeUpdate();
                if (deletedRow > 0) {
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("Có lỗi khi xoá sách");
                e.printStackTrace();
            }
            return false;
        }
    }
    public boolean isDuplicateGoogleBook(String imagePath)
    {
        String qu = "SELECT COUNT(*) FROM BOOK WHERE image_path = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(qu);
            preparedStatement.setString(1, imagePath);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1) > 0;
            }
            return true;
            
        } catch (Exception e) {
            System.out.println("Kiểm tra trùng lặp ID googleBook bị lỗi");
            return true;
        }
    }
}
