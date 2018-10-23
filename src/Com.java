import com.google.common.eventbus.Subscribe;

import java.util.*;

public class Com {

    private EventBusService bus;
    private String stateToken = "null";
    private int ack = 0;
    private Queue<Object> mails;
    //Process
    private Process p;
    private HashMap<Integer,Boolean> neighboors = new HashMap<>();



    private static int nbProcess = 0;


    public Com(Lamport p)
    {
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.p = (Process)p;
        mails = new LinkedList<>();
    }


    /**
     * Take next mail, and remove it
     * @return mail
     */
    public Object readNextMail()
    {
        return mails.remove();
    }



    /**
     * Give a id from a precess demand
     * @return (int) process id
     */
    public int askId(){

        return Com.nbProcess++;
    }

    /**
     * Subscribe the process into the bus
     */
    public void askSubscribe()
    {
        this.bus.registerSubscriber(p);
    }

    /**
     * Unsubscribe the process from the bus
     */
    public void askUnsubscribe()
    {
        this.bus.unRegisterSubscriber(p);
    }

    public void sendHeartbit(boolean isAlive)
    {

        HeartbitMessage m = new HeartbitMessage(isAlive,p.getId());
        //System.out.println(p.getThread().getName() +" : Send heartbit: " + m.isAlive());

        bus.postEvent(m);

    }

    @Subscribe
    public void onHeartbit(HeartbitMessage m)
    {
        if(m.getId() != p.getId()){


            //System.out.println(p.getThread().getName() +" : Receives heartbit from : " + m.getId() + " isAlive : " + m.isAlive());
            neighboors.put(m.getId(),m.isAlive());
            System.out.println(p.getThread().getName() + " : " +Arrays.asList(neighboors));


        }

    }


    @Subscribe
    public void onToken(Token t){

        /*if (t.getPayload().equals(this.id)) {

            if (stateToken.equals("request")) {
                stateToken = "sc";
                System.out.println(this.thread.getName() + " get critical section token");

                while (stateToken.equals("sc")) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                t.setPayload((this.id + 1) % Process.nbProcess);
                if ( !this.dead ) {
                    bus.postEvent(t);
                }
                stateToken = "null";

            }else{

                t.setPayload((this.id + 1) % Process.nbProcess);
                if ( !this.dead ) {
                    bus.postEvent(t);
                }
            }
        }*/

    }

    public void request(){
        /*stateToken = "request";

        while(!stateToken.equals("sc")){
            try{
                Thread.sleep(50);
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/
    }

    public void release(){
        //stateToken = "release";
    }

    /**
     * Send object to every other process' mailbox
     * @param o data to send
     */
    public void broadcast(Object o)
    {
        /*clock.lockClock();
        clock.setClock(clock.getClock() + 1);

        AbstractMessage m = new BroadcastMessage(o, clock.getClock(), this.thread.getName());
        System.out.println("Send in broadcast: " + m.getPayload()+ ", with clock at " + clock.getClock());

        bus.postEvent(m);

        clock.unlockClock();*/
    }

    /**
     * Receive a message from a broadcast, added as mail
     * @param m message to search for in the bus
     */
    // Declaration de la methode de callback invoquee lorsqu'un message de type AbstractMessage transite sur le bus
    @Subscribe
    public void onBroadcast(BroadcastMessage m){
        //receive
        /*if(!m.getSender().equals(this.thread.getName())){
            clock.lockClock();

            System.out.println("Receives in broadcast: " + m.getPayload());
            mails.add(m.getPayload());

            clock.setClock(Math.max(clock.getClock(), m.getClock()));
            clock.setClock(clock.getClock() + 1);

            clock.unlockClock();
        }*/
    }

    /**
     * Send an object to a specific process
     * @param o data to be sent
     * @param to id of the destination
     */
    public void sendTo(Object o, int to) {
        /*clock.lockClock();
        clock.setClock(clock.getClock() + 1);

        System.out.println(this.thread.getName() + " send [" + o + "] to [ P" + to+1 + "], with clock at " + clock.getClock());
        MessageTo m = new MessageTo(o, clock.getClock(), to);

        bus.postEvent(m);

        clock.unlockClock();*/
    }

    /**
     * Receive a message from a one-to-one communication, added as mail
     * @param m message to search for in the bus
     */
    @Subscribe
    public void onReceive(MessageTo m) {
       /* if (this.id == m.getIdDest()) { // the current process is the destination
            clock.lockClock();

            System.out.println(this.thread.getName() + " receives in one to one: " + m.getPayload());
            mails.add(m.getPayload());

            clock.setClock(Math.max(clock.getClock(), m.getClock()));
            clock.setClock(clock.getClock() + 1);

            clock.unlockClock();
        }*/
    }

    /**
     * Send a sync message and wait for every other process to send one
     */
    public void synchronize() {
        // check receptions
        /*clock.lockClock();
        clock.setClock(clock.getClock() + 1);

        System.out.println(this.thread.getName() + " sends synchronization, with clock at " + clock.getClock());
        MessageSynchro m = new MessageSynchro(clock.getClock(), this.id);

        bus.postEvent(m);

        // block until everyone sends an ack
        while(ack < Process.nbProcess)
        {
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("[" + this.id + "] Every ACK received, with clock=" + clock.getClock());
        ack -= Process.nbProcess;

        clock.unlockClock();*/
    }

    /**
     * Receive messages to synchronize all processes
     * @param m message to search for in the bus
     */
    @Subscribe
    public void onSynchronize(MessageSynchro m) {
        /*clock.lockClock();

        System.out.println(this.thread.getName() + " receives synchro message from P" + (m.getFrom()+1));

        clock.setClock(Math.max(clock.getClock(), m.getClock()));
        clock.setClock(clock.getClock() + 1);

        // received ACK
        ack++;

        clock.unlockClock();*/
    }

    // TODO broadcastSync & sendToSync


}
