package rs.atekom.prati.view.uredjaji;

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
import pratiBaza.tabele.Uredjaji;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("uredjaji") // an empty view name will also be the default view
@MenuCaption("Уређаји")
@MenuIcon(VaadinIcons.MOBILE)
public class UredjajiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "uredjaji";
	private Grid<Uredjaji> tabela;
	private ListDataProvider<Uredjaji> dataProvider;
	private SerializablePredicate<Uredjaji> filterPredicate;
	private ArrayList<Uredjaji> pocetno, lista;
	private UredjajiLogika viewLogika;
	private UredjajiForma forma;
	private Uredjaji izabrani;
	
	public UredjajiView() {
		viewLogika = new UredjajiLogika(this);
		forma = new UredjajiForma(viewLogika);
		forma.setEnabled(false);
		forma.removeStyleName("visible");
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Uredjaji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Uredjaji> event) {
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
		tabela = new Grid<Uredjaji>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isAdmin()) {
			tabela.addColumn(uredjaji -> uredjaji.getSistemPretplatnici() == null ? "" : uredjaji.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Uredjaji::getSerijskiBr).setCaption("серијски број");
		tabela.addColumn(Uredjaji::getKod).setCaption("код");
		tabela.addColumn(uredjaji -> uredjaji.getSistemUredjajiModeli() == null ? "": uredjaji.getSistemUredjajiModeli().getNaziv()).setCaption("модел");		
		tabela.addColumn(uredjaji -> uredjaji.getSistemUredjajiModeli() == null ? "" : uredjaji.getSistemUredjajiModeli().getSistemUredjajiProizvodjac() == null ? "" : 
			uredjaji.getSistemUredjajiModeli().getSistemUredjajiProizvodjac().getNaziv()).setCaption("произвођач");		
		tabela.addColumn(uredjaji -> uredjaji.getSim() == null ? "" : uredjaji.getSim().getBroj()).setCaption("сим број");
		tabela.addColumn(uredjaji -> uredjaji.getSim() == null ? "" : uredjaji.getSim().getIccid()).setCaption("сим iccid");
		tabela.addColumn(uredjaji -> uredjaji.getObjekti() == null ? "" : uredjaji.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addComponentColumn(uredjaji -> {CheckBox chb = new CheckBox(); if(uredjaji.isAktivno()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(uredjaji -> uredjaji.getOrganizacija() == null ? "" : uredjaji.getOrganizacija().getNaziv()).setCaption("организација");
		if(isAdmin()) {
			tabela.addComponentColumn(uredjaji -> {CheckBox chb = new CheckBox(); if(uredjaji.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		}
		tabela.addColumn(Uredjaji::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Uredjaji::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
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
		tabela.getSelectionModel().select((Uredjaji)red);
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
		Uredjaji uredjaj = (Uredjaji)podatak;
		if(uredjaj != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(uredjaj);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.uredjajServis.izbrisiUredjaj(izabrani);
				pokaziPorukuUspesno("уређај " + izabrani.getSerijskiBr() + " је избрисан");
			}else {
				pokaziPorukuGreska("уређај је већ избрисан!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Uredjaji>();
		lista = Servis.uredjajServis.nadjiSveUredjaje(korisnik, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Uredjaji>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Uredjaji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Uredjaji t) {
				return (t.getKod().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSerijskiBr() == null ? "" : t.getSerijskiBr()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).contains(filter.getValue().toLowerCase()) ||
						(t.getSim() == null ? "" : t.getSim().getBroj()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSistemUredjajiModeli() == null ? "" : t.getSistemUredjajiModeli().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
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
