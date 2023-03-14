package Product;

public class Product {
    private ProductSeed seed;
    private String name;
    private int volume;
    private int basePrice;

    public Product(ProductSeed seed) {
        this.seed = seed;
        this.name = seed.getName();
        this.volume = seed.getVolume();
        this.basePrice = seed.getBasePrice();
    }
}
