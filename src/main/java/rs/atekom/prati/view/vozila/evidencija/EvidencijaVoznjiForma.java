package rs.atekom.prati.view.vozila.evidencija;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.EvidencijaVoznji;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Vozila;
import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboKorisnici;
import rs.atekom.prati.view.komponente.combo.ComboNalozi;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

public class EvidencijaVoznjiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private EvidencijaVoznjiLogika logika;
	//private CheckBox zakljucan;
	private ComboNalozi nalozi;
	private ComboKorisnici vozaci;
	private Tekst registracija, relacija, brpPutnogNaloga, preuzetoIz, vrstaRobe, magacin, otpremnica;
	private DatumVreme polazak, dolazak;
	private Celobrojni dana, sati, kolicina, utrosenoLitara;
	private Decimalni pocetna, zavrsna, razlika, potrosnja, cenaGoriva, prevoz, putniTroskovi, trosakGoriva, prevozUkupno, prevozPutni;
	private ComboObjekti vozila;
	//sifraPartnera, sifraPrograma, nazivPrograma, 

	public EvidencijaVoznjiForma(EvidencijaVoznjiLogika log) {
		logika = log;
		nalozi = new ComboNalozi(logika.view.korisnik, "налог", false, false);
		vozila = new ComboObjekti(logika.view.korisnik, "возила", true, true);
		vozaci = new ComboKorisnici(logika.view.korisnik, "возач", true, true);
		registracija = new Tekst("регистрација", true);
		relacija = new Tekst("релација", true);
		polazak = new DatumVreme(true, "очекивани полазак", 0, 0, 0);
		polazak.setWidth("100%");
		polazak.setRequiredIndicatorVisible(true);
		dolazak = new DatumVreme(true, "очекивани долазак", 0, 0, 0);
		dolazak.setWidth("100%");
		dolazak.setRequiredIndicatorVisible(true);
		pocetna = new Decimalni("почетна км", true);
		zavrsna = new Decimalni("завршна км", true);
		razlika = new Decimalni("разлика км", true);
		potrosnja = new Decimalni("потрошња", false);
		cenaGoriva = new Decimalni("цена горива дин/лит", false);
		prevoz = new Decimalni("превоз дин/км", false);
		putniTroskovi = new Decimalni("путни трошкови", false);
		brpPutnogNaloga = new Tekst("бр. путног налога", true);
		dana = new Celobrojni("дана", false);
		sati = new Celobrojni("сати", false);
		trosakGoriva = new Decimalni("трошак горива", false);
		prevozUkupno = new Decimalni("превоз укупно", false);
		prevozPutni = new Decimalni("превоз и путни", false);
		preuzetoIz = new Tekst("преузето из", false);
		vrstaRobe = new Tekst("врста робе", false);
		kolicina = new Celobrojni("количина", false);
		magacin = new Tekst("магацин", false);
		otpremnica = new Tekst("отпремница", false);
		//sifrapartnera
		//sifraPrograma
		//nazivPrograma
		utrosenoLitara = new Celobrojni("утрошено литара", false);
		
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
		
		nalozi.addValueChangeListener(new ValueChangeListener<VozilaNalozi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<VozilaNalozi> event) {
				brpPutnogNaloga.clear();
				vozila.clear();
				vozaci.clear();
				registracija.clear();
				relacija.clear();
				polazak.podesiVreme(0, 0, 0);
				dolazak.podesiVreme(0, 0, 0);
				pocetna.setValue(String.valueOf(0));
				zavrsna.setValue(String.valueOf(0));
				razlika.setValue(String.valueOf(0));
				if(event.getValue() != null) {
					VozilaNalozi nalog = event.getValue();
					brpPutnogNaloga.setValue(nalog.getBrojNaloga());
					vozila.setValue(nalog.getVozilo());
					vozaci.setValue(nalog.getVozac());
					Vozila vozilo = Servis.voziloServis.nadjiVoziloPoObjektu(nalog.getVozilo());
					if(vozilo != null) {
						registracija.setValue(vozilo.getRegistracija());
					}else {
						registracija.setValue(nalog.getVozilo().getOznaka());
					}
					relacija.setValue(nalog.getOdMesta() + " - " + nalog.getDoMesta());
					polazak.setValue(localTimeDatum(nalog.getOcekivaniPolazak()));
					dolazak.setValue(localTimeDatum(nalog.getOcekivaniDolazak()));
					Javljanja pocetno = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(nalog.getVozilo(), new Timestamp(nalog.getOcekivaniPolazak().getTime()), false);
					if(pocetno != null) {
						pocetna.setValue(String.valueOf(pocetno.getVirtualOdo()));
					}else {
						pocetna.setValue(String.valueOf(0));
					}
					Javljanja zavrsno = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(nalog.getVozilo(), new Timestamp(nalog.getOcekivaniPolazak().getTime()), true);
					if(zavrsno != null) {
						zavrsna.setValue(String.valueOf(zavrsno.getVirtualOdo()));
						razlika.setValue(String.valueOf(zavrsno.getVirtualOdo() - pocetno.getVirtualOdo()));
					}else {
						zavrsna.setValue(String.valueOf(0));
						razlika.setValue(String.valueOf(0));
					}
				}
			}
		});
		
		polazak.addValueChangeListener(new ValueChangeListener<LocalDateTime>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<LocalDateTime> event) {
				pocetna.setValue(String.valueOf(0));
				razlika.setValue(String.valueOf(0));
				if(event.getValue() != null) {
					if(vozila.getValue() != null) {
						Javljanja pocetno = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozila.getValue(), Timestamp.valueOf(event.getValue()), false);
						if(pocetno != null) {
							pocetna.setValue(String.valueOf(pocetno.getVirtualOdo()));
							if(pocetna.getValue() != null && !pocetna.getValue().equals("") && zavrsna.getValue() != null && !zavrsna.getValue().equals("")) {
								razlika.setValue(String.valueOf(Float.parseFloat(pocetna.getValue()) - Float.parseFloat(zavrsna.getValue())));
								}else {
									razlika.setValue(String.valueOf(0));
									}
							}
						}
					}
				}
			});
		
		dolazak.addValueChangeListener(new ValueChangeListener<LocalDateTime>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<LocalDateTime> event) {
				zavrsna.setValue(String.valueOf(0));
				razlika.setValue(String.valueOf(0));
				if(event.getValue() != null) {
					if(vozila.getValue() != null) {
						Javljanja zavrsno = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozila.getValue(), Timestamp.valueOf(event.getValue()), true);
						if(zavrsno != null) {
							zavrsna.setValue(String.valueOf(zavrsno.getVirtualOdo()));
							if(pocetna.getValue() != null && !pocetna.getValue().equals("") && zavrsna.getValue() != null && !zavrsna.getValue().equals("")) {
								razlika.setValue(String.valueOf(Float.parseFloat(pocetna.getValue()) - Float.parseFloat(zavrsna.getValue())));
							}else {
								razlika.setValue(String.valueOf(0));
							}
						}
					}
				}
			}
		});
		
		vozila.addValueChangeListener(new ValueChangeListener<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Objekti> event) {
				registracija.clear();
				if(event.getValue() != null) {
					Vozila vozilo = Servis.voziloServis.nadjiVoziloPoObjektu(event.getValue());
					if(vozilo != null) {
						registracija.setValue(vozilo.getRegistracija());
					}else {
						registracija.setValue(event.getValue().getOznaka());
					}
				}
			}
		});
		
		layout.addComponent(nalozi);
		layout.addComponent(brpPutnogNaloga);
		layout.addComponent(vozila);
		layout.addComponent(vozaci);
		layout.addComponent(registracija);
		layout.addComponent(relacija);
		layout.addComponent(polazak);
		layout.addComponent(dolazak);
		layout.addComponent(pocetna);
		layout.addComponent(zavrsna);
		layout.addComponent(razlika);
		layout.addComponent(potrosnja);
		layout.addComponent(cenaGoriva);
		layout.addComponent(prevoz);
		layout.addComponent(putniTroskovi);
		layout.addComponent(dana);
		layout.addComponent(sati);
		layout.addComponent(prevozPutni);
		layout.addComponent(preuzetoIz);
		layout.addComponent(vrstaRobe);
		layout.addComponent(kolicina);
		layout.addComponent(magacin);
		layout.addComponent(otpremnica);
		//layout.addComponnt(sifrapartnera)
		//layout.addComponent(sifraPrograma)
		//layout.addComponent(nazivPrograma)
		layout.addComponent(utrosenoLitara);
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		EvidencijaVoznji evidencija;
		ocistiPodatak();
		if(podatak == null) {
			evidencija = new EvidencijaVoznji();
		}else {
			evidencija = (EvidencijaVoznji)podatak;
			postaviPodatak(evidencija);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		EvidencijaVoznji evidencija;
		if(podatak == null) {
			evidencija = new EvidencijaVoznji();
		}else {
			evidencija = (EvidencijaVoznji)podatak;
		}
		evidencija.setSistemPretplatnici(pretplatnici.getValue());
		evidencija.setOrganizacija(organizacije.getValue());
		evidencija.setVoziloNalog(nalozi.getValue());
		evidencija.setVozac(vozaci.getValue());
		evidencija.setRegistracijaVozila(registracija.getValue());
		evidencija.setRelacija(relacija.getValue());
		try {
			evidencija.setDatumVremePolaska(dateTimeDatum(polazak.getValue()));
		}catch (Exception e) {
			evidencija.setDatumVremePolaska(null);
		}
		try {
			evidencija.setDatumVremeDolaska(dateTimeDatum(dolazak.getValue()));
		}catch (Exception e) {
			evidencija.setDatumVremeDolaska(null);
		}
		try {
			evidencija.setPocetnaKm(Float.valueOf(pocetna.getValue()));
		}catch (Exception e) {
			evidencija.setPocetnaKm(0);
		}
		try {
			evidencija.setZavrsnaKm(Float.valueOf(zavrsna.getValue()));
		}catch (Exception e) {
			evidencija.setZavrsnaKm(0);
		}
		try {
			evidencija.setRazlikaKm(Float.valueOf(zavrsna.getValue()));
		}catch (Exception e) {
			evidencija.setRazlikaKm(0);
		}
		try {
			evidencija.setPotrosnja(Float.valueOf(potrosnja.getValue()));
		}catch (Exception e) {
			evidencija.setPotrosnja(0);
		}
		try {
			evidencija.setGorivoCena(Float.valueOf(cenaGoriva.getValue()));
		}catch (Exception e) {
			evidencija.setGorivoCena(0);
		}
		try {
			evidencija.setPrevozCena(Float.valueOf(prevoz.getValue()));
		}catch (Exception e) {
			evidencija.setPrevozCena(0);
		}
		try {
			evidencija.setPutniTroskovi(Float.valueOf(putniTroskovi.getValue()));
		}catch (Exception e) {
			evidencija.setPutniTroskovi(0);
		}
		evidencija.setBrojPutnogNaloga(brpPutnogNaloga.getValue());
		try {
			evidencija.setDana(Integer.valueOf(dana.getValue()));
		}catch (Exception e) {
			evidencija.setDana(0);
		}
		try {
			evidencija.setSati(Integer.valueOf(sati.getValue()));
		}catch (Exception e) {
			evidencija.setSati(0);
		}
		try {
			evidencija.setTroskoviGoriva(Float.valueOf(trosakGoriva.getValue()));
		}catch (Exception e) {
			evidencija.setTroskoviGoriva(0);
		}
		try {
			evidencija.setPrevozUkupno(Float.valueOf(prevozUkupno.getValue()));
		}catch (Exception e) {
			evidencija.setPrevozUkupno(0);
		}
		evidencija.setPreuzetoIz(preuzetoIz.getValue());
		evidencija.setVrstaRobe(vrstaRobe.getValue());
		try {
			evidencija.setKolicina(Integer.valueOf(kolicina.getValue()));
		}catch (Exception e) {
			evidencija.setKolicina(0);
		}
		
		evidencija.setMagacin(magacin.getValue());
		evidencija.setOtpremnica(otpremnica.getValue());
		//evidencija.setSifra(sifraPartnera);
		//evidencija.setSifraPrograma(sifraPrograma);
		//evidencija.setProgram(nazivPrograma);
		try {
			evidencija.setUtrosenoLitara(Float.valueOf(utrosenoLitara.getValue()));
		}catch (Exception e) {
			evidencija.setUtrosenoLitara(0);
		}
		evidencija.setZakljucan(true);
		evidencija.setUradio(logika.view.korisnik);
		return evidencija;
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
		nalozi.clear();
		vozaci.clear();
		registracija.clear();
		relacija.clear();
		polazak.podesiVreme(0,0,0);
		dolazak.podesiVreme(23, 59, 0);
		pocetna.setValue("0");
		zavrsna.setValue("0");
		razlika.setValue("0");
		potrosnja.setValue("0");
		cenaGoriva.setValue("0");
		prevoz.setValue("0");
		putniTroskovi.setValue("0");
		brpPutnogNaloga.clear();
		dana.setValue("0");
		sati.setValue("0");
		trosakGoriva.setValue("0");
		prevozUkupno.setValue("0");
		prevozPutni.setValue("0");
		preuzetoIz.clear();
		vrstaRobe.clear();
		magacin.clear();
		otpremnica.clear();
		//sifraPartnera.clear();
		//sifraPrograma.clear();
		//nazivPrograma.clear();
		utrosenoLitara.setValue("0");
		zakljucaj(true);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		EvidencijaVoznji evidencija = (EvidencijaVoznji)podatak;
		if(evidencija.getId() != null) {
			pretplatnici.setValue(evidencija.getSistemPretplatnici());
			organizacije.setValue(evidencija.getOrganizacija());
			organizacije.setEnabled(false);
			nalozi.setValue(evidencija.getVoziloNalog());
			vozaci.setValue(evidencija.getVozac());
			try {
				registracija.setValue(evidencija.getRegistracijaVozila());
			}catch (Exception e) {
				registracija.setValue("");
			}
			try {
				relacija.setValue(evidencija.getRelacija());
			}catch (Exception e) {
				relacija.setValue("");
			}
			if(evidencija.getDatumVremePolaska() != null) {
				polazak.setValue(localTimeDatum(evidencija.getDatumVremePolaska()));
			}else {
				polazak.setValue(null);
			}
			if(evidencija.getDatumVremeDolaska() != null) {
				dolazak.setValue(localTimeDatum(evidencija.getDatumVremeDolaska()));
			}else {
				polazak.setValue(null);
			}
			pocetna.setValue(String.valueOf(evidencija.getPocetnaKm()));
			zavrsna.setValue(String.valueOf(evidencija.getZavrsnaKm()));
			razlika.setValue(String.valueOf(evidencija.getRazlikaKm()));
			potrosnja.setValue(String.valueOf(evidencija.getPotrosnja()));
			cenaGoriva.setValue(String.valueOf(evidencija.getGorivoCena()));
			prevoz.setValue(String.valueOf(evidencija.getPrevozCena()));
			putniTroskovi.setValue(String.valueOf(evidencija.getPutniTroskovi()));
			brpPutnogNaloga.setValue(evidencija.getBrojPutnogNaloga());
			dana.setValue(String.valueOf(evidencija.getDana()));
			sati.setValue(String.valueOf(evidencija.getSati()));
			trosakGoriva.setValue(String.valueOf(evidencija.getTroskoviGoriva()));
			prevozUkupno.setValue(String.valueOf(evidencija.getPrevozUkupno()));
			prevozPutni.setValue(String.valueOf(evidencija.getPrevozPutniTrosak()));
			preuzetoIz.setValue(evidencija.getPreuzetoIz());
			vrstaRobe.setValue(evidencija.getVrstaRobe());
			kolicina.setValue(String.valueOf(evidencija.getKolicina()));
			magacin.setValue(evidencija.getMagacin());
			otpremnica.setValue(evidencija.getOtpremnica());
			//sifraPartnera.setValue(evidencija.getSifraPartnera())
			//sifraprograma.setValue(evidencija.getSifraPrograma())
			//nazivPrograma.setValue(evidencija.getNazivPrograma())
			utrosenoLitara.setValue(String.valueOf(evidencija.getUtrosenoLitara()));
			if(evidencija.isZakljucan()) {
				zakljucaj(false);
			}else {
				zakljucaj(true);
			}
		}else {
			zakljucaj(true);
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(brpPutnogNaloga.getValue() == null || brpPutnogNaloga.getValue().equals("")) {
			sveIma = false;
		}
		return sveIma;
	}

	private void zakljucaj(boolean zakljucaj) {
		int i = 0;
		while(layout.iterator().hasNext() && i < 27) {
			layout.getComponent(i).setEnabled(zakljucaj);
			i++;
		}
		//otkazi.setEnabled(true);
		//izbrisi.setEnabled(true);
	}

}
