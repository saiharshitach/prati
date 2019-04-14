package rs.cybertrade.prati.view.proizvodjaci;

import rs.cybertrade.prati.view.OpstiViewInterface;
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
	private UredjajiProizvodjaciLogika viewLogika;
	private UredjajiProizvodjaciForma forma;
	private SistemUredjajiProizvodjac izabrani;
	public static final String VIEW_NAME = "uredjajiModeli";

	public UredjajiProizvodjaciView() {
		viewLogika = new UredjajiProizvodjaciLogika(this);
		forma = new UredjajiProizvodjaciForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		tabela.addSelectionListener(new SelectionListener<SistemUredjajiProizvodjac>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemUredjajiProizvodjac> event) {
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
		tabela = new Grid<SistemUredjajiProizvodjac>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemUredjajiProizvodjac::getNaziv).setCaption("naziv");
		tabela.addColumn(SistemUredjajiProizvodjac::getOpis).setCaption("опис");
		tabela.addColumn(SistemUredjajiProizvodjac::getAdresa).setCaption("адреса");
		tabela.addComponentColumn(sistemUredjajiProizvodjac-> {CheckBox chb = new CheckBox(); if(sistemUredjajiProizvodjac.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemUredjajiProizvodjaci -> "v-align-right");
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
		tabela.getSelectionModel().select((SistemUredjajiProizvodjac)red);
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
		SistemUredjajiProizvodjac proizvodjac = (SistemUredjajiProizvodjac)podatak;
		if(proizvodjac != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(proizvodjac);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemUredjajProizvodjacServis.izbrisiSistemUredjajProizvodjaca(izabrani);
				pokaziPorukuUspesno("произвођач уређаја " + izabrani.getNaziv() + " je izbrisan!");
			}else {
				pokaziPorukuGreska("произвођач је већ избрисан!");
			}
		}
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
