package rs.atekom.prati.view.vozaci.lekarsko;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozaciLekarsko;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboKorisniciVozaci;

public class VozaciLekarskoForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozaciLekarskoLogika logika;
	private ComboKorisniciVozaci vozaci;
	private Tekst izdao, opis;
	private Datum izdato, vaziDo;
	private CheckBox izbrisan;

	public VozaciLekarskoForma(VozaciLekarskoLogika log) {
		logika = log;
		vozaci = new ComboKorisniciVozaci(logika.view.korisnik, "возач", true, true);
		izdao = new Tekst("издао", false);
		izdato = new Datum("издато", true);
		vaziDo = new Datum("важеће до", true);
		opis = new Tekst("опис", false);
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(event.getValue(), null, true));
				}
				
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozaci.clear();
				if(event.getValue() != null) {
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), event.getValue(), true));
				}else {
					vozaci.setItems(Servis.korisnikServis.nadjiSveKorisnikeVozace(pretplatnici.getValue(), null, true));
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

		layout.addComponent(vozaci);
		layout.addComponent(izdao);
		layout.addComponent(izdato);
		layout.addComponent(vaziDo);
		layout.addComponent(opis);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciLekarsko lekarsko;
		ocistiPodatak();
		if(podatak == null) {
			lekarsko = new VozaciLekarsko();
		}else {
			lekarsko = (VozaciLekarsko)podatak;
			postaviPodatak(lekarsko);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozaciLekarsko lekarsko;
		if(podatak == null) {
			lekarsko = new VozaciLekarsko();
		}else {
			lekarsko = (VozaciLekarsko)podatak;
		}
		lekarsko.setSistemPretplatnici(pretplatnici.getValue());
		lekarsko.setOrganizacija(null);
		lekarsko.setVozaci(vozaci.getValue());
		lekarsko.setIzdao(izdao.getValue());
		
		try {
			lekarsko.setIzdato(dateDatum(izdato.getValue()));
		}catch (Exception e) {
			lekarsko.setIzdato(null);
		}
		try {
			lekarsko.setVaziDo(dateDatum(vaziDo.getValue()));
		}catch (Exception e) {
			lekarsko.setVaziDo(null);
		}
		lekarsko.setOpis(opis.getValue());
		lekarsko.setIzbrisan(izbrisan.getValue());
		return lekarsko;
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
		vozaci.clear();
		izdao.clear();
		izdato.clear();
		vaziDo.clear();
		opis.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		if(lekarsko.getId() != null) {
			pretplatnici.setValue(lekarsko.getSistemPretplatnici());
			organizacije.setValue(lekarsko.getVozaci().getOrganizacija());
			organizacije.setEnabled(false);
			vozaci.setValue(lekarsko.getVozaci());
			try {
				izdao.setValue(lekarsko.getIzdao());
			}catch (Exception e) {
				izdao.setValue("");
			}
			try {
				opis.setValue(lekarsko.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			if(lekarsko.getIzdato() != null) {
				izdato.setValue(localDatum(lekarsko.getIzdato()));
			}else {
				izdato.setValue(null);
			}
			if(lekarsko.getVaziDo() != null) {
				vaziDo.setValue(localDatum(lekarsko.getVaziDo()));
			}else {
				vaziDo.setValue(null);
			}
			izbrisan.setValue(lekarsko.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(vozaci.getValue() == null) {
			sveIma = false;
		}
		if(izdato.getValue() == null) {
			sveIma = false;
		}
		if(vaziDo.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}
}
