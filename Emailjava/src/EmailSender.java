import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;

public class EmailSender {
    private String email;
    private String password;

    public EmailSender(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void sendEmail(String to, String cc, String subject, String messageText, String attachmentPath) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        if (cc != null && !cc.isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }

        message.setSubject(subject);

        // Tạo một phần thân email
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(messageText);

        // Tạo một multipart để chứa cả nội dung và file đính kèm
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Xử lý file đính kèm nếu có
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentPath);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(new File(attachmentPath).getName());
            multipart.addBodyPart(attachmentBodyPart);
        }

        // Cài đặt nội dung email với multipart
        message.setContent(multipart);

        Transport.send(message);
    }
}
