package rs.atekom.prati.view.vozaci;

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
import pratiBaza.tabele.Vozaci;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("vozaci")
@MenuCaption("Возачи")
@MenuIcon(VaadinIcons.USER_CHECK)
public class VozaciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "korisnici";
	private Grid<Vozaci> tabela;
	private ListDataProvider<Vozaci> dataProvider;
	private SerializablePredicate<Vozaci> filterPredicate;
	private ArrayList<Vozaci> pocetno, lista;
	private VozaciLogika viewLogika;
	public VozaciForma forma;
	private Vozaci izabrani;
	
	public VozaciView() {
		viewLogika = new VozaciLogika(this);
		forma = new VozaciForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Vozaci>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Vozaci> event) {
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
		tabela = new Grid<Vozaci>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(vozaci -> vozaci.getSistemPretplatnici() == null ? "" : vozaci.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(vozaci -> vozaci.getKorisnici() == null ? "" : vozaci.getKorisnici().toString()).setCaption("корисник");
		tabela.addColumn(Vozaci::getJmbg).setCaption("јмбг");
		tabela.addColumn(Vozaci::getPrebivaliste).setCaption("пребивалиште");
		tabela.addColumn(Vozaci::getZaposlenOd, new DateRenderer(DANFORMAT)).setCaption("запослен од").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Vozaci::getZaposlenDo, new DateRenderer(DANFORMAT)).setCaption("запослен до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(vozaci -> vozaci.getKorisnici() == null ? "" : vozaci.getKorisnici().getOrganizacija() == null ? "" : 
			vozaci.getKorisnici().getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(vozaci -> {CheckBox chb = new CheckBox(); if(vozaci.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(Vozaci::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Vozaci::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((Vozaci)red);
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
		Vozaci vozac = (Vozaci)podatak;
		if(vozac != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(vozac);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.vozacServis.izbrisiVozaca(izabrani);
				pokaziPorukuUspesno("подаци за возача " + izabrani.getKorisnici().getIme() + " " + izabrani.getKorisnici().getPrezime() + " избрисани");
			}else {
				pokaziPorukuGreska("подаци за возача су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Vozaci>();
		lista = Servis.vozacServis.nadjiSveVozace(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Vozaci>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Vozaci>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Vozaci t) {
				return (((t.getKorisnici() == null ? "" : t.getKorisnici().toString().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getJmbg() == null ? "" : t.getJmbg()).toLowerCase().contains(filter.getValue().toLowerCase())));
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
