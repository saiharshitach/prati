package rs.atekom.prati.view.vozila.evidencija;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.EvidencijaVoznji;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboKorisnici;
import rs.atekom.prati.view.komponente.ComboNalozi;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;

public class EvidencijaVoznjiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private EvidencijaVoznjiLogika logika;
	private CheckBox zakljucan;
	private ComboNalozi nalozi;
	private ComboKorisnici vozaci;
	private Tekst registracija, relacija, brpPutnogNaloga, preuzetoIz, vrstaRobe, magacin, otpremnica;
	private DatumVreme polazak, dolazak;
	private Celobrojni dana, sati, kolicina, utrosenoLitara;
	private Decimalni pocetna, zavrsna, razlika, potrosnja, cenaGoriva, prevoz, putniTroskovi, trosakGoriva, prevozUkupno, prevozPutni;
	//sifraPartnera, sifraPrograma, nazivPrograma, 

	public EvidencijaVoznjiForma(EvidencijaVoznjiLogika log) {
		logika = log;
		nalozi = new ComboNalozi(logika.view.korisnik, "налог", false, false);
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
		brpPutnogNaloga = new Tekst("бр. путног налога", false);
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
		
		layout.addComponent(nalozi);
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
		layout.addComponent(brpPutnogNaloga);
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
		evidencija.setRegistracijaVozila(registracija.getValue());
		evidencija.setRelacija(relacija.getValue());
		evidencija.setDatumVremePolaska(dateTimeDatum(polazak.getValue()));
		evidencija.setDatumVremeDolaska(dateTimeDatum(dolazak.getValue()));
		evidencija.setPocetnaKm(Float.valueOf(pocetna.getValue()));
		evidencija.setZavrsnaKm(Float.valueOf(zavrsna.getValue()));
		evidencija.setRazlikaKm(Float.valueOf(zavrsna.getValue()));
		return null;
	}

	@Override
	public void ocistiPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postaviPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean proveraPodataka() {
		// TODO Auto-generated method stub
		return false;
	}

}
