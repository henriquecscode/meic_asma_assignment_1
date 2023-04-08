package Network;

import utils.CreatorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// A class to set the properties of the world
public class NetworkCreator implements CreatorInterface {
    private static int NO_PORTS = 3;
    private static int NO_CITIES = 5;
    private static int NO_HOUSES = 10;
    private static int MEAN_PORTS = 50;
    private static int MEAN_CITIES = 15;
    private static int MEAN_HOUSES = 6;
    private static double STD_PORTS = 10;
    private static double STD_CITIES = 5;
    private static double STD_HOUSES = 1.2;

    private final int noPorts;
    private final int noCities;
    private final int noHouses;
    private final int meanPorts;
    private final int meanCities;
    private final int meanHouses;
    private final double stdPorts;
    private final double stdCities;
    private final double stdHouses;


    public NetworkCreator() {
        this(NO_PORTS, NO_CITIES, NO_HOUSES, MEAN_PORTS, MEAN_CITIES, MEAN_HOUSES, STD_PORTS, STD_CITIES, STD_HOUSES);
    }

    public NetworkCreator(int noPorts, int noCities, int noHouses, int meanPorts, int meanCities, int meanHouses, double stdPorts, double stdCities, double stdHouses) {
        this.noPorts = noPorts;
        this.noCities = noCities;
        this.noHouses = noHouses;
        this.meanPorts = meanPorts;
        this.meanCities = meanCities;
        this.meanHouses = meanHouses;
        this.stdPorts = stdPorts;
        this.stdCities = stdCities;
        this.stdHouses = stdHouses;
    }

    public NetworkSeed getSeed() {

        List<Integer> portDistances = new ArrayList<>();
        List<List<Integer>> cityDistances = new ArrayList<>();
        List<List<List<Integer>>> houseDistances = new ArrayList<>();
        DistanceCreator portDistanceGenerator = new DistanceCreator(meanPorts, stdPorts);
        DistanceCreator cityDistanceGenerator = new DistanceCreator(meanCities, stdCities);
        DistanceCreator houseDistanceGenerator = new DistanceCreator(meanCities, stdHouses);

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
    private final static Random random = new Random(1);

    public DistanceCreator(int mean, double standardDeviation) {
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
