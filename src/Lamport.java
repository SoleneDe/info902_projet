public interface Lamport {

    int getClock();
    void setClock(int clock);
    void lockClock();
    void unlockClock();

}
