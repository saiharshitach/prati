package rs.cybertrade.prati.mape;

import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

public class GMarker extends GoogleMapMarker{

	private static final long serialVersionUID = 1L;
	private Long objekat_id;
	private String oznaka;
	private String registracija;
	private String model;
	private String datum_vreme;
	private String brzina;
	private Integer ukupnokm;
	private Float ukupnoGorivo;

	public GMarker(String caption, LatLon position, boolean draggable, String iconUrl){
		super();
		this.setCaption(caption);
		this.setPosition(position);
		this.setDraggable(draggable);
		this.setIconUrl(iconUrl);
	}

	public Long getObjekat_id() {
		return objekat_id;
	}

	public void setObjekat_id(Long objekat_id) {
		this.objekat_id = objekat_id;
	}

	public String getOznaka() {
		return oznaka;
	}

	public void setOznaka(String oznaka) {
		this.oznaka = oznaka;
	}

	public String getRegistracija() {
		return registracija;
	}

	public void setRegistracija(String registracija) {
		this.registracija = registracija;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDatum_vreme() {
		return datum_vreme;
	}

	public void setDatum_vreme(String datum_vreme) {
		this.datum_vreme = datum_vreme;
	}

	public String getBrzina() {
		return brzina;
	}

	public void setBrzina(String brzina) {
		this.brzina = brzina;
	}

	public Integer getUkupnokm() {
		return ukupnokm;
	}

	public void setUkupnokm(Integer ukupnokm) {
		this.ukupnokm = ukupnokm;
	}

	public Float getUkupnoGorivo() {
		return ukupnoGorivo;
	}

	public void setUkupnoGorivo(Float ukupnoGorivo) {
		this.ukupnoGorivo = ukupnoGorivo;
	}
	
}
