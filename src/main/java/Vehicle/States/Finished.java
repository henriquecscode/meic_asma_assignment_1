package Vehicle.States;

import Network.Location.Location;
import Network.Route.Route;
import Vehicle.Vehicle;

public class Finished extends State {
    private Route route;
    private Location location;

    public Finished(Route route, Location location) {
        super();
        this.route = route;
        this.location = location;
    }
}
