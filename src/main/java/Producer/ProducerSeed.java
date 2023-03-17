package Producer;

import Network.Location.Location;
import utils.ClassSeed;
import utils.SeedInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProducerSeed extends ClassSeed implements SeedInterface {
    private static final String PATH = "data/producer_seeds/";
    private List<ProductionSeed> productionSeeds;
    private List<Integer> location; // [port, city, house]

    public ProducerSeed() {
        super();
    }

    public ProducerSeed(String filename) {
        super(filename);
    }

    public ProducerSeed(List<ProductionSeed> productionSeeds, List<Integer> location) {
        super();
        this.productionSeeds = productionSeeds;
        this.location = location;
    }

    @Override
    public void init() {
        productionSeeds = new ArrayList<>();
    }

    @Override
    public void scanSeed(Scanner scanner) {
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            ProductionSeed productionSeed = new ProductionSeed();
            productionSeed.scanSeed(scanner);
            productionSeeds.add(productionSeed);
        }
        int port, city, house;
        port = scanner.nextInt();
        city = scanner.nextInt();
        house = scanner.nextInt();
        location = List.of(port, city, house);
    }

    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        String productionSeed = "";
        productionSeed += productionSeeds.size() + " ";
        for (ProductionSeed seed : productionSeeds) {
            productionSeed += seed.serialize() + " ";
        }
        productionSeed += location.get(0) + " " + location.get(1) + " " + location.get(2);
        return productionSeed;
    }

    public List<ProductionSeed> getProductionSeeds() {
        return productionSeeds;
    }

    public List<Integer> getLocation() {
        return location;
    }
}
