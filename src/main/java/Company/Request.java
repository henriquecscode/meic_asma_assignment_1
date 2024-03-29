package Company;

import Network.Location.House;
import Network.Location.Location;
import Network.Network;

import java.util.List;

public class Request {
    private final String clientName;
    private final String productName;
    private final int productVolume;
    private final int quantity;
    private final Itinerary route;

    public Request(House start, House end, String productName, String clientName, int productVolume, int quantity) {
        this.clientName = clientName;
        this.productName = productName;
        this.productVolume = productVolume;
        this.quantity = quantity;
        this.route = new Itinerary(start, end);
    }

    public Request(Network network, String request) {
        String[] requestInfo = request.split(";");
        this.clientName = requestInfo[0];
        this.productName = requestInfo[1];
        this.productVolume = Integer.parseInt(requestInfo[2]);
        this.quantity = Integer.parseInt(requestInfo[3]);
        this.route = new Itinerary(network, requestInfo[4]);
    }

    public String getClientName() {
        return clientName;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductVolume() {
        return productVolume;
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
        return clientName + ";" + productName + ";" + productVolume + ";" + quantity + ";" + route.toString();
    }
}
