package Company;

import Network.Location.City;
import Network.Location.Port;
import Network.Network;
import Vehicle.Seed.FleetSeed;
import Vehicle.States.EnRoute;
import Vehicle.States.Finished;
import Vehicle.States.Idle;
import Vehicle.States.State;
import Vehicle.Vehicle;
import Vehicle.Ship;
import Vehicle.Semi;
import Vehicle.Van;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Company {

    public Network network;
    private CompanySeed companySeed;
    private FleetSeed fleetSeed;
    private List<Hub> hubs = new ArrayList<>();
    private List<GlobalHub> globalHubs = new ArrayList<>();
    private HashMap<Integer, GlobalHub> globalHubsByLocation = new HashMap<>();
    private List<RegionHub> regionHubs = new ArrayList<>();
    private HashMap<List<Integer>, RegionHub> regionHubsByLocation = new HashMap<>();
    private CompanyTypeVehicles ships;
    private CompanyTypeVehicles semis;
    private CompanyTypeVehicles vans;

    public Company(Network network, CompanySeed companySeed, FleetSeed fleetSeed) {
        this.network = network;
        this.companySeed = companySeed;
        this.fleetSeed = fleetSeed;
        this.makeHubs(network, companySeed);
        this.makeVehicles(network, companySeed, fleetSeed);

    }

    public void makeHubs(Network network, CompanySeed companySeed) {
        System.out.println();
        List<Integer> seedGlobalHubs = companySeed.getGlobalHubs();
        for (Integer globalHubIndex : seedGlobalHubs) {
            Port port = network.getPorts().get(globalHubIndex);
            GlobalHub globalHub = new GlobalHub(this, port);
            port.addHub(globalHub);
            this.hubs.add(globalHub);
            this.globalHubs.add(globalHub);
            this.globalHubsByLocation.put(globalHubIndex, globalHub);
        }

        List<List<Integer>> seedRegionHubs = companySeed.getRegionalHubs();
        for (List<Integer> regionHubIndex : seedRegionHubs) {
            int hubPort = regionHubIndex.get(0);
            int hubCity = regionHubIndex.get(1);
            City city = network.getCitiesByLocation().get(hubPort).get(hubCity);
            RegionHub regionHub = new RegionHub(this, city);
            city.addHub(regionHub);
            this.hubs.add(regionHub);
            this.regionHubs.add(regionHub);
            this.regionHubsByLocation.put(List.of(hubPort, hubCity), regionHub);
        }
    }

    public void makeVehicles(Network network, CompanySeed companySeed, FleetSeed fleetSeed) {

        List<Ship> ships = new ArrayList<>();
        List<Integer> shipsLocations = companySeed.getShipLocations();
        for (int i = 0; i < shipsLocations.size(); i++) {
            int portIndex = shipsLocations.get(i);
            Port shipLocation = network.getPorts().get(portIndex);
            Ship ship = new Ship(this, shipLocation, fleetSeed.getShipSeed());
            ships.add(ship);
            Hub hub = globalHubsByLocation.get(portIndex);
            hub.addVehicle(ship);
            ship.setHub(hub);

        }
        this.ships = new CompanyTypeVehicles(ships);

        List<Semi> semis = new ArrayList<Semi>();
        List<Integer> semiInGlobalLocations = companySeed.getGlobalSemiLocations();
        for (int i = 0; i < semiInGlobalLocations.size(); i++) {
            int portIndex = semiInGlobalLocations.get(i);
            Port port = network.getPorts().get(portIndex);
            Vehicle semi = new Semi(this, port, fleetSeed.getSemiSeed());
            semis.add((Semi) semi);
            Hub hub = globalHubsByLocation.get(portIndex);
            hub.addVehicle(semi);
            semi.setHub(hub);
        }

        List<List<Integer>> semiInRegionalLocations = companySeed.getRegionalSemiLocations();
        for (List<Integer> semiInRegionalLocation : semiInRegionalLocations) {
            int port = semiInRegionalLocation.get(0);
            int city = semiInRegionalLocation.get(1);
            City cityLocation = network.getCitiesByLocation().get(port).get(city);
            Vehicle semi = new Semi(this, cityLocation, fleetSeed.getSemiSeed());
            semis.add((Semi) semi);
            Hub hub = regionHubsByLocation.get(List.of(port, city));
            hub.addVehicle(semi);
            semi.setHub(hub);
        }
        this.semis = new CompanyTypeVehicles(semis);

        List<Van> vans = new ArrayList<Van>();
        List<List<Integer>> vanInRegionalLocations = companySeed.getVanLocations();
        for (List<Integer> vanInRegionalLocation : vanInRegionalLocations) {
            int port = vanInRegionalLocation.get(0);
            int city = vanInRegionalLocation.get(1);
            City cityLocation = network.getCitiesByLocation().get(port).get(city);
            Vehicle van = new Van(this, cityLocation, fleetSeed.getVanSeed());
            vans.add((Van) van);
            Hub hub = regionHubsByLocation.get(List.of(port, city));
            hub.addVehicle(van);
            van.setHub(hub);
        }
        this.vans = new CompanyTypeVehicles(vans);
    }

    public CompanySeed getSeed() {
        return companySeed;
    }

    public List<GlobalHub> getGlobalHubs() {
        return globalHubs;
    }

    public List<RegionHub> getRegionHubs() {
        return regionHubs;
    }

    public CompanyTypeVehicles getShips() {
        return ships;
    }

    public CompanyTypeVehicles getSemis() {
        return semis;
    }

    public CompanyTypeVehicles getVans() {
        return vans;
    }
}

class CompanyTypeVehicles<V extends Vehicle> {

    private List<V> vehicles = new ArrayList<>();
    private List<V> idlingVehicles = new ArrayList<>();
    private List<V> enRouteVehicles = new ArrayList<>();
    private List<V> finishedVehicles = new ArrayList<>();

    CompanyTypeVehicles(List<V> vehicles) {
        this.vehicles = vehicles;
        for (V vehicle : vehicles) {
            this.updateVehicle(vehicle);
        }
    }

    public void updateVehicle(V vehicle) {
        State state = vehicle.getState();

        boolean found = idlingVehicles.remove(vehicle);
        if (!found) {
            found = enRouteVehicles.remove(vehicle);
            if (!found) {
                finishedVehicles.remove(vehicle);
            }
        }

        if (state instanceof Idle) {
            this.idlingVehicles.add(vehicle);
        } else if (state instanceof EnRoute) {
            this.enRouteVehicles.add(vehicle);
        } else if (state instanceof Finished) {
            this.finishedVehicles.add(vehicle);
        }
    }

    public List<V> getEnRouteVehicles() {
        return enRouteVehicles;
    }

    public List<V> getIdlingVehicles() {
        return idlingVehicles;
    }

    public List<V> getFinishedVehicles() {
        return finishedVehicles;
    }

    public List<V> getVehicles() {
        return vehicles;
    }
}