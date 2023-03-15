package Product;

import utils.ClassSeed;
import utils.SeedInterface;

import java.util.Scanner;

public class ProductSeed extends ClassSeed implements SeedInterface {
    private static final String PATH = "data/product_seeds/";
    private int volume;
    private int basePrice;

    public ProductSeed() {
        super();
    }

    public ProductSeed(int volume, int basePrice) {
        super();
        this.volume = volume;
        this.basePrice = basePrice;
    }

    public ProductSeed(String filename) {
        super(filename);
    }

    @Override
    public void init() {

    }

    @Override
    public void scanSeed(Scanner scanner) {
        this.volume = scanner.nextInt();
        this.basePrice = scanner.nextInt();
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        return this.volume + " " + this.basePrice;
    }

    public int getVolume() {
        return volume;
    }

    public int getBasePrice() {
        return basePrice;
    }
}
