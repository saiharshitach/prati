package rs.atekom.prati.view.vozila;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Vozila;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboGorivo;
import rs.atekom.prati.view.komponente.ComboObjekti;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;

public class VozilaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaLogika logika;
	private Tekst registracija, marka, model, tip, brojSaobracajne, serijskiBroj;
	private CheckBox teretno, izbrisan;
	private ComboObjekti objekti;
	private ComboGorivo gorivo;
	private Celobrojni godina, rezervoar;
	private Decimalni potrosnja;
	private Datum datumPrveRegistracije;

	public VozilaForma(VozilaLogika log) {
		logika = log;
		objekti = new ComboObjekti(logika.view.korisnik, "објекти", true, true);
		registracija = new Tekst("регистрација", true);
		marka = new Tekst("марка", true);
		model = new Tekst("модел", true);
		tip = new Tekst("тип", false);
		godina = new Celobrojni("година", true);
		gorivo = new ComboGorivo("гориво", true, false);
		rezervoar = new Celobrojni("резервоар", false);
		potrosnja = new Decimalni("просечна потрошња", false);
		teretno = new CheckBox("теретно");
		brojSaobracajne = new Tekst("број саобраћајне", false);
		serijskiBroj = new Tekst("серијски број",false);
		datumPrveRegistracije = new Datum("датум прве регистрације", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				objekti.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					objekti.setItems(Servis.objekatServis.vratiObjektePoPretplatniku(event.getValue(), organizacije.getValue(), true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				objekti.clear();
				if(event.getValue() != null) {
					objekti.setItems(Servis.objekatServis.vratiObjektePoPretplatniku(pretplatnici.getValue(), event.getValue(),true));
				}else {
					objekti.setItems(Servis.objekatServis.vratiObjektePoPretplatniku(pretplatnici.getValue(), null, true));
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

		layout.addComponent(objekti);
		layout.addComponent(registracija);
		layout.addComponent(marka);
		layout.addComponent(model);
		layout.addComponent(tip);
		layout.addComponent(godina);
		layout.addComponent(gorivo);
		layout.addComponent(potrosnja);
		layout.addComponent(brojSaobracajne);
		layout.addComponent(serijskiBroj);
		layout.addComponent(datumPrveRegistracije);
		layout.addComponent(teretno);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		Vozila vozilo;
		if(podatak == null) {
			vozilo = new Vozila();
		}else {
			vozilo = (Vozila)podatak;
			postaviPodatak(vozilo);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Vozila vozilo;
		if(podatak == null) {
			vozilo = new Vozila();
		}else {
			vozilo = (Vozila)podatak;
		}
		vozilo.setSistemPretplatnici(pretplatnici.getValue());
		vozilo.setOrganizacija(null);
		vozilo.setObjekti(objekti.getValue());
		vozilo.setRegistracija(registracija.getValue());
		vozilo.setMarka(marka.getValue());
		vozilo.setModel(model.getValue());
		vozilo.setTip(tip.getValue());
		vozilo.setGodina(Integer.parseInt(godina.getValue()));
		vozilo.setSistemGoriva(gorivo.getValue());
		try {
			vozilo.setRezervoar(Integer.parseInt(rezervoar.getValue()));
		}catch (Exception e) {
			vozilo.setRezervoar(0);
		}
		try {
			vozilo.setPotrosnja(Float.parseFloat(potrosnja.getValue()));
		}catch (Exception e) {
			vozilo.setPotrosnja(0.0f);
		}
		vozilo.setTeretno(teretno.getValue());
		vozilo.setBrojSaobracajne(brojSaobracajne.getValue());
		vozilo.setSerijskiBroj(serijskiBroj.getValue());
		try {
			vozilo.setDatumRegistracije(dateDatum(datumPrveRegistracije.getValue()));
		}catch (Exception e) {
			vozilo.setDatumRegistracije(null);
		}
		vozilo.setIzbrisan(izbrisan.getValue());
		return vozilo;
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
		objekti.clear();
		registracija.clear();
		marka.clear();
		model.clear();
		tip.clear();
		godina.clear();
		gorivo.clear();
		rezervoar.clear();
		potrosnja.clear();
		teretno.setValue(false);
		brojSaobracajne.clear();
		serijskiBroj.clear();
		datumPrveRegistracije.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Vozila vozilo = (Vozila)podatak;
		if(vozilo.getId() != null) {
			pretplatnici.setValue(vozilo.getSistemPretplatnici());
			organizacije.setValue(vozilo.getObjekti().getOrganizacija());
			organizacije.setEnabled(false);
			try {
				objekti.setValue(vozilo.getObjekti());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању објекта!");
				objekti.setValue(null);
			}
			try {
				registracija.setValue(vozilo.getRegistracija());
			}catch (Exception e) {
				registracija.setValue("");
			}
			try {
				marka.setValue(vozilo.getMarka());
			}catch (Exception e) {
				marka.setValue("");
			}
			try {
				model.setValue(vozilo.getModel());
			}catch (Exception e) {
				model.setValue("");
			}
			try {
				tip.setValue(vozilo.getTip());
			}catch (Exception e) {
				tip.setValue("");
			}
			try {
				godina.setValue(String.valueOf(vozilo.getGodina()));
			}catch (Exception e) {
				godina.setValue("");
			}
			gorivo.setValue(vozilo.getSistemGoriva());
			try {
				rezervoar.setValue(String.valueOf(vozilo.getRezervoar()));
			}catch (Exception e) {
				rezervoar.setValue("");
			}
			try {
				potrosnja.setValue(String.valueOf(vozilo.getPotrosnja()));
			}catch (Exception e) {
				potrosnja.setValue("");
			}
			teretno.setValue(vozilo.isTeretno());
			try {
				brojSaobracajne.setValue(vozilo.getBrojSaobracajne());
			}catch (Exception e) {
				brojSaobracajne.setValue("");
			}
			try {
				serijskiBroj.setValue(vozilo.getSerijskiBroj());
			}catch (Exception e) {
				serijskiBroj.setValue("");
			}
			if(vozilo.getDatumRegistracije() != null) {
				datumPrveRegistracije.setValue(localDatum(vozilo.getDatumRegistracije()));
			}else {
				datumPrveRegistracije.setValue(null);
			}
			izbrisan.setValue(vozilo.isIzbrisan());
		}
		
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(objekti.getValue() == null) {
			sveIma = false;
		}
		if(registracija.isEmpty() || registracija.getValue().isEmpty()) {
			sveIma = false;
		}
		if(marka.isEmpty() || marka.getValue().isEmpty()) {
			sveIma = false;
		}
		if(model.isEmpty() || model.getValue().isEmpty()) {
			sveIma = false;
		}
		if(godina.isEmpty() || godina.getValue().isEmpty()) {
			sveIma = false;
		}
		return sveIma;
	}

}
