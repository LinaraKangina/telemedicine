package api;

import java.sql.*;
import java.util.Scanner;

public class User {

    private static int userID;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public DBWoker getWorker() {
        return worker;
    }

    public void setWorker(DBWoker worker) {
        this.worker = worker;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userLogin='" + userLogin + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userRole=" + userRole +
                '}' + '\'';
    }

    DBWoker worker = new DBWoker();
    Scanner scanner = new Scanner(System.in);

    public void logIn() {

        String query = "SELECT * FROM app.authorization_data WHERE (user_login = ? AND user_password = ?)";


        try (PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query)){
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

    public int userRoleDefinition () {

        String query = "SELECT user_role FROM app.authorization_data WHERE (user_login = ?)";

        try (PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query)) {
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
}
