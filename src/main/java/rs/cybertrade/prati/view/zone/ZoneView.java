package rs.cybertrade.prati.view.zone;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;
import pratiBaza.tabele.Zone;

@NavigatorViewName("zone") // an empty view name will also be the default view
@MenuCaption("Зоне")
@MenuIcon(VaadinIcons.BULLSEYE)
public class ZoneView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Zone> tabela;
	private ListDataProvider<Zone> dataProvider;
	private SerializablePredicate<Zone> filterPredicate;
	private ArrayList<Zone> pocetno, lista;
	
	public ZoneView() {
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		addComponent(barGrid);
	}

	@Override
	public void buildTable() {
		tabela = new Grid<Zone>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(zone -> zone.getSistemPretplatnici() == null ? "" : zone.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Zone::getNaziv).setCaption("назив");
		tabela.addColumn(Zone::getOpis).setCaption("опис");
		tabela.addColumn(Zone::getLon, decimalni).setCaption("дужина").setStyleGenerator(zone -> "v-align-right");
		tabela.addColumn(Zone::getLat, new NumberRenderer(new DecimalFormat(DECIMALNI))).setCaption("ширина").setStyleGenerator(zone -> "v-align-right");
		tabela.addColumn(Zone::getPrecnik).setCaption("пречник").setStyleGenerator(zone -> "v-align-right");
		tabela.addColumn(zone -> zone.getOrganizacija() == null ? "" : zone.getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addComponentColumn(zone -> {CheckBox chb = new CheckBox(); if(zone.isIzbrisan()) {chb.setValue(true);}return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Zone::getIzmenjen, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Zone::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void izaberiRed(Object red) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object dajIzabraniRed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ukloniPodatak() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Zone>();
		lista = Servis.zonaServis.nadjiSveZone(korisnik);
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
