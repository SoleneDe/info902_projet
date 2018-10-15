import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;


public class Process  implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private int clock = 0;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int ack = 0;

    public Process(String name){

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();
    }

    public void broadcast(Object o)
    {
        this.clock++;
        System.out.println(Thread.currentThread().getName() + " clock : " + this.clock);
        AbstractMessage m1 = new BroadcastMessage(o,this.clock, Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName() + " send : " + m1.getPayload());
        bus.postEvent(m1);
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type AbstractMessage transite sur le bus
    @Subscribe
    public void onBroadcast(BroadcastMessage m){
        //receive
        if(!m.getSender().equals(this.thread.getName())){
            System.out.println(Thread.currentThread().getName() + " receives: " + m.getPayload() + " for " + this.thread.getName());
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


    public void sendTo(Object o, int to) {
        this.clock++;
        System.out.println(Thread.currentThread().getName() + " send [" + o + "] to [id " + to + "], with clock at " + this.clock);
        MessageTo m = new MessageTo(o, this.clock, to);
        bus.postEvent(m);
    }

    @Subscribe
    public void onReceive(MessageTo m) {
        if (this.id == m.getIdDest()) { // the current process is the destination
            System.out.println(Thread.currentThread().getName() + " receives: " + m.getPayload()  + " for " + this.thread.getName());
            this.clock = Math.max(this.clock, m.getClock());
            this.clock++;
            System.out.println("New clock (" + Thread.currentThread().getName() + ", id " + this.id + "): " + this.clock);
        }
    }

    public void synchronize() {
        // check receptions
        ack = 0;

        this.clock++;
        System.out.println(Thread.currentThread().getName() + " sends synchronization, with clock at " + this.clock);
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
    }

    @Subscribe
    public void onSynchronize(MessageSynchro m) {
        System.out.println(Thread.currentThread().getName() + " receives synchro message from id " + m.getFrom());
        this.clock = Math.max(this.clock, m.getClock());
        this.clock++;
        System.out.println("New clock (" + Thread.currentThread().getName() + ", id " + this.id + "): " + this.clock);

        // received ACK
        ack++;
    }


    public void run(){

        System.out.println(Thread.currentThread().getName() + " id: " + this.id);

        while(this.alive){
            try{
                Thread.sleep(500);

                synchronize();
                System.out.println("Sync OK");

                if(Thread.currentThread().getName().equals("P1")){
                    // send

                    //broadcast("ga");
                    //sendTo("Hello", 1);

                    // test synchronization, one process sleeps, others should wait as well
                    System.out.println("WAIT");
                    Thread.sleep(1300);
                }



            }catch(Exception e){

                e.printStackTrace();
            }
        }

        // liberation du bus
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
        System.out.println(Thread.currentThread().getName() + " stopped");
        this.dead = true;
    }

    public void waitStopped(){
        while(!this.dead){
            try{
                Thread.sleep(500);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        this.alive = false;
    }

    public int getClock() {
        return clock;
    }
}
