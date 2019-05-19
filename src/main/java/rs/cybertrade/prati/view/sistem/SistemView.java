package rs.cybertrade.prati.view.sistem;

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

import pratiBaza.tabele.Sistem;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("sistem") // an empty view name will also be the default view
@MenuCaption("Систем")
@MenuIcon(VaadinIcons.COG_O)
public class SistemView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "sistem";
	private Grid<Sistem> tabela;
	private ListDataProvider<Sistem> dataProvider;
	private SerializablePredicate<Sistem> filterPredicate;
	private ArrayList<Sistem> lista;
	private SistemLogika viewLogika;
	private SistemForma forma;
	private Sistem izabrani;
	
	public SistemView() {
		viewLogika = new SistemLogika(this);
		forma = new SistemForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildlayout();
		buildTable();
		tabela.addSelectionListener(new SelectionListener<Sistem>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Sistem> event) {
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
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}
	
	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Sistem)red);
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
		Sistem sistem = (Sistem)podatak;
		if(sistem != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(sistem);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			Servis.sistemServis.izbrisiSistem(izabrani);
			pokaziPorukuUspesno("системски подаци избрисани, унесите нове!");
		}else {
			pokaziPorukuGreska("системски подаци избрисани!");
		}
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
