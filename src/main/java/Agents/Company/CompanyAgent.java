package Agents.Company;


import Agents.Protocols;
import Network.Location.House;
import Company.Company;
import Company.Request;
import Company.RequestPrice;
import Company.FulfilledRequest;
import Company.Dispatch;
import Company.RequestDispatch;
import Network.Location.Location;
import Product.Product;
import Producer.Producer;
import Vehicle.States.EnRoute;
import Vehicle.States.Holding;
import Vehicle.States.Idle;
import Vehicle.Vehicle;
import World.World;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
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

    protected static Random random = new Random(1);
    protected Company company;
    protected Queue<FulfilledRequest> queuedRequests = new LinkedList<>();
    protected Map<Vehicle, List<CompanyRequest>> holdingRequests = new HashMap<>();
    protected Map<Vehicle, List<CompanyRequest>> enRouteRequests = new HashMap<>();


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

        return new Request(start, end, "test", "noclient", 1, 1);
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

    public List<Double> getRoutePrices(Request request) {
        RequestPrice requestPrice = new RequestPrice(request);
        List<Double> prices = requestPrice.getBestPathPrices(company);
        return prices;
    }

    public void addResponderBehaviour() {
        MessageTemplate routeRequestTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(Protocols.ROUTE_REQUEST.name()),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        MessageTemplate requestDispatchedTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(Protocols.REQUEST_DISPATCHED.name()),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        MessageTemplate requestArrivalTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(Protocols.REQUEST_ARRIVAL.name()),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );

        addBehaviour(new RouteRequestResponderDispatcher(this, routeRequestTemplate));
        addBehaviour(new RequestDispatchResponderDispatcher(this, requestDispatchedTemplate));
        addBehaviour(new RequestArrivalResponderDispatcher(this, requestArrivalTemplate));
    }

    class RouteRequestResponderDispatcher extends SSResponderDispatcher {

        public RouteRequestResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected Behaviour createResponder(ACLMessage cfp) {
//            System.out.println(myAgent.getLocalName() + " got a route request from " + cfp.getSender().getLocalName() + "!");
            return new RouteRequestContractNetResponder(myAgent, cfp);
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
                List<Double> prices = getRoutePrices(request);
                reply.setContent(String.join(",", prices.stream().map(Object::toString).toArray(String[]::new)));
                reply.addUserDefinedParameter("log", prices.toString() + " to " + cfp.getSender().getLocalName() + " (from " + myAgent.getLocalName() + ")");
                // ...
//                System.out.println(myAgent.getLocalName() + " sending reply to route request from " + cfp.getSender().getName() + "!");
                return reply;
            }

            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
//                System.out.println(myAgent.getLocalName() + " got a reject from " + reject.getSender().getLocalName() + "!");
            }

            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
