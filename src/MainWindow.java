import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private DefaultListModel<Flight> flightListModel;
    private JList<Flight> flightList;
    private List<Flight> allFlights = new ArrayList<>();  // This will store all generated flights

    // This handles the account switching
    private UserManagement userManager;
    private JComboBox<Passenger> userDropdown;

    public MainWindow(UserManagement userManager) {
        setTitle("Flight Scheduler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        this.userManager = userManager;
        initUserSwitcher();

        flightListModel = new DefaultListModel<>();
        generateFlights(50); // Generate 50 random flights
        flightList = new JList<>(flightListModel);
        JScrollPane scrollPane = new JScrollPane(flightList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by Flight Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (!searchText.isEmpty()) {
                try {
                    int flightNumber = Integer.parseInt(searchText);
                    List<Flight> filteredFlights = allFlights.stream()
                            .filter(f -> f.getFlightNumber() == flightNumber)
                            .collect(Collectors.toList());
                    if (!filteredFlights.isEmpty()) {
                        updateFlightListModel(filteredFlights);
                    } else {
                        JOptionPane.showMessageDialog(this, "Flight not found", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                updateFlightListModel(allFlights); // Restore original flight list
            }
        });

        flightList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected, open the booking window
                    int index = list.locationToIndex(evt.getPoint());
                    new BookingWindow(flightListModel.getElementAt(index), userManager.getActiveUser()).setVisible(true);
                }
            }
        });
    }

    private void initUserSwitcher() {
        System.out.println("Initializing user switcher with users count: " + userManager.getUsers().size());

        JPanel panel = new JPanel(new FlowLayout()); // Using FlowLayout for simplicity
        userDropdown = new JComboBox<>(new Vector<>(userManager.getUsers())); // Make sure users are added before this line is executed
        JButton switchUserButton = new JButton("Switch User");
        JButton cancelFlightButton = new JButton("Cancel flight");

        cancelFlightButton.addActionListener(e -> {
            JFrame cancelFrame = new JFrame("Booking cancellation");
            cancelFrame.setSize(300, 100);
            cancelFrame.setLocationRelativeTo(null);
            cancelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel2 = new JPanel();
            cancelFrame.add(panel2);
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

            JLabel bookingLabel = new JLabel("Booking ref:");
            JTextField bookingText = new JTextField(20);
            JButton cancelBookingButton = new JButton("Cancel booking");

            cancelBookingButton.addActionListener(e2 -> {
                String bookingRef = bookingText.getText();
                if (bookingRef.isEmpty()) {
                    JOptionPane.showMessageDialog(cancelFrame, "Please enter a booking reference.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Extract flight number from booking reference
                String[] parts = bookingRef.split("-");
                if (parts.length != 2) {
                    JOptionPane.showMessageDialog(cancelFrame, "Invalid booking reference format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int flightNumber;
                try {
                    flightNumber = Integer.parseInt(parts[0]);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(cancelFrame, "Invalid flight number in booking reference.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Flight flight = allFlights.stream()
                        .filter(f -> f.getFlightNumber() == flightNumber)
                        .findFirst()
                        .orElse(null);

                if (flight == null) {
                    JOptionPane.showMessageDialog(cancelFrame, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = flight.cancelBookingByRef(bookingRef, userManager.getActiveUser());
                if (success) {
                    JOptionPane.showMessageDialog(cancelFrame, "Booking canceled successfully.");
                } else {
                    JOptionPane.showMessageDialog(cancelFrame, "Booking reference not found or user not authorized.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                cancelFrame.dispose();
            });

            panel2.add(bookingLabel);
            panel2.add(bookingText);
            panel2.add(cancelBookingButton);

            cancelFrame.setVisible(true);
        });

        panel.add(cancelFlightButton);

        switchUserButton.addActionListener(e -> {
            Passenger selectedUser = (Passenger) userDropdown.getSelectedItem();
            userManager.setActiveUser(selectedUser);
            JOptionPane.showMessageDialog(this, "Switched to user: " + selectedUser.getName());
        });

        panel.add(new JLabel("Switch User:"));
        panel.add(userDropdown);
        panel.add(switchUserButton);

        add(panel, BorderLayout.SOUTH); // Ensure this is added to the JFrame correctly
    }

    private void generateFlights(int count) {
        Random rand = new Random();
        allFlights.clear();  // Clear previous data

        // List of sample airports
        String[] airports = {"JFK", "LAX", "ORD", "DFW", "DEN", "SFO", "SEA", "ATL", "MIA", "BOS"};

        // Generate random flights
        for (int i = 0; i < count; i++) {
            int flightNumber = rand.nextInt(900) + 100; // Ensure flight number is between 100 and 999

            // Random departure and arrival airports
            String departureAirport = airports[rand.nextInt(airports.length)];
            String arrivalAirport;
            do {
                arrivalAirport = airports[rand.nextInt(airports.length)];
            } while (departureAirport.equals(arrivalAirport));

            // Random date within the next 30 days
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, rand.nextInt(30)); // Add random number of days to current date
            Date departureDate = calendar.getTime();

            // Random seat capacities
            int firstClassSeats = rand.nextInt(10) + 1;    // 1 to 10 seats
            int businessClassSeats = rand.nextInt(20) + 5; // 5 to 25 seats
            int economyClassSeats = rand.nextInt(50) + 20; // 20 to 70 seats

            Flight flight = new Flight(flightNumber, departureAirport, departureDate, arrivalAirport,
                    firstClassSeats, businessClassSeats, economyClassSeats);
            allFlights.add(flight);
        }

        mergeSort(allFlights); // Sort the flights using merge sort
        updateFlightListModel(allFlights);
    }

    private void updateFlightListModel(List<Flight> flights) {
        flightListModel.clear();
        flights.forEach(flightListModel::addElement);
    }

    public void mergeSort(List<Flight> list) {
        if (list.size() > 1) {
            int mid = list.size() / 2;
            List<Flight> leftHalf = new ArrayList<>(list.subList(0, mid));
            List<Flight> rightHalf = new ArrayList<>(list.subList(mid, list.size()));

            mergeSort(leftHalf);
            mergeSort(rightHalf);

            merge(list, leftHalf, rightHalf);
        }
    }

    private void merge(List<Flight> list, List<Flight> leftHalf, List<Flight> rightHalf) {
        int i = 0, j = 0, k = 0;
        while (i < leftHalf.size() && j < rightHalf.size()) {
            if (leftHalf.get(i).getFlightNumber() < rightHalf.get(j).getFlightNumber()) {
                list.set(k++, leftHalf.get(i++));
            } else {
                list.set(k++, rightHalf.get(j++));
            }
        }
        while (i < leftHalf.size()) {
            list.set(k++, leftHalf.get(i++));
        }
        while (j < rightHalf.size()) {
            list.set(k++, rightHalf.get(j++));
        }
    }
}
