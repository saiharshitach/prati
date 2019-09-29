package rs.atekom.prati.view.vozaci.licna;

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
import pratiBaza.tabele.VozaciLicna;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("licna")
@MenuCaption("Лична карта")
@MenuIcon(VaadinIcons.HEALTH_CARD)
public class VozaciLicnaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "licna";
	private Grid<VozaciLicna> tabela;
	private ListDataProvider<VozaciLicna> dataProvider;
	private SerializablePredicate<VozaciLicna> filterPredicate;
	private ArrayList<VozaciLicna> pocetno, lista;
	private VozaciLicnaLogika viewLogika;
	public VozaciLicnaForma forma;
	private VozaciLicna izabrani;
	
	public VozaciLicnaView() {
		viewLogika = new VozaciLicnaLogika(this);
		forma = new VozaciLicnaForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozaciLicna>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozaciLicna> event) {
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
		tabela = new Grid<VozaciLicna>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(vozaciLicna -> vozaciLicna.getSistemPretplatnici() == null ? "" : vozaciLicna.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaciLicna -> vozaciLicna.getVozaci().getKorisnici() == null ? "" : vozaciLicna.getVozaci().getKorisnici().toString()).setCaption("возач");
		tabela.addColumn(VozaciLicna::getBroj).setCaption("број");
		tabela.addColumn(VozaciLicna::getIzdao).setCaption("издао");
		tabela.addColumn(VozaciLicna::getIzdato, new DateRenderer(DANFORMAT)).setCaption("издато").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLicna::getVaziDo, new DateRenderer(DANFORMAT)).setCaption("важеће до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(vozaciLicna -> vozaciLicna.getVozaci().getKorisnici().getOrganizacija() == null ? "" : vozaciLicna.getVozaci().getKorisnici().getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(vozaciLicna -> {CheckBox chb = new CheckBox(); if(vozaciLicna.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaciLicna -> "v-align-right");
		}
		tabela.addColumn(VozaciLicna::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLicna::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozaciLicna)red);
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
		VozaciLicna licna = (VozaciLicna)podatak;
		if(licna != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(licna);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.licnaServis.izbrisiVozacLicna(izabrani);
				pokaziPorukuUspesno("подаци за личну карту " + izabrani.getVozaci().getKorisnici().toString() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за личну карту су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozaciLicna>();
		lista = Servis.licnaServis.nadjiSveVozacLicna(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozaciLicna>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozaciLicna>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozaciLicna t) {
				return (((t.getVozaci().getKorisnici() == null ? "" : t.getVozaci().getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						((t.getBroj() == null ? "" : t.getBroj().toLowerCase()).contains(filter.getValue().toLowerCase())));
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
