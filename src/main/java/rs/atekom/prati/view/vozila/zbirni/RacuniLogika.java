package rs.atekom.prati.view.vozila.zbirni;

import com.vaadin.server.Page;
import pratiBaza.tabele.Racuni;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class RacuniLogika implements LogikaInterface{

	public RacuniView view;
	
	public RacuniLogika(RacuniView zbirniView) {
		view = zbirniView;
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
				Racuni racun = Servis.racunServis.nadjiRacunPoId(id);
				view.izaberiRed(racun);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Racuni racun = (Racuni)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(racun.getId() != null) {
			Servis.racunServis.izmeniRacun(racun);
			view.pokaziPorukuUspesno("подаци рачуна измењени");
		}else {
			try {
				Servis.racunServis.unesiRacun(racun);
				view.pokaziPorukuUspesno("подаци рачуна сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци рачуна због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Racuni racun = (Racuni)podatak;
		if(podatak == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(racun.getId() + "");
		}
		view.izmeniPodatak(racun);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Racuni());
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
		view.izmeniPodatak((Racuni)podatak);
	}

}
