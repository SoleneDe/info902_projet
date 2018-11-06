/**
 * Type of message when signalling its alive or not
 */
public class HeartbitMessage {

    private boolean isAlive;
    private int id;

    /**
     * Constructor for a HeartbitMessage
     * @param isAlive
     * @param id
     */
    public HeartbitMessage(boolean isAlive, int id) {
        this.isAlive = isAlive;
        this.id = id;
    }


    public boolean isAlive() {
        return isAlive;
    }

    public int getId() {
        return id;
    }
}
