package rs.atekom.prati.view.vozaci.dozvole;

import java.util.ArrayList;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.VozaciDozvole;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.ComboKorisniciVozaci;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Tekst;

public class VozaciDozvoleForma extends OpstaForma implements OpstaFormaInterface{
	
	private static final long serialVersionUID = 1L;
	private VozaciDozvoleLogika logika;
	private ComboKorisniciVozaci vozaci;
	private Tekst broj, izdao;
	private Datum vaziDo;
	private CheckBox izbrisan;
	private CheckBoxGroup<CheckBox> kategorije;
	private CheckBox AM, A1, A2, A, B1, B, BE, C1, C1E, C, CE, D1, D1E, D, DE, F, M;
	private ArrayList<CheckBox> niz;

	public VozaciDozvoleForma(VozaciDozvoleLogika log) {
		logika = log;
		vozaci = new ComboKorisniciVozaci(logika.view.korisnik, "возач", true, true);
		broj = new Tekst("број", true);
		izdao = new Tekst("издао", false);
		vaziDo = new Datum("важеће до", true);
		
		niz = new ArrayList<CheckBox>();
		kategorije = new CheckBoxGroup<CheckBox>("категорије");
		AM = new CheckBox("AM");
		A1 = new CheckBox("A1");
		A2 = new CheckBox("A2");
		A = new CheckBox("A");
		B1 = new CheckBox("B1");
		B = new CheckBox("B");
		BE = new CheckBox("BE");
		C1 = new CheckBox("C1");
		C1E = new CheckBox("C1E");
		C = new CheckBox("C");
		CE = new CheckBox("CE");
		D1 = new CheckBox("D1");
		D1E = new CheckBox("D1E");
		D = new CheckBox("D");
		DE = new CheckBox("DE");
		F = new CheckBox("F");
		M = new CheckBox("M");
		niz.add(AM);
		niz.add(A1);
		niz.add(A2);
		niz.add(A);
		niz.add(B1);
		niz.add(B);
		niz.add(BE);
		niz.add(C1);
		niz.add(C1E);
		niz.add(C);
		niz.add(CE);
		niz.add(D1);
		niz.add(D1E);
		niz.add(D);
		niz.add(DE);
		niz.add(F);
		niz.add(M);
		
		kategorije.setItemCaptionGenerator(item -> item.getCaption());
		izbrisan = new CheckBox("избрисан");
		
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				vozaci.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
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
		layout.addComponent(broj);
		layout.addComponent(izdao);
		layout.addComponent(vaziDo);

		kategorije.setItems(niz);
		layout.addComponent(kategorije);
		if(logika.view.isSistem())  {
			layout.addComponent(izbrisan);
		}
		dodajExpanderButton();
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciDozvole dozvola;
		ocistiPodatak();
		if(podatak == null) {
			dozvola = new VozaciDozvole();
		}else {
			dozvola = (VozaciDozvole)podatak;
			postaviPodatak(dozvola);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		VozaciDozvole dozvola;
		if(podatak == null) {
			dozvola = new VozaciDozvole();
		}else {
			dozvola = (VozaciDozvole)podatak;
		}
		dozvola.setSistemPretplatnici(pretplatnici.getValue());
		dozvola.setOrganizacija(null);
		dozvola.setVozaci(vozaci.getValue());
		dozvola.setBrojDozvole(broj.getValue());
		dozvola.setIzdao(izdao.getValue());
		try {
			dozvola.setVaziDo(dateDatum(vaziDo.getValue()));
		}catch (Exception e) {
			dozvola.setVaziDo(null);
		}
		
		if(kategorije.isSelected(niz.get(0))) {
			dozvola.setAM(true);
		}else {
			dozvola.setAM(false);
		}
		if(kategorije.isSelected(niz.get(1))) {
			dozvola.setA1(true);
		}else {
			dozvola.setA1(false);
		}
		if(kategorije.isSelected(niz.get(2))){
			dozvola.setA2(true);
		}else {
			dozvola.setA2(false);
		}
		if(kategorije.isSelected(niz.get(3))){
			dozvola.setA(true);
		}else {
			dozvola.setA(false);
		}
		if(kategorije.isSelected(niz.get(4))){
			dozvola.setB1(true);
		}else {
			dozvola.setB1(false);
		}
		if(kategorije.isSelected(niz.get(5))){
			dozvola.setB(true);
		}else {
			dozvola.setB(false);
		}
		if(kategorije.isSelected(niz.get(6))){
			dozvola.setBE(true);
		}else {
			dozvola.setBE(false);
		}
		if(kategorije.isSelected(niz.get(7))){
			dozvola.setC1(true);
		}else {
			dozvola.setC1(false);
		}
		if(kategorije.isSelected(niz.get(8))){
			dozvola.setC1E(true);
		}else {
			dozvola.setC1E(false);
		}
		if(kategorije.isSelected(niz.get(9))){
			dozvola.setC(true);
		}else {
			dozvola.setC(false);
		}
		if(kategorije.isSelected(niz.get(10))){
			dozvola.setCE(true);
		}else {
			dozvola.setCE(false);
		}
		if(kategorije.isSelected(niz.get(11))){
			dozvola.setD1(true);
		}else {
			dozvola.setD1(false);
		}
		if(kategorije.isSelected(niz.get(12))){
			dozvola.setD1E(true);
		}else {
			dozvola.setD1E(false);
		}
		if(kategorije.isSelected(niz.get(13))){
			dozvola.setD(true);
		}else {
			dozvola.setD(false);
		}
		if(kategorije.isSelected(niz.get(14))){
			dozvola.setDE(true);
		}else {
			dozvola.setDE(false);
		}
		if(kategorije.isSelected(niz.get(15))){
			dozvola.setF(true);
		}else {
			dozvola.setF(false);
		}
		if(kategorije.isSelected(niz.get(16))){
			dozvola.setM(true);
		}else {
			dozvola.setM(false);
		}
		
		dozvola.setIzbrisan(izbrisan.getValue());
		return dozvola;
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
		broj.clear();
		izdao.clear();
		vaziDo.clear();
		kategorije.deselectAll();
		
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciDozvole dozvola = (VozaciDozvole)podatak;
		if(dozvola.getId() != null) {
			pretplatnici.setValue(dozvola.getSistemPretplatnici());
			organizacije.setValue(dozvola.getVozaci().getOrganizacija());
			organizacije.setEnabled(false);
			vozaci.setValue(dozvola.getVozaci());
			try {
				izdao.setValue(dozvola.getIzdao());
			}catch (Exception e) {
				izdao.setValue("");
			}
			try {
				broj.setValue(dozvola.getBrojDozvole());
			}catch (Exception e) {
				broj.setValue("");
			}
			if(dozvola.getVaziDo() != null) {
				vaziDo.setValue(localDatum(dozvola.getVaziDo()));
			}else {
				vaziDo.setValue(null);
			}
			if(dozvola.isAM()) {
				kategorije.select(niz.get(0));
				}
			if(dozvola.isA1()) {
				kategorije.select(niz.get(1));
				}
			if(dozvola.isA2()) {
				kategorije.select(niz.get(2));
				}
			if(dozvola.isA()) {
				kategorije.select(niz.get(3));
				}
			if(dozvola.isB1()) {
				kategorije.select(niz.get(4));
				}
			if(dozvola.isB()) {
				kategorije.select(niz.get(5));
				}
			if(dozvola.isBE()) {
				kategorije.select(niz.get(6));
				}
			if(dozvola.isC1()) {
				kategorije.select(niz.get(7));
				}
			if(dozvola.isC1E()) {
				kategorije.select(niz.get(8));
				}
			if(dozvola.isC()) {
				kategorije.select(niz.get(9));
				}
			if(dozvola.isCE()) {
				kategorije.select(niz.get(10));
				}
			if(dozvola.isD1()) {
				kategorije.select(niz.get(11));
				}
			if(dozvola.isD1E()) {
				kategorije.select(niz.get(12));
				}
			if(dozvola.isD()) {
				kategorije.select(niz.get(13));
				}
			if(dozvola.isDE()) {
				kategorije.select(niz.get(14));
				}
			if(dozvola.isF()) {
				kategorije.select(niz.get(15));
				}
			if(dozvola.isM()) {
				kategorije.select(niz.get(16));
				}
			
			izbrisan.setValue(dozvola.isIzbrisan());
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
		if(broj.getValue() == null) {
			sveIma = false;
		}
		if(vaziDo.getValue() == null) {
			sveIma = false;
		}
		return sveIma;
	}

}
