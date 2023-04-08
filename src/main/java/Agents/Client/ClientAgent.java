package Agents.Client;

import Client.Client;
import Company.Request;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.util.leap.Iterator;

import java.util.*;

public class ClientAgent extends Agent {
    private static Random random = new Random(1);
    private Client client;
    private String requestedProduct;
    private List<DFAgentDescription> requestedProductproducers;

    public ClientAgent(Client client) {
        super();
        this.client = client;
    }

    protected void setup() {
        prepareRequest();
        addBehaviour(new ProductRequestContractNetInit(this, new ACLMessage(ACLMessage.CFP)));
    }

    private List<DFAgentDescription> prepareRequest() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("producer");
        template.addServices(sd);
        DFAgentDescription[] result = null;
        HashMap<String, List<DFAgentDescription>> productProducers = new HashMap<>();
        try {
            result = DFService.search(this, template);

        } catch (FIPAException fe) {
            fe.printStackTrace();
            return null;
        }
        for (int i = 0; i < result.length; ++i) {
            for (Iterator it = result[i].getAllServices(); it.hasNext(); ) {
                ServiceDescription serviceDescription = (ServiceDescription) it.next();
                String name = serviceDescription.getName();
                if (!productProducers.containsKey(name)) {
                    productProducers.put(name, new ArrayList<>());
                }
                productProducers.get(name).add(result[i]);
            }
        }

        return makeRequest(productProducers);
    }

    private List<DFAgentDescription> makeRequest(HashMap<String, List<DFAgentDescription>> productProducers) {
        System.out.println("Client-agent " + getAID().getName() + " is making a request.");
        List<String> productKeys = new ArrayList<>(productProducers.keySet());
        Random r = new Random(1);
        String randomKey = productKeys.get(r.nextInt(productKeys.size()));
        List<DFAgentDescription> producers = productProducers.get(randomKey);
        System.out.println("Client-agent " + getAID().getName() + " is requesting " + randomKey + " from " + producers.size() + " producers.");
        for (DFAgentDescription producer : producers) {
            System.out.println("Producer: " + producer.getName());
        }
        this.requestedProduct = randomKey;
        this.requestedProductproducers = producers;
        return producers;


    }

    class ProductRequestContractNetInit extends ContractNetInitiator {
        public ProductRequestContractNetInit(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();
            for (DFAgentDescription producer : requestedProductproducers) {
                cfp.addReceiver(producer.getName());
            }
            Request request = new Request(null, client.getHouse(), requestedProduct, 1);
            cfp.setContent(request.toString());
            cfp.setProtocol("product-request");


            v.add(cfp);
            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {
            int bestIndex = 0;
            double bestPrice = Double.MAX_VALUE;

            // choose only the best (lowest) price
            for (int i = 0; i < responses.size(); i++) {
                ACLMessage response = (ACLMessage) responses.get(i);

                if (response.getPerformative() == ACLMessage.PROPOSE) {
                    double responsePrice = Double.parseDouble(response.getContent());
                    if (responsePrice < bestPrice) {
                        bestPrice = responsePrice;
                        bestIndex = i;
                    }
                }
            }

            // accept proposal of responses[i] and reject all others
            for (int i = 0; i < responses.size(); i++) {
                ACLMessage response = (ACLMessage) responses.get(i);
                ACLMessage msg = response.createReply();
                if (i == bestIndex) {
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.add(msg);
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
