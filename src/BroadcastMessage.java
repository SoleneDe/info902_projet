public class BroadcastMessage extends AbstractMessage {


    protected String sender;

    public BroadcastMessage(Object payload, int clock, String sender) {

        super(payload, clock);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }


    public String toString() {

        return "Broadcasted object: " + payload.toString();
    }
}
