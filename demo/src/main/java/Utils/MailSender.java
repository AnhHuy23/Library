package Utils;

import java.util.Properties;

import Models.BookBorrowing;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.*;

public class MailSender {
    private static MailSender instance; // Đối tượng singleton duy nhất
    private final String host = "smtp.gmail.com"; // Máy chủ SMTP của Gmail
    private final String from = ""; // Địa chỉ email người gửi
    private final String password = ""; // Mật khẩu ứng dụng Gmail người gửi (không phải mật khẩu tài khoản Gmail chính)
    private Properties properties = new Properties(); // Thuộc tính kết nối máy chủ SMTP

    // Hàm khởi tạo MailSender, cấu hình các thuộc tính kết nối SMTP
    private MailSender() {
        properties.put("mail.smtp.host", host); // Tên máy chủ SMTP
        properties.put("mail.smtp.port", "587"); // Cổng SMTP (587 là cổng chuẩn cho STARTTLS)
        properties.put("mail.smtp.auth", "true"); // Cần xác thực người gửi
        properties.put("mail.smtp.starttls.enable", "true"); // Sử dụng STARTTLS để bảo mật kết nối
    }

    // Phương thức lấy instance của MailSender (Dùng Singleton Pattern)
    public static MailSender getInstance() {
        if (instance == null) {
            instance = new MailSender(); // Nếu chưa có instance, tạo mới
        }
        return instance;
    }

    // Phương thức kiểm tra khả năng đăng nhập vào email
    public boolean checkEmailLogin() {
        // Tạo session với xác thực thông tin tài khoản email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(); // Kết nối đến máy chủ SMTP
            return true; // Nếu kết nối thành công, trả về true
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Nếu có lỗi khi kết nối, trả về false
        }
    }

    // Phương thức gửi email thông báo về việc mượn sách
    public void sendEmail(String recipient, BookBorrowing bookBorrowing) {
        // Tạo session với xác thực thông tin tài khoản email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Tạo một message mới
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from)); // Địa chỉ người gửi
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient)); // Địa chỉ người nhận
            message.setSubject("Thông Báo Về Việc Mượn Sách"); // Tiêu đề thư
            message.setText("Chúng tôi là HHV Library xin gửi đến bạn thông báo liên quan đến sách mà bạn đã mượn.\n"
                    + "\n"
                    + "Tên sách: " + bookBorrowing.getTitle() // Thêm tên sách vào thư
                    + "\n"
                    + "Mã sách:" + bookBorrowing.getBookId() // Thêm mã sách vào thư
                    + "\n"
                    + "Ngày gia hạn: 1 Tháng kể từ hôm nay "
                    + "Chúng tôi nhắc nhở bạn vui lòng trả sách đúng hạn để đảm bảo quyền lợi mượn sách của các độc giả khác. Nếu bạn cần gia hạn thêm thời gian mượn, vui lòng liên hệ qua (số điện thoại/email của thư viện) trước ngày gia hạn cuối cùng.\n"
                    + "\n"
                    + "Trường hợp bạn đã trả sách, vui lòng bỏ qua thông báo này.\n"
                    + "\n"
                    + "Chúng tôi rất cảm ơn sự hợp tác của bạn để cùng xây dựng một môi trường học tập hiệu quả và lành mạnh. Nếu có bất kỳ thắc mắc nào, bạn đừng ngần ngại liên hệ với chúng tôi.\n"
                    + "\n"
                    + "Trân trọng,\n"
                    + "Lê Anh Huy \n"
                    + "Quản lý thư viện\n"
                    + "HHV Library\n"
                    + "0852444196\n"
                    + "ducvietnguyen04@gmail.com");

            // Gửi email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // In lỗi nếu có sự cố khi gửi email
        }
    }
}
