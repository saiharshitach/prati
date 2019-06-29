package rs.cybertrade.prati;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import com.github.appreciated.app.layout.behaviour.AppLayoutComponent;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.Query;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.SistemSesije;
import rs.cybertrade.prati.Broadcaster.BroadcastListener;
import rs.cybertrade.prati.meni.PratiEventBus;
import rs.cybertrade.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.cybertrade.prati.meni.PratiEvent.KorisnikLoginRequestedEvent;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.PracenjeView;

@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@PreserveOnRefresh // zadržava poziciju kada se uradi refresh strane
@Push
@Title("Праћење")
public class Prati extends UI implements BroadcastListener{

	private static final long serialVersionUID = 1L;
	private final PratiEventBus pratiEventBus = new PratiEventBus();
	private Korisnici korisnik;
	public PollListener osvezavanjeMarkera; 
	public static final String DANSATFORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS";
	
	public SistemPretplatnici pretplatnik;
	public Organizacije organizacija;
	public Grupe grupa;
	public ArrayList<Objekti> objekti = new ArrayList<Objekti>();
	public ArrayList<Objekti> sviObjekti = new ArrayList<Objekti>();
	public Grid<JavljanjaPoslednja> poslednjaJavljanja;
	public Grid<Javljanja> javljanjaAlarmi;
	private ArrayList<Javljanja> javljanjaAlarmiNiz;
	public boolean centriranje = true;
	public PracenjeView pracenjeView;
	public static SistemSesije sesija;
	private static String ip;
	private static final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		PratiEventBus.register(this);
		addStyleName("v-font");

		poslednjaJavljanja = new Grid<JavljanjaPoslednja>();
		poslednjaJavljanja.setSizeFull();
		poslednjaJavljanja.addStyleName(ValoTheme.TABLE_BORDERLESS);
		poslednjaJavljanja.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		poslednjaJavljanja.addStyleName(ValoTheme.TABLE_COMPACT);
		poslednjaJavljanja.setSelectionMode(SelectionMode.MULTI);
		poslednjaJavljanja.addColumn(javljanjaPoslednja -> vratiBoju(javljanjaPoslednja), new HtmlRenderer()).setCaption("стаус");;
		poslednjaJavljanja.addColumn(javljanjaPoslednja -> javljanjaPoslednja.getObjekti().getOznaka()).setCaption("објект");
		poslednjaJavljanja.addColumn(JavljanjaPoslednja::getBrzina).setCaption("брзина").setStyleGenerator(javljanjaPoslednja -> "v-align-right");
		poslednjaJavljanja.addColumn(JavljanjaPoslednja::getDatumVreme, new DateRenderer(DANSATFORMAT)).setId("datumVreme").setCaption("датум/време").setStyleGenerator(javljanjaPoslednja -> "v-align-right");
		poslednjaJavljanja.setSortOrder(GridSortOrder.desc(poslednjaJavljanja.getColumn("datumVreme")));
		
