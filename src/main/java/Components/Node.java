package Components;
import Events.InitMessage;
import Ports.EdgePort;
import misc.Utils;
import se.sics.kompics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Node extends ComponentDefinition {
    Positive<EdgePort> recievePort = positive(EdgePort.class);
    Negative<EdgePort> sendPort = negative(EdgePort.class);

    public String nodeId;
    public String parentId;
    HashMap<String,Integer> neighbours = new HashMap<>();
    HashMap<String,Integer> children = new HashMap<>();

    Boolean isRoot = false;
    public String fragmentId;
    public int level;

//    Handler reportHandler = new Handler<ReportMessage>() {
//        @Override
//        public void handle(ReportMessage event) {
//            if (nodeName.equalsIgnoreCase(event.dst)) {
//            }
//        }
//    };


    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            System.out.println(nodeId + ' ' + Utils.sortByValue(neighbours).entrySet().iterator().next());
//            System.out.println(minWeightNeighbor);
        }
    };

    public Node(InitMessage initMessage) {
        System.out.println("initNode :" + initMessage.nodeId);
        nodeId = initMessage.nodeId;
        this.neighbours = initMessage.neighbours;
        this.isRoot = true;
        this.level = 0;
        this.fragmentId = initMessage.nodeId;
        this.parentId = null;
        subscribe(startHandler, control);
//        subscribe(reportHandler,recievePort);
//        subscribe(routingHandler,recievePort);
    }
}

