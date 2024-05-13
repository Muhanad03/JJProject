import javax.swing.*;

public class Main {



    public static void main(String [] args){

        UserManagement userManager = new UserManagement();
        userManager.addUser(new Passenger("Ben", 1));
        userManager.addUser(new Passenger("Jack", 2));
        userManager.addUser(new Passenger("Tom", 3));

        MainWindow mainWindow = new MainWindow(userManager);
        mainWindow.setVisible(true);


    }
}
