package rs.atekom.prati.view.vozilo.oprema;

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
import pratiBaza.tabele.VozilaOprema;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("oprema")
@MenuCaption("Опрема")
@MenuIcon(VaadinIcons.TOOLBOX)
public class VozilaOpremaView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "oprema";
	private Grid<VozilaOprema> tabela;
	private ListDataProvider<VozilaOprema> dataProvider;
	private SerializablePredicate<VozilaOprema> filterPredicate;
	private ArrayList<VozilaOprema> pocetno, lista;
	private VozilaOpremaLogika viewLogika;
	public VozilaOpremaForma forma;
	private VozilaOprema izabrani;
	
	public VozilaOpremaView() {
		viewLogika = new VozilaOpremaLogika(this);
		forma = new VozilaOpremaForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<VozilaOprema>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<VozilaOprema> event) {
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
		tabela = new Grid<VozilaOprema>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(voziloOprema -> voziloOprema.getSistemPretplatnici() == null ? "" : voziloOprema.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(voziloOprema -> voziloOprema.getNaziv() == null ? "" : voziloOprema.getNaziv()).setCaption("назив");
		tabela.addColumn(voziloOprema -> voziloOprema.getOpis() == null ? "" : voziloOprema.getOpis()).setCaption("опис");
		if(isAdmin()) {
			tabela.addComponentColumn(voziloOprema -> {CheckBox chb = new CheckBox(); if(voziloOprema.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(vozaci -> "v-align-right");
		}
		tabela.addColumn(VozilaOprema::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(VozilaOprema::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((VozilaOprema)red);
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
		VozilaOprema oprema = (VozilaOprema)podatak;
		if(oprema != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(oprema);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.opremaServis.izbrisiVoziloOpremu(izabrani);
				pokaziPorukuUspesno("подаци за опрему " + izabrani.getNaziv() + " су избрисани");
			}else {
				pokaziPorukuGreska("подаци за опрему су већ избрисани!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<VozilaOprema>();
		lista = Servis.opremaServis.nadjiSveVozilaOprema(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<VozilaOprema>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<VozilaOprema>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(VozilaOprema t) {
				return ((t.getNaziv() == null ? "" : t.getNaziv().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getOpis() == null ? "" : t.getOpis().toLowerCase()).contains(filter.getValue().toLowerCase()));
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
