public class BroadcastSyncMessage extends AbstractMessage {


    private int idOrigin;

    /**
     * Constructor for a MessageTo
     * @param payload The object to send as a message
     * @param clock The value of the clock of the sender
     * @param idOrigin The ID of the origin
     */
    public BroadcastSyncMessage(Object payload, int clock, int idOrigin) {
        super(payload, clock);
        this.idOrigin = idOrigin;
    }

    public int getIdOrigin() {
        return idOrigin;
    }

    /**
     * Display a MessageTo
     * @return a String
     */
    public String toString() {
        return "Object: " + payload + ", from: " +idOrigin;
    }
}


