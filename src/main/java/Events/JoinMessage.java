package Events;

import se.sics.kompics.KompicsEvent;

public class JoinMessage implements KompicsEvent {
    public String targetNodeId;
    public String nodeId;
    public int level;
    public boolean isRespond;

    public JoinMessage(String targetNodeId, String nodeId, int level, boolean isRespond) {
        this.targetNodeId = targetNodeId;
        this.nodeId = nodeId;
        this.level = level;
        this.isRespond = isRespond;
    }
}
