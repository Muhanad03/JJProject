import javax.swing.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Login");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            Passenger p = new Passenger(username,0);
            dispose();
        });

        panel.add(userLabel);
        panel.add(userText);
        panel.add(loginButton);
    }
}
