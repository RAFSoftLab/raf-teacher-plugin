public class Config {
    public static final String SERVER_GIT_USERNAME = System.getenv("SERVER_GIT_USERNAME");
    public static final String SERVER_USERNAME = System.getenv("SERVER_USERNAME");
    public static final String SERVER_PASSWORD = System.getenv("SERVER_PASSWORD");
    public static final String HTTP_REPO_URL = System.getenv("HTTP_REPO_URL");
    public static final String SERVER_HOST = System.getenv("SERVER_HOST");
    public static final String REMOTE_SCRIPT_1 = System.getenv("REMOTE_SCRIPT_1");
    public static final int SERVER_SSH_PORT = Integer.parseInt(System.getenv("SERVER_SSH_PORT"));
}