import javax.swing.*;
import java.awt.*;

public class BookingWindow extends JFrame {
    private Flight flight; // The flight being booked
    private Passenger passenger; // The passenger making the booking

    private JLabel firstClassLabel, businessClassLabel, economyClassLabel; // Labels to show seat availability
    private JButton bookFirstClassButton, bookBusinessClassButton, bookEconomyClassButton; // Buttons for booking seats
    private JButton joinWaitlistButton, leaveWaitlistButton; // Buttons for waitlist actions

    public BookingWindow(Flight flight, Passenger passenger) {
        this.flight = flight;
        this.passenger = passenger;
        setupWindow(); // Set up the booking window
    }

    // Initialize the booking window
    private void setupWindow() {
        setTitle("Flight booker");
        setSize(700, 400);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel infoPanel = createInfoPanel(); // Panel to show seat availability
        JPanel actionPanel = createActionPanel(); // Panel for booking and waitlist actions

        add(infoPanel, BorderLayout.CENTER); // Add the info panel to the center
        add(actionPanel, BorderLayout.SOUTH); // Add the action panel to the bottom

        setVisible(true); // Make the window visible
    }

    // Create a panel to display seat availability
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels showing the number of available seats for each class
        firstClassLabel = new JLabel("First Class Seats Available: " + getAvailableSeats("First"));
        businessClassLabel = new JLabel("Business Class Seats Available: " + getAvailableSeats("Business"));
        economyClassLabel = new JLabel("Economy Seats Available: " + getAvailableSeats("Economy"));

        panel.add(new JLabel("First Class:"));
        panel.add(firstClassLabel);
        panel.add(new JLabel("Business Class:"));
        panel.add(businessClassLabel);
        panel.add(new JLabel("Economy Class:"));
        panel.add(economyClassLabel);

        return panel;
    }

    // Create a panel with booking and waitlist action buttons
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // Booking buttons for each class
        bookFirstClassButton = new JButton("Book First Class");
        bookFirstClassButton.addActionListener(e -> Booking("First"));
        panel.add(bookFirstClassButton);

        bookBusinessClassButton = new JButton("Book Business Class");
        bookBusinessClassButton.addActionListener(e -> Booking("Business"));
        panel.add(bookBusinessClassButton);

        bookEconomyClassButton = new JButton("Book Economy");
        bookEconomyClassButton.addActionListener(e -> Booking("Economy"));
        panel.add(bookEconomyClassButton);

        // Waitlist action buttons
        joinWaitlistButton = new JButton("Join Waitlist");
        joinWaitlistButton.addActionListener(e -> Waitlist());
        panel.add(joinWaitlistButton);

        leaveWaitlistButton = new JButton("Leave Waitlist");
        leaveWaitlistButton.addActionListener(e -> RemoveFromWaitlist());
        panel.add(leaveWaitlistButton);

        updateButtons(); // Update button states based on availability
        return panel;
    }

    // Get the number of available seats for a given class
    private int getAvailableSeats(String classType) {
        return flight.getAvailableSeats(classType);
    }

    // Handle booking a seat in a specified class
    private void Booking(String classType) {
        flight.handlePassengerBooking(passenger, classType);

        updateLabels();
        updateButtons();
    }

    // Handle adding the passenger to a waitlist
    private void Waitlist() {
        String[] options = {"First", "Business", "Economy"};
        String classType = (String) JOptionPane.showInputDialog(this, "Select class to join waitlist:", "Waitlist", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (classType != null) {
            if (getAvailableSeats(classType) > 0) {
                JOptionPane.showMessageDialog(this, "Seats are available in " + classType + " class. You cannot join the waitlist.");
            } else {
                int position = flight.addToWaitList(passenger, classType);
                JOptionPane.showMessageDialog(this, "Added to " + classType + " waitlist. Position: " + position);
            }
        }
        updateButtons(); // Update button states
    }

    // Handle removing the passenger from the waitlist
    private void RemoveFromWaitlist() {
        if (flight.removeFromWaitList(passenger)) {
            JOptionPane.showMessageDialog(this, "Removed from waiting list.");
        } else {
            JOptionPane.showMessageDialog(this, "Passenger is not on any waiting list.");
        }
        updateButtons(); // Update button states
    }

    // Update seat availability labels
    private void updateLabels() {
        firstClassLabel.setText("First Class Seats Available: " + getAvailableSeats("First"));
        businessClassLabel.setText("Business Class Seats Available: " + getAvailableSeats("Business"));
        economyClassLabel.setText("Economy Seats Available: " + getAvailableSeats("Economy"));
    }

    // Update the states of booking and waitlist buttons
    private void updateButtons() {
        bookFirstClassButton.setEnabled(getAvailableSeats("First") > 0);
        bookBusinessClassButton.setEnabled(getAvailableSeats("Business") > 0);
        bookEconomyClassButton.setEnabled(getAvailableSeats("Economy") > 0);
        leaveWaitlistButton.setEnabled(flight.isPassengerInWaitlist(passenger));
    }
}
