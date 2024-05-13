import javax.swing.*;
import java.awt.*;

public class BookingWindow extends JFrame {
    private Flight flight;
    private Passenger passenger;

    private JLabel first, business, economy;
    private JButton firstClassBook, businessClassBook, economyBook;
    private JButton firstWaitlist, businessWaitlist, economyWaitlist;

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

        setVisible(true);
    }

    private void initBookingButtons() {
        firstClassBook = new JButton("Book First Class");
        businessClassBook = new JButton("Book Business Class");
        economyBook = new JButton("Book Economy");

        firstClassBook.addActionListener(e -> handleBooking("First"));
        businessClassBook.addActionListener(e -> handleBooking("Business"));
        economyBook.addActionListener(e -> handleBooking("Economy"));

        add(firstClassBook);
        add(businessClassBook);
        add(economyBook);
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

    private void handleBooking(String classType) {

        flight.handlePassengerBooking(passenger, classType);

        updateSeatAvailability();
        updateWaitlistButtons();
    }

    private void handleWaitlist(String classType) {
        int position = flight.addToWaitList(passenger, classType);
        System.out.println("Position in waitlist for " + classType + ": " + position);
    }

    private void updateSeatAvailability() {
        first.setText("First Class Seats Available: " + flight.getAvailableSeats("First"));
        business.setText("Business Class Seats Available: " + flight.getAvailableSeats("Business"));
        economy.setText("Economy Seats Available: " + flight.getAvailableSeats("Economy"));
    }

    private void updateWaitlistButtons() {
        firstWaitlist.setVisible(flight.getAvailableSeats("First") == 0);
        businessWaitlist.setVisible(flight.getAvailableSeats("Business") == 0);
        economyWaitlist.setVisible(flight.getAvailableSeats("Economy") == 0);

        if (!firstWaitlist.isShowing()) add(firstWaitlist);
        if (!businessWaitlist.isShowing()) add(businessWaitlist);
        if (!economyWaitlist.isShowing()) add(economyWaitlist);

        revalidate(); // Ensure UI updates are applied
        repaint();
    }
}
