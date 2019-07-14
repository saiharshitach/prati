package rs.atekom.prati.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.Prati;
import rs.atekom.prati.mape.Gmap;
import rs.atekom.prati.server.Servis;

@NavigatorViewName("pracenje") // an empty view name will also be the default view
@MenuCaption("Праћење")
@MenuIcon(VaadinIcons.MAP_MARKER)
public class PracenjeView extends OpstiPanelView{
	
	private static final long serialVersionUID = 1L;

	public PracenjeView() {
		if(Prati.getCurrent().sirina()) {
			dodajZaRacunare();
		}else {
			dodajZaMobilni();
		}
	}

	public void dodajZaRacunare() {
		root.addComponent(buildPanelToolBar());
		topLayout.removeComponent(filter);
		if(Prati.getCurrent().pretplatnik == null && Prati.getCurrent().organizacija == null && Prati.getCurrent().grupa == null) {
			pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
			organizacijeCombo.setValue(korisnik.getOrganizacija());
		}else {
			pretplatniciCombo.setValue(Prati.getCurrent().pretplatnik);
			organizacijeCombo.setValue(Prati.getCurrent().organizacija);
			grupeCombo.setValue(Prati.getCurrent().grupa);
		}
		
		centriraj.setValue(Prati.getCurrent().centriranje);
		centriraj.addValueChangeListener(new ValueChangeListener<Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				Prati.getCurrent().centriranje = event.getValue();
			}
		});
		
		sortiraj.setValue(Prati.getCurrent().sortiranje);
		sortiraj.addValueChangeListener(new ValueChangeListener<Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				Prati.getCurrent().sortiranje = event.getValue();
			}
		});
		
		//updateTable();
		
		//selektovanje onog što je bilo odabrano
		List<JavljanjaPoslednja> izTabele = Prati.getCurrent().poslednjaJavljanja.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
		for(JavljanjaPoslednja javljanje : izTabele) {
			for(Objekti objekat : Prati.getCurrent().objekti) {
				if(objekat.getId().equals(javljanje.getObjekti().getId())){
					Prati.getCurrent().poslednjaJavljanja.getSelectionModel().select(javljanje);
					break;
				}
			}
		}
		
		Prati.getCurrent().poslednjaJavljanja.addItemClickListener(listener -> {
			if(listener.getMouseEventDetails().isDoubleClick()) {
				JavljanjaPoslednja klik = listener.getItem();
				if(klik != null && sadrziObjekat(Prati.getCurrent().objekti, klik.getObjekti().getId())) {
					centriraj.setValue(false);
					mapa.setCenter(new LatLon(klik.getLat(), klik.getLon()));
					mapa.setZoom(14);
					Prati.getCurrent().pokaziObavestenje("центрирање мапе је искључено");
				}else {
					pokaziPorukuGreska("морате изабрати објекат који је већ приказан на мапи");
				}
			}
		});
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				if(event.getValue() != null) {
					Prati.getCurrent().poslednjaJavljanja.setItems(Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(event.getValue())));
				}else {
					//updateTable();
				}
				Prati.getCurrent().objekti.clear();
				mapa.ukloniMarkere();
			}
		});
		
		lociraj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				mapa.ukloniMarkere();
				Prati.getCurrent().objekti.clear();
				
				for(JavljanjaPoslednja javljanje : Prati.getCurrent().poslednjaJavljanja.getSelectedItems()) {
					Prati.getCurrent().objekti.add(javljanje.getObjekti());
				}
				mapa.dodavanjeMarkera();
				Prati.getCurrent().pretplatnik = pretplatniciCombo.getValue();
				Prati.getCurrent().organizacija = organizacijeCombo.getValue();
				Prati.getCurrent().grupa = grupeCombo.getValue();
			}
		});
		
		String slot = "dupli-panel-slot";
		mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		mapa.centriraj();
		mapa.dodavanjeMarkera();
		mapa.osvezavanja();
		Component content = buildContent(createContentWraper(mapa, slot, true), createContentWraper(buildTable(true), slot, true));
		
		root.addComponent(content);
		root.setExpandRatio(content, 1);
		Prati.getCurrent().pracenjeView = this;
		Prati.getCurrent().alarmiKorisnika = Servis.alarmKorisnikServis.vratiAlarmePoKorisniku(korisnik, true, false, true);
		setContent(root);
	}
	
	
	public void dodajZaMobilni() {
		root.addComponent(buildPanelToolBar());
		topLayout.removeComponent(filter);
		if(Prati.getCurrent().pretplatnik == null && Prati.getCurrent().organizacija == null && Prati.getCurrent().grupa == null) {
			pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
			organizacijeCombo.setValue(korisnik.getOrganizacija());
		}else {
			pretplatniciCombo.setValue(Prati.getCurrent().pretplatnik);
			organizacijeCombo.setValue(Prati.getCurrent().organizacija);
			grupeCombo.setValue(Prati.getCurrent().grupa);
		}
		
		centriraj.setValue(Prati.getCurrent().centriranje);
		centriraj.addValueChangeListener(new ValueChangeListener<Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				Prati.getCurrent().centriranje = event.getValue();
			}
		});
		
		sortiraj.setValue(Prati.getCurrent().sortiranje);
		sortiraj.addValueChangeListener(new ValueChangeListener<Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Boolean> event) {
				Prati.getCurrent().sortiranje = event.getValue();
			}
		});
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				if(event.getValue() != null) {
					Prati.getCurrent().poslednjaJavljanja.setItems(Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(event.getValue())));
				}else {
					//updateTable();
				}
				Prati.getCurrent().objekti.clear();
			}
		});
		
		lociraj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				root.removeAllComponents();
				mapa = new Gmap(Servis.apiGoogle, null, "serbian");
				mapa.centriraj();
				mapa.osvezavanja();
				Prati.getCurrent().objekti.clear();
				for(JavljanjaPoslednja javljanje : Prati.getCurrent().poslednjaJavljanja.getSelectedItems()) {
					Prati.getCurrent().objekti.add(javljanje.getObjekti());
				}
				mapa.dodavanjeMarkera();
				Prati.getCurrent().pretplatnik = pretplatniciCombo.getValue();
				Prati.getCurrent().organizacija = organizacijeCombo.getValue();
				Prati.getCurrent().grupa = grupeCombo.getValue();
				root.addComponentsAndExpand(mapa);
			}
		});
		
		root.addComponent(Prati.getCurrent().poslednjaJavljanja);
		root.setExpandRatio(Prati.getCurrent().poslednjaJavljanja, 1);
		setContent(root);
	}
	
	public VerticalLayout buildTable(boolean obe) {
		VerticalLayout tabele = new VerticalLayout();
		tabele.setSizeFull();
		tabele.setMargin(false);
		tabele.setSpacing(false);
		if(obe) {
			tabele.addComponent(Prati.getCurrent().poslednjaJavljanja);
			tabele.addComponent(Prati.getCurrent().javljanjaAlarmi);
		}else {
			tabele.addComponent(Prati.getCurrent().poslednjaJavljanja);
		}
		return tabele;
	}

	public void updateTable() {
		Prati.getCurrent().poslednjaJavljanja.setItems(new ArrayList<JavljanjaPoslednja>());
		if(!korisnik.isAdmin()) {
			ArrayList<Grupe> grupe = Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik);
			ArrayList<Objekti> objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
			if(objekti != null && !objekti.isEmpty()) {
				Prati.getCurrent().poslednjaJavljanja.setItems(Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(objekti));
			}
		}else {
			if(korisnik.getSistemPretplatnici() != null) {
				ArrayList<Objekti> objekti = Servis.objekatServis.vratiSveObjekte(korisnik, true);
				if(objekti != null && !objekti.isEmpty()) {
					Prati.getCurrent().poslednjaJavljanja.setItems(Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(objekti));
				}
			}
		}
	}
	
	public void azurirajMarker(Javljanja javljanje) {
		Collection<GoogleMapMarker> markeri = mapa.getMarkers();
		for(GoogleMapMarker marker : markeri) {
			if(marker.getId() == javljanje.getObjekti().getId().longValue()) {
				mapa.removeMarker(marker);
				GoogleMapMarker mm = new GoogleMapMarker(mapa.podesiCaption(javljanje),new LatLon(javljanje.getLat(), javljanje.getLon()), false, mapa.ikonica.icon(javljanje));
				mm.setId(javljanje.getObjekti().getId().longValue());
				
				mapa.lat.remove(marker.getPosition().getLat());
				mapa.lon.remove(marker.getPosition().getLon());
				mapa.lat.add(javljanje.getLat());
				mapa.lon.add(javljanje.getLon());
				
				mapa.markAsDirty();
				mapa.addMarker(mm);
				mapa.centriraj();
				break;
				}
			}
		}
	
	private boolean sadrziObjekat(ArrayList<Objekti> list, Long id) {
		return list.stream().filter(j -> j.getId().equals(id)).findFirst().isPresent();
	}
}
