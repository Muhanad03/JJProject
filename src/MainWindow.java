import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Main application window class
public class MainWindow extends JFrame {
    private DefaultListModel<Flight> flightListModel; // Model for storing flight data
    private JList<Flight> flightList; // Component to display flights
    private List<Flight> allFlights = new ArrayList<>(); // List to keep all generated flights

    // Handles user account switching
    private UserManagement userManager;
    private JComboBox<Passenger> userDropdown; // Dropdown menu for user selection

    // Constructor for the MainWindow class
    public MainWindow(UserManagement userManager) {
        setTitle("Flight Scheduler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        this.userManager = userManager;
        initializeUserSwitcher(); // Set up user switcher

        flightListModel = new DefaultListModel<>();
        createFlights(50); // Create 50 random flights
        flightList = new JList<>(flightListModel);
        JScrollPane scrollPane = new JScrollPane(flightList);
        add(scrollPane, BorderLayout.CENTER); // Add flight list to the center of the window

        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by Flight Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH); // Add search panel to the top of the window

        // Action listener for search button
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (!searchText.isEmpty()) {
                try {
                    int flightNumber = Integer.parseInt(searchText);
                    List<Flight> filteredFlights = binarySearchFlights(flightNumber);
                    if (!filteredFlights.isEmpty()) {
                        updateFlightList(filteredFlights);
                    } else {
                        JOptionPane.showMessageDialog(this, "Flight not found", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                updateFlightList(allFlights); // Restore the original flight list
            }
        });

        // Mouse listener for double-clicks on the flight list
        flightList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<?> list = (JList<?>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Open booking window on double-click
                    int index = list.locationToIndex(evt.getPoint());
                    new BookingWindow(flightListModel.getElementAt(index), userManager.getActiveUser()).setVisible(true);
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton viewFlightDetailsButton = new JButton("View Flight Details");
        bottomPanel.add(viewFlightDetailsButton);
        add(bottomPanel, BorderLayout.LINE_END); // Add bottom panel to the right

        // Action listener for viewing flight details
        viewFlightDetailsButton.addActionListener(e -> {
            int selectedIndex = flightList.getSelectedIndex();
            if (selectedIndex != -1) {
                Flight selectedFlight = flightListModel.getElementAt(selectedIndex);
                showFlightDetails(selectedFlight);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a flight to view details.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // Displays the details of a flight in a new window
    private void showFlightDetails(Flight flight) {
        JFrame detailsFrame = new JFrame("Flight Details");
        detailsFrame.setSize(400, 300);
        detailsFrame.setLocationRelativeTo(null);

        JTextArea detailsText = new JTextArea();
        detailsText.setEditable(false);
        detailsText.append("Flight Number: " + flight.getFlightNumber() + "\n");
        detailsText.append("Departure Airport: " + flight.getDepartureAirport() + "\n");
        detailsText.append("Arrival Airport: " + flight.getArrivalAirport() + "\n");
        detailsText.append("Departure Date: " + flight.getDepartureDate() + "\n");
        detailsText.append("Passengers:\n");

        // Append first class passengers
        for (Seat seat : flight.getFirstClassSeats()) {
            if (seat.isBooked()) {
                detailsText.append("First Class Seat " + seat.getSeatNumber() + ": " + seat.getPassenger().getName() + "\n");
            }
        }
        // Append business class passengers
        for (Seat seat : flight.getBusinessClassSeats()) {
            if (seat.isBooked()) {
                detailsText.append("Business Class Seat " + seat.getSeatNumber() + ": " + seat.getPassenger().getName() + "\n");
            }
        }
        // Append economy class passengers
        for (Seat seat : flight.getEconomyClassSeats()) {
            if (seat.isBooked()) {
                detailsText.append("Economy Class Seat " + seat.getSeatNumber() + ": " + seat.getPassenger().getName() + "\n");
            }
        }

        detailsFrame.add(new JScrollPane(detailsText));
        detailsFrame.setVisible(true);
    }

    // Sets up the user switcher interface
    private void initializeUserSwitcher() {
        System.out.println("Initializing user switcher with user count: " + userManager.getUsers().size());

        JPanel panel = new JPanel(new FlowLayout()); // Using FlowLayout for simplicity
        userDropdown = new JComboBox<>(new Vector<>(userManager.getUsers())); // Populate dropdown with users
        JButton switchUserButton = new JButton("Switch User");
        JButton cancelFlightButton = new JButton("Cancel Flight");

        // Action listener for canceling a flight
        cancelFlightButton.addActionListener(e -> {
            JFrame cancelFrame = new JFrame("Cancel Booking");
            cancelFrame.setSize(300, 100);
            cancelFrame.setLocationRelativeTo(null);
            cancelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel2 = new JPanel();
            cancelFrame.add(panel2);
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

            JLabel bookingLabel = new JLabel("Booking Reference:");
            JTextField bookingText = new JTextField(20);
            JButton cancelBookingButton = new JButton("Cancel Booking");

            // Action listener for booking cancellation
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

                Flight flight = binarySearchFlightByNumber(flightNumber);

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

        // Action listener for switching users
        switchUserButton.addActionListener(e -> {
            Passenger selectedUser = (Passenger) userDropdown.getSelectedItem();
            userManager.setActiveUser(selectedUser);
            JOptionPane.showMessageDialog(this, "Switched to user: " + selectedUser.getName());
        });

        panel.add(new JLabel("Switch User:"));
        panel.add(userDropdown);
        panel.add(switchUserButton);

        add(panel, BorderLayout.SOUTH); // Add panel to the bottom of the window
    }

    // Creates random flight data
    private void createFlights(int count) {
        Random rand = new Random();
        allFlights.clear(); // Clear previous flights

        // Sample airport codes
        String[] airports = {"JFK", "LAX", "ORD", "DFW", "DEN", "SFO", "SEA", "ATL", "MIA", "BOS"};

        // Generate random flights
        for (int i = 0; i < count; i++) {
            int flightNumber = rand.nextInt(900) + 100; // Ensure flight number is between 100 and 999

            // Randomly select departure and arrival airports
            String departureAirport = airports[rand.nextInt(airports.length)];
            String arrivalAirport;
            do {
                arrivalAirport = airports[rand.nextInt(airports.length)];
            } while (departureAirport.equals(arrivalAirport)); // Ensure departure and arrival are different

            // Random date within the next 30 days
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, rand.nextInt(30)); // Add a random number of days to the current date
            Date departureDate = calendar.getTime();

            // Random seat capacities
            int firstClassSeats = rand.nextInt(10) + 1; // 1 to 10 seats
            int businessClassSeats = rand.nextInt(20) + 5; // 5 to 25 seats
            int economyClassSeats = rand.nextInt(50) + 20; // 20 to 70 seats

            Flight flight = new Flight(flightNumber, departureAirport, departureDate, arrivalAirport,
                    firstClassSeats, businessClassSeats, economyClassSeats);
            allFlights.add(flight);
        }

        sortFlights(allFlights); // Sort flights using merge sort
        updateFlightList(allFlights);
    }

    // Updates the flight list model
    private void updateFlightList(List<Flight> flights) {
        flightListModel.clear();
        flights.forEach(flightListModel::addElement);
    }

    // Binary search implementation for finding flights
    private List<Flight> binarySearchFlights(int flightNumber) {
        int left = 0;
        int right = allFlights.size() - 1;
        List<Flight> result = new ArrayList<>();

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Flight midFlight = allFlights.get(mid);

            if (midFlight.getFlightNumber() == flightNumber) {
                result.add(midFlight);
                // Search for any additional flights with the same flight number
                int leftIndex = mid - 1;
                while (leftIndex >= left && allFlights.get(leftIndex).getFlightNumber() == flightNumber) {
                    result.add(allFlights.get(leftIndex));
                    leftIndex--;
                }
                int rightIndex = mid + 1;
                while (rightIndex <= right && allFlights.get(rightIndex).getFlightNumber() == flightNumber) {
                    result.add(allFlights.get(rightIndex));
                    rightIndex++;
                }
                break;
            } else if (midFlight.getFlightNumber() < flightNumber) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Binary search implementation for finding a single flight by number
    private Flight binarySearchFlightByNumber(int flightNumber) {
        int left = 0;
        int right = allFlights.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Flight midFlight = allFlights.get(mid);

            if (midFlight.getFlightNumber() == flightNumber) {
                return midFlight;
            } else if (midFlight.getFlightNumber() < flightNumber) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null; // Flight not found
    }

    // Merge sort implementation for sorting flights
    public void sortFlights(List<Flight> list) {
        if (list.size() > 1) {
            int mid = list.size() / 2;
            List<Flight> leftHalf = new ArrayList<>(list.subList(0, mid));
            List<Flight> rightHalf = new ArrayList<>(list.subList(mid, list.size()));

            sortFlights(leftHalf);
            sortFlights(rightHalf);

            merge(list, leftHalf, rightHalf);
        }
    }

    // Merges two halves of a list during merge sort
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
