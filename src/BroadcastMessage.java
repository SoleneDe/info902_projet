public class BroadcastMessage extends Message {
    public BroadcastMessage(Object payload, int clock) {
        super(payload, clock);
    }

    public String toString() {
        return "Broadcasted object: " + payload.toString();
    }
}
