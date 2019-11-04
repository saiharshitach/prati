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
					vozaci.setItems(Servis.vozacServis.nadjiSveVozacePoPretplatniku(event.getValue()));
				}
				
			}
		});
		
		organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				vozaci.clear();
				if(event.getValue() != null) {
					vozaci.setItems(Servis.vozacServis.nadjiSveVozacePoOrganizaciji(event.getValue()));
				}else {
					vozaci.setItems(Servis.vozacServis.nadjiSveVozacePoPretplatniku(pretplatnici.getValue()));
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
		if(logika.view.isSistem())  {
			niz.add(izbrisan);
		}
		kategorije.setItems(niz);
		layout.addComponent(kategorije);
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
		dozvola.setAM(AM.getValue());
		dozvola.setAM(A1.getValue());
		dozvola.setAM(A2.getValue());
		dozvola.setAM(A.getValue());
		dozvola.setAM(B1.getValue());
		dozvola.setAM(B.getValue());
		dozvola.setAM(BE.getValue());
		dozvola.setAM(C1.getValue());
		dozvola.setAM(C1E.getValue());
		dozvola.setAM(C.getValue());
		dozvola.setAM(CE.getValue());
		dozvola.setAM(D1.getValue());
		dozvola.setAM(D1E.getValue());
		dozvola.setAM(D.getValue());
		dozvola.setAM(DE.getValue());
		dozvola.setAM(F.getValue());
		dozvola.setAM(M.getValue());
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
		AM.setValue(false);
		A1.setValue(false);
		A2.setValue(false);
		A.setValue(false);
		B1.setValue(false);
		B.setValue(false);
		BE.setValue(false);
		C1.setValue(false);
		C1E.setValue(false);
		C.setValue(false);
		CE.setValue(false);
		D1.setValue(false);
		D1E.setValue(false);
		D.setValue(false);
		DE.setValue(false);
		F.setValue(false);
		M.setValue(false);
		izbrisan.setValue(false);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		VozaciDozvole dozvola = (VozaciDozvole)podatak;
		if(dozvola.getId() != null) {
			pretplatnici.setValue(dozvola.getSistemPretplatnici());
			organizacije.setValue(dozvola.getVozaci().getKorisnici().getOrganizacija());
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
			AM.setValue(dozvola.isAM());
			A1.setValue(dozvola.isA1());
			A2.setValue(dozvola.isA2());
			A.setValue(dozvola.isA());
			B1.setValue(dozvola.isB1());
			B.setValue(dozvola.isB());
			BE.setValue(dozvola.isBE());
			C1.setValue(dozvola.isC1());
			C1E.setValue(dozvola.isC1E());
			C.setValue(dozvola.isC());
			CE.setValue(dozvola.isCE());
			D1.setValue(dozvola.isD1());
			D1E.setValue(dozvola.isD1E());
			D.setValue(dozvola.isD());
			DE.setValue(dozvola.isDE());
			F.setValue(dozvola.isF());
			M.setValue(dozvola.isM());
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
