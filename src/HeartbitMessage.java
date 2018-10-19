public class HeartbitMessage {


    private boolean isAlive;
    private int id;

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
