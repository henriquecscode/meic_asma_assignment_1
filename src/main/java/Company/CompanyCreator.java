package Company;

import Network.NetworkSeed;
import utils.CreatorInterface;
import utils.Randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class CompanyCreator implements CreatorInterface {
    private final static Random random = new Random();
    private final int NO_GLOBAL = 3;
    private final int NO_REGIONAL = 5;
    private final int NO_SHIPS = NO_GLOBAL;
    private final int NO_SEMIS_IN_GLOBAL = 0;
    private final int NO_SEMIS_IN_REGIONAL = NO_REGIONAL;
    private final int NO_VANS = 0;
    private NetworkSeed seed;

    public CompanyCreator(NetworkSeed seed) {
        this.seed = seed;
    }

    public CompanySeed getSeed() {
        int noGlobal = NO_GLOBAL;
        int noRegional = NO_REGIONAL;
        int noShips = NO_SHIPS;
        int noSemiInGlobal = NO_SEMIS_IN_GLOBAL;
        int noSemiInRegional = NO_SEMIS_IN_REGIONAL;
        int noVans = noRegional * seed.getNumberOfHouses();

        List<Integer> globalHubs = Randomizer.intAllocator(noShips, noGlobal);
        List<List<Integer>> regionalHubs = Randomizer.listAllocator(globalHubs, noRegional, noRegional);
        List<Integer> shipLocations = Randomizer.generalAllocatorReplacement(globalHubs, noShips);
        List<Integer> globalSemiLocations = Randomizer.generalAllocatorReplacement(globalHubs, noSemiInGlobal);
        List<List<Integer>> regionalSemiLocations = Randomizer.generalAllocatorReplacement(regionalHubs, noSemiInRegional);
        List<List<Integer>> vanLocations = Randomizer.generalAllocatorReplacement(regionalHubs, noVans);

        CompanySeed seed = new CompanySeed(noGlobal, noRegional, noShips, noSemiInGlobal, noSemiInRegional, noVans, globalHubs, regionalHubs, shipLocations, globalSemiLocations, regionalSemiLocations, vanLocations);
        return seed;
    }


}
