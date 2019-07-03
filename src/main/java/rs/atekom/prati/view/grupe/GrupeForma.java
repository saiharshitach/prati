package rs.atekom.prati.view.grupe;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Tekst;

public class GrupeForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private GrupeLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private Tekst naziv, opis;
	private CheckBox aktivan, izbrisan;

	public GrupeForma(GrupeLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		naziv = new Tekst("назив", true);
		opis = new Tekst("опис", false);
		aktivan = new CheckBox("активан");
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, true);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
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
		
		if(logika.view.korisnik.isSistem() || logika.view.korisnik.getSistemPretplatnici() == null) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(naziv);
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
		Grupe grupa;
		ocistiPodatak();
		if(podatak == null) {
			grupa = new Grupe();
		}else {
			grupa = (Grupe)podatak;
			postaviPodatak(grupa);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Grupe grupa;
		if(podatak == null) {
			grupa = new Grupe();
		}else {
			grupa = (Grupe)podatak;
		}
		grupa.setSistemPretplatnici(pretplatnici.getValue());
		grupa.setNaziv(naziv.getValue());
		grupa.setOpis(opis.getValue());
		grupa.setAktivan(aktivan.getValue());
		grupa.setOrganizacija(organizacije.getValue());
		grupa.setIzbrisan(izbrisan.getValue());
		return grupa;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		naziv.clear();
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
		Grupe grupa = (Grupe)podatak;
		if(grupa.getId() != null) {
			pretplatnici.setValue(grupa.getSistemPretplatnici());
			try {
				naziv.setValue(grupa.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				opis.setValue(grupa.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			aktivan.setValue(grupa.isAktivan());
			organizacije.setValue(grupa.getOrganizacija());
			izbrisan.setValue(grupa.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
