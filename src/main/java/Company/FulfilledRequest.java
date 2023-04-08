package Company;

import Company.Dispatch;

import java.util.ArrayList;
import java.util.List;

import Network.Network;

public class FulfilledRequest extends Request {
    private List<Dispatch> dispatches = new ArrayList<>();
    private double price;
    private int startedOn;
    private int finishedOn;

    public FulfilledRequest(Request request) {
        super(request.getStart(), request.getEnd(), request.getProductName(), request.getQuantity());
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

    @Override
    public String toString() {
        String dispatchesString = String.join(":", dispatches.stream().map(Dispatch::toString).toArray(String[]::new));
        return super.toString() + ";" + dispatchesString + ";" + price + ";" + startedOn + ";" + finishedOn + ";";
    }
}
