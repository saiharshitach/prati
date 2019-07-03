package rs.atekom.prati.meni;

import com.vaadin.server.VaadinSession;

public abstract class PratiEvent {

	public static final class KorisnikLoginRequestedEvent{
		private final String korisnicko, lozinka;
		
		public KorisnikLoginRequestedEvent(final String korisnicko, final String lozinka) {
			this.korisnicko = korisnicko;
            this.lozinka = lozinka;
		}
		
        public String getKorisnicko() {
            return korisnicko;
        }

        public String getLozinka() {
            return lozinka;
        }
	}
	
	public static class BrowserResizeEvent {

    }

    public static class KorisnikLoggedOutEvent {
    	public KorisnikLoggedOutEvent(){
    		VaadinSession.getCurrent().close();
    	}
    }

    public static class NotificationsCountUpdatedEvent {
    	
    }

    public static final class ReportsCountUpdatedEvent {
        private final int count;

        public ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }
    
    public static final class PostViewChangeEvent {
    	private final PratiViewType view;
    	
    	public PostViewChangeEvent(final PratiViewType view) {
    		this.view = view;
    	}
    	
    	public PratiViewType getView() {
    		return view;
    	}
    }

    public static class CloseOpenWindowsEvent {
    	
    }

    public static class ProfileUpdatedEvent {
    	
    }
}
