package DB_Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public Connection con;

    public static Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://localhost:3306/quyclb?useUnicode=true&characterEncoding=UTF-8";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối CSDL thành công!");

        } catch (ClassNotFoundException e) {
            System.err.println("LỖI: Chưa thêm thư viện MySQL Connector JAR vào dự án!");
        } catch (SQLException e) {
            System.err.println("LỖI: Không thể kết nối");
            e.printStackTrace();
        }
        return connection;
    }
    public void connect() {
        this.con = getConnection();
    }
    public static void closeConnection(Connection c) {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}