package rs.atekom.prati.view.alarmiKorisnik;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboAlarmi;
import rs.atekom.prati.view.komponente.ComboKorisnici;
import rs.atekom.prati.view.komponente.ComboObjekti;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;

public class AlarmKorisnikForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private AlarmKorisnikLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboKorisnici korisnici;
	private ComboObjekti objekti;
	private ComboAlarmi alarmi;
	private CheckBox aktivan;
	

	public AlarmKorisnikForma(AlarmKorisnikLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, true);
		korisnici = new ComboKorisnici(logika.view.korisnik, "корисник", true, true);
		objekti = new ComboObjekti(logika.view.korisnik, "објекти", true, true);
		alarmi = new ComboAlarmi("аларми", true, true, true, true);
		aktivan = new CheckBox("активан");
		
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
				// TODO Auto-generated method stub
				
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
		
		if(logika.view.korisnik.isSistem() && logika.view.korisnik.getSistemPretplatnici() == null) {
			layout.addComponent(pretplatnici);
		}
		if(logika.view.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
		if(logika.view.korisnik.isAdmin()) {
			layout.addComponent(korisnici);
		}
		layout.addComponent(objekti);
		layout.addComponent(alarmi);
		layout.addComponent(aktivan);
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
		alarmKorisnik.setAktivan(aktivan.getValue());
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
		aktivan.setValue(true);
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
			aktivan.setValue(alarmKorisnik.isAktivan());
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
