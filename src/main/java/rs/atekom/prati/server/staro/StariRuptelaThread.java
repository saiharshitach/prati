package rs.atekom.prati.server.staro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.bind.DatatypeConverter;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.ObjekatZone;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import pratiBaza.tabele.Zone;
import rs.atekom.prati.server.JavljanjeObd;
import rs.atekom.prati.server.OpstiServer;
import rs.atekom.prati.server.Servis;


public class StariRuptelaThread implements Runnable{

	private Socket socket = null;
	private LinkedBlockingQueue<Socket> socketQueue;
	private InputStream input;
    private OutputStream out;
    private boolean isStopped = false;
    private boolean prekoracenje = false;
    private byte[] data;
    private byte[] odg = {(byte)0x00, (byte)0x02, (byte)0x64, (byte)0x01, (byte)0x13, (byte)0xbc};
    private int offset;
    private OpstiServer server;
    private ArrayList<ObjekatZone> objekatZone;
    private ArrayList<AlarmiKorisnik> alarmiKorisnici;
	private String testDate;
	private DateFormat formatter;
	private Date date;
    private Obd obdStop;
    private Javljanja javljanjePoslednje, javljanjeStop;
    private Objekti objekat;
    private Uredjaji uredjaj;
	private String ulaz;
	private boolean zaustavljeno;
	private boolean gorivo;
	private Long imei;
	private int brojIspodNivoa;//koliko puta je nivo goriva manji za više od 1%
	private Date pocetak;
    
