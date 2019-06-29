package rs.cybertrade.prati.view.objektidetalji;

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

import pratiBaza.tabele.ObjektiDetalji;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("objektiDetalji") // an empty view name will also be the default view
@MenuCaption("Објекти детаљи")
@MenuIcon(VaadinIcons.DASHBOARD)
public class ObjektiDetaljiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "objektiDetalji";
	private Grid<ObjektiDetalji> tabela;
	private ListDataProvider<ObjektiDetalji> dataProvider;
	private SerializablePredicate<ObjektiDetalji> filterPredicate;
	private ArrayList<ObjektiDetalji> pocetno, lista;
	private ObjektiDetaljiLogika viewLogika;
	private ObjektiDetaljiForma forma;
	private ObjektiDetalji izabrani;
	
	public ObjektiDetaljiView() {
		viewLogika = new ObjektiDetaljiLogika(this);
		forma = new ObjektiDetaljiForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<ObjektiDetalji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<ObjektiDetalji> event) {
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
		tabela = new Grid<ObjektiDetalji>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(isAdmin()) {
			tabela.addColumn(objektiDetalji -> objektiDetalji.getSistemPretplatnici() == null ? "" : objektiDetalji.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(objektiDetalji -> objektiDetalji.getObjekti() == null ? "" : objektiDetalji.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(ObjektiDetalji::getRegistracija).setCaption("регистрација");
		tabela.addColumn(ObjektiDetalji::getMarka).setCaption("марка");
		tabela.addColumn(ObjektiDetalji::getModel).setCaption("модел");
		tabela.addColumn(ObjektiDetalji::getTip).setCaption("тип");
		tabela.addColumn(ObjektiDetalji::getGodina).setCaption("година");
		tabela.addColumn(objektiDetalji -> objektiDetalji.getSistemGoriva() == null ? "" : objektiDetalji.getSistemGoriva().getNaziv()).setCaption("врста горива");
		tabela.addColumn(ObjektiDetalji::getPotrosnja).setCaption("потрошња");
		tabela.addComponentColumn(objektiDetalji -> {CheckBox chb = new CheckBox(); if(objektiDetalji.isTeretno()) {chb.setValue(true);} return chb;}).setCaption("теретно").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(ObjektiDetalji::getBrojSaobracajne).setCaption("број саобраћајне");
		tabela.addColumn(ObjektiDetalji::getSerijskiBroj).setCaption("серијски број");
		tabela.addColumn(ObjektiDetalji::getDatumRegistracije, new DateRenderer(DANFORMAT)).setCaption("датум прве регистрације").setStyleGenerator(objekti -> "v-align-right");
		if(isAdmin()) {
			tabela.addComponentColumn(objektiDetalji -> {CheckBox chb = new CheckBox(); if(objektiDetalji.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(ObjektiDetalji::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(ObjektiDetalji::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((ObjektiDetalji)red);
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
		ObjektiDetalji objekatDetalji = (ObjektiDetalji)podatak;
		if(objekatDetalji != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(objekatDetalji);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.objekatDetaljiServis.izbrisiObjektiDetalji(izabrani);
				pokaziPorukuUspesno("детаљи објекта су избрисани");
			}else {
				pokaziPorukuGreska("детаљи објекта је већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<ObjektiDetalji>();
		lista = Servis.objekatDetaljiServis.vratisveObjekatDetalje(korisnik, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<ObjektiDetalji>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<ObjektiDetalji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(ObjektiDetalji t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(filter.getValue().toLowerCase()));
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
