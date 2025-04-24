package Models;

/**
 * Lớp EntityLibrary là lớp trừu tượng đại diện cho các thực thể có ID trong hệ thống.
 * Các lớp con sẽ kế thừa lớp này và thực hiện các hành động cụ thể đối với thực thể.
 */
public abstract class EntityLibrary {
    final private String id; // ID của thực thể (không thể thay đổi sau khi khởi tạo)

    /**
     * Constructor khởi tạo đối tượng EntityLibrary với ID.
     * @param id ID của thực thể.
     */
    protected EntityLibrary(String id) {
        this.id = id; // Gán giá trị cho ID
    }

    /**
     * Phương thức lấy ID của thực thể.
     * @return ID của thực thể dưới dạng chuỗi.
     */
    public String getId() {
        return id; // Trả về giá trị ID
    }
}
