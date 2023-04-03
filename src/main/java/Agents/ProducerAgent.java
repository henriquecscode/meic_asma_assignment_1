package Agents;


import Company.Company;
import Network.Location.House;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

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

        addBehaviour(new RouteRequestContractNetInit(this, new ACLMessage(ACLMessage.CFP)));

    }

    class RouteRequestContractNetInit extends ContractNetInitiator {
        public RouteRequestContractNetInit(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector prepareCfps(ACLMessage cfp) {

            cfp.setContent("Hello");
            cfp.setProtocol("route-request");
            for(AID company : companies) {
                cfp.addReceiver(company);
            }
            Vector v = new Vector();
            v.add(cfp);
            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");

            for(int i=0; i<responses.size(); i++) {
                ACLMessage response = (ACLMessage) responses.get(i);
                ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                System.out.println("Producer got an answer from " + response.getSender().getName() + " with content " + response.getContent());
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                acceptances.add(msg);
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
