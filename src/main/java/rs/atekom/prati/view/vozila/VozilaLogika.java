package rs.atekom.prati.view.vozila;

import com.vaadin.server.Page;

import pratiBaza.tabele.Vozila;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaLogika implements LogikaInterface{

	public VozilaView view;

	public VozilaLogika(VozilaView voziloView) {
		view = voziloView;
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
				Vozila vozilo = Servis.voziloServis.nadjiVoziloPoId(id);
				view.izaberiRed(vozilo);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Vozila vozilo = (Vozila)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(vozilo.getId() != null) {
			Servis.voziloServis.azurirajVozilo(vozilo);
			view.pokaziPorukuUspesno("подаци возила измењени");
		}else {
			try {
				if(Servis.voziloServis.nadjiVoziloPoObjektu(vozilo.getObjekti()) == null) {
					Servis.voziloServis.unesiVozilo(vozilo);
					view.pokaziPorukuUspesno("подаци возила сачувани");
				}else {
					view.pokaziPorukuGreska("подаци за изабрано возило су већ унети!");
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци за возило због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Vozila vozilo = (Vozila)podatak;
		if(vozilo == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(vozilo.getId() + "");
		}
		view.izmeniPodatak(vozilo);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Vozila());
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
		view.izmeniPodatak((Vozila)podatak);
	}
	
}