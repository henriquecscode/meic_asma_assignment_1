package Client;

import Network.Location.Location;
import Product.Product;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final Product product;
    private final int quantity;
    private final int price;
    private final Location origin;
    private final Location destination;
    private List<Dispatch> dispatches = new ArrayList<>();

    public Order(Product product, int quantity, int price, Location origin, Location destination) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.origin = origin;
        this.destination = destination;
    }
}

