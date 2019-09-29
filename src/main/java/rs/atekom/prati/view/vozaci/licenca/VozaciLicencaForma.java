package rs.atekom.prati.view.vozaci.licenca;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozaciLicence;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboVozaci;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;

public class VozaciLicencaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozaciLicencaLogika logika;
	private ComboPretplatnici pretplatnici;
	//private ComboOrganizacije organizacije;
	private ComboVozaci vozaci;
	private Tekst broj, izdao;
	private Datum izdato, vaziDo;
	private CheckBox izbrisan;

	public VozaciLicencaForma(VozaciLicencaLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		//organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		vozaci = new ComboVozaci(logika.view.korisnik, "возач", true, true);
		broj = new Tekst("број", true);
		izdao = new Tekst("издао", false);
		izdato = new Datum("издато", true);
		vaziDo = new Datum("важеће до", true);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				//organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
			}
		});
		
		/*organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				
			}
		});**/
		
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
		/*if(logika.view.korisnik.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}**/
		layout.addComponent(vozaci);
		layout.addComponent(broj);
		layout.addComponent(izdao);
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
		VozaciLicence licna;
		ocistiPodatak();
		if(podatak == null) {
			licna = new VozaciLicence();
		}else {
			licna = (VozaciLicence)podatak;
			postaviPodatak(licna);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozaciLicence licenca;
		if(podatak == null) {
			licenca = new VozaciLicence();
		}else {
			licenca = (VozaciLicence)podatak;
		}
		licenca.setSistemPretplatnici(pretplatnici.getValue());
		//licenca.setOrganizacija(organizacije.getValue());
		licenca.setVozaci(vozaci.getValue());
		licenca.setBroj(broj.getValue());
		licenca.setIzdao(izdao.getValue());
		
		try {
			licenca.setIzdato(dateDatum(izdato.getValue()));
		}catch (Exception e) {
			licenca.setIzdato(null);
		}
		try {
			licenca.setVaziDo(dateDatum(vaziDo.getValue()));
		}catch (Exception e) {
			licenca.setVaziDo(null);
		}
		licenca.setIzbrisan(izbrisan.getValue());
		return licenca;
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
		vozaci.clear();
		broj.clear();
		izdao.clear();
		izdato.clear();
		vaziDo.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciLicence licenca = (VozaciLicence)podatak;
		if(licenca.getId() != null) {
			pretplatnici.setValue(licenca.getSistemPretplatnici());
			//organizacije.setValue(licenca.getOrganizacija());
			vozaci.setValue(licenca.getVozaci());
			try {
				izdao.setValue(licenca.getIzdao());
			}catch (Exception e) {
				izdao.setValue("");
			}
			try {
				broj.setValue(licenca.getBroj());
			}catch (Exception e) {
				broj.setValue("");
			}
			if(licenca.getIzdato() != null) {
				izdato.setValue(localDatum(licenca.getIzdato()));
			}else {
				izdato.setValue(null);
			}
			if(licenca.getVaziDo() != null) {
				vaziDo.setValue(localDatum(licenca.getVaziDo()));
			}else {
				vaziDo.setValue(null);
			}
			izbrisan.setValue(licenca.isIzbrisan());
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
