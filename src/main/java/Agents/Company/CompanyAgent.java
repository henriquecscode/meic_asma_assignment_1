package Agents.Company;


import Company.Company;
import Network.Location.House;
import Company.Request;
import Company.RequestPrice;
import Network.Location.Location;
import Network.Route.Itinerary;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSContractNetResponder;
import jade.proto.SSResponderDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.List;

public class CompanyAgent extends Agent {

    private static Random random = new Random(1);
    private Company company;

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
        // Request request = getRandomRequest();
        // RequestPrice requestPrice = new RequestPrice(request);
        // List<Integer> prices = requestPrice.getBestPathPrices(company);
        // TODO: send the prices to the client after the client sends the request
        
        registerAgent();
        addResponderBehaviour();
    }

    private void registerAgent() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("company");
        sd.setName(getName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public List<Double> getRoutePrices(Itinerary itinerary){
        List<Location> locations = itinerary.getLocations();
        List<Double> prices = new ArrayList<>();
        for(int i = 0; i < locations.size()-1; i++){
            double price = random.nextInt(100);
            prices.add(price);
        }
        return prices;
    }

    public void addResponderBehaviour() {
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol("route-request"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        addBehaviour(new RouteRequestResponderDispatcher(this, template));
    }

    class RouteRequestResponderDispatcher extends SSResponderDispatcher {
        public RouteRequestResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected Behaviour createResponder(ACLMessage cfp) {
            return new RouteRequestContractNetResponder(myAgent, cfp);
        }
    }

    class RouteRequestContractNetResponder extends SSContractNetResponder {
        Itinerary itinerary;
        public RouteRequestContractNetResponder(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            String content = cfp.getContent();
            itinerary = new Itinerary(company.getNetwork(), content);
            List<Double> prices = getRoutePrices(itinerary);
            reply.setContent(String.join(",", prices.stream().map(Object::toString).toArray(String[]::new)));
            reply.addUserDefinedParameter("log",prices.toString() +" to " + cfp.getSender().getName() + " (from " + myAgent.getLocalName() + ")" );
            // ...
            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject...");
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            System.out.println(myAgent.getLocalName() + " got an accept!");
            ACLMessage result = accept.createReply();
            result.setPerformative(ACLMessage.INFORM);
            result.setContent("this is the result");

            Integer itineraryRouteIndex = Integer.valueOf(accept.getContent());
            Location location1 = itinerary.getLocations().get(itineraryRouteIndex);
            Location location2 = itinerary.getLocations().get(itineraryRouteIndex+1);
            //need to do some handling to coordinate with the other companies
            return result;
        }
    }
}
