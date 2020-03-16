package rs.atekom.prati.view.komponente;

import java.io.Serializable;

public class VrstaDatoteke implements Serializable{

	private static final long serialVersionUID = 1L;
	private String naziv;
	private int id;
	
	public VrstaDatoteke() {
		// TODO Auto-generated constructor stub
	}
	
	public VrstaDatoteke(String naziv, int id) {
		this.naziv = naziv;
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
