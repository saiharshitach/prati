package rs.atekom.prati.view.komponente;

import java.io.Serializable;

import com.vaadin.addon.excel.ExcelColumn;

public class EkoGorivo implements Serializable{

	private static final long serialVersionUID = 1L;

	@ExcelColumn("Date for sales Idoc")
	private String DateForSales;
	
	@ExcelColumn("Vehicle number")
	private String vehicleNumber;
	
	@ExcelColumn("Material number")
	private String materialNumber;
	
	@ExcelColumn("Material Description")
	private String materialDescription;
	
	@ExcelColumn("Quantity sold")
	private String quantitySold;
	
	@ExcelColumn("Sales price")
	private String salesPrice;
	
	@ExcelColumn("Sales value")
	private String salesValue;
	
	@ExcelColumn("Plant")
	private String plant;
	
	@ExcelColumn("Fisc. Date Time")
	private String fiscalDateTime;
	
	public EkoGorivo() {
		// TODO Auto-generated constructor stub
	}

	public String getDateForSales() {
		return DateForSales;
	}

	public void setDateForSales(String dateForSales) {
		DateForSales = dateForSales;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public String getQuantitySold() {
		return quantitySold;
	}

	public void setQuantitySold(String quantitySold) {
		this.quantitySold = quantitySold;
	}

	public String getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(String salesPrice) {
		this.salesPrice = salesPrice;
	}

	public String getSalesValue() {
		return salesValue;
	}

	public void setSalesValue(String salesValue) {
		this.salesValue = salesValue;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getFiscalDateTime() {
		return fiscalDateTime;
	}

	public void setFiscalDateTime(String fiscalDateTime) {
		this.fiscalDateTime = fiscalDateTime;
	}
	
}
