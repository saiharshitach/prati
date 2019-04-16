package rs.cybertrade.prati.view.uredjajiModeli;

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
import pratiBaza.tabele.SistemUredjajiModeli;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("uredjajiModeli") // an empty view name will also be the default view
@MenuCaption("Уређаји модели")
@MenuIcon(VaadinIcons.MOBILE)
public class UredjajiModeliView extends OpstiView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "uredjajiModeli";
	private Grid<SistemUredjajiModeli> tabela;
	private ListDataProvider<SistemUredjajiModeli> dataProvider;
	private SerializablePredicate<SistemUredjajiModeli> filterPredicate;
	private ArrayList<SistemUredjajiModeli> pocetno, lista;
	private UredjajiModeliLogika viewLogika;
	private UredjajiModeliForma forma;
	private SistemUredjajiModeli izabrani;

	public UredjajiModeliView() {
		viewLogika = new UredjajiModeliLogika(this);
		forma = new UredjajiModeliForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<SistemUredjajiModeli>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemUredjajiModeli> event) {
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
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((SistemUredjajiModeli)red);
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
		SistemUredjajiModeli model = (SistemUredjajiModeli)podatak;
		if(model != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(model);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemUredjajModelServis.izbrisiUredjajModel(izabrani);
				pokaziPorukuUspesno("модел уређаја " + izabrani.getNaziv() + " je izbrisan!");
			}else {
				pokaziPorukuGreska("модел је већ избрисан!");
			}
		}
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
