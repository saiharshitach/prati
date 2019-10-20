package rs.atekom.prati.view.vozaci.dozvole;

import java.util.ArrayList;

import com.vaadin.server.Page;

import pratiBaza.tabele.VozaciDozvole;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciDozvoleLogika implements LogikaInterface{

	public VozaciDozvoleView view;
	
	public VozaciDozvoleLogika(VozaciDozvoleView vozacDozvolaView) {
		view = vozacDozvolaView;
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
				VozaciDozvole dozvola = Servis.dozvolaServis.nadjiVozacDozvolaPoId(id);
				view.izaberiRed(dozvola);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozaciDozvole dozvola = (VozaciDozvole)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(dozvola.getId() != null) {
			Servis.dozvolaServis.izmeniVozacDozvola(dozvola);
			view.pokaziPorukuUspesno("подаци за возачку дозволу измењени");
		}else {
			try {
				ArrayList<VozaciDozvole> lista = Servis.dozvolaServis.nadjiSveVozacDozvolePoVozacu(dozvola.getVozaci());
				if(lista.isEmpty() || lista == null) {
					Servis.dozvolaServis.unesiVozacDozvola(dozvola);
					view.pokaziPorukuUspesno("подаци за возачку дозволу сачувани");
				}else {
					boolean unet = false;
					for(VozaciDozvole doz: lista) {
						if((doz.getBrojDozvole() != null && doz.getBrojDozvole().equals(dozvola.getBrojDozvole())) || doz.getVaziDo() == null || doz.getVaziDo().after(dozvola.getVaziDo())) {
							unet = true;
						}
					}
					if(!unet) {
						Servis.dozvolaServis.unesiVozacDozvola(dozvola);
						view.pokaziPorukuUspesno("подаци за возача сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за о дозволи већ унети, проверите унос броја и датума");
					}
				}
				
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciDozvole dozvola = (VozaciDozvole)podatak;
		if(dozvola == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(dozvola.getId() + "");
		}
		view.izmeniPodatak(dozvola);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozaciDozvole());
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
		view.izmeniPodatak((VozaciDozvole)podatak);
	}
}
