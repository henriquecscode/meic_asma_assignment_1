package World;

import Network.NetworkCreator;
import Network.NetworkSeed;
import Network.Network;

public class World {
    private Network network;

    public World() {
        this.makeNetwork();
        this.network.getGraph().display();
    }

    public void makeNetwork() {
        NetworkCreator networkCreator = new NetworkCreator();
        NetworkSeed seed = networkCreator.getSeed();
        this.network = new Network(seed);
    }
}
