package Events;

import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    public String targetNodeId;
    public String rootId;

    public InitiateMessage(String targetId, String rootId) {
        this.targetNodeId = targetId;
        this.rootId = rootId;
    }
}
