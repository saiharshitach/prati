package rs.cybertrade.prati.view.organizacije;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;

import pratiBaza.tabele.Organizacije;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.ComboPretplatnici;
import rs.cybertrade.prati.view.komponente.Tekst;

public class OrganizacijeForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private OrganizacijeLogika logika;
	private ComboPretplatnici pretplatnici;
	private Tekst naziv, opis;
	private CheckBox aktivan, izbrisan;

	public OrganizacijeForma(OrganizacijeLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатници", true, true);
		naziv = new Tekst("назив", true);
		opis = new Tekst("опис", false);
		aktivan = new CheckBox("активан");
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
		
		if(logika.view.isAdmin()) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(naziv);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
		if(logika.view.isAdmin()) {
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
		Organizacije organizacija;
		ocistiPodatak();
		if(podatak == null) {
			organizacija = new Organizacije();
		}else {
			organizacija = (Organizacije)podatak;
			postaviPodatak(organizacija);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Organizacije organizacija;
		if(podatak == null) {
			organizacija = new Organizacije();
		}else {
			organizacija = (Organizacije)podatak;
		}
		organizacija.setSistemPretplatnici(pretplatnici.getValue());
		organizacija.setNaziv(naziv.getValue());
		organizacija.setOpis(opis.getValue());
		organizacija.setAktivan(aktivan.getValue());
		organizacija.setIzbrisan(izbrisan.getValue());
		return organizacija;
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
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Organizacije organizacija = (Organizacije)podatak;
		if(organizacija.getId() != null) {
			pretplatnici.setValue(organizacija.getSistemPretplatnici());
			naziv.setValue(organizacija.getNaziv());
			opis.setValue(organizacija.getOpis());
			aktivan.setValue(organizacija.isAktivan());
			izbrisan.setValue(organizacija.isIzbrisan());
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
