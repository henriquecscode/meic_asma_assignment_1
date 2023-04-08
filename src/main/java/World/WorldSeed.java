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

    static void seekAndConsumeTexts(Scanner scanner, String text) {
        String placeholder;
        while (!scanner.hasNext(text) && scanner.hasNextLine()) {
            placeholder = scanner.next();
        }
        scanner.next();
    }

    @Override
    public void scanSeed(Scanner scanner) {

        networkSeed.scanSeed(scanner);
        fleetSeed.scanSeed(scanner);
        WorldSeed.seekAndConsumeTexts(scanner, "Companies:");
        int numberOfCompanies = scanner.nextInt();
        for (int i = 0; i < numberOfCompanies; i++) {
            WorldSeed.seekAndConsumeTexts(scanner, "Company" + i + ":");
            CompanySeed companySeed = new CompanySeed();
            companySeed.scanSeed(scanner);
            companySeeds.add(companySeed);
        }
        WorldSeed.seekAndConsumeTexts(scanner, "Producers:");
        int numberOfProducers = scanner.nextInt();
        for (int i = 0; i < numberOfProducers; i++) {
            WorldSeed.seekAndConsumeTexts(scanner, "Producer" + i + ":");
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
        text += networkSeed.serialize() + "\n";
        text += fleetSeed.serialize() + "\n";
        text += "Companies:\n";
        text += companySeeds.size() + "\n";
        for (int i = 0; i < companySeeds.size(); i++) {
            text += "Company" + i + ":\n";
            text += companySeeds.get(i).serialize();
        }
        text += "\n";
        text += "Producers:\n";
        text += producerSeeds.size() + "\n";
        for (int i = 0; i < producerSeeds.size(); i++) {
            text += "Producer" + i + ":\n";
            text += producerSeeds.get(i).serialize();
        }
        return text;
    }
}
