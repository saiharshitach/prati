package rs.atekom.prati.server.staro;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.ObjekatZone;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import pratiBaza.tabele.Zone;
import rs.atekom.prati.server.OpstiServer;
import rs.atekom.prati.server.Servis;

public class StariNeonThread implements Runnable{

	private Socket socket;
	private LinkedBlockingQueue<Socket> socketQueue;
	private OpstiServer server;
	private InputStream input;
	private byte[] data;
	private boolean isStopped = false;
	private ArrayList<ObjekatZone> objekatZone;
	private ArrayList<AlarmiKorisnik> alarmiKorisnici;
	private boolean prekoracenje;
	private Uredjaji uredjaj;
	private String kodUredjaja;
	private Objekti objekat;
	private Javljanja javljanjeTrenutno, javljanjePoslednje, javljanjeStop;
	private boolean zaustavljeno;
	private String[] da;
	private String [] niz;
	private int brojPromasaja;
	private String inputLine;
	private Date pocetak;
	
	public StariNeonThread(LinkedBlockingQueue<Socket> queue, OpstiServer srv) {
    	socketQueue = queue;
    	data = new byte[2048];
    	server = srv;
    	prekoracenje = false;
    	zaustavljeno = false;
    	brojPromasaja = 0;
    	inputLine = "";
    	pocetak = new Date();
	}
	
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			int br = 0;
			
