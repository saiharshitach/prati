package rs.atekom.prati.view.sim;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Sim;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboOperateri;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Tekst;

public class SimForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private SimLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboOperateri operateri;
	private Tekst uredjaj, broj, iccid, opis;
	private CheckBox aktivan, izbrisan;
	
	public SimForma(SimLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		operateri = new ComboOperateri("оператер", true, true);
		uredjaj = new Tekst("уређај", false);
		uredjaj.setEnabled(false);
		broj = new Tekst("сим број", true);
		iccid = new Tekst("сим иццд", true);
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
		layout.addComponent(operateri);
		layout.addComponent(iccid);
		layout.addComponent(broj);
		layout.addComponent(uredjaj);
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
		ocistiPodatak();
		Sim sim;
		if(podatak == null) {
			sim = new Sim();
		}else {
			sim = (Sim)podatak;
			postaviPodatak(sim);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Sim sim;
		if(podatak == null) {
			sim = new Sim();
		}else {
			sim = (Sim)podatak;
		}
		sim.setSistemPretplatnici(pretplatnici.getValue());
		sim.setOrganizacija(organizacije.getValue());
		sim.setSistemOperateri(operateri.getValue());
		sim.setIccid(iccid.getValue());
		sim.setBroj(broj.getValue());
		sim.setOpis(opis.getValue());
		sim.setAktivno(aktivan.getValue());
		sim.setIzbrisan(izbrisan.getValue());
		return sim;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		operateri.clear();
		iccid.clear();
		broj.clear();
		uredjaj.clear();
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
		Sim sim = (Sim)podatak;
		if(sim.getId() != null) {
			pretplatnici.setValue(sim.getSistemPretplatnici());
			operateri.setValue(sim.getSistemOperateri());
			try {
				iccid.setValue(sim.getIccid());
			}catch (Exception e) {
				iccid.setValue("");
			}
			try {
				broj.setValue(sim.getBroj());
			}catch (Exception e) {
				broj.setValue("");
			}
			try {
				uredjaj.setValue(sim.getUredjaji().getSerijskiBr());
			}catch (Exception e) {
				uredjaj.setValue("");
			}
			try {
				opis.setValue(sim.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			aktivan.setValue(sim.isAktivno());
			organizacije.setValue(sim.getOrganizacija());
			izbrisan.setValue(sim.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(operateri.getValue() == null) {
			sveIma = false;
		}
		if(iccid.isEmpty() || iccid.getValue() == "") {
			sveIma = false;
		}
		if(broj.isEmpty() || broj.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
