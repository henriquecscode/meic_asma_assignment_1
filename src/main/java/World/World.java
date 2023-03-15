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

public class World {
    private final static int NUMBER_OF_COMPANIES = 10;
    private final static int NUMBER_OF_PRODUCERS = 20;
    private Network network;
    private NetworkSeed networkSeed;
    private FleetSeed fleetSeed;
    private List<Company> companies;
    private int numberOfCompanies;
    private int numberOfProducers;
    private ArrayList<Object> producers;

    public World() {
        this.numberOfCompanies = NUMBER_OF_COMPANIES;
        this.numberOfProducers = NUMBER_OF_PRODUCERS;

        this.makeNetwork();
        this.makeFleet();
        this.makeCompanies();
        this.makeProducers();
    }

    public World(String networkSeedFilename, String fleetSeedFilename, List<String> companySeedFilenames, List<String> producerSeedFilenames) {
        this.makeNetwork(networkSeedFilename);
        this.makeFleet(fleetSeedFilename);
        this.makeCompanies(companySeedFilenames);
        this.makeProducers(producerSeedFilenames);
    }

    public void makeNetwork() {
        NetworkCreator networkCreator = new NetworkCreator();
        networkSeed = networkCreator.getSeed();
        this.networkSeed.saveSeed();
        this.network = new Network(this.networkSeed);
    }

    public void makeNetwork(String networkSeedFilename) {
        NetworkSeed seed = new NetworkSeed(networkSeedFilename);
        this.network = new Network(seed);
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

    public void makeCompany(String companySeedFilename) {
        CompanySeed companySeed = new CompanySeed(companySeedFilename);
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

    public void makeProducer(String producerSeedFilename) {
        ProducerSeed producerSeed = new ProducerSeed(producerSeedFilename);
        this.producers.add(new Producer(this.network, producerSeed));
    }


}

