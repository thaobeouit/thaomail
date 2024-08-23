import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emaillogs?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void saveReceivedEmail(String emailAccount, String sender, String subject, String message, String attachment) throws SQLException {
        String query = "INSERT IGNORE INTO received_emails (email_account, sender, subject, message, attachment) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, emailAccount);
            pstmt.setString(2, sender);
            pstmt.setString(3, subject);
            pstmt.setString(4, message);
            pstmt.setString(5, attachment);
            pstmt.executeUpdate();
        }
    }
}
