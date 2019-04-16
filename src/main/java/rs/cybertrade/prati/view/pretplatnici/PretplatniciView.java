package rs.cybertrade.prati.view.pretplatnici;

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
import pratiBaza.tabele.SistemPretplatnici;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("pretplatnici") // an empty view name will also be the default view
@MenuCaption("Претплатници")
@MenuIcon(VaadinIcons.BRIEFCASE)
public class PretplatniciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "pretplatnici";
	private Grid<SistemPretplatnici> tabela;
	private ListDataProvider<SistemPretplatnici> dataProvider;
	private SerializablePredicate<SistemPretplatnici> filterPredicate;
	private ArrayList<SistemPretplatnici> pocetno, lista;
	private PretplatniciLogika viewLogika;
	private PretplatniciForma forma;
	private SistemPretplatnici izabrani;

	public PretplatniciView() {
		viewLogika = new PretplatniciLogika(this);
		forma = new PretplatniciForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		tabela.addSelectionListener(new SelectionListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<SistemPretplatnici> event) {
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
		tabela = new Grid<SistemPretplatnici>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemPretplatnici::getNaziv).setCaption("назив");
		tabela.addColumn(SistemPretplatnici::getEmail).setCaption("е-пошта");
		tabela.addColumn(SistemPretplatnici::getAktivanDo, new DateRenderer(DANFORMAT)).setCaption("активан до");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isgMapa()) {chb.setValue(true); }return chb;}).setCaption("гугл мапа").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getApiKey).setCaption("апи кључ");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isAktivan()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((SistemPretplatnici)red);
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
		SistemPretplatnici pretplatnik = (SistemPretplatnici)podatak;
		if(pretplatnik != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(pretplatnik);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sistemPretplatnikServis.izbrisiPretplatnika(izabrani);
				pokaziPorukuUspesno("претплатник " + izabrani.getNaziv() + " je izbrisan!");
			}else {
				pokaziPorukuGreska("претплатник је већ избрисан!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<SistemPretplatnici>();
		lista = Servis.sistemPretplatnikServis.nadjiSvePretplatnike();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemPretplatnici>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemPretplatnici t) {
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
