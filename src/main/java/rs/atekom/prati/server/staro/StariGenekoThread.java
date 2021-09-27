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

public class StariGenekoThread implements Runnable{

	private Socket socket = null;
	private LinkedBlockingQueue<Socket> socketQueue;
	private OpstiServer server;
	private InputStream input;
	private boolean isStopped = false;
	private ArrayList<ObjekatZone> objekatZone;
	private ArrayList<AlarmiKorisnik> alarmiKorisnici;
	private byte[] data;
	private boolean prekoracenje;
	private Uredjaji uredjaj;
	private Objekti objekat;
	private String kodUredjaja;
	private Javljanja javljanjeTrenutno, javljanjePoslednje, javljanjeStop;
	private String [] da;
	private String [] niz;
	private String inputLine;
	private boolean zaustavljeno;
	private Date pocetak;
	
	public StariGenekoThread(LinkedBlockingQueue<Socket> queue, OpstiServer srv) {
		socketQueue = queue;
		data = new byte[2048];
		server = srv;
		prekoracenje = false;
		inputLine = "";
		zaustavljeno = false;
		pocetak = new Date();
	}
	
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			int br = 0;
			
			while(!isStopped() && !socket.isClosed()){
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if (br <= 0) {
					break;
				}
				inputLine = new String(data, 0, br);
				
				//System.out.println("geneko poruka " + inputLine);

				niz = inputLine.split("</fox>");
				for(int i = 0; i < niz.length; i++){
					//System.out.println("geneko poruka početak" + inputLine);
					if(niz[i].startsWith("<fox>")) {
						da = niz[i].split("\"");
	                	if(uredjaj == null){
		                	kodUredjaja = da[1];
		                	if(kodUredjaja != null){
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
				                		}else{
				                			System.out.println("objekat null... prvi put" + da[1]);
				                			//break;
				                			}
			                		}
		                	}else{
		                		uredjaj = null;
		                	}
		                	//break;
	                	}
		                if(objekat != null){
		                	//System.out.println("geneko poruka obrada " + inputLine);
		                	javljanjeTrenutno = server.gProtokol.genekoObrada(da[3], objekat);
		                	
		                	if(javljanjeTrenutno != null  && javljanjeTrenutno.getBrzina() < 200){
		                        JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat);
		                        //System.out.println("geneko poruka obracun " + inputLine);
								upisObracun(javljanjeTrenutno, poslednje);
								
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
		                        //System.out.println("geneko poruka prekoracenje " + inputLine);
		            			//alarm stajanje
	            				if(javljanjeStop != null) {
	            					long vremeRazlika = javljanjeTrenutno.getDatumVreme().getTime() - javljanjeStop.getDatumVreme().getTime();
	            					if(!zaustavljeno) {
	            						if(objekat.getVremeStajanja() != 0 && vremeRazlika / 1000 > objekat.getVremeStajanja() * 60) {
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
	            					}
		        				
	            				//alarm prekoračenje brzine
	            				if(objekat.getPrekoracenjeBrzine() != 0) {
		            				if(javljanjeTrenutno.getBrzina() > objekat.getPrekoracenjeBrzine() && !prekoracenje) {
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
	            				//System.out.println("geneko poruka zona " + inputLine);
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
			            		//System.out.println("geneko poruka obrada " );
	            				//Servis.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
	            				//System.out.println("geneko poruka kraj " );
		                        }
		                	}else {
		                		System.out.println("objekat null... " + niz[i]);
	                			//break;
		                	}
					}else{
						System.out.println("nije ispravan fox.... " + niz[i]);
	                	}
					}
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("thread geneko interrupted exiting... ");
                    break;
                    }
				}
			stop();
			} catch(SocketTimeoutException e){
				//System.out.println("geneko thread soket timeout " + e.getMessage());
				stop();
			} catch(SocketException e){
				System.out.println("geneko thread soket greška " + e.getMessage());
		    	stop();
			} catch (Throwable e) {
				String por = " geneko: ";
				if(objekat != null) {
					por += objekat.getOznaka();
				}
				System.out.println("geneko thread throwable greška " + e.getMessage() + por);
		    	stop();
			}
		}
	
	private void upisObracun(Javljanja javljanje, JavljanjaPoslednja javljanjePoslednje) {
		if(javljanjePoslednje != null) {
			if(javljanje.getDatumVreme().after(javljanjePoslednje.getDatumVreme())) {
				javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanje, javljanjePoslednje));
			}else {
				javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo());
			}
		}else {
			javljanje.setVirtualOdo(0.0f);
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
				//System.out.println("geneko stream connection closed ");
			}
		}catch(Throwable e){
			System.out.println("geneko stream connection closed problem...");
			}
		return;
		}
}
