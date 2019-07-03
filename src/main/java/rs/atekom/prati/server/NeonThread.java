package rs.atekom.prati.server;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;

import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import rs.atekom.prati.Broadcaster;

public class NeonThread implements Runnable{

	private Socket socket;
	private LinkedBlockingQueue<Socket> socketQueue;
	private NeonServer server;
	private InputStream input;
	private boolean isStopped = false;
	
	public NeonThread(LinkedBlockingQueue<Socket> queue, NeonServer serverNeon) {
    	socketQueue = queue;
    	server = serverNeon;
	}
	
	@Override
	public void run() {
		try {
			socket = socketQueue.take();
			input = socket.getInputStream();
			Javljanja stop = null;
	    	boolean zaustavljeno = false;
	    	int vreme = 0;
	    	String[] da;
	    	String [] niz;
	    	byte[] data = new byte[1024];
	    	String inputLine = "";
			Javljanja javljanje = null; 
			Objekti objekat = null;
			Uredjaji uredjaj = null;
			String kodUredjaja = null;
			int prolaz = 0;
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
						while(prolaz < 1){
			        		kodUredjaja = da[2];
			        		if(kodUredjaja != null){
			        			uredjaj = Servis.uredjajServis.nadjiUredjajPoKodu(kodUredjaja);
			        			if(uredjaj != null){
					        		objekat = Servis.objekatServis.nadjiObjekatPoUredjaju(uredjaj);
					        		if(objekat != null){
					        			vreme = objekat.getVremeStajanja();
						        		}
					        		}else{
					        			System.out.println("objekat null... " + inputLine);
					        			//break;
					        			}
				        		}else{
				        			uredjaj = null;
				        			}
							prolaz++;
							break;
							}
						if(objekat != null){
							javljanje = server.protokol.neonObrada(da, inputLine, objekat, zaustavljeno, vreme, stop);
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
	
	private void upisObracun(Javljanja javljanje, JavljanjaPoslednja javljanjePoslednje) {
		javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanje, javljanjePoslednje));
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
				//System.out.println("neon stream connection closed ");
			}
		}catch(Throwable e){
			System.out.println("neon stream connection closed problem...");
			}
		return;
		}
}
