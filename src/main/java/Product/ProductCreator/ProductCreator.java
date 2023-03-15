package Product.ProductCreator;

import Product.ProductSeed;
import utils.CreatorInterface;


public abstract class ProductCreator implements CreatorInterface {


    @Override
    public ProductSeed getSeed() {
        return null;
    }
}
