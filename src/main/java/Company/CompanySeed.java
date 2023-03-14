package Company;

import utils.ClassSeed;
import utils.SeedInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CompanySeed extends ClassSeed implements SeedInterface {
    static final String PATH = "data/company_seeds/";
    private int numberOfGlobal;
    private int numberOfRegional;
    private int numberOfVans;
    private int numberOfSemisInGlobal, numberOfSemisInRegional;
    private int numberOfShips;
    private List<Integer> globalHubs;
    private List<List<Integer>> regionalHubs; // List<<Integer,Integer>>
    private List<Integer> shipLocations, globalSemiLocations;
    private List<List<Integer>> regionalSemiLocations, vanLocations; // List<<Integer,Integer>>

    public CompanySeed(int numberOfGlobal, int numberOfRegional, int numberOfShips, int numberOfSemisInGlobal, int numberOfSemisInRegional, int numberOfVans, List<Integer> globalHubs, List<List<Integer>> regionalHubs, List<Integer> shipLocations, List<Integer> globalSemiLocations, List<List<Integer>> regionalSemiLocations, List<List<Integer>> vanLocations) {
        super();
        this.numberOfGlobal = numberOfGlobal;
        this.numberOfRegional = numberOfRegional;
        this.numberOfShips = numberOfShips;
        this.numberOfSemisInGlobal = numberOfSemisInGlobal;
        this.numberOfSemisInRegional = numberOfSemisInRegional;
        this.numberOfVans = numberOfVans;
        this.globalHubs = globalHubs;
        this.regionalHubs = regionalHubs;
        this.shipLocations = shipLocations;
        this.globalSemiLocations = globalSemiLocations;
        this.regionalSemiLocations = regionalSemiLocations;
        this.vanLocations = vanLocations;
    }

    public CompanySeed(String filename) {
        super(filename);
    }

    public void init() {
        globalHubs = new ArrayList<>();
        regionalHubs = new ArrayList<>();
        shipLocations = new ArrayList<>();
        globalSemiLocations = new ArrayList<>();
        regionalSemiLocations = new ArrayList<>();
        vanLocations = new ArrayList<>();
    }

    public void scanSeed(Scanner scanner) {
        this.numberOfGlobal = scanner.nextInt();
        this.numberOfRegional = scanner.nextInt();
        this.numberOfShips = scanner.nextInt();
        this.numberOfSemisInGlobal = scanner.nextInt();
        this.numberOfSemisInRegional = scanner.nextInt();
        this.numberOfVans = scanner.nextInt();

        for (int i = 0; i < this.numberOfGlobal; i++) {
            this.globalHubs.add(scanner.nextInt());
        }

        for (int i = 0; i < this.numberOfRegional; i++) {
            List<Integer> temp = List.of(scanner.nextInt(), scanner.nextInt());
            this.regionalHubs.add(temp);
        }

        for (int i = 0; i < this.numberOfShips; i++) {
            this.shipLocations.add(scanner.nextInt());
        }

        for (int i = 0; i < this.numberOfSemisInGlobal; i++) {
            this.globalSemiLocations.add(scanner.nextInt());
        }

        for (int i = 0; i < this.numberOfSemisInRegional; i++) {
            List<Integer> temp = List.of(scanner.nextInt(), scanner.nextInt());
            this.regionalSemiLocations.add(temp);
        }

        for (int i = 0; i < this.numberOfVans; i++) {
            List<Integer> temp = List.of(scanner.nextInt(), scanner.nextInt());
            this.vanLocations.add(temp);
        }

    }

    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        String text = this.numberOfGlobal + " " +
                this.numberOfRegional + " " +
                this.numberOfShips + " " +
                this.numberOfSemisInGlobal + " " +
                this.numberOfSemisInRegional + " " +
                this.numberOfVans + '\n';
        for (int i = 0; i < this.numberOfGlobal; i++) {
            text += this.globalHubs.get(i) + " ";
        }
        text += '\n';

        for (int i = 0; i < this.numberOfRegional; i++) {
            text += this.regionalHubs.get(i).get(0) + " " + this.regionalHubs.get(i).get(1) + " ";
        }
        text += '\n';

        for (int i = 0; i < this.numberOfShips; i++) {
            text += this.shipLocations.get(i) + " ";
        }
        text += '\n';

        for (int i = 0; i < this.numberOfSemisInGlobal; i++) {
            text += this.globalSemiLocations.get(i) + " ";
        }
        text += '\n';

        for (int i = 0; i < this.numberOfSemisInRegional; i++) {
            text += this.regionalSemiLocations.get(i).get(0) + " " + this.regionalSemiLocations.get(i).get(1) + " ";
        }
        text += '\n';

        for (int i = 0; i < this.numberOfVans; i++) {
            text += this.vanLocations.get(i).get(0) + " " + this.vanLocations.get(i).get(1) + " ";
        }

        return text;
    }

    public int getNumberOfGlobal() {
        return numberOfGlobal;
    }

    public int getNumberOfRegional() {
        return numberOfRegional;
    }

    public int getNumberOfVans() {
        return numberOfVans;
    }

    public int getNumberOfSemisInGlobal() {
        return numberOfSemisInGlobal;
    }

    public int getNumberOfSemisInRegional() {
        return numberOfSemisInRegional;
    }

    public int getNumberOfShips() {
        return numberOfShips;
    }

    public List<Integer> getGlobalHubs() {
        return globalHubs;
    }

    public List<List<Integer>> getRegionalHubs() {
        return regionalHubs;
    }

    public List<Integer> getShipLocations() {
        return shipLocations;
    }

    public List<Integer> getGlobalSemiLocations() {
        return globalSemiLocations;
    }

    public List<List<Integer>> getRegionalSemiLocations() {
        return regionalSemiLocations;
    }

    public List<List<Integer>> getVanLocations() {
        return vanLocations;
    }
}
