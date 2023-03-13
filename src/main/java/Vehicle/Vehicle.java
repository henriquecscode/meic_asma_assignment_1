package Vehicle;

import Company.Company;
import Network.Location.Location;
import Vehicle.Seed.VehicleSeed;
import Vehicle.States.State;
import Vehicle.States.Idle;

public class Vehicle {

    public VehicleSeed seed;
    public Company company;
    private int cargo;
    private int speed;
    private int costPerMile;
    private int upkeep;
    private State state;


    Vehicle(Company company, Location location, VehicleSeed vehicleSeed) {
        this.seed = vehicleSeed;
        this.company = company;
        this.cargo = vehicleSeed.getCargo();
        this.speed = vehicleSeed.getSpeed();
        this.costPerMile = vehicleSeed.getCostPerMile();
        this.upkeep = vehicleSeed.getUpkeep();
        this.state = new Idle(this, location);
    }

    public int getCargo() {
        return cargo;
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
}

