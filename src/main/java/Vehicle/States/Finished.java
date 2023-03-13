package Vehicle.States;

import Network.Location.Location;
import Network.Route.Route;
import Vehicle.Vehicle;

public class Finished extends State {
    private Route route;
    private Location location;

    public Finished(Vehicle vehicle, Route route, Location location) {
        super(vehicle);
        this.route = route;
        this.location = location;
    }
}
