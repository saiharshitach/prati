package rs.atekom.prati.view.vozilo.primoPredaje;

import rs.atekom.prati.view.OpstaForma;
import rs.atekom.prati.view.OpstaFormaInterface;
import rs.atekom.prati.view.komponente.ComboKorisniciVozaci;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;

public class VozilaPrimoPredajeForma extends OpstaForma implements OpstaFormaInterface{

	private static final long serialVersionUID = 1L;
	private VozilaPrimoPredajeLogika logika;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboKorisniciVozaci vozaci;

	public VozilaPrimoPredajeForma(VozilaPrimoPredajeLogika log) {
		logika = log;
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
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
