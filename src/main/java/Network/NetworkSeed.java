package Network;

import utils.ClassSeed;
import utils.SeedInterface;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class NetworkSeed extends ClassSeed implements SeedInterface {
    static final String PATH = "data/network_seeds/";
    private int numberOfPorts;
    private int numberOfCities;
    private int numberOfHouses;
    private List<Integer> portDistances;
    private List<List<Integer>> cityDistances;
    private List<List<List<Integer>>> houseDistances;

    public void init() {
        portDistances = new ArrayList<>();
        cityDistances = new ArrayList<>();
        houseDistances = new ArrayList<>();
    }

    public NetworkSeed() {
        super();
    }

    public NetworkSeed(int numberOfPorts, int numberOfCities, int numberOfHouses, List<Integer> portDistances, List<List<Integer>> cityDistances, List<List<List<Integer>>> houseDistances) {
        super();
        this.numberOfPorts = numberOfPorts;
        this.numberOfCities = numberOfCities;
        this.numberOfHouses = numberOfHouses;
        this.portDistances = portDistances;
        this.cityDistances = cityDistances;
        this.houseDistances = houseDistances;
    }

    public NetworkSeed(String filename) {
        super(filename);
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

    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
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
        text += "\n";
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

}
