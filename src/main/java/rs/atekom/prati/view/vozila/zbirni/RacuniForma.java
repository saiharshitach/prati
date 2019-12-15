package rs.atekom.prati.view.vozila.zbirni;

import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;

public class RacuniForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private RacuniLogika logika;
	
	public RacuniForma(RacuniLogika log) {
		logika = log;
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object sacuvajPodatak(Object podatak) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ocistiPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postaviPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean proveraPodataka() {
		// TODO Auto-generated method stub
		return false;
	}

}
