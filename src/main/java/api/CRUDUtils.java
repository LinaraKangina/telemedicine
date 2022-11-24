package api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CRUDUtils {


    public static List<User> getUserData(String query)  {
        List<User> users = new ArrayList<>();

        try (Connection connection = DBWoker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int userID = resultSet.getInt("user_id");
                String userLogin = resultSet.getString("user_login");
                String userPassword = resultSet.getString("user_password");
                int userRole = resultSet.getInt("user_role");

                users.add(new User(userID, userLogin, userPassword, userRole));
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

//    public static List<User> saveUser (User user, String query){
//        List<User> users = new ArrayList<>();
//
//        try (Connection connection = DBWoker.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)){
//
//            preparedStatement.setInt(1, user.getUserRole());
//            preparedStatement.setString(2, user.getUserLogin());
//            preparedStatement.setString(3, user.getUserPassword());
//            preparedStatement.setInt(4, user.getUserRole());
//
//            preparedStatement.executeUpdate();
//
//            PreparedStatement allUsers = connection.prepareStatement("SELECT * FROM app.authorization_data");
//            ResultSet resultSet = allUsers.executeQuery();
//
//            while (resultSet.next()){
//                int userID = resultSet.getInt("user_id");
//                String userLogin = resultSet.getString("user_login");
//                String userPassword = resultSet.getString("user_password");
//                int userRole = resultSet.getInt("user_role");
//
//                users.add(new User(userID, userLogin, userPassword, userRole));
//            }
//        }catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return users;
//    }
}
