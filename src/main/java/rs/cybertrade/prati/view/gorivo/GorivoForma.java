package rs.cybertrade.prati.view.gorivo;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;

import pratiBaza.tabele.SistemGoriva;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.Tekst;

public class GorivoForma extends OpstaForma implements OpstaFormaInterface{
	
	private static final long serialVersionUID = 1L;
	private GorivoLogika logika;
	private Tekst naziv;
	private CheckBox izbrisan;

	public GorivoForma(GorivoLogika log) {
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
		SistemGoriva gorivo;
		ocistiPodatak();
		if(podatak == null) {
			gorivo = new SistemGoriva();
		}else {
			gorivo = (SistemGoriva)podatak;
			postaviPodatak(gorivo);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemGoriva gorivo;
		if(podatak == null) {
			gorivo = new SistemGoriva();
		}else {
			gorivo = (SistemGoriva)podatak;
		}
		gorivo.setNaziv(naziv.getValue());
		gorivo.setIzbrisan(izbrisan.getValue());
		return gorivo;
	}

	@Override
	public void ocistiPodatak() {
		naziv.clear();
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemGoriva gorivo = (SistemGoriva)podatak;
		if(gorivo.getId() != null) {
			try {
				naziv.setValue(gorivo.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				izbrisan.setValue(gorivo.isIzbrisan());
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
