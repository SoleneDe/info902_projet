/**
 * Type of message when signalling its alive or not
 * System message
 */
public class HeartbitMessage {

    /**
     * To signal if it's alive, else it's going to stop
     */
    private boolean isAlive;
    /**
     * ID of the sender
     */
    private int id;

    /**
     * Constructor for a HeartbitMessage
     * @param isAlive Boolean to tell if it's still running or stopping
     * @param id The id of the sender
     */
    public HeartbitMessage(boolean isAlive, int id) {
        this.isAlive = isAlive;
        this.id = id;
    }

    /**
     * Returns isAlive
     * @return isAlive
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Returns the ID of the sender
     * @return id
     */
    public int getId() {
        return id;
    }
}
