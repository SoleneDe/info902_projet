/**
 * Synchronous broadcast message type
 */
public class BroadcastSyncMessage extends AbstractMessage {


    /**
     * Sender's id
     */
    private int sender;

    /**
     * Constructor for a BroadcastSyncMessage
     * @param payload The object to send as a message content
     * @param clock The value of the clock of the sender
     * @param sender sender's id
     */
    public BroadcastSyncMessage(Object payload, int clock, int sender) {
        super(payload, clock);
        this.sender = sender;
    }

    /**
     * Returns sender's id
     * @return sender
     */
    public int getSenderId() {
        return sender;
    }

    /**
     * Display a BroadcastSyncMessage
     * @return a String
     */
    public String toString() {
        return "Object: " + payload + ", from: " + sender;
    }
}


