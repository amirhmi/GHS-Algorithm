package Events;

import se.sics.kompics.KompicsEvent;

import java.util.HashSet;

public class JoinMessage implements KompicsEvent {
    public String targetNodeId;
    public String nodeId;
    public int level;
    public boolean isRespond;
    public HashSet<String> fragmentNodes;

    public JoinMessage(String targetNodeId, String nodeId, int level, boolean isRespond,
                       HashSet<String> fragmentNodes) {
        this.targetNodeId = targetNodeId;
        this.nodeId = nodeId;
        this.level = level;
        this.isRespond = isRespond;
        this.fragmentNodes = fragmentNodes;
    }
}
