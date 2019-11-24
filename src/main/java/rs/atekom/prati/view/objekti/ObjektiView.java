package rs.atekom.prati.view.objekti;

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
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("objekti") // an empty view name will also be the default view
@MenuCaption("Објекти")
@MenuIcon(VaadinIcons.LOCATION_ARROW_CIRCLE)
public class ObjektiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "objekti";
	private Grid<Objekti> tabela;
	private ListDataProvider<Objekti> dataProvider;
	private SerializablePredicate<Objekti> filterPredicate;
	private ArrayList<Objekti> pocetno, lista;
	private ObjektiLogika viewLogika;
	private ObjektiForma forma;
	private Objekti izabrani;
	
	public ObjektiView() {
		viewLogika = new ObjektiLogika(this);
		forma = new ObjektiForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Objekti> event) {
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
		tabela = new Grid<Objekti>();
		pocetno = new ArrayList<Objekti>();
		updateTable();
		dodajFilter();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(objekti -> objekti.getSistemPretplatnici() == null ? "" : objekti.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Objekti::getOznaka).setCaption("ознака");
		//tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getKod()).setCaption("уређај");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSerijskiBr()).setCaption("уређај сер.бр");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSim() == null ? "" : 
			objekti.getUredjaji().getSim().getBroj()).setCaption("сим број");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSim() == null ? "" : objekti.getUredjaji().getSim().getIccid()).setCaption("сим иццид");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.getTip()) {chb.setValue(true);} return chb;}).setCaption("возило").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Objekti::getVremeStajanja).setCaption("време стајања");
		tabela.addColumn(Objekti::getPrekoracenjeBrzine).setCaption("прекорачење брзине");
		//tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isDetalji()) {chb.setValue(true);} return chb;}).setCaption("детаљи").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(objekti -> "v-align-right");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(objekti -> objekti.getOrganizacija() == null ? "" : objekti.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Objekti::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Objekti::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("уписано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((Objekti)red);
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
		Objekti objekat = (Objekti)podatak;
		if(objekat != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(objekat);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.grupeObjekatServis.izbrisiSveGrupeObjekatPoObjektu(izabrani);
				Servis.zonaObjekatServis.izbrisiZoneObjektiPoObjektu(izabrani);
				Servis.objekatServis.izbrisiObjekte(izabrani);
				pokaziPorukuUspesno("корисник " + izabrani.getOznaka() + " је избрисан");
			}else {
				pokaziPorukuGreska("објекат је већ избрисан!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.objekatServis.vratiSveObjekte(korisnik, false);
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
		dataProvider = (ListDataProvider<Objekti>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Objekti t) {
				return (t.getOznaka().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getSistemPretplatnici().getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : t.getUredjaji().getKod()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : t.getUredjaji().getSerijskiBr()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : (t.getUredjaji().getSim() == null ? "" : t.getUredjaji().getSim().getBroj())).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : (t.getUredjaji().getSim() == null ? "" : t.getUredjaji().getSim().getIccid())).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getUredjaji() == null ? "" : (t.getUredjaji().getSistemUredjajiModeli() == null ? "" : t.getUredjaji().getSistemUredjajiModeli().getNaziv())).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}
	
}
