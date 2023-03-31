package Client;

import Agents.ClientAgent;
import Network.Location.House;
import Network.Network;

import java.util.List;
import java.util.Random;

public class ClientSpawnerThread extends Thread {
    public static final int MIN_SPAWN_INTERVAL_MS = 1000;
    public static final int MAX_SPAWN_INTERVAL_MS = 10000;

    private static final Random random = new Random();
    private final Network network;

    public ClientSpawnerThread(Network network) {
        this.network = network;
    }

    private Client spawnClient() {
        List<House> houses = network.getHouses();
        int networkSize = houses.size();
        int houseIndex = random.nextInt(networkSize);
        House house = houses.get(houseIndex);

        return new Client(network, house);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            new ClientAgent(spawnClient());
            try {
                int sleepTime = random.nextInt(MAX_SPAWN_INTERVAL_MS - MIN_SPAWN_INTERVAL_MS) + MIN_SPAWN_INTERVAL_MS;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
