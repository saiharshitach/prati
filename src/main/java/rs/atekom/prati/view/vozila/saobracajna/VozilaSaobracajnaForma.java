package rs.atekom.prati.view.vozila.saobracajna;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozilaSaobracajne;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboVozila;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;

public class VozilaSaobracajnaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaSaobracajnaLogika logika;
	private ComboVozila vozila;
	private Tekst brojSaobracajne, izdao, homologacija, sasija, brojMotora, boja, masa, ukupnaMasa, kategorija, nosivost;
	private Datum datumIzdavanja;
	private Decimalni snagaMotora;
	private Celobrojni zapreminaMotora, zapreminaRezervoara, zapreminaRezervoaraAdBlue, mestaSedenja;
	private CheckBox izbrisan;

	public VozilaSaobracajnaForma(VozilaSaobracajnaLogika log) {
		logika = log;
		vozila = new ComboVozila(logika.view.korisnik, "возило", true, true);
		brojSaobracajne = new Tekst("број саобраћајне", true);
		datumIzdavanja = new Datum("датум издавања", true);
		izdao = new Tekst("издао", true);
		homologacija = new Tekst("хомологација", true);
		sasija = new Tekst("број шасије", true);
		brojMotora = new Tekst("број мотора", true);
		snagaMotora = new Decimalni("снага мотора", false);
		zapreminaMotora = new Celobrojni("запремина мотора", false);
		zapreminaRezervoara = new Celobrojni("запремина резервоара", false);
		zapreminaRezervoaraAdBlue = new Celobrojni("запремина резервоара AdBlue", false);
		boja = new Tekst("боја", false);
		masa = new Tekst("маса/кг", false);
		ukupnaMasa = new Tekst("укупна маса/кг", false);
		kategorija = new Tekst("категорија", false);
		nosivost = new Tekst("носивост", false);
		mestaSedenja = new Celobrojni("места за седење", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				vozila.clear();
				organizacije.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					vozila.setItems(Servis.voziloServis.vratisvaVozila(logika.view.korisnik, true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozila.clear();
				if(event.getValue() != null) {
					vozila.setItems(Servis.voziloServis.nadjisvaVozilaPoOrganizaciji(event.getValue()));
				}else {
					vozila.setItems(Servis.voziloServis.nadjisvaVozilaPoPretplatniku(pretplatnici.getValue()));
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
		
		layout.addComponent(vozila);
		layout.addComponent(brojSaobracajne);
		layout.addComponent(datumIzdavanja);
		layout.addComponent(izdao);
		layout.addComponent(homologacija);
		layout.addComponent(sasija);
		layout.addComponent(brojMotora);
		layout.addComponent(snagaMotora);
		layout.addComponent(zapreminaMotora);
		layout.addComponent(zapreminaRezervoara);
		layout.addComponent(zapreminaRezervoaraAdBlue);
		layout.addComponent(boja);
		layout.addComponent(masa);
		layout.addComponent(ukupnaMasa);
		layout.addComponent(kategorija);
		layout.addComponent(nosivost);
		layout.addComponent(mestaSedenja);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		VozilaSaobracajne saobracajna;
		if(podatak == null) {
			saobracajna = new VozilaSaobracajne();
		}else {
			saobracajna = (VozilaSaobracajne)podatak;
			postaviPodatak(saobracajna);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozilaSaobracajne saobracajna;
		if(podatak == null) {
			saobracajna = new VozilaSaobracajne();
		}else {
			saobracajna = (VozilaSaobracajne)podatak;
		}
		saobracajna.setSistemPretplatnici(pretplatnici.getValue());
		saobracajna.setOrganizacija(null);
		saobracajna.setVozilo(vozila.getValue());
		saobracajna.setBrojSaobracajne(brojSaobracajne.getValue());
		try {
			saobracajna.setDatumIzdavanja(dateDatum(datumIzdavanja.getValue()));
		}catch (Exception e) {
			saobracajna.setDatumIzdavanja(null);
		}
		saobracajna.setIzdao(izdao.getValue());
		saobracajna.setHomologacija(homologacija.getValue());
		saobracajna.setSasija(sasija.getValue());
		saobracajna.setBrojMotora(brojMotora.getValue());
		try {
			saobracajna.setSnagaMotora(Double.parseDouble(snagaMotora.getValue()));
		}catch (Exception e) {
			saobracajna.setSnagaMotora(0.0);
		}
		try {
			saobracajna.setZapreminaMotora(Integer.parseInt(zapreminaMotora.getValue()));
		}catch (Exception e) {
			saobracajna.setZapreminaMotora(0);
		}
		try {
			saobracajna.setZapreminaRezervoara(Integer.parseInt(zapreminaRezervoara.getValue()));
		}catch (Exception e) {
			saobracajna.setZapreminaRezervoara(0);
		}
		try {
			saobracajna.setZapreminaRezervoaraAdBlue(Integer.parseInt(zapreminaRezervoaraAdBlue.getValue()));
		}catch (Exception e) {
			saobracajna.setZapreminaRezervoaraAdBlue(0);
		}
		saobracajna.setBoja(boja.getValue());
		saobracajna.setMasa(masa.getValue());
		saobracajna.setUkupnaMasa(ukupnaMasa.getValue());
		saobracajna.setKategorija(kategorija.getValue());
		saobracajna.setNosivost(nosivost.getValue());
		try {
			saobracajna.setMestaSedenja(Integer.parseInt(mestaSedenja.getValue()));
		}catch (Exception e) {
			saobracajna.setMestaSedenja(0);
		}
		saobracajna.setIzbrisan(izbrisan.getValue());
		return saobracajna;
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
		vozila.clear();
		datumIzdavanja.clear();
		izdao.clear();
		homologacija.clear();
		sasija.clear();
		brojMotora.clear();
		snagaMotora.clear();
		zapreminaMotora.clear();
		zapreminaRezervoara.clear();
		zapreminaRezervoaraAdBlue.clear();
		boja.clear();
		masa.clear();
		ukupnaMasa.clear();
		kategorija.clear();
		nosivost.clear();
		mestaSedenja.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozilaSaobracajne saobracajna = (VozilaSaobracajne)podatak;
		if(saobracajna.getId() != null) {
			pretplatnici.setValue(saobracajna.getSistemPretplatnici());
			organizacije.setValue(saobracajna.getVozilo().getOrganizacija());
			organizacije.setEnabled(false);
			try {
				vozila.setValue(saobracajna.getVozilo());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању возила!");
				vozila.setValue(null);
			}
			try {
				brojSaobracajne.setValue(saobracajna.getBrojSaobracajne());
			}catch (Exception e) {
				brojSaobracajne.setValue("");
			}
			if(saobracajna.getDatumIzdavanja() != null) {
				datumIzdavanja.setValue(localDatum(saobracajna.getDatumIzdavanja()));
			}else {
				datumIzdavanja.setValue(null);
			}
			try {
				izdao.setValue(saobracajna.getIzdao());
			}catch (Exception e) {
				izdao.setValue("");
			}
			try {
				homologacija.setValue(saobracajna.getHomologacija());
			}catch (Exception e) {
				homologacija.setValue("");
			}
			try {
				sasija.setValue(saobracajna.getSasija());
			}catch (Exception e) {
				sasija.setValue("");
			}
			try {
				brojMotora.setValue(saobracajna.getBrojMotora());
			}catch (Exception e) {
				brojMotora.setValue("");
			}
			try {
				snagaMotora.setValue(String.valueOf(saobracajna.getSnagaMotora()));
			}catch (Exception e) {
				snagaMotora.setValue(String.valueOf(0.0));
			}
			try {
				zapreminaMotora.setValue(String.valueOf(saobracajna.getZapreminaMotora()));
			}catch (Exception e) {
				zapreminaMotora.setValue(String.valueOf(0));
			}
			try {
				zapreminaRezervoara.setValue(String.valueOf(saobracajna.getZapreminaRezervoara()));
			}catch (Exception e) {
				zapreminaRezervoara.setValue(String.valueOf(0));
			}
			try {
				zapreminaRezervoaraAdBlue.setValue(String.valueOf(saobracajna.getZapreminaRezervoaraAdBlue()));
			}catch (Exception e) {
				zapreminaRezervoaraAdBlue.setValue(String.valueOf(0));
			}
			try {
				boja.setValue(saobracajna.getBoja());
			}catch (Exception e) {
				sasija.setValue("");
			}
			try {
				masa.setValue(saobracajna.getMasa());
			}catch (Exception e) {
				masa.setValue("");
			}
			try {
				ukupnaMasa.setValue(saobracajna.getUkupnaMasa());
			}catch (Exception e) {
				ukupnaMasa.setValue("");
			}
			try {
				kategorija.setValue(saobracajna.getKategorija());
			}catch (Exception e) {
				kategorija.setValue("");
			}
			try {
				masa.setValue(saobracajna.getMasa());
			}catch (Exception e) {
				masa.setValue("");
			}
			try {
				kategorija.setValue(saobracajna.getKategorija());
			}catch (Exception e) {
				kategorija.setValue("");
			}
			try {
				nosivost.setValue(saobracajna.getNosivost());
			}catch (Exception e) {
				nosivost.setValue("");
			}
			try {
				mestaSedenja.setValue(String.valueOf(saobracajna.getMestaSedenja()));
			}catch (Exception e) {
				mestaSedenja.setValue(String.valueOf(0));
			}
			izbrisan.setValue(saobracajna.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(vozila.getValue() == null) {
			sveIma = false;
		}
		if(brojSaobracajne.getValue() == null || brojSaobracajne.getValue().isEmpty()) {
			
		}
		if(datumIzdavanja.getValue() == null) {
			sveIma = false;
		}
		if(izdao.getValue() == null || izdao.getValue().isEmpty()) {
			sveIma = false;
		}
		if(homologacija.getValue() == null || homologacija.getValue().isEmpty()) {
			sveIma = false;
		}
		if(sasija.getValue() == null || sasija.getValue().isEmpty()) {
			sveIma = false;
		}
		if(brojMotora.getValue() == null || brojMotora.getValue().isEmpty()) {
			sveIma = false;
		}
		return sveIma;
	}

}
