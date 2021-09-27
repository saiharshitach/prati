package rs.atekom.prati.view.ruta;

import java.util.ArrayList;
import com.google.maps.model.LatLng;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboGrupe;
import rs.atekom.prati.view.komponente.combo.ComboJavljanjaPoslednja;
import rs.atekom.prati.view.komponente.combo.ComboOrganizacije;
import rs.atekom.prati.view.komponente.combo.ComboPretplatnici;

public class RutaForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private RutaLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboGrupe grupeCombo;
	private ComboJavljanjaPoslednja javljanjaPoslednjaCombo;
	private Tekst tackaPrva, tackaDruga, odrediste;
	
	public RutaForma(RutaLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		grupeCombo = new ComboGrupe(pretplatnici.getValue(), organizacije.getValue(), "групе", true, true);
		javljanjaPoslednjaCombo = new ComboJavljanjaPoslednja(null, "објекти", true, true);
		tackaPrva = new Tekst("прва тачка", false);
		tackaDruga = new Tekst("друга тачка", false);
		odrediste = new Tekst("одредиште", true);
		
        pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.clear();
				grupeCombo.clear();
				if(event.getValue() != null) {
					organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnici.getValue(), true));
					if(!logika.view.korisnik.isAdmin()) {
						grupeCombo.setItems(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(logika.view.korisnik));
					}else {
						grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatnici.getValue(), organizacije.getValue()));
					}
				}
			}
		});
        
        organizacije.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				grupeCombo.clear();
				if(!logika.view.korisnik.isAdmin()) {
					grupeCombo.setItems(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(logika.view.korisnik));
				}else {
					grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatnici.getValue(), organizacije.getValue()));
				}
			}
		});
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				if(event.getValue() != null) {
					ArrayList<Objekti> objekti = new ArrayList<Objekti>();
					objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(event.getValue()));
					javljanjaPoslednjaCombo.setItems(Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(objekti));
				}else {
					javljanjaPoslednjaCombo.setItems(new ArrayList<JavljanjaPoslednja>());
				}
			}
		});
		
		sacuvaj.setCaption("прикажи");
		sacuvaj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(proveraPodataka()) {
					logika.view.prikaziRutu(javljanjaPoslednjaCombo.getValue().getObjekti(), new LatLng(javljanjaPoslednjaCombo.getValue().getLat(),
							javljanjaPoslednjaCombo.getValue().getLon()), tackaPrva.getValue(), tackaDruga.getValue(), odrediste.getValue());
					Prati.getCurrent().brojRuta++;
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
				logika.ukloniPodatak();
			}
		});
		
		if(logika.view.isSistem()) {
			layout.addComponent(pretplatnici);
		}
		if(logika.view.korisnik.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
		layout.addComponent(grupeCombo);
		layout.addComponent(javljanjaPoslednjaCombo);
		layout.addComponent(tackaPrva);
		layout.addComponent(tackaDruga);
		layout.addComponent(odrediste);
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		postaviPodatak(podatak);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ocistiPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postaviPodatak(Object podatak) {
		pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		organizacije.setValue(logika.view.korisnik.getOrganizacija());
	}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(javljanjaPoslednjaCombo.getValue() == null) {
			sveIma = true;
		}
		if(odrediste.getValue() == null || odrediste.getValue().equals("")) {
			sveIma = false;
		}
		if(Prati.getCurrent().brojRuta > 4) {
			sveIma = false;
		}
		return sveIma;
	}

}
