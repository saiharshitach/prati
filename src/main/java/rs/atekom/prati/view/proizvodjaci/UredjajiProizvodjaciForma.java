package rs.atekom.prati.view.proizvodjaci;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.SistemUredjajiProizvodjac;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Tekst;

public class UredjajiProizvodjaciForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private UredjajiProizvodjaciLogika logika;
	private Tekst naziv, opis, adresa;
	private CheckBox izbrisan;
	
	public UredjajiProizvodjaciForma(UredjajiProizvodjaciLogika log) {
		logika = log;
		naziv = new Tekst("naziv", true);
		opis = new Tekst("опис", false);
		adresa = new Tekst("адреса", false);
		izbrisan = new CheckBox("избрисан");
		
		ukloniCombo();
		
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
		layout.addComponent(opis);
		layout.addComponent(adresa);
		layout.addComponent(izbrisan);
		
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		SistemUredjajiProizvodjac proizvodjac;
		ocistiPodatak();
		if(podatak == null) {
			proizvodjac = new SistemUredjajiProizvodjac();
		}else {
			proizvodjac = (SistemUredjajiProizvodjac)podatak;
			postaviPodatak(proizvodjac);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemUredjajiProizvodjac proizvodjac;
		if(podatak == null) {
			proizvodjac = new SistemUredjajiProizvodjac();
		}else {
			proizvodjac = (SistemUredjajiProizvodjac)podatak;
		}
		proizvodjac.setNaziv(naziv.getValue());
		proizvodjac.setOpis(opis.getValue());
		proizvodjac.setAdresa(adresa.getValue());
		proizvodjac.setIzbrisan(izbrisan.getValue());
		return proizvodjac;
	}

	@Override
	public void ocistiPodatak() {
		naziv.clear();
		opis.clear();
		adresa.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemUredjajiProizvodjac proizvodjac =(SistemUredjajiProizvodjac)podatak;
		if(proizvodjac.getId() != null) {
			try {
				naziv.setValue(proizvodjac.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				opis.setValue(proizvodjac.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			try {
				adresa.setValue(proizvodjac.getAdresa());
			}catch (Exception e) {
				adresa.setValue("");
			}
			try {
				izbrisan.setValue(proizvodjac.isIzbrisan());
			}catch (Exception e) {
				izbrisan.setValue(false);
			}
		}else {
			ocistiPodatak();
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(naziv.getValue() == "" || naziv.getValue().isEmpty() || naziv.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
