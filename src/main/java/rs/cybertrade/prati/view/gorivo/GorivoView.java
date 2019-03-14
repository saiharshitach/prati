package rs.cybertrade.prati.view.gorivo;

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

import pratiBaza.tabele.SistemGoriva;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OsnovniView;

@NavigatorViewName("gorivo") // an empty view name will also be the default view
@MenuCaption("Гориво")
@MenuIcon(VaadinIcons.INBOX)
public class GorivoView extends OsnovniView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	private Grid<SistemGoriva> tabela;
	private ListDataProvider<SistemGoriva> dataProvider;
	private SerializablePredicate<SistemGoriva> filterPredicate;
	private ArrayList<SistemGoriva> pocetno, lista;

	public GorivoView() {
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
		tabela = new Grid<SistemGoriva>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemGoriva::getNaziv).setCaption("назив");
		tabela.addComponentColumn(sistemGoriva -> {CheckBox chb = new CheckBox(); if(sistemGoriva.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(sistemGoriva -> "v-align-right");
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
		pocetno = new ArrayList<SistemGoriva>();
		lista = Servis.sistemGorivoServis.vratiSvaGoriva();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemGoriva>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemGoriva>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(SistemGoriva t) {
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
