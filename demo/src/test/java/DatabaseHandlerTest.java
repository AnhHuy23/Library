package ManageData;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseHandlerTest {

    private static DatabaseHandler dbHandler;

    @BeforeAll
    static void setup() {
        // Khởi tạo DatabaseHandler (Singleton)
        dbHandler = DatabaseHandler.getInstance();
    }

    @Test
    @Order(1)
    void testConnectionIsEstablished() {
        Connection connection = dbHandler.getConnection();
        assertNotNull(connection, "Kết nối cơ sở dữ liệu không thành công");
    }

    @Test
    @Order(2)
    void testBookTableExists() {
        boolean tableExists = doesTableExist("BOOK");
        assertTrue(tableExists, "Bảng BOOK chưa được tạo");
    }

    @Test
    @Order(3)
    void testMemberTableExists() {
        boolean tableExists = doesTableExist("MEMBER");
        assertTrue(tableExists, "Bảng MEMBER chưa được tạo");
    }

    @Test
    @Order(4)
    void testBookBorrowingTableExists() {
        boolean tableExists = doesTableExist("BOOKBORROWING");
        assertTrue(tableExists, "Bảng BOOKBORROWING chưa được tạo");
    }

    private boolean doesTableExist(String tableName) {
        try {
            Connection connection = dbHandler.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName.toUpperCase(), null);
            return tables.next();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Không thể kiểm tra sự tồn tại của bảng: " + tableName);
            return false;
        }
    }
}
