package rs.atekom.prati.view.objekti;

import com.vaadin.server.Page;

import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Uredjaji;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class ObjektiLogika implements LogikaInterface{

	public ObjektiView view;
	
	public ObjektiLogika(ObjektiView objektiView) {
		view = objektiView;
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
				Objekti objekat = Servis.objekatServis.nadjiObjekatPoId(id);
				view.izaberiRed(objekat);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Objekti objekat = (Objekti)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(objekat.getId() != null) {
			Servis.objekatServis.azurirajObjekte(objekat);
			
			/*if(objekat.getUredjaji() != null) {
				Uredjaji uredjaj = objekat.getUredjaji();
				uredjaj.setObjekti(objekat);
				uredjaj.setZauzet(true);
				Servis.uredjajServis.izmeniUredjaj(uredjaj);
			}**/
			view.pokaziPorukuUspesno("објекат измењен");
		}else {
			try {
				Servis.objekatServis.unesiObjekte(objekat);
				Uredjaji uredjaj = objekat.getUredjaji();
				if(uredjaj != null) {
					uredjaj.setObjekti(objekat);
					uredjaj.setZauzet(true);
					Servis.uredjajServis.izmeniUredjaj(uredjaj);
				}
				view.pokaziPorukuUspesno("објекат сачуван");
			}catch (Exception e) {
				e.printStackTrace();
				view.pokaziPorukuGreska("објекат због грешке није сачуван!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Objekti objekat = (Objekti)podatak;
		if(objekat == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(objekat.getId() + "");
		}
		view.izmeniPodatak(objekat);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Objekti());
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
		view.izmeniPodatak((Objekti)podatak);
	}
}
