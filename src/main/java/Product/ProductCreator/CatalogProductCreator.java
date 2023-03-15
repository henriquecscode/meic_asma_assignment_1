package Product.ProductCreator;

import Product.Catalog;
import Product.ProductSeed;
import utils.ClassSeed;
import utils.CreatorInterface;

public class CatalogProductCreator extends ProductCreator implements CreatorInterface {
    private int creationCounter = 0;

    public CatalogProductCreator() {
    }

    @Override
    public ProductSeed getSeed() {
        ProductSeed seed = Catalog.get(creationCounter).getSeed();
        creationCounter = (creationCounter + 1) % Catalog.size();
        return seed;
    }
}
