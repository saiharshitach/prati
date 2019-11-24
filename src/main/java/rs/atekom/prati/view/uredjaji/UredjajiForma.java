package rs.atekom.prati.view.uredjaji;

import java.util.ArrayList;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.Sim;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.SistemUredjajiModeli;
import pratiBaza.tabele.Uredjaji;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboSim;
import rs.atekom.prati.view.komponente.ComboUredjajiModeli;
import rs.atekom.prati.view.komponente.Tekst;

public class UredjajiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private UredjajiLogika logika;
	private ComboUredjajiModeli modeli;
	private ComboSim sim, sim2;
	private Tekst kod, serBroj, opis, objekat;
	private CheckBox aktivan, izbrisan;

	public UredjajiForma(UredjajiLogika log) {
		logika = log;
		modeli = new ComboUredjajiModeli("модели уређаја", true, true);
		kod = new Tekst("kod", true);
		serBroj = new Tekst("серијски број", true);
		objekat = new Tekst("објекат", false);
		objekat.setEnabled(false);
		sim = new ComboSim(pretplatnici.getValue(), organizacije.getValue(), null, "сим", true, false);
		sim2 = new ComboSim(pretplatnici.getValue(), organizacije.getValue(), null, "сим2", true, false);
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				if(logika.view.korisnik.getSistemPretplatnici().isSistem()) {
					organizacije.clear();
					sim.clear();
					sim2.clear();
					if(event.getValue() != null) {
						organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
						ArrayList<Sim> lista = Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, event.getValue(), null, null);
						sim.setItems(lista);
						sim2.setItems(lista);
					}
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				sim.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, null, event.getValue(), null));
				sim2.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, null, event.getValue(), null));
			}
		});
		
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
								ArrayList<Sim> lista = Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, pretplatnici.getValue(), organizacije.getValue(), null);
								sim.setItems(lista);
								sim2.setItems(lista);
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
							ArrayList<Sim> lista = Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, pretplatnici.getValue(), organizacije.getValue(), null);
							sim.setItems(lista);
							sim2.setItems(lista);
						}
					}
				});
			}
		});
		
		layout.addComponent(modeli);
		layout.addComponent(kod);
		layout.addComponent(serBroj);
		layout.addComponent(objekat);
		layout.addComponent(sim);
		layout.addComponent(sim2);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
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
				//sim.setUredjaji(null);
				//sim.setZauzet(false);
				Servis.simServis.azurirajSim(uredjaj.removeSim(sim));
				
			}
			Sim sim2 = uredjaj.getSim2();
			if(sim2 != null) {
				//sim2.setUredjaji(null);
				//sim2.setZauzet(false);
				Servis.simServis.azurirajSim(uredjaj.removeSim(sim2));
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
		pretplatnici.setEnabled(true);
		modeli.clear();
		kod.clear();
		serBroj.clear();
		objekat.clear();
		sim.clear();
		sim.setEnabled(true);
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
			organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(uredjaj.getSistemPretplatnici(), true));
			organizacije.setValue(uredjaj.getOrganizacija());
			pretplatnici.setEnabled(false);
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
				sim.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), simKartica));
				sim.setSelectedItem(simKartica);
				if(!logika.view.korisnik.getSistemPretplatnici().isSistem() || !korisnik.isSistem()) {
					if(simKartica.getSistemPretplatnici().isSistem()) {
						sim.setEnabled(false);
					}else {
						sim.setEnabled(true);
					}
				}else {
					sim.setEnabled(true);
				}
			}catch (Exception e) {
				sim.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, pretplatnici.getValue(), organizacije.getValue(), null));
			}
			try {
				Sim simKartica = uredjaj.getSim2();
				sim2.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, uredjaj.getSistemPretplatnici(), uredjaj.getOrganizacija(), simKartica));
				sim2.setSelectedItem(simKartica);
				if(!logika.view.korisnik.getSistemPretplatnici().isSistem() || !korisnik.isSistem()) {
					if(simKartica.getSistemPretplatnici().isSistem()) {
						sim2.setEnabled(false);
					}else {
						sim.setEnabled(true);
					}
				}else {
					sim.setEnabled(true);
				}
			}catch (Exception e) {
				sim2.setItems(Servis.simServis.vratiSveSlobodneSim(logika.view.korisnik, pretplatnici.getValue(), organizacije.getValue(), null));
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
		if(sim.getValue() != null && sim2.getValue() != null) {
			if(sim.getValue().getId().equals(sim2.getValue().getId())){
				sveIma = false;
			}
		}
		return sveIma;
	}

}
