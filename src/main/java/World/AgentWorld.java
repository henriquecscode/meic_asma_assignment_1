package World;

import Agents.Producer.ProducerAgent;
import Producer.Producer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.List;

public class AgentWorld extends World {
    private ContainerController container;

    public AgentWorld() {
        super();
    }

    public AgentWorld(String seed) {
        super(seed);
    }

    public AgentWorld(String networkSeedFilename, String fleetSeedFilename, java.util.List<String> companySeedFilenames, List<String> producerSeedFilenames) {
        super(networkSeedFilename, fleetSeedFilename, companySeedFilenames, producerSeedFilenames);
    }

    public void run() {
        initJade();

        try {
            makeProducers();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeProducers() throws StaleProxyException {
        int producerIndex = 0;
        AgentController ac;
        String name;
        for (Producer producer : producers) {
            name = "Producer" + String.format("%02d", producerIndex);
            ac = container.acceptNewAgent(name, new ProducerAgent(producer));
            ac.start();
            producerIndex++;
        }
    }

    private void initJade() {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        //p1.setParameter(...);
        container = rt.createMainContainer(p1);

        AgentController ac3;
        try {
            ac3 = container.acceptNewAgent("myRMA", new jade.tools.rma.rma());
            ac3.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void createProducerAgent(Producer producer) {
    }
}
