package rs.atekom.prati.view.vozila;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Vozila;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.ObdPoslednji;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboGorivo;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

public class VozilaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaLogika logika;
	private Tekst registracija, marka, model, tip, brojSaobracajne, serijskiBroj;
	private CheckBox teretno, izbrisan;
	private ComboObjekti objekti;
	private ComboGorivo gorivo;
	private Celobrojni godina, rezervoar, maliServisKm, maliServisMeseci, velikiServisKm, velikiServisMeseci, maliPoslednjiOBDkm, velikiPoslednjiOBDkm;
	private Decimalni potrosnja, maliPoslednjiGPSkm, velikiPoslednjiGPSkm;
	private Datum datumPrveRegistracije, datumPoslednjeRegistracije, maliPoslednjiDatum, velikiPoslednjiDatum;

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
		maliServisKm = new Celobrojni("мали сервис - км", false);
		maliServisMeseci = new Celobrojni("мали сервис - месеци", false);
		velikiServisKm = new Celobrojni("велики сервис - км", false);
		velikiServisMeseci = new Celobrojni("велики сервис - месеци", false);
		
		datumPoslednjeRegistracije= new Datum("датум последње регистрације", false);
		datumPoslednjeRegistracije.setEnabled(false);
		maliPoslednjiDatum = new Datum("датум последњег МС", false);
		maliPoslednjiDatum.setEnabled(false);
		maliPoslednjiGPSkm = new Decimalni("гпс км последњи МС", false);
		maliPoslednjiGPSkm.setEnabled(false);
		maliPoslednjiOBDkm = new Celobrojni("обд км последњи МС", false);
		maliPoslednjiOBDkm.setEnabled(false);
		velikiPoslednjiDatum = new Datum("датум последњег ВС", false);
		velikiPoslednjiDatum.setEnabled(false);
		velikiPoslednjiGPSkm = new Decimalni("гпс км последњи ВС", false);
		velikiPoslednjiGPSkm.setEnabled(false);
		velikiPoslednjiOBDkm = new Celobrojni("обд км последњи ВС", false);
		velikiPoslednjiOBDkm.setEnabled(false);
		
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				objekti.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					objekti.setItems(Servis.objekatServis.nadjiSveObjekteBezVozila(event.getValue(), null));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				objekti.clear();
				objekti.setItems(Servis.objekatServis.nadjiSveObjekteBezVozila(pretplatnici.getValue(), event.getValue()));
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
		layout.addComponent(maliServisKm);
		layout.addComponent(maliServisMeseci);
		layout.addComponent(velikiServisKm);
		layout.addComponent(velikiServisMeseci);
		layout.addComponent(datumPoslednjeRegistracije);
		layout.addComponent(maliPoslednjiDatum);
		layout.addComponent(maliPoslednjiGPSkm);
		layout.addComponent(maliPoslednjiOBDkm);
		layout.addComponent(velikiPoslednjiDatum);
		layout.addComponent(velikiPoslednjiGPSkm);
		layout.addComponent(velikiPoslednjiOBDkm);
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
			vozilo.setPotrosnja(Float.parseFloat(potrosnja.vratiIznos()));
		}catch (Exception e) {
			vozilo.setPotrosnja(0.0f);
		}
		vozilo.setBrojSaobracajne(brojSaobracajne.getValue());
		vozilo.setSerijskiBroj(serijskiBroj.getValue());
		try {
			vozilo.setDatumRegistracije(dateDatum(datumPrveRegistracije.getValue()));
		}catch (Exception e) {
			vozilo.setDatumRegistracije(null);
		}
		vozilo.setMaliServisKm(Integer.parseInt(maliServisKm.getValue()));
		vozilo.setMaliServisMeseci(Integer.parseInt(maliServisMeseci.getValue()));
		vozilo.setVelikiServisKm(Integer.parseInt(velikiServisKm.getValue()));
		vozilo.setVelikiServisMeseci(Integer.parseInt(velikiServisMeseci.getValue()));
		vozilo.setTeretno(teretno.getValue());
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
		objekti.setEnabled(true);
		
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
		maliServisKm.setValue(String.valueOf(10000));
		maliServisMeseci.setValue(String.valueOf(6));
		velikiServisKm.setValue(String.valueOf(50000));
		velikiServisMeseci.setValue(String.valueOf(24));
		datumPoslednjeRegistracije.clear();
		maliPoslednjiDatum.clear();
		maliPoslednjiGPSkm.clear();
		maliPoslednjiOBDkm.clear();
		velikiPoslednjiDatum.clear();
		velikiPoslednjiGPSkm.clear();
		velikiPoslednjiOBDkm.clear();
		
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Vozila vozilo = (Vozila)podatak;
		if(vozilo.getId() != null) {
			JavljanjaPoslednja jp = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(vozilo.getObjekti());
			ObdPoslednji op = Servis.obdPoslednjiServis.nadjiObdPoslednjiPoObjektu(vozilo.getObjekti());
			if(jp != null) {
				vozilo.setKmOdGpsMs(jp.getVirtualOdo() - vozilo.getMaliPoslednjiGPSkm());
				vozilo.setKmOdGpsVs(jp.getVirtualOdo() - vozilo.getVelikiPoslednjiGPSkm());
			}
			if(op != null) {
				vozilo.setKmOdObdMs(op.getUkupnoKm() - vozilo.getMaliPoslednjiOBDkm());
				vozilo.setKmOdObdVs(op.getUkupnoKm() - vozilo.getVelikiPoslednjiOBDkm());
			}
			if(vozilo.getMaliPoslednjiDatum() != null)
				vozilo.setDanaOdMs((int)Duration.between(vozilo.getMaliPoslednjiDatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), 
						(new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toDays());
			if(vozilo.getVelikiPoslednjiDatum() != null)
				vozilo.setDanaOdVs((int)Duration.between(vozilo.getVelikiPoslednjiDatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), 
						(new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toDays());
			if(vozilo.getDatumPoslednjeRegistracije() != null)
				vozilo.setDanaOdRegistracije((int)Duration.between(vozilo.getDatumPoslednjeRegistracije().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), 
						(new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toDays());
			
			pretplatnici.setValue(vozilo.getSistemPretplatnici());
			organizacije.setValue(vozilo.getObjekti().getOrganizacija());
			organizacije.setEnabled(false);
			try {
				objekti.setValue(vozilo.getObjekti());
				if(vozilo.getObjekti() != null) {
					objekti.setEnabled(false);
				}else {
					objekti.setEnabled(true);
				}
				
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
			
			try {
				maliServisKm.setValue(String.valueOf(vozilo.getMaliServisKm()));
			}catch (Exception e) {
				maliServisKm.setValue("");
			}
			try {
				maliServisMeseci.setValue(String.valueOf(vozilo.getMaliServisMeseci()));
			}catch (Exception e) {
				maliServisMeseci.setValue("");
			}
			try {
				velikiServisKm.setValue(String.valueOf(vozilo.getVelikiServisKm()));
			}catch (Exception e) {
				velikiServisKm.setValue("");
			}
			try {
				velikiServisMeseci.setValue(String.valueOf(vozilo.getVelikiServisMeseci()));
			}catch (Exception e) {
				velikiServisMeseci.setValue("");
			}
			
			if(vozilo.getDatumPoslednjeRegistracije() != null) {
				datumPoslednjeRegistracije.setValue(localDatum(vozilo.getDatumPoslednjeRegistracije()));
			}else {
				datumPoslednjeRegistracije.setValue(null);
			}
			if(vozilo.getMaliPoslednjiDatum() != null) {
				maliPoslednjiDatum.setValue(localDatum(vozilo.getMaliPoslednjiDatum()));
			}else {
				maliPoslednjiDatum.setValue(null);
			}
			try {
				maliPoslednjiGPSkm.setValue(String.valueOf(vozilo.getMaliPoslednjiGPSkm()));
			}catch (Exception e) {
				maliPoslednjiGPSkm.setValue("");
			}
			try {
				maliPoslednjiOBDkm.setValue(String.valueOf(vozilo.getMaliPoslednjiOBDkm()));
			}catch (Exception e) {
				maliPoslednjiGPSkm.setValue("");
			}
			if(vozilo.getVelikiPoslednjiDatum() != null) {
				velikiPoslednjiDatum.setValue(localDatum(vozilo.getVelikiPoslednjiDatum()));
			}else {
				velikiPoslednjiDatum.setValue(null);
			}
			try {
				velikiPoslednjiGPSkm.setValue(String.valueOf(vozilo.getVelikiPoslednjiGPSkm()));
			}catch (Exception e) {
				velikiPoslednjiGPSkm.setValue("");
			}
			try {
				velikiPoslednjiOBDkm.setValue(String.valueOf(vozilo.getVelikiPoslednjiOBDkm()));
			}catch (Exception e) {
				velikiPoslednjiGPSkm.setValue("");
			}
			
			teretno.setValue(vozilo.isTeretno());
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
