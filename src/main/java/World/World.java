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
    protected final static int NUMBER_OF_COMPANIES = 10;
    protected final static int NUMBER_OF_PRODUCERS = 20;
    protected WorldSeed worldSeed;
    protected Network network;
    protected NetworkSeed networkSeed;
    protected FleetSeed fleetSeed;
    protected List<Company> companies = new ArrayList<>();
    protected int numberOfCompanies;
    protected int numberOfProducers;
    protected ArrayList<Producer> producers = new ArrayList<>();

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


    private void makeNetwork() {
        NetworkCreator networkCreator = new NetworkCreator();
        networkSeed = networkCreator.getSeed();
        this.networkSeed.saveSeed();
        this.network = new Network(this.networkSeed);
    }

    private void makeNetwork(String networkSeedFilename) {
        NetworkSeed seed = new NetworkSeed(networkSeedFilename);
        makeNetwork(seed);
    }

    private void makeNetwork(NetworkSeed networkSeed) {
        this.network = new Network(networkSeed);
    }

    private void makeFleet() {
        ShipCreator shipCreator = new ShipCreator();
        SemiCreator semiCreator = new SemiCreator();
        VanCreator vanCreator = new VanCreator();
        VehicleSeed shipSeed = shipCreator.getSeed();
        VehicleSeed semiSeed = semiCreator.getSeed();
        VehicleSeed vanSeed = vanCreator.getSeed();

        this.fleetSeed = new FleetSeed(shipSeed, semiSeed, vanSeed);
        this.fleetSeed.saveSeed();
    }

    private void makeFleet(String fleetSeedFilename) {
        this.fleetSeed = new FleetSeed(fleetSeedFilename);
    }

    private void makeCompanies() {
        this.makeCompanies(numberOfCompanies);
    }


    private void makeCompanies(int numberOfCompanies) {
        companies = new ArrayList<>();
        CompanyCreator companyCreator = new CompanyCreator(this.network.seed);
        String standardFilename = ClassSeed.getStandardFilename();
        for (int i = 0; i < numberOfCompanies; i++) {
            CompanySeed companySeed = companyCreator.getSeed();
            companySeed.saveSeed(standardFilename + "_" + i);
            companies.add(new Company(this.network, companySeed, fleetSeed));
        }
    }

    private void makeCompanies(List<String> companySeedFilenames) {
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

    private void makeCompany(String companySeedFilename) {
        CompanySeed companySeed = new CompanySeed(companySeedFilename);
        this.makeCompany(companySeed);
    }

    private void makeCompany(CompanySeed companySeed) {
        this.companies.add(new Company(this.network, companySeed, fleetSeed));
    }

    private void makeProducers() {
        this.makeProducers(numberOfProducers);
    }

    private void makeProducers(int numberOfProducers) {
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

    private void makeProducers(List<String> producerSeedFilenames) {
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

    private void makeProducer(String producerSeedFilename) {
        ProducerSeed producerSeed = new ProducerSeed(producerSeedFilename);
        makeProducer(producerSeed);
    }

    private void makeProducer(ProducerSeed producerSeed) {
        this.producers.add(new Producer(this.network, producerSeed));
    }


}

