package org.redsha.transbox.service.event;

public class OpenBoxEvent {

    /**
     * true：调用开箱，false：调用关箱
     */
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}
