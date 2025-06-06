package project.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DB 
public class DBManager {
	private static final String URL = "jdbc:mysql://localhost:3306/GigaChat?serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "wkdwlsrb";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
