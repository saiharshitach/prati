package rs.atekom.prati.view.komponente;

public class TipServisa {

	String naziv;
	int rb;
	
	public TipServisa(String naziv, int rb) {
		this.naziv = naziv;
		this.rb = rb;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getRb() {
		return rb;
	}

	public void setRb(int rb) {
		this.rb = rb;
	}
	
}
