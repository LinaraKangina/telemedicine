package classes;

import api.DBWorker;
import api.User;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Registrator{

    DBWorker worker;
    Scanner scanner = new Scanner(System.in);

    User user = new User();
    Patient patient = new Patient();

    //Заведение пациента (опрос, запись в БД)
    public void registerUser () throws ParseException {

        System.out.println("\nВведите данные пациента\n");
        System.out.print("Фамилия: ");
        patient.setSurname(scanner.nextLine());
        System.out.print("Имя: ");
        patient.setName(scanner.nextLine());
        System.out.print("Отчество: ");
        patient.setPatronymic(scanner.nextLine());
        System.out.print("Дата рождения (YYYY-MM-DD): ");
        String  sDate = scanner.nextLine(); //TODO Ввести проверку на формат
        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
        patient.setPatientBirthdate(new Date(date.getTime()));
        System.out.print("Телефон: +7"); //TODO Добавить проверку на не ноль и на уникальность номера телефона, так как он затем используется как логин
        patient.setTelephoneNumber("+7" + scanner.nextLine());

        String query1 = "INSERT INTO app.patient_сard_regdata\n" +
                "(surname, \"name\", patronymic, birthdate, telephone)\n" +
                "VALUES(?, ?, ?, ?, ?);";

        String query2 = "SELECT patient_id FROM app.patient_сard_regdata WHERE telephone = ?";


        try {
            PreparedStatement preparedStatement1  = worker.getConnection().prepareStatement(query1);
            preparedStatement1.setString(1, patient.getSurname());
            preparedStatement1.setString(2, patient.getName());
            preparedStatement1.setString(3, patient.getPatronymic());
            preparedStatement1.setDate(4, patient.getPatientBirthdate());
            preparedStatement1.setString(5, patient.getTelephoneNumber());

            preparedStatement1.execute();

            PreparedStatement preparedStatement2  = worker.getConnection().prepareStatement(query2);
            ResultSet resultSet;

            preparedStatement2.setString(1, patient.getTelephoneNumber());
            resultSet= preparedStatement2.executeQuery();

            while (resultSet.next()){
                user.setUserID(resultSet.getInt ("patient_id"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Заводим логин/пароль пациенту
    public void generateUserLoginPassword (){
        String query = "INSERT INTO app.authorization_data (user_id, user_login, user_password, user_role) VALUES (?, ?, ?, 4)";
        try {
            PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query);
            // Генерация логина: логин = номер телефона без +7 (например, если телефон +79047697711, то логин 9047697711)
            user.setUserLogin(patient.getTelephoneNumber().substring(2)); //TODO сделать проверку на уникальность логина

            // Генерация пароля (8 знаков, буквенно-цифровой)
            // Диапазон ASCII – буквенно-цифровой (0-9, a-z, A-Z)
            final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            SecureRandom random = new SecureRandom();

            //каждая итерация цикла случайным образом выбирает символ из заданного диапазона и добавляет его к экземпляру StringBuilder.
            user.setUserPassword(IntStream.range(0, 8)
                    .map(i -> random.nextInt(chars.length()))
                    .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                    .collect(Collectors.joining()));

            preparedStatement.setInt(1, user.getUserID());
            preparedStatement.setString(2, user.getUserLogin());
            preparedStatement.setString(3, user.getUserPassword());

            preparedStatement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void givesLoginPasswordToUser (){
        String query = "SELECT user_login, user_password FROM app.authorization_data where user_id = ?";

        try {
            PreparedStatement preparedStatement  = worker.getConnection().prepareStatement(query);
            ResultSet resultSet;

            preparedStatement.setInt(1, user.getUserID());
            resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                user.setUserLogin(resultSet.getString ("user_login"));
                user.setUserPassword(resultSet.getString ("user_password"));
            }
            System.out.println("\nРасчпечатайте данные для авторизации и передайте пациенту:\n"); //TODO реализовать отправку логина+пароля на e-mail
            System.out.println("_______________________");
            System.out.println("Логин: "+ user.getUserLogin());
            System.out.println("Пароль: "+ user.getUserPassword());
            System.out.println("-----------------------");

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

