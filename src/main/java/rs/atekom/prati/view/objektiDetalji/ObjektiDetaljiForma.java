package rs.atekom.prati.view.objektiDetalji;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.ObjektiDetalji;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboGorivo;
import rs.atekom.prati.view.komponente.ComboObjekti;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;

public class ObjektiDetaljiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private ObjektiDetaljiLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private Tekst registracija, marka, model, tip, brojSaobracajne, serijskiBroj;
	private CheckBox teretno, izbrisan;
	private ComboObjekti objekti;
	private ComboGorivo gorivo;
	private Celobrojni godina, rezervoar;
	private Decimalni potrosnja;
	private Datum datumPrveRegistracije;

	public ObjektiDetaljiForma(ObjektiDetaljiLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		objekti = new ComboObjekti(logika.view.korisnik, "објекти", true, true);
		registracija = new Tekst("регистрација", true);
		marka = new Tekst("марка", true);
		model = new Tekst("модел", true);
		tip = new Tekst("тип", false);
		godina = new Celobrojni("година", true);
		gorivo = new ComboGorivo("гориво", true, false);
		rezervoar = new Celobrojni("резервоар", false);
		potrosnja = new Decimalni("просечна потрошња", false);
		teretno = new CheckBox("возило");
		brojSaobracajne = new Tekst("број саобраћајне", false);
		serijskiBroj = new Tekst("серијски број",false);
		datumPrveRegistracije = new Datum("датум прве регистрације", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				if(event != null) {
					if(event.getValue() != null) {
						organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
						organizacije.clear();
						objekti.setItems(Servis.objekatServis.vratiObjektePoPretplatniku(event.getValue(), organizacije.getValue(),true));
						objekti.clear();
					}
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				if(event != null) {
					if(event.getValue() != null) {
						objekti.setItems(Servis.objekatServis.vratiObjektePoPretplatniku(pretplatnici.getValue(), event.getValue(),true));
						objekti.clear();
					}
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
		
		if(logika.view.isAdmin()) {
			layout.addComponent(pretplatnici);
		}
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
		if(logika.view.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
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
		ocistiPodatak();
		ObjektiDetalji objekatDetalj;
		if(podatak == null) {
			objekatDetalj = new ObjektiDetalji();
		}else {
			objekatDetalj = (ObjektiDetalji)podatak;
			postaviPodatak(objekatDetalj);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		ObjektiDetalji objekatDetalj;
		if(podatak == null) {
			objekatDetalj = new ObjektiDetalji();
		}else {
			objekatDetalj = (ObjektiDetalji)podatak;
		}
		objekatDetalj.setSistemPretplatnici(pretplatnici.getValue());
		objekatDetalj.setObjekti(objekti.getValue());
		objekatDetalj.setRegistracija(registracija.getValue());
		objekatDetalj.setMarka(marka.getValue());
		objekatDetalj.setModel(model.getValue());
		objekatDetalj.setTip(tip.getValue());
		objekatDetalj.setGodina(Integer.parseInt(godina.getValue()));
		objekatDetalj.setSistemGoriva(gorivo.getValue());
		try {
			objekatDetalj.setRezervoar(Integer.parseInt(rezervoar.getValue()));
		}catch (Exception e) {
			objekatDetalj.setRezervoar(0);
		}
		try {
			objekatDetalj.setPotrosnja(Float.parseFloat(potrosnja.getValue()));
		}catch (Exception e) {
			objekatDetalj.setPotrosnja(0.0f);
		}
		objekatDetalj.setTeretno(teretno.getValue());
		objekatDetalj.setBrojSaobracajne(brojSaobracajne.getValue());
		objekatDetalj.setSerijskiBroj(serijskiBroj.getValue());
		try {
			objekatDetalj.setDatumRegistracije(dateDatum(datumPrveRegistracije.getValue()));
		}catch (Exception e) {
			objekatDetalj.setDatumRegistracije(null);
		}
		objekatDetalj.setIzbrisan(izbrisan.getValue());
		return objekatDetalj;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
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
		ObjektiDetalji objekatDetalj = (ObjektiDetalji)podatak;
		if(objekatDetalj.getId() != null) {
			pretplatnici.setValue(objekatDetalj.getSistemPretplatnici());
			organizacije.setValue(objekatDetalj.getOrganizacija());
			try {
				objekti.setValue(objekatDetalj.getObjekti());
			}catch (Exception e) {
				logika.view.pokaziPorukuGreska("грешка у преузимању објекта!");
				objekti.setValue(null);
			}
			try {
				registracija.setValue(objekatDetalj.getRegistracija());
			}catch (Exception e) {
				registracija.setValue("");
			}
			try {
				marka.setValue(objekatDetalj.getMarka());
			}catch (Exception e) {
				marka.setValue("");
			}
			try {
				model.setValue(objekatDetalj.getModel());
			}catch (Exception e) {
				model.setValue("");
			}
			try {
				tip.setValue(objekatDetalj.getTip());
			}catch (Exception e) {
				tip.setValue("");
			}
			try {
				godina.setValue(String.valueOf(objekatDetalj.getGodina()));
			}catch (Exception e) {
				godina.setValue("");
			}
			gorivo.setValue(objekatDetalj.getSistemGoriva());
			try {
				rezervoar.setValue(String.valueOf(objekatDetalj.getRezervoar()));
			}catch (Exception e) {
				rezervoar.setValue("");
			}
			try {
				potrosnja.setValue(String.valueOf(objekatDetalj.getPotrosnja()));
			}catch (Exception e) {
				potrosnja.setValue("");
			}
			teretno.setValue(objekatDetalj.isTeretno());
			try {
				brojSaobracajne.setValue(objekatDetalj.getBrojSaobracajne());
			}catch (Exception e) {
				brojSaobracajne.setValue("");
			}
			try {
				serijskiBroj.setValue(objekatDetalj.getSerijskiBroj());
			}catch (Exception e) {
				serijskiBroj.setValue("");
			}
			if(objekatDetalj.getDatumRegistracije() != null) {
				datumPrveRegistracije.setValue(localDatum(objekatDetalj.getDatumRegistracije()));
			}else {
				datumPrveRegistracije.setValue(null);
			}
			izbrisan.setValue(objekatDetalj.isIzbrisan());
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
		if(registracija.isEmpty() || registracija.getValue() == "") {
			sveIma = false;
		}
		if(marka.isEmpty() || marka.getValue() == "") {
			sveIma = false;
		}
		if(model.isEmpty() || model.getValue() == "") {
			sveIma = false;
		}
		if(godina.isEmpty() || godina.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
