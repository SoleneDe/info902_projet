import com.google.common.eventbus.Subscribe;

import java.util.*;

/**
 * Class taking care of the messages in the bus
 * A Lamport object can check its messages through the mailbox
 */
public class Com {

    private EventBusService bus;
    private String stateToken = "null";
    private int ackSynchronize = 0; // for the synchronization barrier
    private boolean ackSendToSync = false;
    private int ackBroadcastSync = 0;
    private Queue<Object> mails; // the mailbox where messages are stored
    private Queue<Object> mailsSync;
    private Queue<Object> broadcastMailsSync;
    //Process
    private Lamport p;
    private int id;

    private static int nbProcess = 0;

    /**
     * Constructor for Com
     * @param p The Lamport object that needs to access the bus
     */
    public Com(Lamport p)
    {
        this.id = Com.nbProcess++;
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.p = p;
        mails = new LinkedList<>();
        mailsSync = new LinkedList<>();
        broadcastMailsSync = new LinkedList<>();
    }

    /**
     * When attribute p stops, it should unregister itself from the bus
     */
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
     * Take next mail, and remove it
     * @return mail
     */
    public Object readNextMailSync()
    {
        if(mailsSync.isEmpty())
        {
            return null;
        }
        return mailsSync.remove();
    }

    /**
     * Take next mail, and remove it
     * @return mail
     */
    public Object readNextBoadcastMailSync()
    {
        if(broadcastMailsSync.isEmpty())
        {
            return null;
        }
        return broadcastMailsSync.remove();
    }


    /**
     * Returns the size of the map mails, representing the mailbox
     * @return The number of messages in the mailbox
     */
    public int checkMailBoxSize()
    {
        return mails.size();
    }


    public int checkMailSyncBoxSize()
    {
        return mailsSync.size();
    }


    public int checkBroadcastSyncMailBoxSize()
    {
        return broadcastMailsSync.size();
    }




    /**
     * Give a id from a process demands
     * @return (int) process id
     */
    public int getId(){
        return this.id;
    }

    /**
     * Send a signal on the bus to tell others that it's alive
     * TODO still an early version
     * @param isAlive Boolean to tell if it's still running or stopping
     */
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

    /**
     * Read a signal from another to tell its alive
     * TODO still an early version
     * @param m The message to read
     */
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

    /**
     * Change its own ID when another is dead
     * @param id The ID of the entity that died
     */
    private void changeIdAfterDeath(int id) {
        if (this.id > id)
        {
            this.id--;
        }
    }

    /**
     * Returns the total number of prcesses
     * @return nbProcess
     */
    public int getNbProcess()
    {
        return Com.nbProcess;
    }

    /**
     * Describes how to handle the token (to deal with critical sections),
     * depending on the attribute stateToken
     * @param t The message to read
     */
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

    /**
     * Should be called when a critical section is starting, you get the token
     * Don't forget to use release() afterwards
     */
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

    /**
     * Should be used when a critical section is ending, to give the token to the next one
     * Use request() beforehand, to get the token
     */
    public void release(){
        stateToken = "release";

        System.out.println(this.id + " releases sc");
    }

    /**
     * Should be used at the very start,
     * the last Com created will get the token, to start the cycle
     */
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
     * @param o Data to send
     */
    public void broadcast(Object o)
    {
        p.lockClock();
        p.setClock(p.getClock() + 1);

        BroadcastMessage m = new BroadcastMessage(o, p.getClock(), this.id);
        System.out.println(this.id + " broadcasts: " + m.getPayload()+ ", with clock at " + p.getClock());

        bus.postEvent(m);

        p.unlockClock();
    }



    /**
     * Receive a message from a broadcast, added as mail
     * @param m The message to read
     */
    @Subscribe
    public void onBroadcast(BroadcastMessage m){
        //receive
        if(m.getSender() != this.id){
            p.lockClock();

            System.out.println(this.id + " receives in broadcast: " + m.getPayload());
            mails.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();
        }
    }

