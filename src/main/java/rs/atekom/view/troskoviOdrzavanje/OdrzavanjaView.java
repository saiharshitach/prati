package rs.atekom.view.troskoviOdrzavanje;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Troskovi;
import pratiBaza.tabele.Vozila;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("odrzavanje") // an empty view name will also be the default view
@MenuCaption("Одржавање")
@MenuIcon(VaadinIcons.AUTOMATION)
public class OdrzavanjaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "odrzavanje";
	private Grid<Troskovi> tabela;
	private ListDataProvider<Troskovi> dataProvider;
	private SerializablePredicate<Troskovi> filterPredicate;
	private ArrayList<Troskovi> pocetno, lista;
	private OdrzavanjaLogika viewLogika;
	private OdrzavanjaForma forma;
	private Troskovi izabrani;
	
	public OdrzavanjaView() {
		viewLogika = new OdrzavanjaLogika(this);
		forma = new OdrzavanjaForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Troskovi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Troskovi> event) {
				if(event.getFirstSelectedItem().isPresent()) {
					izabrani = event.getFirstSelectedItem().get();
				}else {
					izabrani = null;
				}
				viewLogika.redIzabran(izabrani);
			}
		});
		
		dodaj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				viewLogika.noviPodatak();
			}
		});
		
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		
		addComponent(barGrid);
		addComponent(forma);
		
		viewLogika.init();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Troskovi>();
		pocetno = new ArrayList<Troskovi>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(troskovi -> troskovi.getSistemPretplatnici() == null ? "" : troskovi.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Troskovi::getDatumVreme, new DateRenderer(DANFORMAT)).setCaption("датум").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(troskovi -> troskovi.getPartner() == null ? "" : troskovi.getPartner().getNaziv()).setCaption("партнер");
		tabela.addColumn(troskovi -> troskovi.getObjekti() == null ? "" : troskovi.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(troskovi -> troskovi.getMarka() == null ? "" : troskovi.getMarka()).setCaption("марка");
		tabela.addColumn(troskovi -> troskovi.getModel() == null ? "" : troskovi.getMarka()).setCaption("модел");
		tabela.addColumn(Troskovi::getTipServisaNaziv).setCaption("тип одржавања").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getCena).setCaption("цена").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getPdvProcenat).setCaption("ПДВ %").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getPdvIznos).setCaption("ПДВ").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getUkupno).setCaption("укупно").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getOpis).setCaption("опис").setStyleGenerator(objekti -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(troskovi -> troskovi.getObjekti() == null ? "" : troskovi.getObjekti().getOrganizacija() == null ? "" : troskovi.getObjekti().getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(troskovi -> {CheckBox chb = new CheckBox(); if(troskovi.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Troskovi::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Troskovi::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Troskovi)red);
	}

	@Override
	public Object dajIzabraniRed() {
		try {
			return tabela.getSelectionModel().getFirstSelectedItem().get();
		}catch (NoSuchElementException e) {
			return null;
		}
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		if(trosak != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(trosak);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Vozila vozilo = izabrani.getObjekti().getVozilo();
				switch (izabrani.getTipServisa()) {
				case 1: if(izabrani.getDatumVreme().equals(vozilo.getMaliPoslednjiDatum())) {
					Troskovi trosak = Servis.trosakServis.nadjiPoslednjiTrosakDo(izabrani.getDatumVreme(), 1);//prve pre izabranog za brisanje
					if(trosak != null) {
						vozilo.setMaliPoslednjiDatum(trosak.getDatumVreme());
						Javljanja gpsKm = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozilo.getObjekti(), trosak.getDatumVreme(), true);
						if(gpsKm != null) {
							vozilo.setMaliPoslednjiGPSkm(gpsKm.getVirtualOdo());
						}
						Obd obdKm = Servis.obdServis.nadjiObdPoslednji(vozilo.getObjekti(), trosak.getDatumVreme());
						if(obdKm != null) {
							vozilo.setMaliPoslednjiOBDkm(obdKm.getUkupnoKm());
							}
					}else {
						vozilo.setMaliPoslednjiDatum(null);
						vozilo.setMaliPoslednjiGPSkm(0);
						vozilo.setMaliPoslednjiOBDkm(0);
						}
				    }
				break;

				case 2: if(izabrani.getDatumVreme().equals(vozilo.getVelikiPoslednjiDatum())) {
					Troskovi trosak = Servis.trosakServis.nadjiPoslednjiTrosakDo(izabrani.getDatumVreme(), 2);//prve pre izabranog za brisanje
					
					if(trosak != null) {
						vozilo.setVelikiPoslednjiDatum(trosak.getDatumVreme());
						
						Javljanja gpsKm = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozilo.getObjekti(), trosak.getDatumVreme(), true);
						if(gpsKm != null) {
							vozilo.setVelikiPoslednjiGPSkm(gpsKm.getVirtualOdo());
						}
						
						Obd obdKm = Servis.obdServis.nadjiObdPoslednji(vozilo.getObjekti(), trosak.getDatumVreme());
						if(obdKm != null) {
							vozilo.setVelikiPoslednjiOBDkm(obdKm.getUkupnoKm());
							}
					}else {
						vozilo.setVelikiPoslednjiDatum(null);
						vozilo.setVelikiPoslednjiGPSkm(0);
						vozilo.setVelikiPoslednjiOBDkm(0);
						}
				    }
				break;
				
				case 3: if(izabrani.getDatumVreme().equals(vozilo.getDatumPoslednjeRegistracije())) {
					Troskovi trosak = Servis.trosakServis.nadjiPoslednjiTrosakDo(izabrani.getDatumVreme(), 3);//prve pre izabranog za brisanje
					if(trosak != null) {
						vozilo.setDatumPoslednjeRegistracije(trosak.getDatumVreme());
					}else {
						vozilo.setDatumPoslednjeRegistracije(null);
					}
				}
				default:
					break;
				}
				Servis.trosakServis.izbrisiTrosak(izabrani);
				pokaziPorukuUspesno("одржавање избрисано");
			}else {
				pokaziPorukuGreska("одржавање већ избрисано!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.trosakServis.nadjiSvaOdrzavanja(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dodajFilter();
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dodajFilter() {
		dataProvider = (ListDataProvider<Troskovi>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Troskovi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Troskovi t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getPartner() == null ? "" : t.getPartner().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}
}
