package app;

import api.User;
import classes.Laborant;
import classes.Registrator;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class Application {
    public static void main(String[] args) throws SQLException, IOException, ParseException {

        User user = new User();
        Registrator registrator = new Registrator();
        Laborant laborant = new Laborant();

        System.out.println("Вход с систему");
        user.logIn(); // авторизация пользователя в систему

        //есть 4 вида пользователей. 1 - регистратор, 2 - лаборант, 3 - врач, 4 - пациент
        switch (user.userRoleDefinition()) {
            case (1):
                registrator.registerUser(); //
                registrator.generateUserLoginPassword();
                registrator.givesLoginPasswordToUser();
                break;
            case (2):
                laborant.openPatientCard();
                break;
            case (3):
                System.out.println("Это врач");
                break;
            case (4):
                System.out.println("Это пациент");
                break;
            default:
                break;
        }


    }
}
