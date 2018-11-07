/**
 * Broadcast message type
 */
public class BroadcastMessage extends AbstractMessage {

    /**
     * Sender's id
     */
    private int sender;

    /**
     * Constructor for a BroadcastMessage
     * @param payload The object to send as a message content
     * @param clock The value of the clock of the sender
     * @param sender sender's id
     */
    public BroadcastMessage(Object payload, int clock, int sender) {

        super(payload, clock);
        this.sender = sender;
    }

    /**
     * Returns sender's id
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
