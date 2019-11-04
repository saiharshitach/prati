package rs.atekom.prati.view.pretplatnici;

import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;

import pratiBaza.tabele.SistemPretplatnici;

import java.util.ArrayList;

import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;

public class PretplatniciForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private PretplatniciLogika logika;
	private Tekst naziv, ePosta, api;
	private CheckBox googleMapa, aktivan, izbrisan;
	private Datum aktivanDo;
	private CheckBoxGroup<CheckBox> chb;
	private ArrayList<CheckBox> niz;

	public PretplatniciForma(PretplatniciLogika log) {
		logika = log;
		naziv = new Tekst("назив", true);
		ePosta = new Tekst("е-пошта", false);
		aktivanDo = new Datum("активан до", false);
		googleMapa = new CheckBox("гугл мапа");
		api = new Tekst("апи", false);
		aktivan = new CheckBox("активан");
		izbrisan = new CheckBox("избрисан");
		
		ukloniCombo();
		
		niz = new ArrayList<CheckBox>();
		niz.add(googleMapa);
		niz.add(aktivan);
		niz.add(izbrisan);
		chb = new CheckBoxGroup<CheckBox>();
		chb.setItemCaptionGenerator(item -> item.getCaption());
		chb.setItems(niz);
		
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
		layout.addComponent(api);
		layout.addComponent(chb);
		
		dodajExpanderButton();
		
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
			naziv.setValue(pretplatnik.getNaziv());
			try {
				ePosta.setValue(pretplatnik.getEmail());
			}catch (Exception e) {
				ePosta.setValue("");
			}
			if(pretplatnik.getAktivanDo() != null) {
				aktivanDo.setValue(localDatum(pretplatnik.getAktivanDo()));
			}else {
				aktivanDo.setValue(null);
			}
			googleMapa.setValue(pretplatnik.isgMapa());
			try {
				api.setValue(pretplatnik.getApiKey());
			}catch (Exception e) {
				api.setValue("");
			}
			
			aktivan.setValue(pretplatnik.isAktivan());
			izbrisan.setValue(pretplatnik.isIzbrisan());
		}
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		return sveIma;
	}

}
