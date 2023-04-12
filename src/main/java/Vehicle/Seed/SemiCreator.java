package Vehicle.Seed;

import utils.CreatorInterface;

public class SemiCreator extends VehicleCreator implements CreatorInterface {

    private static int CARGO = 400;
    private static int SPEED = 120;
    private static int COST_PER_MILE = 0;
    private static int UPKEEP = 0;

    public SemiCreator() {
        super(CARGO, SPEED, COST_PER_MILE, UPKEEP);
    }

    SemiCreator(int cargo, int speed, int costPerMile, int upkeep) {
        super(cargo, speed, costPerMile, upkeep);
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
}
