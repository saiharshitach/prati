package rs.atekom.prati.view.vozaci.pasosi;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozaciPasosi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboKorisniciVozaci;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;

public class VozaciPasosiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozaciPasosiLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboKorisniciVozaci vozaci;
	private Tekst broj;
	private Datum izdato, vaziDo;
	private CheckBox izbrisan;
	
	public VozaciPasosiForma(VozaciPasosiLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		vozaci = new ComboKorisniciVozaci(logika.view.korisnik, "возач", true, true);
		broj = new Tekst("број", true);
		izdato = new Datum("издато", true);
		vaziDo = new Datum("важеће до", true);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				
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
		
		if(logika.view.korisnik.isSistem() && logika.view.korisnik.getSistemPretplatnici() == null) {
			layout.addComponent(pretplatnici);
		}
		if(logika.view.korisnik.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
		layout.addComponent(vozaci);
		layout.addComponent(broj);
		layout.addComponent(izdato);
		layout.addComponent(vaziDo);
		if(logika.view.isAdmin())  {
			layout.addComponent(izbrisan);
		}
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciPasosi pasos;
		ocistiPodatak();
		if(podatak == null) {
			pasos = new VozaciPasosi();
		}else {
			pasos = (VozaciPasosi)podatak;
			postaviPodatak(pasos);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozaciPasosi pasos;
		if(podatak == null) {
			pasos = new VozaciPasosi();
		}else {
			pasos = (VozaciPasosi)podatak;
		}
		pasos.setSistemPretplatnici(pretplatnici.getValue());
		pasos.setOrganizacija(organizacije.getValue());
		pasos.setKorisnici(vozaci.getValue());
		pasos.setBrojPasosa(broj.getValue());
		
		try {
			pasos.setIzdato(dateDatum(izdato.getValue()));
		}catch (Exception e) {
			pasos.setIzdato(null);
		}
		try {
			pasos.setVaziDo(dateDatum(vaziDo.getValue()));
		}catch (Exception e) {
			pasos.setVaziDo(null);
		}
		pasos.setIzbrisan(izbrisan.getValue());
		return pasos;
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
		vozaci.clear();
		broj.clear();
		izdato.clear();
		vaziDo.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciPasosi pasos = (VozaciPasosi)podatak;
		if(pasos.getId() != null) {
			pretplatnici.setValue(pasos.getSistemPretplatnici());
			organizacije.setValue(pasos.getOrganizacija());
			vozaci.setValue(pasos.getKorisnici());
			try {
				broj.setValue(pasos.getBrojPasosa());
			}catch (Exception e) {
				broj.setValue("");
			}
			if(pasos.getIzdato() != null) {
				izdato.setValue(localDatum(pasos.getIzdato()));
			}else {
				izdato.setValue(null);
			}
			if(pasos.getVaziDo() != null) {
				vaziDo.setValue(localDatum(pasos.getVaziDo()));
			}else {
				vaziDo.setValue(null);
			}
			izbrisan.setValue(pasos.isIzbrisan());
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
