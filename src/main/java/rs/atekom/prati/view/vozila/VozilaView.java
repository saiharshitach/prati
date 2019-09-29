package rs.atekom.prati.view.vozila;

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

import pratiBaza.tabele.Vozila;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("vozila") // an empty view name will also be the default view
@MenuCaption("Возила")
@MenuIcon(VaadinIcons.DASHBOARD)
public class VozilaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "vozila";
	private Grid<Vozila> tabela;
	private ListDataProvider<Vozila> dataProvider;
	private SerializablePredicate<Vozila> filterPredicate;
	private ArrayList<Vozila> pocetno, lista;
	private VozilaLogika viewLogika;
	private VozilaForma forma;
	private Vozila izabrani;
	
	public VozilaView() {
		viewLogika = new VozilaLogika(this);
		forma = new VozilaForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Vozila>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Vozila> event) {
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
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Vozila>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isAdmin()) {
			tabela.addColumn(vozila -> vozila.getSistemPretplatnici() == null ? "" : vozila.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozila -> vozila.getObjekti() == null ? "" : vozila.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(Vozila::getRegistracija).setCaption("регистрација");
		tabela.addColumn(Vozila::getMarka).setCaption("марка");
		tabela.addColumn(Vozila::getModel).setCaption("модел");
		tabela.addColumn(Vozila::getTip).setCaption("тип");
		tabela.addColumn(Vozila::getGodina).setCaption("година");
		tabela.addColumn(vozila -> vozila.getSistemGoriva() == null ? "" : vozila.getSistemGoriva().getNaziv()).setCaption("врста горива");
		tabela.addColumn(Vozila::getPotrosnja).setCaption("потрошња");
		tabela.addComponentColumn(vozila -> {CheckBox chb = new CheckBox(); if(vozila.isTeretno()) {chb.setValue(true);} return chb;}).setCaption("теретно").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Vozila::getBrojSaobracajne).setCaption("број саобраћајне");
		tabela.addColumn(Vozila::getSerijskiBroj).setCaption("серијски број");
		tabela.addColumn(Vozila::getDatumRegistracije, new DateRenderer(DANFORMAT)).setCaption("датум прве регистрације").setStyleGenerator(objekti -> "v-align-right");
		if(isAdmin()) {
			tabela.addComponentColumn(vozila -> {CheckBox chb = new CheckBox(); if(vozila.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Vozila::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Vozila::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Vozila)red);
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
		Vozila vozilo = (Vozila)podatak;
		if(vozilo != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(vozilo);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.voziloServis.izbrisiVozilo(izabrani);
				pokaziPorukuUspesno("возило избрисано");
			}else {
				pokaziPorukuGreska("возило већ избрисано!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Vozila>();
		lista = Servis.voziloServis.vratisvaVozila(korisnik, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Vozila>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Vozila>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Vozila t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()));
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
