package Models;

/**
 * Lớp trừu tượng ItemLibrary kế thừa từ EntityLibrary.
 * Lớp này đại diện cho một đối tượng sách trong thư viện, với thông tin cơ bản như ID và tiêu đề.
 */
public abstract class ItemLibrary extends EntityLibrary {
    private String title; // Tiêu đề của sách

    /**
     * Constructor khởi tạo đối tượng ItemLibrary với ID và tiêu đề sách.
     * @param id ID của sách.
     * @param title Tiêu đề của sách.
     */
    protected ItemLibrary(String id, String title) {
        super(id); // Gọi constructor của lớp cha (EntityLibrary)
        this.title = title;
    }

    /**
     * Phương thức trả về tiêu đề của sách.
     * @return Tiêu đề của sách.
     */
    public String getTitle() {
        return title;
    }
}
