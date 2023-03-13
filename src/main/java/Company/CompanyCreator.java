package Company;

import Network.NetworkSeed;
import utils.CreatorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class CompanyCreator implements CreatorInterface {
    static Random random = new Random();
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

        List<Integer> globalHubs = intAllocator(noShips, noGlobal);
        List<List<Integer>> regionalHubs = listAllocator(globalHubs, noRegional, noRegional);
        List<Integer> shipLocations = generalAllocatorReplacement(globalHubs, noShips);
        List<Integer> globalSemiLocations = generalAllocatorReplacement(globalHubs, noSemiInGlobal);
        List<List<Integer>> regionalSemiLocations = generalAllocatorReplacement(regionalHubs, noSemiInRegional);
        List<List<Integer>> vanLocations = generalAllocatorReplacement(regionalHubs, noVans);

        CompanySeed seed = new CompanySeed(noGlobal, noRegional, noShips, noSemiInGlobal, noSemiInRegional, noVans, globalHubs, regionalHubs, shipLocations, globalSemiLocations, regionalSemiLocations, vanLocations);
        return seed;
    }

    private static <T> List<T> generalAllocatorReplacement(List<T> slots, int noAllocations) {
        List<T> allocatedSlots = new ArrayList<>();
        int size = slots.size();
        for (int i = 0; i < noAllocations; i++) {
            int index = random.nextInt(size);
            T slotIndex = slots.get(index);
            allocatedSlots.add(slotIndex);
        }
        return allocatedSlots;
    }

    private static <T> List<T> generalAllocator(List<T> slots, int noAllocations) {
        if (noAllocations > slots.size()) {
            throw new IllegalArgumentException("Cannot allocate more than the number of slots");
        }
        List<T> allocatedSlots = new ArrayList<>();
        List<T> slotsCopy = new ArrayList<>(slots);
        for (int i = 0; i < noAllocations; i++) {
            int indexToRemove = random.nextInt(slotsCopy.size());
            T slotIndex = slotsCopy.remove(indexToRemove);
            allocatedSlots.add(slotIndex);
        }
        return allocatedSlots;
    }

    private static List<Integer> intAllocator(int noSlots, int noAllocations) {
        List<Integer> slots = getIndexes(noSlots);
        List<Integer> allocated = generalAllocator(slots, noAllocations);
        return allocated;
    }

    private static List<List<Integer>> listAllocator(List<Integer> baseSlots, int noSecondarySlots, int noAllocations) {
        List<List<Integer>> indexes = getIndexesList(baseSlots, noSecondarySlots);
        List<List<Integer>> allocated = generalAllocator(indexes, noAllocations);
        return allocated;
    }

    private static List<Integer> getIndexes(int size) {
        return IntStream.range(0, size).collect(ArrayList::new, List::add, List::addAll);
    }

    private static List<List<Integer>> getIndexesList(List<Integer> firstIndex, int secondIndexSize) {
        List<Integer> secondIndex = getIndexes(secondIndexSize);
        List<List<Integer>> indexes = cartesianProduct(List.of(firstIndex, secondIndex));
        return indexes;
    }

    // https://stackoverflow.com/a/9496234
    private static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
