package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Randomizer {
    private final static Random random = new Random();

    public static <T> List<T> generalAllocatorReplacement(List<T> slots, int noAllocations) {
        List<T> allocatedSlots = new ArrayList<>();
        int size = slots.size();
        for (int i = 0; i < noAllocations; i++) {
            int index = random.nextInt(size);
            T slotIndex = slots.get(index);
            allocatedSlots.add(slotIndex);
        }
        return allocatedSlots;
    }

    public static <T> List<T> generalAllocator(List<T> slots, int noAllocations) {
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

    public static List<Integer> intAllocator(int noSlots, int noAllocations) {
        List<Integer> slots = getIndexes(noSlots);
        List<Integer> allocated = generalAllocator(slots, noAllocations);
        return allocated;
    }

    public static List<List<Integer>> listAllocator(List<Integer> baseSlots, int noSecondarySlots, int noAllocations) {
        List<List<Integer>> indexes = getIndexesList(baseSlots, noSecondarySlots);
        List<List<Integer>> allocated = generalAllocator(indexes, noAllocations);
        return allocated;
    }

    public static List<Integer> getIndexes(int size) {
        return IntStream.range(0, size).collect(ArrayList::new, List::add, List::addAll);
    }

    public static List<List<Integer>> getIndexesList(List<Integer> firstIndex, int secondIndexSize) {
        List<Integer> secondIndex = getIndexes(secondIndexSize);
        List<List<Integer>> indexes = cartesianProduct(List.of(firstIndex, secondIndex));
        return indexes;
    }

    // https://stackoverflow.com/a/9496234
    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
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
