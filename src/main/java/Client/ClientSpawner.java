package Client;

import Agents.Client.ClientAgent;
import Network.Location.House;
import Network.Network;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.List;
import java.util.Random;

public class ClientSpawner {
    private static final Random random = new Random();
    private final Network network;
    private final ContainerController container;
    private final ClientSpawnerConfigs configs;
    private static int clientIndex = 0;

    public ClientSpawner(Network network, ContainerController container, ClientSpawnerConfigs configs) {
        this.network = network;
        this.container = container;
        this.configs = configs;
    }

    public void spawnClient() {
        List<House> houses = network.getHouses();
        int networkSize = houses.size();
        int houseIndex = random.nextInt(networkSize);
        House house = houses.get(houseIndex);
        ClientConfigs configs = spawnClientConfigs();

        Client client = new Client(network, house, configs);

        String name = "Client" + String.format("%02d", ClientSpawner.getClientIndex());
        try {
            AgentController ac = container.acceptNewAgent(name, new ClientAgent(client));
            ac.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        clientIndex++;
    }

    public ClientConfigs spawnClientConfigs() {
        return new ClientSpawnerConfigs().getClient();
    }

    public static int getClientIndex() {
        return clientIndex;
    }
}
