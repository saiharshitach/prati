package rs.atekom.prati.view.vozila.zbirni;

import java.util.Date;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Racuni;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboKorisnici;
import rs.atekom.prati.view.komponente.ComboPartneri;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.TekstArea;

public class RacuniForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private RacuniLogika logika;
	private ComboPartneri partneri;
	private Tekst brojRacuna;
	private Datum datum;
	private TekstArea opis;
	private ComboKorisnici korisnici;
	private CheckBox izbrisan;
	private Button dodajStavku, ucitajDatoteku;
	
	public RacuniForma(RacuniLogika log) {
		logika = log;
		partneri = new ComboPartneri(logika.view.korisnik,  "партнери", true, true);
		datum = new Datum("датум", true);
		brojRacuna = new Tekst("број рачуна", true);
		opis = new TekstArea("опис");
		korisnici = new ComboKorisnici(logika.view.korisnik, "урадио", true, false);
		izbrisan = new CheckBox("избрисан");
		
		dodajStavku = new Button("додај ставку");
		dodajStavku.addStyleName("primary");
		ucitajDatoteku = new Button("учитај");
		ucitajDatoteku.addStyleName("primary");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				partneri.clear();
				if(event.getValue() != null) {
					partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(event.getValue(), true));
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
		
		dodajStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				logika.otkaziPodatak();
			}
		});
		
		layout.addComponent(partneri);
		layout.addComponent(datum);
		layout.addComponent(brojRacuna);
		layout.addComponent(opis);
		korisnici.setEnabled(false);
		layout.addComponent(korisnici);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		
		layout.addComponent(ucitajDatoteku);
		layout.addComponent(dodajStavku);
		
		dodajExpanderButton();
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		Racuni racun;
		if(podatak == null) {
			racun = new Racuni();
		}else {
			racun = (Racuni)podatak;
			postaviPodatak(racun);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Racuni racun;
		if(podatak == null) {
			racun = new Racuni();
			racun.setUradio(logika.view.korisnik);
		}else {
			racun = (Racuni)podatak;
		}
		racun.setSistemPretplatnici(pretplatnici.getValue());
		racun.setOrganizacija(organizacije.getValue());
		racun.setDatum(dateDatum(datum.getValue()));
		racun.setPartner(partneri.getValue());
		racun.setBrojRacuna(brojRacuna.getValue());
		racun.setOpis(opis.getValue());
		racun.setIzbrisan(izbrisan.getValue());
		return racun;
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
		datum.clear();
		partneri.clear();
		brojRacuna.clear();
		opis.setValue("");
		korisnici.setValue(logika.view.korisnik);
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Racuni racun = (Racuni)podatak;
		if(racun.getId() != null) {
			pretplatnici.setValue(racun.getSistemPretplatnici());
			organizacije.setValue(racun.getOrganizacija());
			try {
				partneri.setValue(racun.getPartner());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању партнера!");
				partneri.setValue(null);
			}
			if(racun.getDatum() != null) {
				datum.setValue(localDatum(new Date(racun.getDatum().getTime())));
			}
			try {
				brojRacuna.setValue(racun.getBrojRacuna());
			}catch (Exception e) {
				brojRacuna.setValue("");
			}
			try {
				opis.setValue(racun.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			try {
				korisnici.setValue(racun.getUradio());
			}catch (Exception e) {
				korisnici.setValue(null);
			}
			izbrisan.setValue(racun.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(partneri.getValue() == null) {
			sveIma = false;
		}
		if(brojRacuna.getValue().isEmpty() || brojRacuna.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