		javljanjaAlarmiNiz = new ArrayList<Javljanja>();
		javljanjaAlarmi = new Grid<Javljanja>();
		javljanjaAlarmi.setSizeFull();
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_BORDERLESS);
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_COMPACT);
		javljanjaAlarmi.setSelectionMode(SelectionMode.SINGLE);
		javljanjaAlarmi.addColumn(Javljanja::getDatumVreme,new DateRenderer(DANSATFORMAT)).setId("datumVreme").setCaption("датум/време").setStyleGenerator(uredjaji -> "v-align-left");
		javljanjaAlarmi.addColumn(javljanja -> javljanja.getObjekti().getOznaka()).setCaption("објект");
		javljanjaAlarmi.addColumn(javljanja -> javljanja.getSistemAlarmi().getNaziv()).setCaption("аларм");
		javljanjaAlarmi.addColumn(Javljanja::getEventData).setCaption("опис");
		
		updateContent();
		Broadcaster.register(this);
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
			getNavigator().navigateTo("pracenje");

		}else {
			setContent(new PrijavaView());
			addStyleName("loginview");
		}
	}

	@Subscribe
	public void korisnikLoginRequested(final KorisnikLoginRequestedEvent event) {
		Korisnici korisnik = Servis.korisnikServis.nadjiKorisnikaPoKorisnickom(event.getKorisnicko(), event.getLozinka());
		boolean prijavi = true;
		if(korisnik == null) {
			prijavi = false;
			}else {
				if(korisnik.isIzbrisan()) {
					prijavi = false;
				}
			if(!korisnik.isAktivan()) {
				prijavi = false;
				}
			if(!korisnik.isKorisnik()) {
				prijavi = false;
				}
			//ubaciti kontrolu za vreme korisnika ako je podešeno
			if(korisnik.getSistemPretplatnici() != null) {
				if(korisnik.getSistemPretplatnici().isIzbrisan()) {
					prijavi = false;
					}
				if(!korisnik.getSistemPretplatnici().isAktivan()) {
					prijavi = false;
					}
				}//ubaciti kontrolu za vreme za pretplatnika ako je podešeno
			if(korisnik.getOrganizacija() != null) {
				if(korisnik.getOrganizacija().isIzbrisan()) {
					prijavi = false;
				}
				if(!korisnik.getOrganizacija().isAktivan()) {
					prijavi = false;
					}
				}
			}
		if(prijavi) {
			VaadinSession.getCurrent().setAttribute(Korisnici.class.getName(), korisnik);
			Prati.sesija.setDatumPocetak(new Timestamp((new Date()).getTime()));
			Prati.sesija.setSistemPretplatnici(korisnik.getSistemPretplatnici());
			Prati.sesija.setOrganizacija(korisnik.getOrganizacija());
			Prati.sesija.setKorisnici(korisnik);
			Servis.sistemSesijaServis.izmeniSesiju(sesija);
			if(korisnik.isAdmin()) {
				sviObjekti = Servis.objekatServis.vratiSveObjekte(korisnik, true);
			}else {
				ArrayList<Grupe> grupe = Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik);
				sviObjekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
			}
			
		}else {
			showNotification(new Notification("Пријава није успела, покушајте поново или контактирајте администратора!", Notification.Type.HUMANIZED_MESSAGE));
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
	//da li je sirina browser-a veća od 768
	public Boolean sirina(){
		return (getPage().getBrowserWindowWidth() >= 700);
	}
	
    @WebServlet(urlPatterns = "/*", name = "PratiServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Prati.class, productionMode = true)
    public static class PratiServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener{
		private static final long serialVersionUID = 1L;

		@Override
	    protected void servletInitialized() throws ServletException {
	        super.servletInitialized();
	        getService().addSessionInitListener(this);
	        getService().addSessionDestroyListener(this);
	    }
		
		@Override
		public void sessionInit(SessionInitEvent event) throws ServiceException {
			//System.out.println("sesija počela...");
			sesija = new SistemSesije();
			//ip = VaadinService.getCurrentRequest().getHeader("x-forwarded-for");
			ip = getClientIp(request);
			sesija.setIpAdresa(ip);
			//sesija.setIpAdresa(Prati.getCurrent().getSession().getBrowser().getAddress());
			Servis.sistemSesijaServis.unesiSesiju(sesija);
		}

		@Override
		public void sessionDestroy(SessionDestroyEvent event) {
			//System.out.println("sesija kraj...");
			sesija.setDatumKraj(new Timestamp((new Date()).getTime()));
			Servis.sistemSesijaServis.izmeniSesiju(sesija);
		}
    }
    
	private static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
    
    @Override
    public void detach() {
		try {
			Broadcaster.unregister(this);
			super.detach();
		}catch(Exception e) {
			
		}
    }

    @Override
	public void receiveBroadcast(final Javljanja message) {
		try {
			this.access(new Runnable() {
				@Override
				public void run() {
					//System.out.println("prolaz..." + message.getDatumVreme());
					if(sadrziObjekat(sviObjekti, message.getObjekti().getId())) {
						if(message.getSistemAlarmi() != null && message.getSistemAlarmi().isPrikaz()) {
							if(javljanjaAlarmiNiz.size() > 99) {
								javljanjaAlarmiNiz.remove(javljanjaAlarmiNiz.get(0));
							}
								javljanjaAlarmiNiz.add(message);
								javljanjaAlarmi.setItems(javljanjaAlarmiNiz);
								javljanjaAlarmi.setSortOrder(GridSortOrder.desc(javljanjaAlarmi.getColumn("datumVreme")));
						}
						if(pracenjeView != null) {
							for(JavljanjaPoslednja javljanje : poslednjaJavljanja.getDataProvider().fetch(new Query<>()).collect(Collectors.toList())) {
								if(message.getObjekti().getId().equals(javljanje.getObjekti().getId())) {
									javljanje.setDatumVreme(message.getDatumVreme());
									javljanje.setBrzina(message.getBrzina());
									javljanje.setKontakt(message.isKontakt());
									javljanje.setLat(message.getLat());
									javljanje.setLon(message.getLon());
									javljanje.setPravac(message.getPravac());
									poslednjaJavljanja.getDataProvider().refreshAll();
									break;
								}
							}
						}
					}
				}
			});	
		}catch (NullPointerException e) {
			// TODO: handle exception
		}
	}
    
    public boolean sadrziObjekat(ArrayList<Objekti> list, Long id){
    	return list.stream().filter(o -> o.getId().equals(id)).findFirst().isPresent();
    }
    
	private String vratiBoju(JavljanjaPoslednja javljanjePoslednje) {
		boolean kontakt = javljanjePoslednje.isKontakt();
		int brzina = javljanjePoslednje.getBrzina();
		String boja = "#000000";
		String ikonicaBoja = "";
		if(kontakt && brzina >= 5) {
			boja = "#2dd085";
			}else if(kontakt && brzina < 5) {
				boja = "#ffc66e";
				}else {
					boja = "#f54993";
					}
		ikonicaBoja =  "<div class=\"v-icon\" style=\"font-family: " + VaadinIcons.CIRCLE.getFontFamily() + ";color:" + boja + ";vertical-align:middle" + "\">&#x" 
					+ Integer.toHexString(VaadinIcons.CIRCLE.getCodepoint()) + ";</div>";
		return ikonicaBoja;
	}
}
