package rs.cybertrade.prati.view;

public interface LogikaInterface {

	public void init();
	
	public void otkaziPodatak();
	
	public void setFragmentParametar(String objectId);
	
	public void enter(String objectId);
	
	//za cuvanje korisnika novog ili izmena starog
    public void sacuvajPodatak(Object podatak);
    
    public void izmeniPodatak(Object podatak);
    
    public void noviPodatak();
    
    public void ukloniPodatak();
    
    public void redIzabran(Object podatak);
}
