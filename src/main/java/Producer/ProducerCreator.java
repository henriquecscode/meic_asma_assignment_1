package Producer;

import Network.NetworkSeed;
import Product.ProductCreator.ProductCreator;
import utils.CreatorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProducerCreator implements CreatorInterface {
    private static final Random random = new Random(1);
    private static final int NUMBER_OF_PRODUCTIONS = 2;
    private final NetworkSeed seed;
    private final ProductCreator productCreator;
    private final int numberOfProductions;

    public ProducerCreator(NetworkSeed seed, ProductCreator productCreator) {
        this(seed, productCreator, NUMBER_OF_PRODUCTIONS);
    }

    ProducerCreator(NetworkSeed seed, ProductCreator productCreator, int numberOfProductions) {
        super();
        this.productCreator = productCreator;
        this.seed = seed;
        this.numberOfProductions = numberOfProductions;
    }

    @Override
    public ProducerSeed getSeed() {
        ProductionCreator productionCreator = new ProductionCreator(productCreator);
        List<ProductionSeed> productions = new ArrayList<>();
        for (int i = 0; i < numberOfProductions; i++) {
            productions.add(productionCreator.getSeed());
        }
        int port, city, house;
        port = random.nextInt(seed.getNumberOfPorts());
        city = random.nextInt(seed.getNumberOfCities());
        house = random.nextInt(seed.getNumberOfHouses());
        List<Integer> location = List.of(port, city, house);
        return new ProducerSeed(productions, location);
    }


}
