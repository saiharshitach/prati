package rs.atekom.prati.view.vozilo.primoPredaje;

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
import pratiBaza.tabele.VozilaPrimoPredaje;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;


@NavigatorViewName("primoPredaja")
@MenuCaption("Примопредаја")
@MenuIcon(VaadinIcons.CLIPBOARD_CHECK)
public class VozilaPrimoPredajeView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "primoPredaja";
	private Grid<VozilaPrimoPredaje> tabela;
	private ListDataProvider<VozilaPrimoPredaje> dataProvider;
	private SerializablePredicate<VozilaPrimoPredaje> filterPredicate;
	private ArrayList<VozilaPrimoPredaje> pocetno, lista;
	private VozilaPrimoPredajeLogika viewLogika;
	private VozilaPrimoPredajeForma forma;
	private VozilaPrimoPredaje izabrani;

	public VozilaPrimoPredajeView() {
		viewLogika = new VozilaPrimoPredajeLogika(this);
		forma = new VozilaPrimoPredajeForma(viewLogika);
		forma = new VozilaPrimoPredajeForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozilaPrimoPredaje>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozilaPrimoPredaje> event) {
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
		tabela = new Grid<VozilaPrimoPredaje>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getSistemPretplatnici() == null ? "" : voziloPrimoPredaja.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(VozilaPrimoPredaje::getBroj).setCaption("број");
		tabela.addColumn(VozilaPrimoPredaje::getDatum, new DateRenderer(DANFORMAT)).setCaption("датум").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getVozilo().getObjekti() == null ? "" : voziloPrimoPredaja.getVozilo().getObjekti().getOznaka()).setCaption("возило");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getVozilo().getObjekti() == null ? "" : voziloPrimoPredaja.getVozilo().getRegistracija()).setCaption("регистрација");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getVozacPredaja().getKorisnici() == null ? "" : voziloPrimoPredaja.getVozacPredaja().getKorisnici().toString()).setCaption("возач предаја");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getVozacPrijem().getKorisnici() == null ? "" : voziloPrimoPredaja.getVozacPrijem().getKorisnici().toString()).setCaption("возач пријем");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getAdministrator() == null ? "" : voziloPrimoPredaja.getAdministrator().toString()).setCaption("администратор");
		tabela.addColumn(VozilaPrimoPredaje::getKomentar).setCaption("коментар");
		tabela.addColumn(voziloPrimoPredaja -> voziloPrimoPredaja.getVozilo().getObjekti().getOrganizacija() == null ? "" : voziloPrimoPredaja.getVozilo().getObjekti().getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(voziloPrimoPredaja -> {CheckBox chb = new CheckBox(); if(voziloPrimoPredaja.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozilaPrimoPredaje::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaPrimoPredaje::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozilaPrimoPredaje)red);
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
		VozilaPrimoPredaje primoPredaja = (VozilaPrimoPredaje)podatak;
		if(primoPredaja != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(primoPredaja);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.primoPredajaServis.izbrisiVoziloPrimoPredaja(izabrani);
				pokaziPorukuUspesno("подаци за примопредају " + izabrani.getBroj() + " су избрисани");
			}else {
				pokaziPorukuGreska("подаци за примопредају су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozilaPrimoPredaje>();
		lista = Servis.primoPredajaServis.nadjiSveVozilaPrimoPredaje(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozilaPrimoPredaje>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozilaPrimoPredaje>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozilaPrimoPredaje t) {
				return (((t.getVozacPredaja() == null ? "" : t.getVozacPredaja().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						((t.getVozacPrijem() == null ? "" : t.getVozacPrijem().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						((t.getVozilo() == null ? "" : t.getVozilo().toString().toLowerCase()).contains(filter.getValue().toLowerCase())) ||
						(t.getBroj() == null ? "" : t.getBroj().toLowerCase()).contains(filter.getValue().toLowerCase()));
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
