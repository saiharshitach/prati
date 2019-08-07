package rs.atekom.prati.view.uredjaji;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Sim;
import pratiBaza.tabele.SistemUredjajiModeli;
import pratiBaza.tabele.Uredjaji;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.ComboSim;
import rs.atekom.prati.view.komponente.ComboUredjajiModeli;
import rs.atekom.prati.view.komponente.Tekst;

public class UredjajiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private UredjajiLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboUredjajiModeli modeli;
	private ComboSim sim, sim2;
	private Tekst kod, serBroj, opis, objekat;
	private CheckBox aktivan, izbrisan;

	public UredjajiForma(UredjajiLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		modeli = new ComboUredjajiModeli("модели уређаја", true, true);
		kod = new Tekst("kod", true);
		serBroj = new Tekst("серијски број", true);
		objekat = new Tekst("објекат", false);
		objekat.setEnabled(false);
		sim = new ComboSim(pretplatnici.getValue(), organizacije.getValue(), null, "сим", true, false);
		sim2 = new ComboSim(pretplatnici.getValue(), organizacije.getValue(), null, "сим2", true, false);
		modeli.addValueChangeListener(new ValueChangeListener<SistemUredjajiModeli>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemUredjajiModeli> event) {
				if(event == null) {
					sim2.setEnabled(false);
				}else {
					if(event.getValue() == null) {
						sim2.setEnabled(false);
					}else {
						if(event.getValue().isSim2()) {
							sim2.setEnabled(true);
						}else {
							sim2.setEnabled(false);
						}
					}
				}
			}
		});
		
		aktivan = new CheckBox("активан");
		opis = new Tekst("опис", false);
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
								logika.sacuvajPodatak(sacuvajPodatak(logika.view.dajIzabraniRed()));
								sim.setItems(sim.lista(pretplatnici.getValue(), organizacije.getValue(), null));
								sim2.setItems(sim2.lista(pretplatnici.getValue(), organizacije.getValue(), null));
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
							sim.setItems(sim.lista(pretplatnici.getValue(), organizacije.getValue(), null));
							sim2.setItems(sim2.lista(pretplatnici.getValue(), organizacije.getValue(), null));
						}
					}
				});
			}
		});
		
		if(logika.view.korisnik.isSistem() && logika.view.korisnik.getSistemPretplatnici() == null) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(modeli);
		layout.addComponent(kod);
		layout.addComponent(serBroj);
		layout.addComponent(objekat);
		layout.addComponent(sim);
		layout.addComponent(sim2);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
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
		Uredjaji uredjaj;
		if(podatak == null) {
			uredjaj = new Uredjaji();
		}else {
			uredjaj = (Uredjaji)podatak;
			postaviPodatak(uredjaj);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Uredjaji uredjaj;
		if(podatak == null) {
			uredjaj = new Uredjaji();
		}else {
			uredjaj = (Uredjaji)podatak;
			Sim sim = uredjaj.getSim();
			if(sim != null) {
				sim.setUredjaji(null);
				sim.setZauzet(false);
				Servis.simServis.azurirajSim(sim);
			}
			Sim sim2 = uredjaj.getSim2();
			if(sim2 != null) {
				sim2.setUredjaji(null);
				sim2.setZauzet(false);
				Servis.simServis.azurirajSim(sim2);
			}
		}
		uredjaj.setSistemPretplatnici(pretplatnici.getValue());
		uredjaj.setOrganizacija(organizacije.getValue());
		uredjaj.setSistemUredjajiModeli(modeli.getValue());
		uredjaj.setKod(kod.getValue());
		uredjaj.setSerijskiBr(serBroj.getValue());
		uredjaj.setSim(sim.getValue());
		uredjaj.setSim2(sim2.getValue());
		uredjaj.setOpis(opis.getValue());
		uredjaj.setAktivno(aktivan.getValue());
		uredjaj.setIzbrisan(izbrisan.getValue());
		return uredjaj;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		modeli.clear();
		kod.clear();
		serBroj.clear();
		objekat.clear();
		sim.clear();
		sim2.clear();
		sim2.setEnabled(false);
		opis.clear();
		aktivan.setValue(true);
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Uredjaji uredjaj = (Uredjaji)podatak;
		if(uredjaj.getId() != null) {
			pretplatnici.setValue(uredjaj.getSistemPretplatnici());
			modeli.setValue(uredjaj.getSistemUredjajiModeli());
			try {
				kod.setValue(uredjaj.getKod());
			}catch (Exception e) {
				kod.setValue("");
			}
			try {
				serBroj.setValue(uredjaj.getSerijskiBr());
			}catch (Exception e) {
				serBroj.setValue("");
			}
			try {
				objekat.setValue(uredjaj.getObjekti().getOznaka());
			}catch (Exception e) {
				objekat.setValue("");
			}
			try {
				Sim simKartica = uredjaj.getSim();
				sim.setItems(Servis.simServis.vratiSveAktivneSimKartice(uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), simKartica));
				sim.setSelectedItem(simKartica);
			}catch (Exception e) {
				sim.setItems(Servis.simServis.vratiSveAktivneSimKartice(uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), null));
			}
			try {
				Sim simKartica = uredjaj.getSim2();
				sim2.setItems(Servis.simServis.vratiSveAktivneSimKartice(uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), simKartica));
				sim2.setSelectedItem(simKartica);
			}catch (Exception e) {
				sim2.setItems(Servis.simServis.vratiSveAktivneSimKartice(uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), null));
			}
			if(uredjaj.getSistemUredjajiModeli().isSim2()) {
				sim2.setEnabled(true);
			}else {
				sim2.setEnabled(false);
			}
			try {
				opis.setValue(uredjaj.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			aktivan.setValue(uredjaj.isAktivno());
			organizacije.setValue(uredjaj.getOrganizacija());
			izbrisan.setValue(uredjaj.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(modeli.getValue() == null) {
			sveIma = false;
		}
		if(kod.isEmpty() || kod.getValue() == "") {
			sveIma = false;
		}
		if(serBroj.isEmpty() || serBroj.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
