package World;

import Agents.Client.ClientAgent;
import Agents.ProducerAgent;
import App.App;
import Client.ClientSpawnerThread;
import Agents.Company.CompanyAgent;
import Company.Company;
import Producer.Producer;
import jade.core.Agent;
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
    public static List<Agent> agents = new ArrayList<>();
    public ClientSpawnerThread clientSpawner;

    public AgentWorld() {
        super();
    }

    public AgentWorld(String seed) {
        super(seed);
    }


    public AgentWorld(String networkSeedFilename, String fleetSeedFilename, java.util.List<String> companySeedFilenames, List<String> producerSeedFilenames) {
        super(networkSeedFilename, fleetSeedFilename, companySeedFilenames, producerSeedFilenames);
    }

    public void startAgents() {
        initJade();

        try {
            makeProducers();
            makeCompanies();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        clientSpawner = new ClientSpawnerThread(network, container);
    }

    public void log() {
        worldSeed.saveSeed(App.executionLogFolderSettings, "/worldSeed.txt");
        clientSpawner.save(App.executionLogFolderSettings + "/clientSeed.txt");
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
            agents.add(producerAgent);
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
            agents.add(companyAgent);

//            int hubIndex = 0;
//            for (GlobalHub hub : company.getGlobalHubs()) {
//                name = "Company" + String.format("%02d", companyIndex) + "GlobalHub" + String.format("%02d", hubIndex);
//                CompanyGlobalHubAgent companyGlobalHubAgent = new CompanyGlobalHubAgent(hub);
//                ac = container.acceptNewAgent(name, companyGlobalHubAgent);
//                ac.start();
//                agents.add(companyGlobalHubAgent);
//                hubIndex++;
//
//            }
//
//            hubIndex = 0;
//            for (RegionHub hub : company.getRegionHubs()) {
//                name = "Company" + String.format("%02d", companyIndex) + "RegionHub" + String.format("%02d", hubIndex);
//                CompanyRegionHubAgent companyRegionHubAgent = new CompanyRegionHubAgent(hub);
//                ac = container.acceptNewAgent(name, companyRegionHubAgent);
//                ac.start();
//                agents.add(companyRegionHubAgent);
//                hubIndex++;
//            }
            companyIndex++;

        }

    }

    public void start() {
        clientSpawner.start();
    }

    private void initJade() {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        //p1.setParameter(...);
        container = rt.createMainContainer(p1);

        AgentController ac3;
        try {
            Agent rma = new jade.tools.rma.rma();
            ac3 = container.acceptNewAgent("myRMA", rma);
            ac3.start();
            agents.add(rma);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void testProducerAgent() {
        producersAgents.get(0).testProducerRequest();
    }

    public void testClientAgent() {
//        Agents.Client.ClientAgent.random.nextInt();
//        Agents.Client.ClientAgent.random.nextInt();
        ClientSpawnerThread.random.nextInt();
        clientSpawner.spawnClient();
    }

}
