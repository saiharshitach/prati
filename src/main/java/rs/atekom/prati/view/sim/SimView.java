package rs.atekom.prati.view.sim;

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
import pratiBaza.tabele.Sim;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("sim") // an empty view name will also be the default view
@MenuCaption("СИМ")
@MenuIcon(VaadinIcons.CREDIT_CARD)
public class SimView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "sim";
	private Grid<Sim> tabela;
	private ListDataProvider<Sim> dataProvider;
	private SerializablePredicate<Sim> filterPredicate;
	private ArrayList<Sim> pocetno, lista;
	private SimLogika viewLogika;
	private SimForma forma;
	private Sim izabrani;

	public SimView() {
		viewLogika = new SimLogika(this);
		forma = new SimForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Sim>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Sim> event) {
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
		tabela = new Grid<Sim>();
		pocetno = new ArrayList<Sim>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);

		if(isSistem()) {
			tabela.addColumn(sim -> sim.getSistemPretplatnici() == null ? "" : sim.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Sim::getBroj).setCaption("број");
		tabela.addColumn(Sim::getIccid).setCaption("иццид");
		tabela.addColumn(sim -> sim.getSistemOperateri() == null ? "" : sim.getSistemOperateri().getNaziv()).setCaption("оператер");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getSistemUredjajiModeli() == null ? "" :
			sim.getUredjaji().getSistemUredjajiModeli().getNaziv()).setCaption("модел");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getSerijskiBr()).setCaption("уређај");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getObjekti() == null ? "" : 
			sim.getUredjaji().getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(Sim::getOpis).setCaption("опис");
		tabela.addComponentColumn(sim -> {CheckBox chb = new CheckBox(); if(sim.isAktivno()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(sim -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(sim -> sim.getOrganizacija() == null ? "" : sim.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(sim -> {CheckBox chb = new CheckBox(); if(sim.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(sim -> "v-align-right");
		}
		tabela.addColumn(Sim::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(sim -> "v-align-right");
		tabela.addColumn(Sim::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(sim -> "v-align-right");
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
		tabela.getSelectionModel().select((Sim)red);
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
		Sim sim = (Sim)podatak;
		if( podatak != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(sim);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.simServis.izbrisiSim(izabrani);
				pokaziPorukuUspesno("сим картица " + izabrani.getIccid() + " је избрисана");
			}else {
				pokaziPorukuGreska("сим картица је већ избрисана!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.simServis.vratiSveSimKartice(korisnik, false);
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
		dataProvider = (ListDataProvider<Sim>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Sim>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Sim t) {
				return (t.getBroj().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getIccid().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : t.getUredjaji().getSerijskiBr()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : t.getUredjaji().getObjekti() == null ? "" : t.getUredjaji().getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
