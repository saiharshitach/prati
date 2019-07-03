package rs.atekom.prati.view.uredjajiModeli;

import com.vaadin.server.Page;

import pratiBaza.tabele.SistemUredjajiModeli;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class UredjajiModeliLogika implements LogikaInterface{
	
	public UredjajiModeliView view;
	
	public UredjajiModeliLogika(UredjajiModeliView modeliView) {
		view = modeliView;
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
				SistemUredjajiModeli model = Servis.sistemUredjajModelServis.nadjiModelPoId(id);
				view.izaberiRed(model);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		SistemUredjajiModeli model = (SistemUredjajiModeli)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(model.getId() != null) {
			Servis.sistemUredjajModelServis.izmeniUredjajModel(model);
			view.pokaziPorukuUspesno("модел измењен");
		}else {
			Servis.sistemUredjajModelServis.unesiUredjajModel(model);
			view.pokaziPorukuUspesno("модел сачуван");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemUredjajiModeli model = (SistemUredjajiModeli)podatak;
		if(model == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(model.getId() + "");
		}
		view.izmeniPodatak(model);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new SistemUredjajiModeli());
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
		view.izmeniPodatak((SistemUredjajiModeli)podatak);
	}

}
