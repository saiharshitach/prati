package rs.atekom.view.troskoviPotrosnja;

import com.vaadin.server.Page;

import pratiBaza.tabele.Troskovi;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class PotrosnjaLogika implements LogikaInterface{

	public PotrosnjaView view;
	
	public PotrosnjaLogika(PotrosnjaView potrosnjaView) {
		view = potrosnjaView;
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
				Troskovi trosak = Servis.trosakServis.nadjiTrosakPoId(id);
				view.izaberiRed(trosak);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(trosak.getId() != null) {
			Servis.trosakServis.izmeniTrosak(trosak);
			view.pokaziPorukuUspesno("подаци одржавања измењени");
		}else {
			try {
				Servis.trosakServis.unesiTrosak(trosak);
				view.pokaziPorukuUspesno("подаци потрошње сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци потрошње због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		if(trosak == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(trosak.getId() + "");
		}
		view.izmeniPodatak(trosak);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Troskovi());
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
		view.izmeniPodatak((Troskovi)podatak);
	}

}
