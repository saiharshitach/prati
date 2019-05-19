package rs.cybertrade.prati.view.sistem;

import com.vaadin.server.Page;

import pratiBaza.tabele.Sistem;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class SistemLogika implements LogikaInterface{

	public SistemView view;
	
	public SistemLogika(SistemView sistemView) {
		view = sistemView;
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
		page.setUriFragment("!" + SistemView.VIEW_NAME + "/" + fragmentParametar, false);
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
				Sistem sistem = Servis.sistemServis.nadjiSistemPoId(id);
				view.izaberiRed(sistem);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Sistem sistem = (Sistem)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(sistem.getId() != null) {
			Servis.sistemServis.azurirajSistem(sistem);
			view.pokaziPorukuUspesno("систем подаци измењени");
		}else {
			Servis.sistemServis.unesiSistem(sistem);
			view.pokaziPorukuUspesno("систем подаци сачувани");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Sistem sistem = (Sistem)podatak;
		if(sistem == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(sistem.getId() + "");
		}
		view.izmeniPodatak(sistem);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Sistem());
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
		view.izmeniPodatak((Sistem)podatak);
	}

}
