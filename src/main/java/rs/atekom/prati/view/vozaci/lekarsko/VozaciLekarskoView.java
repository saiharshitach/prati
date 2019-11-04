package rs.atekom.prati.view.vozaci.lekarsko;

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
import pratiBaza.tabele.VozaciLekarsko;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("lekarsko")
@MenuCaption("Лекарско")
@MenuIcon(VaadinIcons.DOCTOR_BRIEFCASE)
public class VozaciLekarskoView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "lekarsko";
	private Grid<VozaciLekarsko> tabela;
	private ListDataProvider<VozaciLekarsko> dataProvider;
	private SerializablePredicate<VozaciLekarsko> filterPredicate;
	private ArrayList<VozaciLekarsko> pocetno, lista;
	private VozaciLekarskoLogika viewLogika;
	public VozaciLekarskoForma forma;
	private VozaciLekarsko izabrani;
	
	public VozaciLekarskoView() {
		viewLogika = new VozaciLekarskoLogika(this);
		forma = new VozaciLekarskoForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozaciLekarsko>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozaciLekarsko> event) {
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
		tabela = new Grid<VozaciLekarsko>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isSistem()) {
			tabela.addColumn(vozaciLekarsko -> vozaciLekarsko.getSistemPretplatnici() == null ? "" : vozaciLekarsko.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaciLekarsko -> vozaciLekarsko.getVozaci() == null ? "" : vozaciLekarsko.getVozaci().getKorisnici() == null ? "" : 
			vozaciLekarsko.getVozaci().getKorisnici().toString()).setCaption("возач");
		tabela.addColumn(VozaciLekarsko::getIzdao).setCaption("издао");
		tabela.addColumn(VozaciLekarsko::getIzdato, new DateRenderer(DANFORMAT)).setCaption("издато").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLekarsko::getVaziDo, new DateRenderer(DANFORMAT)).setCaption("важеће до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLekarsko::getOpis).setCaption("опис");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
					tabela.addColumn(vozaciLekarsko -> vozaciLekarsko.getVozaci() == null ? "" : vozaciLekarsko.getVozaci().getKorisnici() == null ? "" : 
			vozaciLekarsko.getVozaci().getKorisnici().toString()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(vozaciLekarsko -> {CheckBox chb = new CheckBox(); if(vozaciLekarsko.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozaciLekarsko::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozaciLekarsko::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozaciLekarsko)red);
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
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		if(lekarsko != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(lekarsko);
	}
	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.lekarskoServis.izbrisiVozacLekarsko(izabrani);
				pokaziPorukuUspesno("подаци за лекарско уверење " + izabrani.getVozaci().getKorisnici().toString() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за лекарско уверење су већ избрисани!");
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozaciLekarsko>();
		lista = Servis.lekarskoServis.nadjiSveVozacLekarske(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozaciLekarsko>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozaciLekarsko>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozaciLekarsko t) {
				return (((t.getVozaci().getKorisnici() == null ? "" : t.getVozaci().getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase())));
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
