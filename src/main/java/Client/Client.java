package Client;

import Network.Location.House;
import Network.Network;

public class Client {
    private static int clientId = 0;
    private final int id;
    private final String name;
    public final Network network;
    private final House house;

    Client(Network network, House house) {
        this.id = clientId++;
        this.name = "Client" + this.id;
        this.network = network;
        this.house = house;
    }

    public String getName() {
        return name;
    }

    public House getHouse() {
        return house;
    }
}
