package rs.atekom.prati.view.grupe;

import com.vaadin.server.Page;

import pratiBaza.tabele.Grupe;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class GrupeLogika implements LogikaInterface{

	public GrupeView view;
	
	public GrupeLogika(GrupeView grupaView) {
		view = grupaView;
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
				Grupe grupa = Servis.grupeServis.nadjiGrupuPoId(id);
				view.izaberiRed(grupa);
			}catch (Exception e) {
				
			}
			
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Grupe grupa = (Grupe)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(grupa.getId() != null) {
			Servis.grupeServis.azurirajGrupu(grupa);
			view.pokaziPorukuUspesno("група измењена");
		}else {
			Servis.grupeServis.unesiGrupu(grupa);
			view.pokaziPorukuUspesno("група сачувана");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Grupe grupa = (Grupe)podatak;
		if(grupa == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(grupa.getId() + "");
		}
		view.izmeniPodatak(grupa);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Grupe());
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
		view.izmeniPodatak((Grupe)podatak);
	}

}
