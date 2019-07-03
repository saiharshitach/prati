package rs.atekom.prati.view.sim;

import com.vaadin.server.Page;

import pratiBaza.tabele.Sim;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class SimLogika implements LogikaInterface{

	public SimView view;
	
	public SimLogika(SimView simView) {
		view = simView;
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
				Sim sim = Servis.simServis.nadjiSimPoID(id);
				view.izaberiRed(sim);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Sim sim = (Sim)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(sim.getId() != null) {
			Servis.simServis.azurirajSim(sim);
			view.pokaziPorukuUspesno("сим измењена");
		}else {
			try {
				Servis.simServis.unesiSim(sim);
				view.pokaziPorukuUspesno("сим сачувана");
			}catch (Exception e) {
				view.pokaziPorukuGreska("сим са унетим иццид или позивним бројем већ постоји!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Sim sim = (Sim)podatak;
		if(sim == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(sim.getId() + "");
		}
		view.izmeniPodatak(sim);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Sim());
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
		view.izmeniPodatak((Sim)podatak);
	}

}
