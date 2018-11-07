import com.google.common.eventbus.Subscribe;

import java.util.*;

/**
 * Class taking care of the messages in the bus
 * It's a middleware to supply the guava library
 * A Lamport object can check its messages through mailboxes
 */
public class Com {

    /**
     * Medium to make communication between process
     */
    private EventBusService bus;
    /**
     * Token state: it can take value like "null", "request", "sc", "release"
     */
    private String stateToken = "null";
    /**
     * Acknowledgment variable for a synchonization barrier
     */
    private int ackSynchronize = 0;
    /**
     * Acknowledgment variable for a one to one communication
     */
    private boolean ackSendToSync = false;
    /**
     * Acknowledgment variable for a synchronous broadcast
     */
    private int ackBroadcastSync = 0;
    /**
     * Mailbox for asynchronous messages
     */
    private Queue<Object> mails; // the mailbox where messages are stored
    /**
     * Mailbox for synchronous one to one communication messages
     */
    private Queue<Object> mailsSync;
    /**
     * Mailbox for synchronous broadcast messages
     */
    private Queue<Object> broadcastMailsSync;
    /**
     * Process with a lamport clock
     */
    private Lamport p;
    /**
     * Process's id
     */
    private int id;
    /**
     * Alive process number
     */
    private static int nbProcess = 0;

    /**
     * Constructor for Com
     * @param p The Lamport process object which needs to access the bus
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
     * Unregistration demand from attributed process when it stops, it should unregister itself from the bus
     */
    public void unregister()
    {
        this.bus.unRegisterSubscriber(this);
    }

    /**
     * Read next mail, and remove it from the asynchronous message mailbox
     * @return Object
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
     * Read next mail, and remove it from the synchronous one to one communication message mailbox
     * @return Object
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
     * Read next mail, and remove it from the synchronous broadcast message mailbox
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
     * Returns the size of the asynchronous message mailbox
     * @return The number of messages in the mailbox
     */
    public int checkMailBoxSize()
    {
        return mails.size();
    }

    /**
     * Returns the size of the synchronous one to one communication message mailbox
     * @return The number of messages in the mailbox
     */
    public int checkMailSyncBoxSize()
    {
        return mailsSync.size();
    }

    /**
     * Returns the size of the synchronous broadcast message mailbox
     * @return The number of messages in the mailbox
     */
    public int checkBroadcastSyncMailBoxSize()
    {
        return broadcastMailsSync.size();
    }




    /**
     * Give a id from a process demand
     * @return process id
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

        if (t.getId() == this.id) {

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
                t.setId((this.id + 1) % Com.nbProcess);
                //if ( !this.dead ) {
                    bus.postEvent(t);
                //}
                stateToken = "null";

            }else{

                t.setId((this.id + 1) % Com.nbProcess);
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
     * the last Process created will get the token, to start the cycle
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
     * @param o Data to be sent
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
     * Receive a message from a broadcast, added as mail synchronously
     * @param m The message to read
     */
    @Subscribe
    public void recevBroadcastSync(BroadcastSyncMessage m) {
        if (this.id != m.getSenderId()) { // the current process is the destination
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


            sendBroadcastAckTo(m.getSenderId());

        }
    }

    /**
     * Send a acknowledgment for synchronous broadcast to the sender
     * @param to sender's id
     */
    private void sendBroadcastAckTo( int to)
    {
        System.out.println(this.id + " sends ack");
        AckBroadcastMessage m = new AckBroadcastMessage(to);
        bus.postEvent(m);
    }

    /**
     * Receive the acknowledgment from a synchronous broadcast
     * @param m ack
     */
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
     * @param to recipient's id
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

            //System.out.println(this.id + " receives in one to one: " + m.getId());
            mails.add(m.getPayload());

            p.setClock(Math.max(p.getClock(), m.getClock()));
            p.setClock(p.getClock() + 1);

            p.unlockClock();
        }
    }


    /**
     * Send synchronously an object to a specific process
     * @param o Data to be sent
     * @param to recipient's id
     */
    public void sendToSync(Object o, int to) {
        p.lockClock();
        p.setClock(p.getClock() + 1);
        p.unlockClock();

        System.out.println(this.id + " send [" + o + "] to [" + to + "], with clock at " + p.getClock());
        SendToSyncMessage m = new SendToSyncMessage(o, p.getClock(), to, this.id);

        bus.postEvent(m);

        while(!ackSendToSync){


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

                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            ackSendToSync = true;
            sendAckTo(m.getIdOrigin());

        }
    }

    /**
     * Send acknowledgment for a one to one synchronous communication
     * @param to recipient's id
     */
    private void sendAckTo( int to)
    {
        System.out.println(this.id + "sends ack");
        AckSendToMessage m = new AckSendToMessage(to);
        bus.postEvent(m);
    }

    /**
     * Receive the acknowledgment for a one to one synchronous communication
     * @param m ack
     */
    @Subscribe
    public void receiveAck(AckSendToMessage m)
    {

        if (this.id == m.getTo()){

            System.out.println(this.id + "receives ack");
            ackSendToSync = true;
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



}
