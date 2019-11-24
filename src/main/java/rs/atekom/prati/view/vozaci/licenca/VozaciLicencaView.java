package rs.atekom.prati.view.vozaci.licenca;

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
import pratiBaza.tabele.VozaciLicence;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("licenca")
@MenuCaption("Лиценца")
@MenuIcon(VaadinIcons.MEDAL)
public class VozaciLicencaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "licenca";
	private Grid<VozaciLicence> tabela;
	private ListDataProvider<VozaciLicence> dataProvider;
	private SerializablePredicate<VozaciLicence> filterPredicate;
	private ArrayList<VozaciLicence> pocetno, lista;
	private VozaciLicencaLogika viewLogika;
	public VozaciLicencaForma forma;
	private VozaciLicence izabrani;
	
	public VozaciLicencaView() {
		viewLogika = new VozaciLicencaLogika(this);
		forma = new VozaciLicencaForma(viewLogika);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozaciLicence>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozaciLicence> event) {
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
		tabela = new Grid<VozaciLicence>();
		pocetno = new ArrayList<VozaciLicence>();
		updateTable();
		dodajFilter();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isSistem()) {
			tabela.addColumn(vozaciLicenca -> vozaciLicenca.getSistemPretplatnici() == null ? "" : vozaciLicenca.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaciLicenca -> vozaciLicenca.getVozaci() == null ? "" : vozaciLicenca.getVozaci().toString()).setCaption("корисник");
		tabela.addColumn(VozaciLicence::getBroj).setCaption("број");
		tabela.addColumn(VozaciLicence::getIzdao).setCaption("издао");
		tabela.addColumn(VozaciLicence::getIzdato, new DateRenderer(DANFORMAT)).setCaption("издато").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLicence::getVaziDo, new DateRenderer(DANFORMAT)).setCaption("важеће до").setStyleGenerator(objekti -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
					tabela.addColumn(vozaciLicenca -> vozaciLicenca.getVozaci() == null ? "" : vozaciLicenca.getVozaci().getOrganizacija() == null ? "" : 
						vozaciLicenca.getVozaci().getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(vozaciLicenca -> {CheckBox chb = new CheckBox(); if(vozaciLicenca.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaciLicenca -> "v-align-right");
		}
		tabela.addColumn(VozaciLicence::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLicence::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozaciLicence)red);
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
		VozaciLicence licenca = (VozaciLicence)podatak;
		if(licenca != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(licenca);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.licencaServis.izbrisiVozacLicenca(izabrani);
				pokaziPorukuUspesno("подаци за лиценцу " + izabrani.getVozaci().toString() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за лиценцу су већ избрисани!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.licencaServis.nadjiSveVozacLicenca(korisnik);
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
		dataProvider = (ListDataProvider<VozaciLicence>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozaciLicence>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozaciLicence t) {
				return (((t.getVozaci() == null ? "" : t.getVozaci().toString().toLowerCase()).contains(filter.getValue().toLowerCase()))||
						((t.getBroj() == null ? "" : t.getBroj().toLowerCase()).contains(filter.getValue().toLowerCase())));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