    /**
     * Broadcast synchronously an object
     * @param o Data to be sent
     */
    public void broadcastSync(Object o) {
        p.lockClock();
        p.setClock(p.getClock() + 1);
        p.unlockClock();

        System.out.println(this.id + " send [" + o + "], with clock at " + p.getClock());
        BroadcastSyncMessage m = new BroadcastSyncMessage(o, p.getClock(), this.id);

        bus.postEvent(m);

        //int nbPro = Com.nbProcess;
        while(ackBroadcastSync < Com.nbProcess-1)
        {
            System.out.println("aie alone");
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }

            /*if(nbPro != Com.nbProcess)
            {
                System.out.println("break");
                break;
            }*/
        }
        System.out.println("[" + this.id + "] ackBroadcastSync received, with clock=" + p.getClock());
        ackBroadcastSync -= Com.nbProcess-1;

    }


    /**
     * Receive a message from a broadcast communication, added as mail synchronously
     * @param m The message to read
     */
    @Subscribe
    public void recevBroadcastSync(BroadcastSyncMessage m) {
        if (this.id != m.getIdOrigin()) { // the current process is the destination
            p.lockClock();

            System.out.println(this.id + " receives in broadcast: " + m.getPayload());
            broadcastMailsSync.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();

            while(!broadcastMailsSync.isEmpty()){

                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            sendBroadcastAckTo(m.getIdOrigin());

        }
    }


    private void sendBroadcastAckTo( int to)
    {
        System.out.println(this.id + " sends ack");
        AckBroadcastMessage m = new AckBroadcastMessage(to);
        bus.postEvent(m);
    }

    @Subscribe
    public void receiveAck(AckBroadcastMessage m)
    {

        if (this.id == m.getTo()){

            System.out.println(this.id + " receives broadcast ack");
            ackBroadcastSync++;
        }
    }

    /**
     * Send an object to a specific process
     * @param o Data to be sent
     * @param to ID of the destination
     */
    public void sendTo(Object o, int to) {
        p.lockClock();
        p.setClock(p.getClock() + 1);

        //System.out.println(this.id + " send [" + o + "] to [" + to + "], with clock at " + p.getClock());
        MessageTo m = new MessageTo(o, p.getClock(), to);

        bus.postEvent(m);

        p.unlockClock();
    }

    /**
     * Receive a message from a one-to-one communication, added as mail
     * @param m The message to read
     */
    @Subscribe
    public void onReceive(MessageTo m) {
        if (this.id == m.getIdDest()) { // the current process is the destination
            p.lockClock();

            //System.out.println(this.id + " receives in one to one: " + m.getPayload());
            mails.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();
        }
    }


    /**
     * Send synchronously an object to a specific process
     * @param o Data to be sent
     * @param to ID of the destination
     */
    public void sendToSync(Object o, int to) {
        p.lockClock();
        p.setClock(p.getClock() + 1);
        p.unlockClock();

        System.out.println(this.id + " send [" + o + "] to [" + to + "], with clock at " + p.getClock());
        SendToSyncMessage m = new SendToSyncMessage(o, p.getClock(), to, this.id);

        bus.postEvent(m);

        while(!ackSendToSync){

            System.out.println("sendto loop");
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("[" + this.id + "] ackSendToSync received, with clock=" + p.getClock());
        ackSendToSync = false;

    }

    /**
     * Receive a message from a one-to-one communication, added as mail synchronously
     * @param m The message to read
     */
    @Subscribe
    public void recevFromSync(SendToSyncMessage m) {
        if (this.id == m.getIdDest()) { // the current process is the destination
            p.lockClock();

            System.out.println(this.id + " receives in one to one: " + m.getPayload());
            mailsSync.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();

            while(!mailsSync.isEmpty()){
                System.out.println("receive loop");
                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            System.out.println("continue");
            ackSendToSync = true;
            sendAckTo(ackSendToSync, m.getIdOrigin());

        }
    }

    private void sendAckTo(boolean ack, int to)
    {
        System.out.println(this.id + "sends ack");
        AckMessage m = new AckMessage(ack,to);
        bus.postEvent(m);
    }

    @Subscribe
    public void receiveAck(AckMessage m)
    {

        if (this.id == m.getTo()){

            System.out.println(this.id + "receives ack");
            ackSendToSync = m.isAck();
        }
    }




    /**
     * Send a sync message and wait for every other process to send one before continuing
     */
    public void synchronize() {
        // check receptions
        p.lockClock();
        p.setClock(p.getClock() + 1);

        //System.out.println(this.id + " sends synchronization, with clock at " + p.getClock());
        MessageSynchro m = new MessageSynchro(p.getClock(), this.id);
        p.unlockClock();

        bus.postEvent(m);

        // block until everyone sends an ackSynchronize
        while(ackSynchronize < Com.nbProcess-1)
        {
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("[" + this.id + "] Every ackSynchronize received, with clock=" + p.getClock());
        ackSynchronize -= Com.nbProcess-1;
    }

    /**
     * Receive messages to synchronize all processes
     * @param m The message to read
     */
    @Subscribe
    public void onSynchronize(MessageSynchro m) {

        if (m.getFrom() != this.id) {

            //System.out.println(this.id + " receives synchro message from " + m.getFrom());

            p.lockClock();
            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);
            p.unlockClock();

            // received ackSynchronize
            ackSynchronize++;
        }
    }

    // TODO broadcastSync & sendToSync


}
