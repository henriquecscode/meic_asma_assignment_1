package Company;

import java.util.ArrayList;
import java.util.List;

import Network.Network;

public class FulfilledRequest extends Request {
    private static final int PARENT_ATTRIBUTES = 4;
    private List<RequestDispatch> requestDispatches = new ArrayList<>();
    private double price;
    private long startedOn;
    private long finishedOn;
    private int requestStage;

    public FulfilledRequest(Request request) {
        super(request.getStart(), request.getEnd(), request.getProductName(), request.getClientName(), request.getQuantity());
        this.requestStage = 0;
    }


    public FulfilledRequest(Network network, String request) {
        super(network, request);
        int i = PARENT_ATTRIBUTES;
        String requestInfo[] = request.split(";");
        String dispatchesInfo[] = requestInfo[i + 0].split(":");
        for (String dispatchInfo : dispatchesInfo) {
            RequestDispatch requestDispatch = new RequestDispatch(network, dispatchInfo);
            this.requestDispatches.add(requestDispatch);
        }
        this.price = Double.parseDouble(requestInfo[i + 1]);
        this.startedOn = Integer.parseInt(requestInfo[i + 2]);
        this.finishedOn = Integer.parseInt(requestInfo[i + 3]);
        this.requestStage = Integer.parseInt(requestInfo[i + 4]);
    }

    public void setDispatches(List<RequestDispatch> requestDispatches) {
        this.requestDispatches = requestDispatches;
    }

    public List<RequestDispatch> getDispatches() {
        return requestDispatches;
    }

    public RequestDispatch getDispatch(int i) {
        return requestDispatches.get(i);
    }

    public RequestDispatch getDispatch() {
        if (requestStage >= requestDispatches.size()) {
            return null;
        }
        return requestDispatches.get(requestStage);
    }

    public double getPrice() {
        return price;
    }

    public long getStartedOn() {
        return startedOn;
    }

    public long getFinishedOn() {
        return finishedOn;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStartedOn(long startedOn) {
        this.startedOn = startedOn;
    }

    public void setFinishedOn(long finishedOn) {
        this.finishedOn = finishedOn;
    }

    public int getRequestStage() {
        return requestStage;
    }

    public void setRequestStage(int requestStage) {
        this.requestStage = requestStage;
    }

    public void increaseRequestStage() {
        this.requestStage++;
    }

    public boolean isFinished() {
        return requestStage >= requestDispatches.size();
    }

    @Override
    public String toString() {
        String dispatchesString = String.join(":", requestDispatches.stream().map(RequestDispatch::toString).toArray(String[]::new));
        return super.toString() + ";" + dispatchesString + ";" + price + ";" + startedOn + ";" + finishedOn + ";" + requestStage;
    }
}
