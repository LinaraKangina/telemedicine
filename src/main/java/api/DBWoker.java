package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBWoker {
    private final String url = "jdbc:postgresql://localhost:5432/appfl";
    private final String userDb = "postgres";
    private final String passwordDb = "postgres";

    private Connection connection;

    // конструктор с теализацией подключения к бд
    public DBWoker(){
        try {
            connection = DriverManager.getConnection(url, userDb, passwordDb);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //геттер коннекшена
    public Connection getConnection(){
        return connection;
    }
}
