package rs.atekom.prati.meni;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import rs.atekom.prati.Prati;

public class PratiEventBus implements SubscriberExceptionHandler{
	
	private final EventBus eventBus = new EventBus(this);
	
	public static void post(final Object event) {
		Prati.getPratiEventBus().eventBus.post(event);
	}
	
	public static void register(final Object object) {
		Prati.getPratiEventBus().eventBus.register(object);
	}
	
	public static void unregister(final Object object) {
		Prati.getPratiEventBus().eventBus.unregister(object);
	}
	
	@Override
	public void handleException(Throwable exception, SubscriberExceptionContext context) {
		exception.printStackTrace();
	}

}
