package classes;

import api.CRUDUtils;
import api.DBWoker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Laborant {

    Scanner scanner = new Scanner(System.in);
    DBWoker worker;

    Patient patient = new Patient();

    public void openPatientCard (){

        int numberOfLines = 0; // количество строк, найденных по запросу
        int numberOfTry = 0; // количество попыток ввода. Дается две попытки на ввод каждого параметра
        int patientId = 0; // сюда запишем значение колонки patientId, оно нам понадобится позже

        System.out.println("\nОткрыть карточку пациента\n");

        // TODO Степень "матрёшочности" очень высокая. Придумать, как оптимизировать. Рекурсия?
        try {
            PreparedStatement ps1  = worker.getConnection().prepareStatement("SELECT count(*) FROM app.patient_сard_regdata where surname = ?");
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
                patientId = CRUDUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where surname = ?",
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
                    patientId = CRUDUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ?)",
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
                        patientId = CRUDUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ? AND patronymic = ?)",
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
                            patientId = CRUDUtils.getPatientCard("SELECT patient_id, surname, \"name\", patronymic, birthdate, telephone FROM app.patient_сard_regdata where (surname = ? AND name = ? AND patronymic = ? AND telephone = ?)",
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
    }

    public void inputTestResults (){

        boolean isCritical = false;
        System.out.println("\nВведите результаты анализов пациента\n");
        System.out.print("Гликированный гемоглобин: ");
        patient.setGlycatedHemoglobin(scanner.nextDouble());
        System.out.print("Уровень сахара: ");
        patient.setSugarLevel(scanner.nextDouble());

        // если уровень гликированного гемоглобина > 6,00 или уровень сахара выходит за пределы [3,30; 7,80], то считаем значения анализы плохими
        if (patient.getGlycatedHemoglobin() > 6.00 || patient.getSugarLevel() > 7.80 || patient.getSugarLevel() < 3.30){
            isCritical = true;
        }

        //записываем данные анализов в таблицу patient_сard_labdata. Значения patient_id - не уникальны = patient_id из таблицы patient_сard_regdata
        String query = "INSERT INTO app.patient_сard_labdata (patient_id, glycated_hemoglobin, sugar_level, is_critical, recording_date) VALUES (?, ?, ?, ?, now())";
        try {
            PreparedStatement preparedStatement = worker.getConnection().prepareStatement(query);

            preparedStatement.setInt(1, patient.getPatientId());
            preparedStatement.setDouble(2, patient.getGlycatedHemoglobin());
            preparedStatement.setDouble(3, patient.getSugarLevel());
            preparedStatement.setBoolean(4, isCritical);
            preparedStatement.execute();

            System.out.print("Данные успешно записаны");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
