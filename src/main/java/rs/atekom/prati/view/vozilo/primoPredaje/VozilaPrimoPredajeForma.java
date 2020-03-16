package rs.atekom.prati.view.vozilo.primoPredaje;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.Query;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import pratiBaza.pomocne.StavkaPrijema;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozilaOpremaPrijem;
import pratiBaza.tabele.VozilaPrimoPredaje;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboKorisniciVozaci;
import rs.atekom.prati.view.komponente.combo.ComboOprema;
import rs.atekom.prati.view.komponente.combo.ComboVozila;

public class VozilaPrimoPredajeForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaPrimoPredajeLogika logika;
	private ComboKorisniciVozaci vozacPrijem, vozacPredaja;
	private Tekst administrator, broj, komentar;
	private ComboVozila vozila;
	private CheckBox izbrisan;
	private Datum datum; 
	private Grid<StavkaPrijema> opremaTabela;
	private List<StavkaPrijema> lista;
	private ComboOprema oprema;
	private Celobrojni kolicina;
	private Button dodajStavku, izbrisiStavku;

	public VozilaPrimoPredajeForma(VozilaPrimoPredajeLogika log) {
		logika = log;
		broj = new Tekst("број", false);
		datum = new Datum("датум", true);
		vozila = new ComboVozila(logika.view.korisnik, "возила", true, true);
		vozacPrijem = new ComboKorisniciVozaci(logika.view.korisnik, "возач пријем", true, true);
		vozacPredaja = new ComboKorisniciVozaci(logika.view.korisnik, "возач прeдаја", true, true);
		administrator =  new Tekst("администратор", false);
		administrator.setEnabled(false);
		komentar =  new Tekst("коментар", false);
		oprema = new ComboOprema("опрема", logika.view.korisnik, true, false);
		kolicina = new Celobrojni("количина", false);
		lista = new ArrayList<StavkaPrijema>();
		
		layout.removeComponent(organizacije);
		
		dodajStavku = new Button("додај прему");
		dodajStavku.addStyleName("primary");
		dodajStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				lista = opremaTabela.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
				if(oprema.getValue() != null && Integer.parseInt(kolicina.getValue()) > 0 ) {
					StavkaPrijema stavka = new StavkaPrijema(oprema.getValue(), Integer.parseInt(kolicina.getValue()));
					lista.add(stavka);
					opremaTabela.setItems(lista);
				}else {
					logika.view.pokaziPorukuGreska("морате изабрати опрему и количину већу од 0!");
				}
			}
		});
		
		opremaTabela = new Grid<StavkaPrijema>();
		opremaTabela.setCaption("задужена опрема");
		opremaTabela.setWidth("100%");
		opremaTabela.setHeightByRows(3.0);
		opremaTabela.setHeightMode(HeightMode.ROW);
		opremaTabela.setSelectionMode(SelectionMode.SINGLE);
		opremaTabela.addColumn(oprema -> oprema.getOprema() == null ? "" : oprema.getOprema().getNaziv()).setCaption("назив");
		opremaTabela.addColumn(StavkaPrijema::getKolicina).setCaption("количина");
		
		izbrisiStavku = new Button("избриши опрему");
		izbrisiStavku.addStyleName("cancel");
		izbrisiStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {	
				for(StavkaPrijema stavka : opremaTabela.getSelectedItems()) {
					lista.remove(stavka);
				}
				opremaTabela.setItems(lista);
			}
		});
		
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozila.clear();
				vozacPrijem.clear();
				vozacPredaja.clear();
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
				vozila.setItems(Servis.voziloServis.nadjisvaVozilaPoPretplatniku(event.getValue()));
				vozacPrijem.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
				vozacPredaja.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
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
				vozacPrijem.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
				vozacPredaja.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
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

		layout.addComponent(broj);
		layout.addComponent(datum);
		layout.addComponent(vozila);
		layout.addComponent(vozacPrijem);
		layout.addComponent(vozacPredaja);
		layout.addComponent(administrator);
		layout.addComponent(komentar);
		layout.addComponent(oprema);
		layout.addComponent(kolicina);
		layout.addComponent(dodajStavku);
		layout.addComponent(opremaTabela);
		layout.addComponent(izbrisiStavku);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaPrimoPredaje primoPredaja;
		ocistiPodatak();
		if(podatak == null) {
			primoPredaja = new VozilaPrimoPredaje();
		}else {
			primoPredaja = (VozilaPrimoPredaje)podatak;
			if(primoPredaja.getId() != null) {
				postaviPodatak(primoPredaja);
			}
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozilaPrimoPredaje primoPredaja;
		if(podatak == null) {
			primoPredaja = new VozilaPrimoPredaje();
		}else {
			primoPredaja = (VozilaPrimoPredaje)podatak;
		}
		primoPredaja.setSistemPretplatnici(pretplatnici.getValue());
		//primoPredaja.setOrganizacija(organizacije.getValue());
		primoPredaja.setBroj(broj.getValue());
		try {
			primoPredaja.setDatum(dateDatum(datum.getValue()));
		}catch (Exception e) {
			primoPredaja.setDatum(null);
		}
		primoPredaja.setVozilo(vozila.getValue());
		primoPredaja.setVozacPrijem(vozacPrijem.getValue());
		primoPredaja.setVozacPredaja(vozacPredaja.getValue());
		primoPredaja.setAdministrator(logika.view.korisnik);
		primoPredaja.setKomentar(komentar.getValue());
		primoPredaja.setIzbrisan(izbrisan.getValue());
		logika.opremaStavke = opremaTabela.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
		return primoPredaja;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		/*if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}**/
		broj.clear();
		datum.clear();
		vozila.clear();
		vozacPredaja.clear();
		vozacPrijem.clear();
		administrator.clear();
		komentar.clear();
		izbrisan.setValue(false);
		oprema.clear();
		kolicina.setValue(String.valueOf(0));
		opremaTabela.setItems(new ArrayList<StavkaPrijema>());
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozilaPrimoPredaje primoPredaja = (VozilaPrimoPredaje)podatak;
		if(primoPredaja != null) {
			pretplatnici.setValue(primoPredaja.getSistemPretplatnici());
			//organizacije.setValue(primoPredaja.getOrganizacija());
			try {
				broj.setValue(primoPredaja.getBroj());
			}catch (Exception e) {
				broj.setValue("");
			}
			if(primoPredaja.getDatum() != null) {
				datum.setValue(localDatum(primoPredaja.getDatum()));
			}else {
				datum.setValue(null);
			}
			vozila.setValue(primoPredaja.getVozilo());
			vozacPredaja.setValue(primoPredaja.getVozacPredaja());
			vozacPrijem.setValue(primoPredaja.getVozacPrijem());
			try {
				administrator.setValue(primoPredaja.getAdministrator().toString());
			}catch (Exception e) {
				administrator.setValue("");
			}
			try {
				komentar.setValue(primoPredaja.getKomentar());
			}catch (Exception e) {
				komentar.setValue("");
			}
			izbrisan.setValue(primoPredaja.isIzbrisan());
			ArrayList<VozilaOpremaPrijem> opremaPrijem = Servis.opremaPrijemServis.nadjiVozilaOpremaPredajaPoPP(primoPredaja);
			lista.clear();
			oprema.clear();
			kolicina.setValue(String.valueOf(0));
			for(VozilaOpremaPrijem prijemStavka : opremaPrijem) {
				lista.add(new StavkaPrijema(prijemStavka.getOprema(), prijemStavka.getKolicina()));
			}
			opremaTabela.setItems(lista);
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
			System.out.println("pretplatnika");
		}
		if(vozila.getValue() == null) {
			sveIma = false;
			System.out.println("vozilo");
		}
		if(datum.getValue() == null){
			sveIma = false;
			System.out.println("datum");
		}
		if(vozacPredaja.getValue() == null) {
			sveIma = false;
			System.out.println("v predaja");
		}
		if(vozacPrijem.getValue() == null) {
			sveIma = false;
			System.out.println("v prijem");
		}
		if(vozacPredaja.getValue().equals(vozacPrijem.getValue())) {
			sveIma = false;
			System.out.println("prijem = predaja");
		}
		return sveIma;
	}

}
