package rs.atekom.prati.view.zone;

import java.text.DecimalFormat;
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
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Zone;
import rs.atekom.prati.mape.Gmap;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("zone")
@MenuCaption("Зоне")
@MenuIcon(VaadinIcons.BULLSEYE)
public class ZoneView extends OpstiPanelView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "zone";
	private Grid<Zone> tabela;
	private ListDataProvider<Zone> dataProvider;
	private SerializablePredicate<Zone> filterPredicate;
	private ArrayList<Zone> pocetno, lista;
	private ZoneLogika viewLogika;
	private ZoneForma forma;
	private Zone izabrani;
	private GoogleMapMarker klikMarker;
	private CssLayout css;
	
	public ZoneView() {
		viewLogika = new ZoneLogika(this);
		forma = new ZoneForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		css = new CssLayout();
		css.setSizeFull();
		css.addStyleName("crud-view");
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Zone>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Zone> event) {
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
		
		String slot = "dupli-panel-slot";
		mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		mapa.centriraj();
		Component content = buildContent(createContentWraper(mapa, slot, true), createContentWraper(tabela, slot, true));
		
		root.addComponent(topLayout);
		root.addComponent(content);
		root.setExpandRatio(content, 1);
		
		css.addComponent(root);
		css.addComponent(forma);
		
		mapa.addMapClickListener(new MapClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void mapClicked(LatLon position) {
				forma.postaviLokaciju(position);
				mapa.clearMarkers();
				klikMarker = new GoogleMapMarker("Локација", new LatLon(position.getLat(), position.getLon()), false);
				mapa.addMarker(klikMarker);
				}
			});
		
        setContent(css);
        
		viewLogika.init();
	}

	@Override
	public void buildTable() {
		tabela = new Grid<Zone>();
		updateTable();
		tabela.setSizeFull();
		//tabela.setStyleName("list");
		tabela.addStyleName(ValoTheme.TABLE_BORDERLESS);
		tabela.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		tabela.addStyleName(ValoTheme.TABLE_COMPACT);
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isSistem()) {
			tabela.addColumn(zone -> zone.getSistemPretplatnici() == null ? "" : zone.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Zone::getNaziv).setCaption("назив");
		tabela.addColumn(Zone::getOpis).setCaption("опис");
		tabela.addColumn(Zone::getLon,  new NumberRenderer(new DecimalFormat(DECIMALNIVISE))).setCaption("дужина").setStyleGenerator(zone -> "v-align-right");
		tabela.addColumn(Zone::getLat, new NumberRenderer(new DecimalFormat(DECIMALNIVISE))).setCaption("ширина").setStyleGenerator(zone -> "v-align-right");
		tabela.addColumn(Zone::getPrecnik).setCaption("пречник").setStyleGenerator(zone -> "v-align-right");
		tabela.addComponentColumn(zone -> {CheckBox chb = new CheckBox(); if(zone.isAktivan()) {chb.setValue(true);}return chb;}).setCaption("активан").setStyleGenerator(uredjaji -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(zone -> zone.getOrganizacija() == null ? "" : zone.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(zone -> {CheckBox chb = new CheckBox(); if(zone.isIzbrisan()) {chb.setValue(true);}return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		}
		tabela.addColumn(Zone::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Zone::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
	}

	
	@Override
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}
	
	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Zone)red);
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
		Zone zona = (Zone)podatak;
		if(zona != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
			mapa.clearMarkers();
		}
		forma.izmeniPodatak(zona);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.zonaObjekatServis.izbrisiZoneObjektiPOZoni(izabrani);
				Servis.zonaServis.izbrisiZonu(izabrani);
				pokaziPorukuUspesno("зона " + izabrani.getNaziv() + " је избрисана");
			}else {
				pokaziPorukuGreska("зона је већ избрисана!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Zone>();
		lista = Servis.zonaServis.nadjiSveZone(korisnik, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Zone>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Zone>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Zone t) {
				return (t.getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).contains(filter.getValue().toLowerCase()) ||
						(t.getOrganizacija() == null ? "" : t.getOrganizacija().getNaziv()).contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}
}
