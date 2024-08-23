import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CustomPanel extends JPanel {
    private BufferedImage backgroundImage;

    public CustomPanel() {
        try {
            // Đường dẫn tới hình nền cũ của bạn, thay đổi nếu cần thiết
            backgroundImage = ImageIO.read(new File("C:/Users/thaok//Downloads/back.jpg")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Nếu không có hình nền, sử dụng màu nền mặc định
            g.setColor(new Color(34, 34, 34)); // Màu xám tối
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
