package Vehicle.States;

import Company.Dispatch;

public class Holding extends State {
    private final Dispatch dispatch;

    public Holding(Dispatch dispatch) {
        super();
        this.dispatch = dispatch;
    }

    public Dispatch getDispatch() {
        return dispatch;
    }
}
