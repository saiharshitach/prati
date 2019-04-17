package rs.cybertrade.prati.view.simSistemOperateri;

import com.vaadin.server.Page;

import pratiBaza.tabele.SistemOperateri;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

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
		SistemOperateri operater = (SistemOperateri)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
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
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new SistemOperateri());
	}

	@Override
	public void ukloniPodatak() {
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((SistemOperateri)podatak);
	}

}
