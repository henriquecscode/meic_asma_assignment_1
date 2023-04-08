package Client;

import Network.Location.House;
import Network.Network;

public class Client {
    public final Network network;
    private final House house;

    Client(Network network, House house) {
        this.network = network;
        this.house = house;
    }

    public House getHouse() {
        return house;
    }
}
