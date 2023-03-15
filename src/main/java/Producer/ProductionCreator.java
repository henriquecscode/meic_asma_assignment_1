package Producer;

import Product.ProductCreator.CatalogProductCreator;
import Product.ProductCreator.ProductCreator;
import Product.ProductSeed;
import utils.CreatorInterface;

public class ProductionCreator implements CreatorInterface {
    private static double PRICE_MULT = 1;
    private final double priceMult;
    private static ProductCreator PRODUCT_CREATOR = new CatalogProductCreator();
    private final ProductCreator productCreator;

    public ProductionCreator() {
        this(PRODUCT_CREATOR, PRICE_MULT);
    }

    ProductionCreator(ProductCreator productCreator) {

        this(productCreator, PRICE_MULT);
    }

    ProductionCreator(ProductCreator productCreator, double priceMult) {
        this.productCreator = productCreator;
        this.priceMult = priceMult;
    }


    public ProductionSeed getSeed() {
        ProductSeed productSeed = productCreator.getSeed();
        return new ProductionSeed(productSeed, priceMult);
    }
}
