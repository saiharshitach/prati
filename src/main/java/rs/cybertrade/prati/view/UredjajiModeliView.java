package rs.cybertrade.prati.view;

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
import pratiBaza.tabele.SistemUredjajiModeli;
import rs.cybertrade.prati.Servis;

@NavigatorViewName("uredjajiModeli") // an empty view name will also be the default view
@MenuCaption("Уређаји модели")
@MenuIcon(VaadinIcons.MOBILE)
public class UredjajiModeliView extends OpstiView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	private Grid<SistemUredjajiModeli> tabela;
	private ListDataProvider<SistemUredjajiModeli> dataProvider;
	private SerializablePredicate<SistemUredjajiModeli> filterPredicate;
	private ArrayList<SistemUredjajiModeli> pocetno, lista;

	public UredjajiModeliView() {
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
		tabela = new Grid<SistemUredjajiModeli>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(sistemUredjajiModeli -> sistemUredjajiModeli.getSistemUredjajiProizvodjac().getNaziv()).setCaption("произвођач");
		tabela.addColumn(SistemUredjajiModeli::getNaziv).setCaption("назив");
		tabela.addColumn(SistemUredjajiModeli::getOpis).setCaption("опис");
		tabela.addComponentColumn(sistemUredjajiModeli -> {CheckBox chb = new CheckBox(); if(sistemUredjajiModeli.isObd()) {chb.setValue(true); }return chb;}).setCaption("обд").setStyleGenerator(sistemUredjajiModeli -> "v-align-right");
		tabela.addComponentColumn(sistemUredjajiModeli -> {CheckBox chb = new CheckBox(); if(sistemUredjajiModeli.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemUredjajiModeli -> "v-align-right");
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
		pocetno = new ArrayList<SistemUredjajiModeli>();
		lista = Servis.sistemUredjajModelServis.nadjiSveUredjajModele();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemUredjajiModeli>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemUredjajiModeli>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemUredjajiModeli t) {
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
