package rs.atekom.prati.view.vozaci;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.Vozaci;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboKorisnici;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;

public class VozaciForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozaciLogika logika;
	private ComboKorisnici vozaci;
	private Tekst jmbg, prebivaliste;
	private Datum zaposlenDo, zaposlenOd;
	private CheckBox izbrisan;

	public VozaciForma(VozaciLogika log) {
		logika = log;
		vozaci = new ComboKorisnici(logika.view.korisnik, "возач", true, true);
		jmbg = new Tekst("јмбг", false);
		prebivaliste = new Tekst("пребивалиште", false);
		zaposlenOd = new Datum("запослен од", true);
		zaposlenDo = new Datum("запослен до", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozaci.clear();
				vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
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
		
		layout.addComponent(vozaci);
		layout.addComponent(jmbg);
		layout.addComponent(prebivaliste);
		layout.addComponent(zaposlenOd);
		layout.addComponent(zaposlenDo);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	
	@Override
	public void izmeniPodatak(Object podatak) {
		Vozaci vozac;
		ocistiPodatak();
		if(podatak == null) {
			vozac = new Vozaci();
		}else {
			vozac = (Vozaci)podatak;
			postaviPodatak(vozac);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Vozaci vozac;
		if(podatak == null) {
			vozac = new Vozaci();
		}else {
			vozac = (Vozaci)podatak;
		}
		vozac.setSistemPretplatnici(pretplatnici.getValue());
		vozac.setOrganizacija(null);
		vozac.setKorisnici(vozaci.getValue());
		vozac.setJmbg(jmbg.getValue());
		vozac.setPrebivaliste(prebivaliste.getValue());
		try {
			vozac.setZaposlenOd(dateDatum(zaposlenOd.getValue()));
		}catch (Exception e) {
			vozac.setZaposlenOd(null);
		}
		try {
			vozac.setZaposlenDo(dateDatum(zaposlenDo.getValue()));
		}catch (Exception e) {
			vozac.setZaposlenDo(null);
		}
		vozac.setIzbrisan(izbrisan.getValue());
		return vozac;
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
		vozaci.clear();
		jmbg.clear();
		prebivaliste.clear();
		zaposlenOd.clear();
		zaposlenDo.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Vozaci vozac = (Vozaci)podatak;
		if(vozac.getId() != null) {
			pretplatnici.setValue(vozac.getSistemPretplatnici());
			organizacije.setValue(vozac.getKorisnici().getOrganizacija());
			organizacije.setEnabled(false);
			vozaci.setValue(vozac.getKorisnici());
			try {
				jmbg.setValue(vozac.getJmbg());
			}catch (Exception e) {
				jmbg.setValue("");
			}
			try {
				prebivaliste.setValue(vozac.getPrebivaliste());
			}catch (Exception e) {
				prebivaliste.setValue("");
			}
			if(vozac.getZaposlenOd() != null) {
				zaposlenOd.setValue(localDatum(vozac.getZaposlenOd()));
			}else {
				zaposlenOd.setValue(null);
			}
			if(vozac.getZaposlenDo() != null) {
				zaposlenDo.setValue(localDatum(vozac.getZaposlenDo()));
			}else {
				zaposlenDo.setValue(null);
			}
			izbrisan.setValue(vozac.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(vozaci.getValue() == null) {
			sveIma = false;
		}
		if(zaposlenOd.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
