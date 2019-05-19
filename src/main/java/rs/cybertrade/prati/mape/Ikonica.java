package rs.cybertrade.prati.mape;

import java.io.Serializable;
import java.util.HashMap;
import pratiBaza.tabele.JavljanjaPoslednja;

public class Ikonica implements Serializable{

	private static final long serialVersionUID = 1L;
	//za razvoj ide sa /
	private final String c = "";
	//private final String pokret = c + "VAADIN/Play.png";
	private final String pause = c + "VAADIN/markoIkonice/pauza.png";//"VAADIN/Pause.png";
	private final String sleep = c + "VAADIN/Sleep.png";
	private final String stop = c + "VAADIN/markoIkonice/stop.png";//"VAADIN/Stop.png";
	private final String alarm = c + "VAADIN/Alarm48.png";
	private final String putanja = c + "VAADIN/markoIkonice/";
	private HashMap<Integer, String> ikonice;
	
	public Ikonica(){
		ikonice = vratiKomplet();
	}
	
	public String icon(JavljanjaPoslednja javljanjaPoslednja){
		String ikonica = "";
		Float ugao = javljanjaPoslednja.getPravac();
		if(Integer.parseInt(javljanjaPoslednja.getSistemAlarmi().getSifra()) == 1071){
			ikonica = alarm;
		}else if(javljanjaPoslednja.isKontakt() && javljanjaPoslednja.getBrzina()> 5.00){
			ikonica = putanja + ikonice.get(Math.round(ugao/5));
		}else if(javljanjaPoslednja.isKontakt() && javljanjaPoslednja.getBrzina()<= 5.00){
			ikonica = pause;
		}else if(!javljanjaPoslednja.isKontakt() && !(Integer.parseInt(javljanjaPoslednja.getSistemAlarmi().getSifra()) == 3002)){
			ikonica = stop;
		}else if(Integer.parseInt(javljanjaPoslednja.getSistemAlarmi().getSifra()) == 3002){
			ikonica = sleep;
		}
		return ikonica;
	}
	
	private HashMap<Integer, String> vratiKomplet(){
		HashMap<Integer, String> listaIkonica = new HashMap<Integer, String>();
		listaIkonica.put(0, "pravac_000.png");
		listaIkonica.put(1, "pravac_005.png");
		listaIkonica.put(2, "pravac_010.png");
		listaIkonica.put(3, "pravac_015.png");
		listaIkonica.put(4, "pravac_020.png");
		listaIkonica.put(5, "pravac_025.png");
		listaIkonica.put(6, "pravac_030.png");
		listaIkonica.put(7, "pravac_035.png");
		listaIkonica.put(8, "pravac_040.png");
		listaIkonica.put(9, "pravac_045.png");
		listaIkonica.put(10, "pravac_050.png");
		listaIkonica.put(11, "pravac_055.png");
		listaIkonica.put(12, "pravac_060.png");
		listaIkonica.put(13, "pravac_065.png");
		listaIkonica.put(14, "pravac_070.png");
		listaIkonica.put(15, "pravac_075.png");
		listaIkonica.put(16, "pravac_080.png");
		listaIkonica.put(17, "pravac_085.png");
		listaIkonica.put(18, "pravac_090.png");
		listaIkonica.put(19, "pravac_095.png");
		listaIkonica.put(20, "pravac_100.png");
		listaIkonica.put(21, "pravac_105.png");
		listaIkonica.put(22, "pravac_110.png");
		listaIkonica.put(23, "pravac_115.png");
		listaIkonica.put(24, "pravac_120.png");
		listaIkonica.put(25, "pravac_125.png");
		listaIkonica.put(26, "pravac_130.png");
		listaIkonica.put(27, "pravac_135.png");
		listaIkonica.put(28, "pravac_140.png");
		listaIkonica.put(29, "pravac_145.png");
		listaIkonica.put(30, "pravac_150.png");
		listaIkonica.put(31, "pravac_155.png");
		listaIkonica.put(32, "pravac_160.png");
		listaIkonica.put(33, "pravac_165.png");
		listaIkonica.put(34, "pravac_170.png");
		listaIkonica.put(35, "pravac_175.png");
		listaIkonica.put(36, "pravac_180.png");
		listaIkonica.put(37, "pravac_185.png");
		listaIkonica.put(38, "pravac_190.png");
		listaIkonica.put(39, "pravac_195.png");
		listaIkonica.put(40, "pravac_200.png");
		listaIkonica.put(41, "pravac_205.png");
		listaIkonica.put(42, "pravac_210.png");
		listaIkonica.put(43, "pravac_215.png");
		listaIkonica.put(44, "pravac_220.png");
		listaIkonica.put(45, "pravac_225.png");
		listaIkonica.put(46, "pravac_230.png");
		listaIkonica.put(47, "pravac_235.png");
		listaIkonica.put(48, "pravac_240.png");
		listaIkonica.put(49, "pravac_245.png");
		listaIkonica.put(50, "pravac_250.png");
		listaIkonica.put(51, "pravac_255.png");
		listaIkonica.put(52, "pravac_260.png");
		listaIkonica.put(53, "pravac_265.png");
		listaIkonica.put(54, "pravac_270.png");
		listaIkonica.put(55, "pravac_275.png");
		listaIkonica.put(56, "pravac_280.png");
		listaIkonica.put(57, "pravac_285.png");
		listaIkonica.put(58, "pravac_290.png");
		listaIkonica.put(59, "pravac_295.png");
		listaIkonica.put(60, "pravac_300.png");
		listaIkonica.put(61, "pravac_305.png");
		listaIkonica.put(62, "pravac_310.png");
		listaIkonica.put(63, "pravac_315.png");
		listaIkonica.put(64, "pravac_320.png");
		listaIkonica.put(65, "pravac_325.png");
		listaIkonica.put(66, "pravac_330.png");
		listaIkonica.put(67, "pravac_335.png");
		listaIkonica.put(68, "pravac_340.png");
		listaIkonica.put(69, "pravac_345.png");
		listaIkonica.put(70, "pravac_350.png");
		listaIkonica.put(71, "pravac_355.png");
		listaIkonica.put(72, "pravac_000.png");
		return listaIkonica;
	}

}
