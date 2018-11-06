/**
 * Type of a one-to-one message
 */
public class MessageTo extends AbstractMessage {

    private int idDest; // process id of the destination

    /**
     * Constructor for a MessageTo
     * @param payload The object to send as a message
     * @param clock The value of the clock of the sender
     * @param idDest The ID of the destination
     */
    public MessageTo(Object payload, int clock, int idDest) {
        super(payload, clock);
        this.idDest = idDest;
    }

    /**
     * Returns the ID of the destination
     * @return idDest
     */
    public int getIdDest() {
        return idDest;
    }

    /**
     * Display a MessageTo
     * @return a String
     */
    public String toString() {
        return "Object: " + payload + ", to: " + idDest;
    }
}
