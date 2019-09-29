package rs.atekom.prati.view.partneri;

import com.vaadin.server.Page;
import pratiBaza.tabele.Partneri;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class PartneriLogika implements LogikaInterface{

	public PartneriView view;
	
	public PartneriLogika(PartneriView partneriView) {
		view = partneriView;
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
				Partneri partner = Servis.partnerServis.nadjiPartneraPoId(id);
				view.izaberiRed(partner);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Partneri partner = (Partneri)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(partner.getId() != null) {
			Servis.partnerServis.izmeniPartnera(partner);
			view.pokaziPorukuUspesno("партнер измењен");
		}else {
			try {
				if(Servis.partnerServis.nadjiPartneraPoPibu(partner.getSistemPretplatnici(), partner.getPib()) == null) {
					Servis.partnerServis.unesiPartnera(partner);
					view.pokaziPorukuUspesno("партнер сачуван");
				}else {
					view.pokaziPorukuGreska("партнер са овим ПИБ-ом је већ унет!");
				}
				
			}catch (Exception e) {
				view.pokaziPorukuGreska("партнер са овим ПИБ-ом је већ унет!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Partneri partner = (Partneri)podatak;
		if(partner == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(partner.getId() + "");
		}
		view.izmeniPodatak(partner);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Partneri());
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
		view.izmeniPodatak((Partneri)podatak);
	}

}
