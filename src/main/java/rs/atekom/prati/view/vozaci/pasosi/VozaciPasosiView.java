package rs.atekom.prati.view.vozaci.pasosi;

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
import pratiBaza.tabele.VozaciPasosi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("pasos")
@MenuCaption("Пасош")
@MenuIcon(VaadinIcons.DIPLOMA)
public class VozaciPasosiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "pasos";
	private Grid<VozaciPasosi> tabela;
	private ListDataProvider<VozaciPasosi> dataProvider;
	private SerializablePredicate<VozaciPasosi> filterPredicate;
	private ArrayList<VozaciPasosi> pocetno, lista;
	private VozaciPasosiLogika viewLogika;
	public VozaciPasosiForma forma;
	private VozaciPasosi izabrani;
	
	public VozaciPasosiView() {
		viewLogika = new VozaciPasosiLogika(this);
		forma = new VozaciPasosiForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozaciPasosi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozaciPasosi> event) {
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
		tabela = new Grid<VozaciPasosi>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(vozaciPasos -> vozaciPasos.getSistemPretplatnici() == null ? "" : vozaciPasos.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaciPasos -> vozaciPasos.getKorisnici() == null ? "" : vozaciPasos.getKorisnici().toString()).setCaption("корисник");
		tabela.addColumn(VozaciPasosi::getBrojPasosa).setCaption("број");
		tabela.addColumn(VozaciPasosi::getIzdato, new DateRenderer(DANFORMAT)).setCaption("издато").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciPasosi::getVaziDo, new DateRenderer(DANFORMAT)).setCaption("важеће до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(vozaciPasos -> vozaciPasos.getOrganizacija() == null ? "" : vozaciPasos.getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(vozaciPasos -> {CheckBox chb = new CheckBox(); if(vozaciPasos.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozaciPasosi::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciPasosi::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
		
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
		tabela.getSelectionModel().select((VozaciPasosi)red);
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
		VozaciPasosi pasosi = (VozaciPasosi)podatak;
		if(pasosi != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(pasosi);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.pasosServis.izbrisiVozacPasos(izabrani);
				pokaziPorukuUspesno("подаци за пасош " + izabrani.getKorisnici().toString() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за пасош су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozaciPasosi>();
		lista = Servis.pasosServis.nadjiSveVozacPasos(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozaciPasosi>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozaciPasosi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozaciPasosi t) {
				return (((t.getKorisnici() == null ? "" : t.getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase())));
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
