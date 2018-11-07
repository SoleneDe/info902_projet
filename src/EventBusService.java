
import java.util.concurrent.Executors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

/**
 * Bus interface service, it use guava API
 */
public class EventBusService {

    /**
     * Service singleton instance
     */
    private static EventBusService instance = null;

    /**
     * Guava bus api event object
     */
    private EventBus eventBus = null;

    /**
     * Constructor for a service
     */
    private EventBusService() {
        eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
    }

    /**
     * Returns service instance
     * @return instance
     */
    public static EventBusService getInstance() {
        if (instance==null){
            instance = new EventBusService();
        }
        return instance;
    }

    /**
     * Subscribe process on the bus
     * @param subscriber process to subscribe
     */
    public void registerSubscriber(Object subscriber) {
        eventBus.register(subscriber);
    }

    /**
     * Unsubscribe process on the bus
     * @param subscriber process to unsubscribe
     */
    public void unRegisterSubscriber(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    /**
     * Post a message on the bus
     * @param e object
     */
    public void postEvent(Object e) {
        try{
            eventBus.post(e);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
