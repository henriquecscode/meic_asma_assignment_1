package Company;

import Network.Location.Location;
import Network.Network;
import Vehicle.Vehicle;

public class Dispatch {
    private static String SEP = ",";
    private Location start;
    private Location end;
    private long loadedOn = -1;
    private long holdingTime = -1; //time until a vehicle loads it
    private long dispatchedOn = -1;
    private long idleTime = -1; // time until the vehicle starts moving
    private long arrivedOn = -1;
    private long travelTime = -1; // travel time (deterministic from vehicle speed and distance)
    private final String companyName;
    private String vehicleName = "";
    private int vehicleCapacity = -1; // vehicle capacity
    private int vehicleFilledUpCargo = -1; // quantity * volume at time of dispatch
    private int cargoOccupancy = -1; // occupied space / capacity

    private final int requestStage;

    public Dispatch(Location start, Location end, String companyName, int routeStage) {
        this.start = start;
        this.end = end;
        this.companyName = companyName;
        this.requestStage = routeStage;
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
        this.vehicleFilledUpCargo = Integer.parseInt(dispatchInfo[11]);
        this.cargoOccupancy = Integer.parseInt(dispatchInfo[12]);
        this.requestStage = Integer.parseInt(dispatchInfo[13]);
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

    public long getLoadedOn() {
        return loadedOn;
    }

    public void setLoadedOn(long loadedOn) {
        this.loadedOn = loadedOn;
    }

    public long getHoldingTime() {
        return holdingTime;
    }

    public void setHoldingTime(long holdingTime) {
        this.holdingTime = holdingTime;
    }

    public long getDispatchedOn() {
        return dispatchedOn;
    }

    public void setDispatchedOn(long dispatchedOn) {
        this.dispatchedOn = dispatchedOn;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public long getArrivedOn() {
        return arrivedOn;
    }

    public void setArrivedOn(long arrivedOn) {
        this.arrivedOn = arrivedOn;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
        this.travelTime = travelTime;
    }

    public String getCompanyName() {
        return companyName;
    }


    public void setVehicle(Vehicle vehicle) {
        this.vehicleName = vehicle.getName();
        this.vehicleCapacity = vehicle.getCargoCapacity();
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getVehicleFilledUpCargo() {
        return vehicleFilledUpCargo;
    }

    public void setVehicleFilledUpCargo(int vehicleFilledUpCargo) {
        this.vehicleFilledUpCargo = vehicleFilledUpCargo;
        this.cargoOccupancy = vehicleFilledUpCargo / vehicleCapacity;
    }

    public int getCargoOccupancy() {
        return cargoOccupancy;
    }

    public int getRequestStage() {
        return requestStage;
    }

    @Override
    public String toString() {
        return start.getName() + SEP + end.getName() + SEP + loadedOn + SEP + holdingTime + SEP + dispatchedOn + SEP + idleTime + SEP + arrivedOn + SEP + travelTime + SEP + companyName + SEP + vehicleName + SEP + vehicleCapacity + SEP + vehicleFilledUpCargo + SEP + cargoOccupancy + SEP + requestStage;
    }
}
