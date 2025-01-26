import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static final String SERVER_GIT_USERNAME = properties.getProperty("SERVER_GIT_USERNAME");
    public static final String SERVER_USERNAME = properties.getProperty("SERVER_USERNAME");
    public static final String SERVER_PASSWORD = properties.getProperty("SERVER_PASSWORD");
    public static final String HTTP_REPO_URL = properties.getProperty("HTTP_REPO_URL");
    public static final String SERVER_HOST = properties.getProperty("SERVER_HOST");
    public static final String REMOTE_SCRIPT_1 = properties.getProperty("REMOTE_SCRIPT_1");
    public static final int SERVER_SSH_PORT = Integer.parseInt(properties.getProperty("SERVER_SSH_PORT"));
}
