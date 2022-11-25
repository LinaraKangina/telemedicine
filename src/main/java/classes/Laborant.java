package classes;

import api.DBWoker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Laborant {

    Scanner scanner = new Scanner(System.in);
    DBWoker worker = new DBWoker();

    Patient patient = new Patient();
    List<Patient> patients = new ArrayList<>();

    public void openPatientCard (){

        int numberOfLines = 0;

        System.out.println("\nОткрыть карточку пациента\n");
        System.out.print("Введите фамилию: ");
        patient.setSurname(scanner.nextLine());

        String query1 = "SELECT count(*) FROM app.patient_сard_regdata where surname = ?";
        String query2 = "SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ?";
        String query3 = "SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ?";
        String query4 = "SELECT count(*) FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ? and telephone = ?";

        String query11 = "SELECT * FROM app.patient_сard_regdata where surname = ?";
        String query22 = "SELECT * FROM app.patient_сard_regdata where surname = ? and \"name\" = ?";
        String query33 = "SELECT * FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ?";
        String query44 = "SELECT * FROM app.patient_сard_regdata where surname = ? and \"name\" = ? and patronymic = ? and telephone = ?";

        try {
            PreparedStatement ps1  = worker.getConnection().prepareStatement(query1);
            PreparedStatement ps2  = worker.getConnection().prepareStatement(query2);
            PreparedStatement ps3  = worker.getConnection().prepareStatement(query3);
            PreparedStatement ps4  = worker.getConnection().prepareStatement(query4);
            PreparedStatement ps11  = worker.getConnection().prepareStatement(query11);
            PreparedStatement ps22  = worker.getConnection().prepareStatement(query22);
            PreparedStatement ps33  = worker.getConnection().prepareStatement(query33);
            PreparedStatement ps44  = worker.getConnection().prepareStatement(query44);

            ResultSet resultSet1;
            ResultSet resultSet2;
            ResultSet resultSet3;
            ResultSet resultSet4;
            ResultSet resultSet11;
            ResultSet resultSet22;
            ResultSet resultSet33;
            ResultSet resultSet44;

            ps1.setString(1, patient.getSurname());

            resultSet1 = ps1.executeQuery();

            while (resultSet1.next()){
                numberOfLines = resultSet1.getInt(1);
            }

            //TODO реализовать более оптимально, может быть рекурсия?
            if (numberOfLines == 1) {

            }
            if (numberOfLines == 0){
                System.out.println("Нет карточки пациента с такой фамилией ");
            } else
            if (numberOfLines > 1){
                System.out.print("Введите имя: ");
                patient.setName(scanner.nextLine());

                ps2.setString(1, patient.getSurname());
                ps2.setString(2, patient.getName());

                resultSet2 = ps2.executeQuery();
                while (resultSet2.next()){
                    numberOfLines = resultSet2.getInt(1);
                }
                if (numberOfLines > 1) {
                    System.out.print("Введите отчество: ");
                    patient.setPatronymic(scanner.nextLine());

                    ps3.setString(1, patient.getSurname());
                    ps3.setString(2, patient.getName());
                    ps3.setString(3, patient.getPatronymic());

                    resultSet3 = ps3.executeQuery();
                    while (resultSet3.next()) {
                        numberOfLines = resultSet3.getInt(1);
                    }
                    if (numberOfLines > 1) {
                        System.out.print("Введите номер телефона: +7");
                        patient.setTelephoneNumber("+7"+scanner.nextLine());

                        ps4.setString(1, patient.getSurname());
                        ps4.setString(2, patient.getName());
                        ps4.setString(3, patient.getPatronymic());
                        ps4.setString(4, patient.getTelephoneNumber());

                        resultSet4 = ps4.executeQuery();
                        while (resultSet4.next()) {
                            numberOfLines = resultSet4.getInt(1);
                        }
                        System.out.println(patient.getTelephoneNumber());
                    }
                }
            }


            System.out.println(numberOfLines);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
