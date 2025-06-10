package project.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	private static final String URL = "jdbc:mysql://localhost:3306/GigaChat?serverTimezone=UTC";
	private static final String USER = "Server";
	private static final String PASSWORD = "8ufsh8eFW@#rVSyjju3#325";
	
	static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}