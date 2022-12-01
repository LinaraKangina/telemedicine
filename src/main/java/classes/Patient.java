package classes;

import api.DBWoker;
import api.User;

import java.sql.Date;
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
    public Patient (){

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public DBWoker getWorker() {
        return worker;
    }

    public void setWorker(DBWoker worker) {
        this.worker = worker;
    }

    User user = new User();
    Scanner scanner = new Scanner(System.in);
    DBWoker worker = new DBWoker();


    public void selectAction (){
        System.out.println("\n Выберите, что хотите сделать:");
        System.out.println(" -получить уведомления (введите 1) \n -получить результаты анализов (введите 2) \n -внести данные (введите 3)");

    }
}


