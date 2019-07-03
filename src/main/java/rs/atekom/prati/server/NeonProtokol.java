package rs.atekom.prati.server;

import java.sql.Timestamp;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;


public class NeonProtokol {

	public NeonProtokol() {
		
	}
	
	public Javljanja neonObrada(String[] da, String ulaz, Objekti objekat, boolean zaustavljeno, int vreme, Javljanja stop) {

		Javljanja javljanje = null;
		String eventData = "0";
		String alarm_id = "0";
		Double longitude = 0.00;
		Double latitude = 0.00;
		Integer brzina = 0;
		Float pravac = 0.00f;
		Float nadmorskaVisina = 0.00f;
		String iButton = "0";
		Integer kontaktBrava = 0;
		Float virtualOdo = 0.00f;
		Boolean kontakt = false;
		Boolean valid = false;
		Timestamp upis;
		Date sada;
		SistemAlarmi alarm;
		LatLng pozicija;
		String adresa = "";
		Timestamp datumVreme = new Timestamp(parseDateTime("010170", "000000").getTime());
		boolean ispravnoVreme;
		try{
			//String[] da = ulaz.split(",");
			//prvo provera da li je validan ako jeste idemo dalje ako nije ne upisujemo ništa
			if (da[5].equals("A")) {
				valid = true;			
			}
			if(valid){
				//id vozila 2
				//vozilo_id = da[2];
				//alarm id 3
				if(da[3] != null && !da[3].isEmpty()){
					if(da[3].equals("1071")){
						alarm_id = "6022";
						}else{
							alarm_id = da[3];
							}
					}
				//validan
				//datum vreme 6 i 7
				try{
					datumVreme = new Timestamp(parseDateTime(da[6], da[7]).getTime());
					ispravnoVreme = true;
					}catch(Exception e){
						System.out.println("Greška vreme");
						ispravnoVreme = false;
					}
				//pozicija 8 i 9, 10 i 11
				longitude = parseDouble(da[8].substring(0, 3)) + parseDouble(da[8].substring(3)) / 60.0;
				if (da[9].equalsIgnoreCase("S")) {
					longitude *= -1;
					}
				latitude = parseDouble(da[10].substring(0, 2)) + parseDouble(da[10].substring(2)) / 60.0;
				if (da[11].equalsIgnoreCase("W")) {
					latitude *= -1;
					}
				//brzina 12
				if(da[12]!=null && !da[12].isEmpty()){
					Float gpsBrzina = Float.parseFloat(da[12]);
					brzina = gpsBrzina.intValue();
					}
				//pravac 13
				if(da[13]!=null && !da[13].isEmpty()){
					pravac = parseFloat(da[13]);
					}
				//Float naponAkumulatora = 0.00f;
				//if(da[15]!=null && !da[15].isEmpty()){
				//	naponAkumulatora = parseFloat(da[15]);
				//}	
				//Integer rpm = 0;
				//if(da[17]!=null && !da[17].isEmpty()){
				//rpm = Integer.valueOf(da[17]);
				//}
				//Float nivoGoriva = 0.00f;
				//if(da[18]!=null && !da[18].isEmpty()){
				//	nivoGoriva = parseFloat(da[18]);
				//}
				//event data 19
				if(da[19]!=null && !da[19].isEmpty()){
					eventData = da[19];
					}
				//nadmorska visina 20
				/*if(da[20] != null && !da[20].isEmpty()){
				 * nadmorskaVisina=parseFloat(da[20]);
				 * }**/
				//iButton 21
				if(da[21] != null && !da[21].isEmpty()){
					iButton = da[21];
					}
				//kontakt brava
				if(da[22] != null && !da[22].isEmpty()){
					kontaktBrava = Integer.valueOf(da[22]);
					}
				//kontakt 
				if(kontaktBrava == 1){
					kontakt = true;
					}else{
						kontakt = false;
					}
				//Integer rezimRada = parseInt(da[23]);
				//String iccid = da[24];
				//String verzijaTelita = da[25];
				//String verzijaFw = da[26];
				//Float temp = 0.00f;
				sada = new Date();
				upis = new Timestamp(sada.getTime());
				alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(alarm_id));
				if(alarm == null || alarm_id.equals("0")){
					alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(0));
					}
				if(!(stop == null) && !zaustavljeno && vreme != 0){
					if((datumVreme.getTime() - stop.getDatumVreme().getTime())/1000 > vreme*60){
						alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri("1095");
					}
				}
				
				if(objekat != null){
					if(!alarm.getSifra().equals("0") && (latitude!=0.00 && longitude!=0.00)){
						if(alarm.isAdresa()){
							try {
								Address adressa = Servis.nominatim.getAdress(latitude, longitude);
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
										pozicija = new LatLng(latitude, longitude);
										GeocodingResult[] adresaTrazena= GeocodingApi.reverseGeocode(Servis.gContext, pozicija).await();
										adresa = adresaTrazena[0].formattedAddress;
										}
								if(eventData.equals("0")){
									eventData = adresa;
									}else{
										eventData = eventData + " " + adresa;
										if(eventData.length() > 250){
											eventData = adresa;
											} 
										}
								} catch (Exception e) {
									System.out.println("Problem sa adresama openstreet mape... ");
									 e.printStackTrace();
									}
							}
						}
					if(brzina < 200 && ispravnoVreme){
						javljanje = new Javljanja();
						javljanje.setValid(valid);
						javljanje.setObjekti(objekat);
						javljanje.setDatumVreme(datumVreme);
						javljanje.setLon(longitude);
						javljanje.setLat(latitude);
						javljanje.setPravac(pravac);
						javljanje.setBrzina(brzina);
						javljanje.setVisina(nadmorskaVisina);
						javljanje.setSistemAlarmi(alarm);
						javljanje.setKontakt(kontakt);
						javljanje.setVirtualOdo(virtualOdo);
						javljanje.setEventData(eventData);
						javljanje.setIbutton(iButton);
						javljanje.setKreirano(upis);
						javljanje.setIzmenjeno(upis);
					}
					return javljanje;
					}else{
						return null;
						}
				}else{
				System.out.println("Neispravan GPS podatak - ");
				return null;
				}
			}catch(Exception e){
				System.out.println("Nije podržan protokol" + " " + e.getMessage());
				return null;
				}
		}
	
	private Date parseDateTime(String dt, String tm) {
		try {
			int dd = Integer.parseInt(dt.substring(0, 2));
			int mm = Integer.parseInt(dt.substring(2, 4));
			int yy = Integer.parseInt("20" + dt.substring(4));
			int hh = Integer.parseInt(tm.substring(0, 2));
			int ii = Integer.parseInt(tm.substring(2, 4));
			int ss = Integer.parseInt(tm.substring(4));
			DateTime dateTime = new DateTime(yy, mm, dd, hh, ii, ss, DateTimeZone.UTC);
			return dateTime.toDate();
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private Double parseDouble(String v) {
		return parseDouble(v, null);
	}
	
	private Double parseDouble(String v, Double dv) {
		try {
			Double pv = Double.parseDouble(v);
			return pv;
		} catch (NumberFormatException ex) {
			return dv;
		}
	}
	
	private Float parseFloat(String v) {
		return parseFloat(v, null);
	}

	private Float parseFloat(String v, Float dv) {
		try {
			Float pv = Float.parseFloat(v);
			return pv;
		} catch (NumberFormatException ex) {
			return dv;
		}
	}
}
