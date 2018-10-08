public abstract class Message {
    protected Object payload=null;
    protected int clock;

    public Message(Object payload, int clock){
        this.payload = payload;
        this.clock = clock;
    }

    public Object getPayload(){
        return this.payload;
    }

    public int getClock() {
        return clock;
    }

    public String toString(){
        return "Ga Bu Zo Meu: " + this.payload.toString();
    }
}
