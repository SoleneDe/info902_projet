import com.google.common.eventbus.Subscribe;

import java.util.*;

public class Com {

    private EventBusService bus;
    private String stateToken = "null";
    private int ack = 0;
    private Queue<Object> mails;
    //Process
    private Lamport p;
    //private HashMap<Integer,Boolean> neighbours = new HashMap<>();
    private int id;

    private static int nbProcess = 0;


    public Com(Lamport p)
    {
        this.id = Com.nbProcess++;
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.p = p;
        mails = new LinkedList<>();
    }

    public void unregister()
    {
        this.bus.unRegisterSubscriber(this);
    }

    /**
     * Take next mail, and remove it
     * @return mail
     */
    public Object readNextMail()
    {
        if(mails.isEmpty())
        {
            return null;
        }
        return mails.remove();
    }

    /**
     * Give a id from a process demands
     * @return (int) process id
     */
    public int getId(){
        return this.id;
    }

    public void sendHeartbit(boolean isAlive)
    {
        HeartbitMessage m = new HeartbitMessage(isAlive, this.id);
        //System.out.println(p.getThread().getName() +" : Send heartbit: " + m.isAlive());

        bus.postEvent(m);

        if (!isAlive)
        {
            Com.nbProcess--;
        }

    }

    @Subscribe
    public void onHeartbit(HeartbitMessage m)
    {
        if(m.getId() != this.id){

            if(!m.isAlive())
            {
                changeIdAfterDeath(m.getId());
            }

        }

    }

    private void changeIdAfterDeath(int id) {
        if (this.id > id)
        {
            this.id--;
        }
    }


    @Subscribe
    public void onToken(Token t){

        if (t.getPayload().equals(this.id)) {

            if (stateToken.equals("request")) {
                stateToken = "sc";

                System.out.println(this.id + " get critical section token");
                while (stateToken.equals("sc")) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                t.setPayload((this.id + 1) % Com.nbProcess);
                //if ( !this.dead ) {
                    bus.postEvent(t);
                //}
                stateToken = "null";

            }else{

                t.setPayload((this.id + 1) % Com.nbProcess);
                //if ( !this.dead ) {
                    bus.postEvent(t);
                //}
            }
        }

    }

    public void request(){
        stateToken = "request";

        while(!stateToken.equals("sc")){
            try{
                Thread.sleep(50);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void release(){
        stateToken = "release";

        System.out.println(this.id + " releases sc");
    }

    public void startToken()
    {
        if(this.id == Com.nbProcess-1){
            Token t = new Token(this.id);
            bus.postEvent(t);
            System.out.println("Token thrown");
        }
    }

    /**
     * Send object to every other process' mailbox
     * @param o data to send
     */
    public void broadcast(Object o)
    {
        p.lockClock();
        p.setClock(p.getClock() + 1);

        BroadcastMessage m = new BroadcastMessage(o, p.getClock(), this.id);
        //System.out.println(this.id + " broadcasts: " + m.getPayload()+ ", with clock at " + p.getClock());

        bus.postEvent(m);

        p.unlockClock();
    }

    /**
     * Receive a message from a broadcast, added as mail
     * @param m message to search for in the bus
     */
    // Declaration de la methode de callback invoquee lorsqu'un message de type AbstractMessage transite sur le bus
    @Subscribe
    public void onBroadcast(BroadcastMessage m){
        //receive
        if(m.getSender() != this.id){
            p.lockClock();

            //System.out.println(this.id + " receives in broadcast: " + m.getPayload());
            mails.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();
        }
    }

    /**
     * Send an object to a specific process
     * @param o data to be sent
     * @param to id of the destination
     */
    public void sendTo(Object o, int to) {
        p.lockClock();
        p.setClock(p.getClock() + 1);

        System.out.println(this.id + " send [" + o + "] to [" + to + "], with clock at " + p.getClock());
        MessageTo m = new MessageTo(o, p.getClock(), to);

        bus.postEvent(m);

        p.unlockClock();
    }

    /**
     * Receive a message from a one-to-one communication, added as mail
     * @param m message to search for in the bus
     */
    @Subscribe
    public void onReceive(MessageTo m) {
        if (this.id == m.getIdDest()) { // the current process is the destination
            p.lockClock();

            System.out.println(this.id + " receives in one to one: " + m.getPayload());
            mails.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();
        }
    }

    /**
     * Send a sync message and wait for every other process to send one
     */
    public void synchronize() {
        // check receptions
        p.lockClock();
        p.setClock(p.getClock() + 1);

        System.out.println(this.id + " sends synchronization, with clock at " + p.getClock());
        MessageSynchro m = new MessageSynchro(p.getClock(), this.id);
        p.unlockClock();

        bus.postEvent(m);

        // block until everyone sends an ack
        while(ack < Com.nbProcess-1)
        {
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("[" + this.id + "] Every ACK received, with clock=" + p.getClock());
        ack -= Com.nbProcess-1;
    }

    /**
     * Receive messages to synchronize all processes
     * @param m message to search for in the bus
     */
    @Subscribe
    public void onSynchronize(MessageSynchro m) {

        if (m.getFrom() != this.id) {

            System.out.println(this.id + " receives synchro message from " + m.getFrom());

            p.lockClock();
            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);
            p.unlockClock();

            // received ACK
            ack++;
        }
    }

    // TODO broadcastSync & sendToSync


}
