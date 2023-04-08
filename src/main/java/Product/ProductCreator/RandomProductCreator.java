package Product.ProductCreator;

import Producer.ProductionCreator;
import Product.Catalog;
import Product.ProductSeed;
import utils.CreatorInterface;

import java.util.Random;

public class RandomProductCreator extends ProductCreator implements CreatorInterface {
    private final static Random random = new Random(1);
    private static int MEAN_VOLUME = 20;
    private static int MEAN_BASE_PRICE = 10;
    private static double STD_VOLUME = 5;
    private static double STD_BASE_PRICE = 1.3;
    private final int meanVolume;
    private final int meanBasePrice;
    private final double stdVolume;
    private final double stdBasePrice;


    public RandomProductCreator() {
        this(MEAN_VOLUME, MEAN_BASE_PRICE, STD_VOLUME, STD_BASE_PRICE);
    }

    public RandomProductCreator(int meanVolume, int meanBasePrice, double stdVolume, double stdBasePrice) {
        this.meanVolume = meanVolume;
        this.meanBasePrice = meanBasePrice;
        this.stdVolume = stdVolume;
        this.stdBasePrice = stdBasePrice;
    }

    public ProductSeed getSeed() {
        int volume = getRandomMetric(meanVolume, stdVolume);
        int basePrice = getRandomMetric(meanBasePrice, stdBasePrice);
        return Catalog.getProductSeed(volume, basePrice);
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
