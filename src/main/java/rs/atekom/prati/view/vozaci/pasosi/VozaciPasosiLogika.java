package rs.atekom.prati.view.vozaci.pasosi;

import java.util.ArrayList;

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
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		VozaciPasosi pasos = (VozaciPasosi)podatak;
		if(pasos.getId() != null) {
			Servis.pasosServis.izmeniVozacPasos(pasos);
			view.pokaziPorukuUspesno("подаци за пасош уверење измењени");
		}else {
			try {
				ArrayList<VozaciPasosi> lista = Servis.pasosServis.nadjiSveVozacPasosPoVozacu(pasos.getVozaci());
				if(lista.isEmpty() || lista == null) {
					Servis.pasosServis.unesiVozacPasos(pasos);
					view.pokaziPorukuUspesno("подаци за пасош сачувани");
				}else {
					boolean unet = false;
					for(VozaciPasosi pas: lista) {
						if((pas.getBrojPasosa() != null && pas.getBrojPasosa().equals(pasos.getBrojPasosa()) || pas.getVaziDo() == null || pas.getVaziDo().after(pas.getIzdato()))) {
							unet = true;
						}
					}
					if(!unet) {
						Servis.pasosServis.unesiVozacPasos(pasos);
						view.pokaziPorukuUspesno("подаци за пасош сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за о пасошу већ унети, проверите унос броја и датума");
					}
				}
				
			}catch (Exception e) {
				e.printStackTrace();
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
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new VozaciPasosi());
	}

	@Override
	public void ukloniPodatak() {
		setFragmentParametar("");
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		view.updateTable();
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((VozaciPasosi)podatak);
	}
	
	
}
