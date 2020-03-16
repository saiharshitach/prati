package rs.atekom.prati.view.sifre;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.Sifre;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboPartneri;

public class SifreForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private SifreLogika logika;
	private ComboPartneri partneri;
	private Tekst sifraPolje, opis;
	private CheckBox izbrisan;

	public SifreForma(SifreLogika log) {
		logika = log;
		partneri = new ComboPartneri(logika.view.korisnik, "партнери", true, true);
		sifraPolje = new Tekst("шифра", false);
		opis = new Tekst("опис", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				partneri.clear();
				organizacije.clear();
				if(event.getValue() != null) {
					partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(event.getValue(), true));
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				partneri.clear();
				partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(pretplatnici.getValue(), true));
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
		
		layout.addComponent(partneri);
		layout.addComponent(sifraPolje);
		layout.addComponent(opis);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		Sifre sifra;
		if(podatak == null) {
			sifra = new Sifre();
		}else {
			sifra = (Sifre)podatak;
			postaviPodatak(sifra);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Sifre sifra;
		if(podatak == null) {
			sifra = new Sifre();
		}else {
			sifra = (Sifre)podatak;
		}
		sifra.setSistemPretplatnici(pretplatnici.getValue());
		sifra.setOrganizacija(organizacije.getValue());
		sifra.setPartner(partneri.getValue());
		sifra.setSifra(sifraPolje.getValue());
		sifra.setOpis(opis.getValue());
		sifra.setIzbrisan(izbrisan.getValue());
		return sifra;
	}

	@Override
	public void ocistiPodatak() {
		if(!logika.view.korisnik.getSistemPretplatnici().isSistem()) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}
		partneri.clear();
		sifraPolje.clear();
		opis.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Sifre sifra = (Sifre)podatak;
		if(sifra.getId() != null) {
			pretplatnici.setValue(sifra.getSistemPretplatnici());
			organizacije.setValue(sifra.getOrganizacija());
			try {
				partneri.setValue(sifra.getPartner());
			}catch (Exception e) {
				partneri.setValue(null);
			}
			try {
				sifraPolje.setValue(sifra.getSifra());
			}catch (Exception e) {
				sifraPolje.setValue("");
			}
			try {
				opis.setValue(sifra.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			izbrisan.setValue(sifra.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(sifraPolje.getValue() == null || sifraPolje.getValue().isEmpty() || sifraPolje.getValue().equals("")) {
			sveIma = false;
		}
		return sveIma;
	}

}
