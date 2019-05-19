package rs.cybertrade.prati.mape;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.ObjektiDetalji;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.server.Servis;

public class StaraGMapa extends VerticalLayout implements Serializable{

	private static final long serialVersionUID = 1L;
	private Korisnici korisnik;
	private GoogleMap googleMap;
	private static GoogleMapInfoWindow window;
	private GeoApiContext gContext;
	private LatLon pozicijaM;
	private LatLng pozicija;
	private String podaciMarker;
	private ArrayList<Double> lat = new ArrayList<Double>();
    private ArrayList<Double> lon = new ArrayList<Double>();
    private ArrayList<JavljanjaPoslednja> javljanjaLista;
    private ArrayList<Objekti> objekti = new ArrayList<Objekti>();
    private Objekti objekat;
    private JavljanjaPoslednja javljanjePoslednje = new JavljanjaPoslednja();
    private String apiGoogle;
    private String oznaka = "";
    private String datum_vreme;
    private String[] datum;
    private String brzina;
    private String caption, naslov;
    private ObjektiDetalji objekatDetalji = null;
    private Obd obd = null;
    private GMarker marker;
    private GMarker kliknutiMarker;
    private String[] datumMarker;
    private Ikonica ikonica = new Ikonica();
    
	public StaraGMapa() {
		korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
		if(apiGoogle == null || apiGoogle.isEmpty()){
			apiGoogle = Servis.apiGoogle; //"AIzaSyDD6DzSebIUn6n2zEUm6f00tWnytTqfjy0";
		}
		setSizeFull();
		setMargin(new MarginInfo(false, false, false, false));
		naslov = "ПРЕГЛЕД КРЕТАЊА";
		googleMap = new GoogleMap(apiGoogle, null, "serbian");
		googleMap.setSizeFull();
		googleMap.addMarkerClickListener(markerInfo);
		googleMap.setCenter(new LatLon(44.751802, 19.703187));
        googleMap.setZoom(8);
		osvezavanja();
		addComponent(googleMap);
	}
	
	public GoogleMap vratiMapu(){
		return googleMap;
	}
	
	public String getCaption(){
		return naslov;
	}
	
    public void dodavanjeMarkera(){
		googleMap.clearMarkers();
		lat.clear();
		lon.clear();
		objekti = new ArrayList<Objekti>(Prati.getCurrent().izabraniId.values());
		if(!objekti.isEmpty()){
			javljanjaLista = Servis.javljanjePoslednjeServis.vratiListuJavljanjaPoslednjih(objekti);
			for(JavljanjaPoslednja javljanje: javljanjaLista){
				javljanjePoslednje = javljanje;
			    if(javljanjePoslednje != null){
			    	objekat = javljanje.getObjekti();
					objekatDetalji = Servis.objekatDetaljiServis.nadjiObjekatDetaljePoObjektu(objekat);
					obd = Servis.obdServis.nadjiObdPoslednji(objekat);
					oznaka = objekat.getOznaka();
					datum_vreme = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(javljanjePoslednje.getDatumVreme());
					datum = datum_vreme.split(" ");
					brzina = "брзина: " + javljanjePoslednje.getBrzina() + "км/ч";
					caption = String.join("\n", oznaka + " ", brzina/*datum_vremejavljanjePoslednje.getDatum_vreme()**/ + " ", datum[1] + " ", datum[0]);//
					marker = new GMarker(caption, new LatLon(javljanjePoslednje.getLat(), javljanjePoslednje.getLon()), false, ikonica.icon(javljanjePoslednje));
					if(objekat.getTip()){
						if(obd!=null){
							marker.setUkupnokm(obd.getUkupnoKm());
							marker.setUkupnoGorivo(obd.getUkupnoGorivo());
							}
						}
					marker.setOznaka(oznaka);
					if(objekatDetalji != null){
						marker.setRegistracija(objekatDetalji.getRegistracija());
						marker.setModel(objekatDetalji.getModel());
						}
					marker.setDatum_vreme(datum_vreme);
					marker.setBrzina(String.valueOf(javljanjePoslednje.getBrzina()));
					lat.add(javljanjePoslednje.getLat());
					lon.add(javljanjePoslednje.getLon());
					marker.setAnimationEnabled(false);
					googleMap.addMarker(marker);
					}
			    }
			}
	}
    
    
    public ArrayList<Double> getLat(){
    	return lat;
    }
    
    public ArrayList<Double> getLon(){
    	return lon;
    }
    
