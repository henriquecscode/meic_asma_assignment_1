package Company;


import Network.Location.Location;
import Vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestPrice {
    private final Double IMPOSSIBLE_PRICE = Double.MAX_VALUE;
    private Request request;

    public RequestPrice(Request request) {
        this.request = request;
    }

    private double getPriceFromMetrics(int numPrimaryVehicles, int numSecondaryVehicles, int numAnyVehiclePrimary, int numAnyVehicleSecondary, double pricePerVehicle) {

        if (numPrimaryVehicles == 0 && numSecondaryVehicles == 0 && numAnyVehiclePrimary == 0 && numAnyVehicleSecondary == 0) {
            return IMPOSSIBLE_PRICE;
        }
        double primaryComponent = numPrimaryVehicles == 0 ? pricePerVehicle : pricePerVehicle / (numPrimaryVehicles);
        double secondaryComponent = numSecondaryVehicles == 0 ? pricePerVehicle : pricePerVehicle / (numSecondaryVehicles);
        double anyVehiclePrimaryComponent = numAnyVehiclePrimary == 0 ? pricePerVehicle : pricePerVehicle / (numAnyVehiclePrimary);
        double anyVehicleSecondaryComponent = numAnyVehicleSecondary == 0 ? pricePerVehicle : pricePerVehicle / (numAnyVehicleSecondary);

        double weighedPrimaryComponent = primaryComponent * 2;
        double weighedSecondaryComponent = secondaryComponent;
        double weighedAnyVehiclePrimaryComponent = anyVehiclePrimaryComponent / 2;
        double weighedAnyVehicleSecondaryComponent = anyVehicleSecondaryComponent / 2;
        return weighedPrimaryComponent + weighedSecondaryComponent + weighedAnyVehiclePrimaryComponent + weighedAnyVehicleSecondaryComponent;
    }

    private Double getBestPrice(Location start, Location end, Company company) {
        List<Vehicle> primaryVehicles = company.findVehicle(start, end, request.getProductVolume());
        List<Vehicle> secondaryVehicles = company.findVehicle(end, start, request.getProductVolume());
        List<Vehicle> anyVehiclePrimary = company.findAnyVehicle(start, end, request.getProductVolume());
        List<Vehicle> anyVehicleSecondary = company.findAnyVehicle(end, start, request.getProductVolume());
        int numPrimaryVehicles = primaryVehicles.size();
        int numSecondaryVehicles = secondaryVehicles.size();
        int numAnyVehiclePrimary = anyVehiclePrimary.size();
        int numAnyVehicleSecondary = anyVehicleSecondary.size();
        return getPriceFromMetrics(numPrimaryVehicles, numSecondaryVehicles, numAnyVehiclePrimary, numAnyVehicleSecondary, company.getPricePerVehicle());
//        int numVehicles = company.getVehiclesInAdjacentLocations(start, end).size();
//        return numVehicles == 0 ? IMPOSSIBLE_PRICE : company.getPricePerVehicle();
    }

    public List<Double> getBestPathPrices(Company company) {
        List<Location> route = request.getRoute();

        List<Double> prices = new ArrayList<>(Collections.nCopies(route.size() - 1, IMPOSSIBLE_PRICE));

        for (int i = 0; i < route.size() - 1; i++) {
            Location start = route.get(i);
            Location end = route.get(i + 1);
            Hub hub = company.findLocationHub(start);
            Hub hub2 = company.findLocationHub(end);
            if (hub != null || hub2 != null) {
                Double price = getBestPrice(start, end, company);
                prices.set(i, price);
            }
        }

        return prices;
    }
}
