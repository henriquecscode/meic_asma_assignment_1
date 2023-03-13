package Network.Location;

import Company.Hub;
import Network.Graph.Node;
import Network.Route.Route;

import java.util.ArrayList;
import java.util.List;

public class Location extends Node {
    private final String name;
    private final int level;

    private List<Hub> hubs = new ArrayList<>();
    //    Nodes of a higher of equal level
    private List<Location> upperLocations = new ArrayList<>();
    private List<Route> upperRoutes = new ArrayList<>();

    //    Nodes of a lower level
    private List<Location> lowerLocations = new ArrayList<>();
    private List<Route> lowerRoutes = new ArrayList<>();

    public Location(String name, int level) {
        super();
        this.name = name;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public List<Route> getUpperRoutes() {
        return upperRoutes;
    }

    public List<Location> getUpperLocations() {
        return upperLocations;
    }

    public List<Route> getLowerRoutes() {
        return lowerRoutes;
    }

    public List<Location> getLowerLocations() {
        return lowerLocations;
    }

    public void addRoute(Route route) {
        super.addEdge(route);
        Location from = (Location) route.getFrom();
        if (from == this) {
            Location to = (Location) route.getTo();
            if (to.getLevel() >= this.level) {
                this.upperLocations.add(to);
                this.upperRoutes.add(route);
            } else {
                this.lowerLocations.add(to);
                this.lowerRoutes.add(route);
            }
        } else {
            if (from.getLevel() >= this.level) {
                this.upperLocations.add(from);
                this.upperRoutes.add(route);
            } else {
                this.lowerLocations.add(from);
                this.lowerRoutes.add(route);
            }
        }
    }

    public List<Hub> getHubs() {
        return hubs;
    }

    public void addHub(Hub hub) {
        this.hubs.add(hub);
    }

    public boolean removeHub(Hub hub) {
        return this.hubs.remove(hub);
    }
}
