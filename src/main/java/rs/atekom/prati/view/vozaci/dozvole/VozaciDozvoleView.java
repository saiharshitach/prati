package rs.atekom.prati.view.vozaci.dozvole;

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
import pratiBaza.tabele.VozaciDozvole;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("dozvola")
@MenuCaption("Дозвола")
@MenuIcon(VaadinIcons.DIPLOMA_SCROLL)
public class VozaciDozvoleView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "dozvola";
	private Grid<VozaciDozvole> tabela;
	private ListDataProvider<VozaciDozvole> dataProvider;
	private SerializablePredicate<VozaciDozvole> filterPredicate;
	private ArrayList<VozaciDozvole> pocetno, lista;
	private VozaciDozvoleLogika viewLogika;
	public VozaciDozvoleForma forma;
	private VozaciDozvole izabrani;

	public VozaciDozvoleView() {
		viewLogika = new VozaciDozvoleLogika(this);
		forma = new VozaciDozvoleForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozaciDozvole>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozaciDozvole> event) {
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
		tabela = new Grid<VozaciDozvole>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(vozaciDozvola -> vozaciDozvola.getSistemPretplatnici() == null ? "" : vozaciDozvola.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaciDozvola -> vozaciDozvola.getVozaci() == null ? "" : vozaciDozvola.getVozaci().getKorisnici().toString()).setCaption("возач");
		tabela.addColumn(VozaciDozvole::getBrojDozvole).setCaption("број");
		tabela.addColumn(VozaciDozvole::getIzdao).setCaption("издао");
		tabela.addColumn(VozaciDozvole::getVaziDo, new DateRenderer(DANFORMAT)).setCaption("важеће до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(vozaciDozvola -> vozaciDozvola.getVozaci().getKorisnici().getOrganizacija() == null ? "" : vozaciDozvola.getVozaci().getKorisnici().getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(vozaciDozvola -> {CheckBox chb = new CheckBox(); if(vozaciDozvola.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozaciDozvole::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciDozvole::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozaciDozvole)red);
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
		VozaciDozvole dozvola = (VozaciDozvole)podatak;
		if(dozvola != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(dozvola);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.dozvolaServis.izbrisiVozacDozvola(izabrani);
				pokaziPorukuUspesno("подаци за возачку дозволу " + izabrani.getVozaci().getKorisnici().toString() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за личну карту су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozaciDozvole>();
		lista = Servis.dozvolaServis.nadjiSveVozacDozvole(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozaciDozvole>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozaciDozvole>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozaciDozvole t) {
				return (((t.getVozaci().getKorisnici() == null ? "" : t.getVozaci().getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase())));
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
