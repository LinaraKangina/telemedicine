package classes;

import api.CommonUtils;
import api.DBWorker;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Laborant {

    Scanner scanner = new Scanner(System.in);
    DBWorker worker;

    Patient patient = new Patient();

    public int openPatientCard(){

        patient.setPatientId(CommonUtils.openPatientCard());
        return patient.getPatientId();
    }

    public void inputTestResults (){

        boolean isCritical = false;
        System.out.println("\nВведите результаты анализов пациента\n");
        System.out.print("Гликированный гемоглобин (%): ");
        patient.setGlycatedHemoglobin(scanner.nextDouble());
        System.out.print("Уровень сахара (ммоль/л): ");
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
