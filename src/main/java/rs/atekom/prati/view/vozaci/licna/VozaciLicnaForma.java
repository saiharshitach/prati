package rs.atekom.prati.view.vozaci.licna;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozaciLicna;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboKorisniciVozaci;

public class VozaciLicnaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozaciLicnaLogika logika;
	private ComboKorisniciVozaci vozaci;
	private Tekst broj, izdao;
	private Datum izdato, vaziDo;
	private CheckBox izbrisan;

	public VozaciLicnaForma(VozaciLicnaLogika log) {
		logika = log;
		vozaci = new ComboKorisniciVozaci(logika.view.korisnik, "возач", true, true);
		broj = new Tekst("број", true);
		izdao = new Tekst("издао", false);
		izdato = new Datum("издато", true);
		vaziDo = new Datum("важеће до", true);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozaci.clear();
				if(event.getValue() != null) {
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
				}else {
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), null, true));
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

		layout.addComponent(vozaci);
		layout.addComponent(broj);
		layout.addComponent(izdao);
		layout.addComponent(izdato);
		layout.addComponent(vaziDo);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciLicna licna;
		ocistiPodatak();
		if(podatak == null) {
			licna = new VozaciLicna();
		}else {
			licna = (VozaciLicna)podatak;
			postaviPodatak(licna);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozaciLicna licna;
		if(podatak == null) {
			licna = new VozaciLicna();
		}else {
			licna = (VozaciLicna)podatak;
		}
		licna.setSistemPretplatnici(pretplatnici.getValue());
		licna.setOrganizacija(null);
		licna.setVozaci(vozaci.getValue());
		licna.setBroj(broj.getValue());
		licna.setIzdao(izdao.getValue());
		
		try {
			licna.setIzdato(dateDatum(izdato.getValue()));
		}catch (Exception e) {
			licna.setIzdato(null);
		}
		try {
			licna.setVaziDo(dateDatum(vaziDo.getValue()));
		}catch (Exception e) {
			licna.setVaziDo(null);
		}
		licna.setIzbrisan(izbrisan.getValue());
		return licna;
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
		vozaci.clear();
		broj.clear();
		izdao.clear();
		izdato.clear();
		vaziDo.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciLicna licna = (VozaciLicna)podatak;
		if(licna.getId() != null) {
			pretplatnici.setValue(licna.getSistemPretplatnici());
			organizacije.setValue(licna.getVozaci().getOrganizacija());
			organizacije.setEnabled(false);
			vozaci.setValue(licna.getVozaci());
			try {
				izdao.setValue(licna.getIzdao());
			}catch (Exception e) {
				izdao.setValue("");
			}
			try {
				broj.setValue(licna.getBroj());
			}catch (Exception e) {
				broj.setValue("");
			}
			if(licna.getIzdato() != null) {
				izdato.setValue(localDatum(licna.getIzdato()));
			}else {
				izdato.setValue(null);
			}
			if(licna.getVaziDo() != null) {
				vaziDo.setValue(localDatum(licna.getVaziDo()));
			}else {
				vaziDo.setValue(null);
			}
			izbrisan.setValue(licna.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(vozaci.getValue() == null) {
			sveIma = false;
		}
		if(broj.getValue() == null) {
			sveIma = false;
		}
		if(izdato.getValue() == null) {
			sveIma = false;
		}
		if(vaziDo.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
