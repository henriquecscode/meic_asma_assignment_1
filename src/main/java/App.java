import World.World;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    private final static String DATA_FOLDER = "data";
    private final static List<String> SUB_DATA_FOLDERS = List.of(
            "company_seeds",
            "fleet_seeds",
            "network_seeds",
            "producer_seeds",
            "product_seeds",
            "vehicle_seeds"
    );

    public static void main(String[] args) throws Exception {
        initDir();
        World world = new World();

//        World world2 = new World("2023_03_14_20_22_08_seed", "2023_03_14_20_22_08_seed",
//                IntStream.range(0, 10).mapToObj(i -> "2023_03_14_20_22_08_seed_" + i).collect(Collectors.toList()),
//                IntStream.range(0, 30).mapToObj(i -> "2023_03_14_20_22_08_seed_" + i).collect(Collectors.toList()));
        System.out.println("Hello, World!" );
    }

    public static void initDir() {
        for(String folder: SUB_DATA_FOLDERS){
            File subDataFolder = new File(DATA_FOLDER, folder);
            subDataFolder.mkdirs();
        }

    }
}
