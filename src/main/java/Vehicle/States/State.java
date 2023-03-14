package Vehicle.States;

import Product.Product;
import Vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Vehicle vehicle;
    protected List<Product> cargo = new ArrayList<>();


    State(Vehicle vehicle) {
        this.vehicle = vehicle;
    }


}
