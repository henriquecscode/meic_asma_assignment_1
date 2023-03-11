package Network;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class NetworkSeed {
    static final String PATH = "data/seeds/";
    private int numberOfPorts;
    private int numberOfCities;
    private int numberOfHouses;
    private List<Integer> portDistances = new ArrayList<>();
    private List<List<Integer>> cityDistances = new ArrayList<>();
    private List<List<List<Integer>>> houseDistances = new ArrayList<>();

    public NetworkSeed(int numberOfPorts, int numberOfCities, int numberOfHouses, List<Integer> portDistances, List<List<Integer>> cityDistances, List<List<List<Integer>>> houseDistances) {
        this.numberOfPorts = numberOfPorts;
        this.numberOfCities = numberOfCities;
        this.numberOfHouses = numberOfHouses;
        this.portDistances = portDistances;
        this.cityDistances = cityDistances;
        this.houseDistances = houseDistances;
    }

    public NetworkSeed(String filename) {
        File seed = new File(PATH + filename + ".txt");
        Scanner scanner;
        try {
            scanner = new Scanner(seed).useDelimiter("( |\n)+");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.scanSeed(scanner);
    }

    public void scanSeed(Scanner scanner) {
        this.numberOfPorts = scanner.nextInt();
        this.numberOfCities = scanner.nextInt();
        this.numberOfHouses = scanner.nextInt();
        for (int i = 0; i < this.numberOfPorts; i++) {
            this.portDistances.add(scanner.nextInt());
        }
        for (int i = 0; i < this.numberOfPorts; i++) {
            List<Integer> cityDistances = new ArrayList<>();
            for (int j = 0; j < this.numberOfCities; j++) {
                cityDistances.add(scanner.nextInt());
            }
            this.cityDistances.add(cityDistances);
        }
        for (int i = 0; i < this.numberOfPorts; i++) {
            List<List<Integer>> houseDistances = new ArrayList<>();
            for (int j = 0; j < this.numberOfCities; j++) {
                List<Integer> houseDistance = new ArrayList<>();
                for (int k = 0; k < this.numberOfHouses; k++) {
                    houseDistance.add(scanner.nextInt());
                }
                houseDistances.add(houseDistance);
            }
            this.houseDistances.add(houseDistances);
        }
    }

    public void saveSeed() {
        String filename = NetworkSeed.getDate() + "_seed";
        this.saveSeed(filename);
    }

    public void saveSeed(String filename) {
        String filepath = NetworkSeed.PATH + filename + ".txt";
        PrintWriter writer;
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print(this);
        writer.close();
    }

    @Override
    public String toString() {
        String text = this.numberOfPorts + " " + this.numberOfCities + " " + this.numberOfHouses + "\n";
        for (int i = 0; i < this.numberOfPorts; i++) {
            text += this.portDistances.get(i) + " ";
        }
        text += "\n";
        for (int i = 0; i < this.numberOfPorts; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
                text += this.cityDistances.get(i).get(j) + " ";
            }
        }
        text += "\n";
        for (int i = 0; i < this.numberOfPorts; i++) {
            for (int j = 0; j < this.numberOfCities; j++) {
                for (int k = 0; k < this.numberOfHouses; k++) {
                    text += this.houseDistances.get(i).get(j).get(k) + " ";
                }
            }
        }
        return text;
    }

    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    public int getNumberOfCities() {
        return numberOfCities;
    }

    public int getNumberOfHouses() {
        return numberOfHouses;
    }

    public List<Integer> getPortDistances() {
        return portDistances;
    }

    public List<List<Integer>> getCityDistances() {
        return cityDistances;
    }

    public List<List<List<Integer>>> getHouseDistances() {
        return houseDistances;
    }

    static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
