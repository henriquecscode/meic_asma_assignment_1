package Client;

import Agents.ClientAgent;
import Network.Location.House;
import Network.Network;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.List;
import java.util.Random;

public class ClientSpawnerThread extends Thread {
    public static final int MIN_SPAWN_INTERVAL_MS = 1000;
    public static final int MAX_SPAWN_INTERVAL_MS = 10000;

    private static final Random random = new Random(1);
    private final Network network;
    private final ContainerController container;
    private static int clientIndex = 0;

    public ClientSpawnerThread(Network network, ContainerController container) {
        this.network = network;
        this.container = container;
    }

    public void spawnClient() {
        List<House> houses = network.getHouses();
        int networkSize = houses.size();
        int houseIndex = random.nextInt(networkSize);
        House house = houses.get(houseIndex);

        Client client = new Client(network, house);

        String name = "Client" + String.format("%02d", ClientSpawnerThread.getClientIndex());
        try {
            AgentController ac = container.acceptNewAgent(name, new Agents.Client.ClientAgent(client));
            ac.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        clientIndex++;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            spawnClient();
            try {
                int sleepTime = random.nextInt(MAX_SPAWN_INTERVAL_MS - MIN_SPAWN_INTERVAL_MS) + MIN_SPAWN_INTERVAL_MS;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getClientIndex() {
        return clientIndex;
    }
}
