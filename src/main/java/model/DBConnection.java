package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/market?serverTimezone=Asia/Seoul";
    private static final String USER = "root"; // MySQL 사용자명
    private static final String PASSWORD = "mysql1234"; // 비밀번호로 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //  MySQL 드라이버
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" DB 연결 성공: " + conn);
        } catch (ClassNotFoundException e) {
            System.out.println(" 드라이버 로드 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println(" DB 연결 실패: " + e.getMessage());
        }
        return conn;
    }
}
