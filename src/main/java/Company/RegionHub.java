package Company;

import Network.Location.City;
import Vehicle.Vehicle;
import Vehicle.Semi;
import Vehicle.Van;

import java.util.List;

public class RegionHub extends Hub {

    private List<Semi> semis;
    private List<Van> vans;

    RegionHub(Company company, City city) {
        super(company, city);
    }

    public City getCity() {
        return (City) getLocation();
    }

    public void addVehicle(Semi semi) {
        semis.add(semi);
        super.addVehicle(semi);
    }

    public void addVehicle(Van van) {
        vans.add(van);
        super.addVehicle(van);
    }

}
