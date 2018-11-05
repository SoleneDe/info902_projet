import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

public class Process  implements Runnable, Lamport {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private int clock = 0;
    private Semaphore semaphore;
    private Com com;

    private ArrayList<Integer> broadcastData;

    public Process(String name){

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.semaphore = new Semaphore(1);
        this.com = new Com(this);
        this.broadcastData = new ArrayList<>();
        this.thread.start();
    }

    public void run(){

        System.out.println(this.thread.getName() + " id: " + this.com.getId());

        com.startToken();

        while(this.alive){
            try{
                /*– Attend quelques secondes que tous les autres soient lancés
                – Lance un dé n faces (n est laissé à votre choix)
                – Diffuse le résultat
                – Celui qui a tiré la plus grande valeur demande une section critique pour écrire son numéro dans un fichier
                – Tous les Process se synchronisent avant de relancer le dé.*/

                // nouveau lancé de dé
                broadcastData.clear();
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
                com.synchronize();

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
                com.synchronize();

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

    public int throwDie(int n)
    {
        return (int)(Math.random() * n + 1);
    }

    @Override
    public int getClock() {
        return clock;
    }

    @Override
    public void setClock(int clock) {
        this.clock = clock;
    }

    @Override
    public void lockClock() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlockClock() {
        semaphore.release();
    }

    public Thread getThread() {
        return thread;
    }
}
