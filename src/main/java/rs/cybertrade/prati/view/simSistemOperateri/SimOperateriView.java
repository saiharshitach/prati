package rs.cybertrade.prati.view.simSistemOperateri;

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
import pratiBaza.tabele.SistemOperateri;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("operateri") // an empty view name will also be the default view
@MenuCaption("Оператери")
@MenuIcon(VaadinIcons.CREDIT_CARD)
public class SimOperateriView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<SistemOperateri> tabela;
	private ListDataProvider<SistemOperateri> dataProvider;
	private SerializablePredicate<SistemOperateri> filterPredicate;
	private ArrayList<SistemOperateri> pocetno, lista;
	
	public SimOperateriView() {
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
		tabela = new Grid<SistemOperateri>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemOperateri::getNaziv).setCaption("назив");
		tabela.addComponentColumn(sistemOperateri -> {CheckBox chb = new CheckBox(); if(sistemOperateri.isIzbrisan()) {chb.setValue(true);}return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
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
		lista = Servis.sistemOperaterServis.nadjiSveOperatere();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemOperateri>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemOperateri>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemOperateri t) {
				return ((t.getNaziv() == null ? "" : t.getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
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
