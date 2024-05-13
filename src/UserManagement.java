import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserManagement {
    private List<Passenger> users;
    private Passenger activeUser;

    public UserManagement() {
        this.users = new ArrayList<>();
    }

    public void addUser(Passenger user) {
        this.users.add(user);
        if (activeUser == null) { // Automatically set the first added user as active
            setActiveUser(user);
        }
    }

    public void setActiveUser(Passenger user) {
        if (users.contains(user)) {
            this.activeUser = user;
            System.out.println("Switched to user: " + user.getName());
        } else {
            System.out.println("User not found.");
        }
    }

    public Passenger getActiveUser() {
        return activeUser;
    }

    public List<Passenger> getUsers() {
        return users;
    }


}
