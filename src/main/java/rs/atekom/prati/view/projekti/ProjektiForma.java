package rs.atekom.prati.view.projekti;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.Projekti;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboPartneri;
import rs.atekom.prati.view.komponente.Tekst;

public class ProjektiForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private ProjektiLogika logika;
	private ComboPartneri partneri;
	private Tekst naziv, sifra, opis;
	private CheckBox izbrisan;
	
	public ProjektiForma(ProjektiLogika log) {
		logika = log;
		partneri = new ComboPartneri(logika.view.korisnik, "партнери", true, true);
		naziv = new Tekst("назив", true);
		sifra = new Tekst("шифра", false);
		opis = new Tekst("опис", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				partneri.clear();
				organizacije.clear();
				if(event.getValue() != null) {
					partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(event.getValue(), true));
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
				}
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				partneri.clear();
				partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(pretplatnici.getValue(), true));
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
		
		layout.addComponent(naziv);
		layout.addComponent(sifra);
		layout.addComponent(partneri);
		layout.addComponent(opis);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		Projekti projekt;
		if(podatak == null) {
			projekt = new Projekti();
		}else {
			projekt = (Projekti)podatak;
			postaviPodatak(projekt);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Projekti projekt;
		if(podatak == null) {
			projekt = new Projekti();
		}else {
			projekt = (Projekti)podatak;
		}
		projekt.setSistemPretplatnici(pretplatnici.getValue());
		projekt.setOrganizacija(organizacije.getValue());
		projekt.setNaziv(naziv.getValue());
		projekt.setSifra(sifra.getValue());
		projekt.setOpis(opis.getValue());
		projekt.setPartner(partneri.getValue());
		projekt.setIzbrisan(izbrisan.getValue());
		return projekt;
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
		}
		naziv.clear();
		sifra.clear();
		partneri.clear();
		opis.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Projekti projekt = (Projekti)podatak;
		if(projekt.getId() != null) {
			pretplatnici.setValue(projekt.getSistemPretplatnici());
			organizacije.setValue(projekt.getOrganizacija());
			try {
				naziv.setValue(projekt.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				sifra.setValue(projekt.getSifra());
			}catch (Exception e) {
				sifra.setValue("");
			}
			try {
				partneri.setValue(projekt.getPartner());
			}catch (Exception e) {
				partneri.setValue(null);
			}
			try {
				opis.setValue(projekt.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			izbrisan.setValue(projekt.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
			System.out.println("pretplatnik");
		}
		if(naziv.getValue() == null || naziv.getValue().isEmpty() || naziv.getValue().equals("")) {
			sveIma = false;
			System.out.println("naziv");
		}
		return sveIma;
	}

}
