package Vehicle.Seed;

import utils.ClassSeed;
import utils.SeedInterface;

import java.util.Scanner;

public class FleetSeed extends ClassSeed implements SeedInterface {
    private static final String PATH = "data/fleet_seeds/";
    private VehicleSeed shipSeed, semiSeed, vanSeed;

    public FleetSeed() {
        super();
    }

    public FleetSeed(String filename) {
        super(filename);
    }

    public FleetSeed(VehicleSeed shipSeed, VehicleSeed semiSeed, VehicleSeed vanSeed) {
        this.shipSeed = shipSeed;
        this.semiSeed = semiSeed;
        this.vanSeed = vanSeed;
    }

    public VehicleSeed getShipSeed() {
        return shipSeed;
    }

    public VehicleSeed getSemiSeed() {
        return semiSeed;
    }

    public VehicleSeed getVanSeed() {
        return vanSeed;
    }

    @Override
    public void init() {
        this.shipSeed = new VehicleSeed();
        this.semiSeed = new VehicleSeed();
        this.vanSeed = new VehicleSeed();
    }

    @Override
    public void scanSeed(Scanner scanner) {
        shipSeed.scanSeed(scanner);
        semiSeed.scanSeed(scanner);
        vanSeed.scanSeed(scanner);
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String serialize() {
        return shipSeed.serialize() + "\n" + semiSeed.serialize() + "\n" + vanSeed.serialize();
    }
}
