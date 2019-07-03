package rs.atekom.prati.view.pretplatnici;

import com.vaadin.server.Page;

import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class PretplatniciLogika implements LogikaInterface{

	public PretplatniciView view;
	
	public PretplatniciLogika(PretplatniciView pretplatniciView) {
		view = pretplatniciView;
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
				SistemPretplatnici pretplatnik = Servis.sistemPretplatnikServis.nadjiPretplatnikaPoId(id);
				view.izaberiRed(pretplatnik);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		SistemPretplatnici pretplatnik = (SistemPretplatnici)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(pretplatnik.getId() != null) {
			Servis.sistemPretplatnikServis.izmeniPretplatnika(pretplatnik);
			view.pokaziPorukuUspesno("претплатник измењен");
		}else {
			Servis.sistemPretplatnikServis.unesiPretplatnika(pretplatnik);
			view.pokaziPorukuUspesno("претплатник сачуван");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemPretplatnici pretplatnik = (SistemPretplatnici)podatak;
		if(pretplatnik == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(pretplatnik.getId() + "");
		}
		view.izmeniPodatak(pretplatnik);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new SistemPretplatnici());
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
		view.izmeniPodatak((SistemPretplatnici)podatak);
	}

}
