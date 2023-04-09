package Company;

import Network.Location.City;
import Vehicle.Vehicle;
import Vehicle.Semi;
import Vehicle.Van;

import java.util.ArrayList;
import java.util.List;

public class RegionHub extends Hub {

    private List<Semi> semis = new ArrayList<>();
    private List<Van> vans = new ArrayList<>();

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

    public List<Semi> getSemis() {
        return semis;
    }

    public List<Van> getVans() {
        return vans;
    }
}
