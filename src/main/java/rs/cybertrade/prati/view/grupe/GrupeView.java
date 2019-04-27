package rs.cybertrade.prati.view.grupe;

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
import com.vaadin.ui.renderers.DateRenderer;
import pratiBaza.tabele.Grupe;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("grupe") // an empty view name will also be the default view
@MenuCaption("Групе")
@MenuIcon(VaadinIcons.GROUP)
public class GrupeView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "grupe";
	private Grid<Grupe> tabela;
	private ListDataProvider<Grupe> dataProvider;
	private SerializablePredicate<Grupe> filterPredicate;
	private ArrayList<Grupe> pocetno, lista;
	private GrupeLogika viewLogika;
	private GrupeForma forma;
	private Grupe izabrani;

	public GrupeView() {
		viewLogika = new GrupeLogika(this);
		forma = new GrupeForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Grupe> event) {
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
		tabela = new Grid<Grupe>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(grupe -> grupe.getSistemPretplatnici() == null ? "" : grupe.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Grupe::getNaziv).setCaption("назив");
		tabela.addColumn(Grupe::getOpis).setCaption("опис");
		tabela.addComponentColumn(grupe -> {CheckBox chb = new CheckBox(); if(grupe.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(grupe -> grupe.getOrganizacija() == null ? "" : grupe.getOrganizacija().getNaziv()).setCaption("организација");
		if(this.isAdmin()) {
			tabela.addComponentColumn(grupe -> {CheckBox chb = new CheckBox(); if(grupe.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		}
		tabela.addColumn(Grupe::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Grupe::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
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
		tabela.getSelectionModel().select((Grupe)red);
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
		Grupe grupa = (Grupe)podatak;
		if(grupa != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(grupa);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.grupeServis.izbrisiGrupu(izabrani);
				pokaziPorukuUspesno("група " + izabrani.getNaziv() + " је избрисана");
			}else {
				pokaziPorukuGreska("група је већ избрисана!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Grupe>();
		lista = Servis.grupeServis.vratiGrupe(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Grupe>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Grupe t) {
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
