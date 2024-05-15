import java.text.SimpleDateFormat;
import java.util.*;

public class Flight {
    private int flightNumber;
    private String departureAirport;
    private Date departureDate;
    private String arrivalAirport;
    private List<Seat> firstClassSeats;
    private List<Seat> businessClassSeats;
    private List<Seat> economyClassSeats;
    private Queue<Passenger> firstClassWaitList = new LinkedList<>();
    private Queue<Passenger> businessClassWaitList = new LinkedList<>();
    private Queue<Passenger> economyClassWaitList = new LinkedList<>();

    // Flight constructor to initialize flight details and generate seats
    public Flight(int flightNumber, String departureAirport, Date departureDate, String arrivalAirport,
                  int noOfFirstClassSeats, int noOfBusinessClassSeats, int noOfEconomyClassSeats) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.departureDate = departureDate;
        this.arrivalAirport = arrivalAirport;
        this.firstClassSeats = new ArrayList<>();
        this.businessClassSeats = new ArrayList<>();
        this.economyClassSeats = new ArrayList<>();
        generateSeats(noOfFirstClassSeats, noOfBusinessClassSeats, noOfEconomyClassSeats); // Generate seats for the flight
    }

    // Generate seats for each class type
    private void generateSeats(int firstClass, int businessClass, int economyClass) {
        for (int i = 1; i <= firstClass; i++) {
            this.firstClassSeats.add(new Seat(i, "First"));
        }
        for (int i = 1; i <= businessClass; i++) {
            this.businessClassSeats.add(new Seat(firstClass + i, "Business"));
        }
        for (int i = 1; i <= economyClass; i++) {
            this.economyClassSeats.add(new Seat(firstClass + businessClass + i, "Economy"));
        }
    }

    // Select the seat list based on class type
    private List<Seat> selectSeatList(String classType) {
        switch (classType.toLowerCase()) {
            case "first":
                return firstClassSeats;
            case "business":
                return businessClassSeats;
            case "economy":
                return economyClassSeats;
            default:
                return null;
        }
    }

    // Book a passenger in a specified class type
    public String bookPassenger(Passenger passenger, String classType) {
        List<Seat> seats = selectSeatList(classType);
        if (seats == null) {
            System.out.println("Invalid class type specified.");
            return null;
        }

        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                String bookingRef = generateBookingRef();
                seat.bookSeat(passenger, bookingRef);
                System.out.println("Booking successful. Reference number: " + bookingRef);
                return bookingRef;
            }
        }

        addToWaitList(passenger, classType); // Add to waitlist if no seats available
        return null;
    }

    // Generate a booking reference number
    private String generateBookingRef() {
        return flightNumber + "-" + UUID.randomUUID().toString().substring(0, 8); // Include flight number in the booking reference
    }

    // Handle the booking process for a passenger
    public void handlePassengerBooking(Passenger passenger, String classType) {
        if (isPassengerBookedAnywhere(passenger)) {
            System.out.println("Passenger is already booked or on a waitlist.");
            return;
        }

        String bookingRef = bookPassenger(passenger, classType);
        if (bookingRef == null) {
            int position = addToWaitList(passenger, classType);
            System.out.println("No available seats. Passenger added to waitlist at position: " + position);
        }
    }

    // Cancel a booking by reference number and notify the next passenger on the waitlist
    public boolean cancelBookingByRef(String bookingRef, Passenger currentLoggedPassenger) {
        Seat seat = findSeatByBookingRef(bookingRef);
        if (seat == null) {
            System.out.println("Booking reference not found.");
            return false;
        }
        if (seat.getPassenger() == currentLoggedPassenger) {
            seat.cancelSeat();
            System.out.println("Booking with reference " + bookingRef + " has been canceled.");
            Passenger nextPassenger = getNextPassengerFromWaitList(seat.getClassType());
            if (nextPassenger != null) {
                bookPassenger(nextPassenger, seat.getClassType());
                System.out.println("Next passenger in line: " + nextPassenger.getName());
            }
            return true;
        }
        return false;
    }

    // Find a seat by booking reference number
    private Seat findSeatByBookingRef(String bookingRef) {
        for (Seat seat : firstClassSeats) {
            if (bookingRef.equals(seat.getBookingRef())) {
                return seat;
            }
        }
        for (Seat seat : businessClassSeats) {
            if (bookingRef.equals(seat.getBookingRef())) {
                return seat;
            }
        }
        for (Seat seat : economyClassSeats) {
            if (bookingRef.equals(seat.getBookingRef())) {
                return seat;
            }
        }
        return null;
    }

    // Get the next passenger from the waitlist based on class type
    private Passenger getNextPassengerFromWaitList(String classType) {
        switch (classType.toLowerCase()) {
            case "first":
                return firstClassWaitList.poll();
            case "business":
                return businessClassWaitList.poll();
            case "economy":
                return economyClassWaitList.poll();
            default:
                return null;
        }
    }

    // Check if a passenger is booked in any seat
    public boolean checkIfPassengerIsBookedIn(Passenger passenger) {
        return isPassengerInSeatsList(firstClassSeats, passenger) ||
                isPassengerInSeatsList(businessClassSeats, passenger) ||
                isPassengerInSeatsList(economyClassSeats, passenger);
    }

    // Helper method to check if a passenger is in a list of seats
    private boolean isPassengerInSeatsList(List<Seat> seats, Passenger passenger) {
        for (Seat seat : seats) {
            if (seat.isBooked() && seat.getPassenger().equals(passenger)) {
                return true;
            }
        }
        return false;
    }

    // Get the number of available seats for a given class
    public int getAvailableSeats(String classType) {
        List<Seat> seats = selectSeatList(classType);
        if (seats == null) {
            System.out.println("Invalid class type specified.");
            return 0;
        }

        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                count++;
            }
        }
        return count;
    }

    // Add a passenger to the waitlist for a given class type
    public int addToWaitList(Passenger passenger, String classType) {
        if (isPassengerBookedAnywhere(passenger)) {
            System.out.println("Passenger is already booked. Cannot add to waitlist.");
            return -1; // Indicate failure to add to waitlist
        }

        Queue<Passenger> waitList = getWaitList(classType);
        if (waitList == null) {
            System.out.println("Invalid class type for waitlist: " + classType);
            return -1;
        }

        if (!waitList.contains(passenger)) {
            waitList.add(passenger);
        }
        return new ArrayList<>(waitList).indexOf(passenger) + 1;
    }

    // Get the waitlist for a given class type
    private Queue<Passenger> getWaitList(String classType) {
        switch (classType.toLowerCase()) {
            case "first":
                return firstClassWaitList;
            case "business":
                return businessClassWaitList;
            case "economy":
                return economyClassWaitList;
            default:
                return null;
        }
    }

    // Remove a passenger from the waitlist
    public boolean removeFromWaitList(Passenger passenger) {
        boolean removed = false;
        Passenger nextPassenger = null;

        if (firstClassWaitList.remove(passenger)) {
            removed = true;
            nextPassenger = firstClassWaitList.peek();
        } else if (businessClassWaitList.remove(passenger)) {
            removed = true;
            nextPassenger = businessClassWaitList.peek();
        } else if (economyClassWaitList.remove(passenger)) {
            removed = true;
            nextPassenger = economyClassWaitList.peek();
        }

        if (removed) {
            System.out.println("Passenger removed from waitlist.");
            if (nextPassenger != null) {
                System.out.println("Next passenger in line: " + nextPassenger.getName());
            }
        } else {
            System.out.println("Passenger was not found in any waitlist.");
        }

        return removed;
    }

    // Check if a passenger is on any waitlist
    public boolean isPassengerInWaitlist(Passenger passenger) {
        return firstClassWaitList.contains(passenger) ||
                businessClassWaitList.contains(passenger) ||
                economyClassWaitList.contains(passenger);
    }

    // Check if a passenger is booked anywhere (in seats or waitlists)
    public boolean isPassengerBookedAnywhere(Passenger passenger) {
        return isPassengerInSeatsList(firstClassSeats, passenger) ||
                isPassengerInSeatsList(businessClassSeats, passenger) ||
                isPassengerInSeatsList(economyClassSeats, passenger) ||
                isPassengerInWaitlist(passenger);
    }

    // toString method to provide a string representation of the flight
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Flight No: " + flightNumber +
                ", Date: " + sdf.format(departureDate) +
                ", From: " + departureAirport +
                " to " + arrivalAirport;
    }

    // Placeholder for cancel booking method, not yet implemented
    public void cancelBooking(Passenger passenger) {

    }

    // Getters and setters for flight details
    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public List<Seat> getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(List<Seat> firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public List<Seat> getBusinessClassSeats() {
        return businessClassSeats;
    }

    public void setBusinessClassSeats(List<Seat> businessClassSeats) {
        this.businessClassSeats = businessClassSeats;
    }

    public List<Seat> getEconomyClassSeats() {
        return economyClassSeats;
    }

    public void setEconomyClassSeats(List<Seat> economyClassSeats) {
        this.economyClassSeats = economyClassSeats;
    }
}
