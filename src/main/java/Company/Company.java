package Company;

import Network.Location.City;
import Network.Location.House;
import Network.Location.Location;
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
import java.util.stream.Collectors;

public class Company {

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
            Hub hub = globalHubsByLocation.get(portIndex);
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
            Hub hub = regionHubsByLocation.get(List.of(port, city));
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
            Hub hub = regionHubsByLocation.get(List.of(port, city));
            hub.addVehicle(van);
            van.setHub(hub);
            van.setLocation(cityLocation);
        }
        this.vans = new CompanyTypeVehicles<Van>(vans);
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
}

class CompanyTypeVehicles<V extends Vehicle> {

    private List<V> vehicles = new ArrayList<>();
    private final List<V> idlingVehicles = new ArrayList<>();
    private final List<V> enRouteVehicles = new ArrayList<>();
    private final List<V> finishedVehicles = new ArrayList<>();

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