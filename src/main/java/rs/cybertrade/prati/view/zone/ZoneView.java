package rs.cybertrade.prati.view.zone;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;
import pratiBaza.tabele.Sim;
import pratiBaza.tabele.Zone;

@NavigatorViewName("zone") // an empty view name will also be the default view
@MenuCaption("Зоне")
@MenuIcon(VaadinIcons.BULLSEYE)
public class ZoneView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "zone";
	private Grid<Zone> tabela;
	private ListDataProvider<Zone> dataProvider;
	private SerializablePredicate<Zone> filterPredicate;
	private ArrayList<Zone> pocetno, lista;
	private ZoneLogika viewLogika;
	private ZoneForma forma;
	private Zone izabrani;
	
	public ZoneView() {
		viewLogika = new ZoneLogika(this);
		forma = new ZoneForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		topLayout = buildToolbar();
		buildlayout();
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
		
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		
		addComponent(barGrid);
		addComponent(forma);
		
		viewLogika.init();
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
		tabela.addColumn(Zone::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
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
