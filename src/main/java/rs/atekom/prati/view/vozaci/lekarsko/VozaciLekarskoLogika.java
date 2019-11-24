package rs.atekom.prati.view.vozaci.lekarsko;

import java.util.ArrayList;

import com.vaadin.server.Page;
import pratiBaza.tabele.VozaciLekarsko;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciLekarskoLogika implements LogikaInterface{

	public VozaciLekarskoView view;
	
	public VozaciLekarskoLogika(VozaciLekarskoView vozaciLekarskoView) {
		view = vozaciLekarskoView;
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
				VozaciLekarsko lekarsko = Servis.lekarskoServis.nadjiVozacLekarskoPoId(id);
				view.izaberiRed(lekarsko);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		if(lekarsko.getId() != null) {
			Servis.lekarskoServis.izmeniVozacLekarsko(lekarsko);
			view.pokaziPorukuUspesno("подаци за лекарско уверење измењени");
		}else {
			try {
				ArrayList<VozaciLekarsko> lista = Servis.lekarskoServis.nadjiSveVozacLekarskePoVozacu(lekarsko.getVozaci());
				if(lista.isEmpty() || lista == null) {
					Servis.lekarskoServis.unesiVozacLekarsko(lekarsko);
				view.pokaziPorukuUspesno("подаци за лекарско уверење сачувани");
				}else {
					boolean unet = false;
					for(VozaciLekarsko lek: lista) {
						if(lek.getVaziDo() == null || lek.getVaziDo().after(lekarsko.getIzdato())){
							unet = true;
						}
					}
					if(!unet) {
						Servis.lekarskoServis.izmeniVozacLekarsko(lekarsko);
						view.pokaziPorukuUspesno("подаци за лекарско уверење измењени");
					}else {
						view.pokaziPorukuGreska("подаци за о лекарском већ унети");
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
		VozaciLekarsko lekarsko = (VozaciLekarsko)podatak;
		if(lekarsko == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(lekarsko.getId() + "");
		}
		view.izmeniPodatak(lekarsko);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new VozaciLekarsko());
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
		view.izmeniPodatak((VozaciLekarsko)podatak);
	}
}
