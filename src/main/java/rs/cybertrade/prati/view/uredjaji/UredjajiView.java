package rs.cybertrade.prati.view.uredjaji;

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

import pratiBaza.tabele.Uredjaji;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiViewInterface;
import rs.cybertrade.prati.view.OpstiView;

@NavigatorViewName("uredjaji") // an empty view name will also be the default view
@MenuCaption("Уређаји")
@MenuIcon(VaadinIcons.MOBILE)
public class UredjajiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Uredjaji> tabela;
	private ListDataProvider<Uredjaji> dataProvider;
	private SerializablePredicate<Uredjaji> filterPredicate;
	private ArrayList<Uredjaji> pocetno, lista;
	
	public UredjajiView() {
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
		tabela = new Grid<Uredjaji>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(uredjaji -> uredjaji.getSistemPretplatnici() == null ? "" : uredjaji.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(uredjaji -> uredjaji.getObjekti() == null ? "" : uredjaji.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(uredjaji -> uredjaji.getSistemUredjajiModeli() == null ? "" : uredjaji.getSistemUredjajiModeli().getSistemUredjajiProizvodjac() == null ? "" : 
			uredjaji.getSistemUredjajiModeli().getSistemUredjajiProizvodjac().getNaziv()).setCaption("произвођач");
		tabela.addColumn(uredjaji -> uredjaji.getSistemUredjajiModeli() == null ? "": uredjaji.getSistemUredjajiModeli().getNaziv()).setCaption("модел");
		tabela.addColumn(Uredjaji::getKod).setCaption("код");
		tabela.addColumn(Uredjaji::getSerijskiBr).setCaption("серијски број");
		tabela.addColumn(uredjaji -> uredjaji.getSim() == null ? "" : uredjaji.getSim().getBroj()).setCaption("сим број");
		tabela.addColumn(uredjaji -> uredjaji.getSim() == null ? "" : uredjaji.getSim().getIccid()).setCaption("сим iccid");
		tabela.addComponentColumn(uredjaji -> {CheckBox chb = new CheckBox(); if(uredjaji.isAktivno()) {chb.setValue(true); }return chb;}).setCaption("активан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(uredjaji -> uredjaji.getOrganizacija() == null ? "" : uredjaji.getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addComponentColumn(uredjaji -> {CheckBox chb = new CheckBox(); if(uredjaji.isIzbrisan()) {chb.setValue(true); }return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Uredjaji::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Uredjaji::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {

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
		pocetno = new ArrayList<Uredjaji>();
		lista = Servis.uredjajServis.nadjiSveUredjaje(korisnik);
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
