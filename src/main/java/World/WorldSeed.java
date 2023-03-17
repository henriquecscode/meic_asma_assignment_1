package World;

import Company.CompanySeed;
import Network.NetworkSeed;
import Producer.ProducerSeed;
import Vehicle.Seed.FleetSeed;
import utils.ClassSeed;
import utils.SeedInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WorldSeed extends ClassSeed implements SeedInterface {
    private static final String PATH = "data/world_seeds/";
    public NetworkSeed networkSeed;
    public FleetSeed fleetSeed;
    public List<CompanySeed> companySeeds;
    public List<ProducerSeed> producerSeeds;

    public WorldSeed(String filename) {
        super(filename);
    }
    public WorldSeed(NetworkSeed networkSeed, FleetSeed fleetSeed, List<CompanySeed> companySeeds, List<ProducerSeed> producerSeeds) {
        this.networkSeed = networkSeed;
        this.fleetSeed = fleetSeed;
        this.companySeeds = companySeeds;
        this.producerSeeds = producerSeeds;
    }

    @Override
    public void init() {
        networkSeed = new NetworkSeed();
        fleetSeed = new FleetSeed();
        companySeeds = new ArrayList<>();
        producerSeeds = new ArrayList<>();
    }

    @Override
    public void scanSeed(Scanner scanner) {
        networkSeed.scanSeed(scanner);
        fleetSeed.scanSeed(scanner);
        int numberOfCompanies = scanner.nextInt();
        for (int i = 0; i < numberOfCompanies; i++) {
            CompanySeed companySeed = new CompanySeed();
            companySeed.scanSeed(scanner);
            companySeeds.add(companySeed);
        }
        int numberOfProducers = scanner.nextInt();
        for (int i = 0; i < numberOfProducers; i++) {
            ProducerSeed producerSeed = new ProducerSeed();
            producerSeed.scanSeed(scanner);
            producerSeeds.add(producerSeed);
        }
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        String text = "";
        text += networkSeed.serialize() + " ";
        text += fleetSeed.serialize() + " ";
        text += companySeeds.size() + " ";
        for (CompanySeed companySeed : companySeeds) {
            text += companySeed.serialize() + " ";
        }
        text += producerSeeds.size() + " ";
        for (ProducerSeed producerSeed : producerSeeds) {
            text += producerSeed.serialize() + " ";
        }
        return text;
    }
}
