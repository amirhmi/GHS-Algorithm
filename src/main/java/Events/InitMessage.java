package Events;

import Components.Node;
import se.sics.kompics.Init;

import java.util.ArrayList;
import java.util.HashMap;

public class InitMessage extends Init<Node> {
    public String nodeId;
    public HashMap<String,Integer> neighbours = new HashMap<>();

    public InitMessage(String nodeId, HashMap<String,Integer> neighbours) {
        this.nodeId = nodeId;
        this.neighbours = neighbours;
    }
}