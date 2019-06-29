package rs.cybertrade.prati.mape;

import java.text.SimpleDateFormat;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.JavljanjaPoslednja;

public class GMarker extends GoogleMapMarker{

	private static final long serialVersionUID = 1L;
	private String oznaka;
	private String registracija;
	private String model;
	private String datumVreme;
	private String brzina;
	private Integer ukupnokm;
	private Float ukupnoGorivo;
	private String[] datum;
	private Long objekatId;

	public GMarker(JavljanjaPoslednja javljanjePoslednje, boolean draggable, String iconUrl){
		super();
		setAnimationEnabled(false);
		setPosition(new LatLon(javljanjePoslednje.getLat(), javljanjePoslednje.getLon()));
		setDraggable(draggable);
		setIconUrl(iconUrl);
		setOznaka(javljanjePoslednje.getObjekti().getOznaka());
		setObjekatId(javljanjePoslednje.getObjekti().getId());
		podesiCaption(javljanjePoslednje);
	}

	public GMarker(Javljanja javljanjePoslednje, boolean draggable, String iconUrl){
		super();
		setAnimationEnabled(false);
		setPosition(new LatLon(javljanjePoslednje.getLat(), javljanjePoslednje.getLon()));
		setDraggable(draggable);
		setIconUrl(iconUrl);
		setOznaka(javljanjePoslednje.getObjekti().getOznaka());
		setObjekatId(javljanjePoslednje.getObjekti().getId());
		podesiCaption(javljanjePoslednje);
	}

	public Long getObjekatId() {
		return objekatId;
	}

	public void setObjekatId(Long objekatId) {
		this.objekatId = objekatId;
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

	public String getDatumVreme() {
		return datumVreme;
	}

	public void setDatumVreme(String datumVreme) {
		this.datumVreme = datumVreme;
	}

	public String[] getDatum() {
		return datum;
	}

	public void setDatum(String[] datum) {
		this.datum = datum;
	}
	
	private void podesiCaption(JavljanjaPoslednja javljanjePoslednje) {
		datumVreme = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(javljanjePoslednje.getDatumVreme());
		datum = datumVreme.split(" ");
		brzina = "брзина: " + javljanjePoslednje.getBrzina() + "км/ч";
		setCaption(String.join("\n", oznaka + " ", brzina + " ", datum[1] + " ", datum[0]));
	}
	
	private void podesiCaption(Javljanja javljanje) {
		datumVreme = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(javljanje.getDatumVreme());
		datum = datumVreme.split(" ");
		brzina = "брзина: " + javljanje.getBrzina() + "км/ч";
		setCaption(String.join("\n", oznaka + " ", brzina + " ", datum[1] + " ", datum[0]));
	}
}
