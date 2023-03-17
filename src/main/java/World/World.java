package World;

import Company.Company;
import Company.CompanyCreator;
import Company.CompanySeed;
import Network.NetworkCreator;
import Network.NetworkSeed;
import Network.Network;
import Producer.Producer;
import Producer.ProducerCreator;
import Producer.ProducerSeed;
import Product.ProductCreator.ProductCreator;
import Product.ProductCreator.RandomProductCreator;
import Vehicle.Seed.*;
import utils.ClassSeed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class World {
    private final static int NUMBER_OF_COMPANIES = 10;
    private final static int NUMBER_OF_PRODUCERS = 20;
    private WorldSeed worldSeed;
    private Network network;
    private NetworkSeed networkSeed;
    private FleetSeed fleetSeed;
    private List<Company> companies = new ArrayList<>();
    private int numberOfCompanies;
    private int numberOfProducers;
    private ArrayList<Producer> producers = new ArrayList<>();

    public World() {
        this.numberOfCompanies = NUMBER_OF_COMPANIES;
        this.numberOfProducers = NUMBER_OF_PRODUCERS;

        this.makeNetwork();
        this.makeFleet();
        this.makeCompanies();
        this.makeProducers();

        this.createWorldSeed();
    }

    public World(String networkSeedFilename, String fleetSeedFilename, List<String> companySeedFilenames, List<String> producerSeedFilenames) {
        this.makeNetwork(networkSeedFilename);
        this.makeFleet(fleetSeedFilename);
        this.makeCompanies(companySeedFilenames);
        this.makeProducers(producerSeedFilenames);

    }

    public void createWorldSeed() {
        List<CompanySeed> companySeeds = companies.stream().map(Company::getSeed).collect(Collectors.toList());
        List<ProducerSeed> producerSeeds = producers.stream().map(Producer::getSeed).collect(Collectors.toList());
        this.worldSeed = new WorldSeed(networkSeed, fleetSeed, companySeeds, producerSeeds);
        this.worldSeed.saveSeed();
    }

    public World(String worldSeedString) {
        this.worldSeed = new WorldSeed(worldSeedString);
        this.makeNetwork(worldSeed.networkSeed);
        this.fleetSeed = worldSeed.fleetSeed;
        this.makeCompaniesFromSeed(worldSeed.companySeeds);
        this.makeProducersFromSeed(worldSeed.producerSeeds);
    }


    public void makeNetwork() {
        NetworkCreator networkCreator = new NetworkCreator();
        networkSeed = networkCreator.getSeed();
        this.networkSeed.saveSeed();
        this.network = new Network(this.networkSeed);
    }

    public void makeNetwork(String networkSeedFilename) {
        NetworkSeed seed = new NetworkSeed(networkSeedFilename);
        makeNetwork(seed);
    }

    public void makeNetwork(NetworkSeed networkSeed) {
        this.network = new Network(networkSeed);
    }

    public void makeFleet() {
        ShipCreator shipCreator = new ShipCreator();
        SemiCreator semiCreator = new SemiCreator();
        VanCreator vanCreator = new VanCreator();
        VehicleSeed shipSeed = shipCreator.getSeed();
        VehicleSeed semiSeed = semiCreator.getSeed();
        VehicleSeed vanSeed = vanCreator.getSeed();

        this.fleetSeed = new FleetSeed(shipSeed, semiSeed, vanSeed);
        this.fleetSeed.saveSeed();
    }

    public void makeFleet(String fleetSeedFilename) {
        this.fleetSeed = new FleetSeed(fleetSeedFilename);
    }

    public void makeCompanies() {
        this.makeCompanies(numberOfCompanies);
    }


    public void makeCompanies(int numberOfCompanies) {
        companies = new ArrayList<>();
        CompanyCreator companyCreator = new CompanyCreator(this.network.seed);
        String standardFilename = ClassSeed.getStandardFilename();
        for (int i = 0; i < numberOfCompanies; i++) {
            CompanySeed companySeed = companyCreator.getSeed();
            companySeed.saveSeed(standardFilename + "_" + i);
            companies.add(new Company(this.network, companySeed, fleetSeed));
        }
    }

    public void makeCompanies(List<String> companySeedFilenames) {
        this.companies = new ArrayList<>();
        for (String companySeedFilename : companySeedFilenames) {
            this.makeCompany(companySeedFilename);
        }
    }

    private void makeCompaniesFromSeed(List<CompanySeed> companySeeds) {
        for (CompanySeed companySeed : companySeeds) {
            this.makeCompany(companySeed);
        }
    }

    public void makeCompany(String companySeedFilename) {
        CompanySeed companySeed = new CompanySeed(companySeedFilename);
        this.makeCompany(companySeed);
    }

    public void makeCompany(CompanySeed companySeed) {
        this.companies.add(new Company(this.network, companySeed, fleetSeed));
    }

    public void makeProducers() {
        this.makeProducers(numberOfProducers);
    }

    public void makeProducers(int numberOfProducers) {
        producers = new ArrayList<>();
        ProductCreator productCreator = new RandomProductCreator();
        String standardFilename = ClassSeed.getStandardFilename();
        ProducerCreator producerCreator = new ProducerCreator(networkSeed, productCreator);
        for (int i = 0; i < numberOfProducers; i++) {
            ProducerSeed producerSeed = producerCreator.getSeed();
            producerSeed.saveSeed(standardFilename + "_" + i);
            producers.add(new Producer(this.network, producerSeed));
        }
    }

    public void makeProducers(List<String> producerSeedFilenames) {
        this.producers = new ArrayList<>();
        for (String producerSeedFilename : producerSeedFilenames) {
            this.makeProducer(producerSeedFilename);
        }
    }

    private void makeProducersFromSeed(List<ProducerSeed> producerSeeds) {
        for (ProducerSeed producerSeed : producerSeeds) {
            this.makeProducer(producerSeed);
        }
    }

    public void makeProducer(String producerSeedFilename) {
        ProducerSeed producerSeed = new ProducerSeed(producerSeedFilename);
        makeProducer(producerSeed);
    }

    public void makeProducer(ProducerSeed producerSeed) {
        this.producers.add(new Producer(this.network, producerSeed));
    }


}

