package rs.cybertrade.prati.view.organizacije;

import com.vaadin.server.Page;

import pratiBaza.tabele.Organizacije;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class OrganizacijeLogika implements LogikaInterface{

	public OrganizacijeView view;
	
	public OrganizacijeLogika(OrganizacijeView orgView) {
		view = orgView;
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
				Organizacije organizacija = Servis.organizacijaServis.nadjiOrganizacijuPoId(id);
				view.izaberiRed(organizacija);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Organizacije organizacija = (Organizacije)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(organizacija.getId() != null) {
			Servis.organizacijaServis.azurirajOrganizacije(organizacija);
			view.pokaziPorukuUspesno("организација измењена");
		}else {
			Servis.organizacijaServis.unesiOrganizacije(organizacija);
			view.pokaziPorukuUspesno("организација измењена");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Organizacije organizacija = (Organizacije)podatak;
		if(organizacija == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(organizacija.getId() + "");
		}
		view.izmeniPodatak(organizacija);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Organizacije());
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
		view.izmeniPodatak((Organizacije)podatak);
	}

}
