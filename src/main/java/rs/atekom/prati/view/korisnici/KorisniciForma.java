package rs.atekom.prati.view.korisnici;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.GrupeKorisnici;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.TekstLozinka;

public class KorisniciForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private KorisniciLogika logika;
	private Grid<Grupe> grupeTabela;
	private ArrayList<Grupe> lista;
	private Tekst ime, prezime, ePosta, telefon, mobilni, iDugme;
	private TekstLozinka lozinka;
	private CheckBox aktivan, korisnikCB, vozac, administrator, sistem, izbrisan;
	private Datum aktivanDo;
	
	public KorisniciForma(KorisniciLogika log) {
		logika = log;
		ime = new Tekst("име", true);
		prezime = new Tekst("презиме", true);
		ePosta = new Tekst("е-пошта", true);
		lozinka = new TekstLozinka("лозинка", true);
		aktivan = new CheckBox("активан");
		aktivanDo = new Datum("активан до", false);
		korisnikCB = new CheckBox("корисник");
		vozac = new CheckBox("возач");
		administrator = new CheckBox("администратор");
		telefon = new Tekst("телефон", false);
		mobilni = new Tekst("мобилни", false);
		iDugme = new Tekst("и-дугме", false);
		sistem = new CheckBox("систем");
		
		grupeTabela = new Grid<Grupe>();
		grupeTabela.setCaption("групе");
		grupeTabela.setWidth("100%");
		grupeTabela.setHeightByRows(3.0);
		grupeTabela.setHeightMode(HeightMode.ROW);
		grupeTabela.setSelectionMode(SelectionMode.MULTI);
		grupeTabela.addColumn(grupe -> grupe.getNaziv()).setExpandRatio(1).setCaption("назив");
		grupeTabela.addColumn(Grupe::getOpis).setCaption("опис");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
				popuniTabeluGrupe();
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				popuniTabeluGrupe();
			}
		});
		
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
								logika.grupe = grupeTabela.getSelectedItems();
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

		layout.addComponent(ime);
		layout.addComponent(prezime);
		layout.addComponent(ePosta);
		layout.addComponent(lozinka);
		layout.addComponent(aktivan);
		layout.addComponent(aktivanDo);
		layout.addComponent(korisnikCB);
		layout.addComponent(vozac);
		layout.addComponent(administrator);
		layout.addComponent(telefon);
		layout.addComponent(mobilni);
		layout.addComponent(iDugme);
		if(logika.view.isSistem())  {
			layout.addComponent(sistem);
			layout.addComponent(izbrisan);
		}
		layout.addComponent(grupeTabela);
		dodajExpanderButton();
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Korisnici korisnik;
		ocistiPodatak();
		if(podatak == null) {
			korisnik = new Korisnici();
		}else {
			korisnik = (Korisnici)podatak;
			postaviPodatak(korisnik);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Korisnici korisnik;
		if(podatak == null) {
			korisnik = new Korisnici();
		}else {
			korisnik = (Korisnici)podatak;
		}
		korisnik.setSistemPretplatnici(pretplatnici.getValue());
		korisnik.setIme(ime.getValue());
		korisnik.setPrezime(prezime.getValue());
		korisnik.setEmail(ePosta.getValue());
		korisnik.setLozinka(lozinka.getValue());
		korisnik.setAktivan(aktivan.getValue());
		try {
			korisnik.setAktivanDo(dateDatum(aktivanDo.getValue()));
		}catch (Exception e) {
			korisnik.setAktivanDo(null);
		}
		korisnik.setKorisnik(korisnikCB.getValue());
		korisnik.setVozac(vozac.getValue());
		korisnik.setAdmin(administrator.getValue());
		korisnik.setTelefon(telefon.getValue());
		korisnik.setMobilni(mobilni.getValue());
		korisnik.setIbutton(iDugme.getValue());
		korisnik.setOrganizacija(organizacije.getValue());
		korisnik.setSistem(sistem.getValue());
		korisnik.setIzbrisan(izbrisan.getValue());
		return korisnik;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		ime.clear();
		prezime.clear();
		ePosta.clear();
		lozinka.clear();
		aktivan.setValue(true);
		aktivanDo.clear();
		korisnikCB.setValue(false);
		vozac.setValue(false);
		administrator.setValue(false);
		telefon.clear();
		mobilni.clear();
		iDugme.clear();
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}
		sistem.setValue(false);
		izbrisan.setValue(false);
		grupeTabela.deselectAll();
		popuniTabeluGrupe();
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Korisnici korisnik = (Korisnici)podatak;
		if(korisnik.getId() != null) {
			pretplatnici.setValue(korisnik.getSistemPretplatnici());
			try {
				ime.setValue(korisnik.getIme());
			}catch (Exception e) {
				ime.setValue("");
			}
			try {
				prezime.setValue(korisnik.getPrezime());
			}catch (Exception e) {
				prezime.setValue("");
			}
			try {
				ePosta.setValue(korisnik.getEmail());
			}catch (Exception e) {
				ePosta.setValue("");
			}
			try {
				lozinka.setValue(korisnik.getLozinka());
			}catch (Exception e) {
				lozinka.setValue("");
			}
			aktivan.setValue(korisnik.isAktivan());
			if(korisnik.getAktivanDo() != null) {
				aktivanDo.setValue(localDatum(korisnik.getAktivanDo()));
			}else {
				aktivanDo.setValue(null);
			}
			korisnikCB.setValue(korisnik.isKorisnik());
			vozac.setValue(korisnik.isVozac());
			administrator.setValue(korisnik.isAdmin());
			try {
				telefon.setValue(korisnik.getTelefon());
			}catch (Exception e) {
				telefon.setValue("");
			}
			try {
				mobilni.setValue(korisnik.getMobilni());
			}catch (Exception e) {
				mobilni.setValue("");
			}
			try {
				iDugme.setValue(korisnik.getIbutton());
			}catch (Exception e) {
				iDugme.setValue("");
			}
			organizacije.setValue(korisnik.getOrganizacija());
			sistem.setValue(korisnik.isSistem());
			izbrisan.setValue(korisnik.isIzbrisan());
			ArrayList<GrupeKorisnici> grupeKorisnik = Servis.grupeKorisnikServis.vratiSveGrupeKorisnikPoKorisniku(korisnik);
			for(GrupeKorisnici grKorisnik: grupeKorisnik) {
				for(Grupe grupa: lista) {
					if(grKorisnik.getGrupe().getId().equals(grupa.getId())) {
						grupeTabela.getSelectionModel().select(grupa);
					}
				}
			}
		}
	}
	
	private void popuniTabeluGrupe() {
		lista = new ArrayList<Grupe>();
		lista = Servis.grupeServis.vratiGrupeAktivne(pretplatnici.getValue(), organizacije.getValue());
		grupeTabela.setItems(lista);
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(ime.isEmpty() || ime.getValue() == "") {
			sveIma = false;
		}
		if(prezime.isEmpty() || prezime.getValue() == "") {
			sveIma = false;
		}
		if(ePosta.isEmpty() || ePosta.getValue() == "") {
			sveIma = false;
		}else {
			sveIma = isEmail(ePosta.getValue());
		}
		if(lozinka.isEmpty() || lozinka.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}
	
	private boolean isEmail(String email) {
		 String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                 "[a-zA-Z0-9_+&*-]+)*@" + 
                 "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                 "A-Z]{2,7}$"; 
		 Pattern pat = Pattern.compile(emailRegex); 
		 if (email == null) 
			 return false; 
		 return pat.matcher(email).matches();
	}

}
