package rs.atekom.prati.server;

import static java.lang.Math.toIntExact;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;


public class NyiTechProtokol {
	
	private SistemAlarmi redovno;
	
	public NyiTechProtokol() {
		redovno = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(0));
	}
	
	public NyiTechPar<Javljanja, Obd> nyiTechObrada( String uredjaj, String eventCode, String ulaz){
		NyiTechPar<Javljanja, Obd> par = null;
		Javljanja javljanje = null;
		Objekti objekat = null;
		Obd obd = null;
		String uredjajId = "";
		boolean obdData = false;
		boolean gps = false;
		boolean gSensor = false;
		String binarni = "";
		Timestamp datumVreme = new Timestamp(parseDateTime("010170", "000000").getTime());
		boolean valid = true;
		double longitude = 0.00;
		double latitude = 0.00;
		int brzina = 0;
		float pravac = 0.00f;
		float nadmorskaVisina = 0.00f;
		int temp = 0;
		int rpm = 0;
		float nivoGoriva = 0.00f;
		float opterecenje = 0.00f;
		float gas = 0.00f;
		String iButton = "0";
		float virtualOdo = 0.00f;
		float tripgorivo = 0.00f;
		float tripkm = 0.00f;
		float tripvreme = 0.00f;
		float ukupnogorivo = 0.00f;
		int ukupnokm = 0;	
		float prosecna_potrosnja = 0.00f;//prosečna potrošnja goriva	
		float naponAkumulatora = 0.00f;
		boolean kontakt = false;
		SistemAlarmi alarm = null;
		uredjajId = uredjaj;
		String eventData = "0";
		String alarm_id = "0";
		try{	
			//ovde je regularno javljanje
			 if(eventCode.equals("2001") || eventCode.equals("2002")){
				 datumVreme = new Timestamp(parseDateTime(ulaz.substring(0, 6), ulaz.substring(6, 12)).getTime());
				 
				  if(ulaz.substring(12, 14).equals("80")){
					  gps= true;
				  }
				  if(ulaz.substring(14, 16).equals("80")){
					  obdData  = true;
				  }
				  if(ulaz.substring(16, 18).equals("80")){
					  gSensor = true;
				  }
				  
				  binarni = lokacija(ulaz.substring(31, 32) + ulaz.substring(30, 31));
			  
			  if(gps == true){
					valid = true;
				}else{
					valid = false;
				}
				if(valid == true){
					String longiString = ulaz.substring(46, 48) + ulaz.substring(44, 46) + ulaz.substring(42, 44) + ulaz.substring(40, 42);
					int longit = Integer.parseInt(longiString,16);
					longitude = longit/3600000.00;
					
					if(!binarni.substring(0, 1).equals("1")){
						longitude = longitude * -1;
					}
					
					String latiString = ulaz.substring(38, 40) + ulaz.substring(36, 38) + ulaz.substring(34, 36) + ulaz.substring(32, 34);
					int latit = Integer.parseInt(latiString,16);
					latitude = latit/3600000.00;
					
					if(!binarni.substring(1,2).equals("1")){
						latitude = latitude * -1;
					}
				}
				
				 int gpsBrzina = Integer.parseInt((ulaz.substring(50, 52) + ulaz.substring(48, 50)),16);
				 brzina = gpsBrzina*36/1000;
				 
				
				 int gpsPravac = Integer.parseInt((ulaz.substring(54, 56) + ulaz.substring(52, 54)),16);
				 pravac = gpsPravac/10.00f;
				 
				 int gpsNadmorska = Integer.parseInt((ulaz.substring(58, 60) + ulaz.substring(56, 58)),16);	
				 nadmorskaVisina = gpsNadmorska/10.00f;
				 
				 
				 int ind = 60;	
					
					if(obdData == true){
						int brPid = Integer.parseInt(ulaz.substring(ind, ind + 2),16);
						ind = ind + 2;
					for(int j = 0; j < brPid; j++){
						String pid = ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind, ind + 2);
						
						switch(pid){
						
						case "2005": int temp1Temp = Integer.parseInt(ulaz.substring(ind + 6, ind + 8),16);//temperatura
							         temp = temp1Temp - 40;
							         ind = ind + 8;
							         //System.out.println(temp + " temp");
							         break;
							
						case "2045": int xThrottle = Integer.parseInt(ulaz.substring(ind + 6, ind + 8),16);//pedala gasa
						             //float throttleKoef = 100/255;
						             gas = xThrottle*100.00f/255.00f;//*throttleKoef;
							         ind = ind + 8;
							         //System.out.println(gas + " gas");
					                 break;
					    
						case "2004": int xOpterecenje = Integer.parseInt(ulaz.substring(ind + 6, ind + 8),16);//calculated load value
						             //float opterecKoef = 100/255;
			                         opterecenje = xOpterecenje*100.00f/255.00f;//.floatValue()*opterecKoef;
				                     ind = ind + 8;
				                     //System.out.println(opterecenje + " opt");
		                             break;
					    
						case "200B": ind = ind + 8;
						             break;
						       
						case "200C": int x1 = Integer.parseInt(ulaz.substring(ind + 8, ind + 10),16);//broj obrtaja
						             int x2 = Integer.parseInt(ulaz.substring(ind + 6, ind + 8),16);
						             rpm = (x1*256 + x2)/4;
						             //System.out.println(rpm+ " rpm");
						             ind = ind + 10;
						             break;
						
						case "202F": int nGorivo = Integer.parseInt(ulaz.substring(ind + 6, ind + 8),16);//nivo goriva vozila
						             //float nivoKoef = 100/255;
						             nivoGoriva = nGorivo*100.00f/255.00f;
						             //System.out.println(nivoGoriva + " nivo");
						             ind = ind + 8;
						             break;
						             
						case "0050": int nGorivoMasine = Integer.parseInt(ulaz.substring(ind + 6, ind +8),16);//nivo goriva mašine
						             nivoGoriva = nGorivoMasine*0.40f;
						             ind = ind + 8;
						             //System.out.println(nivoGoriva + " nivo2");
						             break;
						             
						case "00F5": if(!ulaz.substring(ind + 6, ind +14).equals("FFFFFFFF") && !ulaz.substring(ind + 6, ind +14).equals("00000000")){
							          long ukupnoKmMasine = Integer.parseInt(ulaz.substring(ind + 6, ind +14), 16);
				                      ukupnokm = toIntExact(ukupnoKmMasine * 125/1000);
				                      //System.out.println(ukupnokm + " ukKm");
				                      ind = ind + 14;
						             }
						             break;
						             
						case "00FA": if(!ulaz.substring(ind + 6, ind +14).equals("FFFFFFFF") && !ulaz.substring(ind + 6, ind +14).equals("00000000")){
							         long ukupnoGorivoMasine = Integer.parseInt(ulaz.substring(ind + 6, ind +14), 16);
	                                 ukupnogorivo = ukupnoGorivoMasine * 0.5f;
	                                 //System.out.println(ukupnogorivo + " ukgor");
	                                 ind = ind + 14;
						             }
						             break;
						             
						case "00B9": int potrosnjaMasina = Integer.parseInt(ulaz.substring(ind + 8, ind + 12), 16);
						//Integer potrosnjaMasina = Integer.parseInt(ulaz.substring(ind + 10, ind + 12) + ulaz.substring(ind + 8, ind + 10), 16);
						             prosecna_potrosnja = 100/(potrosnjaMasina/512f);
						             //System.out.println(prosecna_potrosnja + " prospotr");
						             ind = ind + 10;
						             break;
						             
						default: if(ulaz.substring(ind + 4, ind + 6).equals("02")){
							           ind = ind + 10;
						           }else if(ulaz.substring(ind + 4, ind + 6).equals("04")){
						        	   ind = ind + 14;
						           }else{
						        	   ind = ind + 8;
						           }
						             break;
						}
					}
					//Ovde gorivo... pa onda dodajem + 8
					
					String tripFuelString = ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
							ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2);
					int tripFuel = Integer.parseInt(tripFuelString,16);
					tripgorivo = tripFuel/100.00f;
					//System.out.println(tripgorivo + " trip gor");
					ind = ind + 8;
					
					String tripKmString = ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
							ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2);
					int tripKm = Integer.parseInt(tripKmString, 16);
					tripkm = tripKm/1000.00f;
					
					//System.out.println(tripkm + " trip km");
					ind = ind + 8;
					
					int tripVreme = Integer.parseInt(ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
							ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2), 16);
					tripvreme = tripVreme/1000.00f;
					
					ind = ind + 8;
				}	
					if(gSensor == true){
						try{
							String sensorDuzina = ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind, ind + 2);
							int sensor = Integer.parseInt(sensorDuzina, 16);
							ind = ind + sensor*2 + 4;
						}catch(Exception e){
							System.out.println("Greška g sensora " + ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2));
						}
					}	
					try{
						int naponAk = Integer.parseInt(ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2),16);
						naponAkumulatora = naponAk/10.00f;
					}catch(Exception e){
						System.out.println("Greška napon akumulatora " + ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2));
					}
					//System.out.println(naponAkumulatora + " V");
					int accOnOff = Integer.parseInt(ulaz.substring(ind + 8, ind + 10),16);
					ind = ind + 16;//+ 2byte za akumulator + 2byte za protokol 1byte ACC on/off 1byte MMCX 2byte Reserved **/
					
					if(/*rpm > 0**/ accOnOff == 1){
						kontakt = true;
					}else{
						kontakt = false;
					}
					if(!obdData){
						kontakt = true;//ovo proveriti, napravljeno ovako za slučaj kada je isključen OBD
					}
					if(obdData){
						if(ukupnokm==0){
							try{
								int ukupnoKm = Integer.parseInt(ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
										ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2), 16);
								ukupnokm = ukupnoKm/1000;
								ind = ind + 8;
							}catch(Exception e){
								System.out.println("Greška ukupno km " + ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
										ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2));
							}
						}
						if(ukupnogorivo==0.00f){
							try{
								int ukupnoGorivo = Integer.parseInt(ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
										ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2), 16);
								ukupnogorivo = ukupnoGorivo/100.00f;
							}catch(Exception e){
								System.out.println("Greška ukupno gorivo " + ulaz.substring(ind + 6, ind + 8) + ulaz.substring(ind + 4, ind + 6) + 
										ulaz.substring(ind + 2, ind + 4) + ulaz.substring(ind + 0, ind + 2));
							}
						}
					}	
