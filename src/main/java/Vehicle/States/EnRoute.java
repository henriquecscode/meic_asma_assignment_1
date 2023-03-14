package Vehicle.States;

import Network.Location.Location;
import Network.Route.Route;
import Product.Product;
import Vehicle.Vehicle;

import java.util.List;

public class EnRoute extends State {
    private Route route;
    private Location origin;
    private Location destination;

    public EnRoute(Vehicle vehicle) {
        super(vehicle);
    }

    public EnRoute(Vehicle vehicle, Route route, Location origin, Location destination, List<Product> cargo) {
        super(vehicle);
        this.route = route;
        this.origin = origin;
        this.destination = destination;
        this.setCargo(cargo);
    }

    public void setCargo(List<Product> cargo) {
        this.cargo = cargo;
    }

}
