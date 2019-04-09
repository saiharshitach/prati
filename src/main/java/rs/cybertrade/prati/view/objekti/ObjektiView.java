package rs.cybertrade.prati.view.objekti;

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
import pratiBaza.tabele.Objekti;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("objekti") // an empty view name will also be the default view
@MenuCaption("Објекти")
@MenuIcon(VaadinIcons.CAR)
public class ObjektiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Objekti> tabela;
	private ListDataProvider<Objekti> dataProvider;
	private SerializablePredicate<Objekti> filterPredicate;
	private ArrayList<Objekti> pocetno, lista;
	
	public ObjektiView() {
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
		tabela = new Grid<Objekti>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(objekti -> objekti.getSistemPretplatnici() == null ? "" : objekti.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Objekti::getOznaka).setCaption("ознака");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getKod()).setCaption("уређај");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSerijskiBr()).setCaption("уређај сер.бр");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSim() == null ? "" : 
			objekti.getUredjaji().getSim().getBroj()).setCaption("сим број");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSim() == null ? "" : objekti.getUredjaji().getSim().getIccid()).setCaption("сим иццид");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.getTip()) {chb.setValue(true);} return chb;}).setCaption("возило").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isDetalji()) {chb.setValue(true);} return chb;}).setCaption("детаљи").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(objekti -> objekti.getOrganizacija() == null ? "" : objekti.getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Objekti::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Objekti::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("уписано").setStyleGenerator(objekti -> "v-align-right");
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
		pocetno = new ArrayList<Objekti>();
		lista = Servis.objekatServis.vratiSveObjekte(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Objekti>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Objekti t) {
				return (t.getOznaka().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getSistemPretplatnici().getNaziv().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getKod().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSerijskiBr().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSim().getBroj().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSim().getIccid().toLowerCase().contains(filter.getValue().toLowerCase()));
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
