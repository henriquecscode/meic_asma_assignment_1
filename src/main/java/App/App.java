package App;

import World.AgentWorld;
import World.World;
import utils.GetDate;

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

    private final static String EXECUTIONS_FOLDER = "execution";
    private final static String DEFAULT_WORLD_SEED = "2023_04_08_15_27_41_seed";
    private static String worldSeed = DEFAULT_WORLD_SEED;
    public static String executionLogFolder = "";
    public static String executionLogFolderSettings = "";

    public static void run(String[] args) {
        if (args.length > 0) {
            worldSeed = args[0];
        }
        init();
        AgentWorld world = new AgentWorld(worldSeed);
        world.startAgents();
        world.log();
        world.start();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        input = scanner.nextLine();
        while (!input.equals("exit")) {
            world.testClientAgent();
            input = scanner.nextLine();
        }
        System.exit(0);
    }

    public static void seeder() {
        World world = new World();
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

        File executionsFolder = new File(EXECUTIONS_FOLDER);
        executionsFolder.mkdirs();

        executionLogFolder = executionsFolder + "/" + worldSeed + "_" + GetDate.getDate();
        File worldSeedFolder = new File(executionLogFolder);
        worldSeedFolder.mkdirs();

        executionLogFolderSettings = executionLogFolder + "/settings";
        File worldSeedSettingsFolder = new File(executionLogFolderSettings);
        worldSeedSettingsFolder.mkdirs();


    }
}
