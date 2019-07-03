package rs.atekom.prati.view.simSistemOperateri;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;

import pratiBaza.tabele.SistemOperateri;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Tekst;

public class SimOperateriForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private SimOperateriLogika logika;
	private Tekst naziv;
	private CheckBox izbrisan;
	
	public SimOperateriForma(SimOperateriLogika log) {
		logika = log;
		naziv = new Tekst("назив", true);
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
		
		layout.addComponent(naziv);
		layout.addComponent(izbrisan);
		
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemOperateri operater;
		ocistiPodatak();
		if(podatak == null) {
			operater = new SistemOperateri();
		}else {
			operater = (SistemOperateri)podatak;
			postaviPodatak(operater);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemOperateri operater;
		if(podatak == null) {
			operater = new SistemOperateri();
		}else {
			operater = (SistemOperateri)podatak;
		}
		operater.setNaziv(naziv.getValue());
		operater.setIzbrisan(izbrisan.getValue());
		return operater;
	}

	@Override
	public void ocistiPodatak() {
		naziv.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemOperateri operater = (SistemOperateri)podatak;
		if(operater.getId() != null) {
			try {
				naziv.setValue(operater.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				izbrisan.setValue(operater.isIzbrisan());
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
		if(naziv == null || naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