//                System.out.println(myAgent.getLocalName() + " got an accept from" + accept.getSender().getLocalName());
                ACLMessage reply = accept.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                //split and parse the content
                String content = accept.getContent();
                FulfilledRequest fulfilledRequest = new FulfilledRequest(company.network, content);

                handleFulfilledRequest(fulfilledRequest);
                return reply;
            }
        }

    }

    class RequestDispatchResponderDispatcher extends SSResponderDispatcher {
        private final MessageTemplate mt;

        public RequestDispatchResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
            this.mt = mt;
        }

        protected Behaviour createResponder(ACLMessage msg) {
//            System.out.println(myAgent.getLocalName() + " got a request dispatch from " + msg.getSender().getLocalName() + "!");
            return new RequestDispatchHandler(msg);
        }

        class RequestDispatchHandler extends Behaviour {
            private ACLMessage msg;
            Boolean isDone = false;

            public RequestDispatchHandler(ACLMessage msg) {
                this.msg = msg;
            }

            @Override
            public void action() {
                String content = msg.getContent();
                FulfilledRequest request = new FulfilledRequest(company.network, content);
                prepareRequest(request);
                isDone = true;
            }

            @Override
            public boolean done() {
                return isDone;
            }
        }
    }

    class RequestArrivalResponderDispatcher extends SSResponderDispatcher {
        private final MessageTemplate mt;

        public RequestArrivalResponderDispatcher(Agent a, MessageTemplate mt) {
            super(a, mt);
            this.mt = mt;
        }

        protected Behaviour createResponder(ACLMessage msg) {
//            System.out.println(myAgent.getLocalName() + " got a request dispatch from " + msg.getSender().getLocalName() + "!");
            return new RequestArrivalHandler(msg);
        }

        class RequestArrivalHandler extends Behaviour {
            private ACLMessage msg;
            Boolean isDone = false;

            public RequestArrivalHandler(ACLMessage msg) {
                this.msg = msg;
            }

            @Override
            public void action() {
                String content = msg.getContent();
                FulfilledRequest request = new FulfilledRequest(company.network, content);
                continueRequest(request);
                isDone = true;

            }

            @Override
            public boolean done() {
                return isDone;
            }
        }
    }


    protected void handleFulfilledRequest(FulfilledRequest fulfilledRequest) {
        if (firstDispatcher(fulfilledRequest)) {
            startRequest(fulfilledRequest);
        } else {
//            System.out.println(getLocalName() + "Not first dispatcher of " + fulfilledRequest.toString() + "!");
        }
    }

    protected boolean firstDispatcher(FulfilledRequest fulfilledRequest) {
        RequestDispatch requestDispatch = fulfilledRequest.getDispatch(0);
        String dispatcherName = requestDispatch.getCompanyName();
        if (dispatcherName.equals(getLocalName())) {
            return true;
        } else {
            return false;
        }
    }

    protected void startRequest(FulfilledRequest fulfilledRequest) {
        int requestStage = 0;
        RequestDispatch requestDispatch = fulfilledRequest.getDispatch(requestStage);
        executeRequest(fulfilledRequest, requestDispatch);
    }

    protected void continueRequest(FulfilledRequest fulfilledRequest) {
        if (fulfilledRequest == null) {
            return;
        }
        int requestStage = fulfilledRequest.getRequestStage();
        if (fulfilledRequest.isFinished()) {
            return;
        }
        RequestDispatch requestDispatch = fulfilledRequest.getDispatch(requestStage);
        if (!requestDispatch.getCompanyName().equals(getLocalName())) {
            return;
        }
        executeRequest(fulfilledRequest, requestDispatch);
    }

    protected void executeRequest(FulfilledRequest fulfilledRequest, RequestDispatch requestDispatch) {
        requestDispatch.setStartedOn(World.getTime());
        Product product = Producer.getProduct(fulfilledRequest.getProductName());
        int quantity = fulfilledRequest.getQuantity();
        int cargoSpace = product.getVolume() * quantity;
        List<Vehicle> vehicles = findVehicle(requestDispatch, cargoSpace);
        if (!vehicles.isEmpty()) {
            Vehicle vehicle = getBestVehicle(vehicles, requestDispatch, cargoSpace);
            if (vehicle == null) {
                transferRequest(requestDispatch, cargoSpace, fulfilledRequest);
                return;
            }
            prepareDispatch(fulfilledRequest, cargoSpace, vehicle);
            holdRequest(fulfilledRequest, requestDispatch, vehicle);
            processDispatch(vehicle);
        } else {
            Vehicle vehicle = getVehicleForRequest(fulfilledRequest, requestDispatch, cargoSpace);
            if (vehicle == null) {
                transferRequest(requestDispatch, cargoSpace, fulfilledRequest);
            } else {
                Dispatch transferDispatch = new Dispatch(requestDispatch.getEnd(), requestDispatch.getStart(), requestDispatch.getCompanyName());
                transferDispatch.setVehicle(vehicle);
                holdRequest(fulfilledRequest, transferDispatch, vehicle);
                processDispatch(vehicle);
            }
        }
    }

    protected void transferRequest(RequestDispatch requestDispatch, int cargoSpace, FulfilledRequest fulfilledRequest) {
        List<Vehicle> vehicles;
        Vehicle vehicle;
        vehicles = findEventualVehicle(requestDispatch, cargoSpace);
        if (vehicles.isEmpty()) {
//                    System.out.println(getLocalName() + " no vehicles found. Must be in transit");
            queueRequest(fulfilledRequest);
            return;
        }
//                System.out.println(getLocalName() + "Sending a vehicle to make a connectiong");
        vehicle = getBestVehicle(vehicles, requestDispatch, cargoSpace);
        if (vehicle.getLocation() == requestDispatch.getStart()) {
//            throw new RuntimeException("Vehicle is already at the start");
            queueRequest(fulfilledRequest);
            return;
        }
        if (vehicle.getLocation() == requestDispatch.getEnd()) {
//            throw new RuntimeException("Vehicle is already at the end");
            queueRequest(fulfilledRequest);
            return;
        }
        Dispatch transferDispatch;
        if (vehicle.getLocation() == vehicle.getHub().getLocation()) {
            //It must be a ship that is at the opposite hub
            transferDispatch = new Dispatch(vehicle.getLocation(), requestDispatch.getStart(), requestDispatch.getCompanyName());
        } else {
            transferDispatch = new Dispatch(vehicle.getLocation(), vehicle.getHub().getLocation(), requestDispatch.getCompanyName());
        }
        transferDispatch.setVehicle(vehicle);
        holdRequest(fulfilledRequest, transferDispatch, vehicle);
        processDispatch(vehicle);
    }

    protected void processDispatch(Vehicle vehicle) {
        if (isSendDispatch(vehicle)) {
            sendDispatch(vehicle);
        }
    }


    protected Vehicle getBestVehicle(List<Vehicle> vehicles, Dispatch dispatch, int cargoSpace) {
        List<Vehicle> readyVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (!vehicle.canFillUpCargo(cargoSpace)) {
                continue;
            }

            //if is in route
            if (vehicle.getState() instanceof EnRoute) {
                continue;
            }

            if (vehicle.getState() instanceof Holding) {
                Holding holding = (Holding) vehicle.getState();
                if (!holding.getDispatch().equals(dispatch)) {
                    continue;
                }
            }
            readyVehicles.add(vehicle);
        }

        List<Vehicle> holdingVehicles = new ArrayList<>();
        List<Vehicle> idlingAvailableVehicles = new ArrayList<>();
        for (Vehicle vehicle : readyVehicles) {
            if (holdingRequests.containsKey(vehicle)) {
                holdingVehicles.add(vehicle);
            } else {
                idlingAvailableVehicles.add(vehicle);
            }
        }
        if (!holdingVehicles.isEmpty()) {
            return holdingVehicles.get(0);
        } else if (!idlingAvailableVehicles.isEmpty()) {
            return idlingAvailableVehicles.get(0);
        } else {
            return null;
        }
    }


    protected List<Vehicle> findVehicle(RequestDispatch requestDispatch, int cargoSpace) {
        Location start = requestDispatch.getStart();
        Location end = requestDispatch.getEnd();

        return company.findVehicle(start, end, cargoSpace);
    }

    protected List<Vehicle> findEventualVehicle(RequestDispatch requestDispatch, int cargoSpace) {
        Location start = requestDispatch.getStart();
        Location end = requestDispatch.getEnd();

        return company.findEventualVehicle(start, end, cargoSpace);
    }

    protected void prepareDispatch(FulfilledRequest fulfilledRequest, int cargoSpace, Vehicle vehicle) {
        RequestDispatch requestDispatch = fulfilledRequest.getDispatch();
        boolean loaded = loadVehicle(requestDispatch, vehicle, cargoSpace);
        if (!loaded) {
            throw new RuntimeException("Could not fill up cargo");
        }
    }

    protected boolean loadVehicle(RequestDispatch requestDispatch, Vehicle vehicle, int cargoSpace) {
        boolean couldLoad = vehicle.fillUpCargo(cargoSpace);
        if (!couldLoad) {
            return false;
        }
        requestDispatch.setLoadedOn(World.getTime());
        requestDispatch.setVehicle(vehicle);
        return true;
    }


    protected boolean isSendDispatch(FulfilledRequest fulfilledRequest, Vehicle vehicle) {
        return true;
    }

    protected boolean isSendDispatch(Vehicle vehicle) {
        if (!(vehicle.getState() instanceof Holding)) {
            throw new RuntimeException("Vehicle is not holding");
        }
        if (holdingRequests.containsKey(vehicle)) {
            List<CompanyRequest> requests = holdingRequests.get(vehicle);
            return true;
        } else {
            throw new RuntimeException("Vehicle is not in holding requests");
        }
    }

    protected void sendDispatch(Vehicle vehicle) {
        List<CompanyRequest> requests = holdingRequests.remove(vehicle);
        enRouteRequests.put(vehicle, requests);

        updateVehicleRequests(vehicle, requests);

        Thread vehicleDispatchThread = company.dispatchVehicle(vehicle);
        try {
            vehicleDispatchThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(getLocalName() + " finished dispatch " + vehicle);

        enRouteRequests.remove(vehicle);
        arriveVehicle(vehicle);
        List<CompanyRequest> proceedingRequests = updateVehicleRequestsArrival(vehicle, requests);
        handleArrival(vehicle, proceedingRequests);

    }

    protected void handleArrival(Vehicle vehicle, List<CompanyRequest> requests) {
        for (CompanyRequest request : requests) {
            FulfilledRequest fulfilledRequest = request.getFulfilledRequest();
            continueRequest(fulfilledRequest);
        }
        if (queuedRequests.isEmpty()) {
            return;
        }
        while (!queuedRequests.isEmpty()) {
            FulfilledRequest request = queuedRequests.remove();
            System.out.println(getLocalName() + "queueRequests reducing to " + queuedRequests.size());
            continueRequest(request);
        }
        System.out.println(getLocalName() + "Emptied queue------");

    }

    protected void arriveVehicle(Vehicle vehicle) {
        vehicle.emptyCargo();
        company.idleVehicle(vehicle);

    }


    protected void updateVehicleRequests(Vehicle vehicle, List<CompanyRequest> requests) {
        for (CompanyRequest request : requests) {
            FulfilledRequest fulfilledRequest = request.getFulfilledRequest();
            Dispatch dispatch = request.getDispatch();
            if (dispatch instanceof RequestDispatch) {
                updateVehicleDispatch((RequestDispatch) dispatch, vehicle);
                fulfilledRequest.increaseRequestStage();
                if (!fulfilledRequest.isFinished()) {
                    informParticipants(request);
                }
            }
        }
    }

    protected List<CompanyRequest> updateVehicleRequestsArrival(Vehicle vehicle, List<CompanyRequest> requests) {
        List<CompanyRequest> proceedingRequests = new ArrayList<>();
        for (CompanyRequest request : requests) {
            FulfilledRequest fulfilledRequest = request.getFulfilledRequest();
            Dispatch dispatch = request.getDispatch();
            if (dispatch instanceof RequestDispatch) {
                updateVehicleDispatchArrival((RequestDispatch) dispatch, vehicle);

                if (fulfilledRequest.isFinished()) {
                    finishRequest(fulfilledRequest);
                } else {
                    RequestDispatch nextDispatch = fulfilledRequest.getDispatch();
                    if (!nextDispatch.getCompanyName().equals(getLocalName())) {
                        informParticipantsArrival(fulfilledRequest);
                    } else {
                        proceedingRequests.add(request);
                    }
                }
            } else {
                proceedingRequests.add(request);
            }
        }
        return proceedingRequests;
    }


    public void finishRequest(FulfilledRequest fulfilledRequest) {
        //invoked only after arrival
        System.out.println(getLocalName() + " finished request " + fulfilledRequest);
        fulfilledRequest.setFinishedOn(World.getTime());
        informClient(fulfilledRequest);
        //inform the client too
    }

    protected void informClient(FulfilledRequest fulfilledRequest) {
        //sendDispatchInformClient
        String content = fulfilledRequest.toString();
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID(fulfilledRequest.getClientName(), AID.ISLOCALNAME));
        message.setProtocol(Protocols.REQUEST_FINISHED.name());
        message.setContent(content);
        send(message);
    }

    protected void informParticipants(CompanyRequest companyRequest) {
        //sendDispatchInformParticipants
        FulfilledRequest fulfilledRequest = companyRequest.getFulfilledRequest();
        String content = fulfilledRequest.toString();
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        List<RequestDispatch> requestDispatches = fulfilledRequest.getDispatches();

        for (int i = fulfilledRequest.getRequestStage(); i < requestDispatches.size(); i++) {
            RequestDispatch d = requestDispatches.get(i);
            if (!d.getCompanyName().equals(getLocalName())) {
                message.addReceiver(new AID(d.getCompanyName(), AID.ISLOCALNAME));
//                System.out.println(getLocalName() + " sending requestDispatch to " + d.getCompanyName() + "!");
            }
        }
        message.setProtocol(Protocols.REQUEST_DISPATCHED.name());
        message.setContent(content);
        send(message);

        if (fulfilledRequest.getDispatch().getCompanyName().equals(getLocalName())) {
            prepareRequest(fulfilledRequest);
        }
    }


    protected void updateVehicleDispatch(RequestDispatch requestDispatch, Vehicle vehicle) {
        requestDispatch.setVehicle(vehicle);
        requestDispatch.setVehicleFilledUpCargo(vehicle.getFilledUpCargo());
        requestDispatch.setDispatchedOn(World.getTime());
    }

    protected void informParticipantsArrival(FulfilledRequest fulfilledRequest) {
        Dispatch dispatch = fulfilledRequest.getDispatch();
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID(dispatch.getCompanyName(), AID.ISLOCALNAME));
        message.setProtocol(Protocols.REQUEST_ARRIVAL.name());
        message.setContent(fulfilledRequest.toString());
        send(message);
    }


    protected void updateVehicleDispatchArrival(RequestDispatch requestDispatch, Vehicle vehicle) {
        requestDispatch.setArrivedOn(World.getTime());
    }

    public void prepareRequest(FulfilledRequest fulfilledRequest) {

    }

    protected boolean holdRequest(FulfilledRequest fulfilledRequest, Dispatch newDispatch, Vehicle vehicle) {
        if (vehicle.getState() instanceof Idle) {
            Dispatch dispatch = new Dispatch(newDispatch.getStart(), newDispatch.getEnd(), newDispatch.getCompanyName());
            dispatch.setVehicle(vehicle);
            company.holdVehicle(vehicle, dispatch);
        } else if (vehicle.getState() instanceof Holding) {
            Holding holding = (Holding) vehicle.getState();
            Dispatch dispatch = holding.getDispatch();
            if (!newDispatch.equals(dispatch)) {
                throw new RuntimeException("Vehicle is holding a different dispatch");
            }
        } else {
            throw new RuntimeException("Vehicle is not idle or holding");
        }
        if (!holdingRequests.containsKey(vehicle)) {
            holdingRequests.put(vehicle, new ArrayList<>());
        }
        CompanyRequest companyRequest = new CompanyRequest(fulfilledRequest, newDispatch, vehicle);
        holdingRequests.get(vehicle).add(companyRequest);

        return true;
    }


    protected void queueRequest(FulfilledRequest fulfilledRequest) {
        queuedRequests.add(fulfilledRequest);
//        System.out.println(getLocalName() + " queueing request is now at " + queuedRequests.size());
    }


    protected Vehicle getVehicleForRequest(FulfilledRequest fulfilledRequest, RequestDispatch requestDispatch,
                                           int cargoSpace) {
        Location start = requestDispatch.getStart();
        Location end = requestDispatch.getEnd();
        List<Vehicle> vehicles = company.findVehicle(end, start, 0);
        if (!vehicles.isEmpty()) {
            Vehicle vehicle = getBestVehicle(vehicles, requestDispatch, 0);
            return vehicle;
        }
        return null;
    }
}

class CompanyRequest {
    private final FulfilledRequest fulfilledRequest;
    private final Dispatch dispatch;
    private final Vehicle vehicle;

    public CompanyRequest(FulfilledRequest fulfilledRequest, Dispatch dispatch, Vehicle vehicle) {
        this.fulfilledRequest = fulfilledRequest;
        this.dispatch = dispatch;
        this.vehicle = vehicle;
    }

    public FulfilledRequest getFulfilledRequest() {
        return fulfilledRequest;
    }

    public Dispatch getDispatch() {
        return dispatch;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
