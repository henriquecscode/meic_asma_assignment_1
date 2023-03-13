package Network;

import utils.CreatorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// A class to set the properties of the world
public class NetworkCreator implements CreatorInterface {
    private final int NO_PORTS = 3;
    private final int NO_CITIES = 5;
    private final int NO_HOUSES = 10;
    private final int MEAN_PORTS = 50;
    private final int MEAN_CITIES = 15;
    private final int MEAN_HOUSES = 6;
    private final double STD_PORTS = 10;
    private final double STD_CITIES = 5;
    private final double STD_HOUSES = 1.2;


    public NetworkCreator() {
    }

    public NetworkSeed getSeed() {
        int noPorts = NO_PORTS;
        int noCities = NO_CITIES;
        int noHouses = NO_HOUSES;

        List<Integer> portDistances = new ArrayList<>();
        List<List<Integer>> cityDistances = new ArrayList<>();
        List<List<List<Integer>>> houseDistances = new ArrayList<>();
        DistanceCreator portDistanceGenerator = new DistanceCreator(this.MEAN_PORTS, this.STD_PORTS);
        DistanceCreator cityDistanceGenerator = new DistanceCreator(this.MEAN_CITIES, this.STD_CITIES);
        DistanceCreator houseDistanceGenerator = new DistanceCreator(this.MEAN_HOUSES, this.STD_HOUSES);

        for (int i = 0; i < noPorts; i++) {
            int distance = portDistanceGenerator.getDistance();
            portDistances.add(distance);
        }
        for (int i = 0; i < noPorts; i++) {
            List<Integer> perCityDistance = new ArrayList<>();
            for (int j = 0; j < noCities; j++) {
                int distance = cityDistanceGenerator.getDistance();
                perCityDistance.add(distance);
            }
            cityDistances.add(perCityDistance);
        }
        for (int i = 0; i < noPorts; i++) {
            List<List<Integer>> perPortDistance = new ArrayList<>();
            for (int j = 0; j < noCities; j++) {
                List<Integer> perCityDistance = new ArrayList<>();
                for (int k = 0; k < noHouses; k++) {
                    int distance = houseDistanceGenerator.getDistance();
                    perCityDistance.add(distance);
                }
                perPortDistance.add(perCityDistance);
            }
            houseDistances.add(perPortDistance);
        }
        NetworkSeed seed = new NetworkSeed(noPorts, noCities, noHouses, portDistances, cityDistances, houseDistances);
        return seed;
    }

}

class DistanceCreator {
    private int mean;
    private double standardDeviation;
    private Random random;

    public DistanceCreator(int mean, double standardDeviation) {
        this.random = new Random();
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public DistanceCreator(int mean) {
        this(mean, 0);
    }

    public DistanceCreator() {
        this(0, 1);
    }

    public int getDistance() {
        int distance = (int) (this.mean + this.standardDeviation * this.random.nextGaussian());
        if (distance < 1) {
            distance = 1;
        }
        return distance;
    }

}
