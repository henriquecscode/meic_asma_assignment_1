package Agents.Company;


import Company.Company;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class CompanyAgent extends Agent {
    private Company company;

    public CompanyAgent(Company company) {
        super();
        this.company = company;

        // randomize two locations (ad hoc)
        // determine the best path (deterministic and common with step 2)
        // figure out which paths can offer
        // give a price for those paths
    }


    protected void setup() {
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

    public void addResponderBehaviour(){
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol("route-request"),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        addBehaviour(
                new RouteRequestContractNetResponder(
                        this, MessageTemplate.MatchPerformative(ACLMessage.CFP))
        );
    }

    class RouteRequestContractNetResponder extends ContractNetResponder {
        public RouteRequestContractNetResponder(Agent a, MessageTemplate template) {
            super(a, template);
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent("I will do it for free!!!" + " (from " + myAgent.getLocalName() +")" );
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

            return result;
        }
    }
}
