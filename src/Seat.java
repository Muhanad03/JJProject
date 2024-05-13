public class Seat {
    private int seatNumber;
    private boolean isBooked;
    private Passenger passenger;



    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isBooked = false;
        this.passenger = null;
    }

    public void bookSeat(Passenger passenger) {
        this.isBooked = true;
        this.passenger = passenger;
    }

    public void cancelSeat() {
        this.isBooked = false;
        this.passenger = null;
    }



    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }



    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
