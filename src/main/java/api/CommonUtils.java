package api;

import classes.Patient;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommonUtils {
    static Scanner scanner = new Scanner(System.in);
    static DBWorker worker;
    static Patient patient = new Patient();

    // получить всех юзеров. Похоже, не понадобится
    // Вызов так:
    // List<User> users = CommonUtils.getUserData("SELECT * FROM app.authorization_data");
    //    System.out.println(users);
    public static List<User> getUserData(String query)  {
        List<User> users = new ArrayList<>();

        try (Connection connection = DBWorker.getConnection();
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

        int patientId = 0;
        try {
            Connection connection = DBWorker.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, surname);
            if (name != null) {preparedStatement.setString(2, name);}
            if (patronymic != null) {preparedStatement.setString(3, patronymic);}
            if (telephone != null) {preparedStatement.setString(4, telephone);}
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()){
                if (patientId == 0) {patientId = resultSet.getInt(1);}
                if (surname == null) { surname  = resultSet.getString(2);}
                if (name == null) { name  = resultSet.getString(3);}
                if (patronymic == null)  { patronymic = resultSet.getString(4);}
                Date birthdate = resultSet.getDate(5);
                if (telephone == null) telephone = resultSet.getString(6);

                System.out.println ("\n_________________________________________________________________________________________________________________________");
                System.out.println ("Открыта карточка пациента: " + surname + " " + name + " " + patronymic + ", Дата рождения " + birthdate + ", Телефон " + telephone +"\n");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return patientId;
    }

    public static int openPatientCard(){

        int numberOfLines = 0; // Количество строк, найденных по запросу
        int numberOfTry = 0; // Количество попыток ввода. Дается две попытки на ввод каждого параметра
        int patientId = 0; // сюда запишем значение колонки patientId, оно нам понадобится позже

        System.out.println("\nОткрыть карточку пациента\n");

        // TODO Степень "матрёшочности" очень высокая. Придумать, как оптимизировать. Рекурсия?
        try {
            PreparedStatement ps1 = DBWorker.getConnection().prepareStatement("SELECT count(*) FROM app.patient_сard_regdata where surname = ?");
            ResultSet resultSet1;

            do {
                System.out.print("Введите фамилию: ");
                patient.setSurname(scanner.nextLine());
                ps1.setString(1, patient.getSurname());
                resultSet1 = ps1.executeQuery();

                while (resultSet1.next()){
                    numberOfLines = resultSet1.getInt(1);
                }

                if (numberOfLines == 0){
                    System.out.println("Нет карточки пациента с такой фамилией ");
                }else break;

                numberOfTry++;
                if (numberOfTry == 2){
                    System.out.println("Обратитесь к регистратору");
                }
            } while (numberOfLines == 0 && numberOfTry <2);
            numberOfTry=0; // обнуляем счетчик количества попыток ввода

            if (numberOfLines == 1) {
                patientId = CommonUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where surname = ?",
                        patient.getSurname(), patient.getName(), patient.getPatronymic(), patient.getTelephoneNumber());
            }else
            if (numberOfLines > 1){
                PreparedStatement ps2  = worker.getConnection().prepareStatement("SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ?");
                ResultSet resultSet2;
                do {
                    System.out.print("Введите имя: ");
                    patient.setName(scanner.nextLine());
                    ps2.setString(1, patient.getSurname());
                    ps2.setString(2, patient.getName());
                    resultSet2 = ps2.executeQuery();

                    while (resultSet2.next()){
                        numberOfLines = resultSet2.getInt(1);
                    }

                    if (numberOfLines == 0){
                        System.out.println("Нет карточки пациента с такими фамилией и именем");
                    }else break;

                    numberOfTry++;
                    if (numberOfTry == 2){
                        System.out.println("Обратитесь к регистратору");
                    }
                } while (numberOfLines == 0 && numberOfTry <2);
                numberOfTry=0;

                if (numberOfLines == 1) {
                    patientId = CommonUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ?)",
                            patient.getSurname(), patient.getName(), patient.getPatronymic(), patient.getTelephoneNumber());
                }else
                if (numberOfLines > 1) {
                    PreparedStatement ps3  = worker.getConnection().prepareStatement("SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ?");
                    ResultSet resultSet3;

                    do{
                        System.out.print("Введите отчество: ");
                        patient.setPatronymic(scanner.nextLine());
                        ps3.setString(1, patient.getSurname());
                        ps3.setString(2, patient.getName());
                        ps3.setString(3, patient.getPatronymic());
                        resultSet3 = ps3.executeQuery();

                        while (resultSet3.next()) {
                            numberOfLines = resultSet3.getInt(1);
                        }
                        if (numberOfLines == 0){
                            System.out.println("Нет карточки пациента с такими фамилией, именем, отчеством");
                        } else break;

                        numberOfTry++;
                        if (numberOfTry == 2){
                            System.out.println("Обратитесь к регистратору");
                        }
                    } while (numberOfLines == 0 && numberOfTry <2);
                    numberOfTry=0;

                    if (numberOfLines == 1) {
                        patientId = CommonUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ? AND patronymic = ?)",
                                patient.getSurname(), patient.getName(), patient.getPatronymic(), patient.getTelephoneNumber());

                    }else
                    if (numberOfLines > 1) {
                        PreparedStatement ps4  = worker.getConnection().prepareStatement("SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ? and telephone = ?");
                        ResultSet resultSet4;

                        do {
                            System.out.print("Введите номер телефона: +7");
                            patient.setTelephoneNumber("+7" + scanner.nextLine());
                            ps4.setString(1, patient.getSurname());
                            ps4.setString(2, patient.getName());
                            ps4.setString(3, patient.getPatronymic());
                            ps4.setString(4, patient.getTelephoneNumber());

                            resultSet4 = ps4.executeQuery();
                            while (resultSet4.next()) {
                                numberOfLines = resultSet4.getInt(1);
                            }

                            if (numberOfLines == 0) {
                                System.out.println("Номер телефона не найден, попробуйте ввести еще раз");
                            }else break;
                            numberOfTry++;

                            if (numberOfTry == 2) {
                                System.out.println("Обратитесь к регистратору");
                            }
                        } while (numberOfLines == 0 && numberOfTry <2);

                        if (numberOfLines == 1) {
                            patientId = CommonUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ? AND patronymic = ? AND telephone = ?)",
                                    patient.getSurname(), patient.getName(), patient.getPatronymic(), patient.getTelephoneNumber());
                        }else
                        if (numberOfLines > 1){
                            System.out.println("Нарушена уникальность данных, обратитесь к регистратору");
                        }
                    }
                }
            }
            patient.setPatientId(patientId);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return patientId;
    }

    //метод заставляет пользователя вводить только "да" или "нет". В зависимости от ответа да/нет возвращает соответсвенно true/false
    public static boolean yesNoResponseHandler() {
        String yesOrNo;
        boolean yn = false;
        do {
            System.out.print(" (введите 'да' или 'нет'): ");
            yesOrNo = scanner.nextLine();

            if (yesOrNo.equals("да")) {
                yn = true;
            } else if (yesOrNo.equals("нет")) {
                yn = false;
            }
        } while (!yesOrNo.equals("да") && !yesOrNo.equals("нет"));

        return yn;
    }

    // метод вычисляет ИМТ, печатает его значение и расшифровку (если needToPrint == true)
    public static double imtCalculator (double weight, double height, boolean needToPrint) {
        double imt; // ИМТ пациента = вес / (рост^2) * 10000

        imt = (weight * 10000) / (Math.pow(height, 2));
        String formattedDouble = new DecimalFormat("#0.00").format(imt);
        System.out.print("\nИМТ: " + formattedDouble);

        if (needToPrint) {
            if (imt <= 16) {
                System.out.println(" - выраженный дефицит массы тела");
            } else if (imt > 16 && imt <= 18.5) {
                System.out.println(" - дефицит массы тела");
            } else if (imt > 18.5 && imt <= 25) {
                System.out.println(" - норма");
            } else if (imt > 25 && imt <= 30) {
                System.out.println(" - избыточная масса тела");
            } else if (imt > 30 && imt <= 35) {
                System.out.println(" - ожирение первой степени");
            } else if (imt > 35 && imt < 40) {
                System.out.println(" - ожирение второй степени");
            } else if (imt >= 40) {
                System.out.println(" - ожирение третьей степени");
            }
        }
        return imt;
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
