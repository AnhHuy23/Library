package ManageData;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Models.GoogleBook;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Lớp ManageGoogleBookData chịu trách nhiệm quản lý việc tìm kiếm sách từ API Google Books.
 * Sử dụng Singleton để đảm bảo chỉ có một đối tượng được khởi tạo trong ứng dụng.
 */
public class ManageGoogleBookData {
    private static ManageGoogleBookData instance; // Đối tượng Singleton
    private final String apiUrl = "https://www.googleapis.com/books/v1/volumes?q="; // URL API Google Books
    private OkHttpClient client; // Đối tượng để thực hiện các yêu cầu HTTP
    private Gson gson; // Đối tượng để xử lý JSON

    /**
     * Constructor private để ngăn việc khởi tạo đối tượng từ bên ngoài.
     * Khởi tạo các thành phần cần thiết như OkHttpClient và Gson.
     */
    private ManageGoogleBookData() {
        gson = new Gson();
        client = new OkHttpClient();
    }

    /**
     * Phương thức tìm kiếm sách theo tên thông qua API Google Books.
     *
     * @param bookName Tên sách cần tìm kiếm.
     * @return Danh sách các sách phù hợp với từ khóa.
     */
    public List<GoogleBook> searchBook(String bookName) {
        List<GoogleBook> booksJson = new ArrayList<>();
        String urlFind = apiUrl + bookName; // Xây dựng URL yêu cầu
        Request request = new Request.Builder().url(urlFind).build(); // Tạo yêu cầu HTTP

        try (Response response = client.newCall(request).execute()) { // Gửi yêu cầu và xử lý phản hồi
            if (response.isSuccessful()) {
                String responseData = response.body().string(); // Nhận dữ liệu JSON từ phản hồi
                JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class); // Chuyển đổi JSON sang đối tượng Java

                // Nhận danh sách các sách trong mảng "items"
                JsonArray items = jsonResponse.getAsJsonArray("items");
                if (items != null) {
                    for (int i = 0; i < items.size() && i < 10; i++) { // Lấy tối đa 10 kết quả
                        JsonObject book = items.get(i).getAsJsonObject(); // Lấy thông tin sách cụ thể

                        // Lấy thông tin chi tiết của sách
                        String id = getIdFromItem(book);
                        JsonObject volumeInfo = book.get("volumeInfo").getAsJsonObject();
                        String title = getDataFromVolumeInfor(volumeInfo, "title");
                        List<String> authors = getAuthorsfromVolumeInfo(volumeInfo);

                        // Nếu không có tác giả, đặt thông báo mặc định
                        if (authors == null) {
                            authors = new ArrayList<>();
                            authors.add("Can't determine authors");
                        }

                        String publisher = getDataFromVolumeInfor(volumeInfo, "publisher");
                        if( publisher==null ) {
                            publisher = "";
                        }
                        String description = getDataFromVolumeInfor(volumeInfo, "description");
                        String link = getDataFromVolumeInfor(volumeInfo, "infoLink");
                        // Tạo đối tượng GoogleBook và thêm vào danh sách kết quả
                        GoogleBook newBookJson = new GoogleBook(id, title, authors, publisher, description, link,id);
                        booksJson.add(newBookJson);
                    }
                }
            } else {
                // Hiển thị lỗi nếu phản hồi không thành công
                Alert errorAlert = new Alert(AlertType.ERROR, "Error: " + response.code());
                errorAlert.showAndWait();
                System.out.println("Error: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return booksJson;
    }

    /**
     * Phương thức lấy ID của một sách từ đối tượng JSON.
     *
     * @param item Đối tượng JSON đại diện cho sách.
     * @return ID của sách.
     */
    private String getIdFromItem(JsonObject item) {
        return item.getAsJsonObject().get("id").getAsString();
    }

    /**
     * Phương thức lấy dữ liệu chuỗi từ volumeInfo của sách.
     *
     * @param volumInfo Đối tượng JSON chứa thông tin sách.
     * @param dataName Tên trường dữ liệu cần lấy.
     * @return Chuỗi dữ liệu hoặc null nếu không tồn tại.
     */
    private String getDataFromVolumeInfor(JsonObject volumInfo, String dataName) {
        if (volumInfo.has(dataName)) {
            return volumInfo.get(dataName).getAsString();
        }
        return null;
    }

    /**
     * Phương thức lấy danh sách tác giả từ volumeInfo của sách.
     *
     * @param volumInfo Đối tượng JSON chứa thông tin sách.
     * @return Danh sách tác giả hoặc null nếu không có thông tin.
     */
    private List<String> getAuthorsfromVolumeInfo(JsonObject volumeInfo) {
        if (volumeInfo.has("authors")) {
            JsonArray authors = volumeInfo.get("authors").getAsJsonArray();
            List<String> authorList = new ArrayList<>();
            for (var jsonElement : authors) {
                authorList.add(jsonElement.getAsString());
            }
            return authorList;
        }
        return null;
    }
    /**
     * Phương thức lấy đối tượng Singleton của ManageGoogleBookData.
     *
     * @return Đối tượng Singleton của lớp này.
     */
    static public ManageGoogleBookData getInstance() {
        if (instance == null) {
            instance = new ManageGoogleBookData();
        }
        return instance;
    }
}
