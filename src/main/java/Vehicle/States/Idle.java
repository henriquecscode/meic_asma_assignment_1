package Vehicle.States;

import Network.Location.Location;
import Vehicle.Vehicle;

public class Idle extends State {
    private final Location location;


    public Idle(Location location) {
        super();
        this.location = location;
    }
}
