package rs.cybertrade.prati.view;

public interface OpstaFormaInterface {
	
	public void izmeniPodatak(Object podatak);
	
	//u ButtonClickListener-u dugmeta dodaj u ovoj formi
	public Object sacuvajPodatak(Object podatak);
	
	public void ocistiPodatak();
	
	public void postaviPodatak(Object podatak);
	
	public boolean proveraPodataka();
}
