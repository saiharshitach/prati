package rs.atekom.prati.view.partneri;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Partneri;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.Tekst;

public class PartneriForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private PartneriLogika logika;
	private Tekst naziv, mb, adresa, telefon, mobilni, eposta, kontaktOsoba;
	private Celobrojni pib;
	private CheckBox izbrisan;

	public PartneriForma(PartneriLogika log) {
		logika = log;
		naziv = new Tekst("назив", true);
		pib = new Celobrojni("ПИБ", true);
		mb = new Tekst("мб", false);
		adresa = new Tekst("адреса", false);
		telefon = new Tekst("телефон", false);
		mobilni = new Tekst("мобилни", false);
		eposta = new Tekst("е-пошта", false);
		kontaktOsoba = new Tekst("контакт особа", false);
		izbrisan = new CheckBox("избрисан");
		
		layout.removeComponent(organizacije);
		
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

		layout.addComponent(naziv);
		layout.addComponent(pib);
		layout.addComponent(mb);
		layout.addComponent(adresa);
		layout.addComponent(telefon);
		layout.addComponent(mobilni);
		layout.addComponent(eposta);
		layout.addComponent(kontaktOsoba);
		layout.addComponent(izbrisan);
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		Partneri partner;
		ocistiPodatak();
		if(podatak == null) {
			partner = new Partneri();
		}else {
			partner = (Partneri)podatak;
			postaviPodatak(partner);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Partneri partner;
		if(podatak == null) {
			partner = new Partneri();
		}else {
			partner = (Partneri)podatak;
		}
		partner.setSistemPretplatnici(pretplatnici.getValue());
		partner.setNaziv(naziv.getValue());
		partner.setPib(Integer.parseInt(pib.getValue()));
		partner.setMb(mb.getValue());
		partner.setAdresa(adresa.getValue());
		partner.setTelefon(telefon.getValue());
		partner.setMobilni(mobilni.getValue());
		partner.setEposta(eposta.getValue());
		partner.setKontaktOsoba(kontaktOsoba.getValue());
		partner.setIzbrisan(izbrisan.getValue());
		return partner;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		naziv.clear();
		pib.clear();
		mb.clear();
		adresa.clear();
		mobilni.clear();
		eposta.clear();
		kontaktOsoba.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Partneri partner = (Partneri)podatak;
		if(partner.getId() != null) {
			pretplatnici.setValue(partner.getSistemPretplatnici());
			try {
				naziv.setValue(partner.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				pib.setValue(String.valueOf(partner.getPib()));
			}catch (Exception e) {
				pib.setValue("");
			}
			try {
				mb.setValue(partner.getMb());
			}catch (Exception e) {
				mb.setValue("");
			}
			try {
				adresa.setValue(partner.getAdresa());
			}catch (Exception e) {
				adresa.setValue("");
			}
			try {
				telefon.setValue(partner.getTelefon());
			}catch (Exception e) {
				telefon.setValue("");
			}
			try {
				mobilni.setValue(partner.getMobilni());
			}catch (Exception e) {
				mobilni.setValue("");
			}
			try {
				eposta.setValue(partner.getEposta());
			}catch (Exception e) {
				eposta.setValue("");
			}
			try {
				kontaktOsoba.setValue(partner.getKontaktOsoba());
			}catch (Exception e) {
				kontaktOsoba.setValue("");
			}
			izbrisan.setValue(partner.isIzbrisan());
		}
		
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
		if(pib.isEmpty() || pib.getValue() == "" || pib.getValue().length() != 9) {
			sveIma = false;
		}
		return sveIma;
	}

}
