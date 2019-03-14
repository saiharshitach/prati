package rs.cybertrade.prati.view.sim;

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

import pratiBaza.tabele.Sim;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OsnovniView;

@NavigatorViewName("sim") // an empty view name will also be the default view
@MenuCaption("СИМ")
@MenuIcon(VaadinIcons.CREDIT_CARD)
public class SimView extends OsnovniView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Sim> tabela;
	private ListDataProvider<Sim> dataProvider;
	private SerializablePredicate<Sim> filterPredicate;
	private ArrayList<Sim> pocetno, lista;

	public SimView() {
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
		tabela = new Grid<Sim>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(Sim::getBroj).setCaption("број");
		tabela.addColumn(Sim::getIccid).setCaption("иццид");
		tabela.addColumn(sim -> sim.getSistemOperateri() == null ? "" : sim.getSistemOperateri().getNaziv()).setCaption("оператер");
		tabela.addColumn(sim -> sim.getSistemPretplatnici() == null ? "" : sim.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getSistemUredjajiModeli() == null ? "" :
			sim.getUredjaji().getSistemUredjajiModeli().getNaziv()).setCaption("модел");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getSerijskiBr()).setCaption("уређај");
		tabela.addColumn(sim -> sim.getUredjaji() == null ? "" : sim.getUredjaji().getObjekti() == null ? "" : 
			sim.getUredjaji().getObjekti().getOznaka()).setCaption("објекат");
		tabela.addComponentColumn(sim -> {CheckBox chb = new CheckBox(); if(sim.isAktivno()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(sim -> {CheckBox chb = new CheckBox(); if(sim.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Sim::getIzmenjeno, new DateRenderer(DANFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Sim::getKreirano, new DateRenderer(DANFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
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
	public void izmeniPodatak() {
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
		pocetno = new ArrayList<Sim>();
		lista = Servis.simServis.vratiSveSimKartice();
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Sim>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Sim>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Sim t) {
				return (t.getBroj().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getIccid().toLowerCase().contains(filter.getValue().toLowerCase()));
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
