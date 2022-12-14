package rs.atekom.prati.view.alarmiKorisnik;

import java.util.ArrayList;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.combo.ComboAlarmi;
import rs.atekom.prati.view.komponente.combo.ComboKorisnici;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

public class AlarmKorisnikForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private AlarmKorisnikLogika logika;
	private ComboKorisnici korisnici;
	private ComboObjekti objekti;
	private ComboAlarmi alarmi;
	private CheckBox email, obavestenje, aktivan;
	private ArrayList<CheckBox> niz;
	private CheckBoxGroup<CheckBox> chkBox;

	public AlarmKorisnikForma(AlarmKorisnikLogika log) {
		logika = log;
		korisnici = new ComboKorisnici(logika.view.korisnik, "корисник", true, true);
		objekti = new ComboObjekti(logika.view.korisnik, "објекти", true, true);
		alarmi = new ComboAlarmi("аларми", true, true, true, false, true);
		email = new CheckBox("е-пошта");
		obavestenje = new CheckBox("обавештење");
		aktivan = new CheckBox("активан");
		
		niz = new ArrayList<CheckBox>();
		chkBox = new CheckBoxGroup<CheckBox>("");
		niz.add(email);
		niz.add(obavestenje);
		niz.add(aktivan);
		
		chkBox.setItemCaptionGenerator(item -> item.getCaption());
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
				
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				korisnici.setItems(Servis.korisnikServis.nadjiKorisnikePoOrganizaciji(pretplatnici.getValue(), event.getValue()));
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
		
		if(logika.view.korisnik.isAdmin()) {
			layout.addComponent(korisnici);
		}
		layout.addComponent(objekti);
		layout.addComponent(alarmi);
		
		chkBox.setItems(niz);
		layout.addComponent(chkBox);
		//layout.addComponent(email);
		//layout.addComponent(obavestenje);
		//layout.addComponent(aktivan);
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		AlarmiKorisnik alarmKorisnik;
		ocistiPodatak();
		if(podatak == null) {
			alarmKorisnik = new AlarmiKorisnik();
		}else {
			alarmKorisnik = (AlarmiKorisnik)podatak;
			postaviPodatak(alarmKorisnik);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		AlarmiKorisnik alarmKorisnik;
		if(podatak == null) {
			alarmKorisnik = new AlarmiKorisnik();
		}else {
			alarmKorisnik = (AlarmiKorisnik)podatak;
		}
		alarmKorisnik.setSistemPretplatnici(pretplatnici.getValue());
		alarmKorisnik.setOrganizacija(organizacije.getValue());
		alarmKorisnik.setKorisnik(korisnici.getValue());
		alarmKorisnik.setObjekti(objekti.getValue());
		alarmKorisnik.setSistemAlarmi(alarmi.getValue());
		if(chkBox.isSelected(niz.get(0))) {
			alarmKorisnik.setEmail(true);
		}else {
			alarmKorisnik.setEmail(false);
		}
		if(chkBox.isSelected(niz.get(1))) {
			alarmKorisnik.setObavestenje(true);
		}else {
			alarmKorisnik.setObavestenje(false);
		}
		if(chkBox.isSelected(niz.get(2))) {
			alarmKorisnik.setAktivan(true);
		}else {
			alarmKorisnik.setAktivan(false);
		}
		return alarmKorisnik;
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
		}
		if(!logika.view.korisnik.isAdmin()) {
			korisnici.setValue(logika.view.korisnik);
		}else {
			korisnici.clear();
		}
		objekti.clear();
		alarmi.clear();
		chkBox.deselectAll();
	}

	@Override
	public void postaviPodatak(Object podatak) {
		AlarmiKorisnik alarmKorisnik = (AlarmiKorisnik)podatak;
		if(alarmKorisnik.getId() != null) {
			pretplatnici.setValue(alarmKorisnik.getSistemPretplatnici());
			organizacije.setValue(alarmKorisnik.getOrganizacija());
			korisnici.setValue(alarmKorisnik.getKorisnik());
			objekti.setValue(alarmKorisnik.getObjekti());
			alarmi.setValue(alarmKorisnik.getSistemAlarmi());
			if(alarmKorisnik.isEmail()) {
				chkBox.select(niz.get(0));
			}
			if(alarmKorisnik.isObavestenje()) {
				chkBox.select(niz.get(1));
			}
			if(alarmKorisnik.isAktivan()) {
				chkBox.select(niz.get(2));
			}
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(korisnici.getValue() == null) {
			sveIma = false;
		}
		if(objekti.getValue() == null) {
			sveIma = false;
		}
		if(alarmi.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
