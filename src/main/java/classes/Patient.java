package classes;

import api.CommonUtils;
import api.DBWorker;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Patient {
    private int patientId;
    private String surname;
    private String name;
    private String patronymic;
    private Date patientBirthdate;
    private String telephoneNumber;
    private double glycatedHemoglobin;
    private double sugarLevel;

    private boolean burdenedAnamnesis;
    private double height;
    private double weight;

    public Patient(String surname, String name, String patronymic, Date patientBirthdate, String telephoneNumber) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.patientBirthdate = patientBirthdate;
        this.telephoneNumber = telephoneNumber;
    }

    public Patient() {

    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getPatientBirthdate() {
        return patientBirthdate;
    }

    public void setPatientBirthdate(Date patientBirthdate) {
        this.patientBirthdate = patientBirthdate;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public double getGlycatedHemoglobin() {
        return glycatedHemoglobin;
    }

    public void setGlycatedHemoglobin(double glycatedHemoglobin) {
        this.glycatedHemoglobin = glycatedHemoglobin;
    }

    public double getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(double sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public boolean isBurdenedAnamnesis() {
        return burdenedAnamnesis;
    }

    public void setBurdenedAnamnesis(boolean burdenedAnamnesis) {
        this.burdenedAnamnesis = burdenedAnamnesis;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    Scanner scanner = new Scanner(System.in);

    public void patientsArm(int patientId) {
        int selectedAction = selectAction();

        switch (selectedAction) {
            case (1):
                getMessages(patientId);
                break;
            case (2):
                getLabData(patientId);
                break;
            case (3):
                inputTestResults(patientId);
                getPatientHeight(patientId); // запрос веса пациента.
                double imt = CommonUtils.imtCalculator (getWeight(), getHeight(), false); // вычислим ИМТ
                if (imt > 25){
                    System.out.println(" - Вам необходимо уменьшить калорийность рациона и увеличить физические нагрузки");
                }else if (imt <= 18.5) {
                    System.out.println(" - Вам необходимо увеличить калорийность рациона");
                }
                break;
            default:
                break;
        }
    }

    private void getMessages(int patientId) {
        System.out.println("------------------------------------------------------");
        String query = "SELECT message_file_address FROM app.messages \n" +
                "WHERE recording_date = (SELECT MAX(recording_date) FROM app.messages WHERE patient_id = ?)\n" +
                "order by patient_id";
        String messageAddress = null;
        try (Connection connection = DBWorker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Date date = null;
            while (resultSet.next()) {
                messageAddress = resultSet.getString("message_file_address"); // путь к самому свежему сообщению от врача
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (FileReader fileReader = new FileReader(messageAddress)){
            while (fileReader.ready()){
                System.out.print((char) fileReader.read());
            }
        } catch (NullPointerException e){
            System.out.println("Сообщений не обнаружено.");
        }
        catch (IOException e){
            System.out.println("Ошибка при считывании данных!");
        }
        System.out.println("\n------------------------------------------------------");
    }


    // Выбрать действие
    public int selectAction() {
        int selectedAction = 0;
        System.out.println("\n Выберите, что хотите сделать:");
        System.out.println(" -получить сообщения (введите 1) \n -получить результаты анализов (введите 2) \n -внести данные (введите 3)");
        do {
            selectedAction = scanner.nextInt();

            if (selectedAction != 1 && selectedAction != 2 && selectedAction != 3) {
                System.out.print("Введено некорректное значение, введите 1, 2 или 3: ");
            }
        } while (selectedAction != 1 && selectedAction != 2 && selectedAction != 3);
        return selectedAction;
    }

    public void getLabData (int patientId) {
        String query = "SELECT patient_id, glycated_hemoglobin, sugar_level, recording_date FROM app.patient_сard_labdata \n" +
                "WHERE recording_date = (SELECT MAX(recording_date) FROM app.patient_сard_labdata WHERE patient_id = ? AND is_critical = true)\n" +
                "order by patient_id";
        try (Connection connection = DBWorker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Date date;

            while (resultSet.next()) {
                setGlycatedHemoglobin(resultSet.getDouble("glycated_hemoglobin"));
                setSugarLevel(resultSet.getDouble("sugar_level"));
                date = resultSet.getDate("recording_date");

                System.out.println("Гликированный гемоглобин (%): " + getGlycatedHemoglobin());
                System.out.println("Уровень сахара (ммоль/л): " + getSugarLevel());
                System.out.println("Дата получения результатов анализов: " + date + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getPatientHeight(int patientId){
        String query = "SELECT height FROM app.patient_сard_docdata WHERE patient_id = ?";
        try (Connection connection = DBWorker.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                setHeight(resultSet.getDouble("height"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getHeight();
    }

    public void inputTestResults (int patientId){


        boolean isCritical = false;

        System.out.print("Введите ваш актуальный вес (кг): ");
        setWeight(scanner.nextDouble());
        System.out.print("Введите уровень сахара в крови (данные необходимо получать утром натощак): ");
        setSugarLevel(scanner.nextDouble());

        // если уровень сахара выходит за пределы [3,30; 7,80], то считаем значения анализы очень плохими
        if (getSugarLevel() < 3.30 || getSugarLevel() > 7.80) {
            isCritical = true;
        }

        //записываем данные анализов в таблицу patient_сard_patdata. Значения patient_id - не уникальны = patient_id из таблицы patient_сard_regdata
        String query = "INSERT INTO app.patient_сard_patdata (patient_id, weight, sugar_level, is_critical, recording_date) VALUES (?, ?, ?, ?, now())";
        try {
            PreparedStatement preparedStatement = DBWorker.getConnection().prepareStatement(query);

            preparedStatement.setInt(1, patientId);
            preparedStatement.setDouble(2, getWeight());
            preparedStatement.setDouble(3, getSugarLevel());
            preparedStatement.setBoolean(4, isCritical);
            preparedStatement.execute();

            System.out.println("Данные успешно записаны");

            if (getSugarLevel() < 3.30) {
                System.out.println("\nУровень сахара сильно понижен. Вам надо поесть. Через три часа после принятия пищи снова повторите тест.");
            }
            if (getSugarLevel() > 5.50 && getSugarLevel() <= 7.8){
                System.out.println("\nУровень сахара повышен. Повторите тест через три часа и в случае, если анализы снова окажутся неудовлетворительными, обратитесь к врачу");
            }
            if (getSugarLevel() > 7.8){
                System.out.println("\nУровень сахара очень сильно повышен. Срочно обратитесь к врачу");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

