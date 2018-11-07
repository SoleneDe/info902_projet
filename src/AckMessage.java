public class AckMessage {



    private boolean ack;
    private int to;

    public AckMessage(boolean ack, int to) {
        this.ack = ack;
        this.to = to;
    }

    public boolean isAck() {
        return ack;
    }

    public int getTo() {
        return to;
    }
}
