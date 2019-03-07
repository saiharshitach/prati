package rs.cybertrade.prati;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pratiBaza.tabele.Javljanja;

public class Broadcaster implements Serializable {
    /**
	 * slanje poruka
	 */
	private static final long serialVersionUID = 1L;
	static ExecutorService execService = Executors.newSingleThreadExecutor();
	
    public interface BroadcastListener {
        void receiveBroadcast(Javljanja message);
    }

    private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

    public static synchronized void register(BroadcastListener listener) {
    	listeners.add(listener);
    	}
    
    public static synchronized void unregister(BroadcastListener listener) {
        listeners.remove(listener);
        }

    public static synchronized void broadcast(final Javljanja message) {
        for (final BroadcastListener listener: listeners){
        	execService.submit(new Runnable() {
        		@Override
                public void run() {
                    listener.receiveBroadcast(message);
                }
            });
        };
    }
}
