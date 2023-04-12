package Client;

import Agents.Client.ClientAgent;
import Network.Location.House;
import Network.Network;
import World.AgentWorld;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.List;
import java.util.Random;

public class ClientSpawnerThread extends Thread {
    public static final int MIN_SPAWN_INTERVAL_MS = 20;
    public static final int MAX_SPAWN_INTERVAL_MS = 200;
    public static final int MAX_CLIENTS = 100;

    public static final Random random = new Random(1);
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
            ClientAgent clientAgent = new ClientAgent(client);
            AgentController ac = container.acceptNewAgent(name, clientAgent);
            ac.start();
            AgentWorld.agents.add(clientAgent);
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        clientIndex++;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (clientIndex >= MAX_CLIENTS) {
                System.out.println("----------------------CLIENT THREAD STOPPED-------------------------------");
                break;
            }
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
