package Client;

import Network.Location.House;
import Network.Network;

public class Client {
    private final Network network;
    private final House house;
    private final ClientConfigs configs;

    Client(Network network, House house, ClientConfigs configs) {
        this.network = network;
        this.house = house;
        this.configs = configs;
    }
}
