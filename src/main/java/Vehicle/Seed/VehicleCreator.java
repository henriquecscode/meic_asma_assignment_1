package Vehicle.Seed;

import utils.CreatorInterface;

public class VehicleCreator implements CreatorInterface {
    private static int CARGO = 0;
    private static int SPEED = 0;
    private static int COST_PER_MILE = 0;
    private static int UPKEEP = 0;
    private final int cargo;
    private final int speed;
    private final int costPerMile;
    private final int upkeep;

    public VehicleCreator() {
        this.cargo = getCargo();
        this.speed = getSpeed();
        this.costPerMile = getCostPerMile();
        this.upkeep = getUpkeep();
    }

    public VehicleCreator(int cargo, int speed, int costPerMile, int upkeep) {
        this.cargo = cargo;
        this.speed = speed;
        this.costPerMile = costPerMile;
        this.upkeep = upkeep;
    }

    public static int getCargo() {
        return CARGO;
    }

    public static int getSpeed() {
        return SPEED;
    }

    public static int getCostPerMile() {
        return COST_PER_MILE;
    }

    public static int getUpkeep() {
        return UPKEEP;
    }

    public VehicleSeed getSeed() {
        return new VehicleSeed(cargo, speed, costPerMile, upkeep);
    }
}
