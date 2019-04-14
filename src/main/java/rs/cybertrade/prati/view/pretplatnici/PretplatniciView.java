package rs.cybertrade.prati.view.pretplatnici;

import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import pratiBaza.tabele.SistemPretplatnici;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("pretplatnici") // an empty view name will also be the default view
@MenuCaption("Претплатници")
@MenuIcon(VaadinIcons.BRIEFCASE)
public class PretplatniciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<SistemPretplatnici> tabela;
	private ListDataProvider<SistemPretplatnici> dataProvider;
	private SerializablePredicate<SistemPretplatnici> filterPredicate;
	private ArrayList<SistemPretplatnici> pocetno, lista;

	public PretplatniciView() {
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		addComponent(barGrid);
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<SistemPretplatnici>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(SistemPretplatnici::getNaziv).setCaption("назив");
		tabela.addColumn(SistemPretplatnici::getEmail).setCaption("е-пошта");
		tabela.addColumn(SistemPretplatnici::getAktivanDo, new DateRenderer(DANFORMAT)).setCaption("активан до");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isgMapa()) {chb.setValue(true); }return chb;}).setCaption("гугл мапа").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getApiKey).setCaption("апи кључ");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isAktivan()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addComponentColumn(sistemPretplatnici -> {CheckBox chb = new CheckBox(); if(sistemPretplatnici.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(sistemPretplatnici -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(SistemPretplatnici::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void izaberiRed(Object red) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object dajIzabraniRed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ukloniPodatak() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<SistemPretplatnici>();
		lista = Servis.sistemPretplatnikServis.nadjiSvePretplatnike();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemPretplatnici>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemPretplatnici t) {
				return (t.getNaziv().toLowerCase().contains(filter.getValue().toLowerCase()));
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
