import World.AgentWorld;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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
        AgentWorld world = new AgentWorld("2023_03_17_09_13_28_seed");
        world.run();
        System.out.println("Hello, World!");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        world.testClientAgent();
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
