package rs.atekom.prati.view.vozila.saobracajna2;

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

import pratiBaza.tabele.VozilaSaobracajne;
import pratiBaza.tabele.VozilaSaobracajne2;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("saobracajna2")
@MenuCaption("Саобраћајна 2")
@MenuIcon(VaadinIcons.BOOKMARK_O)
public class VozilaSaobracajna2View extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "saobracajna2";
	private Grid<VozilaSaobracajne2> tabela;
	private ListDataProvider<VozilaSaobracajne2> dataProvider;
	private SerializablePredicate<VozilaSaobracajne2> filterPredicate;
	private ArrayList<VozilaSaobracajne2> pocetno, lista;
	private VozilaSaobracajna2Logika viewLogika;
	private VozilaSaobracajna2Forma forma;
	private VozilaSaobracajne2 izabrani;

	public VozilaSaobracajna2View() {
		viewLogika = new VozilaSaobracajna2Logika(this);
		forma = new VozilaSaobracajna2Forma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozilaSaobracajne2>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozilaSaobracajne2> event) {
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
		tabela = new Grid<VozilaSaobracajne2>();
		pocetno = new ArrayList<VozilaSaobracajne2>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(vozilaSaobracajna2 -> vozilaSaobracajna2.getSistemPretplatnici() == null ? ""
					: vozilaSaobracajna2.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(saobracajna2 -> saobracajna2.getSaobracajna() == null ? "" : saobracajna2.getSaobracajna().getVozilo() == null ? "" :
			saobracajna2.getSaobracajna().getVozilo().getObjekti() == null ? "" : saobracajna2.getSaobracajna().getVozilo().getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(saobracajna2 -> saobracajna2.getSaobracajna() == null ? "" : saobracajna2.getSaobracajna().getVozilo() == null ? "" :
			saobracajna2.getSaobracajna().getVozilo().getRegistracija()).setCaption("возило");
		tabela.addColumn(saobracajna2 -> saobracajna2.getSaobracajna() == null ? "" : 
			saobracajna2.getSaobracajna().getBrojSaobracajne()).setCaption("број саобраћајне").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaSaobracajne2::getVlasnik).setCaption("власник");
		tabela.addColumn(VozilaSaobracajne2::getAdresaVlasnika).setCaption("адреса власника");
		tabela.addColumn(VozilaSaobracajne2::getJmbgVlasnika).setCaption("јмбг власника");
		tabela.addColumn(VozilaSaobracajne2::getKorisnik).setCaption("корисник");
		tabela.addColumn(VozilaSaobracajne2::getVlasnik).setCaption("власник");
		tabela.addColumn(VozilaSaobracajne2::getDimenzijeGuma).setCaption("димензије гума");
		tabela.addColumn(VozilaSaobracajne2::getPritisakGume).setCaption("притиска гуме");
		tabela.addColumn(VozilaSaobracajne2::getDimenzijeTovara).setCaption("димензије товара");
		tabela.addColumn(VozilaSaobracajne2::getOdnosSnagaMasa).setCaption("однос снага/маса");
		tabela.addColumn(VozilaSaobracajne2::getMestaStajanje).setCaption("места за стајање");
		tabela.addColumn(VozilaSaobracajne2::getVlasnik).setCaption("власник");
		tabela.addColumn(VozilaSaobracajne2::getKupljenoDoniranoOd).setCaption("купљено/донирано од");
		tabela.addColumn(VozilaSaobracajne2::getNabavljenoPoRacunuSert).setCaption("набављено по рачуну/серт");
		tabela.addColumn(VozilaSaobracajne2::getDobavljacDonator).setCaption("добављач/донатор");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(saobracajna2 -> saobracajna2.getSaobracajna() == null ? "" : saobracajna2.getSaobracajna().getVozilo() == null ? "" : 
				saobracajna2.getSaobracajna().getVozilo().getObjekti() == null ? "" : saobracajna2.getSaobracajna().getVozilo().getObjekti().getOrganizacija() == null ? "" :
					saobracajna2.getSaobracajna().getVozilo().getObjekti().getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(saobracajne2 -> {CheckBox chb = new CheckBox(); if(saobracajne2.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(VozilaSaobracajne2::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaSaobracajne2::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((VozilaSaobracajne2)red);
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
		VozilaSaobracajne2 saobracajna2 = (VozilaSaobracajne2)podatak;
		if(saobracajna2 != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(saobracajna2);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				VozilaSaobracajne izabrana = izabrani.getSaobracajna();
				izabrana.setSaobracajna2(null);
				Servis.saobracajna2Servis.izbrisiSaobracajnu2(izabrani);
				Servis.saobracajnaServis.izmeniSaobracajnu(izabrana);
				pokaziPorukuUspesno("саобраћајна2 избрисана");
			}else {
				pokaziPorukuGreska("саобраћајна2 већ избрисана!");
			}
		}
		
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.saobracajna2Servis.nadjiSveSaobracajne2(korisnik, false);
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
		dataProvider = (ListDataProvider<VozilaSaobracajne2>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozilaSaobracajne2>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozilaSaobracajne2 t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSaobracajna() == null ? "" : t.getSaobracajna().getBrojSaobracajne()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSaobracajna() == null ? "" : t.getSaobracajna().getVozilo() == null ? "" : t.getSaobracajna().getVozilo().getRegistracija()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSaobracajna() == null ? "" : t.getSaobracajna().getVozilo() == null ? "" : 
							t.getSaobracajna().getVozilo().getObjekti() == null ? "" : t.getSaobracajna().getVozilo().getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
	}

}
