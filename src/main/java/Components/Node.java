package Components;
import Events.*;
import Ports.EdgePort;
import misc.Utils;
import se.sics.kompics.*;

import javax.rmi.CORBA.Util;
import java.util.*;


public class Node extends ComponentDefinition {
    Positive<EdgePort> recievePort = positive(EdgePort.class);
    Negative<EdgePort> sendPort = negative(EdgePort.class);

    public String nodeId;
    public String parentId;
    HashMap<String,Integer> neighbours = new HashMap<>();
    HashMap<String,Integer> rejected = new HashMap<>();
    HashMap<String,Integer> children = new HashMap<>();

    Boolean isRoot = false;
    public String fragmentId;
    public int level;

    public String lastJoinMessageTargetId;

    private int numOfTestResponds = 0;
    private List<TestMessage> testResponds = new ArrayList<>();

    private List<TestMessage> initiateResponds = new ArrayList<>();

    public HashSet<String> fragmentNodes = new HashSet<>();

    private void freeLocalVar() {
        numOfTestResponds = 0;
        testResponds = new ArrayList<>();
        initiateResponds = new ArrayList<>();
    }

    Handler joinHandler = new Handler<JoinMessage>() {
        @Override
        public void handle(JoinMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                if(event.isRespond == false) {
                    if(lastJoinMessageTargetId.equalsIgnoreCase(event.nodeId)) {
                        System.out.println("node: " + nodeId + ", join message recieved from " + event.nodeId);
//                        if(level < event.level)
//                            level = event.level;
//                        else if (level == event.level)
//                            level += 1;

                        if (nodeId.compareToIgnoreCase(event.nodeId) > 0) {
                            fragmentId = nodeId;
                            isRoot = true;
                            children.put(event.nodeId, neighbours.get(event.nodeId));
                        } else {
                            fragmentId = event.nodeId;
                            isRoot = false;
                            parentId = event.nodeId;
                        }

                        trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, true, fragmentNodes), sendPort);
                        lastJoinMessageTargetId = "";
                    }
                    else {
                        //TODO: Check if can merge or absorb
                    }
                }
                else {
//                    if(level < event.level)
//                        level = event.level;
//                    else if (level == event.level)
//                        level += 1;
                    System.out.println("node: " + nodeId + ", join respond message recieved from " + event.nodeId);

                    fragmentNodes.addAll(event.fragmentNodes);
                    if (fragmentNodes.size() == 9) {
                        return;
                    }

                    if (nodeId.compareToIgnoreCase(event.nodeId) > 0) {

                        if(level < event.level)
                            level = event.level;
                        else if (level == event.level)
                            level += 1;

                        fragmentId = nodeId;
                        isRoot = true;
                        children.put(event.nodeId, neighbours.get(event.nodeId));

                        Utils.clearTheFile();

                        System.out.println("node: " + nodeId + ", become root, send initiate trigger");
                        freeLocalVar();
                        sendInitialMessage(nodeId);
                        sendTestMessages();
                    } else {
                        fragmentId = event.nodeId;
                        isRoot = false;
                        parentId = event.nodeId;
                        trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, true, fragmentNodes), sendPort);
                    }

                }
            }
        }
    };

    private void checkAndSendInitiateBack() {
        TestMessage lwoe = null;

        if(numOfTestResponds == neighbours.size() - rejected.size() & initiateResponds.size() == children.size()) {
            initiateResponds.addAll(testResponds);

            initiateResponds.removeAll(Collections.singleton(null));

            if(initiateResponds.size() > 0) {
                initiateResponds.sort(Comparator.comparing(TestMessage::getWeight));
                lwoe = initiateResponds.get(0);
            }

            if(isRoot == false) {
                trigger(new InitiateMessage(nodeId, parentId, fragmentId, level, true, lwoe), sendPort);
            }
            else {
                if(lwoe == null)
                    return;
                else {
                    if(fragmentNodes.contains(lwoe.nodeId))
                        return;
                    isRoot = false;
                    System.out.println("node: " + nodeId + ", change root send for: " + lwoe.targetNodeId);
                    if(lwoe.targetNodeId == nodeId)
                        sendJoinMessage(lwoe.nodeId);
                    else
                        sendChangeRoot(lwoe);
                }
            }
        }
    }

    Handler testHandler = new Handler<TestMessage>() {
        @Override
        public void handle(TestMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                if(event.isRespond == false) {
//                    System.out.println("node: " + nodeId + ", test message from: " + event.nodeId + " ownFID: "
//                            + fragmentId + " itsFID: " + event.fragmentId + " " +
//                            " eventLevel: " + event.level + " level: " + level);
                    if (event.fragmentId == fragmentId) {
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, false, event.weight), sendPort);
                        return;
                    }

                    if (event.level <= level | event.nodeId.equals(lastJoinMessageTargetId))
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, true, event.weight), sendPort);
                    else
                        trigger(new TestMessage(nodeId, event.nodeId, fragmentId, level,
                                true, false, event.weight), sendPort);
                }
                else {
//                    System.out.println("node: " + nodeId + ", recieve test respond message from " +
//                            event.nodeId + " accept: " + event.isAccept);
                    numOfTestResponds += 1;
                    if(event.isAccept)
                        testResponds.add(event);
                    else {
                        System.out.println("remove node " + event.nodeId + " from " + nodeId);
                        rejected.put(event.nodeId, 0);
                        numOfTestResponds -= 1;
                    }
                    checkAndSendInitiateBack();
                }
            }
        }
    };

    Handler initiateHandler = new Handler<InitiateMessage>() {
        @Override
        public void handle(InitiateMessage event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                fragmentId = event.rootId;
                level = event.level;
                if(event.isRespond == false) {

                    System.out.println("node: " + nodeId + ", recieve initiate message");
                    String printed = event.nodeId + '-' + nodeId + "," + neighbours.get(event.nodeId);
                    System.out.println(printed);
                    Utils.appendStrToFile(printed);

                    if(children.containsKey(event.nodeId))
                        children.remove(event.nodeId);
                    parentId = event.nodeId;

                    freeLocalVar();
                    sendInitialMessage(event.rootId);
                    sendTestMessages();
                }
                else {
                    System.out.println("node: " + nodeId + ", recieve initiate respond message from: "
                            + event.nodeId);
                    initiateResponds.add(event.lwoe);
                    checkAndSendInitiateBack();
                }
            }
        }
    };

    Handler changeRootHandler = new Handler<ChangeRoot>() {
        @Override
        public void handle(ChangeRoot event) {
            if (nodeId.equalsIgnoreCase(event.targetNodeId)) {
                System.out.println("node: " + nodeId + " change root recieved");
                if(event.lwoe.targetNodeId == nodeId) {
                    sendJoinMessage(event.lwoe.nodeId);
                }
                else {
                    sendChangeRoot(event.lwoe);
                }
                children.put(event.nodeId, neighbours.get(event.nodeId));
            }
        }
    };

    private void sendJoinMessage(String lwoeId) {
        System.out.println("node: " + nodeId + ", send join message to: " + lwoeId);
        lastJoinMessageTargetId = lwoeId;
        trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, false, fragmentNodes), sendPort);
    }

    private void sendChangeRoot(TestMessage lwoe) {
        for(Map.Entry<String, Integer> entry: children.entrySet())
            trigger(new ChangeRoot(nodeId, entry.getKey(), lwoe), sendPort);
    }

    private void sendTestMessages() {
        for(Map.Entry<String, Integer> entry: neighbours.entrySet()) {
            if(!rejected.containsKey(entry.getKey()))
                trigger(new TestMessage(nodeId, entry.getKey(), fragmentId, level,
                        false, false, entry.getValue()), sendPort);
        }
    }

    private void sendInitialMessage(String rootId) {
        System.out.println("node: " + nodeId + " send initiate messages " + children);
        for(Map.Entry<String, Integer> entry: children.entrySet())
            trigger(new InitiateMessage(nodeId, entry.getKey(), rootId, level, false, null), sendPort);
        checkAndSendInitiateBack();
    }


    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            Map.Entry<String, Integer> lowestWeightNeighbor = Utils.sortByValue(neighbours).entrySet().iterator().next();
            System.out.println("node: " + nodeId + ", level0, lowe " + lowestWeightNeighbor);
            lastJoinMessageTargetId = lowestWeightNeighbor.getKey();
            trigger(new JoinMessage(lastJoinMessageTargetId, nodeId, level, false, fragmentNodes), sendPort);
        }
    };

    public Node(InitMessage initMessage) {
        System.out.println("initNode :" + initMessage.nodeId);
        nodeId = initMessage.nodeId;
        this.neighbours = initMessage.neighbours;
        this.level = 0;
        this.fragmentId = initMessage.nodeId;
        this.parentId = null;
        fragmentNodes.add(nodeId);
        subscribe(startHandler, control);
        subscribe(joinHandler,recievePort);
        subscribe(initiateHandler,recievePort);
        subscribe(testHandler,recievePort);
        subscribe(changeRootHandler,recievePort);
    }
}

