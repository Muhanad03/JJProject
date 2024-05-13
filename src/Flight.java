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

    public Flight(int flightNumber, String departureAirport, Date departureDate, String arrivalAirport, int NoOfFirstClassSeats,
                  int NoOfbusinessClassSeats,int NoOfeconomyClassSeats) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.departureDate = departureDate;
        this.arrivalAirport = arrivalAirport;
        this.firstClassSeats = new ArrayList<>();
        this.businessClassSeats = new ArrayList<>();
        this.economyClassSeats = new ArrayList<>();
        GenerateSeats(NoOfbusinessClassSeats,NoOfeconomyClassSeats,NoOfFirstClassSeats);
    }

    private void GenerateSeats(int firstClass, int businessClass, int economyClass) {
        for (int i = 1; i <= firstClass; i++) {
            this.firstClassSeats.add(new Seat(i));
        }
        for (int i = 1; i <= businessClass; i++) {
            this.businessClassSeats.add(new Seat(firstClass + i));
        }
        for (int i = 1; i <= economyClass; i++) {
            this.economyClassSeats.add(new Seat(firstClass + businessClass + i));
        }
    }
    private List<Seat> selectSeatList(String classType) {
        if (classType.equalsIgnoreCase("first")) {
            return firstClassSeats;
        } else if (classType.equalsIgnoreCase("business")) {
            return businessClassSeats;
        } else if (classType.equalsIgnoreCase("economy")) {
            return economyClassSeats;
        }
        return null; // Return null if an invalid class type is specified
    }

    public boolean bookPassenger(Passenger passenger, String classType) {
        List<Seat> seats = selectSeatList(classType.toLowerCase());
        if (seats == null) {
            System.out.println("Invalid class type specified.");
            return false;
        }

        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                seat.bookSeat(passenger);
                return true;
            }
        }


        addToWaitList(passenger, classType);
        return false;
    }

    //Linear search small data size
    public boolean checkIfPassengerIsBookedIn(Passenger passenger) {
        // Check all seat lists to see if the passenger is already booked.
        return isPassengerInSeatsList(firstClassSeats, passenger) ||
                isPassengerInSeatsList(businessClassSeats, passenger) ||
                isPassengerInSeatsList(economyClassSeats, passenger);
    }

    private boolean isPassengerInSeatsList(List<Seat> seats, Passenger passenger) {
        for (Seat seat : seats) {
            if (seat.isBooked() && seat.getPassenger().equals(passenger)) {
                return true; // The passenger is already booked in one of the seats.
            }
        }
        return false; // The passenger is not found in this class of seats.
    }

    public int getAvailableSeats(String ClassType){

        int count = 0;
        switch (ClassType.toLowerCase()) {
            case "first":

                for(int i = 0 ;i<firstClassSeats.stream().count();i++){

                    Seat seat = firstClassSeats.get(i);
                    if(seat.isBooked() == false){
                        count ++;
                    }
                }

                break;

            case "business":
                for(int i = 0 ;i<businessClassSeats.stream().count();i++){

                    Seat seat = businessClassSeats.get(i);
                    if(seat.isBooked() == false){
                        count ++;
                    }
                }
                break;
            case "economy":
                for(int i = 0 ;i<economyClassSeats.stream().count();i++){

                    Seat seat = economyClassSeats.get(i);
                    if(seat.isBooked() == false){
                        count ++;
                    }
                }
                break;
        }

        return count;

    }


    public int addToWaitList(Passenger passenger, String classType) {
        switch (classType.toLowerCase()) {
            case "first":
                firstClassWaitList.add(passenger);
                break;
            case "business":
                businessClassWaitList.add(passenger);
                break;
            case "economy":
                economyClassWaitList.add(passenger);
                break;
        }
        return 0;
    }

    public void cancelBooking(Passenger passenger) {

    }

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

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Flight No: " + flightNumber +
                ", Date: " + sdf.format(departureDate) +
                ", From: " + departureAirport +
                " to " + arrivalAirport;
    }


}
