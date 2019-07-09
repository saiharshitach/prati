package rs.atekom.prati.view.pocetna;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import pratiBaza.pomocne.PredjeniPut;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;

@NavigatorViewName("pregled") // an empty view name will also be the default view
@MenuCaption("Преглед")
@MenuIcon(VaadinIcons.HOME)
public class PocetnaView extends OpstiPanelView{
	
	private static final long serialVersionUID = 1L;
	private Grid<Javljanja> javljanjaAlarmi;
	private String slotStyle = "dashboard-panel-slot";
	private boolean maxSize = true;
	private Date datumDo, datumOd;
	private Calendar cal;

	public PocetnaView() {
		root.removeStyleName("dupli-view");
		root.addStyleName("dashboard-view");
		datumDo = new Date();
		cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    datumDo = cal.getTime();
	    datumOd = new Date();
	    cal.set(Calendar.DATE, LocalDate.now().getDayOfMonth() - 1);
	    datumOd = cal.getTime();
		root.addComponentsAndExpand(buildSadrzaj());
		setContent(root);
	}
	
	private Component buildSadrzaj() {
		paneli = new CssLayout();
		paneli.addStyleName("dashboard-panels");
		Responsive.makeResponsive(paneli);
		
		if(buildBrzine() != null) {
			paneli.addComponent(buildBrzine());
		}
		if(buildKilometri() != null) {
			paneli.addComponent(buildKilometri());
		}
		if(buildAlarmi() != null) {
			paneli.addComponent(buildAlarmi());
		}
		
		return paneli;
	}
	
	
	private Component buildAlarmi() {
		javljanjaAlarmi = new Grid<Javljanja>();
		javljanjaAlarmi.setSizeFull();
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_BORDERLESS);
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		javljanjaAlarmi.addStyleName(ValoTheme.TABLE_COMPACT);
		javljanjaAlarmi.setSelectionMode(SelectionMode.SINGLE);
		javljanjaAlarmi.addColumn(Javljanja::getDatumVreme,new DateRenderer(DANSATFORMAT)).setId("datumVreme").setCaption("датум/време").setStyleGenerator(uredjaji -> "v-align-left");
		javljanjaAlarmi.addColumn(javljanja -> javljanja.getObjekti().getOznaka()).setCaption("објект");
		javljanjaAlarmi.addColumn(javljanja -> javljanja.getSistemAlarmi().getNaziv()).setCaption("аларм");
		javljanjaAlarmi.addColumn(Javljanja::getEventData).setCaption("опис");
		ArrayList<Objekti> objekti = vratiObjekte();
		if(objekti != null && !objekti.isEmpty()) {
			javljanjaAlarmi.setItems(Servis.javljanjeServis.vratiJavljanjaObjekataOdDoSaAlarmima(objekti, new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime()), true));
			return createContentWraper(javljanjaAlarmi, slotStyle, maxSize);
		}else {
			return null;
		}
	    
	}
	
	private Component buildBrzine() {
		ArrayList<Objekti> objekti = vratiObjekte();
		if(objekti != null && !objekti.isEmpty()) {
			PregledBrzina brzine = new PregledBrzina(Servis.javljanjeServis.vratiJavljanjaObjekataOdDoSaBrzinama(objekti, 
					new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime())));
			return createContentWraper(brzine, slotStyle, maxSize);
		}else {
			return null;
		}

	}
	
	private Component buildKilometri() {
		List<PredjeniPut> lista = new ArrayList<PredjeniPut>();
		ArrayList<Objekti> objekti = vratiObjekte();
		if(objekti != null && !objekti.isEmpty()) {
			for(Objekti objekat : objekti) {
				
				ArrayList<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDoPrvoPoslednje(objekat,
						new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime()));
				ArrayList<Obd> obdLista = Servis.obdServis.nadjiObdPoObjektuOdDoPrvoPoslednje(objekat,
						new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime()));

				PredjeniPut predjeniPut = new PredjeniPut(objekat.getOznaka(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
				
				if(javljanja != null && !javljanja.isEmpty()) {
					predjeniPut.setVirtualOdo(javljanja.get(1).getVirtualOdo() - javljanja.get(0).getVirtualOdo());
					if(obdLista != null && !obdLista.isEmpty()) {
						predjeniPut.setUkupnoKm(obdLista.get(1).getUkupnoKm() - obdLista.get(0).getUkupnoKm());
						predjeniPut.setUkupnoGorivo(obdLista.get(1).getUkupnoGorivo() - obdLista.get(0).getUkupnoGorivo());
						if(predjeniPut.getVirtualOdo() != 0.0f) {
							predjeniPut.setProsPotGps(predjeniPut.getUkupnoGorivo()/(predjeniPut.getVirtualOdo()/100));
						}
						if(predjeniPut.getUkupnoKm() != 0.0f) {
							predjeniPut.setProsPotr(predjeniPut.getUkupnoGorivo()/(predjeniPut.getUkupnoKm()/100));
						}
					}
				}
				lista.add(predjeniPut);
			}
			lista.sort(Comparator.comparing(PredjeniPut::getVirtualOdo).reversed());
			ArrayList<PredjeniPut> konacno = new ArrayList<PredjeniPut>();
			if(lista.size() < 11) {
				konacno.addAll(lista);
			}else {
				int x = 0;
				while(x < 10) {
					konacno.add(lista.get(x));
					x++;
				}
			}
			PregledKilometara kilometri = new PregledKilometara(konacno);
			return createContentWraper(kilometri, slotStyle, maxSize);
		}else {
			return null;
		}


	}
	
	private ArrayList<Objekti> vratiObjekte() {
		ArrayList<Objekti> objekti = new ArrayList<Objekti>();
		if(!korisnik.isAdmin()) {
			ArrayList<Grupe> grupe = Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik);
			
			objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
			
		}else {
			if(korisnik.getSistemPretplatnici() != null) {
				objekti = Servis.objekatServis.vratiSveObjekte(korisnik, true);
			}
		}
		return objekti;
	}
}
