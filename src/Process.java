import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

/**
 * Processes playing a game of dice with each other
 * Class mostly used to test operations of Com
 */
public class Process implements Runnable, Lamport {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private int clock = 0;
    private Semaphore semaphore;
    private Com com;

    /**
     * Constructor for a Process
     * @param name The name of the process being created
     */
    public Process(String name){

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.semaphore = new Semaphore(1);
        this.com = new Com(this);
        this.thread.start();
    }

    /**
     * Example to test the different operations
     * Processes throw dice and write the best result in a file
     */
    public void run(){

        System.out.println(this.thread.getName() + " id: " + this.com.getId());

        // to gather the results of all the dice thrown by processes
        ArrayList<Integer> broadcastData = new ArrayList<>();

        // launch the token to manage critical sections
        com.startToken();

        while(this.alive){
            try{

                // nouveau lancé de dé
                /*broadcastData.clear();
                Thread.sleep(500);
                int die = throwDie(6); // dé à 6 faces
                System.out.println(this.thread.getName() + " : " + die);
                broadcastData.add(die);
                // diffusion du résultat aux autres processus
                com.broadcast(die);

                // chaque processus attend de tout recevoir
                while(com.checkMailBoxSize() < com.getNbProcess()-1)
                {
                    Thread.sleep(50);
                }

                // récupère tous les résultats de dés reçus
                for (int i=0; i<com.getNbProcess()-1; i++)
                {
                    broadcastData.add((int)com.readNextMail());
                }

                System.out.println(com.getId() + " " + broadcastData);

                // détermine le meilleur résultat
                int max = broadcastData.stream().max(Comparator.comparing(Integer::valueOf)).get();

                // si ce processus détient le meilleur résultat
                if(max == die) {

                    System.out.println(this.thread.getName() + " : J'ai gagné (" + die + ")");

                    // il sera le seul à avoir accès au fichier
                    com.request();

                    // écrire dans fichier
                    String result = die + " \n";
                    File file = new File("resultats.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    // écrit le résultat du dé à la suite du fichier
                    bw.write(result);

                    bw.close();
                    fw.close();

                    // un autre processus pourra avoir accès au fichier
                    com.release();
                }

                // tous les processus doivent finir avant de relancer les dés
                com.synchronize();*/


                Thread.sleep(50);

                if(com.getId() == 0){

                    com.broadcastSync("hello");

                }

                if(com.getId()==1 || com.getId() == 2)
                {

                    while(com.checkBroadcastSyncMailBoxSize() == 0 && this.alive)
                    {
                        Thread.sleep(50);
                    }

                    System.out.println((String)com.readNextBoadcastMailSync());
                }

                /*Thread.sleep(50);

                if(com.getId() == 0){

                    com.sendToSync("hello",1);
                    System.out.println("send done");

                }

                if(com.getId()==1 )
                {

                    while(com.checkMailSyncBoxSize() == 0 && this.alive)
                    {
                        Thread.sleep(50);
                    }

                    System.out.println((String)com.readNextBoadcastMailSync());
                }*/




            }catch(Exception e){

                e.printStackTrace();
            }
        }

        // retirer du bus puisque le processus va cesser d'exister
        this.com.unregister();
        this.dead = true;
        //com.sendHeartbit(this.alive);
        System.out.println(this.com.getId() + " is dead");

    }

    /**
     * active wait until the process change its 'dead' attribute to false,
     * to avoid stopping the program before the process is stopped
     */
    public void waitStopped(){
        while(!this.dead){
            try{
                Thread.sleep(500);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to ask the process to stop executing itself after the end of the current loop
     */
    public void stop(){
        this.alive = false;
    }

    /**
     * Simulate a die throw, by generating a random number between 1 and n included
     * @param n The max value you can get from the die
     * @return The result of the throw
     */
    public int throwDie(int n)
    {
        return (int)(Math.random() * n + 1);
    }

    /**
     * Returns the value of the clock attribute
     * @return clock
     */
    @Override
    public int getClock() {
        return clock;
    }

    /**
     * Set the value of the clock attribute
     * @param clock
     */
    @Override
    public void setClock(int clock) {
        this.clock = clock;
    }

    /**
     * To be used when accessing the clock, to avoid issues with the critical section
     */
    @Override
    public void lockClock() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * To be used after lockClock(), so the clock can be accessed by another
     */
    @Override
    public void unlockClock() {
        semaphore.release();
    }

    /**
     * Returns the value of the thread attribute
     * @return thread
     */
    public Thread getThread() {
        return thread;
    }
}
