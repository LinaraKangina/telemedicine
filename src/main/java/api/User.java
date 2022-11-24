package api;

import java.sql.*;
import java.util.Scanner;

public class User {

    private int userID;
    private String userLogin;
    private String userPassword;
    private int userRole;

    public User () {
    }
    public User (String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }
    public User (int userID, String userLogin, String userPassword) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    public User (int userID, String userLogin, String userPassword, int userRole) {
        this.userID = userID;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }

    public int getUserID() { return userID;}

    public void setUserID() { this.userID = userID; }

    public String getUserLogin() { return userLogin;}

    public void setUserLogin() { this.userPassword = userLogin; }

    public String getUserPassword() { return userPassword;}

    public void setUserPassword() { this.userPassword = userPassword; }

    public int getUserRole() { return userRole;}

    public void setUserRole() { this.userRole = userRole; }

    DBWoker worker = new DBWoker();
    Scanner scanner = new Scanner(System.in);

    public void logIn() throws SQLException {

        String query = "SELECT * FROM app.authorization_data WHERE (user_login = ? AND user_password = ?)";


        try {
            PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query);

            ResultSet resultSet;

                do {
                    System.out.print("Введите логин: ");
                    userLogin = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    userPassword = scanner.nextLine();
                    preparedStatement.setString(1, userLogin);
                    preparedStatement.setString(2, userPassword);
                    resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()) {
                        System.out.println("Пользователя с таким логином/паролем не существует. Попробуйте повторить авторизацию еще раз");
                    }else break;
                }
                while (!resultSet.next());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int userRoleDefinition () throws SQLException {

        String query = "SELECT user_role FROM app.authorization_data WHERE (user_login = ?)";

        try {
            PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query);

            ResultSet resultSet;

            userLogin = getUserLogin();
            preparedStatement.setString(1, userLogin);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                userRole = resultSet.getInt ("user_role");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return getUserRole();
    }

    public void newLogInData() {

        String query = "insert into app.authorization_data (user_login, user_password, user_role) VALUES (?, ?, ?)";

        System.out.print("Введите логин: ");
        String registrationLogin = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String registrationPassword = scanner.nextLine();

        try {
            PreparedStatement preparedStatement  = worker.getConnection().prepareStatement(query);
            preparedStatement.setString(1, registrationLogin);
            preparedStatement.setString(2, registrationPassword);
            preparedStatement.setInt(3, 4);

            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
