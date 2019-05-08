package rs.cybertrade.prati.view.zone;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Zone;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.Celobrojni;
import rs.cybertrade.prati.view.komponente.Decimalni;
import rs.cybertrade.prati.view.komponente.OrganizacijeCombo;
import rs.cybertrade.prati.view.komponente.PretplatniciCombo;
import rs.cybertrade.prati.view.komponente.Tekst;

public class ZoneForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private ZoneLogika logika;
	private PretplatniciCombo pretplatnici;
	private OrganizacijeCombo organizacije;
	private Tekst naziv, opis;
	private Decimalni lon, lat;
	private Celobrojni precnik;
	private CheckBox aktivan, izbrisan;

	public ZoneForma(ZoneLogika log) {
		logika = log;
		pretplatnici = new PretplatniciCombo("претплатник", true, true);
		organizacije = new OrganizacijeCombo(pretplatnici.getValue(), "организација", true, false);
		naziv = new Tekst("назив", true);
		lon = new Decimalni("географска дужина", true);
		lat = new Decimalni("географска ширина", true);
		precnik = new Celobrojni("пречник", true);
		opis = new Tekst("опис", false);
		aktivan = new CheckBox("активан");
		izbrisan = new CheckBox("избрисан");
		
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
		
		if(logika.view.korisnik.isSistem() && logika.view.korisnik.getSistemPretplatnici() == null) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(naziv);
		layout.addComponent(lon);
		layout.addComponent(lat);
		layout.addComponent(precnik);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
		if(logika.view.isAdmin() || logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
		if(logika.view.isAdmin())  {
			layout.addComponent(izbrisan);
		}
		
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		Zone zona;
		if(podatak == null) {
			zona = new Zone();
		}else {
			zona = (Zone)podatak;
			postaviPodatak(zona);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Zone zona;
		if(podatak == null) {
			zona = new Zone();
		}else {
			zona = (Zone)podatak;
		}
		zona.setSistemPretplatnici(pretplatnici.getValue());
		zona.setOrganizacija(organizacije.getValue());
		zona.setNaziv(naziv.getValue());
		zona.setLon(Double.parseDouble(lon.getValue()));
		zona.setLat(Double.parseDouble(lat.getValue()));
		zona.setPrecnik(Integer.parseInt(precnik.getValue()));
		zona.setOpis(opis.getValue());
		zona.setAktivan(aktivan.getValue());
		zona.setIzbrisan(izbrisan.getValue());
		return zona;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		naziv.clear();
		lon.setValue(String.valueOf(0.0));
		lat.setValue(String.valueOf(0.0));
		precnik.setValue(String.valueOf(0));
		opis.clear();
		aktivan.setValue(true);
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Zone zona = (Zone)podatak;
		if(zona.getId() != null) {
			pretplatnici.setValue(zona.getSistemPretplatnici());
		}
		try {
			naziv.setValue(zona.getNaziv());
		}catch (Exception e) {
			naziv.setValue(zona.getNaziv());
		}
		try {
			lon.setValue(String.valueOf(zona.getLon()));
		}catch (Exception e) {
			lon.setValue(String.valueOf(0.0));
		}
		try {
			lat.setValue(String.valueOf(zona.getLat()));
		}catch (Exception e) {
			lat.setValue(String.valueOf(0.0));
		}
		try {
			precnik.setValue(String.valueOf(zona.getPrecnik()));
		}catch (Exception e) {
			precnik.setValue(String.valueOf(0));
		}
		try {
			opis.setValue(zona.getOpis());
		}catch (Exception e) {
			opis.setValue("");
		}
		aktivan.setValue(zona.isAktivan());
		organizacije.setValue(zona.getOrganizacija());
		izbrisan.setValue(zona.isIzbrisan());
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		if(lon.isEmpty() || lon.getValue() == "" || Double.parseDouble(lon.getValue()) == 0.0) {
			sveIma = false;
		}
		if(lat.isEmpty() || lat.getValue() == "" || Double.parseDouble(lat.getValue()) == 0.0) {
			sveIma = false;
		}
		if(precnik.isEmpty() || precnik.getValue() == "" || Double.parseDouble(precnik.getValue()) < 10) {
			sveIma = false;
		}
		return sveIma;
	}

}
