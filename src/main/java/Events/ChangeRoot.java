package Events;

import se.sics.kompics.KompicsEvent;

public class ChangeRoot implements KompicsEvent {
    public String targetNodeId;

    public ChangeRoot(String targetNodeId) {
        this.targetNodeId = targetNodeId;
    }
}
