package rs.atekom.prati.view.sifre;

import com.vaadin.server.Page;

import pratiBaza.tabele.Sifre;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class SifreLogika implements LogikaInterface{

	public SifreView view;
	
	public SifreLogika(SifreView sifreView) {
		view = sifreView;
	}
	
	@Override
	public void init() {
		izmeniPodatak(null);
		view.postaviNoviOmoguceno(true);
	}

	@Override
	public void otkaziPodatak() {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
	}

	@Override
	public void setFragmentParametar(String objectId) {
		String fragmentParametar;
		if(objectId == null || objectId.isEmpty()) {
			fragmentParametar = "";
		}else {
			fragmentParametar = objectId;
		}
		Page page = Prati.getCurrent().getPage();
		page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParametar, false);
	}

	@Override
	public void enter(String objectId) {
		if(objectId != null && !objectId.isEmpty()) {
			if(objectId.equals("new")) {
				noviPodatak();
			}
		}else {
			try {
				int id = Integer.parseInt(objectId);
				Sifre sifra = Servis.sifraServis.nadjiSifruPoId(id);
				view.izaberiRed(sifra);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Sifre sifra = (Sifre)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(sifra.getId() != null) {
			Servis.sifraServis.izmeniSifru(sifra);
			view.pokaziPorukuUspesno("подаци шифре измењени");
		}else {
			try {
				Servis.sifraServis.unesiSifru(sifra);
				view.pokaziPorukuUspesno("подаци шифре сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци шифре због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Sifre sifra = (Sifre)podatak;
		if(sifra == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(sifra.getId() + "");
		}
		view.izmeniPodatak(sifra);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Sifre());
	}

	@Override
	public void ukloniPodatak() {
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		view.updateTable();
		setFragmentParametar("");
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((Sifre)podatak);
	}

}
