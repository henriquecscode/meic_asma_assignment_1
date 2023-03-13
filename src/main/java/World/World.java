package World;

import Company.Company;
import Company.CompanyCreator;
import Company.CompanySeed;
import Network.NetworkCreator;
import Network.NetworkSeed;
import Network.Network;
import Vehicle.Seed.*;
import Vehicle.Vehicle;
import utils.ClassSeed;

import java.util.ArrayList;
import java.util.List;

public class World {
    private Network network;
    private FleetSeed fleetSeed;
    private List<Company> companies;

    public World() {
        this.makeNetwork();
        this.makeFleet();
        this.makeCompanies();
    }

    public World(String networkSeedFilename, String fleetSeedFilename, List<String> companySeedFilenames) {
        this.makeNetwork(networkSeedFilename);
        this.makeFleet(fleetSeedFilename);
        this.makeCompanies(companySeedFilenames);
    }

    public void makeNetwork() {
        NetworkCreator networkCreator = new NetworkCreator();
        NetworkSeed seed = networkCreator.getSeed();
        seed.saveSeed();
        this.network = new Network(seed);
    }

    public void makeNetwork(String networkSeedFilename) {
        NetworkSeed seed = new NetworkSeed(networkSeedFilename);
        this.network = new Network(seed);
    }

    public void makeFleet() {
        ShipCreator shipCreator = new ShipCreator();
        SemiCreator semiCreator = new SemiCreator();
        VanCreator vanCreator = new VanCreator();
        VehicleSeed shipSeed = shipCreator.getSeed();
        VehicleSeed semiSeed = semiCreator.getSeed();
        VehicleSeed vanSeed = vanCreator.getSeed();

        this.fleetSeed = new FleetSeed(shipSeed, semiSeed, vanSeed);
        this.fleetSeed.saveSeed();
    }

    public void makeFleet(String fleetSeedFilename) {
        this.fleetSeed = new FleetSeed(fleetSeedFilename);
    }

    public void makeCompanies() {
        this.makeCompanies(10);
    }

    public void makeCompanies(List<String> companySeedFilenames) {
        for (String companySeedFilename : companySeedFilenames) {
            this.makeCompany(companySeedFilename);
        }
    }

    public void makeCompanies(int numberOfCompanies) {
        companies = new ArrayList<>();
        CompanyCreator companyCreator = new CompanyCreator(this.network.seed);
        String standardFilename = ClassSeed.getStandardFilename();
        for (int i = 0; i < numberOfCompanies; i++) {
            CompanySeed companySeed = companyCreator.getSeed();
            companySeed.saveSeed(standardFilename + "_" + i);
            companies.add(new Company(this.network, companySeed, fleetSeed));
        }
    }


    public void makeCompany(String companySeedFilename) {
        CompanySeed companySeed = new CompanySeed(companySeedFilename);
        this.companies = new ArrayList<>();
        this.companies.add(new Company(this.network, companySeed, fleetSeed));
    }

}

