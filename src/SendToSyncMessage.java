public class SendToSyncMessage extends AbstractMessage {


    private int idDest;// process id of the destination
    private int idOrigin;

    /**
     * Constructor for a MessageTo
     * @param payload The object to send as a message
     * @param clock The value of the clock of the sender
     * @param idDest The ID of the destination
     * @param idOrigin The ID of the origin
     */
    public SendToSyncMessage(Object payload, int clock, int idDest, int idOrigin) {
        super(payload, clock);
        this.idDest = idDest;
        this.idOrigin = idOrigin;
    }

    /**
     * Returns the ID of the destination
     * @return idDest
     */
    public int getIdDest() {
        return idDest;
    }


    public int getIdOrigin() {
        return idOrigin;
    }

    /**
     * Display a MessageTo
     * @return a String
     */
    public String toString() {
        return "Object: " + payload + ", from: " +idOrigin +", to: " + idDest;
    }
}
