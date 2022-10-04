package rs.atekom.view.troskoviOdrzavanje;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
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
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.TekstArea;
import rs.atekom.prati.view.komponente.combo.ComboObjektiSaVozilima;
import rs.atekom.prati.view.komponente.combo.ComboPartneri;
import rs.atekom.prati.view.komponente.combo.ComboTipServisa;
import pratiBaza.pomocne.TipServisa;

public class OdrzavanjaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private OdrzavanjaLogika logika;
	private TekstArea opis;
	private Tekst brojRacuna;
	private CheckBox izbrisan;
	private Datum datum;
	private ComboTipServisa tipServisa;
	private ComboPartneri partneri;
	private Decimalni cena, pdvIznos, ukupno;
	private Celobrojni pdvProcenat;
	private ComboObjektiSaVozilima objekti;
	
	public OdrzavanjaForma(OdrzavanjaLogika log) {
		logika = log;
		partneri = new ComboPartneri(logika.view.korisnik, "партнери", true, true);
		datum = new Datum("датум", true);
		objekti = new ComboObjektiSaVozilima(pretplatnici.getValue(), organizacije.getValue(), "објекти", true, true);
		brojRacuna = new Tekst("број рачуна", false);
		tipServisa = new ComboTipServisa("тип сервиса", false, true);
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
		layout.addComponent(tipServisa);
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
		trosak.setTipServisa(tipServisa.getValue().getRb());
		trosak.setSistemGoriva(null);
		trosak.setKolicina(1.0f);
		trosak.setCena(Float.parseFloat(cena.vratiIznos()));
		trosak.setPdvProcenat(Integer.parseInt(pdvProcenat.getValue()));
		trosak.setPdvIznos(trosak.getCena()*trosak.getPdvProcenat()/100);
		trosak.setUkupno(trosak.getCena() + trosak.getPdvIznos());
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
		tipServisa.clear();
		cena.setValue(String.valueOf(0));
		pdvProcenat.setValue(String.valueOf(0));
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
				TipServisa tip = null;
				ArrayList<TipServisa> lista = tipServisa.lista(false);
				for(TipServisa t : lista) {
					if(trosak.getTipServisa() == t.getRb()) {
						tip = t;
						break;
					}
				}
				tipServisa.setValue(tip);
			}catch (Exception e) {
				tipServisa.setValue(null);
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
		if(tipServisa == null) {
			sveIma = false;
		}
		if(cena.getValue() == null || pdvProcenat == null || datum.getValue() == null) {
			sveIma = false;
		}
		if(datum.getValue().isAfter(LocalDate.now())){
			sveIma = false;
		}
		if(objekti.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
