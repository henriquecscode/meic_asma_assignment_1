package Company;

import Network.Location.City;
import Network.Location.House;
import Network.Location.Location;
import Network.Location.Port;
import Network.Network;
import Network.Route.Route;
import Vehicle.Seed.FleetSeed;
import Vehicle.States.*;
import Vehicle.Vehicle;
import Vehicle.Ship;
import Vehicle.Semi;
import Vehicle.Van;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Company {
    public static int companyId = 0;
    private final int id;
    private final String name;

    public final Network network;
    private final CompanySeed companySeed;
    private final List<Hub> hubs = new ArrayList<>();
    private final List<GlobalHub> globalHubs = new ArrayList<>();
    private final HashMap<Integer, GlobalHub> globalHubsByLocation = new HashMap<>();
    private final List<RegionHub> regionHubs = new ArrayList<>();
    private final HashMap<List<Integer>, RegionHub> regionHubsByLocation = new HashMap<>();
    private CompanyTypeVehicles<Ship> ships;
    private CompanyTypeVehicles<Semi> semis;
    private CompanyTypeVehicles<Van> vans;

    public Company(Network network, CompanySeed companySeed, FleetSeed fleetSeed) {
        this.id = companyId++;
        this.name = "Company" + this.id;
        this.network = network;
        this.companySeed = companySeed;
        this.makeHubs(network, companySeed);
        this.makeVehicles(network, companySeed, fleetSeed);
    }

    public void makeHubs(Network network, CompanySeed companySeed) {
        System.out.println();
        List<Integer> seedGlobalHubs = companySeed.getGlobalHubs();
        for (Integer globalHubIndex : seedGlobalHubs) {
            Port port = network.getPorts().get(globalHubIndex);
            GlobalHub globalHub = new GlobalHub(this, port);
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
            GlobalHub hub = globalHubsByLocation.get(portIndex);
            hub.addVehicle(ship);
            ship.setHub(hub);
            ship.setLocation(shipLocation);

        }
        this.ships = new CompanyTypeVehicles<Ship>(ships);

        List<Semi> semis = new ArrayList<>();
        List<Integer> semiInGlobalLocations = companySeed.getGlobalSemiLocations();
        for (int i = 0; i < semiInGlobalLocations.size(); i++) {
            int portIndex = semiInGlobalLocations.get(i);
            Port port = network.getPorts().get(portIndex);
            Semi semi = new Semi(this, port, fleetSeed.getSemiSeed());
            semis.add(semi);
            GlobalHub hub = globalHubsByLocation.get(portIndex);
            hub.addVehicle(semi);
            semi.setHub(hub);
            semi.setLocation(port);
        }

        List<List<Integer>> semiInRegionalLocations = companySeed.getRegionalSemiLocations();
        for (List<Integer> semiInRegionalLocation : semiInRegionalLocations) {
            int port = semiInRegionalLocation.get(0);
            int city = semiInRegionalLocation.get(1);
            City cityLocation = network.getCitiesByLocation().get(port).get(city);
            Semi semi = new Semi(this, cityLocation, fleetSeed.getSemiSeed());
            semis.add(semi);
            RegionHub hub = regionHubsByLocation.get(List.of(port, city));
            hub.addVehicle(semi);
            semi.setHub(hub);
            semi.setLocation(cityLocation);
        }
        this.semis = new CompanyTypeVehicles<Semi>(semis);

        List<Van> vans = new ArrayList<>();
        List<List<Integer>> vanInRegionalLocations = companySeed.getVanLocations();
        for (List<Integer> vanInRegionalLocation : vanInRegionalLocations) {
            int port = vanInRegionalLocation.get(0);
            int city = vanInRegionalLocation.get(1);
            City cityLocation = network.getCitiesByLocation().get(port).get(city);
            Van van = new Van(this, cityLocation, fleetSeed.getVanSeed());
            vans.add(van);
            RegionHub hub = regionHubsByLocation.get(List.of(port, city));
            hub.addVehicle(van);
            van.setHub(hub);
            van.setLocation(cityLocation);
        }
        this.vans = new CompanyTypeVehicles<Van>(vans);
    }

    public String getName() {
        return name;
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

    public CompanyTypeVehicles<Ship> getShips() {
        return ships;
    }

    public CompanyTypeVehicles<Semi> getSemis() {
        return semis;
    }

    public CompanyTypeVehicles<Van> getVans() {
        return vans;
    }

    public Network getNetwork() {
        return network;
    }

    public Integer getPricePerVehicle() {
        // TODO discuss how should this be set
        return 1000;
    }

    private List<?> filterIdleVehicles(List<?> vehicles) {
        return vehicles.stream().filter(v -> ((Vehicle) v).getState() instanceof Idle).collect(Collectors.toList());
    }

    // This is called to get the number of vehicles ready to go from start to end (being them directly connected)
    public List<?> getVehiclesInAdjacentLocations(Location start, Location end) {
        if (start instanceof Port) {
            Port port = (Port) start;
            List<Hub> hubs = port.getHubs();
            GlobalHub companyGlobalHub = null;
            for (Hub hub : hubs) {
                if (hub.getCompany() == this) {
                    companyGlobalHub = (GlobalHub) hub;
                    break;
                }
            }

            if (companyGlobalHub == null) {
                return new ArrayList<>();
            }

            if (end instanceof Port) {
                return filterIdleVehicles(companyGlobalHub.getShips());
            } else if (end instanceof City) {
                return filterIdleVehicles(companyGlobalHub.getSemis());
            } else {
                throw new IllegalArgumentException("Location end is not a valid type");
            }
        } else if (start instanceof City) {
            City city = (City) start;
            List<Hub> hubs = city.getHubs();
            RegionHub companyRegionHub = null;
            for (Hub hub : hubs) {
                if (hub.getCompany() == this) {
                    companyRegionHub = (RegionHub) hub;
                    break;
                }
            }

            if (companyRegionHub == null) {
                return new ArrayList<>();
            }

            if (end instanceof Port) {
                return filterIdleVehicles(companyRegionHub.getSemis());
            } else if (end instanceof House) {
                return filterIdleVehicles(companyRegionHub.getVans());
            } else {
                throw new IllegalArgumentException("Location end is not a valid type");
            }
        } else if (start instanceof House) {
            House house = (House) start;
            City city = (City) house.getUpperLocations().get(0);
            List<Hub> hubs = city.getHubs();
            RegionHub companyRegionHub = null;
            for (Hub hub : hubs) {
                if (hub.getCompany() == this) {
                    companyRegionHub = (RegionHub) hub;
                    break;
                }
            }

            if (companyRegionHub == null) {
                return new ArrayList<>();
            }

            if (end instanceof City) {
                return filterIdleVehicles(companyRegionHub.getVans());
            } else {
                throw new IllegalArgumentException("Location end is not a valid type");
            }
        } else {
            throw new IllegalArgumentException("Location start is not a valid type");
        }
    }

    public Hub findLocationHub(Location location) {
        if (location instanceof House) {
            return null;
        } else if (location instanceof City) {
            for (RegionHub regionHub : regionHubs) {
                if (regionHub.getLocation() == location) {
                    return regionHub;
                }
            }
        } else if (location instanceof Port) {
            for (GlobalHub globalHub : globalHubs) {
                if (globalHub.getLocation() == location) {
                    return globalHub;
                }
            }
        }
        return null;
    }

    public List<List<Hub>> findLocationIndirectHubs(Location location) {
        List<Hub> regionalHubs = new ArrayList<>();
        List<Hub> globalHubs = new ArrayList<>();
        if (location instanceof House) {
            Hub hub = findLocationHub(((House) location).getCity());
            if (hub != null) {
                regionalHubs.add(hub);
            }
        } else if (location instanceof City) {
            Hub hub = findLocationHub(((City) location).getPort());
            if (hub != null) {
                globalHubs.add(hub);
            }
        } else if (location instanceof Port) {
            for (Location port : location.getUpperLocations()) {
                Hub hub = findLocationHub(port);
                if (hub != null) {
                    globalHubs.add(hub);
                }
            }
            for (Location city : location.getLowerLocations()) {
                Hub hub = findLocationHub(city);
                if (hub != null) {
                    regionalHubs.add(hub);
                }
            }
        }
        return List.of(regionalHubs, globalHubs);
    }

    private RouteType getRouteType(Location start, Location end) {
        if (start instanceof Port && end instanceof Port) {
            return RouteType.International;
        } else if ((start instanceof Port && end instanceof City) || (start instanceof City && end instanceof Port)) {
            return RouteType.Regional;
        } else if ((start instanceof City && end instanceof House) || (start instanceof House && end instanceof City)) {
            return RouteType.Neighbour;
        }
        throw new IllegalArgumentException("Invalid route type");
    }


    private List<Vehicle> getFittingVehicleInLocation(List<Vehicle> vehicles, Location location, int cargoSize) {
        List<Vehicle> fittingVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getLocation() == location && vehicle.canFillUpCargo(cargoSize)) {
                fittingVehicles.add(vehicle);
            }
        }
        return fittingVehicles;
    }

    private List<Van> findVehicleAvailableForNeighbourhoodRoute(Location start, Location end, int cargoSize) {
        RegionHub hub;
        if (start instanceof House) {
            hub = (RegionHub) findLocationHub(((House) start).getCity());
        } else {
            hub = (RegionHub) findLocationHub(start);
        }
        if (hub == null) {
            throw new IllegalArgumentException("Company got contract for route that it does not control");
        }
        List<Van> hubVans = hub.getVans();
        List<Van> idlingVans = vans.getIdlingVehicles();
        //intersect both
        List<Vehicle> availableVans = idlingVans.stream().filter(hubVans::contains).collect(Collectors.toList());
        List<Vehicle> fittingVans = getFittingVehicleInLocation(availableVans, start, cargoSize);
        return fittingVans.stream().map(v -> (Van) v).collect(Collectors.toList());
    }


    private List<Semi> findSemiInHub(Hub hub, int cargoSize) {
        List<Semi> hubSemis;
        if (hub instanceof RegionHub) {
            hubSemis = ((RegionHub) hub).getSemis();
        } else {
            hubSemis = ((GlobalHub) hub).getSemis();
        }
        List<Semi> idlingSemis = semis.getIdlingVehicles();
        //intersect both
        List<Vehicle> availableSemis = idlingSemis.stream().filter(hubSemis::contains).collect(Collectors.toList());
        List<Vehicle> fittingSemis = getFittingVehicleInLocation(availableSemis, hub.getLocation(), cargoSize);
        return fittingSemis.stream().map(v -> (Semi) v).collect(Collectors.toList());
    }

    private List<Semi> findVehicleAvailableForRegionalRoute(Location start, Location end, int cargoSize) {
        RegionHub regionHub;
        GlobalHub globalHub;
        List<Semi> semis;
        if (start instanceof City) {
            regionHub = (RegionHub) findLocationHub(start);
            globalHub = (GlobalHub) findLocationHub(((City) start).getPort());
        } else {
            regionHub = (RegionHub) findLocationHub(end);
            globalHub = (GlobalHub) findLocationHub(((City) end).getPort());
        }
        if (regionHub != null) {
            semis = findSemiInHub(regionHub, cargoSize);
            if (!semis.isEmpty()) {
                return semis;
            }
        }
        if (globalHub != null) {
            semis = findSemiInHub(globalHub, cargoSize);
            if (!semis.isEmpty()) {
                return semis;
            }
        }
        return new ArrayList<>();
    }

    private List<Ship> findShipForPort(GlobalHub hub, int cargoSize) {
        List<Ship> hubShips = hub.getShips();
        List<Ship> idlingShips = ships.getIdlingVehicles();
        //intersect both
        List<Vehicle> availableShips = idlingShips.stream().filter(hubShips::contains).collect(Collectors.toList());
        List<Vehicle> fittingShips = getFittingVehicleInLocation(availableShips, hub.getLocation(), cargoSize);
        return fittingShips.stream().map(v -> (Ship) v).collect(Collectors.toList());
    }

    private List<Ship> findShipInPort(GlobalHub hub, int cargoSize) {
        List<Ship> hubShips = hub.getShips();
        List<Ship> idlingShips = ships.getIdlingVehicles();
        //intersect both
        List<Vehicle> availableShips = idlingShips.stream().filter(hubShips::contains).collect(Collectors.toList());
        List<Vehicle> fittingsShips = getFittingVehicleInLocation(availableShips, hub.getLocation(), cargoSize);
        return fittingsShips.stream().map(v -> (Ship) v).collect(Collectors.toList());
    }

    private List<Ship> findVehicleAvailableForInternationalRoute(Location start, Location end, int cargoSize) {
        GlobalHub hub;
        hub = (GlobalHub) findLocationHub(start);
        List<Ship> ships;
        if (hub != null) {
            ships = findShipInPort(hub, cargoSize);

            if (!ships.isEmpty()) {
                return ships;
            }
        }
        for (Location port : start.getUpperLocations()) {
            hub = (GlobalHub) findLocationHub(port);
            if (hub != null) {
                ships = findShipInPort(hub, cargoSize);
                if (!ships.isEmpty()) {
                    return ships;
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Vehicle> findVehicle(Location start, Location end, int cargoSize) {
        RouteType routeType = getRouteType(start, end);
        List<? extends Vehicle> vehicles;
        if (routeType == RouteType.Neighbour) {
            vehicles = findVehicleAvailableForNeighbourhoodRoute(start, end, cargoSize);
        } else if (routeType == RouteType.Regional) {
            vehicles = findVehicleAvailableForRegionalRoute(start, end, cargoSize);
        } else if (routeType == RouteType.International) {
            vehicles = findVehicleAvailableForInternationalRoute(start, end, cargoSize);
        } else {
            throw new IllegalArgumentException("Invalid route type");
        }
        return vehicles.stream().map(v -> (Vehicle) v).collect(Collectors.toList());
    }

    public List<Vehicle> findAnyVehicle(Location start, Location end, int cargoSize) {
        RouteType routeType = getRouteType(start, end);
        List<? extends Vehicle> vehicles = new ArrayList<>();
        if (routeType == RouteType.Neighbour) {
            RegionHub regionHub = null;
            if (start instanceof City) {
                regionHub = (RegionHub) findLocationHub(start);
            } else {
                regionHub = (RegionHub) findLocationHub(end);
            }
            if (regionHub != null) {
                vehicles = regionHub.getVans();
            }
            return vehicles.stream().map(v -> (Vehicle) v).collect(Collectors.toList());
        } else if (routeType == RouteType.Regional) {
            List<Semi> semis = new ArrayList<>();
            RegionHub regionHub = null;
            GlobalHub globalHub = null;
            if (start instanceof City) {
                regionHub = (RegionHub) findLocationHub(start);
                globalHub = (GlobalHub) findLocationHub(end);
            } else {
                regionHub = (RegionHub) findLocationHub(end);
                globalHub = (GlobalHub) findLocationHub(start);
            }
            if (regionHub != null) {
                semis.addAll(regionHub.getSemis());
            }
            if (globalHub != null) {
                semis.addAll(globalHub.getSemis());
            }
            return semis.stream().map(v -> (Vehicle) v).collect(Collectors.toList());
        } else if (routeType == RouteType.International) {
            GlobalHub g1 = (GlobalHub) findLocationHub(start);
            GlobalHub g2 = (GlobalHub) findLocationHub(end);
            List<Ship> ships = new ArrayList<>();
            if (g1 != null) {
                ships.addAll(g1.getShips());
            }
            if (g2 != null) {
                ships.addAll(g2.getShips());
            }
            ships = ships.stream().filter(s -> s.getCargoCapacity() >= cargoSize).collect(Collectors.toList());
            //return map ships to vehicle
            return ships.stream().map(v -> (Vehicle) v).collect(Collectors.toList());

        } else {
            throw new IllegalArgumentException("Invalid route type");
        }
    }

    public void updateVehicle(Vehicle vehicle) {
        CompanyTypeVehicles vehicles = null;
        if (vehicle instanceof Van) {
            vehicles = vans;
        } else if (vehicle instanceof Semi) {
            vehicles = semis;
        } else if (vehicle instanceof Ship) {
            vehicles = ships;
        } else {
            throw new IllegalArgumentException("Invalid vehicle type");
        }
        vehicles.updateVehicle(vehicle);
    }

    public Thread dispatchVehicle(Vehicle vehicle) {
        if (!(vehicle.getState() instanceof Holding)) {
            throw new IllegalArgumentException("Vehicle is not in holding state");
        }
        Dispatch dispatch = ((Holding) vehicle.getState()).getDispatch();
        Location start = dispatch.getStart();
        Location end = dispatch.getEnd();
        Route route = network.getRoute(start, end);

        vehicle.setLocation(null);
        vehicle.setState(new EnRoute(route, start, end));
        dispatch.setVehicleFilledUpCargo(vehicle.getFilledUpCargo());
        CompanyTypeVehicles vehicles = null;
        if (vehicle instanceof Van) {
            vehicles = vans;
        } else if (vehicle instanceof Semi) {
            vehicles = semis;
        } else /* (vehicle instanceof Ship) */ {
            vehicles = ships;
        }
        vehicles.updateVehicle(vehicle);

        CompanyTypeVehicles finalVehicles = vehicles;
        Thread vehicleDispatch = new Thread(() -> {
            try {
                long time = vehicle.getTravelTime(route);
//                System.out.println("Vehicle " + vehicle.getName() + "sent from " + start.getName() + " to " + end.getName() + " will arrive in" + time + " ms");
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            vehicle.setState(new Finished(route, end));
            vehicle.setLocation(end);
            finalVehicles.updateVehicle(vehicle);
        });
        vehicleDispatch.start();
        return vehicleDispatch;

    }

    public void holdVehicle(Vehicle vehicle, Dispatch dispatch) {
        vehicle.setState(new Holding(dispatch));
        updateVehicle(vehicle);
    }

    public void idleVehicle(Vehicle vehicle) {
        vehicle.setState(new Idle(vehicle.getLocation()));
        updateVehicle(vehicle);
    }

}

enum RouteType {
    Neighbour,
    Regional,
    International
}

class CompanyTypeVehicles<V extends Vehicle> {

    private List<V> vehicles = new ArrayList<>();
    private final List<V> idlingVehicles = new ArrayList<>();
    private final List<V> enRouteVehicles = new ArrayList<>();
    private final List<V> finishedVehicles = new ArrayList<>();
    private final List<V> holdingVehicles = new ArrayList<>();

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
                found = finishedVehicles.remove(vehicle);
                if (!found) {
                    found = holdingVehicles.remove(vehicle);
                }
            }
        }

        if (state instanceof Idle) {
            this.idlingVehicles.add(vehicle);
        } else if (state instanceof EnRoute) {
            this.enRouteVehicles.add(vehicle);
        } else if (state instanceof Finished) {
            this.finishedVehicles.add(vehicle);
        } else if (state instanceof Holding) {
            this.holdingVehicles.add(vehicle);
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

    public List<V> getHoldingVehicles() {
        return holdingVehicles;
    }

    public List<V> getVehicles() {
        return vehicles;
    }
}