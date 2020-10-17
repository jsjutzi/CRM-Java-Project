package Model;

public class User {
    private static int userId;
    private static String username;
    private static String password;

    public User() {
        userId = 0;
        username = null;
        password = null;

    }

    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        User.userId = userId;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }
}
