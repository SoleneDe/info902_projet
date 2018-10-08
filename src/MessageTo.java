public class MessageTo extends AbstractMessage {

    private int idDest; // process id of the destination

    public MessageTo(Object payload, int clock, int idDest) {
        super(payload, clock);
        this.idDest = idDest;
    }

    public int getIdDest() {
        return idDest;
    }

    public String toString() {
        return "Object: " + payload + ", to: " + idDest;
    }
}
