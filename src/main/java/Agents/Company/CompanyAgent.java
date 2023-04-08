package Agents.Company;


import Network.Location.House;
import Company.Company;
import Company.Request;
import Company.FulfilledRequest;
import Company.Dispatch;
import Network.Location.Location;
import Network.Route.Route;
import Product.Product;
import Producer.Producer;
import Vehicle.States.EnRoute;
import Vehicle.Vehicle;
import World.World;
import jade.core.AID;
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

import java.util.*;

public class CompanyAgent extends Agent {

    private static Random random = new Random(1);
    private Company company;
    private Queue<FulfilledRequest> queuedRequests = new LinkedList<>();
    private Queue<CompanyRequest> holdingRequests = new LinkedList<>();


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

    public List<Double> getRoutePrices(List<Location> locations) {
        List<Double> prices = new ArrayList<>();
        for (int i = 0; i < locations.size() - 1; i++) {
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
            System.out.println(myAgent.getLocalName() + " got a route request from " + cfp.getSender().getLocalName() + "!");
            return new RouteRequestContractNetResponder(myAgent, cfp);
        }
    }

    class RouteRequestContractNetResponder extends SSContractNetResponder {
        Request request;

        public RouteRequestContractNetResponder(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            String content = cfp.getContent();
            request = new Request(company.network, content);
            List<Double> prices = getRoutePrices(request.getRoute());
            reply.setContent(String.join(",", prices.stream().map(Object::toString).toArray(String[]::new)));
            reply.addUserDefinedParameter("log", prices.toString() + " to " + cfp.getSender().getLocalName() + " (from " + myAgent.getLocalName() + ")");
            // ...
            System.out.println(myAgent.getLocalName() + " sending reply to route request from " + cfp.getSender().getName() + "!");
            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject from " + reject.getSender().getLocalName() + "!");
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            System.out.println(myAgent.getLocalName() + " got an accept from" + accept.getSender().getLocalName());
            ACLMessage reply = accept.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            //split and parse the content
            String content = accept.getContent();
            FulfilledRequest fulfilledRequest = new FulfilledRequest(company.network, content);

            handleFulfilledRequest(fulfilledRequest);
            return reply;
        }
    }

    private void handleFulfilledRequest(FulfilledRequest fulfilledRequest) {
        if (firstDispatcher(fulfilledRequest)) {
            startDispatch(fulfilledRequest);
        } else {

        }
    }

    private boolean firstDispatcher(FulfilledRequest fulfilledRequest) {
        Dispatch dispatch = fulfilledRequest.getDispatch(0);
        String dispatcherName = dispatch.getCompanyName();
        if (dispatcherName.equals(getLocalName())) {
            return true;
        } else {
            return false;
        }
    }

    private void startDispatch(FulfilledRequest fulfilledRequest) {
        Dispatch dispatch = fulfilledRequest.getDispatch(0);
        Product product = Producer.getProduct(fulfilledRequest.getProductName());
        int quantity = fulfilledRequest.getQuantity();
        int cargoSpace = product.getVolume() * quantity;
        Vehicle vehicle = findVehicle(dispatch, cargoSpace);
        if (vehicle != null) {
            prepareDispatch(fulfilledRequest, cargoSpace, vehicle);
            if (isSendDispatch(fulfilledRequest, vehicle)) {
                sendDispatch(fulfilledRequest, dispatch, vehicle);
            } else {
                holdRequest(fulfilledRequest, dispatch, vehicle);
            }
        } else {
            queueRequest(fulfilledRequest);
        }
    }


    private Vehicle findVehicle(Dispatch dispatch, int cargoSpace) {
        Location start = dispatch.getStart();
        Location end = dispatch.getEnd();

        return company.findVehicle(start, end, cargoSpace);
    }

    private void prepareDispatch(FulfilledRequest fulfilledRequest, int cargoSpace, Vehicle vehicle) {
        Dispatch dispatch = fulfilledRequest.getDispatch(0);
        vehicle.setLocation(null);
        boolean loaded = loadVehicle(dispatch, vehicle, cargoSpace);
        if (!loaded) {
            throw new RuntimeException("Could not fill up cargo");
        } else {
            dispatch.setHoldingTime(dispatch.getLoadedOn() - fulfilledRequest.getStartedOn());
        }
    }

    private boolean loadVehicle(Dispatch dispatch, Vehicle vehicle, int cargoSpace) {
        boolean couldLoad = vehicle.fillUpCargo(cargoSpace);
        if (!couldLoad) {
            return false;
        }
        dispatch.setLoadedOn(World.getTime());
        dispatch.setVehicle(vehicle);
        return true;
    }

    private boolean isSendDispatch(FulfilledRequest fulfilledRequest, Vehicle vehicle) {
        return true;
    }

    private void sendDispatch(FulfilledRequest fulfilledRequest, Dispatch dispatch, Vehicle vehicle) {
        Route route = company.network.getRoute(dispatch.getStart(), dispatch.getEnd());
        int distance = route.getDistance();
        long time = (long) (distance / vehicle.getSpeed()) + 1;
        sendDispatchVehicle(dispatch, vehicle, route, time);

        fulfilledRequest.increaseRequestStage();

        //sendDispatchInformParticipants
        String content = fulfilledRequest.toString();
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        List<Dispatch> dispatches = fulfilledRequest.getDispatches();
        for (int i = fulfilledRequest.getRequestStage(); i < dispatches.size(); i++) {
            Dispatch d = dispatches.get(i);
            message.addReceiver(new AID(d.getCompanyName(), AID.ISLOCALNAME));
            System.out.println(getLocalName() + " sending dispatch to " + dispatch.getCompanyName() + "!");
        }
        message.setProtocol("dispatch-progress");
        message.setContent(content);
        send(message);
    }

    private void sendDispatchVehicle(Dispatch dispatch, Vehicle vehicle, Route route, long time) {
        dispatch.setVehicleFilledUpCargo(vehicle.getFilledUpCargo());
        dispatch.setTravelTime(time);
        dispatch.setIdleTime(dispatch.getDispatchedOn() - dispatch.getLoadedOn());
        vehicle.setLocation(null);
        vehicle.setState(new EnRoute(route, dispatch.getStart(), dispatch.getEnd()));
        dispatch.setDispatchedOn(World.getTime());
        //TODO
        //Get the route and the distance
        // How to make the vehicle wait x amount of time
        //maybe make the function return the time instead?
        company.dispatchVehicle(vehicle);
    }

    private void holdRequest(FulfilledRequest fulfilledRequest, Dispatch dispatch, Vehicle vehicle) {
        holdingRequests.add(new CompanyRequest(fulfilledRequest, dispatch, vehicle));
    }

    private void queueRequest(FulfilledRequest fulfilledRequest) {
        queuedRequests.add(fulfilledRequest);
    }


}

class CompanyRequest {
    private FulfilledRequest fulfilledRequest;
    private Dispatch dispatch;
    private Vehicle vehicle;

    public CompanyRequest(FulfilledRequest fulfilledRequest, Dispatch dispatch, Vehicle vehicle) {
        this.fulfilledRequest = fulfilledRequest;
        this.dispatch = dispatch;
        this.vehicle = vehicle;
    }
}
