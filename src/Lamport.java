/**
 * Interface needed to deal with a clock
 * Needed by the Com class
 */
public interface Lamport {

    /**
     * Returns the value of the clock
     * @return The clock
     */
    int getClock();

    /**
     * Set the value of the clock
     * @param clock The new value of the clock
     */
    void setClock(int clock);

    /**
     * Used to manage the local critical section when starting to access the clock
     */
    void lockClock();

    /**
     * Used to manage the local critical section when ending an access to the clock
     */
    void unlockClock();

}
