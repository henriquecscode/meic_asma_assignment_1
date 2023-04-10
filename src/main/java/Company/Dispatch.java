package Company;

import Network.Network;
import Network.Location.Location;
import Vehicle.Vehicle;

public class Dispatch {
    protected static String SEP = ",";

    protected Location start;
    protected Location end;
    protected final String companyName;
    protected String vehicleName = "";
    protected int vehicleCapacity = -1; // vehicle capacity
    protected int vehicleFilledUpCargo = -1; // quantity * volume at time of dispatch
    protected int cargoOccupancy = -1; // occupied space / capacity

    public Dispatch(Location start, Location end, String companyName) {
        this.start = start;
        this.end = end;
        this.companyName = companyName;
    }

    public Dispatch(Network network, String dispatch) {
        String[] dispatchInfo = dispatch.split(SEP);
        this.start = network.getLocation(dispatchInfo[0]);
        this.end = network.getLocation(dispatchInfo[1]);
        this.companyName = dispatchInfo[2];
        this.vehicleName = dispatchInfo[3];
        this.vehicleCapacity = Integer.parseInt(dispatchInfo[4]);
        this.vehicleFilledUpCargo = Integer.parseInt(dispatchInfo[5]);
        this.cargoOccupancy = Integer.parseInt(dispatchInfo[6]);
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

    @Override
    public String toString() {
        return start.getName() + SEP + end.getName() + SEP + companyName + SEP + vehicleName + SEP + vehicleCapacity + SEP + vehicleFilledUpCargo + SEP + cargoOccupancy;
    }

    public boolean equals(Dispatch dispatch) {
        boolean equalVehicle;
        if (dispatch.getVehicleName() != "" && vehicleName != "") {
            equalVehicle = dispatch.getVehicleName().equals(vehicleName);
        } else {
            equalVehicle = true;
        }
        return start.equals(dispatch.getStart()) && end.equals(dispatch.getEnd()) && companyName.equals(dispatch.getCompanyName()) && equalVehicle;
    }
}

