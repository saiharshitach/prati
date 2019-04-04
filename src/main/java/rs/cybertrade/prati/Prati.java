package rs.cybertrade.prati;

import javax.servlet.annotation.WebServlet;
import com.github.appreciated.app.layout.behaviour.AppLayoutComponent;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Korisnici;
import rs.cybertrade.prati.Broadcaster.BroadcastListener;
import rs.cybertrade.prati.meni.PratiEventBus;
import rs.cybertrade.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.cybertrade.prati.meni.PratiEvent.KorisnikLoginRequestedEvent;

@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@PreserveOnRefresh // zadržava poziciju kada se uradi refresh strane
@Push
@Title("Праћење")
public class Prati extends UI implements BroadcastListener{

	private static final long serialVersionUID = 1L;
	private final PratiEventBus pratiEventBus = new PratiEventBus();
	private Korisnici korisnik;

	@Override
    protected void init(VaadinRequest vaadinRequest) {
		PratiEventBus.register(this);
		//Responsive.makeResponsive(this);
		//addStyleName(ValoTheme.UI_WITH_MENU);
		addStyleName("v-font");
		updateContent();
    }
	
	private void updateContent() {
		korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
		if(korisnik != null) {
			//setContent(new GlavniView());
			//DefaultBadgeHolder badge = new DefaultBadgeHolder();
			PratiMeniKorisnik meniKlasa = new PratiMeniKorisnik();
			AppLayoutComponent meni;
			if(korisnik.getSistemPretplatnici() == null && korisnik.isSistem()) {
				meni = meniKlasa.vratiMeniSistem(korisnik.getIme() + " " + korisnik.getPrezime());
			}else if (korisnik.getSistemPretplatnici() != null && korisnik.isAdmin()){
				meni = meniKlasa.vratiMeniAdministrator(korisnik.getIme() + " " + korisnik.getPrezime());
			}else {
				meni = meniKlasa.vratiMeniKorisnik(korisnik.getIme() + " " + korisnik.getPrezime());
			}
			
		    setContent(meni);
			removeStyleName("loginview");
			getNavigator().navigateTo("");
		}else {
			setContent(new PrijavaView());
			addStyleName("loginview");
		}
	}

	@Subscribe
	public void korisnikLoginRequested(final KorisnikLoginRequestedEvent event) {
		Korisnici korisnik = Servis.korisnikServis.nadjiKorisnikaPoKorisnickom(event.getKorisnicko(), event.getLozinka());
		if(korisnik != null) {
			VaadinSession.getCurrent().setAttribute(Korisnici.class.getName(), korisnik);
		}else {
			showNotification(new Notification("Пријава није успела, покушајте поново!", Notification.Type.HUMANIZED_MESSAGE));
		}
		updateContent();
	}
	
	@Subscribe
	public void userLoggedOut(final KorisnikLoggedOutEvent event) {
		// When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
	}
	
	public void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the mouse, or until clicked
        notification.setDelayMsec(3000);
        notification.show(Page.getCurrent());
    }
	
	public static PratiEventBus getPratiEventBus() {
		return ((Prati)getCurrent()).pratiEventBus;
	}
	
	public static Prati getCurrent() {
		return (Prati)UI.getCurrent();
	}
	
	public Boolean sirina(){
		return (getPage().getBrowserWindowWidth() >= 768);
	}
	
    @WebServlet(urlPatterns = "/*", name = "PratiServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Prati.class, productionMode = true)
    public static class PratiServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
    }
    
    @Override
    public void detach() {
		try {
			super.detach();
		}catch(Exception e) {
			
		}
    }

	@Override
	public void receiveBroadcast(Javljanja message) {
		// TODO Auto-generated method stub
		
	}
}
