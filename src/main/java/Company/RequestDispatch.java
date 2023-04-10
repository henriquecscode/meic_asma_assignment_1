package Company;

import Network.Location.Location;
import Network.Network;

public class RequestDispatch extends Dispatch {
    private static final int PARENT_ATTRIBUTES = 7;
    private long loadedOn = -1;
    private long holdingTime = -1; //time until a vehicle loads it
    private long dispatchedOn = -1;
    private long idleTime = -1; // time until the vehicle starts moving
    private long arrivedOn = -1;
    private long travelTime = -1; // travel time (deterministic from vehicle speed and distance)

    private final int requestStage;

    public RequestDispatch(Location start, Location end, String companyName, int routeStage) {
        super(start, end, companyName);
        this.requestStage = routeStage;
    }

    public RequestDispatch(Network network, String dispatch) {
        super(network, dispatch);
        String[] dispatchInfo = dispatch.split(SEP);
        int i = PARENT_ATTRIBUTES;
        this.loadedOn = Integer.parseInt(dispatchInfo[i + 0]);
        this.holdingTime = Integer.parseInt(dispatchInfo[i + 1]);
        this.dispatchedOn = Integer.parseInt(dispatchInfo[i + 2]);
        this.idleTime = Integer.parseInt(dispatchInfo[i + 3]);
        this.arrivedOn = Integer.parseInt(dispatchInfo[i + 4]);
        this.travelTime = Integer.parseInt(dispatchInfo[i + 5]);
        this.requestStage = Integer.parseInt(dispatchInfo[i + 6]);
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

    public int getRequestStage() {
        return requestStage;
    }

    @Override
    public String toString() {
        return super.toString() + SEP + loadedOn + SEP + holdingTime + SEP + dispatchedOn + SEP + idleTime + SEP + arrivedOn + SEP + travelTime + SEP + requestStage;
    }
}
