package rs.atekom.prati.view.projekti;

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
import pratiBaza.tabele.Projekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("projekti") // an empty view name will also be the default view
@MenuCaption("Пројекти")
@MenuIcon(VaadinIcons.BOOK)
public class ProjektiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "projekti";
	private Grid<Projekti> tabela;
	private ListDataProvider<Projekti> dataProvider;
	private SerializablePredicate<Projekti> filterPredicate;
	private ArrayList<Projekti> pocetno, lista;
	private ProjektiLogika viewLogika;
	private ProjektiForma forma;
	private Projekti izabrani;
	
	public ProjektiView() {
		viewLogika = new ProjektiLogika(this);
		forma = new ProjektiForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Projekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Projekti> event) {
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
		tabela = new Grid<Projekti>();
		pocetno = new ArrayList<Projekti>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(projekti -> projekti.getSistemPretplatnici() == null ? "" : projekti.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Projekti::getNaziv).setCaption("назив");
		tabela.addColumn(Projekti::getSifra).setCaption("шифра");
		tabela.addColumn(projekti -> projekti.getPartner() == null ? "" : projekti.getPartner().getNaziv()).setCaption("партнер");
		tabela.addColumn(Projekti::getOpis).setCaption("опис");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(projekti -> projekti.getOrganizacija() == null ? "" : projekti.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(projekti -> {CheckBox chb = new CheckBox(); if(projekti.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Projekti::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Projekti::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Projekti)red);
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
		Projekti projekt = (Projekti)podatak;
		if(projekt != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(projekt);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.projektServis.izbrisiProjekat(izabrani);
				pokaziPorukuUspesno("пројект избрисан");
			}else {
				pokaziPorukuGreska("пројект већ избрисан!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.projektServis.nadjiSveProjekte(korisnik);
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
		dataProvider = (ListDataProvider<Projekti>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Projekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Projekti t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSifra() == null ? "" : t.getSifra()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getPartner() == null ? "" : t.getPartner().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
