package rs.cybertrade.prati.view.proizvodjaci;

import rs.cybertrade.prati.view.OpstiViewInterface;

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

import pratiBaza.tabele.SistemUredjajiProizvodjac;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("uredjajiProizvodjaci") // an empty view name will also be the default view
@MenuCaption("Уређаји произвођачи")
@MenuIcon(VaadinIcons.AUTOMATION)
public class UredjajiProizvodjaciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<SistemUredjajiProizvodjac> tabela;
	private ListDataProvider<SistemUredjajiProizvodjac> dataProvider;
	private SerializablePredicate<SistemUredjajiProizvodjac> filterPredicate;
	private ArrayList<SistemUredjajiProizvodjac> pocetno, lista;

	public UredjajiProizvodjaciView() {
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
		tabela = new Grid<SistemUredjajiProizvodjac>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemUredjajiProizvodjac::getNaziv).setCaption("naziv");
		tabela.addColumn(SistemUredjajiProizvodjac::getOpis).setCaption("опис");
		tabela.addColumn(SistemUredjajiProizvodjac::getAdresa).setCaption("адреса");
		tabela.addComponentColumn(sistemUredjajiProizvodjac-> {CheckBox chb = new CheckBox(); if(sistemUredjajiProizvodjac.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("обд").setStyleGenerator(sistemUredjajiProizvodjaci -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		
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
	public void izmeniPodatak() {
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
		pocetno = new ArrayList<SistemUredjajiProizvodjac>();
		lista = Servis.sistemUredjajProizvodjacServis.nadjiSveSistemUredjajeProizvodjace();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemUredjajiProizvodjac>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemUredjajiProizvodjac>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemUredjajiProizvodjac t) {
				return (t.getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()));
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
