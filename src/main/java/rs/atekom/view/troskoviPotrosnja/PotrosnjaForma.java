package rs.atekom.view.troskoviPotrosnja;

import java.sql.Timestamp;
import java.util.Date;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.Troskovi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboGorivo;
import rs.atekom.prati.view.komponente.ComboObjektiSaVozilima;
import rs.atekom.prati.view.komponente.ComboPartneri;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.TekstArea;

public class PotrosnjaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private PotrosnjaLogika logika;
	private TekstArea opis;
	private Tekst brojRacuna;
	private CheckBox izbrisan;
	private Datum datum;
	private ComboGorivo gorivo;
	private ComboPartneri partneri;
	private Decimalni cena, pdvIznos, ukupno, kolicina;
	private Celobrojni pdvProcenat;
	private ComboObjektiSaVozilima objekti;
	
	public PotrosnjaForma(PotrosnjaLogika log) {
		logika = log;
		partneri = new ComboPartneri(logika.view.korisnik, "партнери", true, true);
		datum = new Datum("датум", true);
		objekti = new ComboObjektiSaVozilima(pretplatnici.getValue(), organizacije.getValue(), "објекти", true, true);
		brojRacuna = new Tekst("број рачуна", false);
		gorivo = new ComboGorivo("гориво", true, true);
		kolicina = new Decimalni("количина", true);
		cena = new Decimalni("цена", true);
		pdvProcenat = new Celobrojni("пдв %", false);
		pdvIznos = new Decimalni("пдв износ", false);
		ukupno = new Decimalni("укупно", false);
		opis = new TekstArea("опис");
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				partneri.clear();
				organizacije.clear();
				objekti.clear();
				if(event.getValue() != null) {
					partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(event.getValue(), true));
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					objekti.setItems(Servis.objekatServis.nadjiSveObjekteSavozilom(event.getValue(), null));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				objekti.clear();
				objekti.setItems(Servis.objekatServis.nadjiSveObjekteSavozilom(pretplatnici.getValue(), event.getValue()));
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
		layout.addComponent(datum);
		layout.addComponent(objekti);
		layout.addComponent(brojRacuna);
		layout.addComponent(gorivo);
		layout.addComponent(kolicina);
		layout.addComponent(cena);
		layout.addComponent(pdvProcenat);
		pdvIznos.setEnabled(false);
		layout.addComponent(pdvIznos);
		ukupno.setEnabled(false);
		layout.addComponent(ukupno);
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
		Troskovi trosak;
		if(podatak == null) {
			trosak = new Troskovi();
		}else {
			trosak = (Troskovi)podatak;
			postaviPodatak(trosak);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Troskovi trosak;
		if(podatak == null) {
			trosak = new Troskovi();
		}else {
			trosak = (Troskovi)podatak;
		}
		trosak.setSistemPretplatnici(pretplatnici.getValue());
		trosak.setOrganizacija(null);
		trosak.setPartner(partneri.getValue());
		trosak.setDatumVreme(new Timestamp(dateDatum(datum.getValue()).getTime()));
		trosak.setObjekti(objekti.getValue());
		trosak.setBrojRacuna(brojRacuna.getValue());
		trosak.setTipServisa(0);
		trosak.setSistemGoriva(gorivo.getValue());
		trosak.setKolicina(Float.parseFloat(kolicina.getValue()));
		trosak.setCena(Float.parseFloat(cena.vratiIznos()));
		trosak.setPdvProcenat(Integer.parseInt(pdvProcenat.getValue()));
		trosak.setPdvIznos((trosak.getKolicina() * trosak.getCena() * trosak.getPdvProcenat())/100);
		trosak.setUkupno(trosak.getKolicina() * trosak.getCena() + trosak.getPdvIznos());
		trosak.setOpis(opis.getValue());
		return trosak;
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
		partneri.clear();
		datum.clear();
		objekti.clear();
		brojRacuna.clear();
		gorivo.clear();
		kolicina.setValue(String.valueOf(0));
		cena.setValue(String.valueOf(0));
		pdvProcenat.setValue(String.valueOf(20));
		pdvIznos.setValue(String.valueOf(0));
		ukupno.setValue(String.valueOf(0));
		opis.setValue("");
		izbrisan.setValue(false);
		sacuvaj.setEnabled(true);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		if(trosak.getId() != null) {
			pretplatnici.setValue(trosak.getSistemPretplatnici());
			organizacije.setValue(trosak.getObjekti().getOrganizacija());
			organizacije.setEnabled(false);
			try {
				partneri.setValue(trosak.getPartner());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању партнера!");
				partneri.setValue(null);
			}
			if(trosak.getDatumVreme() != null) {
				datum.setValue(localDatum(new Date(trosak.getDatumVreme().getTime())));
			}
			try {
				objekti.setValue(trosak.getObjekti());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању објекта!");
				objekti.setValue(null);
			}
			try {
				brojRacuna.setValue(trosak.getBrojRacuna());
			}catch (Exception e) {
				brojRacuna.setValue("");
			}
			try {
				gorivo.setValue(trosak.getSistemGoriva());
			}catch (Exception e) {
				gorivo.setValue(null);
			}
			try {
				kolicina.setValue(String.valueOf(trosak.getKolicina()));
			}catch (Exception e) {
				kolicina.setValue(String.valueOf(0));
			}
			try {
				cena.setValue(String.valueOf(trosak.getCena()));
			}catch (Exception e) {
				cena.setValue(String.valueOf(0));
			}
			try {
				pdvProcenat.setValue(String.valueOf(trosak.getPdvProcenat()));
			}catch (Exception e) {
				pdvProcenat.setValue(String.valueOf(0));
			}
			try {
				pdvIznos.setValue(String.valueOf(trosak.getPdvIznos()));
			}catch (Exception e) {
				pdvIznos.setValue(String.valueOf(0));
			}
			try {
				ukupno.setValue(String.valueOf(trosak.getUkupno()));
			}catch (Exception e) {
				ukupno.setValue(String.valueOf(0));
			}
			try {
				opis.setValue(trosak.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			izbrisan.setValue(trosak.isIzbrisan());
			sacuvaj.setEnabled(false);
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
		if(gorivo.getValue() == null) {
			sveIma = false;
		}
		if(cena.getValue() == null || pdvProcenat.getValue() == null || datum.getValue() == null || kolicina.getValue() == null) {
			sveIma = false;
		}
		if(objekti.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
