package rs.atekom.prati.view.vozila;

import com.vaadin.server.Page;

import pratiBaza.tabele.Vozila;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaLogika implements LogikaInterface{

	public VozilaView view;

	public VozilaLogika(VozilaView objektDetaljView) {
		view = objektDetaljView;
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
				Vozila objektDetalj = Servis.voziloServis.nadjiVoziloPoId(id);
				view.izaberiRed(objektDetalj);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Vozila objekatDetalj = (Vozila)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(objekatDetalj.getId() != null) {
			Servis.voziloServis.azurirajVozilo(objekatDetalj);
			view.pokaziPorukuUspesno("детаљи објекта измењени");
		}else {
			try {
				if(Servis.voziloServis.nadjiVoziloPoObjektu(objekatDetalj.getObjekti()) == null) {
					Servis.voziloServis.unesiVozilo(objekatDetalj);
					view.pokaziPorukuUspesno("детаљи објекта сачувани");
				}else {
					view.pokaziPorukuGreska("детаљи за изабрани објекат су већ унети!");
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("детаљи објекта због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Vozila objekatDetalj = (Vozila)podatak;
		if(objekatDetalj == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(objekatDetalj.getId() + "");
		}
		view.izmeniPodatak(objekatDetalj);
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
