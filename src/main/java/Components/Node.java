package Components;
import Events.InitMessage;
import Ports.EdgePort;
import se.sics.kompics.*;

import java.util.HashMap;


public class Node extends ComponentDefinition {
    Positive<EdgePort> recievePort = positive(EdgePort.class);
    Negative<EdgePort> sendPort = negative(EdgePort.class);

    public String nodeId;
    public String parentId;
    HashMap<String,Integer> neighbours = new HashMap<>();

    Boolean isRoot = false;
    public String fragmentId;
    public int level;

    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            print(fragmentId);
        }
    };

    public Node(InitMessage initMessage) {
        print("initNode :" + initMessage.nodeId);
        nodeId = initMessage.nodeId;
        this.neighbours = initMessage.neighbours;
        this.isRoot = true;
        this.level = 0;
        this.fragmentId = initMessage.nodeId;
        subscribe(startHandler, control);
//        subscribe(reportHandler,recievePort);
//        subscribe(routingHandler,recievePort);
    }

    private void print(String string) {
        System.out.println(string);
    }
}

