package Producer;

import Product.Product;

public class Production {
    private final Product product;
    private final double priceMult;

    public Production(ProductionSeed seed) {
        this.product = new Product(seed.getProductSeed());
        this.priceMult = seed.getPriceMult();
    }


    public Product getProduct() {
        return product;
    }

    public double getPriceMult() {
        return priceMult;
    }
}
