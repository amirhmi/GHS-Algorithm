package Events;

import se.sics.kompics.KompicsEvent;

public class JoinMessage implements KompicsEvent {
    public String nodeId;
    public String level;

    public JoinMessage(String nodeId, String level) {
        this.nodeId = nodeId;
        this.level = level;
    }
}
