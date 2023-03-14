package Product;

import utils.CreatorInterface;

import java.util.Random;

public class ProductCreator implements CreatorInterface {
    private final static Random random = new Random();
    private static int MEAN_VOLUME = 0;
    private static int MEAN_BASE_PRICE = 0;
    private static double STD_VOLUME = 0;
    private static double STD_BASE_PRICE = 0;
    private final int meanVolume;
    private final int meanBasePrice;
    private final double stdVolume;
    private final double stdBasePrice;


    public ProductCreator() {
        this(MEAN_VOLUME, MEAN_BASE_PRICE, STD_VOLUME, STD_BASE_PRICE);
    }

    public ProductCreator(int meanVolume, int meanBasePrice, double stdVolume, double stdBasePrice) {
        this.meanVolume = meanVolume;
        this.meanBasePrice = meanBasePrice;
        this.stdVolume = stdVolume;
        this.stdBasePrice = stdBasePrice;
    }

    public ProductSeed getSeed() {
        int volume = getRandomMetric(meanVolume, stdVolume);
        int basePrice = getRandomMetric(meanBasePrice, stdBasePrice);
        return new ProductSeed("productName", volume, basePrice);
    }

    private int getRandomMetric(int mean, double std) {
        int metric = (int) (mean + std * this.random.nextGaussian());
        if (metric < 1) {
            metric = 1;
        }
        return metric;
    }

    private double getRandomMetric(double mean, double std) {
        while (true) {
            double metric = mean + std * this.random.nextGaussian();
            if (metric > 0) {
                return metric;
            }
        }
    }
}
