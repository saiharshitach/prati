package rs.atekom.prati.view.objekatZone;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import pratiBaza.tabele.ObjekatZone;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("objekatZone")
@MenuCaption("Објекат зоне")
@MenuIcon(VaadinIcons.BULLETS)
public class ObjekatZoneView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "objekatZone";
	private Grid<ObjekatZone> tabela;
	private ListDataProvider<ObjekatZone> dataProvider;
	private SerializablePredicate<ObjekatZone> filterPredicate;
	private ArrayList<ObjekatZone> pocetno, lista;
	private ObjekatZoneLogika viewLogika;
	private ObjekatZoneForma forma;
	private ObjekatZone izabrani;

	public ObjekatZoneView() {
		viewLogika = new ObjekatZoneLogika(this);
		forma = new ObjekatZoneForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<ObjekatZone>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<ObjekatZone> event) {
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
		tabela = new Grid<ObjekatZone>();
		pocetno = new ArrayList<ObjekatZone>();
		updateTable();
		dodajFilter();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(objekatZone -> objekatZone.getSistemPretplatnici() == null ? "" : objekatZone.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(objekatZone -> objekatZone.getObjekti() == null ? "" : objekatZone.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(objekatZone -> objekatZone.getZone() == null ? "" : objekatZone.getZone().getNaziv()).setCaption("назив зоне");
		tabela.addComponentColumn(objekatZone -> {CheckBox chb = new CheckBox(); if(objekatZone.isUlaz()) {chb.setValue(true);}return chb;}).setCaption("аларм на улаз").setStyleGenerator(objekatZone -> "v-align-right");
		tabela.addComponentColumn(objekatZone -> {CheckBox chb = new CheckBox(); if(objekatZone.isIzlaz()) {chb.setValue(true);}return chb;}).setCaption("аларм на излаз").setStyleGenerator(objekatZone -> "v-align-right");
		tabela.addComponentColumn(objekatZone -> {CheckBox chb = new CheckBox(); if(objekatZone.isAktivan()) {chb.setValue(true);}return chb;}).setCaption("активан").setStyleGenerator(objekatZone -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(objekatZone -> objekatZone.getOrganizacija() == null ? "" : objekatZone.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(objekatZone -> {CheckBox chb = new CheckBox(); if(objekatZone.isIzbrisan()) {chb.setValue(true);}return chb;}).setCaption("избрисан").setStyleGenerator(objekatZone -> "v-align-right");
		}
		tabela.addColumn(ObjekatZone::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(ObjekatZone::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("уписано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((ObjekatZone)red);
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
		ObjekatZone objekatZona = (ObjekatZone)podatak;
		if(objekatZona != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(objekatZona);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.zonaObjekatServis.izbrisiZonaObjekat(izabrani);
				pokaziPorukuUspesno("објекат зона је избрисан");
			}else {
				pokaziPorukuGreska("објекат зона је већ избрисан!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.zonaObjekatServis.vratiSveObjekatZone(korisnik, false);
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
		dataProvider = (ListDataProvider<ObjekatZone>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<ObjekatZone>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(ObjekatZone t) {
				return ((t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getZone() == null ? "" : t.getZone().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
