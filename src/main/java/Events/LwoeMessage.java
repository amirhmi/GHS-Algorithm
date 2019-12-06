package Events;

import se.sics.kompics.KompicsEvent;

public class LwoeMessage implements KompicsEvent {
    public String nodeId;
    public String weight;

    public LwoeMessage(String nodeId, String weight) {
        this.nodeId = nodeId;
        this.weight = weight;
    }
}
