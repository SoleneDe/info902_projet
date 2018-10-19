import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Com {

    private EventBusService bus;
    private String stateToken = "null";
    private int ack = 0;
    private Queue<AbstractMessage> mails;
    private Lamport clock;



    private static int nbProcess = 0;


    public Com(Lamport clock)
    {
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.clock = clock;
        mails = new LinkedList<>();
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
     * @param p process to subscribe
     */
    public void askSubscribe(Process p)
    {
        this.bus.registerSubscriber(p);
    }

    /**
     * Unsubscribe the process from the bus
     * @param p process to unsubscribe
     */
    public void askUnsubscribe(Process p)
    {
        this.bus.unRegisterSubscriber(p);
    }

    public void sendHeartbit()
    {

    }

    public void onHeartbit(HeartbitMessage hbm)
    {

    }


    /*@Subscribe
    public void onToken(Token t){

        if (t.getPayload().equals(this.id)) {

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
    }*/



    public void broadcast(Object o)
    {
        clock.lockClock();
        clock.setClock(clock.getClock() + 1);

        AbstractMessage m = new BroadcastMessage(o, clock.getClock(), this.thread.getName());
        System.out.println("Send in broadcast: " + m.getPayload()+ ", with clock at " + clock.getClock());

        bus.postEvent(m);

        clock.unlockClock();
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type AbstractMessage transite sur le bus
    @Subscribe
    public void onBroadcast(BroadcastMessage m){
        //receive
        if(!m.getSender().equals(this.thread.getName())){
            System.out.println(this.thread.getName() + " receives in broadcast: " + m.getPayload());
            broadcastData.add(m.getPayload());
            if(m.getClock() > this.clock)
            {
                this.clock = m.getClock()+1;
            }
            else
            {
                this.clock++;
            }
        }

    }


    /*public void sendTo(Object o, int to) {
        this.clock++;
        System.out.println(this.thread.getName() + " send [" + o + "] to [ P" + to+1 + "], with clock at " + this.clock);
        MessageTo m = new MessageTo(o, this.clock, to);
        bus.postEvent(m);
    }

    @Subscribe
    public void onReceive(MessageTo m) {
        if (this.id == m.getIdDest()) { // the current process is the destination
            System.out.println(this.thread.getName() + " receives in one to one: " + m.getPayload());
            this.clock = Math.max(this.clock, m.getClock());
            this.clock++;

            //System.out.println("New clock (" + this.thread.getName() + "): " + this.clock);

        }
    }

    public void synchronize() {
        // check receptions


        this.clock++;
        System.out.println(this.thread.getName() + " sends synchronization, with clock at " + this.clock);
        MessageSynchro m = new MessageSynchro(this.clock, this.id);
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


        System.out.println("[" + this.id + "] Every ACK received, with clock=" + this.getClock());
        ack -= Process.nbProcess;
    }

    @Subscribe
    public void onSynchronize(MessageSynchro m) {
        System.out.println(this.thread.getName() + " receives synchro message from P" + (m.getFrom()+1));
        this.clock = Math.max(this.clock, m.getClock());
        this.clock++;
        //System.out.println("New clock (" + this.thread.getName()+"): " + this.clock);

        // received ACK
        ack++;
    }*/


}
