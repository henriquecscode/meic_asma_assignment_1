package Vehicle;

import Company.Company;
import Network.Location.Port;
import Vehicle.Seed.VehicleSeed;

public class Ship extends Vehicle {
    public Ship(Company company, Port port, VehicleSeed seed) {
        super(company, port, seed);
    }
}
