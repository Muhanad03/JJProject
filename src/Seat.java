public class Seat {
    private int seatNumber;
    private boolean isBooked;
    private Passenger passenger;
    private String bookingRef; // Added booking reference
    private String classType;  // Added class type for tracking

    public Seat(int seatNumber, String classType) {
        this.seatNumber = seatNumber;
        this.classType = classType;
        this.isBooked = false;
        this.passenger = null;
        this.bookingRef = null;
    }

    public void bookSeat(Passenger passenger, String bookingRef) {
        this.isBooked = true;
        this.passenger = passenger;
        this.bookingRef = bookingRef;
    }

    public void cancelSeat() {
        this.isBooked = false;
        this.passenger = null;
        this.bookingRef = null;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public String getClassType() {
        return classType;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
}
