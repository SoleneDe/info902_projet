public class MessageSynchro extends AbstractMessage {

    private int from;

    public MessageSynchro(int clock, int from) {
        super("Synchronization...", clock);
        this.from = from;
    }

    public int getFrom()
    {
        return this.from;
    }

    public String toString() {
        return "Object: " + payload + ", from: " + this.from;
    }
}
