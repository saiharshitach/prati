package rs.atekom.prati.view.vozaci.lekarsko;

import com.vaadin.server.Page;
import pratiBaza.tabele.VozaciLekarsko;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciLekarskoLogika implements LogikaInterface{

	public VozaciLekarskoView view;
	
	public VozaciLekarskoLogika(VozaciLekarskoView vozaciLekarskoView) {
		view = vozaciLekarskoView;
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
				VozaciLekarsko lekarsko = Servis.lekarskoServis.nadjiVozacLekarskoPoId(id);
				view.izaberiRed(lekarsko);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(lekarsko.getId() != null) {
			Servis.lekarskoServis.izmeniVozacLekarsko(lekarsko);
			view.pokaziPorukuUspesno("подаци за лекарско уверење измењени");
		}else {
			try {
				Servis.lekarskoServis.unesiVozacLekarsko(lekarsko);
				view.pokaziPorukuUspesno("подаци за лекарско уверење сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		if(lekarsko == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(lekarsko.getId() + "");
		}
		view.izmeniPodatak(lekarsko);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozaciLekarsko());
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
		view.izmeniPodatak((VozaciLekarsko)podatak);
	}
}
