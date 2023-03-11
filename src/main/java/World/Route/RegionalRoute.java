package World.Route;

import World.Location.City;
import World.Location.Port;

public class RegionalRoute extends Route {
    public RegionalRoute(City from , Port to, int distance) {
        super(from, to, distance);
    }

    public RegionalRoute(Port from, City to, int distance) {
        super(from, to, distance);
    }
}
