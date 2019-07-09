package rs.atekom.prati.server;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.ObjekatZone;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import pratiBaza.tabele.Zone;

public class NeonThread implements Runnable{

	private Socket socket;
	private LinkedBlockingQueue<Socket> socketQueue;
	private NeonServer server;
	private InputStream input;
	private byte[] data;
	private boolean isStopped = false;
	private ArrayList<ObjekatZone> objekatZone;
	private ArrayList<AlarmiKorisnik> alarmiKorisnici;
	private String testDate;
	private DateFormat formatter;
	private Date date;
	
	public NeonThread(LinkedBlockingQueue<Socket> queue, NeonServer serverNeon) {
    	socketQueue = queue;
    	server = serverNeon;
    	data = new byte[1024];
		testDate = "01/07/2019 00:00:00";
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			date = formatter.parse(testDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			boolean prekoracenje = false;
			Uredjaji uredjaj = null;
			String kodUredjaja;
			Objekti objekat = null;
			Javljanja javljanje = null; 
			Javljanja stop = null;
	    	boolean zaustavljeno = false;
	    	int vreme = 0;
	    	String[] da;
	    	String [] niz;
	    	
	    	String inputLine = "";
			int br = 0;
			
			while (!isStopped() && !socket.isClosed()) {
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if(br <= 0){
					break;
				}
				
		    	inputLine = new String(data, 0, br);
		    	
		    	//System.out.println("poruka " + inputLine);
		    	niz = inputLine.split(">");
		    	for(int i = 0; i < niz.length; i++){
					if(niz[i].startsWith("<oris")) {
						da = niz[i].split(",");
						if(uredjaj == null){
							kodUredjaja = da[2];
			        		if(kodUredjaja != null){
			        			uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(kodUredjaja);
			        			if(uredjaj != null){
					        		objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);
					        		if(objekat != null){
					        			vreme = objekat.getVremeStajanja();
					        			objekatZone = Servis.zonaObjekatServis.nadjiZoneObjektePoObjektu(objekat);
										alarmiKorisnici = Servis.alarmKorisnikServis.nadjiSveAlarmeKorisnikePoObjektu(objekat);
						        		}
					        		}else{
					        			System.out.println("objekat null... " + inputLine);
					        			//break;
					        			}
				        		}else{
				        			uredjaj = null;
				        			}
							break;
							}
						if(objekat != null ){
							javljanje = server.protokol.neonObrada(da, inputLine, objekat, zaustavljeno, vreme, stop);
		        			if(javljanje != null && javljanje.getBrzina() < 200 && javljanje.getDatumVreme().after(date)){
		        				Javljanja javljanjePoslednje = Servis.javljanjeServis.nadjiPoslednjeJavljanjePoObjektu(objekat);
		        				if(javljanjePoslednje != null) {
		        					if(javljanje.getDatumVreme().after(javljanjePoslednje.getDatumVreme())) {
		        						javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanje, javljanjePoslednje));
		        					}else {
		        						javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo());
		        					}
		        				}else {
		        					javljanje.setVirtualOdo(0.0f);
		        				}
		        				
		        				if(javljanje.getSistemAlarmi().getSifra().equals("1095")){
		        					zaustavljeno = true;
		        					}
		        				if(javljanje.getBrzina() > 5){
		        					stop = null;
		        					}else{
		        						if(stop == null){
		        							stop = javljanje;
		        							}
		        						}

		            			//alarm stajanje
	            				if(stop != null) {
	            					long vremeRazlika = javljanje.getDatumVreme().getTime() - stop.getDatumVreme().getTime();
	            					if(!zaustavljeno) {
	            						if(objekat.getVremeStajanja() != 0 && vremeRazlika / 1000 > objekat.getVremeStajanja() * 60) {
	            							if(javljanje.getSistemAlarmi().getSifra().equals("0")) {
	            								javljanje.setSistemAlarmi(server.stajanje);
	            								zaustavljeno = true;
	            								}else {
	            									Servis.izvrsavanje.obradaAlarma(javljanje, alarmiKorisnici);
	            									javljanje.setSistemAlarmi(server.stajanje);
	            									zaustavljeno = true;
	            								}
	            							}
	            						}
	            					}
		        				
	            				//alarm prekoračenje brzine
	            				if(objekat.getPrekoracenjeBrzine() != 0) {
		            				if(javljanje.getBrzina() > objekat.getPrekoracenjeBrzine() && !prekoracenje) {
		            					prekoracenje = true;
		            					if(javljanje.getSistemAlarmi().getSifra().equals("0")) {
		            						javljanje.setSistemAlarmi(server.prekoracenjeBrzine);
		            					}else {
		            						Servis.izvrsavanje.obradaAlarma(javljanje, alarmiKorisnici);
		            						javljanje.setSistemAlarmi(server.prekoracenjeBrzine);
		            					}
	            						if(javljanje.getEventData().equals("0")) {
	            							javljanje.setEventData(javljanje.getBrzina() + "км/ч");
	            						}else {
	            							javljanje.setEventData(javljanje.getBrzina() + "км/ч, " + javljanje.getEventData());
	            						}
		            				}else {
		            					prekoracenje = false;
		            				}
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
		            						if(objekatZona.isAktivan() && objekatZona.isUlaz()) {
				            					if(Servis.obracun.rastojanjeKoordinate(javljanje, objekatZona.getZone().getLat(), objekatZona.getZone().getLon()) <= objekatZona.getZone().getPrecnik()) {
				            						javljanje.setZona(objekatZona.getZone());
				            						if(javljanje.getSistemAlarmi().getSifra().equals("0")) {
				            							javljanje.setSistemAlarmi(server.ulazak);
				            							javljanje.setEventData(objekatZona.getZone().getNaziv());
				            							break;
				            						}else {
				            							Servis.izvrsavanje.obradaAlarma(javljanje, alarmiKorisnici);
				            							javljanje.setSistemAlarmi(server.ulazak);
				            							javljanje.setEventData(objekatZona.getZone().getNaziv());
				            							break;
				            						}
				            					}
		            						}
		            					}
		            				}else {
		            					//izlazak
		            					ObjekatZone objZona = Servis.zonaObjekatServis.nadjiObjekatZonuPoZoniObjektu(objekat, zonaPoslednja);
		            					if(objZona != null && objZona.isAktivan() && objZona.isIzlaz()) {
			            					if(Servis.obracun.rastojanjeKoordinate(javljanje, zonaPoslednja.getLat(), zonaPoslednja.getLon()) > zonaPoslednja.getPrecnik()) {
			            						if(javljanje.getSistemAlarmi().getSifra().equals("0")) {
			            							javljanje.setSistemAlarmi(server.izlazak);
			            							javljanje.setEventData(zonaPoslednja.getNaziv());
			            						}else {
			            							Servis.izvrsavanje.obradaAlarma(javljanje, alarmiKorisnici);
			            							javljanje.setSistemAlarmi(server.izlazak);
			            							javljanje.setEventData(zonaPoslednja.getNaziv());
			            						}
			            						javljanje.setZona(null);
			            					}else {
			            						javljanje.setZona(zonaPoslednja);
			            					}
		            					}else {
		            						javljanje.setZona(zonaPoslednja);
		            					}
		            				}
			            		}
	            				Servis.izvrsavanje.obradaAlarma(javljanje, alarmiKorisnici);
		        				}else{
		        					System.out.println("javljanje null: " + inputLine);
		        					//break;
		        					}
		        			//ako je javljanje null - greška iz protokola??
		        			}else {
		        				System.out.println("uredjaj null... " + inputLine);
		        				//break;
		        				}
		        		//ako je objekat null
			        	//ako je inputline prazno
			        	}else {
			        		if(!niz[i].equals("#"))
			        			System.out.println("nije oris... " + niz[i]);
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
				System.out.println("neon thread throwable greška " + e.getMessage());
				stop();
			}
		}
	
	private void upisObracun(Javljanja javljanje) {

	}

	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
		try{
			if(!socket.isClosed()){
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
