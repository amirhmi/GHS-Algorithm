package Events;


import se.sics.kompics.KompicsEvent;

public class TestMessage implements KompicsEvent {
    public String name;
    public String lavel;

    public TestMessage(String name, String lavel) {
        this.name = name;
        this.lavel = lavel;
    }
}
