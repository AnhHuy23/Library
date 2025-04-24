package ManageData;

import Models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javafx.application.Platform;

class ManageUserDataTest {

    private static boolean javaFXInitialized = false;

    private ManageUserData manageUserData;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    // Khởi tạo JavaFX chỉ một lần cho toàn bộ bộ kiểm thử
    @BeforeAll
    static void initJavaFX() {
        if (!javaFXInitialized) {
            Platform.startup(() -> {}); // Khởi tạo JavaFX platform nếu chưa được khởi tạo
            javaFXInitialized = true;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup các mock object cho mỗi phương thức kiểm thử
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        manageUserData = ManageUserData.GetInstance();
        manageUserData.connection = mockConnection;
    }

    @Test
    void testCheckIdDuplicate_True() throws Exception {
        // Giả lập kết quả truy vấn trả về giá trị đếm > 0
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Gọi phương thức cần kiểm tra
        boolean isDuplicate = manageUserData.checkIdDuplicate("existingId");

        // Kiểm tra kết quả
        assertTrue(isDuplicate);
    }

    @Test
    void testCheckIdDuplicate_False() throws Exception {
        // Giả lập kết quả truy vấn trả về giá trị đếm = 0
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);

        // Gọi phương thức cần kiểm tra
        boolean isDuplicate = manageUserData.checkIdDuplicate("newId");

        // Kiểm tra kết quả
        assertFalse(isDuplicate);
    }

    @Test
    void testAddData_Success() throws Exception {
        // Tạo spy cho đối tượng manageUserData để có thể mock các phương thức của nó
        ManageUserData spyManageUserData = spy(manageUserData);

        // Giả lập phương thức checkIdDuplicate trả về false (ID không trùng lặp)
        doReturn(false).when(spyManageUserData).checkIdDuplicate("newUserId");

        // Giả lập chuẩn bị statement và thực thi update thành công
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User newUser = new User("newUserId", "John Doe", "123456789", "john.doe@example.com");

        // Gọi phương thức cần kiểm tra
        boolean isAdded = spyManageUserData.AddData(newUser);

        // Kiểm tra kết quả
        assertTrue(isAdded);
    }

    @Test
    void testAddData_Failure() throws Exception {
        // Tạo spy cho đối tượng manageUserData để có thể mock các phương thức của nó
        ManageUserData spyManageUserData = spy(manageUserData);

        // Giả lập phương thức checkIdDuplicate trả về true (ID đã tồn tại)
        doReturn(true).when(spyManageUserData).checkIdDuplicate("existingUserId");

        User existingUser = new User("existingUserId", "Jane Doe", "987654321", "jane.doe@example.com");

        // Gọi phương thức cần kiểm tra
        boolean isAdded = spyManageUserData.AddData(existingUser);

        // Kiểm tra kết quả
        assertFalse(isAdded);
    }

    @Test
    void testGetAllData() throws Exception {
        // Giả lập kết quả trả về từ ResultSet
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString(1)).thenReturn("1", "2");
        when(mockResultSet.getString(2)).thenReturn("John Doe", "Jane Doe");
        when(mockResultSet.getString(3)).thenReturn("123456789", "987654321");
        when(mockResultSet.getString(4)).thenReturn("john@example.com", "jane@example.com");

        // Gọi phương thức cần kiểm tra
        var users = manageUserData.GetAllData("1");

        // Kiểm tra kết quả
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());
    }

    @Test
    void testFetchData_Found() throws Exception {
        // Giả lập tìm thấy dữ liệu
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(2)).thenReturn("John Doe");
        when(mockResultSet.getString(3)).thenReturn("123456789");
        when(mockResultSet.getString(4)).thenReturn("john@example.com");

        // Gọi phương thức cần kiểm tra
        User user = manageUserData.FetchData("1");

        // Kiểm tra kết quả
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testFetchData_NotFound() throws Exception {
        // Giả lập không tìm thấy dữ liệu
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Gọi phương thức cần kiểm tra
        User user = manageUserData.FetchData("nonExistentId");

        // Kiểm tra kết quả
        assertNull(user);
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Giả lập xóa thành công
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Gọi phương thức cần kiểm tra
        boolean isDeleted = manageUserData.DeleteData("1");

        // Kiểm tra kết quả
        assertTrue(isDeleted);
    }

    @Test
    void testDeleteUser_Failure() throws Exception {
        // Giả lập xóa thất bại
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Gọi phương thức cần kiểm tra
        boolean isDeleted = manageUserData.DeleteData("nonExistentId");

        // Kiểm tra kết quả
        assertFalse(isDeleted);
    }
}
