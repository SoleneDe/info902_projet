/**
 * System message for an acknowledgment during synchronous one to one communication
 */
public class AckSendToMessage {

    /**
     * Recipient's id
     */
    private int to;

    /**
     * Basic constructor for a AckSendToMessage
     * @param to
     */
    public AckSendToMessage(int to) {
        this.to = to;
    }

    /**
     * Returns recipent's id
     * @return to
     */
    public int getTo() {
        return to;
    }
}
