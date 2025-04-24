package ManageData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseHandler là một lớp singleton chịu trách nhiệm quản lý kết nối
 * đến cơ sở dữ liệu và thiết lập các bảng cần thiết cho hệ thống thư viện.
 */
public class DatabaseHandler {
    // Biến lưu trữ instance duy nhất của lớp (Singleton)
    private static DatabaseHandler instance;

    // URL kết nối cơ sở dữ liệu
    private static final String URL = "jdbc:derby:database/library2;create=true";

    // Đối tượng Connection và Statement để thực hiện các thao tác cơ sở dữ liệu
    private static Connection connection = null;
    private static Statement statement = null;

    /**
     * Hàm khởi tạo (constructor) là private để đảm bảo chỉ có một instance được tạo.
     * Trong constructor, thiết lập kết nối và tạo các bảng cần thiết.
     */
    private DatabaseHandler() {
        CreateConnection();
        SetUpBookTable();
        SetUpMemberTable();
        SetUpBookBorrowingTable();
    }

    /**
     * Tạo kết nối đến cơ sở dữ liệu.
     * Sử dụng driver Derby (Apache Derby Embedded).
     */
    private void CreateConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // Nạp driver Derby
            connection = DriverManager.getConnection(URL); // Kết nối đến cơ sở dữ liệu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy đối tượng Connection hiện tại.
     *
     * @return Connection - đối tượng kết nối cơ sở dữ liệu
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Thiết lập bảng BOOK nếu chưa tồn tại.
     * Bảng này lưu trữ thông tin sách.
     */
    private void SetUpBookTable() {
        String tableName = "BOOK";
        try {
            statement = connection.createStatement();
            DatabaseMetaData dmb = connection.getMetaData();
            ResultSet tables = dmb.getTables(null, null, tableName, null);

            // Nếu bảng chưa tồn tại thì tạo mới
            if (!tables.next()) {
                statement.execute("CREATE TABLE " + tableName + " ("
                        + "id varchar(50) primary key,"          // ID sách (khóa chính)
                        + "title varchar(200), "               // Tiêu đề sách
                        + "author varchar(200), "              // Tác giả sách
                        + "publisher varchar(200), "           // Nhà xuất bản
                        + "quantity_inStock int, "             // Số lượng trong kho
                        + "release_quantity int DEFAULT 0, "     // Số lượng đã cho mượn
                        + "image_path varchar(200) DEFAULT ''" // Mã Id đến đường dẫn lấy ảnh
                        + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Không thể tạo bảng BOOK");
        }
    }

    /**
     * Thiết lập bảng MEMBER nếu chưa tồn tại.
     * Bảng này lưu trữ thông tin thành viên.
     */
    private void SetUpMemberTable() {
        String tableName = "MEMBER";
        try {
            statement = connection.createStatement();
            DatabaseMetaData dmb = connection.getMetaData();
            ResultSet tables = dmb.getTables(null, null, tableName, null);

            // Nếu bảng chưa tồn tại thì tạo mới
            if (!tables.next()) {
                statement.execute("CREATE TABLE " + tableName + " ("
                        + "id varchar(200) primary key,"      // ID thành viên (khóa chính)
                        + "name varchar(200), "             // Tên thành viên
                        + "phoneNumber varchar(200), "      // Số điện thoại
                        + "email varchar(200)"              // Email
                        + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Không thể tạo bảng MEMBER");
        }
    }

    /**
     * Thiết lập bảng BOOKBORROWING nếu chưa tồn tại.
     * Bảng này lưu trữ thông tin về sách đang được mượn.
     */
    private void SetUpBookBorrowingTable() {
        String tableName = "BOOKBORROWING";
        try {
            statement = connection.createStatement();
            DatabaseMetaData dmb = connection.getMetaData();
            ResultSet tables = dmb.getTables(null, null, tableName, null);

            // Nếu bảng chưa tồn tại thì tạo mới
            if (!tables.next()) {
                statement.execute("CREATE TABLE " + tableName + " ("
                        + "memberId varchar(200), "         // ID thành viên mượn sách
                        + "bookId varchar(200), "          // ID sách được mượn
                        + "borrowDay Date, "               // Ngày mượn
                        + "dueDay Date, "                  // Ngày trả dự kiến
                        + "PRIMARY KEY (memberId, bookId)" // Khóa chính kết hợp
                        + ")");
            }
        } catch (Exception e) {
            System.out.println("Không thể tạo bảng BOOKBORROWING");
        }
    }

    /**
     * Phương thức static để lấy instance duy nhất của DatabaseHandler.
     *
     * @return DatabaseHandler - instance duy nhất của lớp
     */
    static public DatabaseHandler getInstance() {
        // Nếu chưa có instance, khởi tạo một instance mới
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }
}
