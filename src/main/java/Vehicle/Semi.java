package Vehicle;

import Company.Company;
import Network.Location.City;
import Network.Location.Port;
import Vehicle.Seed.VehicleSeed;

public class Semi extends Vehicle {
    public Semi(Company company, City city, VehicleSeed seed) {
        super(company, city, seed);
    }

    public Semi(Company company, Port port, VehicleSeed seed) {
        super(company, port, seed);
    }
}
