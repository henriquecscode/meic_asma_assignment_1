package Company;

import Network.Location.Location;
import Network.Network;
import Vehicle.Vehicle;

public class Dispatch {
    private static String SEP = ",";
    private Location start;
    private Location end;
    private int loadedOn = -1;
    private int holdingTime = -1; //time until a vehicle loads it
    private int dispatchedOn = -1;
    private int idleTime = -1; // time until the vehicle starts moving
    private int arrivedOn = -1;
    private int travelTime = -1; // travel time (deterministic from vehicle speed and distance)
    private final String companyName;
    private String vehicleName = "";
    private int vehicleCapacity = -1; // vehicle capacity
    private int vehicleOccupiedSpace = -1; // quantity * volume at time of dispatch
    private int cargoOccupancy = -1; // occupied space / capacity

    public Dispatch(Location start, Location end, String companyName) {
        this.start = start;
        this.end = end;
        this.companyName = companyName;
    }

    public Dispatch(Network network, String dispatch) {
        String[] dispatchInfo = dispatch.split(SEP);
        this.start = network.getLocation(dispatchInfo[0]);
        this.end = network.getLocation(dispatchInfo[1]);
        this.loadedOn = Integer.parseInt(dispatchInfo[2]);
        this.holdingTime = Integer.parseInt(dispatchInfo[3]);
        this.dispatchedOn = Integer.parseInt(dispatchInfo[4]);
        this.idleTime = Integer.parseInt(dispatchInfo[5]);
        this.arrivedOn = Integer.parseInt(dispatchInfo[6]);
        this.travelTime = Integer.parseInt(dispatchInfo[7]);
        this.companyName = dispatchInfo[8];
        this.vehicleName = dispatchInfo[9];
        this.vehicleCapacity = Integer.parseInt(dispatchInfo[10]);
        this.vehicleOccupiedSpace = Integer.parseInt(dispatchInfo[11]);
        this.cargoOccupancy = Integer.parseInt(dispatchInfo[12]);
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public int getLoadedOn() {
        return loadedOn;
    }

    public void setLoadedOn(int loadedOn) {
        this.loadedOn = loadedOn;
    }

    public int getHoldingTime() {
        return holdingTime;
    }

    public void setHoldingTime(int holdingTime) {
        this.holdingTime = holdingTime;
    }

    public int getDispatchedOn() {
        return dispatchedOn;
    }

    public void setDispatchedOn(int dispatchedOn) {
        this.dispatchedOn = dispatchedOn;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getArrivedOn() {
        return arrivedOn;
    }

    public void setArrivedOn(int arrivedOn) {
        this.arrivedOn = arrivedOn;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public String getCompanyName() {
        return companyName;
    }


    public void setVehicle(Vehicle vehicle) {
        this.vehicleName = vehicle.getName();
        this.vehicleCapacity = vehicle.getCargo();
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getVehicleOccupiedSpace() {
        return vehicleOccupiedSpace;
    }

    public void setVehicleOccupiedSpace(int vehicleOccupiedSpace) {
        this.vehicleOccupiedSpace = vehicleOccupiedSpace;
        this.cargoOccupancy = vehicleOccupiedSpace / vehicleCapacity;
    }

    public int getCargoOccupancy() {
        return cargoOccupancy;
    }

    @Override
    public String toString() {
        return start.getName() + SEP + end.getName() + SEP + loadedOn + SEP + holdingTime + SEP + dispatchedOn + SEP + idleTime + SEP + arrivedOn + SEP + travelTime + SEP + companyName + SEP + vehicleName + SEP + vehicleCapacity + SEP + vehicleOccupiedSpace + SEP + cargoOccupancy;
    }
}
