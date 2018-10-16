import com.google.common.eventbus.Subscribe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class Process  implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private int clock = 0;
    private String stateToken = "null";
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int ack = 0;
    private ArrayList<Object> broadcastData = new ArrayList<>();

    public Process(String name){

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();

    }

    @Subscribe
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
    }



    public void broadcast(Object o)
    {
        this.clock++;
        //System.out.println(this.thread.getName() + " clock : " + this.clock);
        AbstractMessage m1 = new BroadcastMessage(o,this.clock, Thread.currentThread().getName());
        System.out.println(this.thread.getName() + " send in broadcast: " + m1.getPayload()+ ", with clock at " + this.clock);
        bus.postEvent(m1);
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


    public void sendTo(Object o, int to) {
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
        ack = 0;

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
    }

    @Subscribe
    public void onSynchronize(MessageSynchro m) {
        System.out.println(this.thread.getName() + " receives synchro message from P" + (m.getFrom()+1));
        this.clock = Math.max(this.clock, m.getClock());
        this.clock++;
        //System.out.println("New clock (" + this.thread.getName()+"): " + this.clock);

        // received ACK
        ack++;
    }


    public void run(){

        System.out.println(this.thread.getName() + " id: " + this.id);

        if(this.id == Process.nbProcess-1){
            Token t = new Token(this.id);
            bus.postEvent(t);
            System.out.println("Token thrown");
        }

        while(this.alive){
            try{
                /*– Attend quelques secondes que tous les autres soient lancés
                – Lance un dé n faces (n est laissé à votre choix)
                – Diffuse le résultat
                – Celui qui a tiré la plus grande valeur demande une section critique pour écrire son numéro dans un fichier
                – Tous les Process se synchronisent avant de relancer le dé.*/

                broadcastData.clear();
                Thread.sleep(500);
                int die = throwDie(6);
                System.out.println(this.thread.getName() + " : " + die);
                broadcastData.add(die);
                broadcast(die);

                System.out.println(broadcastData);

                synchronize();

                int max = 0;
                for(int i = 0; i< broadcastData.size()-1 ;i++)
                {
                    if((int)broadcastData.get(i) > (int)broadcastData.get(i+1))
                    {
                        max = (int)broadcastData.get(i);
                    }
                    else
                    {
                        max = (int)broadcastData.get(i+1);
                    }
                    System.out.println("Max " + max);
                }

                System.out.println(broadcastData);

                if(max == die) {

                    System.out.println(this.thread.getName() + " : J'ai gagné");

                    request();

                    // écrire dans fichier
                    String result = die + " \n";
                    File file = new File("resultats.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(result);

                    bw.close();
                    fw.close();

                    release();
                }

                synchronize();

            }catch(Exception e){

                e.printStackTrace();
            }
        }

        // liberation du bus
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
        System.out.println(this.thread.getName() + " stopped");
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

    public int throwDie(int n)
    {
        return (int)(Math.random() * n + 1);
    }
}
