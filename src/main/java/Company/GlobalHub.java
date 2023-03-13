package Company;

import Network.Location.Port;
import Vehicle.Semi;
import Vehicle.Ship;
import Vehicle.Vehicle;

import java.util.List;

public class GlobalHub extends Hub {

    private List<Ship> ships;
    private List<Semi> semis;

    GlobalHub(Company company, Port port) {
        super(company, port);
    }

    public Port getPort() {
        return (Port) getLocation();
    }

    public void addVehicle(Ship ship) {
        ships.add(ship);
        super.addVehicle(ship);
    }

    public void addVehicle(Semi semi) {
        semis.add(semi);
        super.addVehicle(semi);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public List<Semi> getSemis() {
        return semis;
    }
}
