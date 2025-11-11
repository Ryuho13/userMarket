package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    private static final String URL  = "jdbc:mysql://localhost:3306/usermarketdb";
    private static final String USER = "root";
    private static final String PASS = "qwe123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ MySQL Driver load fail", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("❌ DB connection fail", e);
        }
    }
}
