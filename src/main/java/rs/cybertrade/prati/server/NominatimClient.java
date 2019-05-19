package rs.cybertrade.prati.server;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;


public class NominatimClient {

	private static JsonNominatimClient nominatimClient;
	
	public NominatimClient(String email, String baseUrl){
		final SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        final ClientConnectionManager connexionManager = new SingleClientConnManager(null, registry);
        final HttpClient httpClient = new DefaultHttpClient(connexionManager, null);
        nominatimClient = new JsonNominatimClient(baseUrl, httpClient, email);
	}
	
	public String getAddress(Double lat, Double lon){
		String adresa = "";
		try{
			final Address  address = nominatimClient.getAddress(lon, lat);
			//elementi = address.getAddressElements();
			adresa = address.getDisplayName();
		}catch(Exception e){
			System.out.println("Problem sa Nominatim klijentom");
		}
		return adresa;
	}


}
