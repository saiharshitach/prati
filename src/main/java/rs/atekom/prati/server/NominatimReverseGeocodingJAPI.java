package rs.atekom.prati.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NominatimReverseGeocodingJAPI {
	private final String NominatimInstance; //"https://nominatim.openstreetmap.org"
	private int zoomLevel = 18;
	
	public NominatimReverseGeocodingJAPI(String url){
		NominatimInstance = url; 
	}
	
	public NominatimReverseGeocodingJAPI(String url, int zoomLevel){
		NominatimInstance = url;
		if(zoomLevel < 0 || zoomLevel > 18){
			System.err.println("invalid zoom level, using default value");
			zoomLevel = 18;
		}
		
		this.zoomLevel = zoomLevel;
	}
	
	public Address getAdress(double lat, double lon){
		Address result = null;		
		String urlString = NominatimInstance + "/reverse?format=json&addressdetails=1&lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&zoom=" + zoomLevel ;
		try {
			result = new Address(getJSON(urlString), zoomLevel);
		} catch (IOException e) {
			System.err.println("Can't connect to server - nominatim getAdress.");
			//e.printStackTrace();
		}		
		return result;
	}
	
	private String getJSON(String urlString) throws IOException{
		@SuppressWarnings("restriction")
		URL url = new URL(null, urlString, new sun.net.www.protocol.https.Handler());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.addRequestProperty("User-Agent", "Mozilla/4.76"); 

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String text;
		StringBuilder result = new StringBuilder();
		while ((text = in.readLine()) != null)
			result.append(text);
		in.close();
		return result.toString();

	}}
