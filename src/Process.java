import com.google.common.eventbus.Subscribe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class Process  implements Runnable, Lamport {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int clock = 0;
    private ArrayList<Object> broadcastData = new ArrayList<>();
    private Com com;

    public Process(String name){

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;
        this.thread.start();

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

                /*broadcastData.clear();
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
                    if((int)broadcastData.get(i) > max)
                    {
                        max = (int)broadcastData.get(i);
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

                synchronize();*/

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
        com.request();
        // TODO
    }

    @Override
    public void unlockClock() {
        com.release();
        // TODO
    }
}
