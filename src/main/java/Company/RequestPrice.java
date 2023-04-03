package Company;


import Network.Location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestPrice {
    private final Integer IMPOSSIBLE_PRICE = Integer.MAX_VALUE;
    private Request request;

    public RequestPrice(Request request) {
        this.request = request;
    }

    private Integer getBestPrice(Location start, Location end, Company company) {
        int numVehicles = company.getVehiclesInAdjacentLocations(start, end).size();
        return numVehicles == 0 ? IMPOSSIBLE_PRICE : company.getPricePerVehicle();
    }

    public List<Integer> getBestPathPrices(Company company) {
        List<Location> route = request.getRoute();

        List<Integer> prices = new ArrayList<>(Collections.nCopies(route.size() - 1, IMPOSSIBLE_PRICE));

        for (int i = 0; i < route.size()-1; i++) {
            Location start = route.get(i);
            Location end = route.get(i+1);
            Integer price = getBestPrice(start, end, company);
            prices.set(i, price);
        }

        return prices;
    }
}
