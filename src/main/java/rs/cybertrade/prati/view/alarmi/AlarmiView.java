package rs.cybertrade.prati.view.alarmi;

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
import pratiBaza.tabele.SistemAlarmi;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("alarmi") // an empty view name will also be the default view
@MenuCaption("Аларми")
@MenuIcon(VaadinIcons.BELL)
public class AlarmiView extends OpstiView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	private Grid<SistemAlarmi> tabela;
	private ListDataProvider<SistemAlarmi> dataProvider;
	private SerializablePredicate<SistemAlarmi> filterPredicate;
	private ArrayList<SistemAlarmi> pocetno, lista;
	
	public AlarmiView() {
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
		tabela = new Grid<SistemAlarmi>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemAlarmi::getSifra).setCaption("шифра");
		tabela.addColumn(SistemAlarmi::getNaziv).setCaption("назив");
		tabela.addColumn(SistemAlarmi::getOpis).setCaption("шифра");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAdresa()) {chb.setValue(true); }return chb;}).setCaption("адреса").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAlarmiranje()) {chb.setValue(true); }return chb;}).setCaption("алармирање").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isPrikaz()) {chb.setValue(true); }return chb;}).setCaption("приказ").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isPregled()) {chb.setValue(true); }return chb;}).setCaption("преглед").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAktivan()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemAlarmi -> "v-align-right");
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
		pocetno = new ArrayList<SistemAlarmi>();
		lista = Servis.sistemAlarmServis.vratiSveAlarme();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemAlarmi>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemAlarmi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemAlarmi t) {
				return (t.getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getSifra().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getOpis().toLowerCase().contains(filter.getValue().toLowerCase()));
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
