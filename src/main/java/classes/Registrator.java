package classes;

import api.DBWoker;
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

    User user = new User();
    DBWoker worker = new DBWoker();
    Scanner scanner = new Scanner(System.in);
    private String patientTelephone;
    private int userId;

    public String getPatientTelephone() { return patientTelephone;}
    public int getUserId() { return userId;}

    //Заведение пациента (опрос, запись в БД)
    public void registerUser () throws ParseException {

        System.out.println("\nВведите данные пациента\n");
        System.out.print("Фамилия: ");
        String patientSurname = scanner.nextLine();
        System.out.print("Имя: ");
        String patientName = scanner.nextLine();
        System.out.print("Отчество: ");
        String patientPatronymic = scanner.nextLine();
        System.out.print("Дата рождения (YYYY-MM-DD): ");
        String  sDate = scanner.nextLine(); //TODO Ввести проверку на формат
        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
        Date patientBirthdate = new Date(date.getTime());
        System.out.print("Телефон: +7");
        patientTelephone = "+7" + scanner.nextLine();

        String query1 = "INSERT INTO app.patient_сard_regdata\n" +
                "(surname, \"name\", patronymic, birthdate, telephone)\n" +
                "VALUES(?, ?, ?, ?, ?);";

        String query2 = "SELECT patient_id FROM app.patient_сard_regdata WHERE telephone = ?";

        try {
            PreparedStatement preparedStatement1  = worker.getConnection().prepareStatement(query1);
            preparedStatement1.setString(1, patientSurname);
            preparedStatement1.setString(2, patientName);
            preparedStatement1.setString(3, patientPatronymic);
            preparedStatement1.setDate(4, patientBirthdate);
            preparedStatement1.setString(5, patientTelephone);

            preparedStatement1.execute();

            PreparedStatement preparedStatement2  = worker.getConnection().prepareStatement(query2);
            ResultSet resultSet;

            preparedStatement2.setString(1, patientTelephone);
            resultSet= preparedStatement2.executeQuery();

            while (resultSet.next()){
                userId = resultSet.getInt ("patient_id");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Заводим логин/пароль пациенту
    public void generateUserLoginPassword (){
        String query = "insert into app.authorization_data (user_id, user_login, user_password, user_role) VALUES (?, ?, ?, 4)";
        try {
            PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query);
            // Генерация логина: логин = номер телефона без +7 (например, если телефон +79047697711, то логин 9047697711)
            String patientLogin = patientTelephone.substring(2);

            // Генерация пароля (8 знаков, буквенно-цифровой)
            String patientPassword;
            // Диапазон ASCII – буквенно-цифровой (0-9, a-z, A-Z)
            final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            SecureRandom random = new SecureRandom();

            //каждая итерация цикла случайным образом выбирает символ из заданного диапазона ASCII и добавляет его к экземпляру StringBuilder.
            patientPassword = IntStream.range(0, 8)
                    .map(i -> random.nextInt(chars.length()))
                    .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                    .collect(Collectors.joining());


            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, patientLogin);
            preparedStatement.setString(3, patientPassword);

            preparedStatement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void givesLoginPasswordToUser (){
        String userLogin = "Логин";
        String userPassword = "Пароль";

        String query = "SELECT user_login, user_password FROM app.authorization_data where user_id = ?";

        try {
            PreparedStatement preparedStatement  = worker.getConnection().prepareStatement(query);
            ResultSet resultSet;

            preparedStatement.setInt(1, userId);
            resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                userLogin = resultSet.getString ("user_login");
                userPassword = resultSet.getString ("user_password");
            }
            System.out.println("\nРасчпечатайте данные для авторизации и передайте пациенту:\n"); //TODO реализовать отправку логина+пароля на e-mail
            System.out.println("Логин: "+ userLogin);
            System.out.println("Логин: "+ userPassword);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

