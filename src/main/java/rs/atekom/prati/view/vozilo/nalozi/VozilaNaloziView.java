package rs.atekom.prati.view.vozilo.nalozi;

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
import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("nalog")
@MenuCaption("Налог")
@MenuIcon(VaadinIcons.CLIPBOARD)
public class VozilaNaloziView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "nalog";
	private Grid<VozilaNalozi> tabela;
	private ListDataProvider<VozilaNalozi> dataProvider;
	private SerializablePredicate<VozilaNalozi> filterPredicate;
	private ArrayList<VozilaNalozi> pocetno, lista;
	private VozilaNaloziLogika viewLogika;
	public VozilaNaloziForma forma;
	private VozilaNalozi izabrani;

	public VozilaNaloziView() {
		viewLogika = new VozilaNaloziLogika(this);
		forma = new VozilaNaloziForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozilaNalozi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozilaNalozi> event) {
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
		tabela = new Grid<VozilaNalozi>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(voziloNalog -> voziloNalog.getSistemPretplatnici() == null ? "" : voziloNalog.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(VozilaNalozi::getBrojNaloga).setCaption("број налога");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozilo().getObjekti() == null ? "" : voziloNalog.getVozilo().getObjekti().getOznaka()).setCaption("возило");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozilo() == null ? "" : voziloNalog.getVozilo().getRegistracija()).setCaption("регистрација");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozac() == null ? "" : voziloNalog.getVozac().getKorisnici().toString()).setCaption("возач");
		tabela.addColumn(VozilaNalozi::getOdMesta).setCaption("од места");
		tabela.addColumn(VozilaNalozi::getDoMesta).setCaption("до места");
		tabela.addColumn(VozilaNalozi::getMedjuTacke).setCaption("међу тачке");
		tabela.addColumn(VozilaNalozi::getOcekivaniPolazak, new DateRenderer(DANSATFORMAT)).setCaption("очекивани полазак").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaNalozi::getOcekivaniDolazak, new DateRenderer(DANSATFORMAT)).setCaption("очекивани долазак").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozac() == null ? "" : voziloNalog.getVozac().toString()).setCaption("возач");
		tabela.addColumn(VozilaNalozi::getKomentar).setCaption("коментар");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozilo().getObjekti().getOrganizacija() == null ? "" : voziloNalog.getVozilo().getObjekti().getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addColumn(voziloNalog -> voziloNalog.getVozilo() == null ? "" : voziloNalog.getVozilo().getObjekti().getOrganizacija() == null ? "" : voziloNalog.getVozilo().getObjekti().getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(voziloNalog -> {CheckBox chb = new CheckBox(); if(voziloNalog.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozilaNalozi::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaNalozi::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozilaNalozi)red);
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
		VozilaNalozi nalog = (VozilaNalozi)podatak;
		if(nalog != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(nalog);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.nalogServis.izbrisiVoziloNalog(izabrani);
				pokaziPorukuUspesno("подаци за налог " + izabrani.getBrojNaloga() + " су избрисани");
			}else {
				pokaziPorukuGreska("подаци за налог су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozilaNalozi>();
		lista = Servis.nalogServis.nadjiSveVozilaNaloge(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozilaNalozi>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozilaNalozi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozilaNalozi t) {
				return (((t.getVozac().getKorisnici() == null ? "" : t.getVozac().getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						((t.getVozilo().getObjekti() == null ? "" : t.getVozilo().getObjekti().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						((t.getVozilo() == null ? "" : t.getVozilo().getRegistracija().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						(t.getBrojNaloga() == null ? "" : t.getBrojNaloga().toLowerCase()).contains(filter.getValue().toLowerCase()));
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
