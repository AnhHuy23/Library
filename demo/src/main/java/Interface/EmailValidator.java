package Interface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface EmailValidator {
    default boolean isValidGmail(String email) {
        // Biểu thức chính quy kiểm tra địa chỉ Gmail
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches(); // Trả về true nếu chuỗi phù hợp với regex
    }
}
