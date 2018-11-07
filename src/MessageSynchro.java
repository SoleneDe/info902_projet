/**
 * Type of a synchronization message
 */
public class MessageSynchro extends AbstractMessage {

    /**
     * ID of the sender
     */
    private int from;

    /**
     * Constructor for a MessageSynchro
     * @param clock The value of the clock of the sender
     * @param from The ID of the sender
     */
    public MessageSynchro(int clock, int from) {
        super("Synchronization...", clock);
        this.from = from;
    }

    /**
     * Returns the id of the sender
     * @return from
     */
    public int getFrom()
    {
        return this.from;
    }

    /**
     * Display a MessageSynchro
     * @return a String
     */
    public String toString() {
        return "Object: " + payload + ", from: " + this.from;
    }
}
