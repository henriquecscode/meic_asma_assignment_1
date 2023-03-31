package Client;

import Network.Location.House;
import Network.Network;

public class Client {
    private final Network network;
    private final House house;

    Client(Network network, House house) {
        this.network = network;
        this.house = house;
    }
}
