package rs.atekom.prati.view.komponente;

import java.io.Serializable;

import com.vaadin.addon.excel.ExcelColumn;

public class LukOilGorivo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ExcelColumn("registracija")
	private String registracija;
	
	@ExcelColumn("derivat")
	private String derivat;
	
	@ExcelColumn("kolicina")
	private String kolicina;
	
	@ExcelColumn("cena")
	private String cena;
	
	@ExcelColumn("ukupno")
	private String ukupno;
	
	@ExcelColumn("stanica")
	private String stanica;
	
	@ExcelColumn("datum bs")
	private String datumBs;

	public LukOilGorivo() {
		// TODO Auto-generated constructor stub
	}

	public String getRegistracija() {
		return registracija;
	}

	public void setRegistracija(String registracija) {
		this.registracija = registracija;
	}

	public String getDerivat() {
		return derivat;
	}

	public void setDerivat(String derivat) {
		this.derivat = derivat;
	}

	public String getKolicina() {
		return kolicina;
	}

	public void setKolicina(String kolicina) {
		this.kolicina = kolicina;
	}

	public String getCena() {
		return cena;
	}

	public void setCena(String cena) {
		this.cena = cena;
	}

	public String getUkupno() {
		return ukupno;
	}

	public void setUkupno(String ukupno) {
		this.ukupno = ukupno;
	}

	public String getStanica() {
		return stanica;
	}

	public void setStanica(String stanica) {
		this.stanica = stanica;
	}

	public String getDatumBs() {
		return datumBs;
	}

	public void setDatumBs(String datumBs) {
		this.datumBs = datumBs;
	}
	
}
