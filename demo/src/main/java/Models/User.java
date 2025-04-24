package Models;

/**
 * Lớp User đại diện cho một người dùng trong hệ thống.
 * Mỗi người dùng có thông tin như tên, số điện thoại và email.
 */
public class User extends EntityLibrary {
    protected String name;   // Tên người dùng
    protected String mobile; // Số điện thoại người dùng
    protected String email;  // Email người dùng

    /**
     * Constructor khởi tạo thông tin người dùng.
     * @param id ID của người dùng.
     * @param name Tên người dùng.
     * @param mobile Số điện thoại người dùng.
     * @param email Email người dùng.
     */
    public User(String id, String name, String mobile, String email) {
        super(id); // Gọi constructor của lớp cha (EntityLibrary) để khởi tạo ID
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    /**
     * Phương thức trả về tên người dùng.
     * @return Tên người dùng.
     */
    public String getName() {
        return name;
    }

    /**
     * Phương thức trả về số điện thoại của người dùng.
     * @return Số điện thoại người dùng.
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Phương thức trả về email của người dùng.
     * @return Email người dùng.
     */
    public String getEmail() {
        return email;
    }
}
