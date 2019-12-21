package rs.atekom.prati.view.vozila.evidencija;

import com.vaadin.server.Page;

import pratiBaza.tabele.EvidencijaVoznji;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class EvidencijaVoznjiLogika implements LogikaInterface{

	public EvidencijaVoznjiView view;
	
	public EvidencijaVoznjiLogika(EvidencijaVoznjiView evidencijaView) {
		view = evidencijaView;
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
				EvidencijaVoznji evidencija = Servis.evidencijaServis.nadjiEvidencijuPoId(id);
				view.izaberiRed(evidencija);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		EvidencijaVoznji evidencija = (EvidencijaVoznji)podatak;
		if(evidencija.getId() != null) {
			Servis.evidencijaServis.izmeniEvidenciju(evidencija);
			view.pokaziPorukuUspesno("подаци измењени");
		}else {
			try {
				Servis.evidencijaServis.unesiEvidenciju(evidencija);
				view.pokaziPorukuUspesno("подаци сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		EvidencijaVoznji evidencija = (EvidencijaVoznji)podatak;
		if(evidencija == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(evidencija.getId() + "");
		}
		view.izmeniPodatak(evidencija);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new EvidencijaVoznji());
	}

	@Override
	public void ukloniPodatak() {
		setFragmentParametar("");
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		view.updateTable();
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((EvidencijaVoznji)podatak);
	}

}
