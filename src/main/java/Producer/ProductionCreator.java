package Producer;

import Product.ProductCreator;
import Product.ProductSeed;
import utils.ClassSeed;
import utils.CreatorInterface;

public class ProductionCreator implements CreatorInterface {
    private static double PRICE_MULT = 1;
    private final double priceMult;
    private static ProductCreator PRODUCT_CREATOR = new ProductCreator();
    private final ProductCreator productCreator;

    ProductionCreator(ProductCreator productCreator) {
        this(PRODUCT_CREATOR, PRICE_MULT);
    }

    ProductionCreator(ProductCreator productCreator, double priceMult) {
        this.productCreator = productCreator;
        this.priceMult = priceMult;
    }


    public ProductionSeed getSeed() {
        ProductSeed productSeed = (ProductSeed) productCreator.getSeed();
        return new ProductionSeed(productSeed, priceMult);
    }
}
