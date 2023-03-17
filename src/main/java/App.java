import World.World;

import java.io.File;
import java.io.IOException;
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
            "vehicle_seeds",
            "world_seeds"
    );

    private final static List<String> DATA_FILES = List.of(
            "product_seeds/names.txt",
            "product_seeds/catalog.txt"
    );

    public static void main(String[] args) throws Exception {
        init();
        World world = new World("2023_03_17_09_13_28_seed");

//        World world2 = new World("2023_03_15_19_50_03_seed", "2023_03_15_19_50_03_seed",
//                IntStream.range(0, 10).mapToObj(i -> "2023_03_15_19_50_03_seed" + "_" + i).collect(Collectors.toList()),
//                IntStream.range(0, 20).mapToObj(i -> "2023_03_15_19_50_03_seed" + "_" + i).collect(Collectors.toList()));
        System.out.println("Hello, World!");
    }

    public static void init() {
        for (String folder : SUB_DATA_FOLDERS) {
            File subDataFolder = new File(DATA_FOLDER, folder);
            subDataFolder.mkdirs();
        }

        for (String file : DATA_FILES) {
            File dataFile = new File(DATA_FOLDER, file);
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
