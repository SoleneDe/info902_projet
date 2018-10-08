public class Bidule {
    private String machin=null;
    private int clock;

    public Bidule(String machin, int clock){
        this.machin = machin;
        this.clock = clock;
    }

    public String getMachin(){
        return this.machin;
    }

    public int getClock() {
        return clock;
    }

    public String toString(){
        return "Ga Bu Zo Meu: " + this.machin;
    }
}
