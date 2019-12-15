package rs.atekom.prati.view.organizacije;

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
import pratiBaza.tabele.Organizacije;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("organizacije") // an empty view name will also be the default view
@MenuCaption("Организације")
@MenuIcon(VaadinIcons.COGS)
public class OrganizacijeView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "organizacije";
	private Grid<Organizacije> tabela;
	private ListDataProvider<Organizacije> dataProvider;
	private SerializablePredicate<Organizacije> filterPredicate;
	private ArrayList<Organizacije> pocetno, lista;
	private OrganizacijeLogika viewLogika;
	private OrganizacijeForma forma;
	private Organizacije izabrani;

	public OrganizacijeView() {
		viewLogika = new OrganizacijeLogika(this);
		forma = new OrganizacijeForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Organizacije> event) {
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
		tabela = new Grid<Organizacije>();
		pocetno = new ArrayList<Organizacije>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(organizacije -> organizacije.getSistemPretplatnici() == null ? "" : organizacije.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Organizacije::getNaziv).setCaption("naziv");
		tabela.addColumn(Organizacije::getOpis).setCaption("опис");
		tabela.addComponentColumn(organizacije -> {CheckBox chb = new CheckBox(); if(organizacije.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(uredjaji -> "v-align-right");
		if(isSistem()) {
			tabela.addComponentColumn(organizacije -> {CheckBox chb = new CheckBox(); if(organizacije.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		}
		tabela.addColumn(Organizacije::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Organizacije::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
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
		tabela.getSelectionModel().select((Organizacije)red);
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
		Organizacije organizacija = (Organizacije)podatak;
		if(organizacija != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(organizacija);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.organizacijaServis.izbrisiOrganizacije(izabrani);
				pokaziPorukuUspesno("организација " + izabrani.getNaziv() + " је избрисана");
			}else {
				pokaziPorukuGreska("организација је већ избрисана!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.organizacijaServis.nadjiSveOrganizacije(korisnik, false);
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
		dataProvider = (ListDataProvider<Organizacije>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Organizacije>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Organizacije t) {
				return (t.getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
