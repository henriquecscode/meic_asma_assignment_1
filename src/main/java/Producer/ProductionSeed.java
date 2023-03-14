package Producer;

import Product.ProductSeed;
import utils.ClassSeed;
import utils.SeedInterface;

import java.util.Scanner;

public class ProductionSeed extends ClassSeed implements SeedInterface {

    private static final String PATH = "data/production_seeds/";
    private ProductSeed productSeed;
    private double priceMult;

    public ProductionSeed() {
        super();
    }

    public ProductionSeed(ProductSeed productSeed, double priceMult) {
        super();
        this.productSeed = productSeed;
        this.priceMult = priceMult;
    }

    ProductionSeed(String filename) {
        super(filename);
    }

    @Override
    public void init() {
        productSeed = new ProductSeed();
    }

    @Override
    public void scanSeed(Scanner scanner) {
        productSeed.scanSeed(scanner);
        priceMult = scanner.nextDouble();
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        return productSeed.serialize() + " " + priceMult;
    }

    public ProductSeed getProductSeed() {
        return productSeed;
    }

    public double getPriceMult() {
        return priceMult;
    }
}
