package Company;

import Network.Location.Location;
import Network.Network;

public class RequestDispatch extends Dispatch {
    private static final int PARENT_ATTRIBUTES = 7;
    private long startedOn = -1;
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
        this.startedOn = Integer.parseInt(dispatchInfo[i + 0]);
        this.loadedOn = Integer.parseInt(dispatchInfo[i + 1]);
        this.holdingTime = Integer.parseInt(dispatchInfo[i + 2]);
        this.dispatchedOn = Integer.parseInt(dispatchInfo[i + 3]);
        this.idleTime = Integer.parseInt(dispatchInfo[i + 4]);
        this.arrivedOn = Integer.parseInt(dispatchInfo[i + 5]);
        this.travelTime = Integer.parseInt(dispatchInfo[i + 6]);
        this.requestStage = Integer.parseInt(dispatchInfo[i + 7]);

    }

    public long getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(long startedOn) {
        if(this.startedOn == -1) {
            this.startedOn = startedOn;
        }
    }

    public long getLoadedOn() {
        return loadedOn;
    }

    public void setLoadedOn(long loadedOn) {
        this.loadedOn = loadedOn;
        this.holdingTime = loadedOn - startedOn;
    }

    public long getHoldingTime() {
        return holdingTime;
    }


    public long getDispatchedOn() {
        return dispatchedOn;
    }

    public void setDispatchedOn(long dispatchedOn) {
        this.dispatchedOn = dispatchedOn;
        this.idleTime = this.dispatchedOn - this.loadedOn;
    }

    public long getIdleTime() {
        return idleTime;
    }


    public long getArrivedOn() {
        return arrivedOn;
    }

    public void setArrivedOn(long arrivedOn) {
        this.arrivedOn = arrivedOn;
        this.travelTime = this.arrivedOn - this.dispatchedOn;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public int getRequestStage() {
        return requestStage;
    }

    @Override
    public String toString() {
        return super.toString() + SEP + startedOn + SEP + loadedOn + SEP + holdingTime + SEP + dispatchedOn + SEP + idleTime + SEP + arrivedOn + SEP + travelTime + SEP + requestStage;
    }
}
