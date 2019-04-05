package rs.cybertrade.prati.view.sistem;

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

import pratiBaza.tabele.Sistem;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("sistem") // an empty view name will also be the default view
@MenuCaption("Систем")
@MenuIcon(VaadinIcons.COG_O)
public class SistemView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Sistem> tabela;
	private ListDataProvider<Sistem> dataProvider;
	private SerializablePredicate<Sistem> filterPredicate;
	private ArrayList<Sistem> lista;
	
	public SistemView() {
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
		tabela = new Grid<Sistem>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(Sistem::getVlasnik).setCaption("власник");
		tabela.addColumn(Sistem::getAdresaVlasnika).setCaption("адреса");
		tabela.addColumn(Sistem::getTelVlasnika).setCaption("телефон");
		tabela.addColumn(Sistem::getSajtVlasnika).setCaption("сајт");
		tabela.addColumn(Sistem::getEmailVlasnika).setCaption("е-пошта власника");
		tabela.addColumn(Sistem::getAdresaServeraMape).setCaption("сервер мапе");
		tabela.addColumn(Sistem::getApi).setCaption("апи");
		tabela.addColumn(Sistem::getEmailKorisnik).setCaption("е-поште налог");
		tabela.addColumn(Sistem::getEmailLozinka).setCaption("е-поште лозинка");
		tabela.addColumn(Sistem::getEmailServer).setCaption("е-пошта сервер");
		tabela.addColumn(Sistem::getEmailServerPort).setCaption("е-пошта порт");
		tabela.addComponentColumn(sistem -> {CheckBox chb = new CheckBox(); if(sistem.isServerMape()) {chb.setValue(true);} return chb;}).setCaption("користи сервер мапе").setStyleGenerator(sistem -> "v-align-right");
		tabela.addColumn(Sistem::getNominatimAdresa).setCaption("номинатим");
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
		lista = new ArrayList<Sistem>();
		Sistem sistem = Servis.sistemServis.vratiSistem();
		if(sistem != null) {
			lista.add(sistem);
		}
		tabela.setItems(lista);
		dataProvider = (ListDataProvider<Sistem>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Sistem>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Sistem t) {
				// TODO Auto-generated method stub
				return false;
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
