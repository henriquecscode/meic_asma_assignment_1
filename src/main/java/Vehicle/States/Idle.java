package Vehicle.States;

import Network.Location.Location;
import Vehicle.Vehicle;

public class Idle extends State {
    private Location location;

    public Idle(Vehicle vehicle) {
        super(vehicle);
    }

    public Idle(Vehicle vehicle, Location location) {
        super(vehicle);
        this.location = location;
    }
}
