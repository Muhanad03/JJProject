public class Passenger {

    private int id;
    private String name;

    public Passenger(String name, int id) {

        this.id = id;
        this.name = name;
    }

    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return name;
    }
}
