package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static DataSource instance=null;
    private Connection connection;

private DataSource(){
    String userName = System.getenv("POSTGRE_USER_NAME_LOCAL");
    String psw = System.getenv("POSTGRE_PSW_LOCAL");
    String JDBC_DATABASE_URL = "jdbc:postgresql://" + System.getenv("JDBC_DATABASE_URI_LOCAL");
    try {
        connection = DriverManager.getConnection(JDBC_DATABASE_URL, userName, psw);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    public  static DataSource getInstance() throws SQLException {
        if(instance==null||instance.getConnection().isClosed()) {
             instance=new DataSource();
        }
        return instance;
    }

    public Connection getConnection(){
    return connection;
    }
}
