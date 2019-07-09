package rs.atekom.prati.view.zone;

import org.vaadin.dialogs.ConfirmDialog;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.vaadin.server.Page;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.CheckBox;
import pratiBaza.tabele.Zone;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import rs.atekom.prati.mape.Gmap;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.DecimalniPozicija;
import rs.atekom.prati.view.komponente.Tekst;


public class ZoneForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private ZoneLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private Tekst adresa, naziv, opis;
	private DecimalniPozicija lon, lat;
	private Celobrojni poluprecnik;
	private CheckBox aktivan, izbrisan;
	private Button nadjiLokaciju;
	private int brojPokusaja;

	public ZoneForma(ZoneLogika log) {
		logika = log;
		brojPokusaja = 0;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		adresa = new Tekst("адреса", false);
		nadjiLokaciju = new Button("нађи локацију");
		nadjiLokaciju.addStyleName("primary");
		naziv = new Tekst("назив", true);
		lon = new DecimalniPozicija("гео. дужина", true);
		lat = new DecimalniPozicija("гео. ширина", true);
		poluprecnik = new Celobrojni("полупречник у метрима", true);
		opis = new Tekst("опис", false);
		aktivan = new CheckBox("активан");
		izbrisan = new CheckBox("избрисан");
		
		nadjiLokaciju.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				nadjiLokaciju(adresa.getValue());
			}
		});
		
		sacuvaj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(proveraPodataka()) {
					ConfirmDialog.show(logika.view.getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						@Override
						public void onClose(ConfirmDialog dialog) {
							if(dialog.isConfirmed()) {
								logika.sacuvajPodatak(sacuvajPodatak(logika.view.dajIzabraniRed()));
								logika.view.mapa.clearMarkers();
								brojPokusaja = 0;
							}
						}
					});
				}else {
					logika.view.pokaziPorukuGreska(OpstiView.OBAVEZNAPOLJA);
				}
			}
		});
		
		otkazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				logika.otkaziPodatak();
				logika.view.mapa.clearMarkers();
			}
		});
		
		izbrisi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(logika.view.getUI(), "Провера", "Избриши изабране податке?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							logika.ukloniPodatak();
							logika.view.mapa.clearMarkers();
						}
					}
				});
			}
		});
		
		if(logika.view.isAdmin()) {
			layout.addComponent(pretplatnici);
		}
		layout.addComponent(adresa);
		layout.addComponent(nadjiLokaciju);
		layout.addComponent(naziv);
		layout.addComponent(lon);
		layout.addComponent(lat);
		layout.addComponent(poluprecnik);
		layout.addComponent(opis);
		layout.addComponent(aktivan);
		if(logika.view.korisnik.isAdmin() && logika.view.korisnik.getOrganizacija() == null) {
			layout.addComponent(organizacije);
		}
		if(logika.view.isAdmin())  {
			layout.addComponent(izbrisan);
		}
		
		Gmap mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		expander.addComponent(mapa);
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
		
		addComponent(layout);
	}
	
	@Override
	public void izmeniPodatak(Object podatak) {
		ocistiPodatak();
		Zone zona;
		if(podatak == null) {
			zona = new Zone();
		}else {
			zona = (Zone)podatak;
			postaviPodatak(zona);
		}
		String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
		Page.getCurrent().getJavaScript().execute(scrollScript);
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		Zone zona;
		if(podatak == null) {
			zona = new Zone();
		}else {
			zona = (Zone)podatak;
		}
		zona.setSistemPretplatnici(pretplatnici.getValue());
		zona.setOrganizacija(organizacije.getValue());
		zona.setNaziv(naziv.getValue());
		zona.setLon(Double.parseDouble(lon.getValue()));
		zona.setLat(Double.parseDouble(lat.getValue()));
		zona.setPrecnik(Integer.parseInt(poluprecnik.getValue()));
		zona.setOpis(opis.getValue());
		zona.setAktivan(aktivan.getValue());
		zona.setIzbrisan(izbrisan.getValue());
		return zona;
	}

	@Override
	public void ocistiPodatak() {
		if(logika.view.korisnik.getSistemPretplatnici() != null) {
			pretplatnici.setValue(logika.view.korisnik.getSistemPretplatnici());
		}else {
			pretplatnici.clear();
		}
		naziv.clear();
		lon.setValue(String.valueOf(0.0));
		lat.setValue(String.valueOf(0.0));
		poluprecnik.setValue(String.valueOf(500));
		opis.clear();
		aktivan.setValue(true);
		if(logika.view.korisnik.getOrganizacija() != null) {
			organizacije.setValue(logika.view.korisnik.getOrganizacija());
		}else {
			organizacije.clear();
		}
		izbrisan.setValue(false);
		logika.view.mapa.setCenter(new LatLon(44.751802, 19.703187));
		logika.view.mapa.setZoom(8);
	}

	@Override
	public void postaviPodatak(Object podatak) {
		Zone zona = (Zone)podatak;
		if(zona.getId() != null) {
			pretplatnici.setValue(zona.getSistemPretplatnici());
			try {
				naziv.setValue(zona.getNaziv());
				}catch (Exception e) {
					naziv.setValue("");
					}
			try {
				lon.setValue(String.valueOf(zona.getLon()));
				}catch (Exception e) {
					lon.setValue(String.valueOf(0.0));
					}
			try {
				lat.setValue(String.valueOf(zona.getLat()));
				}catch (Exception e) {
					lat.setValue(String.valueOf(0.0));
					}
			try {
				poluprecnik.setValue(String.valueOf(zona.getPrecnik()));
				}catch (Exception e) {
					poluprecnik.setValue(String.valueOf(0));
					}
			try {
				opis.setValue(zona.getOpis());
				}catch (Exception e) {
					opis.setValue("");
					}
			aktivan.setValue(zona.isAktivan());
			organizacije.setValue(zona.getOrganizacija());
			izbrisan.setValue(zona.isIzbrisan());
			try {
				logika.view.mapa.clearMarkers();
				if(zona.getLat() != 0.0 && zona.getLon() != 0.0) {
					logika.view.mapa.setCenter(new LatLon(zona.getLat(), zona.getLon()));
					logika.view.mapa.setZoom(12);
					//GoogleMapCircle circle = new GoogleMapCircle(new LatLon(60.448118, 22.253738), 2000);
					logika.view.mapa.addMarker(new GoogleMapMarker(zona.getNaziv() + " " + 
					String.valueOf(zona.getPrecnik()), new LatLon(zona.getLat(), zona.getLon()), false));
					}else {
						logika.view.mapa.clearMarkers();
						logika.view.mapa.setCenter(new LatLon(44.751802, 19.703187));
						logika.view.mapa.setZoom(8);
					}
				}catch (Exception e) {
					logika.view.mapa.setCenter(new LatLon(44.751802, 19.703187));
					logika.view.mapa.setZoom(8);
					}
			}
		}

	@Override
	public boolean proveraPodataka() {
		boolean sveIma = true;
		if(pretplatnici.getValue() == null) {
			sveIma = false;
		}
		if(naziv.isEmpty() || naziv.getValue() == "") {
			sveIma = false;
		}
		if(lon.isEmpty() || lon.getValue() == "" || Double.parseDouble(lon.getValue()) == 0.0) {
			sveIma = false;
		}
		if(lat.isEmpty() || lat.getValue() == "" || Double.parseDouble(lat.getValue()) == 0.0) {
			sveIma = false;
		}
		if(poluprecnik.isEmpty() || poluprecnik.getValue() == "" || Double.parseDouble(poluprecnik.getValue()) < 10) {
			sveIma = false;
		}
		return sveIma;
	}
	
	public void postaviLokaciju(LatLon position) {
		lon.setValue(String.valueOf(position.getLon()));
		lat.setValue(String.valueOf(position.getLat()));
	}
	
	private void nadjiLokaciju(String adresa) {
		if(brojPokusaja < 6) {
			GeoApiContext geoApiContext = new GeoApiContext().setApiKey(Servis.apiGoogle);
		    if (adresa != null && geoApiContext != null) {
		        GeocodingResult[] results;
				try {
					results = GeocodingApi.geocode(geoApiContext, adresa).await();
			        GeocodingResult result = results[0];
			        double latitude = result.geometry.location.lat;
			        double longitude = result.geometry.location.lng;
			        lat.setValue(String.valueOf(latitude));
			        lon.setValue(String.valueOf(longitude));
			        logika.view.mapa.clearMarkers();
					logika.view.mapa.setCenter(new LatLon(latitude, longitude));
					logika.view.mapa.setZoom(12);
					//GoogleMapCircle circle = new GoogleMapCircle(new LatLon(60.448118, 22.253738), 2000);
					logika.view.mapa.addMarker(new GoogleMapMarker(" " , new LatLon(latitude, longitude), false));
				} catch (Exception e) {
					logika.view.pokaziPorukuGreska("неисправни подаци - или адреса или приступ мапи!");
					e.printStackTrace();
				}
				brojPokusaja++;
				}else {
		        	logika.view.pokaziPorukuGreska("неисправни подаци - или адреса или приступ мапи!");
		        	}
		}else {
			logika.view.pokaziPorukuGreska("искористили сте максимални број покушаја тражења локације! за наставак изађите из прозора");
		}

	}
	
	/*public void postaviZonu(LatLon pozicija, int precnik) {
		float radius=3; 
		double d2r= Math.PI/180;
		double r2d=180/Math.PI;
		double clat= (radius/3963.189)*r2d; // Convert statute miles into degrees latitude
		double clng=clat/Math.cos(pozicija.getLon()*d2r);
		Point2D.Double[] cornerss = new Point2D.Double[330]; // more points,more like circle
		for (int i=0;i< cornerss.length;i++) {
		double theta = Math.PI * ((double)i / 16);
		double cy = pozicija.getX()+(clng * Math.sin(theta));
		double cx = myPosMarker.getLatLng().getY()+(clat * Math.cos(theta));
		Point2D.Double point = new Point2D.Double(cy, cx);
		cornerss = point; }
		Polygon poly = new Polygon(12L, cornerss, "#FF0000", 1, 0.5, "#000000", 0.1, true);

		logika.view.mapa.addPolyOverlay(poly);
	}**/

}
