package rs.atekom.prati.view.vozaci.licna;

import java.util.ArrayList;

import com.vaadin.server.Page;

import pratiBaza.tabele.VozaciLicna;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciLicnaLogika implements LogikaInterface{

	public VozaciLicnaView view;
	
	public VozaciLicnaLogika(VozaciLicnaView vozaciLicnaView) {
		view = vozaciLicnaView;
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
				VozaciLicna licna = Servis.licnaServis.nadjiVozacLicnaPoId(id);
				view.izaberiRed(licna);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		VozaciLicna licna = (VozaciLicna)podatak;
		if(licna.getId() != null) {
			Servis.licnaServis.izmeniVozacLicna(licna);
			view.pokaziPorukuUspesno("подаци за личну карту измењени");
		}else {
			try {
				ArrayList<VozaciLicna> lista = Servis.licnaServis.nadjiSveVozacLicnaPoVozacu(licna.getVozaci());
				if(lista.isEmpty() || lista == null) {
					Servis.licnaServis.unesiVozacLicna(licna);
					view.pokaziPorukuUspesno("подаци за личну карту сачувани");
				}else {
					boolean unet = false;
					for(VozaciLicna lic: lista) {
						if((lic.getBroj() != null && lic.getBroj().equals(licna.getBroj())) || lic.getVaziDo() == null || lic.getVaziDo().after(licna.getIzdato())) {
							unet = true;
						}
					}
					if(!unet) {
						Servis.licnaServis.unesiVozacLicna(licna);
						view.pokaziPorukuUspesno("подаци за личну карту сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за о личној већ унети, проверите унос броја и датума");
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
		VozaciLicna licna = (VozaciLicna)podatak;
		if(licna == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(licna.getId() + "");
		}
		view.izmeniPodatak(licna);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new VozaciLicna());
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
		view.izmeniPodatak((VozaciLicna)podatak);
	}

}
