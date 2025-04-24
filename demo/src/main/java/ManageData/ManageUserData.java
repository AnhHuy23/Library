package ManageData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.*;
import Utils.AlertUtils;
import javafx.scene.control.Alert.AlertType;

/**
 * Lớp ManageUserData chịu trách nhiệm quản lý dữ liệu người dùng (User) trong cơ sở dữ liệu.
 * Sử dụng Singleton để đảm bảo chỉ có một đối tượng duy nhất được khởi tạo.
 */
public class ManageUserData extends ManageEntityData<User> {
    private static ManageUserData instance; // Đối tượng Singleton

    /**
     * Constructor private để ngăn việc khởi tạo đối tượng từ bên ngoài.
     */
    private ManageUserData() {}

    /**
     * Phương thức lấy đối tượng Singleton của ManageUserData.
     * @return Đối tượng Singleton của lớp này.
     */
    public static ManageUserData GetInstance() {
        if (instance == null) {
            instance = new ManageUserData();
        }
        return instance;
    }

    /**
     * Kiểm tra xem ID có bị trùng lặp trong cơ sở dữ liệu hay không.
     * @param id ID cần kiểm tra.
     * @return true nếu ID đã tồn tại, ngược lại false.
     */
    @Override
    protected boolean checkIdDuplicate(String id) {
        String qu = "SELECT COUNT(*) FROM MEMBER WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(qu);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0; // Nếu số lượng lớn hơn 0, nghĩa là ID đã tồn tại
        } catch (Exception e) {
            System.out.println("Kiểm tra duplicate ID bị lỗi.");
            return false;
        }
    }

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     * @param user Đối tượng User cần thêm.
     * @return true nếu thêm thành công, ngược lại false.
     */
    @Override
    public boolean AddData(User user) {
        synchronized (lock) {
            if (checkIdDuplicate(user.getId())) {
                AlertUtils.ShowAlert(AlertType.ERROR, "ID đã tồn tại.");
                return false;
            }
            // Thực hiện câu lệnh SQL để thêm người dùng
            String qu = "INSERT INTO MEMBER (id, name, phoneNumber, email) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getMobile());
                preparedStatement.setString(4, user.getEmail());
                if (preparedStatement.executeUpdate() > 0) {
                    return true; // Thêm thành công
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi khi thêm người dùng.");
        return false;
    }

    /**
     * Lấy danh sách tất cả người dùng phù hợp với ID được cung cấp.
     * @param fetchId Chuỗi ID hoặc phần đầu của ID để tìm kiếm.
     * @return Danh sách người dùng phù hợp.
     */
    public List<User> GetAllData(String fetchId) {
        synchronized (lock) {
            List<User> users = new ArrayList<>();
            String qu = "SELECT * FROM MEMBER WHERE id LIKE '" + fetchId + "%'";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String name = resultSet.getString(2);
                    String phone = resultSet.getString(3);
                    String email = resultSet.getString(4);
                    User newUser = new User(id, name, phone, email); // Tạo đối tượng User
                    users.add(newUser); // Thêm vào danh sách kết quả
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Có lỗi khi lấy danh sách người dùng.");
            }
            return users;
        }
    }

    /**
     * Lấy thông tin chi tiết của một người dùng dựa trên ID.
     * @param id ID của người dùng cần tìm.
     * @return Đối tượng User nếu tìm thấy, ngược lại null.
     */
    @Override
    public User FetchData(String id) {
        synchronized (lock) {
            String qu = "SELECT * FROM MEMBER WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return new User(id, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                } else {
                    return null; // Không tìm thấy
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Cập nhật thông tin của một người dùng.
     * @param newUser Đối tượng User chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, ngược lại false.
     */
    public boolean updateUser(User newUser) {
        synchronized (lock) {
            String qu = "UPDATE MEMBER SET name = ?, phoneNumber = ?, email = ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, newUser.getName());
                preparedStatement.setString(2, newUser.getMobile());
                preparedStatement.setString(3, newUser.getEmail());
                preparedStatement.setString(4, newUser.getId());
                if (preparedStatement.executeUpdate() > 0) {
                    return true; // Cập nhật thành công
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Xóa một người dùng khỏi cơ sở dữ liệu dựa trên ID.
     * @param userId ID của người dùng cần xóa.
     * @return true nếu xóa thành công, ngược lại false.
     */
    @Override
    public boolean DeleteData(String userId) {
        synchronized (lock) {
            String qu = "DELETE FROM MEMBER WHERE id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qu);
                preparedStatement.setString(1, userId);
                int deletedRow = preparedStatement.executeUpdate();
                if (deletedRow > 0) {
                    return true; // Xóa thành công
                }
            } catch (SQLException e) {
                AlertUtils.ShowAlert(AlertType.ERROR, "Có lỗi SQL.");
                e.printStackTrace();
            }
            return false;
        }
    }
}
