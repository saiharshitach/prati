package rs.cybertrade.prati.view.uredjaji;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Uredjaji;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.OrganizacijeCombo;
import rs.cybertrade.prati.view.komponente.PretplatniciCombo;
import rs.cybertrade.prati.view.komponente.Tekst;
import rs.cybertrade.prati.view.komponente.UredjajiModeliCombo;

public class UredjajiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private UredjajiLogika logika;
	private PretplatniciCombo pretplatnici;
	private OrganizacijeCombo organizacije;
	private UredjajiModeliCombo modeli;
	private Tekst kod, serBroj, opis, sim, sim2, objekat;
	private CheckBox aktivan, izbrisan;

	public UredjajiForma(UredjajiLogika log) {
		logika = log;
		pretplatnici = new PretplatniciCombo("претплатник", true, true);
		organizacije = new OrganizacijeCombo(pretplatnici.getValue(), "организација", true, false);
		modeli = new UredjajiModeliCombo("модели уређаја", true, true);
		kod = new Tekst("kod", true);
		serBroj = new Tekst("серијски број", true);
		objekat = new Tekst("објекат", false);
		objekat.setEnabled(false);
		sim = new Tekst("сим", false);
		sim.setEnabled(false);
		sim2 = new Tekst("сим2", false);
		sim2.setEnabled(false);
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
		layout.addComponent(modeli);
		layout.addComponent(kod);
		layout.addComponent(serBroj);
		layout.addComponent(objekat);
		layout.addComponent(sim);
		layout.addComponent(sim2);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
		if(logika.view.isAdmin() || logika.view.korisnik.getOrganizacija() == null) {
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
		}
		uredjaj.setSistemPretplatnici(pretplatnici.getValue());
		uredjaj.setOrganizacija(organizacije.getValue());
		uredjaj.setSistemUredjajiModeli(modeli.getValue());
		uredjaj.setKod(kod.getValue());
		uredjaj.setSerijskiBr(serBroj.getValue());
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
				sim.setValue(uredjaj.getSim().getIccid());
			}catch (Exception e) {
				sim.setValue("");
			}
			try {
				sim2.setValue(uredjaj.getSim2().getIccid());
			}catch (Exception e) {
				sim2.setValue("");
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
