package World;

import Agents.ClientAgent;
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

import java.util.ArrayList;
import java.util.List;

public class AgentWorld extends World {
    private ContainerController container;
    private List<ClientAgent> clientsAgents = new ArrayList<>();
    private List<ProducerAgent> producersAgents = new ArrayList<>();
    private List<CompanyAgent> agentsAgents = new ArrayList<>();

    public AgentWorld() {
        super();
    }

    public AgentWorld(String seed) {
        super(seed);
    }

    ClientSpawnerThread clientSpawner;

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
        clientSpawner = new ClientSpawnerThread(network, container);
        //        new ClientSpawnerThread(network, container).start(); // save thread to stop if necessary
    }

    private void makeProducers() throws StaleProxyException {
        int producerIndex = 0;
        AgentController ac;
        String name;
        for (Producer producer : producers) {
            name = "Producer" + String.format("%02d", producerIndex);
            ProducerAgent producerAgent = new ProducerAgent(producer);
            ac = container.acceptNewAgent(name, producerAgent);
            ac.start();
            producersAgents.add(producerAgent);
            producerIndex++;
        }
    }

    private void makeCompanies() throws StaleProxyException {
        int companyIndex = 0;
        AgentController ac;
        String name;
        for (Company company : companies) {
            name = "Company" + String.format("%02d", companyIndex);
            CompanyAgent companyAgent = new CompanyAgent(company);
            ac = container.acceptNewAgent(name, companyAgent);
            ac.start();
            agentsAgents.add(companyAgent);

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

    public void testProducerAgent(){
        producersAgents.get(0).testProducerRequest();
    }

    public void testClientAgent() {
        clientSpawner.spawnClient();
    }

}
