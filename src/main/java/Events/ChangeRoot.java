package Events;

import se.sics.kompics.KompicsEvent;

public class ChangeRoot implements KompicsEvent {
    public String nodeId;
    public String targetNodeId;
    public TestMessage lwoe;

    public ChangeRoot(String nodeId, String targetNodeId, TestMessage lwoe) {
        this.nodeId = nodeId;
        this.targetNodeId = targetNodeId;
        this.lwoe = lwoe;
    }
}
