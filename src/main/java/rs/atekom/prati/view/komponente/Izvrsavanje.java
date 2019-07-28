package rs.atekom.prati.view.komponente;

import java.util.ArrayList;
import java.util.Date;
import com.google.maps.model.LatLng;
import com.ibm.icu.text.SimpleDateFormat;
import com.google.maps.model.GeocodingResult;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Javljanja;
import rs.atekom.prati.Broadcaster;
import rs.atekom.prati.server.Address;
import rs.atekom.prati.server.Servis;

public class Izvrsavanje {

	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private SimpleDateFormat datumVreme;
	
	public Izvrsavanje() {
		datumVreme = new SimpleDateFormat(DATUMVREME);
	}
	
	
	public void obradaAlarma(Javljanja javljanje, ArrayList<AlarmiKorisnik> alarmiKorisnici) {
		alarmAdresa(javljanje);
		Servis.javljanjeServis.unesiJavljanja(javljanje);
        Broadcaster.broadcast(javljanje);
        for(AlarmiKorisnik alarmKorisnik : alarmiKorisnici) {
        	if(alarmKorisnik.getSistemAlarmi().getId().equals(javljanje.getSistemAlarmi().getId()) && alarmKorisnik.isEmail()) {
        		String zaglavlje = "Праћење возила - " + javljanje.getObjekti().getOznaka() + " - " + javljanje.getSistemAlarmi().getNaziv();
        		String poruka = String.join("\n"
        		         , "Поштовани,"
        		         , "Објекат " + javljanje.getObjekti().getOznaka() + " је активирао аларм " + javljanje.getSistemAlarmi().getNaziv()
        		         , "у " + datumVreme.format(javljanje.getDatumVreme()) + " " + javljanje.getEventData()
        		         , " "
        		         , "Порука је аутоматски генерисана, немојте одговарати."
        		         , "Атеком доо, www.atekom.rs info@atekom.rs"
        		);
        		Servis.posta.posaljiMail("prati@atekom.rs", alarmKorisnik.getKorisnik().getEmail(), zaglavlje, poruka);
        	}
        }
	}
	
	private void alarmAdresa(Javljanja javljanje) {
    	String adresa = "";
    	LatLng pozicija;
    	String apiPretplatnik = javljanje.getObjekti().getSistemPretplatnici() == null ? "": javljanje.getObjekti().getSistemPretplatnici().getApiKey();
		if(!javljanje.getSistemAlarmi().getSifra().equals("0") && (javljanje.getLat() != 0.00 && javljanje.getLon() != 0.00)){
			if(javljanje.getSistemAlarmi().isAdresa()){
				try {
					if(!apiPretplatnik.isEmpty() && !apiPretplatnik.equals("") && apiPretplatnik != null) {
						GeoApiContext gContext = new GeoApiContext().setApiKey(apiPretplatnik);
						pozicija = new LatLng(javljanje.getLat(), javljanje.getLon());
						GeocodingResult[] adresaTrazena= GeocodingApi.reverseGeocode(gContext, pozicija).await();
						adresa = adresaTrazena[0].formattedAddress;
					}else {
						Address adressa = Servis.nominatim.getAdress(javljanje.getLat(), javljanje.getLon());
						if(adressa != null){
							if(!adressa.getHouseNumber().equals(""))
								adresa = adressa.getHouseNumber() + ", ";
							if(!adressa.getRoad().equals(""))
								adresa = adresa + adressa.getRoad() + ", ";
							if(!adressa.getSuburb().equals(""))
								adresa = adresa + adressa.getSuburb() + ", ";
							if(!adressa.getCity().equals(""))
								adresa = adresa + adressa.getCity() + " ";
							if(!adressa.getPostcode().equals(""))
								adresa = adresa + adressa.getPostcode();
							if(!adressa.getCounty().equals(""))
								adresa = adresa + ", " + adressa.getCounty() + ", ";
							if(!adressa.getCountry().equals(""))
								adresa = adresa + adressa.getCountry();
							}else{
								pozicija = new LatLng(javljanje.getLat(), javljanje.getLon());
								GeocodingResult[] adresaTrazena= GeocodingApi.reverseGeocode(Servis.gContext, pozicija).await();
								adresa = adresaTrazena[0].formattedAddress;
								}
						if(javljanje.getEventData().equals("0")){
							javljanje.setEventData(adresa);
							}else{
								javljanje.setEventData(javljanje.getEventData() + " " + adresa);
								if(javljanje.getEventData().length() > 250){
									javljanje.setEventData(adresa);
									} 
								}
						}
					} catch (Exception e) {
						System.out.println("Problem sa adresama... ");	
						}
				}
			}
	}
	
    public long razlika(Date vreme){
    	return System.currentTimeMillis() - vreme.getTime();
    }
}
