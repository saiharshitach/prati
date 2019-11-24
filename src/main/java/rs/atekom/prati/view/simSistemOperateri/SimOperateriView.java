package rs.atekom.prati.view.simSistemOperateri;

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
import pratiBaza.tabele.SistemOperateri;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("operateri") // an empty view name will also be the default view
@MenuCaption("Оператери")
@MenuIcon(VaadinIcons.CREDIT_CARD)
public class SimOperateriView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "simOperateri";
	private Grid<SistemOperateri> tabela;
	private ListDataProvider<SistemOperateri> dataProvider;
	private SerializablePredicate<SistemOperateri> filterPredicate;
	private ArrayList<SistemOperateri> pocetno, lista;
	private SimOperateriLogika viewLogika;
	private SimOperateriForma forma;
	private SistemOperateri izabrani;
	
	public SimOperateriView() {
		viewLogika = new SimOperateriLogika(this);
		forma = new SimOperateriForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<SistemOperateri>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemOperateri> event) {
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
		
		addComponent(barGrid);
		addComponent(forma);
		
		viewLogika.init();
	}

	@Override
	public void buildTable() {
		tabela = new Grid<SistemOperateri>();
		pocetno = new ArrayList<SistemOperateri>();
		updateTable();
		dodajFilter();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		tabela.addColumn(SistemOperateri::getNaziv).setCaption("назив");
		tabela.addComponentColumn(sistemOperateri -> {CheckBox chb = new CheckBox(); if(sistemOperateri.isIzbrisan()) {chb.setValue(true);}return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
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
		tabela.getSelectionModel().select((SistemOperateri)red);
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
		SistemOperateri operater = (SistemOperateri)podatak;
		if(operater != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(operater);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemOperaterServis.izbrisiOperatera(izabrani);
				pokaziPorukuUspesno("оператер " + izabrani.getNaziv() + " је избрисан!");
			}else {
				pokaziPorukuGreska("оператер је већ избрисан!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.sistemOperaterServis.nadjiSveOperatere();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dodajFilter() {
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

}
