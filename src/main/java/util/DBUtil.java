package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
	private static final String URL = "jdbc:mysql://localhost:3306/usermarketdb";
	private static final String USER = "root";
	private static final String PASSWORD = "a010203";

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void close(AutoCloseable... resources) {
		for (AutoCloseable r : resources) {
			if (r != null) {
				try {
					r.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}