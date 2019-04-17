package rs.cybertrade.prati.view.pretplatnici;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.SistemPretplatnici;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.cybertrade.prati.view.OpstaForma;
import rs.cybertrade.prati.view.OpstaFormaInterface;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.komponente.Datum;
import rs.cybertrade.prati.view.komponente.Tekst;

public class PretplatniciForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private PretplatniciLogika logika;
	private Tekst naziv, ePosta, api;
	private CheckBox googleMapa, aktivan, izbrisan;
	private Datum aktivanDo;

	public PretplatniciForma(PretplatniciLogika log) {
		logika = log;
		naziv = new Tekst("назив", true);
		ePosta = new Tekst("е-пошта", false);
		aktivanDo = new Datum("активан до", false);
		googleMapa = new CheckBox("гугл мапа");
		api = new Tekst("апи", false);
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
		
		layout.addComponent(naziv);
		layout.addComponent(ePosta);
		layout.addComponent(aktivanDo);
		layout.addComponent(googleMapa);
		layout.addComponent(api);
		layout.addComponent(aktivan);
		layout.addComponent(izbrisan);
		
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		SistemPretplatnici pretplatnik;
		ocistiPodatak();
		if(podatak == null) {
			pretplatnik = new SistemPretplatnici();
		}else {
			pretplatnik = (SistemPretplatnici)podatak;
			postaviPodatak(pretplatnik);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		SistemPretplatnici pretplatnik;
		if(podatak == null) {
			pretplatnik = new SistemPretplatnici();
		}else {
			pretplatnik = (SistemPretplatnici)podatak;
		}
		pretplatnik.setNaziv(naziv.getValue());
		pretplatnik.setEmail(ePosta.getValue());
		try {
			pretplatnik.setAktivanDo(dateDatum(aktivanDo.getValue()));
		}catch (Exception e) {
			pretplatnik.setAktivanDo(null);
		}
		
		pretplatnik.setgMapa(googleMapa.getValue());
		pretplatnik.setApiKey(api.getValue());
		pretplatnik.setAktivan(aktivan.getValue());
		pretplatnik.setIzbrisan(izbrisan.getValue());
		return pretplatnik;
	}

	@Override
	public void ocistiPodatak() {
		naziv.clear();
		ePosta.clear();
		aktivanDo.clear();
		googleMapa.setValue(true);
		api.clear();
		aktivan.setValue(true);
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		SistemPretplatnici pretplatnik = (SistemPretplatnici)podatak;
		if(pretplatnik.getId() != null) {
			try {
				naziv.setValue(pretplatnik.getNaziv());
			}catch (Exception e) {
				naziv.setValue("");
			}
			try {
				ePosta.setValue(pretplatnik.getEmail());
			}catch (Exception e) {
				ePosta.setValue("");
			}
			try {
				aktivanDo.setValue(localDatum(pretplatnik.getAktivanDo()));
			}catch (Exception e) {
				aktivanDo.setValue(null);
			}
			try {
				googleMapa.setValue(pretplatnik.isgMapa());
			}catch (Exception e) {
				googleMapa.setValue(false);
			}
			try {
				api.setValue(pretplatnik.getApiKey());
			}catch (Exception e) {
				api.setValue("");
			}
			try {
				aktivan.setValue(pretplatnik.isAktivan());
			}catch (Exception e) {
				aktivan.setValue(true);
			}
			try {
				izbrisan.setValue(pretplatnik.isIzbrisan());
			}catch (Exception e) {
				izbrisan.setValue(false);
			}
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