			while (!isStopped() && !socket.isClosed()) {
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if(br <= 0){
					break;
				}
				
		    	inputLine = new String(data, 0, br);
		    	
		    	//System.out.println("poruka početak " + inputLine);
		    	niz = inputLine.split(">");
		    	for(int i = 0; i < niz.length; i++){
					if(niz[i].startsWith("<oris") || niz[i].startsWith("#<oris")) {
						da = niz[i].split(",");
						
						if(uredjaj == null){
							kodUredjaja = da[2];
							
			        		if(kodUredjaja != null && !kodUredjaja.isEmpty() && !kodUredjaja.equals("")){
			        			uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(kodUredjaja);
			        			
			        			if(uredjaj != null){
					        		objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);
					        		
					        		if(objekat != null){
					        			objekatZone = Servis.zonaObjekatServis.nadjiZoneObjektePoObjektu(objekat);
					        			alarmiKorisnici = new ArrayList<AlarmiKorisnik>();
										alarmiKorisnici.addAll(Servis.alarmKorisnikServis.nadjiSveAlarmeKorisnikePoObjektu(objekat));
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
											}else {
												javljanjeStop = null;
					            				}
										
						        		}else {
						        			System.out.println("neon nema objekta " + inputLine);
										}
					        		}else{
					        			System.out.println("neon nema uredjaja " + inputLine);
					        			//break;
					        			}
				        		}else{
				        			uredjaj = null;
				        			}
							//break;
							}
						
						if(objekat != null ){
							//System.out.println("objekat " + inputLine);
							javljanjeTrenutno = server.nProtokol.neonObrada(da, inputLine, objekat);
							
		        			if(javljanjeTrenutno != null){

		        				//obracun km
		        				if(javljanjePoslednje != null) {
		        					//System.out.println("obracun ");
		        					JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat);
		        					if(poslednje != null && javljanjeTrenutno.getDatumVreme().after(poslednje.getDatumVreme())) {
		        						javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanjeTrenutno, poslednje));
		        					}else {
		        						javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo());
		        					}
		        				}else {
		        					javljanjeTrenutno.setVirtualOdo(0.0f);
		        				}
		        				
		        				//stop
		        				//System.out.println("stop " + inputLine);
		        				if(javljanjeTrenutno.getSistemAlarmi() != null && javljanjeTrenutno.getSistemAlarmi().getSifra().equals("1095")){
		        					zaustavljeno = true;
		        					}
		        				if(javljanjeTrenutno.getBrzina() > 5){
		        					javljanjeStop = null;
		        					}else{
		        						if(javljanjeStop == null){
		        							javljanjeStop = javljanjeTrenutno;
		        							}
		        						}

		            			//alarm stajanje
		        				//System.out.println("stajanje ");
	            				if(javljanjeStop != null && !zaustavljeno) {
            						long vremeRazlika = javljanjeTrenutno.getDatumVreme().getTime() - javljanjeStop.getDatumVreme().getTime();
            						if(objekat.getVremeStajanja() != 0 && (vremeRazlika / 1000 > objekat.getVremeStajanja() * 60)) {
            							if(javljanjeTrenutno.getSistemAlarmi() != null && javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
            								javljanjeTrenutno.setSistemAlarmi(server.stajanje);
            								//System.out.println("stajanje alarm " + javljanjeTrenutno.getSistemAlarmi().getSifra() + " " + inputLine);
            								zaustavljeno = true;
            								}else {
            									//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
            									javljanjeTrenutno.setSistemAlarmi(server.stajanje);
            									zaustavljeno = true;
            								}
            							}
	            					}
	            				
		        				//System.out.println("prekoracenje " + inputLine);
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
	            				
	            				//System.out.println("zona " + inputLine);
			            		//alarm zona
			            		if(objekatZone != null && objekatZone.size() > 0) {
			            			Zone zonaPoslednja = null;
			            			Javljanja poslednje = Servis.javljanjeServis.nadjiPoslednjeJavljanjePoObjektu(objekat);
			            			if(poslednje != null) {
			            				zonaPoslednja = poslednje.getZona();
			            			}
		            				//ulazak
		            				if(zonaPoslednja == null && javljanjeTrenutno.getDatumVreme().after(poslednje.getDatumVreme())) {
		            					for(ObjekatZone objekatZona : objekatZone) {
		            						if(objekatZona.isAktivan() && objekatZona.isUlaz()) {
				            					if(Servis.obracun.rastojanjeKoordinate(javljanjeTrenutno, objekatZona.getZone().getLat(), objekatZona.getZone().getLon()) <= objekatZona.getZone().getPrecnik()) {
				            						javljanjeTrenutno.setZona(objekatZona.getZone());
				            						if(javljanjeTrenutno.getSistemAlarmi() != null && javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
				            							javljanjeTrenutno.setSistemAlarmi(server.ulazak);
				            							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
				            							//break;
				            						}else {
				            							//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
				            							javljanjeTrenutno.setSistemAlarmi(server.ulazak);
				            							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
				            							//break;
				            						}
				            					}
		            						}
		            					}
		            				}else {
		            					//izlazak
		            					ObjekatZone objZona = Servis.zonaObjekatServis.nadjiObjekatZonuPoZoniObjektu(objekat, zonaPoslednja);
		            					if(javljanjeTrenutno.getDatumVreme().after(poslednje.getDatumVreme()) && objZona != null && objZona.isAktivan() && objZona.isIzlaz()) {
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
			            		//System.out.println("izvrsavanje ");
	            				//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
	            				//System.out.println("kraj " + inputLine);
		        				}else{
		        					System.out.println("javljanje null: " + inputLine);
		        					//break;
		        					}
		        			//ako je javljanje null - greška iz protokola??
		        			}else {
		        				System.out.println("objekat null... " + inputLine);
		        				//break;
		        				}
		        		//ako je objekat null
			        	//ako je inputline prazno
			        	}else {
			        		if(!niz[i].equals("#"))
			        			System.out.println("nije oris... " + niz[i]);
			        		if(brojPromasaja > 20) {
			        			break;
			        		}else {
			        			brojPromasaja++;
			        			}
			        		}
					}
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("thread neon interrupted exiting... ");
					break;
					}
				}
			stop();
			} catch(SocketTimeoutException e){
				//System.out.println("neon thread soket timeout " + e.getMessage());
				stop();
			} catch(SocketException e){
				System.out.println("neon thread soket greška " + e.getMessage());
				stop();
			} catch (Throwable e) {
				String por = " neon: ";
				if(objekat != null) {
					por += objekat.getOznaka();
				}
				System.out.println("neon thread throwable greška " + e.getMessage() + por);
				stop();
			}
		}
	
	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
		try{
			if(socket != null && !socket.isClosed()){
				input.close();
				socket.close();
				server.removeClientSocket(socket);
				//System.out.println("neon stream connection closed ");
			}
		}catch(Throwable e){
			System.out.println("neon stream connection closed problem...");
			}
		return;
		}
}
