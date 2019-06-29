package rs.cybertrade.prati.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;


public class GenekoServer implements Runnable{

	private final int listeningPort;
	private ServerSocket serverSocket;
	private final ExecutorService pool;
	private ArrayList<Socket> clientSockets;
	private boolean isStopped = false;
	private int poolSize;
	private int rb = 1;
	public GenekoProtokol protokol;
	
	public GenekoServer(int port, int poolSizeS) {
		clientSockets = new ArrayList<Socket>();
		listeningPort = port;
		poolSize = poolSizeS;
		pool = Executors.newFixedThreadPool(poolSize);
		protokol = new GenekoProtokol();
	}
	
	@Override
	public void run() {
	     LinkedBlockingQueue<Socket> queue = new LinkedBlockingQueue<>();
	     System.out.println("GENEKO");
	       try {
	    	   serverSocket = new ServerSocket(listeningPort);
	           while (!isStopped()) {
	        	   Socket soket = null;
	        	   try {
	        		   if(clientSockets.size() >= (poolSize - 2)){
	        			   clientSockets.get(0).getInputStream().close();
	        			   clientSockets.get(0).close();
	        			   removeClientSocket(clientSockets.get(0));
	        			   System.out.println("server geneko izbačen soket - " + clientSockets.size());
	        			   }
	        		   soket = serverSocket.accept();
       			   clientSockets.add(soket);
       			   pool.submit(new GenekoThread(queue, this));
       			   queue.put(soket); 
       			   if(rb == 1 || rb % 1000 == 0)
       				   System.out.println("GENEKO " + rb + " STARTOVAN" + " od " + ((ThreadPoolExecutor) pool).getActiveCount() + " " + getVreme());
       			   rb++;
	        		   } catch (Throwable e){
	        			   if (isStopped()) {
	        				   System.out.println("server geneko is stopped");
	        				   System.out.println(e.getMessage());
	        				   } else {
	        					   System.out.println("error accepting geneko client connection" + e.getMessage());
	        					   e.printStackTrace();
	        					   }
	        			   break;
	        			   }
	        	   }
	           try{
	        	   this.pool.shutdown();
	        	   for(Socket s: clientSockets){
	        		   try{
	        			   s.close();
	        			   }catch (Throwable e){
	        				   System.out.println("soket zatvoren: " + e.getMessage());
	        				   }
	        		   }
	        	   }catch(Throwable e){
	        		   System.out.println("shutdown: " + e.getMessage());
	        		   }
	           } catch (IOException ex) {
	        	   System.out.println("greška otvaranja geneko soketa: " + ex.getMessage());
	    	   }
	       }
	
	private synchronized boolean isStopped() {
		return this.isStopped;
	}
	
	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (Throwable e) {
			System.out.println("error stopping server" + e.getMessage());
		}
	}
	
	public synchronized void removeClientSocket(Socket clientSocket) {
		try {
			clientSockets.remove(clientSocket);
		} catch (Throwable e) {
			System.out.println("error removing geneko socket" + e.getMessage());
		}
	}
	
    public String getVreme() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

}