package rs.atekom.prati.view.uredjaji;

import com.vaadin.server.Page;

import pratiBaza.tabele.Sim;
import pratiBaza.tabele.Uredjaji;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class UredjajiLogika implements LogikaInterface{

	public UredjajiView view;
	
	public UredjajiLogika(UredjajiView uredjajiView) {
		view = uredjajiView;
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
				Uredjaji uredjaj = Servis.uredjajServis.nadjiUredjajPoId(id);
				view.izaberiRed(uredjaj);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Uredjaji uredjaj = (Uredjaji)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(uredjaj.getId() != null) {
			Servis.uredjajServis.izmeniUredjaj(uredjaj);
			if(uredjaj.getSim() != null) {
				Sim sim = uredjaj.getSim();
				sim.setUredjaji(uredjaj);
				sim.setZauzet(true);
				Servis.simServis.azurirajSim(sim);
			}
			if(uredjaj.getSim2() != null) {
				Sim sim = uredjaj.getSim2();
				sim.setUredjaji(uredjaj);
				sim.setZauzet(true);
				Servis.simServis.azurirajSim(sim);
			}
			view.pokaziPorukuUspesno("уређај измењен");
		}else {
			try {
				Servis.uredjajServis.unesiUredjaj(uredjaj);
				view.pokaziPorukuUspesno("уређај сачуван");
			}catch (Exception e) {
				view.pokaziPorukuGreska("уређај са унетим кодом већ постоји!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Uredjaji uredjaj = (Uredjaji)podatak;
		if(uredjaj == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(uredjaj.getId() + "");
		}
		view.izmeniPodatak(uredjaj);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Uredjaji());
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
		view.izmeniPodatak((Uredjaji)podatak);
	}

}
