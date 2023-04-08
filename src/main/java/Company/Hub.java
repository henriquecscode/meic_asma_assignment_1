package Company;

import Network.Location.Location;
import Vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Hub {

    private Company company;
    private Location location;
    private List<Vehicle> vehicles = new ArrayList<>();

    Hub(Company company, Location location) {
        this.location = location;
        this.location.addHub(this);
    }

    public Location getLocation() {
        return location;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Company getCompany() {
        return company;
    }
}
