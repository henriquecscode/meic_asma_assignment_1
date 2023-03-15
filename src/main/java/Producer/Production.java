package Producer;

import Product.Product;
import Product.Catalog;

public class Production {
    private final Product product;
    private final double priceMult;

    public Production(ProductionSeed seed) {
        this.product = Catalog.getProduct(seed.getProductSeed());
        this.priceMult = seed.getPriceMult();
    }


    public Product getProduct() {
        return product;
    }

    public double getPriceMult() {
        return priceMult;
    }
}
