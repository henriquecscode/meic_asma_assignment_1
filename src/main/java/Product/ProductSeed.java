package Product;

import utils.ClassSeed;
import utils.SeedInterface;

import java.util.Scanner;

public class ProductSeed extends ClassSeed implements SeedInterface {
    private static final String PATH = "data/product_seeds/";
    private String name;
    private int volume;
    private int basePrice;

    public ProductSeed() {
        super();
    }

    public ProductSeed(String name, int volumne, int basePrice) {
        super();
        this.name = name;
        this.volume = volumne;
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
        this.name = scanner.next();
        this.volume = scanner.nextInt();
        this.basePrice = scanner.nextInt();
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        return this.name + " " + this.volume + " " + this.basePrice;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public int getBasePrice() {
        return basePrice;
    }
}
