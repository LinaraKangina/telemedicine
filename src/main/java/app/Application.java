package app;

import api.User;
import classes.Doctor;
import classes.Laborant;
import classes.Patient;
import classes.Registrator;

import java.io.IOException;
import java.text.ParseException;

public class Application {

    public static void main(String[] args) throws ParseException, IOException {


        User user = new User();
        Registrator registrator = new Registrator();
        Laborant laborant = new Laborant();
        Patient patient = new Patient();
        Doctor doctor = new Doctor();

        System.out.println("Вход с систему");
        user.logIn(); // авторизация пользователя в систему


        //есть 4 вида пользователей. 1 - регистратор, 2 - лаборант, 3 - врач, 4 - пациент
        switch (user.userRoleDefinition()) {
            case (1):
                registrator.registerUser(); //
                registrator.generateUserLoginPassword();
                registrator.givesLoginPasswordToUser();
                System.out.println("\nНаправьте пациента к врачу");
                break;
            case (2):
                laborant.openPatientCard();
                laborant.inputTestResults();
                break;
            case (3):
                doctor.doctorsArm(user.userIDDefinition());
                break;
            case (4):
                patient.patientsArm(user.userIDDefinition());
                break;
            default:
                break;
        }



    }
}
