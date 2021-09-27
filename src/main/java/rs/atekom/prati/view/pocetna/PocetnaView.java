package rs.atekom.prati.view.pocetna;

import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.pomocne.PredjeniPut;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Troskovi;
import pratiBaza.tabele.Vozila;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;

@NavigatorViewName("pregled") // an empty view name will also be the default view
@MenuCaption("Преглед")
@MenuIcon(VaadinIcons.HOME)
public class PocetnaView extends OpstiPanelView{
	
	private static final long serialVersionUID = 1L;
	private Grid<Javljanja> javljanjaAlarmi;
	private Grid<Vozila> mali, veliki, registracija;
	private Grid<Troskovi> troskoviTabela;
	private String slotStyle = "dashboard-panel-slot";
	private boolean maxSize = true;
	private Date datumDo, datumOd, datumOdSedam;
	private Calendar cal;
	private ArrayList<Vozila> vozila;
	private ArrayList<Objekti> objekti;
	private NumberRenderer decimalni2;
	private ArrayList<Troskovi> troskovi;

	public PocetnaView() {
		root.removeStyleName("dupli-view");
		root.addStyleName("dashboard-view");
		decimalni2  = new NumberRenderer(new DecimalFormat(DECIMALNI));
		datumDo = new Date();
		objekti = vratiObjekte();
		vozila = Servis.voziloServis.nadjisvaVozilaPoObjektima(objekti);
		
		cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    datumDo = cal.getTime();
	    datumOd = new Date();
	    cal.set(Calendar.DATE, LocalDate.now().getDayOfMonth() - 1);
	    datumOd = cal.getTime();
	    cal.set(Calendar.DATE, LocalDate.now().getDayOfMonth() - 7);
	    datumOdSedam = cal.getTime();
	    troskovi = Servis.trosakServis.nadjiSveTroskoveOd(new Timestamp(datumOdSedam.getTime()), korisnik.getSistemPretplatnici(), korisnik.getOrganizacija());
		root.addComponentsAndExpand(buildSadrzaj());
		setContent(root);
	}
	
	private Component buildSadrzaj() {
		paneli = new CssLayout();
		paneli.addStyleName("dashboard-panels");
		Responsive.makeResponsive(paneli);
		Component mali = buildMali();
		if(mali != null) {
			paneli.addComponent(mali);
		}
		Component veliki = buildVeliki();
		if(veliki != null) {
			paneli.addComponent(veliki);
		}
		Component registracija = buildRegistracija();
		if(registracija != null) {
			paneli.addComponent(registracija);
		}
		Component trosak = buildTroskovi();
		if(trosak != null) {
			paneli.addComponent(trosak);
		}
		/*
		Component alarmi = buildAlarmi();
		if(alarmi != null) {
			paneli.addComponent(alarmi);
		}
		Component brzina = buildBrzine();
		if(brzina != null) {
			paneli.addComponent(brzina);
		}
		Component km = buildKilometri();
		if(km != null) {
			paneli.addComponent(km);
		}
		**/
		return paneli;
	}
	
	private Component buildMali() {
		mali = new Grid<Vozila>();
		mali.setSizeFull();
		mali.addStyleName(ValoTheme.TABLE_BORDERLESS);
		mali.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		mali.addStyleName(ValoTheme.TABLE_COMPACT);
		mali.setSelectionMode(SelectionMode.SINGLE);
		mali.addColumn(vozila -> vozila.getObjekti() == null ? "" : vozila.getObjekti().getOznaka()).setCaption("возило");
		mali.addColumn(Vozila::getRegistracija).setCaption("регистрација");
		mali.addColumn(Vozila::getMaliPoslednjiDatum, new DateRenderer(DANFORMAT)).setId("datum").setCaption("МС датум последњег").setStyleGenerator(vozilo -> "v-align-right");
		mali.addColumn(Vozila::getMaliPoslednjiGPSkm, decimalni).setCaption("МС последњи ГПС км").setStyleGenerator(vozilo -> "v-align-right");
		mali.addColumn(Vozila::getMaliPoslednjiOBDkm).setCaption("МС последњи ОБД км").setStyleGenerator(vozilo -> "v-align-right");
		if(objekti != null) {
			mali.setItems(vozila);
			mali.sort("datum",  SortDirection.ASCENDING);
			return createContentWraper(mali, slotStyle, maxSize, "мали сервис");
		}else {
			return null;
		}
	}
	
	private Component buildVeliki() {
		veliki = new Grid<Vozila>();
		veliki.setSizeFull();
		veliki.addStyleName(ValoTheme.TABLE_BORDERLESS);
		veliki.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		veliki.addStyleName(ValoTheme.TABLE_COMPACT);
		veliki.setSelectionMode(SelectionMode.SINGLE);
		veliki.addColumn(vozila -> vozila.getObjekti() == null ? "" : vozila.getObjekti().getOznaka()).setCaption("возило");
		veliki.addColumn(Vozila::getRegistracija).setCaption("регистрација");
		veliki.addColumn(Vozila::getVelikiPoslednjiDatum, new DateRenderer(DANFORMAT)).setId("datum").setCaption("ВС датум последњег").setStyleGenerator(vozilo -> "v-align-right");
		veliki.addColumn(Vozila::getVelikiPoslednjiGPSkm, decimalni2).setCaption("ВС последњи ГПС км").setStyleGenerator(vozilo -> "v-align-right");
		veliki.addColumn(Vozila::getVelikiPoslednjiOBDkm).setCaption("ВС последњи ОБД км").setStyleGenerator(vozilo -> "v-align-right");
		if(objekti != null) {
			veliki.setItems(vozila);
			veliki.sort("datum",  SortDirection.ASCENDING);
			return createContentWraper(veliki, slotStyle, maxSize, "велики сервис");
		}else {
			return null;
		}
	}
	
