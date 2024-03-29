package Vehicle;

import Company.Company;
import Network.Location.Location;
import Network.Route.Route;
import Vehicle.Seed.VehicleSeed;
import Vehicle.States.State;
import Vehicle.States.Idle;
import Company.Hub;
import World.World;

public class Vehicle {

    public static int vehicleId = 0;
    private final int id;
    private final String name;
    public VehicleSeed seed;
    public Company company;
    private Hub hub = null;
    private Location location = null;
    private final int cargo;
    private int filledUpCargo = 0;
    private int speed;
    private int costPerMile;
    private int upkeep;
    private State state;


    Vehicle(Company company, Location location, VehicleSeed vehicleSeed) {
        this.id = vehicleId++;
        this.name = company.getName() + "-" + "Vehicle" + this.id;
        this.seed = vehicleSeed;
        this.company = company;
        this.cargo = vehicleSeed.getCargo();
        this.speed = vehicleSeed.getSpeed();
        this.costPerMile = vehicleSeed.getCostPerMile();
        this.upkeep = vehicleSeed.getUpkeep();
        this.state = new Idle(location);
    }

    public String getName() {
        return name;
    }

    public int getCargoCapacity() {
        return cargo;
    }

    public int getFilledUpCargo() {
        return filledUpCargo;
    }

    public boolean fillUpCargo(int cargo) {
        if (filledUpCargo + cargo > this.cargo) {
            return false;
        }
        filledUpCargo += cargo;
        return true;
    }

    public boolean canFillUpCargo(int cargo) {
        return filledUpCargo + cargo <= this.cargo;
    }

    public void emptyCargo() {
        filledUpCargo = 0;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCostPerMile() {
        return costPerMile;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }

    public Hub getHub() {
        return hub;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public long getTravelTime(Route route) {
        return (long) Math.ceil(1.0 * route.getDistance() / speed * World.TICK_LENGTH);
    }

}

