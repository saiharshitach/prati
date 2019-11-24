package rs.atekom.prati.view.vozila.saobracajna;

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
import pratiBaza.tabele.VozilaSaobracajne;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("saobracajna")
@MenuCaption("Саобраћајна")
@MenuIcon(VaadinIcons.BOOKMARK)
public class VozilaSaobracajnaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "saobracajna";
	private Grid<VozilaSaobracajne> tabela;
	private ListDataProvider<VozilaSaobracajne> dataProvider;
	private SerializablePredicate<VozilaSaobracajne> filterPredicate;
	private ArrayList<VozilaSaobracajne> pocetno, lista;
	private VozilaSaobracajnaLogika viewLogika;
	private VozilaSaobracajnaForma forma;
	private VozilaSaobracajne izabrani;

	public VozilaSaobracajnaView() {
		viewLogika = new VozilaSaobracajnaLogika(this);
		forma = new VozilaSaobracajnaForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozilaSaobracajne>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozilaSaobracajne> event) {
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
		tabela = new Grid<VozilaSaobracajne>();
		pocetno = new ArrayList<VozilaSaobracajne>();
		updateTable();
		dodajFilter();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(vozilaSaobracajna -> vozilaSaobracajna.getSistemPretplatnici() == null ? "" 
					: vozilaSaobracajna.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozilaSaobracajna -> vozilaSaobracajna.getVozilo() == null ? "" : vozilaSaobracajna.getVozilo().getObjekti() == null ? "" : vozilaSaobracajna.getVozilo().getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(vozilaSaobracajna -> vozilaSaobracajna.getVozilo() == null ? "" : vozilaSaobracajna.getVozilo().getRegistracija()).setCaption("возило");
		tabela.addColumn(VozilaSaobracajne::getBrojSaobracajne).setCaption("број саобраћајне");
		tabela.addColumn(VozilaSaobracajne::getDatumIzdavanja, new DateRenderer(DANFORMAT)).setCaption("датум издавања").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaSaobracajne::getIzdao).setCaption("издао");
		tabela.addColumn(VozilaSaobracajne::getHomologacija).setCaption("хомологација");
		tabela.addColumn(VozilaSaobracajne::getSasija).setCaption("број шасије");
		tabela.addColumn(VozilaSaobracajne::getBrojMotora).setCaption("број мотора");
		tabela.addColumn(VozilaSaobracajne::getSnagaMotora).setCaption("снага мотора");
		tabela.addColumn(VozilaSaobracajne::getZapreminaMotora).setCaption("запремина мотора");
		tabela.addColumn(VozilaSaobracajne::getZapreminaRezervoara).setCaption("резервоар");
		tabela.addColumn(VozilaSaobracajne::getZapreminaRezervoaraAdBlue).setCaption("adblue");
		tabela.addColumn(VozilaSaobracajne::getBoja).setCaption("боја");
		tabela.addColumn(VozilaSaobracajne::getMasa).setCaption("маса");
		tabela.addColumn(VozilaSaobracajne::getUkupnaMasa).setCaption("укупна маса");
		tabela.addColumn(VozilaSaobracajne::getKategorija).setCaption("категорија");
		tabela.addColumn(VozilaSaobracajne::getNosivost).setCaption("носивост");
		tabela.addColumn(VozilaSaobracajne::getMestaSedenja).setCaption("места за седење");
		tabela.addColumn(VozilaSaobracajne::getBrojSaobracajne).setCaption("број саобраћајне");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
					tabela.addColumn(vozilaSaobracajna -> vozilaSaobracajna.getVozilo() == null ? "" : vozilaSaobracajna.getVozilo().getOrganizacija() == null ? "" : 
			vozilaSaobracajna.getVozilo().getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(vozilaSaobracajne -> {CheckBox chb = new CheckBox(); if(vozilaSaobracajne.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(VozilaSaobracajne::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaSaobracajne::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((VozilaSaobracajne)red);
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
		VozilaSaobracajne saobracajna = (VozilaSaobracajne)podatak;
		if(saobracajna != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(saobracajna);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Vozila izabrano = izabrani.getVozilo();
				izabrano.setSaobracajna(null);
				Servis.saobracajnaServis.izbrisiSaobracajnu(izabrani);
				Servis.voziloServis.azurirajVozilo(izabrano);
				pokaziPorukuUspesno("саобраћајна избрисана");
			}else {
				pokaziPorukuGreska("саобраћајна већ избрисана!");
			}
		}
		
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.saobracajnaServis.nadjiSveSaobracajne(korisnik, false);
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
		dataProvider = (ListDataProvider<VozilaSaobracajne>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozilaSaobracajne>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozilaSaobracajne t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getBrojSaobracajne() == null ? "" : t.getBrojSaobracajne()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getVozilo().getObjekti() == null ? "" : t.getVozilo().getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
