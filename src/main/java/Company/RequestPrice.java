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

    private Double getBestPrice(Location start, Location end, Company company) {
        List<Vehicle> primaryVehicles = company.findVehicle(start, end, 0);
        List<Vehicle> secondaryVehicles = company.findVehicle(end, start, 0);
        int numPrimaryVehicles = primaryVehicles.size();
        int numSecondaryVehicles = secondaryVehicles.size();
        if (numPrimaryVehicles == 0 && numSecondaryVehicles == 0) {
            return IMPOSSIBLE_PRICE;
        }
        return (double) (numPrimaryVehicles * company.getPricePerVehicle() + numSecondaryVehicles * company.getPricePerVehicle() * 2);
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
