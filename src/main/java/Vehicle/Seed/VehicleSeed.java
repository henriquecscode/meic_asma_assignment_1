package Vehicle.Seed;

import utils.ClassSeed;
import utils.SeedInterface;

import java.util.Scanner;

public class VehicleSeed extends ClassSeed implements SeedInterface {
    private int cargo;
    private int speed;
    private int costPerMile;
    private int upkeep;

    public VehicleSeed() {
        super();
    }
    public VehicleSeed(int cargo, int speed, int costPerMile, int upkeep) {
        this.cargo = cargo;
        this.speed = speed;
        this.costPerMile = costPerMile;
        this.upkeep = upkeep;
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

    @Override
    public void init() {

    }

    @Override
    public void scanSeed(Scanner scanner) {
        this.cargo = scanner.nextInt();
        this.speed = scanner.nextInt();
        this.costPerMile = scanner.nextInt();
        this.upkeep = scanner.nextInt();
    }

    @Override
    public String getPath() {
        return "data/vehicle_seeds/";
    }

    @Override
    public String serialize() {
        return cargo + " " + speed + " " + costPerMile + " " + upkeep;
    }
}
