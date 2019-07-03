package rs.atekom.prati.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.bind.DatatypeConverter;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;
import pratiBaza.tabele.Obd;
import rs.atekom.prati.Broadcaster;

public class NyitechThread implements Runnable{

    private Socket socket = null;
    private LinkedBlockingQueue<Socket> socketQueue;
    private NyitechServer server;
	private InputStream input;
	private DataOutputStream out;
	private boolean isStopped = false;
	private NyiTechProtokol protokol;
	private NyiTechPar<Javljanja, Obd> par;
	private byte[] data;
	
	public NyitechThread(LinkedBlockingQueue<Socket> queue, NyitechServer serverNyitech) {
    	socketQueue = queue;
    	server = serverNyitech;
    	protokol = new NyiTechProtokol();
    	par = null;
    	data = new byte[1024];
	}

	@Override
	public void run() {
    	try{
            socket = socketQueue.take();
            input = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());
        	ArrayList<String> poruke = new ArrayList<String>();
        	String ulaz = "0";
        	int br = 0;
        	String priprema = "";
        	String priprema2 = "";
        	String crc = "";
        	String crcKonvertovan = "";
        	String odgovor = "";
        	String odgovor2 = "";
        	String eventData = "";
        	String eventCode = "";
        	String uredjaj = "";
        	String event = "";
			while(!isStopped() && !socket.isClosed()){
				socket.setSoTimeout(720000);
				br = input.read(data, 0, data.length);
				if (br <= 0) {
					break;
				}
				ulaz = DatatypeConverter.printHexBinary(data);
				//Logger.getLogger(getClass().getName()).log(Level.INFO,"NT-183:{0}", ulaz );		
				int start = 0;
				while(ulaz.substring(start, start + 4).equals("4040")){
					int duzina = Integer.parseInt(ulaz.substring(start + 6, start + 8) + ulaz.substring(start + 4, start + 6), 16)*2;
					String poruka = ulaz.substring(start, start + duzina);
					poruke.add(poruka);
					start = start + duzina;
					}	

				for(int i = 0; i < poruke.size(); i++){
					event = poruke.get(i).substring(34, 36) + poruke.get(i).substring(32, 34);
					switch(event){
					//priprema = početak 4040 + dužina 1700 + vrsta0940(4009) + random 0101 + dtc type stored = 00
					case "1001": 
						priprema = "4040" + "1700" + poruke.get(i).substring(8, 32) + "0940" + "010100";
						priprema2 = "4040" + "1700" + poruke.get(i).substring(8, 32) + "0940" + "010101";crc = String.valueOf(crc(priprema));
						crcKonvertovan = crc.substring(2, 4) + crc.substring(0, 2);
						odgovor = priprema + crcKonvertovan + "0D0A";
						odgovor2 = priprema2 + crcKonvertovan + "0D0A";
						out.write(hexStringToByteArray(odgovor));
						out.write(hexStringToByteArray(odgovor2));
						out.flush();
						//System.out.println("PRIJAVA");
						break;
						
					case "1003": 
						priprema = "4040" + "1600" + poruke.get(i).substring(8, 32) + "0390";
						crc = String.valueOf(crc(priprema));
						crcKonvertovan = crc.substring(2, 4) + crc.substring(0, 2);
						odgovor = priprema + crcKonvertovan + "0D0A";
						out.write(hexStringToByteArray(odgovor));
						out.flush();
						//System.out.println("VEZA: " + convertHexToString(poruke.get(i).substring(8, 32)));
						break;
				             
					case "2001": 
						eventData = poruke.get(i).substring(36);
						eventCode = poruke.get(i).substring(34, 36) + poruke.get(i).substring(32, 34);
						uredjaj = convertHexToString(poruke.get(i).substring(8, 32));
						par = protokol.nyiTechObrada(/*this.context,**/uredjaj, eventCode, eventData);
						if(par != null){
							if(par.javljanje != null){
								JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(par.javljanje.getObjekti());
								upisObracun(par.javljanje, poslednje);
								}
							if(par.obd != null)
								Servis.obdServis.unesiObd(par.obd);
							}
						break;
				            
					case "2002": 
						eventData = poruke.get(i).substring(36);
						eventCode = poruke.get(i).substring(34, 36) + poruke.get(i).substring(32, 34);
						uredjaj = convertHexToString(poruke.get(i).substring(8, 32));
						par = protokol.nyiTechObrada(uredjaj, eventCode, eventData);
						if(par != null){
							if(par.javljanje != null){
								JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(par.javljanje.getObjekti());
								upisObracun(par.javljanje, poslednje);
								}
							if(par.obd != null)
								Servis.obdServis.unesiObd(par.obd);
							}
						break;
	                         
					case "2003": 
						priprema = "4040" + "1800" + poruke.get(i).substring(8, 32) + "03A0" + poruke.get(i).substring(36, 40);
						crc = String.valueOf(crc(priprema));
						crcKonvertovan = "0000";
						try{
							crcKonvertovan = crc.substring(2, 4) + crc.substring(0, 2);
							}catch(Exception e){
								System.out.println("CRC problem " + crc);
								}
						odgovor = priprema + crcKonvertovan + "0D0A";
						out.write(hexStringToByteArray(odgovor));
						out.flush();;
						eventData = poruke.get(i).substring(36);
						eventCode = poruke.get(i).substring(34, 36) + poruke.get(i).substring(32, 34);
						uredjaj = convertHexToString(poruke.get(i).substring(8, 32));
						par = protokol.nyiTechObrada(uredjaj, eventCode, eventData);
						if(par != null){
							if(par.javljanje != null){
								JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(par.javljanje.getObjekti());
								upisObracun(par.javljanje, poslednje);
								//ukoliko je alarm kontkt aktiviran šaljem komandu za proveru DTC grešaka
								if(par.javljanje.getSistemAlarmi().getSifra().equals("1092")){
									priprema = "4040" + "1700" + poruke.get(i).substring(8, 32) + "0940" + "010101";
									crc = String.valueOf(crc(priprema));
									crcKonvertovan = crc.substring(2, 4) + crc.substring(0, 2);
									odgovor = priprema + crcKonvertovan + "0D0A";
									out.write(hexStringToByteArray(odgovor));
									out.flush();
									}
								}
							}
						break;
                             
					case "2004": 
						eventData = poruke.get(i).substring(36);
						eventCode = poruke.get(i).substring(34, 36) + poruke.get(i).substring(32, 34);
						uredjaj = convertHexToString(poruke.get(i).substring(8, 32));
						par = protokol.nyiTechObrada(uredjaj, eventCode, eventData);
						if(par != null){
							if(par.javljanje != null){
								JavljanjaPoslednja poslednje = Servis.javljanjePoslednjeServis.nadjiJavljanjaPoslednjaPoObjektu(par.javljanje.getObjekti());
								upisObracun(par.javljanje, poslednje);
								}
							}
						break;
						
					default: break;
					}
					}
				poruke.clear();
				if (Thread.currentThread().isInterrupted()) {
                    System.out.println("thread nyitech interrupted exiting...");
                    break;
                    }
				}
	    	stop();
			} catch(SocketTimeoutException e){
				//System.out.println("neon thread soket timeout " + e.getMessage());
				stop();
			} catch(SocketException e){
				System.out.println("nyitech thread soket greška " + e.getMessage());
				stop();
			} catch (Throwable e) {
				System.out.println("nyitech thread throwable greška " + e.getMessage());
				stop();
			}
    	}
	public synchronized boolean isStopped(){
		return this.isStopped;
	}
	
	public synchronized void stop(){
		this.isStopped = true;
    	try{
			if(!socket.isClosed()){
				input.close();
				out.close();
				socket.close();
				server.removeClientSocket(socket);
				//System.out.println("nyitech stream connection closed ");
			}
		}catch(IOException e){
			System.out.println("nyitech stream connection closed problem...");
		}
		return;
	}
	
	private void upisObracun(Javljanja javljanje, JavljanjaPoslednja javljanjePoslednje) {
		javljanje.setVirtualOdo(javljanjePoslednje.getVirtualOdo() + (float)Servis.obracun.rastojanje(javljanje, javljanjePoslednje));
	    Servis.javljanjeServis.unesiJavljanja(javljanje);
	    Broadcaster.broadcast(javljanje);
	}
    
    final int[] crc_table = {
    		0x0000,0x9705,0x2E01,0xB904,0x5C02,0xCB07,0x7203,0xE506,0xB804,0x2F01,0x9605,0x0100,0xE406,0x7303,0xCA07,0x5D02,0x7003,0xE706,
    		0x5E02,0xC907,0x2C01,0xBB04,0x0200,0x9505,0xC807,0x5F02,0xE606,0x7103,0x9405,0x0300,0xBA04,0x2D01,0xE006,0x7703,0xCE07,0x5902,
    		0xBC04,0x2B01,0x9205,0x0500,0x5802,0xCF07,0x7603,0xE106,0x0400,0x9305,0x2A01,0xBD04,0x9005,0x0700,0xBE04,0x2901,0xCC07,0x5B02,
    		0xE206,0x7503,0x2801,0xBF04,0x0600,0x9105,0x7403,0xE306,0x5A02,0xCD07,0xC007,0x5702,0xEE06,0x7903,0x9C05,0x0B00,0xB204,0x2501,
    		0x7803,0xEF06,0x5602,0xC107,0x2401,0xB304,0x0A00,0x9D05,0xB004,0x2701,0x9E05,0x0900,0xEC06,0x7B03,0xC207,0x5502,0x0800,0x9F05,
    		0x2601,0xB104,0x5402,0xC307,0x7A03,0xED06,0x2001,0xB704,0x0E00,0x9905,0x7C03,0xEB06,0x5202,0xC507,0x9805,0x0F00,0xB604,0x2101,
    		0xC407,0x5302,0xEA06,0x7D03,0x5002,0xC707,0x7E03,0xE906,0x0C00,0x9B05,0x2201,0xB504,0xE806,0x7F03,0xC607,0x5102,0xB404,0x2301,
    		0x9A05,0x0D00,0x8005,0x1700,0xAE04,0x3901,0xDC07,0x4B02,0xF206,0x6503,0x3801,0xAF04,0x1600,0x8105,0x6403,0xF306,0x4A02,0xDD07,
    		0xF006,0x6703,0xDE07,0x4902,0xAC04,0x3B01,0x8205,0x1500,0x4802,0xDF07,0x6603,0xF106,0x1400,0x8305,0x3A01,0xAD04,0x6003,0xF706,
    		0x4E02,0xD907,0x3C01,0xAB04,0x1200,0x8505,0xD807,0x4F02,0xF606,0x6103,0x8405,0x1300,0xAA04,0x3D01,0x1000,0x8705,0x3E01,0xA904,
    		0x4C02,0xDB07,0x6203,0xF506,0xA804,0x3F01,0x8605,0x1100,0xF406,0x6303,0xDA07,0x4D02,0x4002,0xD707,0x6E03,0xF906,0x1C00,0x8B05,
    		0x3201,0xA504,0xF806,0x6F03,0xD607,0x4102,0xA404,0x3301,0x8A05,0x1D00,0x3001,0xA704,0x1E00,0x8905,0x6C03,0xFB06,0x4202,0xD507,
    		0x8805,0x1F00,0xA604,0x3101,0xD407,0x4302,0xFA06,0x6D03,0xA004,0x3701,0x8E05,0x1900,0xFC06,0x6B03,0xD207,0x4502,0x1800,0x8F05,
    		0x3601,0xA104,0x4402,0xD307,0x6A03,0xFD06,0xD007,0x4702,0xFE06,0x6903,0x8C05,0x1B00,0xA204,0x3501,0x6803,0xFF06,0x4602,0xD107,
    		0x3401,0xA304,0x1A00,0x8D05
    		};
    final int start_crc = 0xFFFF;
    
    public int crc(String niz){//unsigned short crc16(unsigned short crc,unsigned char *buffer, unsigned int size)
    byte[] bytes = niz.getBytes();
    int crc = 0x0000;
    for (byte b : bytes) {
        crc = (crc >>> 8) ^ crc_table[(crc ^ b) & 0xff];
    }
    return crc ;
    }
	
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] =  (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	
    public String convertHexToString(String hex){
    	StringBuilder sb = new StringBuilder();
    	StringBuilder temp = new StringBuilder();
    	//49204c6f7665204a617661 split into two characters 49, 20, 4c...
    	for( int i=0; i<hex.length()-1; i+=2 ){
    		//grab the hex in pairs
    		String output = hex.substring(i, (i + 2));
    		//convert hex to decimal
    		int decimal = Integer.parseInt(output, 16);
    		//convert the decimal to character
    		sb.append((char)decimal);
    		temp.append(decimal);
    		}
    	//System.out.println("Decimal : " + temp.toString());
    	return sb.toString();
    	}
	 
    public long razlika(Date vreme){
    	return System.currentTimeMillis() - vreme.getTime();
    	}
	
}
