package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBWoker {
    private static final String url = "jdbc:postgresql://localhost:5432/appfl";
    private static final String userDb = "postgres";
    private static final String passwordDb = "postgres";

    //геттер коннекшена
    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userDb, passwordDb);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return connection;
    }
}
