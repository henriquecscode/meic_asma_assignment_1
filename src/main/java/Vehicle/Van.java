package Vehicle;

import Network.Location.City;
import Vehicle.Seed.VehicleSeed;
import Company.Company;

public class Van extends Vehicle {

    public Van(Company company, City city, VehicleSeed seed) {
        super(company, city, seed);
    }
}
