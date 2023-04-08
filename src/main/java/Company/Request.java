package Company;

import Network.Location.House;
import Network.Location.Location;
import Network.Network;

import java.util.List;

public class Request {
    private String productName;
    private int quantity;

    private Itinerary route;

    public Request(House start, House end, String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
        this.route = new Itinerary(start, end);
    }

    public Request(Network network, String request) {
        String[] requestInfo = request.split(";");
        this.productName = requestInfo[0];
        this.quantity = Integer.parseInt(requestInfo[1]);
        this.route = new Itinerary(network, requestInfo[2]);
    }


    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public House getStart() {
        return route.getStart();
    }

    public House getEnd() {
        return route.getEnd();
    }

    public List<Location> getRoute() {
        return route.getLocations();
    }

    @Override
    public String toString() {
        return productName + ";" + quantity + ";" + route.toString();
    }
}
