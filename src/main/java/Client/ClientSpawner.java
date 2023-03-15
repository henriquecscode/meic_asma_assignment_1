package Client;

import Network.Location.House;
import Network.Network;

import java.util.List;
import java.util.Random;

public class ClientSpawner {
    private static final Random random = new Random();
    private final Network network;
    private final ClientSpawnerConfigs configs;

    public ClientSpawner(Network network, ClientSpawnerConfigs configs) {
        this.network = network;
        this.configs = configs;
    }

    public void spawnClient() {
        List<House> houses = network.getHouses();
        int networkSize = houses.size();
        int houseIndex = random.nextInt(networkSize);
        House house = houses.get(houseIndex);
        ClientConfigs configs = spawnClientConfigs();

        Client client = new Client(network, house, configs);
    }

    public ClientConfigs spawnClientConfigs() {
        return new ClientSpawnerConfigs().getClient();
    }
}
