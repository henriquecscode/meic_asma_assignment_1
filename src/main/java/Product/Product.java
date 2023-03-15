package Product;

import java.util.Objects;

public class Product {
    private ProductSeed seed;
    private String name;
    private int volume;
    private int basePrice;

    public Product(String name, int volume, int basePrice) {
        this.seed = new ProductSeed(volume, basePrice);
        this.name = name;
        this.volume = volume;
        this.basePrice = basePrice;
    }

    public ProductSeed getSeed() {
        return seed;
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

    @Override
    public int hashCode() {
        return Objects.hash(volume, basePrice);
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}
