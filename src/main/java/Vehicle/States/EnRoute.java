package Vehicle.States;

import Network.Location.Location;
import Network.Route.Route;
import Product.Product;
import Vehicle.Vehicle;

import java.util.List;

public class EnRoute extends State {
    private final Route route;
    private final Location origin;
    private final Location destination;


    public EnRoute(Route route, Location origin, Location destination) {
        super();
        this.route = route;
        this.origin = origin;
        this.destination = destination;
    }


}
