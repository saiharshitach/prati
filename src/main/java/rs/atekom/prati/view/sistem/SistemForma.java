package rs.atekom.prati.view.sistem;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Sistem;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.Tekst;

public class SistemForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private SistemLogika logika;
	private Tekst vlasnik, adresa, telefon, sajt, ePostaVlasnik, serverMape, api, ePostaNalog, ePostaServer, ePostaLozinka, nominatim;
	private Celobrojni ePostaPort;
	private CheckBox koristiServerMape;
	
	public SistemForma(SistemLogika log) {
		logika = log;
		vlasnik = new Tekst("власник", true);
		adresa = new Tekst("адреса", true);
		telefon = new Tekst("телефон", true);
		sajt = new Tekst("сајт", true);
		ePostaVlasnik = new Tekst("е-пошта власника", true);
		serverMape = new Tekst("сервер мапе", true);
		api = new Tekst("апи гугле мапе", true);
		ePostaNalog = new Tekst("е-пошта налог", false);
		ePostaServer = new Tekst("е-пошта сервер", false);
		ePostaLozinka = new Tekst("е-пошта лозинка", false);
		ePostaPort = new Celobrojni("порт", false);
		koristiServerMape = new CheckBox("користи сервер мапе");
		nominatim = new Tekst("номинатим", true);
		
		ukloniCombo();
		
		sacuvaj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(proveraPodataka()) {
					ConfirmDialog.show(logika.view.getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						@Override
						public void onClose(ConfirmDialog dialog) {
							if(dialog.isConfirmed()) {
								logika.sacuvajPodatak(sacuvajPodatak(logika.view.dajIzabraniRed()));
							}
						}
					});
				}else {
					logika.view.pokaziPorukuGreska(OpstiView.OBAVEZNAPOLJA);
				}
			}
		});
		
		otkazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				logika.otkaziPodatak();
			}
		});
		
		izbrisi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(logika.view.getUI(), "Провера", "Избриши изабране податке?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							logika.ukloniPodatak();
						}
					}
				});
			}
		});
		
		layout.addComponent(vlasnik);
		layout.addComponent(adresa);
		layout.addComponent(telefon);
		layout.addComponent(sajt);
		layout.addComponent(ePostaVlasnik);
		layout.addComponent(serverMape);
		layout.addComponent(ePostaNalog);
		layout.addComponent(ePostaLozinka);
		layout.addComponent(ePostaServer);
		layout.addComponent(ePostaPort);
		layout.addComponent(koristiServerMape);
		layout.addComponent(nominatim);
		
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		Sistem sistem;
		ocistiPodatak();
		if(podatak == null) {
			sistem = new Sistem();
		}else {
			sistem = (Sistem)podatak;
			postaviPodatak(sistem);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Sistem sistem;
		if(podatak == null) {
			sistem = new Sistem();
		}else {
			sistem = (Sistem)podatak;
		}
		sistem.setVlasnik(vlasnik.getValue());
		sistem.setAdresaVlasnika(adresa.getValue());
		sistem.setTelVlasnika(telefon.getValue());
		sistem.setSajtVlasnika(sajt.getValue());
		sistem.setEmailVlasnika(ePostaVlasnik.getValue());
		sistem.setAdresaServeraMape(serverMape.getValue());
		sistem.setApi(api.getValue());
		sistem.setEmailKorisnik(ePostaNalog.getValue());
		sistem.setEmailLozinka(ePostaLozinka.getValue());
		sistem.setEmailServer(ePostaServer.getValue());
		if(ePostaPort.getValue() != null && !ePostaPort.getValue().isEmpty()) {
			sistem.setEmailServerPort(Integer.valueOf(ePostaPort.getValue()));
		}else {
			sistem.setEmailServerPort(0);
		}
		sistem.setServerMape(koristiServerMape.getValue());
		sistem.setNominatimAdresa(nominatim.getValue());
		return sistem;
	}

	@Override
	public void ocistiPodatak() {
		vlasnik.clear();
		adresa.clear();
		telefon.clear();
		sajt.clear();
		ePostaVlasnik.clear();
		serverMape.clear();
		api.clear();
		ePostaNalog.clear();
		ePostaLozinka.clear();
		ePostaServer.clear();
		ePostaPort.clear();
		koristiServerMape.setValue(false);
		nominatim.clear();
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Sistem sistem = (Sistem)podatak;
		if(sistem.getId() != null) {
			try {
				vlasnik.setValue(sistem.getVlasnik());
			}catch (Exception e) {
				vlasnik.setValue("");
			}
			try {
				adresa.setValue(sistem.getAdresaVlasnika());
			}catch (Exception e) {
				adresa.setValue("");
			}
			try {
				telefon.setValue(sistem.getTelVlasnika());
			}catch (Exception e) {
				telefon.setValue("");
			}
			try {
				sajt.setValue(sistem.getSajtVlasnika());
			}catch (Exception e) {
				sajt.setValue("");
			}
			try {
				ePostaVlasnik.setValue(sistem.getEmailVlasnika());
			}catch (Exception e) {
				ePostaVlasnik.setValue("");
			}
			try {
				serverMape.setValue(sistem.getAdresaServeraMape());
			}catch (Exception e) {
				serverMape.setValue("");
			}
			try {
				api.setValue(sistem.getApi());
			}catch (Exception e) {
				api.setValue("");
			}
			try {
				ePostaNalog.setValue(sistem.getEmailKorisnik());
			}catch (Exception e) {
				ePostaNalog.setValue("");
			}
			try {
				ePostaLozinka.setValue(sistem.getEmailLozinka());
			}catch (Exception e) {
				ePostaLozinka.setValue("");
			}
			try {
				ePostaServer.setValue(sistem.getEmailServer());
			}catch (Exception e) {
				ePostaServer.setValue("");
			}
			try {
				ePostaPort.setValue(String.valueOf(sistem.getEmailServerPort()));
			}catch (Exception e) {
				ePostaPort.setValue("");
			}
			try {
				koristiServerMape.setValue(sistem.isServerMape());
			}catch (Exception e) {
				koristiServerMape.setValue(false);
			}
			try {
				nominatim.setValue(sistem.getNominatimAdresa());
			}catch (Exception e) {
				nominatim.setValue("");
			}
		}else {
			ocistiPodatak();
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(vlasnik.getValue() == null || vlasnik.getValue().isEmpty()) {
			sveIma = false;
		}
		if(adresa.getValue() == null || adresa.getValue().isEmpty()) {
			sveIma = false;
		}
		if(telefon.getValue() == null || telefon.getValue().isEmpty()) {
			sveIma = false;
		}
		if(sajt.getValue() == null || sajt.getValue().isEmpty()) {
			sveIma = false;
		}
		if(ePostaVlasnik.getValue() == null || ePostaVlasnik.getValue().isEmpty()) {
			sveIma = false;
		}
		if(api.getValue() == null || api.getValue().isEmpty()) {
			sveIma = false;
		}
		if(nominatim.getValue() == null || nominatim.getValue().isEmpty()) {
			sveIma = false;
		}
		return sveIma;
	}

}
