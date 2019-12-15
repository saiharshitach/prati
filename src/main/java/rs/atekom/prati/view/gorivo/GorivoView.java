package rs.atekom.prati.view.gorivo;

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

import pratiBaza.tabele.SistemGoriva;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("gorivo") // an empty view name will also be the default view
@MenuCaption("Гориво")
@MenuIcon(VaadinIcons.INBOX)
public class GorivoView extends OpstiView implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "gorivo";
	private Grid<SistemGoriva> tabela;
	private ListDataProvider<SistemGoriva> dataProvider;
	private SerializablePredicate<SistemGoriva> filterPredicate;
	private ArrayList<SistemGoriva> pocetno, lista;
	private GorivoLogika viewLogika;
	private GorivoForma forma;
	private SistemGoriva izabrani;

	public GorivoView() {
		viewLogika = new GorivoLogika(this);
		forma = new GorivoForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<SistemGoriva>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemGoriva> event) {
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
		tabela = new Grid<SistemGoriva>();
		pocetno = new ArrayList<SistemGoriva>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemGoriva::getNaziv).setCaption("назив");
		tabela.addComponentColumn(sistemGoriva -> {CheckBox chb = new CheckBox(); if(sistemGoriva.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(sistemGoriva -> "v-align-right");
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
		tabela.getSelectionModel().select((SistemGoriva)red);
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
		SistemGoriva gorivo = (SistemGoriva)podatak;
		if(gorivo != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(gorivo);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemGorivoServis.izbrisiGorivo(izabrani);
				pokaziPorukuUspesno("врста горива " + izabrani.getNaziv() + " је избрисана!");
			}else {
				pokaziPorukuGreska("модел је већ избрисан!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.sistemGorivoServis.vratiSvaGoriva(true);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dodajFilter();
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dodajFilter() {
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

}
