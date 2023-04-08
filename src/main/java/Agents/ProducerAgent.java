package Agents;


import Company.Request;
import Network.Location.House;
import Network.Location.Location;
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
        if (!companies.isEmpty()) {
            return;
        }
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

    public double getProductPrice(Production production) {
        return production.getPriceMult();
    }

    public void testProducerRequest() {
        Random random = new Random(1);
        List<House> houses = producer.network.getHouses();
        House house = houses.get(random.nextInt(houses.size()));

        getCompanies();

        List<Location> path = Arrays.asList(producer.getLocation(), house);
        Request request = new Request(producer.getLocation(), house, "testProduct", 1);

//        addBehaviour(new RouteRequestContractNetInit(this, new ACLMessage(ACLMessage.CFP), request));

    }

    class ProductRequestResponderDispatcher extends SSResponderDispatcher {
        public ProductRequestResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected Behaviour createResponder(ACLMessage cfp) {
            System.out.println(myAgent.getLocalName() + ": Received product request from " + cfp.getSender().getLocalName());
            return new ProductRequestContractNetResponder(myAgent, cfp);
        }
    }

    class ProductRequestContractNetResponder extends SSContractNetResponder {

        List<ACLMessage> acceptedProposals = new ArrayList<>();
        Request request;
        Production production;

        public ProductRequestContractNetResponder(Agent a, ACLMessage cfp) {
            super(a, cfp);
            request = getRequest(cfp);
            production = getProduction();
            if (production != null) {
                registerHandleCfp(new RouteRequestContractNetInit(myAgent, new ACLMessage(ACLMessage.CFP), CFP_KEY, REPLY_KEY));
                registerHandleAcceptProposal(new RouteRequestConfirmationContractNetInit(myAgent, true));
                registerHandleRejectProposal(new RouteRequestConfirmationContractNetInit(myAgent, false));
            }

        }

        private Request getRequest(ACLMessage cfp) {
            String content = cfp.getContent();
            Request request = new Request(producer.network, content);
            Request producerRequest = new Request(producer.getLocation(), request.getEnd(), request.getProductName(), request.getQuantity());
            return producerRequest;
        }

        private Production getProduction() {
            String productName = request.getProductName();
            producer.getProductions();
            Optional<Production> optionalProduction = producer.getProductions().stream().filter(p -> p.getProduct().getName().equals(productName)).findFirst();
            Production production;

            if (!optionalProduction.isPresent()) {
                // We don't have the production. Reject the proposal
                return null;
            }
            production = optionalProduction.get();
            return production;

        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            // production was necessarily null
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            return reply;
        }

        protected void cleanReply() {
            getDataStore().remove(REPLY_KEY);
        }

        class RouteRequestContractNetInit extends ContractNetInitiator {
            private String Carried_CFP_KEY, Carried_REPLY_KEY;

            public RouteRequestContractNetInit(Agent a, ACLMessage cfp, String CFP_KEY, String REPLY_CFP) {
                super(a, cfp);
                this.Carried_CFP_KEY = CFP_KEY;
                this.Carried_REPLY_KEY = REPLY_CFP;
            }

            protected Vector prepareCfps(ACLMessage cfp) {

                cfp.setContent(request.toString());
                cfp.setProtocol("route-request");
                getCompanies();
                for (AID company : companies) {
                    cfp.addReceiver(company);
                }
                Vector v = new Vector();
                v.add(cfp);
                return v;
            }

            protected void handleAllResponses(Vector responses, Vector acceptances) {

                System.out.println("got " + responses.size() + " responses!");
                int numberRoutes = request.getRoute().size() -1;
                List<Double> prices = Arrays.asList(new Double[numberRoutes]);
                Collections.fill(prices, Double.MAX_VALUE);
                List<Integer> companiesResponseIndex = Arrays.asList(new Integer[numberRoutes]);
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

                //Add companies with possibly accepted proposals to a set with their respective task
                Set<Integer> companiesWithAcceptedProposal = new HashSet<>();
                HashMap<ACLMessage, List<Integer>> proposalsByCompany = new HashMap<>();
                for (int i = 0; i < prices.size(); i++) {
                    if (prices.get(i) == Double.MAX_VALUE) {
                        throw new RuntimeException("No company could offer a price for the route");
                    }
                    int companyIndex = companiesResponseIndex.get(i);
                    ACLMessage companyMessage = (ACLMessage) responses.get(companyIndex);
                    if (!proposalsByCompany.containsKey(companyMessage)) {
                        proposalsByCompany.put(companyMessage, new ArrayList<>());
                    }
                    proposalsByCompany.get(companyMessage).add(i);
                    companiesWithAcceptedProposal.add(companyIndex);
                }

                //Create the accept proposal messages
                //Content is the parts of the path they will be handling
                for (ACLMessage companyMessage : proposalsByCompany.keySet()) {
                    ACLMessage msg = companyMessage.createReply();
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    msg.setContent(proposalsByCompany.get(companyMessage).stream().map(Object::toString).collect(Collectors.joining(",")));
                    acceptedProposals.add(msg);
                }

                // Reject proposals
                for (int i = 0; i < responses.size(); i++) {
                    if (!companiesWithAcceptedProposal.contains(i)) {
                        ACLMessage response = (ACLMessage) responses.get(i);
                        ACLMessage msg = response.createReply();
                        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        System.out.println(myAgent.getLocalName() + " rejected proposal from " + response.getSender().getLocalName() + " for conversation id" + response.getConversationId());
                        acceptances.add(msg);
                    }
                }

                //Transmit the final price to the client
                ACLMessage originalCFP = (ACLMessage) getDataStore().get(Carried_CFP_KEY);
                ACLMessage replyCFP = originalCFP.createReply();
                replyCFP.setPerformative(ACLMessage.PROPOSE);
                double finalPrice = calculatePrice(prices);
                replyCFP.setContent(Double.toString(finalPrice));
                getDataStore().put(Carried_REPLY_KEY, replyCFP);
            }

            protected void handleAllResultNotifications(Vector resultNotifications) {
                System.out.println("got " + resultNotifications.size() + " result notifs!");
            }

            private double calculatePrice(List<Double> prices) {
                double totalRoutePrice = 0;
                double productionMult = production.getPriceMult();
                for (Double price : prices) {
                    totalRoutePrice += price;
                }
                double finalPrice = totalRoutePrice * productionMult;
                return finalPrice;
            }
        }

        class RouteRequestConfirmationContractNetInit extends Behaviour {
            private boolean accept;
            private boolean isDone = false;

            public RouteRequestConfirmationContractNetInit(Agent a, boolean accept) {
                super(a);
                this.accept = accept;
            }

            @Override
            public void action() {
                cleanReply();
                for (ACLMessage acceptedProposal : acceptedProposals) {
                    if (accept) {
                        acceptedProposal.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    } else {
                        acceptedProposal.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    }
                    myAgent.send(acceptedProposal);
                    isDone = true;
                }
            }

            @Override
            public boolean done() {
                return isDone;
            }
        }
    }

}
