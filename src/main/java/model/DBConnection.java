package model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/market?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul";
    private static final String USER = "root";         // ← 본인 계정
    private static final String PASS = "test1234";     // ← 본인 비번

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("DB 연결 성공: " + conn);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
