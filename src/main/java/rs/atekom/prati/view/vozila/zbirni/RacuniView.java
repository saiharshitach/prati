package rs.atekom.prati.view.vozila.zbirni;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import pratiBaza.tabele.Racuni;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.Opsti;
import rs.atekom.prati.view.OpstiViewInterface;


public class RacuniView extends Opsti implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "zbirni";
	private Grid<Racuni> tabela;
	private ListDataProvider<Racuni> dataProvider;
	private SerializablePredicate<Racuni> filterPredicate;
	private ArrayList<Racuni> pocetno, lista;
	public RacuniLogika viewLogika;
	public RacuniForma forma;
	public Racuni izabrani;
	public ZbirniRacuniView zbirni;
	
	public RacuniView(ZbirniRacuniView zbirni) {
		if(zbirni != null) {
			this.zbirni = zbirni;
		}
		viewLogika = new RacuniLogika(this);
		forma = new RacuniForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Racuni>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Racuni> event) {
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

		viewLogika.init();
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Racuni>();
		pocetno = new ArrayList<Racuni>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(racuni -> racuni.getSistemPretplatnici() == null ? "" : racuni.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Racuni::getDatum, new DateRenderer(DANFORMAT)).setCaption("датум").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(racuni -> racuni.getPartner() == null ? "" : racuni.getPartner().getNaziv()).setCaption("партнер");
		tabela.addColumn(Racuni::getBrojRacuna).setCaption("број рачуна");
		tabela.addColumn(racuni -> racuni.getOpis() == null ? "" : racuni.getOpis()).setCaption("опис");
		tabela.addColumn(racuni -> racuni.getUradio() == null ? "" : racuni.getUradio().toString()).setCaption("урадио");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(racuni -> racuni.getOrganizacija() == null ? "" : racuni.getOrganizacija() == null ? "" : racuni.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(racuni -> {CheckBox chb = new CheckBox(); if(racuni.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Racuni::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Racuni::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Racuni)red);
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
		Racuni racun = (Racuni)podatak;
		if(racun != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(racun);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.racunServis.izbrisiRacun(izabrani);
				pokaziPorukuUspesno("рачун избрисан");
			}else {
				pokaziPorukuGreska("рачун већ избрисан!");
			}
		}
		
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.racunServis.nadjiRacunePoPretplatniku(korisnik.getSistemPretplatnici(), korisnik.getOrganizacija(), true, null, null, null);
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
		dataProvider = (ListDataProvider<Racuni>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Racuni>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Racuni t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getBrojRacuna() == null ? "" : t.getBrojRacuna()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getPartner() == null ? "" : t.getPartner().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
