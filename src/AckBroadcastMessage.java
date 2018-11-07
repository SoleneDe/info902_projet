/**
 * System message for an acknowledgment during synchronous boadcast
 */
public class AckBroadcastMessage {


    /**
     * Recipient's id
     */
    private int to;

    /**
     * Basic constructor for a AckBroadcastMessage
     * @param to recipient's id
     */
    public AckBroadcastMessage(int to) {

        this.to = to;
    }

    /**
     * Returns the recipient's id
     * @return to
     */
    public int getTo() {
        return to;
    }
}


