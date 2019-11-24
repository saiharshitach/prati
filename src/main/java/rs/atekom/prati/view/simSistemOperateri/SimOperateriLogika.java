package rs.atekom.prati.view.simSistemOperateri;

import com.vaadin.server.Page;

import pratiBaza.tabele.SistemOperateri;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class SimOperateriLogika implements LogikaInterface{

	public SimOperateriView view;
	
	public SimOperateriLogika(SimOperateriView operaterView) {
		view = operaterView;
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
				SistemOperateri operater = Servis.sistemOperaterServis.nadjiOperateraPoId(id);
				view.izaberiRed(operater);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		SistemOperateri operater = (SistemOperateri)podatak;
		if(operater.getId() != null) {
			Servis.sistemOperaterServis.azurirajOperatera(operater);
			view.pokaziPorukuUspesno("оператер измењен");
		}else {
			Servis.sistemOperaterServis.unesiOperatera(operater);
			view.pokaziPorukuUspesno("оператер сачуван");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemOperateri operater = (SistemOperateri)podatak;
		if(operater == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(operater.getId() + "");
		}
		view.izmeniPodatak(operater);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new SistemOperateri());
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
		view.izmeniPodatak((SistemOperateri)podatak);
	}

}
