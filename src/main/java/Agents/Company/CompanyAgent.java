package Agents.Company;


import Company.Company;
import Network.Location.House;
import Company.Request;
import Company.RequestPrice;
import jade.core.Agent;

import java.util.List;

public class CompanyAgent extends Agent {
    private final Company company;

    public CompanyAgent(Company company) {
        super();
        this.company = company;
    }

    // TODO: this is just temporary while the agent does not receive any real requests
    protected Request getRandomRequest() {
        List<House> houses = company.getNetwork().getHouses();
        House start = houses.get((int) (Math.random() * houses.size()));
        houses.remove(start);
        House end = houses.get((int) (Math.random() * houses.size()));
        houses.add(start);

        return new Request(start, end, "test", 1);
    }

    protected void setup() {
        Request request = getRandomRequest();
        RequestPrice requestPrice = new RequestPrice(request);
        List<Integer> prices = requestPrice.getBestPathPrices(company);

        // TODO: send the prices to the client after the client sends the request
    }
}
