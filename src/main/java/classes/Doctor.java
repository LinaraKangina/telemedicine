package classes;

import api.CommonUtils;
import api.DBWorker;
import api.User;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Doctor {

    DBWorker worker;
    Scanner scanner = new Scanner(System.in);

    User user = new User();
    Patient patient = new Patient();

    public Doctor(int patientId) {
    }
    public Doctor() {
    }

    //
    public void doctorsArm (int doctorID) throws IOException {
        System.out.println("Получить уведомления или провести первичный прием пациента (введите соответственно 1 или 2)");
        int selectedAction = selectAction (doctorID);

        switch (selectedAction) {
            case (1):
                getNotifications(doctorID);
                System.out.println("Хотите отправить сообщение пациенту?");
                if (CommonUtils.yesNoResponseHandler() == true){
                    sendMessage (doctorID);
                }
                break;
            case (2):
                openPatientCard();
                inputInitialDoctorsAppointment(doctorID);
                break;
            default:
                break;
        }
    }

    // выбор действия
    public int selectAction (int doctorID) {
        String enteredValue;
        int selectedAction = 0;
        do {
            enteredValue = scanner.nextLine(); // не selectedAction=scanner.nextInt(), так как если ввести строку, то вылетает исключение

            if (!(enteredValue.equals("1") || enteredValue.equals("2"))){
                System.out.print("Введено некорректное значение, введите 1 или 2: ");
            }
        } while (!(enteredValue.equals("1") || enteredValue.equals("2")));

        try {
            selectedAction = Integer.valueOf(enteredValue);
        }catch (NumberFormatException e) { // это исключение никогда не сработает, так как выше есть проверка на 1 или 2. Но пусть пока остается
            System.err.println("Неправильный формат строки!");
        }
        return selectedAction;
    }


    // открыть карточку пациента
    public int openPatientCard(){

        System.out.println("\n************  Первичный прием пациента  *************");
        patient.setPatientId(CommonUtils.openPatientCard());
        return patient.getPatientId();
    }

    // получить уведомления про критические анализы пациентов
    public void getNotifications(int doctorId){
        List<Integer> patients = getPatientsArray(doctorId);
        for (int patientId : patients){
            getCriticalLabData(patientId);
            getCriticalPatData(patientId);
        }
    }

    // метод возвращает patient_id всех пациентов, которые были на приеме у доктора с doc_id
    public static List<Integer> getPatientsArray (int doctorID) {
        List<Integer> patients = new ArrayList<>();
        String query = "SELECT patient_id FROM app.patient_сard_docdata WHERE doc_id = ?";

        try (Connection connection = DBWorker.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int patientId = resultSet.getInt("patient_ID");

                patients.add(new Integer(patientId));
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    // запрашиваем критические лабораторные анализы
    public void getCriticalLabData (int patientId) {
        String query = "SELECT patient_id, glycated_hemoglobin, sugar_level, recording_date FROM app.patient_сard_labdata \n" +
                "WHERE recording_date = (SELECT MAX(recording_date) FROM app.patient_сard_labdata WHERE patient_id = ? AND is_critical = true)\n" +
                "order by patient_id";
        try (Connection connection = DBWorker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Date date = null;

            while (resultSet.next()) {
                System.out.println("У следующего пациента результаты лабораторных анализов сильно выходят за границы референсных значений:");
                getPatientCard ("SELECT surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where patient_id = ?", patientId);
                patient.setGlycatedHemoglobin(resultSet.getDouble("glycated_hemoglobin"));
                patient.setSugarLevel(resultSet.getDouble("sugar_level"));
                date = resultSet.getDate("recording_date");

                System.out.println("Гликированный гемоглобин (%): " + patient.getGlycatedHemoglobin());
                System.out.println("Уровень сахара (ммоль/л): " + patient.getSugarLevel());
                System.out.println("Дата получения результатов анализов: " + date + "\n");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // запрашиваем критические показатели сахара, введенные пациентом
    public void getCriticalPatData (int patientId) {
        String query = "SELECT patient_id, sugar_level, recording_date FROM app.patient_сard_patdata \n" +
                "WHERE recording_date = (SELECT MAX(recording_date) FROM app.patient_сard_patdata WHERE patient_id = ? AND is_critical = true)\n" +
                "order by patient_id\n";
        try (Connection connection = DBWorker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Date date = null;

            while (resultSet.next()) {
                System.out.println("У следующего пациента данные из глюкометра сильно выходят за границы референсных значений:");
                getPatientCard ("SELECT surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where patient_id = ?", patientId);
                patient.setSugarLevel(resultSet.getDouble("sugar_level"));
                date = resultSet.getDate("recording_date");

                System.out.println("Уровень сахара (ммоль/л): " + patient.getSugarLevel());
                System.out.println("Дата получения результатов анализов: " + date + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getPatientCard (String query, int patientId)  {
        try {
            Connection connection = DBWorker.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String surname  = resultSet.getString(1);
                String name  = resultSet.getString(2);
                String patronymic = resultSet.getString(3);
                Date birthdate = resultSet.getDate(4);
                String telephone = resultSet.getString(5);

                System.out.println (surname + " " + name + " " + patronymic + ", Дата рождения " + birthdate + ", Телефон " + telephone);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // метод создает файл txt c сообщением от врача.
    // пока файл просто записывается в папку C:\temp\messagesToPatient\ , и пациент тоже будет читать оттуда же.
    // Понятное дело, что это неправильно, так как пациент и доктор работают на разных машинах. Но пока успела только так
    // TODO в будущем реализовать отправку письма по почте. Вот тут хорошая статья https://habr.com/ru/post/526162/
    public String sendMessage (int doctorId) throws IOException {

        LocalDateTime dateTime = LocalDateTime.now(); // получить текущее время и дату
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");

        patient.setPatientId(CommonUtils.openPatientCard()); // выбор пациента, кому отправить сообщение
        // TODO сделать так, чтобы выбрать можно было только тех пациентов, кто лечится у текущего доктора

        System.out.println("Введите сообщение:");
        System.out.println("___________________________________________");
        String message = scanner.nextLine();

        // создаем папку с названием = id пациента
        String path = "C:\\temp\\messagesToPatient\\" + patient.getPatientId();
        Files.createDirectories(Paths.get(path));

        String outputFileName = "C:\\temp\\messagesToPatient\\" + patient.getPatientId() + "\\"+ dateTime.format(formatter) +".txt"; // название файла, например, будет такое: 2000017-02122022-2247.txt
        try (FileWriter fileWriter = new FileWriter(outputFileName)){
            fileWriter.write(message);
        }catch (IOException e){
            System.out.println("Ошибка при записи данных"); // Например, в названии файла нельзя писать символ ':' (пример: использовали этот символ в маске ввода времени). В таком случае сработает это исключение
        }
        setMessageFileAddress (doctorId, outputFileName);
        return outputFileName;
    }

    public void setMessageFileAddress (int doctorId, String fileAddress){
        String query = "INSERT INTO app.messages\n" +
                "(patient_id, doc_id, message_file_address, recording_date)\n" +
                "VALUES(?, ?, ?, now());\n";
        try {
            PreparedStatement preparedStatement = worker.getConnection ().prepareStatement(query);
            preparedStatement.setInt(1, patient.getPatientId());
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setString(3, fileAddress);

            preparedStatement.execute();

            System.out.println("___________________________________________");
            System.out.println("Сообщение успешно отправлено.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Первичный прием врача
    public void inputInitialDoctorsAppointment (int doctorId) {

        System.out.println("\nЗаполните данные пациента:\n");
        System.out.print("Отягощенный анамнез");
        patient.setBurdenedAnamnesis(CommonUtils.yesNoResponseHandler());

        System.out.print("Рост (см): ");
        patient.setHeight(scanner.nextDouble());
        System.out.print("Вес (кг): ");
        patient.setWeight(scanner.nextDouble());
        CommonUtils.imtCalculator(patient.getWeight(), patient.getHeight(), true);

        String query = "INSERT INTO app.patient_сard_docdata\n" +
                "(patient_id, burdened_anamnesis, height, weight, doc_id, recording_date)\n" +
                "VALUES(?, ?, ?, ?, ?, now());\n";

        try {
            PreparedStatement preparedStatement = worker.getConnection ().prepareStatement(query);
            preparedStatement.setInt(1, patient.getPatientId());
            preparedStatement.setBoolean(2, patient.isBurdenedAnamnesis());
            preparedStatement.setDouble(3, patient.getHeight());
            preparedStatement.setDouble(4, patient.getWeight());
            preparedStatement.setInt(5, doctorId);
            preparedStatement.execute();

            System.out.println("Данные успешно сохранены в карточку пациента");
            System.out.println("_______________________");
            System.out.println("Направьте пациента в лабораторию для сдачи анализов");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}