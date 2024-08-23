import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailReceiver {

    private String email;
    private String password;

    public EmailReceiver(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void receiveEmails() {
        Properties properties = new Properties();
        properties.put("mail.pop3.host", "pop.gmail.com");
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");

        Session emailSession = Session.getDefaultInstance(properties);

        try {
            Store store = emailSession.getStore("pop3s");
            store.connect("pop.gmail.com", email, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String subject = mimeMessage.getSubject();
                    String sender = mimeMessage.getFrom()[0].toString();
                    String content = mimeMessage.getContent().toString();

                    // Lưu email nhận được vào cơ sở dữ liệu
                    DatabaseManager.saveReceivedEmail(email, sender, subject, content, null);
                }
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Tài khoản email và mật khẩu ứng dụng
        String email1 = "hvthao.ic.tn20@gmail.com";  // Địa chỉ email thứ nhất
        String password1 = "wtdi eznn bbzd jtfh";  // Mật khẩu ứng dụng cho tài khoản thứ nhất

        String email2 = "trungconta@gmail.com";  // Địa chỉ email thứ hai
        String password2 = "cxzr ahzt eygb sume";  // Mật khẩu ứng dụng cho tài khoản thứ hai

        // Nhận email từ tài khoản thứ nhất
        EmailReceiver receiver1 = new EmailReceiver(email1, password1);
        receiver1.receiveEmails();

        // Nhận email từ tài khoản thứ hai
        EmailReceiver receiver2 = new EmailReceiver(email2, password2);
        receiver2.receiveEmails();

        // Khởi chạy giao diện GUI cho mỗi tài khoản
        EmailGUI emailGUI1 = new EmailGUI(email1, password1);
        emailGUI1.setVisible(true);

        EmailGUI emailGUI2 = new EmailGUI(email2, password2);
        emailGUI2.setVisible(true);
    }
}
