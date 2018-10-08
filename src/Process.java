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

    // Declaration de la methode de callback invoquee lorsqu'un message de type Bidule transite sur le bus
    @Subscribe
    public void onTrucSurBus(Bidule b){
        System.out.println(Thread.currentThread().getName() + " receives: " + b.getMachin() + " for " + this.thread.getName());
        if(b.getClock() > this.clock)
        {
            this.clock = b.getClock()+1;
        }
        else
        {
            this.clock++;
        }
    }

    public void run(){


        System.out.println(Thread.currentThread().getName() + " id :" + this.id);

        while(this.alive){
            System.out.println(Thread.currentThread().getName() + " clock : " + this.clock);
            try{
                Thread.sleep(500);

                if(Thread.currentThread().getName().equals("P1")){
                    Bidule b1 = new Bidule("ga",this.clock);
                    //Bidule b2 = new Bidule("bu",this.clock);
                    System.out.println(Thread.currentThread().getName() + " send : " + b1.getMachin());
                    bus.postEvent(b1);
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            this.clock++;
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
}
