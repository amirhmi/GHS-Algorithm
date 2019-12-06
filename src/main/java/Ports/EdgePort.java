package Ports;

import Events.*;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(ChangeRoot.class);
    positive(InitiateMessage.class);
    positive(JoinMessage.class);
    positive(LwoeMessage.class);
    positive(TestMessage.class);
    negative(ChangeRoot.class);
    negative(InitiateMessage.class);
    negative(JoinMessage.class);
    negative(LwoeMessage.class);
    negative(TestMessage.class);
}}
