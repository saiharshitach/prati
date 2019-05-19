package rs.cybertrade.prati.view.alarmi;

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
import pratiBaza.tabele.SistemAlarmi;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("alarmi") // an empty view name will also be the default view
@MenuCaption("Аларми")
@MenuIcon(VaadinIcons.BELL)
public class AlarmiView extends OpstiView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "alarmi";
	private Grid<SistemAlarmi> tabela;
	private ListDataProvider<SistemAlarmi> dataProvider;
	private SerializablePredicate<SistemAlarmi> filterPredicate;
	private ArrayList<SistemAlarmi> pocetno, lista;
	private AlarmiLogika viewLogika;
	private AlarmiForma forma;
	private SistemAlarmi izabrani;
	
	public AlarmiView() {
		viewLogika = new AlarmiLogika(this);
		forma = new AlarmiForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildlayout();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<SistemAlarmi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemAlarmi> event) {
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
		tabela = new Grid<SistemAlarmi>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemAlarmi::getSifra).setCaption("шифра");
		tabela.addColumn(SistemAlarmi::getNaziv).setCaption("назив");
		tabela.addColumn(SistemAlarmi::getOpis).setCaption("опис");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAdresa()) {chb.setValue(true); }return chb;}).setCaption("адреса").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAlarmiranje()) {chb.setValue(true); }return chb;}).setCaption("алармирање").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isPrikaz()) {chb.setValue(true); }return chb;}).setCaption("приказ").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isPregled()) {chb.setValue(true); }return chb;}).setCaption("преглед").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isAktivan()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(sistemAlarmi -> "v-align-right");
		tabela.addComponentColumn(sistemAlarmi -> {CheckBox chb = new CheckBox(); if(sistemAlarmi.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemAlarmi -> "v-align-right");
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
		tabela.getSelectionModel().select((SistemAlarmi)red);
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
		SistemAlarmi alarm = (SistemAlarmi)podatak;
		if(alarm != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(alarm);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemAlarmServis.izbrisiAlarme(izabrani);
				pokaziPorukuUspesno("аларм " + izabrani.getNaziv() + " је избрисан!");
			}else {
				pokaziPorukuGreska("аларм је већ избрисан!");
			}
		}
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
