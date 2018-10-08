import com.google.common.eventbus.Subscribe;


public class Process  implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private int clock = 0;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;

    public Process(String name){

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type Message transite sur le bus
    @Subscribe
    public void onMessageBus(BroadcastMessage m){
        //receive
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
            System.out.println("New clock (" + Thread.currentThread().getName() + "): " + this.clock);
        }
    }

    public void run(){

        System.out.println(Thread.currentThread().getName() + " id: " + this.id);

        while(this.alive){
            try{
                Thread.sleep(500);

                if(Thread.currentThread().getName().equals("P1")){
                    // send
                    sendTo("Hello", 1);
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
