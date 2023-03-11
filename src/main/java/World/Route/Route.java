package World.Route;

import World.Graph.Edge;
import World.Location.Location;

public class Route extends Edge {
    private final int distance;

    public Route(Location from, Location to, int distance) {
        super(from, to);
        this.distance = distance;
        this.updateGraph();
    }

    @Override
    protected void updateGraph() {
        ((Location) this.getFrom()).addRoute(this);
        ((Location) this.getTo()).addRoute(this);
        super.updateGraph();
    }

    public int getDistance() {
        return distance;
    }
}
