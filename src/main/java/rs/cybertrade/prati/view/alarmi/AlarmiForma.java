package rs.cybertrade.prati.view.alarmi;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.SistemAlarmi;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.Tekst;

public class AlarmiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private AlarmiLogika logika;
	private Tekst sifra, naziv, opis;
	private CheckBox adresa, alarmiranje, prikaz, pregled, aktivan, izbrisan;
	
	public AlarmiForma(AlarmiLogika log) {
		logika = log;
		sifra = new Tekst("шифра", true);
		naziv = new Tekst("назив", true);
		opis = new Tekst("опис", false);
		adresa = new CheckBox("адреса");
		alarmiranje = new CheckBox("алармирање");
		prikaz = new CheckBox("приказ");
		pregled = new CheckBox("преглед");
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
		
		layout.addComponent(sifra);
		layout.addComponent(naziv);
		layout.addComponent(opis);
		layout.addComponent(adresa);
		layout.addComponent(alarmiranje);
		layout.addComponent(prikaz);
		layout.addComponent(pregled);
		layout.addComponent(aktivan);
		layout.addComponent(izbrisan);
		
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemAlarmi alarm;
		ocistiPodatak();
		if(podatak == null) {
			alarm = new SistemAlarmi();
		}else {
			alarm = (SistemAlarmi)podatak;
			postaviPodatak(alarm);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemAlarmi alarm;
		if(podatak == null) {
			alarm = new SistemAlarmi();
		}else {
			alarm = (SistemAlarmi)podatak;
		}
		alarm.setSifra(sifra.getValue());
		alarm.setNaziv(naziv.getValue());
		alarm.setOpis(opis.getValue());
		alarm.setAdresa(adresa.getValue());
		alarm.setAlarmiranje(alarmiranje.getValue());
		alarm.setPrikaz(prikaz.getValue());
		alarm.setPregled(pregled.getValue());
		alarm.setAktivan(aktivan.getValue());
		alarm.setIzbrisan(izbrisan.getValue());
		return alarm;
	}

	@Override
	public void ocistiPodatak() {
		sifra.clear();
		naziv.clear();
		opis.clear();
		adresa.setValue(false);
		alarmiranje.setValue(false);
		prikaz.setValue(true);
		pregled.setValue(false);
		aktivan.setValue(true);
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemAlarmi alarm = (SistemAlarmi)podatak;
		if(alarm.getId() != null) {
			try {
				sifra.setValue(alarm.getSifra());
			}catch (Exception e) {
				sifra.setValue("");
			}
			try {
				naziv.setValue(alarm.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				opis.setValue(alarm.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			try {
				adresa.setValue(alarm.isAdresa());
			}catch (Exception e) {
				adresa.setValue(false);
			}
			try {
				alarmiranje.setValue(alarm.isAlarmiranje());
			}catch (Exception e) {
				alarmiranje.setValue(false);
			}
			try {
				prikaz.setValue(alarm.isPrikaz());
			}catch (Exception e) {
				prikaz.setValue(false);
			}
			try {
				pregled.setValue(alarm.isPregled());
			}catch (Exception e) {
				pregled.setValue(false);
			}
			try {
				aktivan.setValue(alarm.isAktivan());
			}catch (Exception e) {
				aktivan.setValue(false);
			}
			try {
				izbrisan.setValue(alarm.isIzbrisan());
			}catch (Exception e) {
				izbrisan.setValue(false);
			}
		}else {
			ocistiPodatak();
		}
		
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(sifra.getValue() == null || sifra.isEmpty() || sifra.getValue() == "") {
			sveIma = false;
		}
		if(naziv.getValue() == null || naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
