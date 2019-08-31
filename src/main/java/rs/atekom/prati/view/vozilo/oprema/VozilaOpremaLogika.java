package rs.atekom.prati.view.vozilo.oprema;

import com.vaadin.server.Page;
import pratiBaza.tabele.VozilaOprema;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaOpremaLogika implements LogikaInterface{

	public VozilaOpremaView view;
	
	public VozilaOpremaLogika(VozilaOpremaView vozilaOpremaView ) {
		view = vozilaOpremaView;
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
				VozilaOprema nalog = Servis.opremaServis.nadjiVoziloOpremuPoId(id);
				view.izaberiRed(nalog);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozilaOprema oprema = (VozilaOprema)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(oprema.getId() != null) {
			Servis.opremaServis.izmeniVoziloOpremu(oprema);
			view.pokaziPorukuUspesno("подаци за опрему измењени");
		}else {
			try {
				Servis.opremaServis.unesiVoziloOpremu(oprema);
				view.pokaziPorukuUspesno("подаци за опрему сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaOprema oprema = (VozilaOprema)podatak;
		if(oprema == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(oprema.getId() + "");
		}
		view.izmeniPodatak(oprema);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozilaOprema());
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
		view.izmeniPodatak((VozilaOprema)podatak);
	}

}
