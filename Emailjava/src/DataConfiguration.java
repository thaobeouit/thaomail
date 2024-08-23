import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataConfiguration {
    private Properties properties = new Properties();

    public DataConfiguration() {
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
