package rs.atekom.prati.server;

import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;

public class JavljanjeObd {

	private Javljanja javljanje;
	private Obd obd;
	
	public JavljanjeObd(Javljanja jav, Obd ob) {
		javljanje = jav;
		obd = ob;
	}

	public Javljanja getJavljanje() {
		return javljanje;
	}

	public void setJavljanje(Javljanja javljanje) {
		this.javljanje = javljanje;
	}

	public Obd getObd() {
		return obd;
	}

	public void setObd(Obd obd) {
		this.obd = obd;
	}
	
}
