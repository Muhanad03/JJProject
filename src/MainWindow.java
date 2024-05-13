import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private DefaultListModel<Flight> flightListModel;
    private JList<Flight> flightList;
    private List<Flight> allFlights = new ArrayList<>();  // This will store all generated flights

    Passenger passenger;

    public MainWindow(Passenger passenger) {
        setTitle("Flight Scheduler");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        this.passenger = passenger;
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
                    new BookingWindow(flightListModel.getElementAt(index),passenger).setVisible(true);
                }
            }
        });
    }
    public void mergeSort(List<Flight> list) {
        if (list.size() > 1) {
            // Split the array in half
            int mid = list.size() / 2;
            List<Flight> leftHalf = new ArrayList<>(list.subList(0, mid));
            List<Flight> rightHalf = new ArrayList<>(list.subList(mid, list.size()));

            // Recursively sort each half
            mergeSort(leftHalf);
            mergeSort(rightHalf);

            // Merge the halves together, overwriting the original array
            int i = 0, j = 0, k = 0;
            while (i < leftHalf.size() && j < rightHalf.size()) {
                if (leftHalf.get(i).getFlightNumber() < rightHalf.get(j).getFlightNumber()) {
                    list.set(k, leftHalf.get(i));
                    i++;
                } else {
                    list.set(k, rightHalf.get(j));
                    j++;
                }
                k++;
            }

            // Copy the remaining elements of both halves back to the list
            while (i < leftHalf.size()) {
                list.set(k, leftHalf.get(i));
                i++;
                k++;
            }

            while (j < rightHalf.size()) {
                list.set(k, rightHalf.get(j));
                j++;
                k++;
            }
        }
    }

    private Flight binarySearchForFlight(int flightNumber) {
        int low = 0;
        int high = flightListModel.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Flight midVal = flightListModel.getElementAt(mid);
            if (midVal.getFlightNumber() < flightNumber)
                low = mid + 1;
            else if (midVal.getFlightNumber() > flightNumber)
                high = mid - 1;
            else
                return midVal; // Flight found
        }
        return null; // Flight not found
    }

    private void generateFlights(int count) {
        Random rand = new Random();
        allFlights.clear();  // Clear previous data
        for (int i = 0; i < count; i++) {
            int flightNumber = rand.nextInt(1000) + 100; // Ensure flight number is at least three digits
            Flight flight = new Flight(flightNumber, "Airport A", new Date(), "Airport B", 1, 1, 1);
            allFlights.add(flight);
        }
        mergeSort(allFlights); // Sort the flights using merge sort
        updateFlightListModel(allFlights);
    }

    private void updateFlightListModel(List<Flight> flights) {
        flightListModel.clear();
        flights.forEach(flightListModel::addElement);
    }

}
