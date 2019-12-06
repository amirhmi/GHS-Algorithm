package Events;


import se.sics.kompics.KompicsEvent;

public class TestMessage implements KompicsEvent {
    public String nodeId;
    public String targetNodeId;
    public String fragmentId;
    public int lavel;
    public boolean isRespond;
    public boolean isAccept;
    public int weight;

    public TestMessage(String nodeId, String targetNodeId, String fragmentId,
                       int lavel, boolean isRespond, boolean isAccept, int weight) {
        this.nodeId = nodeId;
        this.targetNodeId = targetNodeId;
        this.fragmentId = fragmentId;
        this.lavel = lavel;
        this.isRespond = isRespond;
        this.isAccept = isAccept;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
