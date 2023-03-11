package Network.Route;

import Network.Location.City;
import Network.Location.House;

public class NeighbourhoodRoute extends Route {

    public NeighbourhoodRoute(House from, City to, int distance) {
        super(from, to, distance);
    }

    public NeighbourhoodRoute(City from, House to, int distance) {
        super(from, to, distance);
    }

}
