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
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		Uredjaji uredjaj = (Uredjaji)podatak;
		if(uredjaj.getId() != null) {
			Servis.uredjajServis.izmeniUredjaj(uredjaj);
			Sim sim = uredjaj.getSim();
			if(sim != null) {
				sim.setUredjaji(uredjaj);
				sim.setZauzet(true);
				Servis.simServis.azurirajSim(sim);
			}
			Sim sim2 = uredjaj.getSim2();
			if(uredjaj.getSim2() != null) {
				sim2.setUredjaji(uredjaj);
				sim2.setZauzet(true);
				Servis.simServis.azurirajSim(sim2);
			}
			view.pokaziPorukuUspesno("уређај измењен");
		}else {
			try {
				Servis.uredjajServis.unesiUredjaj(uredjaj);
				Sim sim = uredjaj.getSim();
				if(sim != null) {
					sim.setUredjaji(uredjaj);
					sim.setZauzet(true);
					Servis.simServis.azurirajSim(sim);
				}
				Sim sim2 = uredjaj.getSim2();
				if(sim2 != null) {
					sim2.setUredjaji(uredjaj);
					sim2.setZauzet(true);
					Servis.simServis.azurirajSim(sim2);
				}
				System.out.print("br kartica " + uredjaj.getSims().size());
				System.out.print("prva kartica " + uredjaj.getSims().get(0).getBroj());
				view.pokaziPorukuUspesno("уређај сачуван");
			}catch (Exception e) {
				e.printStackTrace();
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
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new Uredjaji());
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
		view.izmeniPodatak((Uredjaji)podatak);
	}

}
