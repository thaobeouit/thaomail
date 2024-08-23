import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import java.io.IOException;

public class EmailGUI extends JFrame {

    private JTextField toField;
    private JTextField ccField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JLabel attachmentLabel;
    private JButton sendButton;
    private JButton refreshButton;
    private JFileChooser fileChooser;
    private File selectedFile;
    private JTextArea emailListArea;
    private JComboBox<String> fontComboBox;
    private CustomPanel backgroundPanel;
    private JLabel senderLabel;
    private JButton replyButton;

    private String emailAccount;

    public EmailGUI(String email, String password) {
        this.emailAccount = email;

        // Thêm đoạn mã sau để thay đổi logo của ứng dụng
        try {
            Image logo = ImageIO.read(new File("C:/Users/thaok//Downloads/email.jpg")); // Đường dẫn tới tệp logo
            setIconImage(logo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EmailSender emailSender = new EmailSender(email, password);

        setTitle("Email Application - " + emailAccount);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Điều chỉnh kích thước của JFrame
        setBounds(100, 100, 1000, 700); // Tăng kích thước cửa sổ

        backgroundPanel = new CustomPanel();
        backgroundPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(null);

        JLabel fromLabel = new JLabel("From: " + emailAccount);
        fromLabel.setForeground(Color.WHITE);
        fromLabel.setBounds(10, 10, 400, 20);  // Tăng chiều rộng
        backgroundPanel.add(fromLabel);

        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(Color.WHITE);
        toLabel.setBounds(10, 50, 80, 20);
        backgroundPanel.add(toLabel);

        toField = new JTextField();
        toField.setBounds(100, 50, 800, 20);  // Tăng chiều rộng
        backgroundPanel.add(toField);
        toField.setColumns(10);

        JLabel ccLabel = new JLabel("CC:");
        ccLabel.setForeground(Color.WHITE);
        ccLabel.setBounds(10, 90, 80, 20);
        backgroundPanel.add(ccLabel);

        ccField = new JTextField();
        ccField.setBounds(100, 90, 800, 20);  // Tăng chiều rộng
        backgroundPanel.add(ccField);
        ccField.setColumns(10);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setForeground(Color.WHITE);
        subjectLabel.setBounds(10, 130, 80, 20);
        backgroundPanel.add(subjectLabel);

        subjectField = new JTextField();
        subjectField.setBounds(100, 130, 800, 20);  // Tăng chiều rộng
        backgroundPanel.add(subjectField);
        subjectField.setColumns(10);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(10, 170, 80, 20);
        backgroundPanel.add(messageLabel);

        messageArea = new JTextArea();
        messageArea.setBounds(100, 170, 800, 200);  // Tăng chiều rộng và chiều cao
        backgroundPanel.add(messageArea);

        JLabel attachmentLabelTitle = new JLabel("Attachment:");
        attachmentLabelTitle.setForeground(Color.WHITE);
        attachmentLabelTitle.setBounds(10, 390, 80, 20);
        backgroundPanel.add(attachmentLabelTitle);

        attachmentLabel = new JLabel("No file selected");
        attachmentLabel.setForeground(Color.WHITE);
        attachmentLabel.setBounds(100, 390, 500, 20);  // Tăng chiều rộng
        backgroundPanel.add(attachmentLabel);

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.setBounds(650, 390, 150, 20);  // Điều chỉnh vị trí để phù hợp với kích thước mới
        backgroundPanel.add(chooseFileButton);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    attachmentLabel.setText(selectedFile.getName());
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.setBounds(100, 430, 100, 30);
        backgroundPanel.add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String to = toField.getText();
                String cc = ccField.getText();

                // Kiểm tra xem trường "To" có bị trống không
                if (to.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a recipient email address!");
                    return;
                }

                // Kiểm tra định dạng địa chỉ email trong trường "To"
                if (!isValidEmailAddress(to)) {
                    JOptionPane.showMessageDialog(null, "Invalid email address format in the To field!");
                    return;
                }

                // Nếu có CC, kiểm tra định dạng địa chỉ email trong trường "CC"
                if (!cc.isEmpty() && !isValidEmailAddress(cc)) {
                    JOptionPane.showMessageDialog(null, "Invalid email address format in the CC field!");
                    return;
                }

                try {
                    String subject = subjectField.getText();
                    String message = messageArea.getText();
                    String attachmentPath = (selectedFile != null) ? selectedFile.getAbsolutePath() : null;
                    
                    // Gửi email
                    emailSender.sendEmail(to, cc, subject, message, attachmentPath);
                    JOptionPane.showMessageDialog(null, "Email sent successfully!");

                    // Lưu email vào bảng sent_emails
                    try (Connection conn = DatabaseManager.getConnection()) {
                        String insertQuery = "INSERT INTO sent_emails (email_account, recipient, cc, subject, message, attachment_path) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                        pstmt.setString(1, emailAccount);  // Tài khoản email hiện tại
                        pstmt.setString(2, to);
                        pstmt.setString(3, cc);
                        pstmt.setString(4, subject);
                        pstmt.setString(5, message);
                        pstmt.setString(6, attachmentPath);
                        pstmt.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to save sent email: " + ex.getMessage());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send email: " + ex.getMessage());
                }
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(220, 430, 100, 30);
        backgroundPanel.add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshEmails();  // Gọi phương thức refreshEmails với tài khoản email hiện tại
            }
        });