//ovde je javljanje alarma..................................................................................................
			 }else if(eventCode.equals("2003")){
				 //int pozicija = 0;
			  datumVreme = new Timestamp(parseDateTime(ulaz.substring(16, 22), ulaz.substring(22, 28)).getTime());
			  
			  binarni = lokacija(ulaz.substring(41, 42) + ulaz.substring(40, 41));
			  
			  if(!binarni.substring(0, 4).equals("0000")){
					valid = true;
				}else{
					valid = false;
				}
				if(valid == true){
					String longiString = ulaz.substring(56, 58) + ulaz.substring(54, 56) + ulaz.substring(52, 54) + ulaz.substring(50, 52);
					int longit = Integer.parseInt(longiString,16);
					longitude = longit/3600000.00;
					if(!binarni.substring(0, 1).equals("1")){
						longitude = longitude * -1;
					}
					String latiString = ulaz.substring(48, 50) + ulaz.substring(46, 48) + ulaz.substring(44, 46) + ulaz.substring(42, 44);
					int latit = Integer.parseInt(latiString,16);
					latitude = latit/3600000.00;
					if(!binarni.substring(1,2).equals("1")){
						latitude = latitude * -1;
					}
				}
				 int gpsBrzina = Integer.parseInt((ulaz.substring(60, 62) + ulaz.substring(58, 60)),16);
				 brzina = gpsBrzina*36/1000;
				 int gpsPravac = Integer.parseInt((ulaz.substring(64, 66) + ulaz.substring(62, 64)),16);
				 pravac = gpsPravac/10.00f;
				 int gpsNadmorska = Integer.parseInt((ulaz.substring(68, 70) + ulaz.substring(66, 68)),16);	
				 nadmorskaVisina = gpsNadmorska/10.00f;
				 String alarmString = ulaz.substring(6, 8);
				 String vrednost = ulaz.substring(4,6);
				 Boolean jedinica = vrednost.equals("01");
				 switch(alarmString){
				 case "01": if(jedinica){
					   alarm_id = "6003";
				   }else{
				       alarm_id = "6004";
				               }
				           kontakt = true;
				 break;
				 case "02": if(jedinica){
		               alarm_id = "6005";
	               }else{
	            	   alarm_id = "6006";
	               }
				 break;
				 case "03": if(jedinica){
		               alarm_id = "6007";
	               }else{
	            	   alarm_id = "6008";
	               }
		              kontakt = true;
				 break;
				 case "04": if(jedinica){
		               alarm_id = "6009";
	               }else{
	            	   alarm_id = "6010";
	               }
		               kontakt = true;
				 eventData = naponAkumulatora + "V";
				 break;
				 case "05": if(jedinica){
		               alarm_id = "6001";
		               eventData = String.valueOf(temp) + "°C";
	               }else{
	            	   alarm_id = "6002";
	               }
		               kontakt = true;
				 break;
				 case "06": if(jedinica){
		               alarm_id = "6011";
	               }else{
	            	   alarm_id = "6012";
	               }
		              kontakt = true;
				 break;
				 case "07": if(jedinica){
		               alarm_id = "6013";
		               eventData = String.valueOf(brzina) + " км/ч";
	               }else{
	            	   alarm_id = "6014";
	               }
		              kontakt = true;
				 break;
				 case "08": if(jedinica){
		               alarm_id = "6015";
		               eventData = String.valueOf(rpm) + " обр/мин";
	               }else{
	            	   alarm_id = "6016";
	               }
		              kontakt = true;
				 break;
				 case "0F": if(jedinica){
		               alarm_id = "1094";
			          kontakt = true;
	               }else{
	            	   alarm_id = "1093";
			          kontakt = false;
	               }
				 break;
				 case "09": alarm_id = "6017";
		              kontakt = true;
				 break;
				 case "0A": alarm_id = "6018";
		              kontakt = true;
				 break;
				 case "0B": alarm_id = "6019";
				 break;
				 case "0C": alarm_id = "6020";
				 break;
				 case "0D": alarm_id = "6021";
				 break;
				 case "0E": alarm_id = "6022";
		              kontakt = true;
				 //eventData = "ПОЗИВ ЗА ПОМОЋ!";
				 break;
				 default : alarm_id = "0";
				 break;
				 }//ovde ide sleep.........................................................................................................................................................
				 
			 }else if(eventCode.equals("2004")){
				 datumVreme = new Timestamp(parseDateTime(ulaz.substring(0, 6), ulaz.substring(6, 12)).getTime());
				 binarni = lokacija(ulaz.substring(25, 26) + ulaz.substring(24, 25));
				 if(!binarni.substring(0, 4).equals("0000")){
					 valid = true;
					 }else{
						 valid = false;
						 }
				 if(valid == true){
					 String longiString = ulaz.substring(40, 42) + ulaz.substring(38, 40) + ulaz.substring(36, 38) + ulaz.substring(34, 36);
					 int longit = Integer.parseInt(longiString,16);
					 longitude = longit/3600000.00;
					 if(!binarni.substring(0, 1).equals("1")){
						 longitude = longitude * -1;
						 }
					 String latiString = ulaz.substring(32, 34) + ulaz.substring(30, 32) + ulaz.substring(28, 30) + ulaz.substring(26, 28);
					 int latit = Integer.parseInt(latiString,16);
					 latitude = latit/3600000.00;
					 if(!binarni.substring(1,2).equals("1")){
						 latitude = latitude * -1;
						 }
					 }
				 int gpsBrzina = Integer.parseInt((ulaz.substring(44, 46) + ulaz.substring(42, 44)),16);
				 brzina = gpsBrzina*36/1000;
				 int gpsPravac = Integer.parseInt((ulaz.substring(48, 50) + ulaz.substring(46, 48)),16);
				 pravac = gpsPravac/10.00f;
				 int gpsNadmorska = Integer.parseInt((ulaz.substring(52, 54) + ulaz.substring(50, 52)),16);	
				 nadmorskaVisina = gpsNadmorska/10.00f;
				 }
			 Date sada = new Date();
			 Timestamp upis = new Timestamp(sada.getTime());
			 if((alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(alarm_id))) == null){
				 alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(0));
				 }
			 //uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(uredjajId);
			 objekat = Servis.uredjajServis.nadjiUredjajPoKodu(uredjajId).getObjekti();
			 if(objekat != null){
				 if(obdData){
					 obd = new Obd();
					 obd.setObjekti(objekat);
					 obd.setDatumVreme(datumVreme);
					 obd.setRpm(rpm);
					 obd.setTemperatura(temp);
					 obd.setOpterecenje(opterecenje);
					 obd.setGas(gas);
					 obd.setNivoGoriva(nivoGoriva);
					 obd.setAkumulator(naponAkumulatora);
					 obd.setTripKm(tripkm);
					 obd.setTripGorivo(tripgorivo);
					 obd.setUkupnoVreme(tripvreme);
					 obd.setUkupnoGorivo(ukupnogorivo);
					 obd.setUkupnoKm(ukupnokm);
					 obd.setProsecnaPotrosnja(prosecna_potrosnja);
					 obd.setKreirano(upis);
					 obd.setIzmenjeno(datumVreme);
					 }else{
						 obd = null;
						 }
				 alarm = Servis.sistemAlarmServis.nadjiAlarmPoSifri(String.valueOf(alarm_id));
				 if(alarm == null || alarm_id.equals("0")){
						alarm = redovno;
						}
				 if(brzina < 200){
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
				 par = new NyiTechPar<Javljanja, Obd>(javljanje, obd);
				 return par;
				 }else{
					 return null;
					 }
		}catch(Exception e){
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Nije uspelo parsiranje protokol " + e);
			}
		return par;
	}
	
	private String lokacija (String hexLokacija){
		int num = (Integer.parseInt(hexLokacija, 16));
		String binarno = Integer.toBinaryString(num);
		return binarno;
	}
	
	private Date parseDateTime(String datum, String vreme) {
		try {
			int dd = Integer.parseInt(datum.substring(0, 2),16);
			int mm = Integer.parseInt(datum.substring(2, 4),16);
			int yy = Integer.parseInt(datum.substring(4),16) + 2000;
			int hh = Integer.parseInt(vreme.substring(0, 2),16);
			int ii = Integer.parseInt(vreme.substring(2, 4),16);
			int ss = Integer.parseInt(vreme.substring(4),16);
			DateTime dateTime = new DateTime(yy, mm, dd, hh, ii, ss, DateTimeZone.UTC);
			return dateTime.toDate();
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	 public String convertHexToString(String hex){
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
		  for( int i=0; i<hex.length()-1; i+=2 ){
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
		      temp.append(decimal);
		  }
		  return sb.toString();
	  }
}
