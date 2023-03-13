import World.World;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) throws Exception {
        World world = new World();

        World world2 = new World("2023_03_13_15_47_22_seed", "2023_03_13_15_47_22_seed",
                IntStream.range(0, 10).mapToObj(i -> "2023_03_13_15_47_22_seed_"+i).collect(Collectors.toList()));
        System.out.println("Hello, World!");
    }
}