    public StariRuptelaThread(LinkedBlockingQueue<Socket> queue, OpstiServer serverRuptela) {
    	socketQueue = queue;
		server = serverRuptela;
		data = new byte[1024];
		testDate = "01/07/2019 00:00:00";
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			date = formatter.parse(testDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pocetak = new Date();
		ulaz = "";
		zaustavljeno = false;
		gorivo = false;
		brojIspodNivoa = 0;
	}
    
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			out = socket.getOutputStream();
			int br = 0;
			while(!isStopped && !socket.isClosed()) {
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if (br <= 0) {
					break;
				}
				offset = 0;
				ulaz = DatatypeConverter.printHexBinary(data);
				
				System.out.println("ruptela " + ulaz);
				System.out.println(" ");
	            //jednom odrediti koji je uredjaj, objekat, detalji
				offset += 4;//offset = 4;
				if(uredjaj == null) {
					imei = Long.parseLong(ulaz.substring(offset, offset + 16), 16);
					uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(imei.toString());
					
					if(uredjaj != null) {
						objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);						
						
						if(objekat != null) {
							objekatZone = Servis.zonaObjekatServis.nadjiZoneObjektePoObjektu(objekat);
							alarmiKorisnici = Servis.alarmKorisnikServis.nadjiSveAlarmeKorisnikePoObjektu(objekat);
							javljanjePoslednje = Servis.javljanjeServis.nadjiPoslednjeJavljanjePoObjektu(objekat);
							
							boolean vremeStarijeOdStajanja = false;
							if(javljanjePoslednje != null) {
								long vreme  = pocetak.getTime() - javljanjePoslednje.getDatumVreme().getTime();
								if(objekat.getVremeStajanja() > 0 && (vreme/1000 > objekat.getVremeStajanja())) {
									vremeStarijeOdStajanja = true;
								}
							}
							
							if(javljanjePoslednje != null && javljanjePoslednje.getBrzina() < 6 && 
									!javljanjePoslednje.getSistemAlarmi().getSifra().equals("1095") && !vremeStarijeOdStajanja) {
								javljanjeStop = javljanjePoslednje;
								obdStop = Servis.obdServis.nadjiObdPoslednji(objekat);
								}else {
									javljanjeStop = null;
		            				obdStop = null;
		            				}
							}else {
								System.out.println("Ruptela nema objekta: " + imei.toString());
								}
						
						}else {
							System.out.println("Ruptela nema uredjaja: " + imei.toString());
							}
					//System.out.println("broj uzimanja objekta: " + broj);
					}
	            offset += 16;//offset = 20
	        	//ako je komanda 01
	            int komanda = Integer.parseInt(ulaz.substring(offset, offset + 2), 16);
	            
	            //System.out.println(imei + " komanda je: " + komanda);
	            if(komanda == 1 || komanda == 68) {
	            	//da dobijem broj zapisa
	            	offset += 4;//offset = 24, uključuje command id i recordsLeft
	            	if(objekat != null) {
	            		int brZapisa = 0;
		            	offset += 2;
		            	int ukZapisa = Integer.parseInt(ulaz.substring(offset - 2, offset),  16);
		            	//System.out.println("zapisa: " + ukZapisa);
            			/*if(objekat.getId().equals(Long.parseLong("2"))) {
            				System.out.println(ulaz);
            			}**/
		            	
	            		//standardni protokol
	            		if(komanda == 1) {
			            	ArrayList<String> zapisi = new ArrayList<String>();
			            	while(brZapisa < ukZapisa) {
			            		int pocetak = offset;
			            		offset += 46;
			            		int brJedan = Integer.parseInt(ulaz.substring(offset, offset + 2), 16);
			            		offset += 2;
			            		offset += brJedan * 4;
			            		int brDva = Integer.parseInt(ulaz.substring(offset, offset + 2), 16);
			            		offset += 2;
			            		offset += brDva * 6;
			            		int brCetiri = Integer.parseInt(ulaz.substring(offset, offset + 2), 16);
			            		offset += 2;
			            		offset += brCetiri * 10;
			            		int brOsam = Integer.parseInt(ulaz.substring(offset, offset + 2), 16);
			            		offset += 2;
			            		offset += brOsam * 18;
			            		zapisi.add(ulaz.substring(pocetak, offset));
			            		brZapisa++;
			            	}		            	
			            	for(String zapis : zapisi) {
		            			JavljanjeObd javljanjeObd = server.rProtokol.vratiJavljanje(0, objekat, zapis);
		            			obradaJavljanja(javljanjeObd.getJavljanje(), javljanjeObd.getObd()); 
			            	}
			            	
	            		//prošireni protokol
	            		}else {/*
	            			int prvi = Integer.parseInt(ulaz.substring(offset + 10, offset + 11));
	            			int drugi = Integer.parseInt(ulaz.substring(offset + 11, offset + 12));
	            			Obd obdTr = null;
	            			//System.out.println("prvi: " + prvi);
	            			//System.out.println("drugi: " + drugi);
	            			if(drugi == 0) {
	            				javljanjeObd = server.rProtokol.vratiExtended(0, objekat, zapis);
	            			}else {
	            				Obd javObd = Servis.obdServis.nadjiObdPoslednji(objekat);
	            				obdTr = javljanjeObd.getObd();
	            				if(javljanjeObd != null && javObd != null && obdTr != null) {
            						if(javObd.getAkumulator() != 0.0f) {
            							obdTr.setAkumulator(javObd.getAkumulator());
            						}
            						if(javObd.getGas() != 0.0f) {
            							obdTr.setGas(javObd.getGas());
            						}
            						if(javObd.getGreske() != "") {
            							obdTr.setGreske(javObd.getGreske());
            						}
            						if(javObd.getNivoGoriva() != 0.0f) {
            							obdTr.setNivoGoriva(javObd.getNivoGoriva());
            						}
            						if(javObd.getOpterecenje() != 0.0f) {
            							obdTr.setOpterecenje(javObd.getOpterecenje());
            						}
            						if(javObd.getProsecnaPotrosnja() != 0.0f) {
            							obdTr.setProsecnaPotrosnja(javObd.getProsecnaPotrosnja());
            						}
            						if(javObd.getRpm() != 0) {
            							obdTr.setRpm(javObd.getRpm());
            						}
            						if(javObd.getTripGorivo() != 0.0f) {
            							obdTr.setTripGorivo(javObd.getTripGorivo());
            						}
            						if(javObd.getTripKm() != 0.0f) {
            							obdTr.setTripKm(javObd.getTripKm());
            						}
            						if(javObd.getUkupnoVreme() != 0.0f) {
            							obdTr.setUkupnoVreme(javObd.getUkupnoVreme());
            						}
            						if(javObd.getUkupnoGorivo() != 0.0f) {
            							obdTr.setUkupnoGorivo(javObd.getUkupnoGorivo());
            						}
            						if(javObd.getUkupnoKm() != 0.0f) {
            							obdTr.setUkupnoKm(javObd.getUkupnoKm());
            						}
	            				}
	            			}
            				obradaJavljanja(javljanjeObd.getJavljanje(), obdTr);
            					            		
			            out.write(odg);
						out.flush();**/
	            		}

		            	}
	            	}else {
	            		System.out.println("druga komanda... " + komanda);
	            		}
	            //ovde je bio odgovor
				//System.out.println("odgovor " + imei);
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("thread ruptela interrupted exiting");
					return;
					}
				}
			stop();
		} catch(SocketTimeoutException e){
			//System.out.println("ruptela thread soket timeout " + e.getMessage());
			stop();
		} catch(SocketException e){
			//System.out.println("ruptela thread soket greška " + e.getMessage());
	    	stop();
		} catch (Throwable e) {
			String por = " ruptela: ";
			if(objekat != null) {
				por += objekat.getOznaka();
			}
			System.out.println("ruptela thread throwable greška " + e.getMessage() + por);
			try {
				out.write(odg);
				out.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void obradaJavljanja(Javljanja javljanjeTrenutno, Obd obdTrenutni) {
		if(javljanjeTrenutno != null  && javljanjeTrenutno.getBrzina() < 200 && javljanjeTrenutno.getDatumVreme().after(date)) {
			JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat);
			//obracun km
			if(poslednje != null) {
				if(javljanjeTrenutno.getDatumVreme().after(javljanjePoslednje.getDatumVreme())) {
					javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanjeTrenutno, poslednje));
				}else {
					javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo());
				}
			}else {
				javljanjeTrenutno.setVirtualOdo(0.0f);
			}
			//
			if(javljanjeTrenutno.getBrzina() > 5) {
				javljanjeStop = null;
    			obdStop = null;
    			zaustavljeno = false;
    			gorivo = false;
    			brojIspodNivoa = 0;
    			}else {
    				if(javljanjeStop == null) {
    					javljanjeStop = javljanjeTrenutno;
    					obdStop = obdTrenutni;
    					}
    				}
			//alarm stajanje
			//System.out.println("stajanje ");
			if(javljanjeStop != null && !zaustavljeno) {
				long vreme = (javljanjeTrenutno.getDatumVreme().getTime() - javljanjeStop.getDatumVreme().getTime());
				if(objekat.getVremeStajanja() > 0 && (vreme > (objekat.getVremeStajanja() * 60 * 1000))) {
					System.out.println("vreme " + vreme);
					if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
						javljanjeTrenutno.setSistemAlarmi(server.stajanje);
						zaustavljeno = true;
						}else {
							//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
							javljanjeTrenutno.setSistemAlarmi(server.stajanje);
							zaustavljeno = true;
						}
					}
				}
			
			//alarm prekoračenje brzine
			if(objekat.getPrekoracenjeBrzine() != 0) {
				if((javljanjeTrenutno.getBrzina() > objekat.getPrekoracenjeBrzine()) && !prekoracenje) {
					prekoracenje = true;
					if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
						javljanjeTrenutno.setSistemAlarmi(server.prekoracenjeBrzine);
					}else {
						//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
						javljanjeTrenutno.setSistemAlarmi(server.prekoracenjeBrzine);
					}
					if(javljanjeTrenutno.getEventData().equals("0")) {
						javljanjeTrenutno.setEventData(javljanjeTrenutno.getBrzina() + "км/ч");
					}else {
						javljanjeTrenutno.setEventData(javljanjeTrenutno.getBrzina() + "км/ч, " + javljanjeTrenutno.getEventData());
					}
				}else {
					prekoracenje = false;
				}
			}
			
    		//alarm gorivo
    		if(obdTrenutni != null) {
    			if(obdStop != null) {
    				if(!gorivo) {
    					if(obdTrenutni.getNivoGoriva() - obdStop.getNivoGoriva() > 1 && brojIspodNivoa > 10) {
    						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
    							javljanjeTrenutno.setSistemAlarmi(server.istakanje);
    							gorivo = true;
    							}else {
    								//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
    								javljanjeTrenutno.setSistemAlarmi(server.istakanje);
    								gorivo = true;
    							}
    						}else {
        						if(obdStop.getNivoGoriva() - obdTrenutni.getNivoGoriva() > 1) {
        							brojIspodNivoa++;
        							}
        						}
    					}else {
        					brojIspodNivoa = 0;
        					}
    				}
    			Servis.obdServis.unesiObd(obdTrenutni);
    			}
    		
    		//alarm zona
    		if(objekatZone != null && objekatZone.size() > 0) {
    			Zone zonaPoslednja = null;
    			if(Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat) != null) {
    				zonaPoslednja = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat).getZona();
    			}
				//ulazak
				if(zonaPoslednja == null) {
					for(ObjekatZone objekatZona : objekatZone) {
						if(objekatZona.isAktivan() && objekatZona.isIzlaz()) {
        					if(Servis.obracun.rastojanjeKoordinate(javljanjeTrenutno, objekatZona.getZone().getLat(), objekatZona.getZone().getLon()) <= objekatZona.getZone().getPrecnik()) {
        						javljanjeTrenutno.setZona(objekatZona.getZone());
        						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
        							javljanjeTrenutno.setSistemAlarmi(server.ulazak);
        							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
        							break;
        						}else {
        							//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
        							javljanjeTrenutno.setSistemAlarmi(server.ulazak);
        							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
        							break;
        						}
        					}
						}
					}
				}else {
					//izlazak
					ObjekatZone objZona = Servis.zonaObjekatServis.nadjiObjekatZonuPoZoniObjektu(objekat, zonaPoslednja);
					if(objZona != null && objZona.isAktivan() && objZona.isIzlaz()) {
    					if(Servis.obracun.rastojanjeKoordinate(javljanjeTrenutno, zonaPoslednja.getLat(), zonaPoslednja.getLon()) > zonaPoslednja.getPrecnik()) {
    						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
    							javljanjeTrenutno.setSistemAlarmi(server.izlazak);
    							javljanjeTrenutno.setEventData(zonaPoslednja.getNaziv());
    						}else {
    							//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
    							javljanjeTrenutno.setSistemAlarmi(server.izlazak);
    							javljanjeTrenutno.setEventData(zonaPoslednja.getNaziv());
    						}
    						javljanjeTrenutno.setZona(null);
    					}else {
    						javljanjeTrenutno.setZona(zonaPoslednja);
    					}
					}else {
						javljanjeTrenutno.setZona(zonaPoslednja);
					}
				}
    		}
    		//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
    		}
	}
	
	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
    	try{
			if(socket != null && !socket.isClosed()){
	    		out.write(odg);
	    		out.flush();
				input.close();
				out.close();
				socket.close();
				server.removeClientSocket(socket);
				//System.out.println("coban stream connection closed ");
			}
		}catch(IOException e){
			System.out.println("ruptela stream connection closed problem...");
		}
		return;
	}
}