    public void osvezavanja(){
    	dodavanjeMarkera();
    	centriraj();
		Prati.getCurrent().setPollInterval(10000);
		Prati.getCurrent().osvezavanjeMarkera = new PollListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void poll(PollEvent event) {
				dodavanjeMarkera();
				if(!(window == null)){
					if(googleMap.isInfoWindowOpen(window))
						googleMap.closeInfoWindow(window);
					}
				if(Prati.getCurrent().centriranje)
					centriraj();
			}
		};
		Prati.getCurrent().addPollListener(Prati.getCurrent().osvezavanjeMarkera);
    }
    
    public void centriraj(){
    	if(!lat.isEmpty() && !lon.isEmpty()){
    		if(lat.size() == 1){
    			googleMap.setCenter(new LatLon((Double)lat.get(0), (Double)lon.get(0)));
    		}else if(lat.size() > 1){
    			googleMap.fitToBounds(new LatLon(Collections.max(lat), Collections.max(lon)), new LatLon(Collections.min(lat),Collections.min(lon)));
    		}else{
    			dodajPraznuMapu();
    		}
    	}else{
    		dodajPraznuMapu();
    	}
    }
    
    /*prazna mapa**/
    public void dodajPraznuMapu(){
    	removeComponent(googleMap);
    	googleMap.setCenter(new LatLon(44.751802, 19.703187));
        googleMap.setZoom(8);
        addComponent(googleMap);
    }
    
    public MarkerClickListener markerInfo = new MarkerClickListener() {
    	private static final long serialVersionUID = 1L;
		@Override
		public void markerClicked(GoogleMapMarker clickedMarker) {
			prikaziMarkerPodatke(clickedMarker);
		}
	};
	
	public void prikaziMarkerPodatke(GoogleMapMarker clickedMarker){
		try{
			kliknutiMarker = (GMarker) clickedMarker;
		    if(window != null){
		      if(googleMap.isInfoWindowOpen(window))
		    	  googleMap.closeInfoWindow(window);
		      }
		    VerticalLayout infoSadrzaj = new VerticalLayout();
		    //String prviRed = kliknutiMarker.getOznaka();
		    infoSadrzaj.addComponent(new Label(kliknutiMarker.getOznaka()));
		    if(kliknutiMarker.getRegistracija() != null ){
		    	infoSadrzaj.addComponent(new Label(kliknutiMarker.getRegistracija()));
		    }
		    if(kliknutiMarker.getModel() != null){
		    	infoSadrzaj.addComponent(new Label(kliknutiMarker.getModel()));
		    }
		    datumMarker = kliknutiMarker.getDatum_vreme().split(" ");
		    infoSadrzaj.addComponent(new Label("брзина: " + kliknutiMarker.getBrzina() + "км/ч"));
		    if(kliknutiMarker.getUkupnokm() != null && kliknutiMarker.getUkupnokm() != 0){
		    	infoSadrzaj.addComponent(new Label("км: " + kliknutiMarker.getUkupnokm().toString()));
		    }
		    if(kliknutiMarker.getUkupnoGorivo() != null && kliknutiMarker.getUkupnoGorivo() != 0.00){
		    	infoSadrzaj.addComponent(new Label("лит: " + kliknutiMarker.getUkupnoGorivo().toString()));
		    }
		    infoSadrzaj.addComponent((new Label(datumMarker[1])));
		    infoSadrzaj.addComponent(new Label(datumMarker[0]));

		    infoSadrzaj.addLayoutClickListener(new LayoutClickListener() {
					
					private static final long serialVersionUID = 1L;

					public void layoutClick(LayoutClickEvent event) {
						String adresa = "";
						korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
						apiGoogle = korisnik.getSistemPretplatnici().getApiKey();
						if(apiGoogle==null || apiGoogle.isEmpty()){
							apiGoogle = Servis.apiGoogle; //"AIzaSyDD6DzSebIUn6n2zEUm6f00tWnytTqfjy0";
						}
						gContext = new GeoApiContext().setApiKey(apiGoogle);
						pozicijaM = kliknutiMarker.getPosition();
						pozicija = new LatLng(pozicijaM.getLat(), pozicijaM.getLon());
						podaciMarker = kliknutiMarker.getOznaka();
						try {
							GeocodingResult[] adresaTrazena= GeocodingApi.reverseGeocode(gContext, pozicija).await();
							adresa = adresaTrazena[0].formattedAddress;
							Prati.getCurrent().showNotification(new Notification( podaciMarker + ": " + adresa, Notification.Type.HUMANIZED_MESSAGE));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		
            window = new GoogleMapInfoWindow("", (GoogleMapMarker) kliknutiMarker);
            googleMap.setInfoWindowContents(window, infoSadrzaj);				
             if (clickedMarker.equals(clickedMarker)) {
                 googleMap.openInfoWindow(window);
                 } 
             }catch(Exception e){
	        	
             }
		}
}
