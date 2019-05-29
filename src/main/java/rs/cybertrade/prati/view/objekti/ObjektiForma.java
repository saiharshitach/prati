package rs.cybertrade.prati.view.objekti;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.Uredjaji;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.ComboOrganizacije;
import rs.cybertrade.prati.view.komponente.ComboPretplatnici;
import rs.cybertrade.prati.view.komponente.Tekst;
import rs.cybertrade.prati.view.komponente.ComboUredjaji;

public class ObjektiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private ObjektiLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboUredjaji uredjaji;
	private Tekst oznaka, simBroj, sim;
	private CheckBox aktivan, vozilo, izbrisan;
	
	public ObjektiForma(ObjektiLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, true);
		oznaka = new Tekst("ознака", true);
		
		//sim = new SimCombo(logika.view.korisnik, "сим картица", true, true);
		sim = new Tekst("сим", false);
		simBroj = new Tekst("сим број", false);
		sim.setEnabled(false);
		simBroj.setEnabled(false);
		aktivan = new CheckBox("активан");
		vozilo = new CheckBox("возило");
		izbrisan = new CheckBox("избрисан");
		

		uredjaji = new ComboUredjaji(pretplatnici.getValue(), organizacije.getValue(), "уређај", true, true, null);
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				
				
			}
		});
		
		uredjaji.addValueChangeListener(new ValueChangeListener<Uredjaji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Uredjaji> event) {
				if(event != null) {
					if(event.getValue() != null) {
						sim.setValue(event.getValue().getSim() == null ? "" : event.getValue().getSim().getIccid());
						simBroj.setValue(event.getValue().getSim() == null ? "" : event.getValue().getSim().getBroj());
					}
				}
			}
		});
		
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
		
		if(logika.view.isAdmin()) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(oznaka);
		layout.addComponent(uredjaji);
		layout.addComponent(sim);
		layout.addComponent(simBroj);
		layout.addComponent(vozilo);
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
		ocistiPodatak();
		Objekti objekat;
		if(podatak == null) {
			objekat = new Objekti();
		}else {
			objekat = (Objekti)podatak;
			postaviPodatak(objekat);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Objekti objekat;
		if(podatak == null) {
			objekat = new Objekti();
		}else {
			objekat = (Objekti)podatak;
			Uredjaji uredjajStari = objekat.getUredjaji();
			if(uredjajStari != null) {
				uredjajStari.setObjekti(null);
				uredjajStari.setZauzet(false);
				Servis.uredjajServis.izmeniUredjaj(uredjajStari);
			}
		}
		objekat.setSistemPretplatnici(pretplatnici.getValue());
		objekat.setOznaka(oznaka.getValue());
		objekat.setUredjaji(uredjaji.getValue());
		objekat.setTip(vozilo.getValue());
		objekat.setAktivan(aktivan.getValue());
		objekat.setOrganizacija(organizacije.getValue());
		objekat.setIzbrisan(izbrisan.getValue());
		if(!objekat.isAktivan()) {
			Servis.grupeObjekatServis.izbrisiSveGrupeObjekatPoObjektu(objekat);
		}
		return objekat;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		oznaka.clear();
		uredjaji.clear();
		sim.clear();
		simBroj.clear();
		vozilo.setValue(true);
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
		Objekti objekat = (Objekti)podatak;
		if(objekat.getId() != null) {
			pretplatnici.setValue(objekat.getSistemPretplatnici());
			try {
				oznaka.setValue(objekat.getOznaka());
			}catch (Exception e) {
				oznaka.setValue("");
			}
			try {
				Uredjaji uredjaj = objekat.getUredjaji();
				uredjaji.setItems(Servis.uredjajServis.nadjiSveAktivneSlobodneUredjajePoPretplatniku(objekat.getSistemPretplatnici(), objekat.getOrganizacija(), uredjaj));
				uredjaji.setSelectedItem(uredjaj);
			}catch (Exception e) {
				uredjaji.setItems(Servis.uredjajServis.nadjiSveAktivneSlobodneUredjajePoPretplatniku(objekat.getSistemPretplatnici(), objekat.getOrganizacija(), null));
			}
			try {
				sim.setValue(objekat.getUredjaji().getSim().getIccid());
				simBroj.setValue(objekat.getUredjaji().getSim().getBroj());
			}catch (Exception e) {
				sim.setValue("");
				simBroj.setValue("");
			}
			vozilo.setValue(objekat.getTip());
			aktivan.setValue(objekat.isAktivan());
			organizacije.setValue(objekat.getOrganizacija());
			izbrisan.setValue(objekat.isIzbrisan());
		}
		
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(oznaka.isEmpty() || oznaka.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
