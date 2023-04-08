package Company;

import Company.Dispatch;

import java.util.ArrayList;
import java.util.List;

import Network.Network;

public class FulfilledRequest extends Request {
    private List<Dispatch> dispatches = new ArrayList<>();
    private double price;
    private long startedOn;
    private long finishedOn;
    private int requestStage;

    public FulfilledRequest(Request request) {
        super(request.getStart(), request.getEnd(), request.getProductName(), request.getQuantity());
        this.requestStage = -1;
    }


    public FulfilledRequest(Network network, String request) {
        super(network, request);
        String requestInfo[] = request.split(";");
        String dispatchesInfo[] = requestInfo[3].split(":");
        for (String dispatchInfo : dispatchesInfo) {
            Dispatch dispatch = new Dispatch(network, dispatchInfo);
            this.dispatches.add(dispatch);
        }
        this.price = Double.parseDouble(requestInfo[4]);
        this.startedOn = Integer.parseInt(requestInfo[5]);
        this.finishedOn = Integer.parseInt(requestInfo[6]);
    }

    public void setDispatches(List<Dispatch> dispatches) {
        this.dispatches = dispatches;
    }

    public List<Dispatch> getDispatches() {
        return dispatches;
    }

    public Dispatch getDispatch(int i) {
        return dispatches.get(i);
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

    @Override
    public String toString() {
        String dispatchesString = String.join(":", dispatches.stream().map(Dispatch::toString).toArray(String[]::new));
        return super.toString() + ";" + dispatchesString + ";" + price + ";" + startedOn + ";" + finishedOn + ";";
    }
}
