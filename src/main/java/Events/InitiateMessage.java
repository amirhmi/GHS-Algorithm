package Events;

import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    public String nodeId;
    public String targetNodeId;
    public String rootId;
    public int level;
    public boolean isRespond;
    public TestMessage lwoe;

    public InitiateMessage(String nodeId, String targetNodeId, String rootId, int level,
                           boolean isRespond, TestMessage lwoe) {
        this.nodeId = nodeId;
        this.targetNodeId = targetNodeId;
        this.rootId = rootId;
        this.level = level;
        this.isRespond = isRespond;
        this.lwoe = lwoe;
    }
}
