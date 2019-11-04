package rs.atekom.prati.view.uredjajiModeli;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.SistemUredjajiModeli;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboProizvodjaci;
import rs.atekom.prati.view.komponente.Tekst;

public class UredjajiModeliForma extends OpstaForma implements OpstaFormaInterface{
	
	private static final long serialVersionUID = 1L;
	private UredjajiModeliLogika logika;
	private ComboProizvodjaci proizvodjaciCombo;
	private Tekst naziv, opis;
	private CheckBox obd, izbrisan;

	public UredjajiModeliForma(UredjajiModeliLogika log) {
		logika = log;
		proizvodjaciCombo = new ComboProizvodjaci("произвођач", false);
		naziv = new Tekst("назив", true);
		opis = new Tekst("опис", false);
		obd = new CheckBox("обд");
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
		
		layout.addComponent(proizvodjaciCombo);
		layout.addComponent(naziv);
		layout.addComponent(opis);
		layout.addComponent(obd);
		layout.addComponent(izbrisan);
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		SistemUredjajiModeli model;
		ocistiPodatak();
		if(podatak == null) {
			model = new SistemUredjajiModeli();
		}else {
			model = (SistemUredjajiModeli)podatak;
			postaviPodatak(model);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemUredjajiModeli model;
		if(podatak == null) {
			model = new SistemUredjajiModeli();
		}else {
			model = (SistemUredjajiModeli)podatak;
		}
		model.setSistemUredjajiProizvodjac(proizvodjaciCombo.getValue());
		model.setNaziv(naziv.getValue());
		model.setOpis(opis.getValue());
		model.setObd(obd.getValue());
		model.setIzbrisan(izbrisan.getValue());
		return model;
	}

	@Override
	public void ocistiPodatak() {
		proizvodjaciCombo.clear();
		naziv.clear();
		opis.clear();
		obd.setValue(false);
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemUredjajiModeli model = (SistemUredjajiModeli)podatak;
		if(model.getId() != null) {
			try {
				proizvodjaciCombo.setValue(model.getSistemUredjajiProizvodjac());
			}catch (Exception e) {
				proizvodjaciCombo.setValue(null);
			}
			try {
				naziv.setValue(model.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				opis.setValue(model.getOpis());
			}catch (Exception e) {
				opis.setValue("");
			}
			try {
				obd.setValue(model.isObd());
			}catch (Exception e) {
				obd.setValue(false);
			}
			try {
				izbrisan.setValue(model.isIzbrisan());
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
		if(proizvodjaciCombo.getValue() == null) {
			sveIma = false;
		}
		if(naziv.getValue() == null || naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
