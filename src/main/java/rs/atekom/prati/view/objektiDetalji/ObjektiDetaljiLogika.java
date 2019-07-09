package rs.atekom.prati.view.objektiDetalji;

import com.vaadin.server.Page;

import pratiBaza.tabele.ObjektiDetalji;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class ObjektiDetaljiLogika implements LogikaInterface{

	public ObjektiDetaljiView view;

	public ObjektiDetaljiLogika(ObjektiDetaljiView objektDetaljView) {
		view = objektDetaljView;
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
				ObjektiDetalji objektDetalj = Servis.objekatDetaljiServis.nadjiObjektiDetaljiPoId(id);
				view.izaberiRed(objektDetalj);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		ObjektiDetalji objekatDetalj = (ObjektiDetalji)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(objekatDetalj.getId() != null) {
			Servis.objekatDetaljiServis.azurirajObjektiDetalji(objekatDetalj);
			view.pokaziPorukuUspesno("детаљи објекта измењени");
		}else {
			try {
				if(Servis.objekatDetaljiServis.nadjiObjekatDetaljePoObjektu(objekatDetalj.getObjekti()) == null) {
					Servis.objekatDetaljiServis.unesiObjektiDetalji(objekatDetalj);
					view.pokaziPorukuUspesno("детаљи објекта сачувани");
				}else {
					view.pokaziPorukuGreska("детаљи за изабрани објекат су већ унети!");
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("детаљи објекта због грешке нису сачувани!");
			}
		}
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		ObjektiDetalji objekatDetalj = (ObjektiDetalji)podatak;
		if(objekatDetalj == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(objekatDetalj.getId() + "");
		}
		view.izmeniPodatak(objekatDetalj);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new ObjektiDetalji());
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
		view.izmeniPodatak((ObjektiDetalji)podatak);
	}
	
}
