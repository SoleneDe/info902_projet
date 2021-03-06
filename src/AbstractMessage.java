/**
 * Defines operations that any applicative message on the bus should implement
 */
public abstract class AbstractMessage {

    /**
     * Message content
     */
    protected Object payload=null;
    /**
     * Lamport clock to stamp the message
     */
    protected int clock;

    /**
     * Basic constructor for a AbstractMessage
     * @param payload The object to send as a message
     * @param clock The value of the clock of the sender
     */
    public AbstractMessage(Object payload, int clock){
        this.payload = payload;
        this.clock = clock;
    }

    /**
     * Returns the payload
     * @return payload
     */
    public Object getPayload(){
        return this.payload;
    }

    /**
     * Returns the clock
     * @return clock
     */
    public int getClock() {
        return clock;
    }

    /**
     * Display an AbstractMessage
     * @return a String
     */
    public String toString(){
        return "Message: " + this.payload.toString();
    }
}
