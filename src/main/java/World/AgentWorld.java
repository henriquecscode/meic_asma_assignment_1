package World;

import Agents.ProducerAgent;
import Client.ClientSpawnerThread;
import Agents.Company.CompanyAgent;
import Agents.Company.CompanyGlobalHubAgent;
import Agents.Company.CompanyRegionHubAgent;
import Company.GlobalHub;
import Company.RegionHub;
import Company.Company;
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
            makeCompanies();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        ClientSpawnerThread clientSpawner = new ClientSpawnerThread(network, container);
        clientSpawner.spawnClient();
//        new ClientSpawnerThread(network, container).start(); // save thread to stop if necessary
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

    private void makeCompanies() throws StaleProxyException {
        int companyIndex = 0;
        AgentController ac;
        String name;
        for (Company company : companies) {
            name = "Company" + String.format("%02d", companyIndex);
            ac = container.acceptNewAgent(name, new CompanyAgent(company));
            ac.start();

            int hubIndex = 0;
            for (GlobalHub hub : company.getGlobalHubs()) {
                name = "Company" + String.format("%02d", companyIndex) + "GlobalHub" + String.format("%02d", hubIndex);
                ac = container.acceptNewAgent(name, new CompanyGlobalHubAgent(hub));
                ac.start();
                hubIndex++;
            }

            hubIndex = 0;
            for (RegionHub hub : company.getRegionHubs()) {
                name = "Company" + String.format("%02d", companyIndex) + "RegionHub" + String.format("%02d", hubIndex);
                ac = container.acceptNewAgent(name, new CompanyRegionHubAgent(hub));
                ac.start();
                hubIndex++;
            }
            companyIndex++;

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
