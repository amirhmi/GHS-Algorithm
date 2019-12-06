package Components;
import Events.InitMessage;
import Events.InitiateMessage;
import Events.JoinMessage;
import Events.TestMessage;
import Ports.EdgePort;
import misc.Utils;
import se.sics.kompics.*;

import java.util.*;


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

    public String lastJoinMessageTargetId;

    private int numOfTestResponds = 0;
    private List<TestMessage> lwoe = new ArrayList<>();

    Handler joinHandler = new Handler<JoinMessage>() {
        @Override
        public void handle(JoinMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                if(event.isRespond == false) {
                    if(lastJoinMessageTargetId.equalsIgnoreCase(event.nodeId)) {
                        System.out.println("node: " + nodeId + ", join message recieved from " + event.nodeId);
                        trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, true), sendPort);
                        lastJoinMessageTargetId = "";
                    }
                    else {
                        //TODO: Check if can merge or absorb
                    }
                }
                else {
                    System.out.println("node: " + nodeId + ", join respond message recieved from " + event.nodeId);
                    if (nodeId.compareToIgnoreCase(event.nodeId) > 0) {
                        fragmentId = nodeId;
                        isRoot = true;
                        children.put(event.nodeId, neighbours.get(event.nodeId));
                        System.out.println("node: " + nodeId + ", become root, send initiate trigger");
                        sendInitialMessage(nodeId);
                    } else {
                        fragmentId = event.nodeId;
                        isRoot = false;
                        parentId = event.nodeId;
                    }
                }
            }
        }
    };

    Handler testeHandler = new Handler<TestMessage>() {
        @Override
        public void handle(TestMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                if(event.isRespond == false) {
                    System.out.println("node: " + nodeId + ", recieve test message from " + event.nodeId);
                    if(event.fragmentId == fragmentId) {
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, false, event.weight), sendPort);
                        return;
                    }
                    if(event.lavel <= level | event.nodeId == lastJoinMessageTargetId)
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, true, event.weight), sendPort);
                    else
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, false, event.weight), sendPort);
                }
                else {
                    System.out.println("node: " + nodeId + ", recieve test respond message from " +
                            event.nodeId + " accept: " + event.isAccept);
                    numOfTestResponds += 1;
                    if(event.isAccept)
                        lwoe.add(event);
                    if(numOfTestResponds == neighbours.size()) {
                        lwoe.sort(Comparator.comparing(TestMessage::getWeight));
                        System.out.println(lwoe.get(0).nodeId + ' ' + lwoe.get(0).weight);
                    }
                }
            }
        }
    };

    Handler initiateHandler = new Handler<InitiateMessage>() {
        @Override
        public void handle(InitiateMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                System.out.println("node: " + nodeId + ", recieve initiate message");
                sendInitialMessage(event.rootId);

                sendTestMessages();
            }
        }
    };

    private void sendTestMessages() {
        for(Map.Entry<String, Integer> entry: neighbours.entrySet()) {
            trigger(new TestMessage(nodeId, entry.getKey(), fragmentId, level,
                    false, false, entry.getValue()), sendPort);
        }
    }

    private void sendInitialMessage(String rootId) {
        for(Map.Entry<String, Integer> entry: children.entrySet())
            trigger(new InitiateMessage(entry.getKey(), rootId), sendPort);
    }


    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            Map.Entry<String, Integer> lowestWeightNeighbor = Utils.sortByValue(neighbours).entrySet().iterator().next();
            System.out.println("node: " + nodeId + ", level0, lowe " + lowestWeightNeighbor);
            lastJoinMessageTargetId = lowestWeightNeighbor.getKey();
            trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, false), sendPort);
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
        subscribe(joinHandler,recievePort);
        subscribe(initiateHandler,recievePort);
        subscribe(testeHandler,recievePort);
    }
}

