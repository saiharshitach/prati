package rs.atekom.prati.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import pratiBaza.tabele.AlarmiKorisnik;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.ObjekatZone;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import pratiBaza.tabele.Zone;

public class OpstiThread implements Runnable{

	public Socket socket = null;
	public LinkedBlockingQueue<Socket> socketQueue;
	public InputStream input;
    public OutputStream out;
    public boolean isStopped = false;
    public boolean prekoracenje = false;
    public byte[] data;
    public byte[] odg = {(byte)0x00, (byte)0x02, (byte)0x64, (byte)0x01, (byte)0x13, (byte)0xbc};
    public int offset;
    public OpstiServer server;
    public ArrayList<ObjekatZone> objekatZone;
    public ArrayList<AlarmiKorisnik> alarmiKorisnici;
    public String testDate;
    public DateFormat formatter;
    public Date date;
    public JavljanjeObd javljanjeObd;
    public Obd obdStop;
    public Javljanja javljanjeTrenutno, javljanjePoslednje, javljanjeStop;
    public Objekti objekat;
    public Uredjaji uredjaj;
    public String ulaz;
    public boolean zaustavljeno;
    public boolean gorivo;
    public String kodUredjaja;
    public int brojIspodNivoa;//koliko puta je nivo goriva manji za više od 1%
    public Date pocetak;
    public String test;
    public static int vreme = 600*1000;
	
	public OpstiThread(LinkedBlockingQueue<Socket> queue, OpstiServer srv) {
		socketQueue = queue;
		server = srv;
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
		zaustavljeno = false;
		gorivo = false;
		brojIspodNivoa = 0;
		test = "test 0";
	}

	@Override
	public void run() {
		
	}
	
