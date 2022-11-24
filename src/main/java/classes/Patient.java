package classes;

import api.DBWoker;
import api.User;

import java.util.Scanner;

public class Patient {
    private int userID;
    private String userLogin;
    private String userPassword;
    private int userRole;


    User user = new User();
    Scanner scanner = new Scanner(System.in);
    DBWoker worker = new DBWoker();
}
