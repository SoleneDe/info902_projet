public class BroadcastMessage extends AbstractMessage {

    protected int sender;

    public BroadcastMessage(Object payload, int clock, int sender) {

        super(payload, clock);
        this.sender = sender;
    }

    public int getSender() {
        return sender;
    }


    public String toString() {

        return "Broadcasted object: " + payload.toString();
    }
}
