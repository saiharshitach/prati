package rs.atekom.prati.view.projekti;

import com.vaadin.server.Page;
import pratiBaza.tabele.Projekti;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class ProjektiLogika implements LogikaInterface{

	public ProjektiView view;
	
	public ProjektiLogika(ProjektiView projektiView) {
		view = projektiView;
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
				Projekti projekt = Servis.projektServis.nadjiProjekatPoId(id);
				view.izaberiRed(projekt);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Projekti projekt = (Projekti)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(projekt.getId() != null) {
			Servis.projektServis.izmeniProjekat(projekt);
			view.pokaziPorukuUspesno("подаци пројекта измењени");
		}else {
			try {
				Servis.projektServis.unesiProjekat(projekt);
				view.pokaziPorukuUspesno("подаци пројекта сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци пројекта због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Projekti projekt = (Projekti)podatak;
		if(projekt == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(projekt.getId() + "");
		}
		view.izmeniPodatak(projekt);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Projekti());
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
		view.izmeniPodatak((Projekti)podatak);
	}
}
