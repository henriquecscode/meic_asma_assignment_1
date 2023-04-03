package Agents;


import Company.Company;
import Network.Location.House;
import Network.Location.Location;
import Network.Route.Itinerary;
import Producer.Producer;
import Producer.Production;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;
import java.util.stream.Collectors;

public class ProducerAgent extends Agent {
    private Producer producer;
    private static int producerAgentId = 0;
    private List<AID> companies = new ArrayList<>();

    public ProducerAgent(Producer producer) {
        super();
        this.producer = producer;
        producerAgentId++;
    }

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd;
        for (Production production : producer.getProductions()) {
            sd = new ServiceDescription();
            sd.setType("producer");
            sd.setName(production.getProduct().getName());
            dfd.addServices(sd);
        }
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public void getCompanies() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("company");
        template.addServices(sd);
        DFAgentDescription[] result = null;
        try {
            result = DFService.search(this, template);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        for (DFAgentDescription description : result) {
            companies.add(description.getName());
        }
    }

    public void testProducerRequest() {
        Random random = new Random();
        List<House> houses = producer.network.getHouses();
        House house = houses.get(random.nextInt(houses.size()));

        if (companies.isEmpty()) {
            getCompanies();
        }
        List<Location> path = Arrays.asList(producer.getLocation(), house);
        Itinerary itinerary = new Itinerary(path);

        addBehaviour(new RouteRequestContractNetInit(this, new ACLMessage(ACLMessage.CFP), itinerary));

    }

    class RouteRequestContractNetInit extends ContractNetInitiator {
        private Itinerary itinerary;

        public RouteRequestContractNetInit(Agent a, ACLMessage cfp, Itinerary itinerary) {
            super(a, cfp);
            this.itinerary = itinerary;
        }

        protected Vector prepareCfps(ACLMessage cfp) {

            cfp.setContent(String.valueOf(itinerary));
            cfp.setProtocol("route-request");
            for (AID company : companies) {
                cfp.addReceiver(company);
            }
            Vector v = new Vector();
            v.add(cfp);
            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");
            List<Double> prices = Arrays.asList(new Double[itinerary.getSize()]);
            Collections.fill(prices, Double.MAX_VALUE);
            List<Integer> companiesResponseIndex = Arrays.asList(new Integer[itinerary.getSize()]);
            List<Double> offeredPrices;
            for (int i = 0; i < responses.size(); i++) {
                ACLMessage response = (ACLMessage) responses.get(i);
                if (response.getPerformative() == ACLMessage.PROPOSE) {
                    offeredPrices = Arrays.asList(response.getContent().split(",")).stream().map(Double::parseDouble).collect(Collectors.toList());
                    for (int j = 0; j < offeredPrices.size(); j++) {
                        if (offeredPrices.get(j) < prices.get(j)) {
                            prices.set(j, offeredPrices.get(j));
                            companiesResponseIndex.set(j, i);
                        }
                    }
                    System.out.println(response.getUserDefinedParameter("log"));

                }

            }
            for (int i = 0; i < prices.size(); i++) {
                if (prices.get(i) == Double.MAX_VALUE) {
                    throw new RuntimeException("No company could offer a price for the route");
                }
                ACLMessage response = (ACLMessage) responses.get(companiesResponseIndex.get(i));
                ACLMessage msg = response.createReply();
                System.out.println(myAgent.getName() + " got an answer from " + response.getSender().getName() + " with content " + response.getContent());
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                msg.setContent(Integer.toString(i));
                acceptances.add(msg);
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
