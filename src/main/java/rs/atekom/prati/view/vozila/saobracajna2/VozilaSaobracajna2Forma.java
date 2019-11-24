package rs.atekom.prati.view.vozila.saobracajna2;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozilaSaobracajne2;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboSaobracajna;
import rs.atekom.prati.view.komponente.Tekst;

public class VozilaSaobracajna2Forma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaSaobracajna2Logika logika;
	private ComboSaobracajna saobracajna;
	private Tekst vlasnik, adresaVlasnika, jmbgVlasnika, korisnik, adresaKorisnika, dimenzijeGuma, pritisakGume, dimenzijeTovara, odnosSnagaMasa, 
	mestaStajanje, kupljenoDoniranoOd, nabavljenoPoRacunuSert, dobavljacDonator;
	private CheckBox izbrisan;

	public VozilaSaobracajna2Forma(VozilaSaobracajna2Logika log) {
		logika = log;
		saobracajna = new ComboSaobracajna(logika.view.korisnik, "саобраћајна", true, true);
		vlasnik = new Tekst("власник", false);
		adresaVlasnika = new Tekst("адреса власника", false);
		jmbgVlasnika = new Tekst("јмбг власника", false);
		korisnik = new Tekst("корисник", false);
		adresaKorisnika = new Tekst("адреса корисник", false);
		dimenzijeGuma = new Tekst("димензије гуме", false);
		pritisakGume = new Tekst("притисак", false);
		dimenzijeTovara = new Tekst("димензије товара", false);
		odnosSnagaMasa = new Tekst("однос снага/маса", false);
		mestaStajanje = new Tekst("места за стајање", false);
		kupljenoDoniranoOd = new Tekst("купљено/донирано од", false);
		nabavljenoPoRacunuSert = new Tekst("набављено по рачуну/серт", false);
		dobavljacDonator = new Tekst("добављач/донатор", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				saobracajna.clear();
				organizacije.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
					saobracajna.setItems(Servis.saobracajnaServis.nadjiSlobodneSaobracajnePoPretplatniku(event.getValue(), null));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				saobracajna.clear();
				if(event.getValue() != null) {
					saobracajna.setItems(Servis.saobracajnaServis.nadjiSlobodneSaobracajnePoPretplatniku(pretplatnici.getValue(), event.getValue()));
				}else {
					saobracajna.setItems(Servis.saobracajnaServis.nadjiSlobodneSaobracajnePoPretplatniku(pretplatnici.getValue(), null));
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
		
		layout.addComponent(saobracajna);
		layout.addComponent(vlasnik);
		layout.addComponent(adresaVlasnika);
		layout.addComponent(jmbgVlasnika);
		layout.addComponent(korisnik);
		layout.addComponent(adresaKorisnika);
		layout.addComponent(dimenzijeGuma);
		layout.addComponent(pritisakGume);
		layout.addComponent(dimenzijeTovara);
		layout.addComponent(odnosSnagaMasa);
		layout.addComponent(mestaStajanje);
		layout.addComponent(kupljenoDoniranoOd);
		layout.addComponent(nabavljenoPoRacunuSert);
		layout.addComponent(dobavljacDonator);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		VozilaSaobracajne2 saobracajna2;
		if(podatak == null) {
			saobracajna2 = new VozilaSaobracajne2();
		}else {
			saobracajna2 = (VozilaSaobracajne2)podatak;
			postaviPodatak(saobracajna2);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozilaSaobracajne2 saobracajna2;
		if(podatak == null) {
			saobracajna2 = new VozilaSaobracajne2();
		}else {
			saobracajna2 = (VozilaSaobracajne2)podatak;
		}
		saobracajna2.setSistemPretplatnici(pretplatnici.getValue());
		saobracajna2.setOrganizacija(null);
		saobracajna2.setSaobracajna(saobracajna.getValue());
		saobracajna2.setVlasnik(vlasnik.getValue());
		saobracajna2.setAdresaVlasnika(adresaVlasnika.getValue());
		saobracajna2.setJmbgVlasnika(jmbgVlasnika.getValue());
		saobracajna2.setKorisnik(korisnik.getValue());
		saobracajna2.setAdresaKorisnika(adresaKorisnika.getValue());
		saobracajna2.setDimenzijeGuma(dimenzijeGuma.getValue());
		saobracajna2.setPritisakGume(pritisakGume.getValue());
		saobracajna2.setDimenzijeTovara(dimenzijeTovara.getValue());
		saobracajna2.setOdnosSnagaMasa(odnosSnagaMasa.getValue());
		saobracajna2.setMestaStajanje(mestaStajanje.getValue());
		saobracajna2.setKupljenoDoniranoOd(kupljenoDoniranoOd.getValue());
		saobracajna2.setNabavljenoPoRacunuSert(nabavljenoPoRacunuSert.getValue());
		saobracajna2.setDobavljacDonator(dobavljacDonator.getValue());
		saobracajna2.setIzbrisan(izbrisan.getValue());
		return saobracajna2;
	}

	@Override
	public void ocistiPodatak() {
		if(!logika.view.korisnik.getSistemPretplatnici().isSistem()) {
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
		saobracajna.clear();
		vlasnik.clear();
		adresaVlasnika.clear();
		jmbgVlasnika.clear();
		korisnik.clear();
		adresaKorisnika.clear();
		dimenzijeGuma.clear();
		pritisakGume.clear();
		dimenzijeTovara.clear();
		odnosSnagaMasa.clear();
		mestaStajanje.clear();
		kupljenoDoniranoOd.clear();
		nabavljenoPoRacunuSert.clear();
		dobavljacDonator.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozilaSaobracajne2 saobracajna2 = (VozilaSaobracajne2)podatak;
		if(saobracajna2.getId() != null) {
			pretplatnici.setValue(saobracajna2.getSistemPretplatnici());
			organizacije.setValue(saobracajna2.getSaobracajna().getVozilo().getOrganizacija());
			organizacije.setEnabled(false);
			saobracajna.setValue(saobracajna2.getSaobracajna());
			try {
				vlasnik.setValue(saobracajna2.getVlasnik());
			}catch (Exception e) {
				vlasnik.setValue("");
			}
			try {
				adresaVlasnika.setValue(saobracajna2.getAdresaVlasnika());
			}catch (Exception e) {
				adresaVlasnika.setValue("");
			}
			try {
				jmbgVlasnika.setValue(saobracajna2.getJmbgVlasnika());
			}catch (Exception e) {
				jmbgVlasnika.setValue("");
			}
			try {
				korisnik.setValue(saobracajna2.getKorisnik());
			}catch (Exception e) {
				korisnik.setValue("");
			}
			try {
				adresaKorisnika.setValue(saobracajna2.getAdresaKorisnika());
			}catch (Exception e) {
				adresaKorisnika.setValue("");
			}
			try {
				dimenzijeGuma.setValue(saobracajna2.getDimenzijeGuma());
			}catch (Exception e) {
				dimenzijeGuma.setValue("");
			}
			try {
				pritisakGume.setValue(saobracajna2.getPritisakGume());
			}catch (Exception e) {
				pritisakGume.setValue("");
			}
			try {
				dimenzijeTovara.setValue(saobracajna2.getDimenzijeTovara());
			}catch (Exception e) {
				dimenzijeTovara.setValue("");
			}
			try {
				odnosSnagaMasa.setValue(saobracajna2.getOdnosSnagaMasa());
			}catch (Exception e) {
				korisnik.setValue("");
			}
			try {
				mestaStajanje.setValue(saobracajna2.getMestaStajanje());
			}catch (Exception e) {
				mestaStajanje.setValue("");
			}
			try {
				kupljenoDoniranoOd.setValue(saobracajna2.getKupljenoDoniranoOd());
			}catch (Exception e) {
				kupljenoDoniranoOd.setValue("");
			}
			try {
				nabavljenoPoRacunuSert.setValue(saobracajna2.getNabavljenoPoRacunuSert());
			}catch (Exception e) {
				nabavljenoPoRacunuSert.setValue("");
			}
			try {
				dobavljacDonator.setValue(saobracajna2.getDobavljacDonator());
			}catch (Exception e) {
				dobavljacDonator.setValue("");
			}
			izbrisan.setValue(saobracajna2.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(saobracajna.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
