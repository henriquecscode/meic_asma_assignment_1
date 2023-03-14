package Producer;

import Network.Location.House;
import Network.Location.Location;
import Network.Network;

import java.util.ArrayList;
import java.util.List;

public class Producer {
    private Network network;
    private ProducerSeed seed;
    private House location;
    private List<Production> productions = new ArrayList<>();

    public Producer(Network network, ProducerSeed seed) {
        this.network = network;
        this.seed = seed;
        this.makeProductions(seed);
        this.makeLocation(seed);
    }

    private void makeProductions(ProducerSeed seed) {
        for (ProductionSeed productionSeed : seed.getProductionSeeds()) {
            productions.add(new Production(productionSeed));
        }
    }

    private void makeLocation(ProducerSeed seed) {
        List<Integer> locationIndexes = seed.getLocation();
        int portIndex = locationIndexes.get(0);
        int cityIndex = locationIndexes.get(1);
        int houseIndex = locationIndexes.get(2);
        location = network.getHousesByLocation().get(portIndex).get(cityIndex).get(houseIndex);
    }
}
