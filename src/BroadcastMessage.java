/**
 * Type of a broadcasted message
 */
public class BroadcastMessage extends AbstractMessage {

    private int sender; // id of the sender

    /**
     * Constructor for a BroadcastMessage
     * @param payload The object to send as a message
     * @param clock The value of the clock of the sender
     * @param sender The id of the sender
     */
    public BroadcastMessage(Object payload, int clock, int sender) {

        super(payload, clock);
        this.sender = sender;
    }

    /**
     * Returns the id of the sender
     * @return sender
     */
    public int getSender() {
        return sender;
    }

    /**
     * Display a BroadcastMessage
     * @return a String
     */
    public String toString() {

        return "Broadcasted object: " + payload.toString();
    }
}
