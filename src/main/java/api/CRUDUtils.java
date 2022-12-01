package api;

import classes.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CRUDUtils {
    static Patient patient = new Patient();


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

    //поиск карты пациента, возвращает ID пациента
    public static int getPatientCard (String query, String surname, String name, String patronymic, String telephone)  {

        int patientid = 0;
        try {
            Connection connection = DBWoker.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, surname);
            if (name != null) {preparedStatement.setString(2, name);}
            if (patronymic != null) {preparedStatement.setString(3, patronymic);}
            if (telephone != null) {preparedStatement.setString(4, telephone);}
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()){
                if (patientid == 0) {patientid = resultSet.getInt(1);}
                if (surname == null) { surname  = resultSet.getString(2);}
                if (name == null) { name  = resultSet.getString(3);}
                if (patronymic == null)  { patronymic = resultSet.getString(4);}
                Date birthdate = resultSet.getDate(5);
                if (telephone == null) telephone = resultSet.getString(6);


                System.out.println ("Открыта карточка пациента: " + surname + " " + name + " " + patronymic + ", Дата рождения " + birthdate + ", Телефон " + telephone +"\n");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return patientid;
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
