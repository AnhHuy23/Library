package ManageData;

import java.sql.Connection;

import Models.EntityLibrary;

/**
 * Lớp trừu tượng ManageEntityData chịu trách nhiệm quản lý các thực thể cơ bản (entities) trong cơ sở dữ liệu.
 * Các lớp con cụ thể phải triển khai các phương thức trừu tượng của lớp này để đáp ứng nhu cầu quản lý cho từng loại thực thể.
 *
 * @param <EntityClass> kiểu dữ liệu của thực thể, phải kế thừa từ EntityLibrary.
 */
public abstract class ManageEntityData<EntityClass extends EntityLibrary> {
    protected final Object lock; // Đối tượng lock để đồng bộ hoá
    protected Connection connection; // Kết nối đến cơ sở dữ liệu

    /**
     * Constructor để khởi tạo các thuộc tính chung cho các lớp con.
     * Thiết lập đối tượng khóa (lock) và lấy kết nối cơ sở dữ liệu thông qua DatabaseHandler.
     */
    protected ManageEntityData() {
        lock = new Object(); // Khởi tạo đối tượng lock để dùng trong synchronized
        connection = DatabaseHandler.getInstance().getConnection(); // Lấy kết nối cơ sở dữ liệu
    }

    /**
     * Phương thức trừu tượng kiểm tra xem ID của thực thể có bị trùng lặp trong cơ sở dữ liệu không.
     *
     * @param id ID của thực thể cần kiểm tra.
     * @return true nếu ID đã tồn tại, ngược lại false.
     */
    protected abstract boolean checkIdDuplicate(String id);

    /**
     * Phương thức trừu tượng để thêm thực thể mới vào cơ sở dữ liệu.
     *
     * @param entity đối tượng thực thể cần thêm.
     * @return true nếu thêm thành công, ngược lại false.
     */
    public abstract boolean AddData(EntityClass entity);

    /**
     * Phương thức trừu tượng để lấy thông tin một thực thể từ cơ sở dữ liệu thông qua ID.
     *
     * @param id ID của thực thể cần lấy.
     * @return đối tượng thực thể nếu tìm thấy, ngược lại null.
     */
    public abstract EntityClass FetchData(String id);

    /**
     * Phương thức trừu tượng để xoá một thực thể khỏi cơ sở dữ liệu thông qua ID.
     *
     * @param id ID của thực thể cần xoá.
     * @return true nếu xoá thành công, ngược lại false.
     */
    public abstract boolean DeleteData(String id);
}
