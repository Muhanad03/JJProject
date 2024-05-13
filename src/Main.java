import javax.swing.*;

public class Main {



    public static void main(String [] args){

        UserManagement userManager = new UserManagement();
        userManager.addUser(new Passenger("Test1", 1));
        userManager.addUser(new Passenger("Test2", 2));

        MainWindow mainWindow = new MainWindow(userManager);
        mainWindow.setVisible(true);


    }
}
