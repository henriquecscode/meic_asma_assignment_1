package Client;


import Network.Location.Location;

public class Dispatch {
    private final Location location;
    private final int dispatchTime;

    public Dispatch(Location location, int dispatchTime) {
        this.location = location;
        this.dispatchTime = dispatchTime;
    }
}