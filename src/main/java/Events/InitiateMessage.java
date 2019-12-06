package Events;

import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    public String rootId;

    public InitiateMessage(String rootId) {
        this.rootId = rootId;
    }
}
