package rs.atekom.prati.view.istorija;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;
import rs.atekom.prati.mape.Gmap;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

@NavigatorViewName("istorija") // an empty view name will also be the default view
@MenuCaption("Историја")
@MenuIcon(VaadinIcons.LINES)
public class IstorijaView extends OpstiPanelView{

	/*@WebServlet("/izvestaj-istorija")
    public static class ReportsImageServlet extends ImageServlet {
		private static final long serialVersionUID = 1L;
	}**/
	private static final long serialVersionUID = 1L;
	private Panel podaci;
	private GoogleMapPolyline linija;
	private HorizontalLayout preuzimanje;
	
	public IstorijaView() {
		root.addComponent(buildObjektiDatumVremeToolBar());
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		
		String slot = "dupli-panel-slot";
		mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		mapa.centriraj();
		Component content = buildContent(createContentWraper(mapa, slot, true), createContentWraper(podaci, slot, true));
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				objektiCombo.clear();
				if(event.getValue() != null) {
					objektiCombo.setItems(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(event.getValue()));
					ukloniPreuzimanje();
				}else {
					objektiCombo.setItems(Servis.objekatServis.vratiSveObjekte(korisnik, true));
				}
			}
		});
		
		objektiCombo.addValueChangeListener(new ValueChangeListener<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Objekti> event) {
				ukloniPreuzimanje();
			}
		});
		
		alarmiCombo.addValueChangeListener(new ValueChangeListener<SistemAlarmi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemAlarmi> event) {
				ukloniPreuzimanje();
			}
		});
		
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 < 8*86400)) {
					prikaziIstoriju(objektiCombo.getValue(), Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()), alarmiCombo.getValue());
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 7 данa!");
				}
			}
		});
		
		root.addComponent(content);
		root.setExpandRatio(content, 1);
		
		setContent(root);
	}
	
	private void prikaziIstoriju(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo, SistemAlarmi alarm) {	
		mapa.removeAllComponents();
		mapa.clearMarkers();
		podaci.setContent(null);
		
		ukloniPreuzimanje();
		
		if(linija != null) {
			mapa.removePolyline(linija);
		}
		
		if(objekat != null) {
			if(!datumVremeOd.after(datumVremeDo) && !datumVremeOd.equals(datumVremeDo)) {
				if((datumVremeDo.getTime() - datumVremeOd.getTime())/1000 > 86400 && prikaziMarkere.getValue() && alarm == null) {
					pokaziPorukuGreska("За приказ свих маркера на мапи време не може бити дуже од 24 часа!");
				}else {
					ArrayList<LatLon> tacke = new ArrayList<LatLon>();
					ArrayList<Double> lat = new ArrayList<Double>();
					ArrayList<Double> lon = new ArrayList<Double>();
					
					ArrayList<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDo(objekat, datumVremeOd, datumVremeDo);
					if(javljanja != null && !javljanja.isEmpty()) {
						if(prikaziMarkere.getValue()) {
							for(Javljanja javljanje : javljanja) {
								tacke.add(new LatLon(javljanje.getLat(), javljanje.getLon()));
								lat.add(javljanje.getLat());
								lon.add(javljanje.getLon());
								if(alarm == null) {
									GoogleMapMarker marker = new GoogleMapMarker(mapa.podesiCaption(javljanje),  new LatLon(javljanje.getLat(), javljanje.getLon()), false);
									marker.setAnimationEnabled(false);
									marker.setIconUrl(mapa.ikonica.icon(javljanje));
									mapa.addMarker(marker);
								}else {
									if(javljanje.getSistemAlarmi().getId().equals(alarm.getId())) {
										GoogleMapMarker marker = new GoogleMapMarker(mapa.podesiCaption(javljanje),  new LatLon(javljanje.getLat(), javljanje.getLon()), false);
										marker.setAnimationEnabled(false);
										marker.setIconUrl(mapa.ikonica.icon(javljanje));
										mapa.addMarker(marker);
									}
								}
								
							}
						}else {
							for(Javljanja javljanje : javljanja) {
								tacke.add(new LatLon(javljanje.getLat(), javljanje.getLon()));
								lat.add(javljanje.getLat());
								lon.add(javljanje.getLon());
							}
						}

						
						linija = new GoogleMapPolyline(tacke, "#0066ff", 0.6, 6);
						mapa.addPolyline(linija);
						
						mapa.fitToBounds(new LatLon(Collections.max(lat), Collections.max(lon)), new LatLon(Collections.min(lat), Collections.min(lon)));
						IstorijaLayout istorija = new IstorijaLayout(objekat, datumVremeOd, datumVremeDo);
						
						preuzimanje = istorija.vratiPreuzimanje();
						if(preuzimanje != null) {
							topLayout.addComponent(preuzimanje);
						}
						
						podaci.setContent(istorija);
					}else {
						pokaziPorukuGreska("За одабрани објекат и период нема података!");
					}
					
				}
			}else {
					pokaziPorukuGreska("Почетно време мора бити старије од крајњег!");
					}
			}else {
				pokaziPorukuGreska("Морате изабрати објекат!");
		}
	}
	
	private void ukloniPreuzimanje() {
		if(preuzimanje != null) {
			topLayout.removeComponent(preuzimanje);
			preuzimanje = null;
		}
	}

}
