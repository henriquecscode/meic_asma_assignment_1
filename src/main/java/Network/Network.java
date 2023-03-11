package Network;

import Network.Location.City;
import Network.Location.House;
import Network.Location.Port;
import Network.Route.InternationalRoute;
import Network.Route.NeighbourhoodRoute;
import Network.Route.RegionalRoute;


import java.util.ArrayList;
import java.util.List;

public class Network {
    private List<House> houses = new ArrayList<>();
    private List<List<List<House>>> housesByLocation = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<List<City>> citiesByLocation = new ArrayList<>();
    private List<Port> ports = new ArrayList<>();
    private List<InternationalRoute> internationalRoutes = new ArrayList<>();
    private List<List<RegionalRoute>> regionalRoutes = new ArrayList<>();
    private List<List<List<NeighbourhoodRoute>>> neighbourhoodRoutes = new ArrayList<>();


    public Network(NetworkSeed seed) {
        this.makePorts(seed);
        this.makeCities(seed);
        this.makeHouses(seed);
    }

    public void makePorts(NetworkSeed seed) {
        int noPorts = seed.getNumberOfPorts();
        for (int i = 0; i < noPorts; i++) {
            Port port = new Port(Integer.toString(i));
            this.ports.add(port);
        }

        int distanceCount = 0;
        for (int i = 0; i < noPorts - 1; i++) { // to
            for (int j = i + 1; j < noPorts; j++) { // from
                Port from = this.ports.get(i);
                Port to = this.ports.get(j);
                int distance = seed.getPortDistances().get(distanceCount);
                InternationalRoute route = new InternationalRoute(from, to, distance);
                this.internationalRoutes.add(route);
            distanceCount++;
            }
        }
    }

    public void makeCities(NetworkSeed seed) {
        int noPorts = seed.getNumberOfPorts();
        int noCities = seed.getNumberOfCities();

        // create cities
        for (int i = 0; i < noPorts; i++) {
            List<City> portCities = new ArrayList<>();
            Port port = this.ports.get(i);
            for (int j = 0; j < noCities; j++) {
                String name = i + "-" + j;
                City city = new City(name);
                this.cities.add(city);
                portCities.add(city);
            }
            this.citiesByLocation.add(portCities);
        }

        // cities distances
        for (int i = 0; i < noPorts; i++) {
            List<RegionalRoute> cityRoutes = new ArrayList<>();
            for (int j = 0; j < noCities; j++) {
                City city = this.citiesByLocation.get(i).get(j);
                Port port = this.ports.get(i);
                int distance = seed.getCityDistances().get(i).get(j);
                RegionalRoute route = new RegionalRoute(port, city, distance);
                cityRoutes.add(route);
            }
            this.regionalRoutes.add(cityRoutes);
        }
    }

    public void makeHouses(NetworkSeed seed) {
        int noPorts = seed.getNumberOfPorts();
        int noCities = seed.getNumberOfCities();
        int noHouses = seed.getNumberOfHouses();

        // create houses
        for (int i = 0; i < noPorts; i++) {
            List<List<House>> portHouses = new ArrayList<>();
            for (int j = 0; j < noCities; j++) {
                List<House> cityHouses = new ArrayList<>();
                for (int k = 0; k < noHouses; k++) {
                    String name = i + "-" + j + "-" + k;
                    House house = new House(name);
                    this.houses.add(house);
                    cityHouses.add(house);
                }
                portHouses.add(cityHouses);
            }
            this.housesByLocation.add(portHouses);
        }

        // houses distances
        for (int i = 0; i < noPorts; i++) {
            List<List<NeighbourhoodRoute>> cityRoutes = new ArrayList<>();
            for (int j = 0; j < noCities; j++) {
                List<NeighbourhoodRoute> houseRoutes = new ArrayList<>();
                for (int k = 0; k < noHouses; k++) {
                    House house = this.housesByLocation.get(i).get(j).get(k);
                    City city = this.citiesByLocation.get(i).get(j);
                    int distance = seed.getHouseDistances().get(i).get(j).get(k);
                    NeighbourhoodRoute route = new NeighbourhoodRoute(city, house, distance);
                    houseRoutes.add(route);
                }
                cityRoutes.add(houseRoutes);
            }
            this.neighbourhoodRoutes.add(cityRoutes);
        }
    }

}
