package rs.atekom.prati.view.vozaci.pasosi;

import com.vaadin.server.Page;
import pratiBaza.tabele.VozaciPasosi;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciPasosiLogika implements LogikaInterface{

	public VozaciPasosiView view;

	public VozaciPasosiLogika(VozaciPasosiView vozaciPasosiView) {
		view = vozaciPasosiView;
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
				VozaciPasosi pasos = Servis.pasosServis.nadjiVozacPasosPoId(id);
				view.izaberiRed(pasos);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozaciPasosi pasos = (VozaciPasosi)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(pasos.getId() != null) {
			Servis.pasosServis.izmeniVozacPasos(pasos);
			view.pokaziPorukuUspesno("подаци за пасош уверење измењени");
		}else {
			try {
				Servis.pasosServis.unesiVozacPasos(pasos);
				view.pokaziPorukuUspesno("подаци за пасош сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciPasosi pasos = (VozaciPasosi)podatak;
		if(pasos == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(pasos.getId() + "");
		}
		view.izmeniPodatak(pasos);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozaciPasosi());
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
		view.izmeniPodatak((VozaciPasosi)podatak);
	}
	
	
}
