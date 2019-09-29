package rs.atekom.prati.view.partneri;

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

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Partneri;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("partneri")
@MenuCaption("Партнери")
@MenuIcon(VaadinIcons.CALENDAR_BRIEFCASE)
public class PartneriView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "partneri";
	private Grid<Partneri> tabela;
	private ListDataProvider<Partneri> dataProvider;
	private SerializablePredicate<Partneri> filterPredicate;
	private ArrayList<Partneri> pocetno, lista;
	private PartneriLogika viewLogika;
	public PartneriForma forma;
	private Partneri izabrani;
	
	public PartneriView() {
		viewLogika = new PartneriLogika(this);
		forma = new PartneriForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Partneri>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Partneri> event) {
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
		tabela = new Grid<Partneri>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(partneri -> partneri.getSistemPretplatnici() == null ? "" : partneri.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Partneri::getNaziv).setCaption("назив");
		tabela.addColumn(Partneri::getPib).setCaption("ПИБ");
		tabela.addColumn(Partneri::getMb).setCaption("МБ");
		tabela.addColumn(Partneri::getAdresa).setCaption("адреса");
		tabela.addColumn(Partneri::getTelefon).setCaption("телефон");
		tabela.addColumn(Partneri::getMobilni).setCaption("мобилни");
		tabela.addColumn(Partneri::getEposta).setCaption("е-пошта");
		tabela.addColumn(Partneri::getKontaktOsoba).setCaption("контакт особа");
		tabela.addComponentColumn(partneri -> {CheckBox chb = new CheckBox(); if(partneri.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Partneri::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Partneri::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((Partneri)red);
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
		Partneri partner = (Partneri)podatak;
		if(partner != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(partner);
		
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			Servis.partnerServis.izbrisiPartnera(izabrani);
			pokaziPorukuUspesno("партнер " + izabrani.getNaziv() + " је избрисан");
		}else {
			pokaziPorukuGreska("партнер је већ избрисан!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Partneri>();
		lista = Servis.partnerServis.nadjiSvePartnere(korisnik, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Partneri>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Partneri>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Partneri t) {
				return ((t.getNaziv() == null ? "" : t.getNaziv().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						String.valueOf(t.getPib()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getMb() == null ? "" : t.getMb()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getKontaktOsoba() == null ? "" : t.getKontaktOsoba()).toLowerCase().contains(filter.getValue().toLowerCase()));
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
