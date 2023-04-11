package Agents.Client;

import Agents.Protocols;
import Client.Client;
import Company.FulfilledRequest;
import Company.Request;
import App.App;
import World.AgentWorld;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.util.leap.Iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ClientAgent extends Agent {
    public static Random random = new Random(1);
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
//        System.out.println("Client-agent " + getAID().getName() + " is making a request.");
        List<String> productKeys = new ArrayList<>(productProducers.keySet());
        String randomKey = productKeys.get(random.nextInt(productKeys.size()));
        List<DFAgentDescription> producers = productProducers.get(randomKey);
        System.out.println("Client-agent " + getAID().getName() + " is requesting " + randomKey + " from " + producers.size() + " producers.");
        for (DFAgentDescription producer : producers) {
//            System.out.println("Producer: " + producer.getName());
        }
        this.requestedProduct = randomKey;
        this.requestedProductproducers = producers;
        return producers;


    }

    public void concludeRequest(FulfilledRequest request) {
        System.out.println("SUCCESS Client-agent " + getAID().getLocalName() + " received a product: " + request.getProductName());
        logRequest(request);
        AgentWorld.agents.remove(this);
        doDelete();
    }

    public void logRequest(FulfilledRequest request) {
//        System.out.println(request.toString());
        String filename = getLocalName() + ".txt";
        String filepath = App.executionLogFolder + "/" + filename;
        PrintWriter writer;
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print(request);
        writer.close();
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
            Request request = new Request(null, client.getHouse(), requestedProduct, getLocalName(), 1);
            cfp.setContent(request.toString());
            cfp.setProtocol(Protocols.PRODUCT_REQUEST.name());


            v.add(cfp);
            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {
            int bestIndex = -1;
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
                if (response.getPerformative() != ACLMessage.PROPOSE) {
                    continue;
                }

                ACLMessage msg = response.createReply();
                if (i == bestIndex) {
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.add(msg);
            }

            if (bestIndex == -1) {
                System.out.println("Client-agent " + getAID().getLocalName() + " did not get a suitable response from any producer.");
                return;
            }

            myAgent.addBehaviour(new ProductArrivalListenerBehaviour());
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
//            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }

    class ProductArrivalListenerBehaviour extends CyclicBehaviour {

        public final MessageTemplate mt = MessageTemplate.MatchProtocol(Protocols.REQUEST_FINISHED.name());


        @Override
        public void action() {
            ACLMessage msg = receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                FulfilledRequest request = new FulfilledRequest(client.network, content);
                concludeRequest(request);
            } else {
                block();
            }
        }
    }
}