        replyButton = new JButton("Reply");
        replyButton.setBounds(340, 430, 100, 30);
        backgroundPanel.add(replyButton);

        replyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEmail = emailListArea.getSelectedText();
                if (selectedEmail != null && !selectedEmail.isEmpty()) {
                    String senderEmail = selectedEmail.split(" ")[0];
                    toField.setText(senderEmail);
                    ccField.setText("");
                    subjectField.setText("Re: " + subjectField.getText());
                    messageArea.setText("\n\n--- Original Message ---\n" + messageArea.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an email to reply.");
                }
            }
        });

        JLabel receivedEmailsLabel = new JLabel(emailAccount + "'s Received Emails");
        receivedEmailsLabel.setForeground(Color.WHITE);
        receivedEmailsLabel.setBounds(10, 470, 300, 20);
        backgroundPanel.add(receivedEmailsLabel);

        emailListArea = new JTextArea();
        emailListArea.setBounds(100, 500, 800, 150);  // Tăng chiều rộng và chiều cao
        backgroundPanel.add(emailListArea);

        // Thêm sự kiện chuột để chọn email và trả lời
        emailListArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Nhấp đúp chuột để trả lời
                    int pos = emailListArea.viewToModel(e.getPoint());  // Thay viewToModel2D bằng viewToModel
                    try {
                        int start = Utilities.getRowStart(emailListArea, pos);
                        int end = Utilities.getRowEnd(emailListArea, pos);
                        String selectedText = emailListArea.getText(start, end - start);

                        // Phân tích cú pháp để lấy thông tin người gửi và tiêu đề
                        String[] parts = selectedText.split(" - ");
                        if (parts.length >= 2) {
                            String senderEmail = parts[0].trim();
                            String subject = parts[1].trim();

                            // Điền vào các trường để trả lời
                            toField.setText(senderEmail);
                            subjectField.setText("Re: " + subject);
                            messageArea.setText("\n\n--- Original Message ---\n");

                            JOptionPane.showMessageDialog(null, "Replying to: " + senderEmail);
                        }
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        fontComboBox = new JComboBox<>(new String[]{"SansSerif", "Serif", "Monospaced"});
        fontComboBox.setBounds(800, 10, 100, 20);  // Điều chỉnh vị trí cho phù hợp với kích thước mới
        backgroundPanel.add(fontComboBox);

        fontComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFont = (String) fontComboBox.getSelectedItem();
                emailListArea.setFont(new Font(selectedFont, Font.PLAIN, 12));
            }
        });

        refreshEmails();  // Gọi hàm để tải email nhận được khi khởi động giao diện
    }

    private boolean isValidEmailAddress(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void refreshEmails() {
        emailListArea.setText("");

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT sender, subject FROM received_emails WHERE email_account = ? ORDER BY id DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, this.emailAccount);  // Chỉ tải email của tài khoản hiện tại
            ResultSet rs = pstmt.executeQuery();
            
            // Kiểm tra xem ResultSet có chứa dữ liệu không
            if (!rs.isBeforeFirst()) {
                emailListArea.append("No emails found for this account.\n");
            }
            
            while (rs.next()) {
                String emailData = rs.getString("sender") + " - " + rs.getString("subject");
                emailListArea.append(emailData + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
