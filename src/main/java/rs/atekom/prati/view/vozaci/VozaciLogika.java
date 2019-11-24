package rs.atekom.prati.view.vozaci;

import java.util.ArrayList;

import com.vaadin.server.Page;
import pratiBaza.tabele.Vozaci;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciLogika implements LogikaInterface{

	public VozaciView view;
	
	public VozaciLogika(VozaciView vozaciView) {
		view = vozaciView;
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
				Vozaci vozac = Servis.vozacServis.nadjiVozacaPoId(id);
				view.izaberiRed(vozac);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		Vozaci vozac = (Vozaci)podatak;
		if(vozac.getId() != null) {
			Servis.vozacServis.izmeniVozaca(vozac);
			view.pokaziPorukuUspesno("подаци за возача измењени");
		}else {
			try {
				ArrayList<Vozaci> lista = Servis.vozacServis.nadjiSveVozacePoKorisniku(vozac.getKorisnici());
				if(lista.isEmpty() || lista == null) {
					Servis.vozacServis.unesiVozaca(vozac);
					view.pokaziPorukuUspesno("подаци за возача сачувани");
				}else {
					boolean unet = false;
					for(Vozaci voz: lista) {
						if(voz.getZaposlenDo() == null || voz.getZaposlenDo().after(vozac.getZaposlenOd())) {
							unet = true;
						}
					}
					if(!unet) {
						Servis.vozacServis.unesiVozaca(vozac);
						view.pokaziPorukuUspesno("подаци за возача сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за овог возача већ унети");
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
		Vozaci vozac = (Vozaci)podatak;
		if(vozac == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(vozac.getId() + "");
		}
		view.izmeniPodatak(vozac);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new Vozaci());
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
		view.izmeniPodatak((Vozaci)podatak);
	}

}
