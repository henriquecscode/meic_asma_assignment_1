package Agents;


import Agents.Company.CompanyAgent;
import Company.Company;
import Network.Location.House;
import Network.Location.Location;
import Network.Route.Itinerary;
import Producer.Producer;
import Producer.Production;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.SSContractNetResponder;
import jade.proto.SSResponderDispatcher;

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

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol("product-request"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        addBehaviour(new ProductRequestResponderDispatcher(this, template));
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

    public double getProductPrice(Production production){
        return production.getPriceMult();
    }

    public void testProducerRequest() {
        Random random = new Random(1);
        List<House> houses = producer.network.getHouses();
        House house = houses.get(random.nextInt(houses.size()));

        if (companies.isEmpty()) {
            getCompanies();
        }
        List<Location> path = Arrays.asList(producer.getLocation(), house);
        Itinerary itinerary = new Itinerary(path);

        addBehaviour(new RouteRequestContractNetInit(this, new ACLMessage(ACLMessage.CFP), itinerary));

    }

    class ProductRequestResponderDispatcher extends SSResponderDispatcher {
        public ProductRequestResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected Behaviour createResponder(ACLMessage cfp) {
            return new ProductRequestContractNetResponder(myAgent, cfp);
        }
    }

    class ProductRequestContractNetResponder extends SSContractNetResponder {

        public ProductRequestContractNetResponder(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {

            //get production with name from the cfp.getContent()
            ACLMessage reply = cfp.createReply();
            // only set to Propose if product exists

            String productName = cfp.getContent();
            producer.getProductions();
            Optional<Production> optionalProduction = producer.getProductions().stream().filter(p -> p.getProduct().getName().equals(productName)).findFirst();
            Production production;

            if(!optionalProduction.isPresent()){
                // We don't have the production. Reject the proposal
                reply.setPerformative(ACLMessage.REFUSE);
            }else{
                reply.setPerformative(ACLMessage.PROPOSE);
                production = optionalProduction.get();
                double price = getProductPrice(production);
                reply.setContent(Double.toString(price));
            }

            return reply;
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            ACLMessage reply = accept.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            return reply;
        }

        protected void	handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            // TODO handle reject proposal of product
        }

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
            Set<Integer> companiesWithAcceptedProposal = new HashSet<>();
            for (int i = 0; i < prices.size(); i++) {
                if (prices.get(i) == Double.MAX_VALUE) {
                    throw new RuntimeException("No company could offer a price for the route");
                }
                int companyIndex = companiesResponseIndex.get(i);
                ACLMessage response = (ACLMessage) responses.get(companyIndex);
                ACLMessage msg = response.createReply();
                System.out.println(myAgent.getName() + " got an answer from " + response.getSender().getName() + " with content " + response.getContent());
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                msg.setContent(Integer.toString(i));
                acceptances.add(msg);
                companiesWithAcceptedProposal.add(companyIndex);
            }
            for (int i = 0; i < responses.size(); i++) {
                if (!companiesWithAcceptedProposal.contains(i)) {
                    ACLMessage response = (ACLMessage) responses.get(i);
                    ACLMessage msg = response.createReply();
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(msg);
                }
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