	public void pronadjiPostavi(String kodUredjaja) {
		
		if(kodUredjaja != null && !kodUredjaja.isEmpty() && !kodUredjaja.equals("")) {
			uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(kodUredjaja);
			
			if(uredjaj != null) {
				//objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);						
				objekat = uredjaj.getObjekti();
				
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
						obdStop = Servis.obdServis.nadjiObdPoslednji(objekat, null);
						}else {
							javljanjeStop = null;
            				obdStop = null;
            				}
					}else {
						System.out.println("uređaj nema objekta: " + kodUredjaja);
						}
				
				}else {
					System.out.println("nema uredjaja: " + kodUredjaja);
					}
			}
		//System.out.println("broj uzimanja objekta: " + broj);
	}
	
	public void obradaJavljanja(Javljanja javljanjeTrenutno, Obd obdTrenutni) {
		test = "ulaz";
		JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(objekat);
		boolean mladje = true;
		if(poslednje != null && javljanjeTrenutno != null) {
			mladje = javljanjeTrenutno.getDatumVreme().after(poslednje.getDatumVreme());
		}
		test = " 1 ";
		if(javljanjeTrenutno != null  && javljanjeTrenutno.getBrzina() < 200 &&  javljanjeTrenutno.getDatumVreme().after(date)) {
			test = " obračun ";
			//obracun km
			if(poslednje != null) {
				if(mladje) {
					javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanjeTrenutno, poslednje));
				}else {
					javljanjeTrenutno.setVirtualOdo(poslednje.getVirtualOdo());
				}
			}else {
				javljanjeTrenutno.setVirtualOdo(0.0f);
			}
			
			//za stop
			test = " za stop ";
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
			test = " stajanje ";
			if(javljanjeStop != null && !zaustavljeno && mladje) {
				long vreme = (javljanjeTrenutno.getDatumVreme().getTime() - javljanjeStop.getDatumVreme().getTime());
				if(objekat.getVremeStajanja() > 0 && (vreme > (objekat.getVremeStajanja() * 60 * 1000))) {
					//System.out.println("vreme " + vreme);
					if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
						server.postaviAlarmStajanje(javljanjeTrenutno);
						zaustavljeno = true;
						}else {
							server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
							server.postaviAlarmStajanje(javljanjeTrenutno);
							zaustavljeno = true;
						}
					}
				}
			
			//alarm prekoračenje brzine
			test = " prekoračenje ";
			if(objekat.getPrekoracenjeBrzine() != 0 && mladje) {
				if((javljanjeTrenutno.getBrzina() > objekat.getPrekoracenjeBrzine()) && !prekoracenje) {
					prekoracenje = true;
					if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
						server.postaviAlarmPrekoracenjeBrzine(javljanjeTrenutno);
					}else {
						server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
						server.postaviAlarmPrekoracenjeBrzine(javljanjeTrenutno);
					}
					if(javljanjeTrenutno.getEventData().equals("0")) {
						javljanjeTrenutno.setEventData(javljanjeTrenutno.getBrzina() + "км/ч");
					}else {
						String eventData = javljanjeTrenutno.getBrzina() + "км/ч, " + javljanjeTrenutno.getEventData();
						javljanjeTrenutno.setEventData(eventData);
					}
				}else {
					prekoracenje = false;
				}
			}
			
    		//alarm gorivo
			test = " gorivo ";
    		if(obdTrenutni != null && mladje && javljanjeTrenutno.getBrzina() < 6) {
    			//System.out.println(test);
    			Javljanja poslednjeSaBrzinom = Servis.javljanjeServis.vratiJavljanjeZaStajanje(objekat);
    			if(!gorivo && poslednjeSaBrzinom != null) {
    				//System.out.println(test += " false");
        			ArrayList<Obd> poslednjiObdUMirovanju = Servis.obdServis.nadjiObdPoslednjaStajanja(objekat, new Timestamp(poslednjeSaBrzinom.getDatumVreme().getTime()));
        			//System.out.println(test + " brzina " + poslednjeSaBrzinom.getBrzina() + " " + poslednjeSaBrzinom.getDatumVreme() + " komada " + poslednjiObdUMirovanju.size());
        			/*System.out.println("početni " + poslednjiObdUMirovanju.get(0).getNivoGoriva() + " krajnji " + obdTrenutni.getNivoGoriva() + " razlika " 
        			+ (poslednjiObdUMirovanju.get(0).getNivoGoriva() - obdTrenutni.getNivoGoriva()));**/
        			if(!gorivo && poslednjiObdUMirovanju.size() > 0 && (poslednjiObdUMirovanju.get(0).getNivoGoriva() - obdTrenutni.getNivoGoriva() > 3)) {
        				//System.out.println(test  + " razlika > 2");
        				if(!javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
        					server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
        					}
        				server.postaviAlarmIstakanje(javljanjeTrenutno);
						gorivo = true;
        			}
        			/*if(obdStop != null) {
        				if(!gorivo) {
        					if(obdTrenutni.getNivoGoriva() - obdStop.getNivoGoriva() > 1 && brojIspodNivoa > 10) {
        						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
        							server.postaviAlarmIstakanje(javljanjeTrenutno);
        							gorivo = true;
        							}else {
        								server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
        								server.postaviAlarmIstakanje(javljanjeTrenutno);
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
        				}**/
        			Servis.obdServis.unesiObd(obdTrenutni);
        			}
    			}else {
    				gorivo = false;
    				}
    		
    		//alarm zona
    		test = " zona ";
    		if(mladje && objekatZone != null && objekatZone.size() > 0) {
    			Zone zonaPoslednja = null;
    			if(poslednje != null) {
    				zonaPoslednja = poslednje.getZona();
    			}
				//ulazak
    			test = " zona ulaz ";
				if(zonaPoslednja == null) {
					for(ObjekatZone objekatZona : objekatZone) {
						if(objekatZona.isAktivan() && objekatZona.isIzlaz()) {
        					if(Servis.obracun.rastojanjeKoordinate(javljanjeTrenutno, objekatZona.getZone().getLat(), objekatZona.getZone().getLon()) 
        							<= objekatZona.getZone().getPrecnik()) {
        						javljanjeTrenutno.setZona(objekatZona.getZone());
        						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
        							server.postaviAlarmUlazakUZonu(javljanjeTrenutno);
        							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
        							break;
        						}else {
        							server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
        							server.postaviAlarmUlazakUZonu(javljanjeTrenutno);
        							javljanjeTrenutno.setEventData(objekatZona.getZone().getNaziv());
        							break;
        						}
        					}
						}
					}
				}else {
					//izlazak
					test = " zona izlaz ";
					javljanjeTrenutno.setZona(zonaPoslednja);
					ObjekatZone objZona = Servis.zonaObjekatServis.nadjiObjekatZonuPoZoniObjektu(objekat, zonaPoslednja);
					if(objZona != null && objZona.isAktivan() && objZona.isIzlaz()) {
    					if(Servis.obracun.rastojanjeKoordinate(javljanjeTrenutno, zonaPoslednja.getLat(), zonaPoslednja.getLon()) > zonaPoslednja.getPrecnik()) {
    						if(javljanjeTrenutno.getSistemAlarmi().getSifra().equals("0")) {
    							server.postaviAlarmIzlazakIzZone(javljanjeTrenutno);
    							javljanjeTrenutno.setEventData(zonaPoslednja.getNaziv());
    						}else {
    							server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
    							server.postaviAlarmIzlazakIzZone(javljanjeTrenutno);
    							javljanjeTrenutno.setEventData(zonaPoslednja.getNaziv());
    						}
    						javljanjeTrenutno.setZona(null);
    					}/*else {
    						javljanjeTrenutno.setZona(zonaPoslednja);
    					}**/
					}/*else {
						javljanjeTrenutno.setZona(zonaPoslednja);
					}**/
				}
    		}
    		test = " izvrsavanje ";
    		server.izvrsavanje.obradaAlarma(javljanjeTrenutno, alarmiKorisnici);
    		}else {
    			System.out.println("javljanje null: " + ulaz);
    		}
	}
	
	
	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
    	try{
			if(socket != null && !socket.isClosed()){
	    		if(input != null)
	    			input.close();
				if(out != null) {
		    		out.flush();
					out.close();
				}
				socket.close();
				isStopped = true;
				//server.removeClientSocket(socket);
				//System.out.println("coban stream connection closed ");
			}
		}catch(IOException e){
			System.out.println("ruptela stream connection closed problem...");
		}
		return;
	}
}
