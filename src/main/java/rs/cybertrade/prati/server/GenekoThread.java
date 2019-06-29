package rs.cybertrade.prati.server;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import rs.cybertrade.prati.Broadcaster;

public class GenekoThread implements Runnable{

	private Socket socket = null;
	private LinkedBlockingQueue<Socket> socketQueue;
	private GenekoServer server;
	private InputStream input;
	private boolean isStopped = false;
	
	public GenekoThread(LinkedBlockingQueue<Socket> queue, GenekoServer serverGeneko) {
		socketQueue = queue;
		server = serverGeneko;
	}
	
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			int br = 0;
			Javljanja javljanje = null; 
			byte[] data = new byte[8192];
			String [] da;
			String [] niz;
			int vreme = 0;
			Javljanja stop = null;
			String inputLine = "";
			Uredjaji uredjaj = null;
			Objekti objekat = null;
			String kodUredjaja = null;
			boolean zaustavljeno = false;
			int prolaz = 0;
			while(!isStopped() && !socket.isClosed()){
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if (br <= 0) {
					break;
				}
				inputLine = new String(data, 0, br);
				
				//System.out.println("poruka " + inputLine);

				niz = inputLine.split("</fox>");
				for(int i = 0; i < niz.length; i++){
					if(niz[i].startsWith("<fox>")) {
						da = niz[i].split("\"");
	                	while(prolaz < 1){
		                	kodUredjaja = da[1];
		                	if(kodUredjaja != null){
		                		uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(kodUredjaja);
		                		if(uredjaj != null){
			                		objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);
			                		if(objekat != null){
			                			vreme = objekat.getVremeStajanja();
				                		}else{
				                			System.out.println("objekat null... prvi put" + da[1]);
				                			//break;
				                			}
			                		}
		                	}else{
		                		uredjaj = null;
		                	}
		                	prolaz++;
		                	break;
	                	}
		                if(objekat != null){
		                	javljanje = server.protokol.genekoObrada(da[3], objekat, zaustavljeno, vreme, stop);
		                	if(javljanje != null){
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
		                        JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(javljanje.getObjekti());
								upisObracun(javljanje, poslednje);
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
				System.out.println("geneko thread throwable greška " + e.getMessage());
		    	stop();
			}
		}
	
	private void upisObracun(Javljanja javljanje, JavljanjaPoslednja javljanjePoslednje) {
		double dist = 0;
		if(javljanje.getDatumVreme().after(javljanjePoslednje.getDatumVreme())) {
			if(javljanje.isValid() && javljanje.getBrzina() < 200) {
			    double earthRadius = 6371; //meters * 1000
			    double dLat = Math.toRadians(javljanje.getLat() - javljanjePoslednje.getLat());
			    double dLng = Math.toRadians(javljanje.getLon() - javljanjePoslednje.getLon());
			    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			               Math.cos(Math.toRadians(javljanjePoslednje.getLat())) * Math.cos(Math.toRadians(javljanje.getLon())) *
			               Math.sin(dLng/2) * Math.sin(dLng/2);
			    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			    if(earthRadius * c > 5) {
			    	dist = (earthRadius * c);
			    }
			}
		    
		    javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo() + (float)dist);
		}
	    Servis.javljanjeServis.unesiJavljanja(javljanje);
	    Broadcaster.broadcast(javljanje);
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
				//System.out.println("geneko stream connection closed ");
			}
		}catch(Throwable e){
			System.out.println("geneko stream connection closed problem...");
			}
		return;
		}
}
