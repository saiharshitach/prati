package rs.atekom.prati.view.proizvodjaci;

import com.vaadin.server.Page;
import pratiBaza.tabele.SistemUredjajiProizvodjac;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class UredjajiProizvodjaciLogika implements LogikaInterface{
	
	public UredjajiProizvodjaciView view;
	
	public UredjajiProizvodjaciLogika(Object proizvodjaciView) {
		view = (UredjajiProizvodjaciView)proizvodjaciView;
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
		page.setUriFragment("!" + UredjajiProizvodjaciView.VIEW_NAME + "/" + fragmentParametar, false);
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
				SistemUredjajiProizvodjac proizvodjac = Servis.sistemUredjajProizvodjacServis.nadjiProizvodjacaPoId(id);
				view.izaberiRed(proizvodjac);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		SistemUredjajiProizvodjac proizvodjac = (SistemUredjajiProizvodjac)podatak;
		if(proizvodjac.getId() != null) {
			Servis.sistemUredjajProizvodjacServis.izmeniSistemUredjajProizvodjaca(proizvodjac);
			view.pokaziPorukuUspesno("произвођач измењен");
		}else {
			Servis.sistemUredjajProizvodjacServis.unesiSistemUredjajProizvodjaca(proizvodjac);
			view.pokaziPorukuUspesno("модел сачуван");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemUredjajiProizvodjac proizvodjac = (SistemUredjajiProizvodjac)podatak;
		if(proizvodjac == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(proizvodjac.getId() + "");
		}
		view.izmeniPodatak(podatak);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new SistemUredjajiProizvodjac());
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
		view.izmeniPodatak((SistemUredjajiProizvodjac)podatak);
	}

}
