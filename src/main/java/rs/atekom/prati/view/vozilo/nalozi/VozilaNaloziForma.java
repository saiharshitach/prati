package rs.atekom.prati.view.vozilo.nalozi;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboKorisnici;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

public class VozilaNaloziForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaNaloziLogika logika;
	private ComboKorisnici vozaci;
	private DatumVreme polazak, dolazak;
	private Tekst brojNaloga, odMesta, doMesta, medjuTacke, komentar;
	private ComboObjekti vozila;
	private CheckBox izbrisan;

	public VozilaNaloziForma(VozilaNaloziLogika log) {
		logika = log;
		brojNaloga = new Tekst("број", true);
		vozila = new ComboObjekti(logika.view.korisnik, "возила", true, true);
		odMesta = new Tekst("од", true);
		medjuTacke = new Tekst("преко", false);
		doMesta = new Tekst("до", true);
		polazak = new DatumVreme(true, "очекивани полазак", 0, 0, 0);
		polazak.setWidth("100%");
		polazak.setRequiredIndicatorVisible(true);
		dolazak = new DatumVreme(true, "очекивани долазак", 0, 0, 0);
		dolazak.setWidth("100%");
		dolazak.setRequiredIndicatorVisible(true);
		vozaci = new ComboKorisnici(logika.view.korisnik, "возач", true, false);
		komentar = new Tekst("опис", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozila.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					vozila.setItems(Servis.objekatServis.vratiSveObjekteVozila(event.getValue(), null));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozila.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					vozila.setItems(Servis.objekatServis.vratiSveObjekteVozila(pretplatnici.getValue(), event.getValue()));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
				}else {
					vozila.setItems(Servis.objekatServis.vratiSveObjekteVozila(pretplatnici.getValue(), null));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), null, true));
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

		layout.addComponent(brojNaloga);
		layout.addComponent(vozila);
		layout.addComponent(odMesta);
		layout.addComponent(medjuTacke);
		layout.addComponent(doMesta);
		layout.addComponent(polazak);
		layout.addComponent(dolazak);
		layout.addComponent(vozaci);
		layout.addComponent(komentar);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaNalozi nalog;
		ocistiPodatak();
		if(podatak == null) {
			nalog = new VozilaNalozi();
		}else {
			nalog = (VozilaNalozi)podatak;
			postaviPodatak(nalog);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozilaNalozi nalog;
		if(podatak == null) {
			nalog = new VozilaNalozi();
		}else {
			nalog = (VozilaNalozi)podatak;
		}
		nalog.setSistemPretplatnici(pretplatnici.getValue());
		nalog.setOrganizacija(null);
		nalog.setBrojNaloga(brojNaloga.getValue());
		nalog.setVozilo(vozila.getValue());
		nalog.setOdMesta(odMesta.getValue());
		nalog.setMedjuTacke(medjuTacke.getValue());
		nalog.setDoMesta(doMesta.getValue());
		try {
			nalog.setOcekivaniPolazak(dateTimeDatum(polazak.getValue()));
		}catch (Exception e) {
			nalog.setOcekivaniPolazak(null);
		}
		try {
			nalog.setOcekivaniDolazak(dateTimeDatum(dolazak.getValue()));
		}catch (Exception e) {
			nalog.setOcekivaniDolazak(null);
		}
		nalog.setVozac(vozaci.getValue());
		nalog.setKomentar(komentar.getValue());
		nalog.setIzbrisan(izbrisan.getValue());
		return nalog;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
			organizacije.setEnabled(true);
		}
		brojNaloga.clear();
		vozila.clear();
		odMesta.clear();
		medjuTacke.clear();
		doMesta.clear();
		polazak.clear();
		polazak.podesiVreme(0,0,0);
		dolazak.clear();
		dolazak.podesiVreme(23, 59, 0);
		vozaci.clear();
		komentar.clear();
		komentar.setValue("превоз стручног сарадника");
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozilaNalozi nalog = (VozilaNalozi)podatak;
		if(nalog.getId() != null) {
			pretplatnici.setValue(nalog.getSistemPretplatnici());
			organizacije.setValue(nalog.getVozilo().getOrganizacija());
			organizacije.setEnabled(false);
			brojNaloga.setValue(nalog.getBrojNaloga());
			vozila.setValue(nalog.getVozilo());
			try {
				odMesta.setValue(nalog.getOdMesta());
			}catch (Exception e) {
				odMesta.setValue("");
			}
			try {
				medjuTacke.setValue(nalog.getMedjuTacke());
			}catch (Exception e) {
				medjuTacke.setValue("");
			}
			try {
				doMesta.setValue(nalog.getDoMesta());
			}catch (Exception e) {
				doMesta.setValue("");
			}
			if(nalog.getOcekivaniPolazak() != null) {
				polazak.setValue(localTimeDatum(nalog.getOcekivaniPolazak()));
			}else {
				polazak.setValue(null);
			}
			if(nalog.getOcekivaniDolazak() != null) {
				dolazak.setValue(localTimeDatum(nalog.getOcekivaniDolazak()));
			}else {
				polazak.setValue(null);
			}
			vozaci.setValue(nalog.getVozac());
			izbrisan.setValue(nalog.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(brojNaloga.getValue() == null || brojNaloga.getValue().equals("")) {
			sveIma = false;
		}
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(vozila.getValue() == null) {
			sveIma = false;
		}
		if(odMesta.getValue() == null || odMesta.getValue().equals("")) {
			sveIma = false;
		}
		if(odMesta.getValue() == null || odMesta.getValue().equals("")) {
			sveIma = false;
		}
		if(polazak.getValue() == null) {
			sveIma = false;
		}
		if(dolazak.getValue() == null) {
			sveIma = false;
		}
		if(sveIma && polazak.getValue().isAfter(dolazak.getValue())) {
			sveIma = false;
		}
		return sveIma;
	}

}
