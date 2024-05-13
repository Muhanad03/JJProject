import javax.swing.*;
import java.awt.*;

public class BookingWindow extends JFrame {
    private Flight flight;
    private Passenger passenger;

    private JLabel first, business, economy;
    private JButton firstClassBook, businessClassBook, economyBook;
    private JButton firstWaitlist, businessWaitlist, economyWaitlist;
    private JButton removeFromWaitlistButton;

    public BookingWindow(Flight flight, Passenger passenger) {
        this.flight = flight;
        this.passenger = passenger;
        setTitle("Book Flight: " + flight.getFlightNumber());
        setSize(600, 400);
        setLayout(new GridLayout(0, 1));

        // Initialize labels
        first = new JLabel("First Class Seats Available: " + flight.getAvailableSeats("First"));
        business = new JLabel("Business Class Seats Available: " + flight.getAvailableSeats("Business"));
        economy = new JLabel("Economy Seats Available: " + flight.getAvailableSeats("Economy"));
        add(first);
        add(business);
        add(economy);

        // Initialize booking buttons
        initBookingButtons();

        // Initialize waitlist buttons
        initWaitlistButtons();

        // Initialize "Remove from Waiting List" button
        initRemoveFromWaitlistButton();

        setVisible(true);
    }

    private void initBookingButtons() {
        firstClassBook = new JButton("Book First Class");
        businessClassBook = new JButton("Book Business Class");
        economyBook = new JButton("Book Economy");

        firstClassBook.addActionListener(e -> handleBooking("First"));
        businessClassBook.addActionListener(e -> handleBooking("Business"));
        economyBook.addActionListener(e -> handleBooking("Economy"));

        updateBookingButtons();
    }

    private void initWaitlistButtons() {
        firstWaitlist = new JButton("Join First Class Waitlist");
        businessWaitlist = new JButton("Join Business Waitlist");
        economyWaitlist = new JButton("Join Economy Waitlist");

        firstWaitlist.addActionListener(e -> handleWaitlist("First"));
        businessWaitlist.addActionListener(e -> handleWaitlist("Business"));
        economyWaitlist.addActionListener(e -> handleWaitlist("Economy"));

        updateWaitlistButtons();
    }

    private void initRemoveFromWaitlistButton() {
        removeFromWaitlistButton = new JButton("Remove from Waiting List");
        removeFromWaitlistButton.addActionListener(e -> handleRemoveFromWaitlist());

        updateRemoveFromWaitlistButton();
    }

    private void handleBooking(String classType) {
        flight.handlePassengerBooking(passenger, classType);

        updateSeatAvailability();
        updateWaitlistButtons();
        updateRemoveFromWaitlistButton();
    }

    private void handleWaitlist(String classType) {
        int position = flight.addToWaitList(passenger, classType);
        System.out.println("Position in waitlist for " + classType + ": " + position);

        updateRemoveFromWaitlistButton();
    }

    private void handleRemoveFromWaitlist() {
        if (flight.removeFromWaitList(passenger)) {
            JOptionPane.showMessageDialog(this, "Removed from waiting list.");
        } else {
            JOptionPane.showMessageDialog(this, "Passenger is not on any waiting list.");
        }
        updateRemoveFromWaitlistButton();
    }

    private void updateSeatAvailability() {
        first.setText("First Class Seats Available: " + flight.getAvailableSeats("First"));
        business.setText("Business Class Seats Available: " + flight.getAvailableSeats("Business"));
        economy.setText("Economy Seats Available: " + flight.getAvailableSeats("Economy"));

        updateBookingButtons();
    }

    private void updateBookingButtons() {
        if (flight.getAvailableSeats("First") > 0) {
            if (!firstClassBook.isShowing()) add(firstClassBook);
        } else {
            if (firstClassBook.isShowing()) remove(firstClassBook);
        }

        if (flight.getAvailableSeats("Business") > 0) {
            if (!businessClassBook.isShowing()) add(businessClassBook);
        } else {
            if (businessClassBook.isShowing()) remove(businessClassBook);
        }

        if (flight.getAvailableSeats("Economy") > 0) {
            if (!economyBook.isShowing()) add(economyBook);
        } else {
            if (economyBook.isShowing()) remove(economyBook);
        }

        revalidate();
        repaint();
    }

    private void updateWaitlistButtons() {
        firstWaitlist.setVisible(flight.getAvailableSeats("First") == 0);
        businessWaitlist.setVisible(flight.getAvailableSeats("Business") == 0);
        economyWaitlist.setVisible(flight.getAvailableSeats("Economy") == 0);

        if (!firstWaitlist.isShowing()) add(firstWaitlist);
        if (!businessWaitlist.isShowing()) add(businessWaitlist);
        if (!economyWaitlist.isShowing()) add(economyWaitlist);

        revalidate();
        repaint();
    }

    private void updateRemoveFromWaitlistButton() {
        boolean isOnWaitlist = flight.isPassengerInWaitlist(passenger);

        if (isOnWaitlist) {
            if (!removeFromWaitlistButton.isShowing()) add(removeFromWaitlistButton);
        } else {
            if (removeFromWaitlistButton.isShowing()) remove(removeFromWaitlistButton);
        }

        revalidate();
        repaint();
    }
}