	private Component buildRegistracija() {
		registracija = new Grid<Vozila>();
		registracija.setSizeFull();
		registracija.addStyleName(ValoTheme.TABLE_BORDERLESS);
		registracija.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		registracija.addStyleName(ValoTheme.TABLE_COMPACT);
		registracija.setSelectionMode(SelectionMode.SINGLE);
		registracija.addColumn(vozila -> vozila.getObjekti() == null ? "" : vozila.getObjekti().getOznaka()).setCaption("возило");
		registracija.addColumn(Vozila::getRegistracija).setCaption("регистрација");
		registracija.addColumn(Vozila::getDatumPoslednjeRegistracije, new DateRenderer(DANFORMAT)).setId("datum").setCaption("датум последње регистрације").setStyleGenerator(vozilo -> "v-align-right");
		if(objekti != null) {
			registracija.setItems(vozila);
			registracija.sort("datum",  SortDirection.ASCENDING);
			return createContentWraper(registracija, slotStyle, maxSize, "регистрација");
		}else {
			return null;
		}
	}
	
	private Component buildTroskovi() {
		troskoviTabela = new Grid<Troskovi>();
		troskoviTabela.setSizeFull();
		troskoviTabela.addStyleName(ValoTheme.TABLE_BORDERLESS);
		troskoviTabela.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		troskoviTabela.addStyleName(ValoTheme.TABLE_COMPACT);
		troskoviTabela.setSelectionMode(SelectionMode.SINGLE);
		troskoviTabela.addColumn(Troskovi::getDatumVreme, new DateRenderer(DANFORMAT)).setCaption("датум").setStyleGenerator(objekti -> "v-align-right");
		troskoviTabela.addColumn(troskovi -> troskovi.getPartner() == null ? "" : troskovi.getPartner().getNaziv()).setCaption("партнер");
		troskoviTabela.addColumn(troskovi -> troskovi.getObjekti() == null ? "" : troskovi.getObjekti().getOznaka()).setCaption("објекат");
		troskoviTabela.addColumn(troskovi -> {
			String tip = "";
			switch (troskovi.getTipServisa()) {
			case 0: tip = "гориво";
			    break;
			case 1: tip = "мали сервис";
				break;
			case 2: tip = "велики сервис";
			    break;
			case 3: tip = "регистрација";
			    break;
			case 4: tip = "сервис";
		        break;
			default: tip = "трошкови";
				break;
			}
			return tip;
		}).setCaption("тип одржавања").setStyleGenerator(objekti -> "v-align-right");
		troskoviTabela.addColumn(Troskovi::getUkupno).setCaption("укупно").setStyleGenerator(objekti -> "v-align-right");
		if(troskovi != null) {
			troskoviTabela.setItems(troskovi);
			return createContentWraper(troskoviTabela, slotStyle, maxSize, "трошкови");
		}else {
			return null;
		}
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
		if(objekti != null && !objekti.isEmpty()) {
			javljanjaAlarmi.setItems(Servis.javljanjeServis.vratiJavljanjaObjekataOdDoSaAlarmima(objekti, new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime()), true));
			return createContentWraper(javljanjaAlarmi, slotStyle, maxSize, "аларми");
		}else {
			return null;
		}
	}
	
	private Component buildBrzine() {
		ArrayList<Objekti> objekti = vratiObjekte();
		if(objekti != null && !objekti.isEmpty()) {
			PregledBrzina brzine = new PregledBrzina(Servis.javljanjeServis.vratiJavljanjaObjekataOdDoSaBrzinama(objekti, 
					new Timestamp(datumOd.getTime()), new Timestamp(datumDo.getTime())));
			return createContentWraper(brzine, slotStyle, maxSize, "брзина");
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
			return createContentWraper(kilometri, slotStyle, maxSize, "пређени пут");
		}else {
			return null;
		}
	}
	
	private ArrayList<Objekti> vratiObjekte() {
		ArrayList<Objekti> objekti = new ArrayList<Objekti>();
		if(!korisnik.isAdmin()) {
			ArrayList<Grupe> grupe = new ArrayList<Grupe>();
			grupe.addAll(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik));
			objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
		}else {
			objekti.addAll(Servis.objekatServis.vratiSveObjekte(korisnik.getSistemPretplatnici(), korisnik.getOrganizacija()));
			/*if(korisnik.getSistemPretplatnici().isSistem()) {
				objekti = Servis.objekatServis.vratiSveObjekte(korisnik, true);
			}else {
				
			}**/
		}
		return objekti;
	}
}
