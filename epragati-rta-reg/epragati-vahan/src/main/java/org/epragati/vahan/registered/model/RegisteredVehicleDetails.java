package org.epragati.vahan.registered.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author rahul.sharma
 *
 */
@XmlRootElement(name = "VehicleDetails")
public class RegisteredVehicleDetails {

    private String registrationNumber;
    private String chasisNumber;
    private String engineNumber;
    private String ownerName;
    private String name;
    private String makersName;
    private String model;
    private String permanentAddress;
    private String color;
    private String fuel;
    private String manufacturedMonthYear;
    private String vehicleClass;
    private String makersModel;
    private Integer rlw;
    private Integer ulw;
    private Integer seatingCapacity;
    private String statusMessage;

    private String registrationDate;
    private String ownerSr;
    private String firstName;
    private String presentAddress;
    private String bodyTypeDesc;
    private String fitUpto;
    private String taxUpto;
    private String financer;
    private String insuranceCompany;
    private String insurancePolicyNumber;
    private String insuranceUpto;
    private String noCyl;
    private String cubicCapacity;
    private Integer sleeperCapacity;
    private Integer standCapacity;
    private String wheelBase;
    private String registeredAt;
    private String statusAsOn;
    private String exShowroomPrice;
    
    /**
	 * @return the exShowroomPrice
	 */
    @XmlElement(name = "ex_showroom_price")
	public String getExShowroomPrice() {
		return exShowroomPrice;
	}

	/**
	 * @param exShowroomPrice the exShowroomPrice to set
	 */
	public void setExShowroomPrice(String exShowroomPrice) {
		this.exShowroomPrice = exShowroomPrice;
	}

	@XmlElement(name = "rc_regn_no")
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @XmlElement(name = "rc_chasi_no")
    public String getChasisNumber() {
        return chasisNumber;
    }

    public void setChasisNumber(String chasisNumber) {
        this.chasisNumber = chasisNumber;
    }

    @XmlElement(name = "rc_eng_no")
    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    @XmlElement(name = "rc_owner_name")
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @XmlElement(name = "rc_f_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "rc_maker_desc")
    public String getMakersName() {
        return makersName;
    }

    public void setMakersName(String makersName) {
        this.makersName = makersName;
    }

    @XmlElement(name = "rc_model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @XmlElement(name = "rc_permanent_address")
    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    @XmlElement(name = "rc_color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @XmlElement(name = "rc_fuel_desc")
    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    @XmlElement(name = "rc_manu_month_yr")
    public String getManufacturedMonthYear() {
        return manufacturedMonthYear;
    }

    public void setManufacturedMonthYear(String manufacturedMonthYear) {
        this.manufacturedMonthYear = manufacturedMonthYear;
    }

    @XmlElement(name = "rc_vh_class_desc")
    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    @XmlElement(name = "rc_maker_model")
    public String getMakersModel() {
        return makersModel;
    }

    public void setMakersModel(String makersModel) {
        this.makersModel = makersModel;
    }

    @XmlElement(name = "rc_gvw")
    public Integer getRlw() {
        return rlw;
    }

    public void setRlw(Integer rlw) {
        this.rlw = rlw;
    }

    @XmlElement(name = "rc_unld_wt")
    public Integer getUlw() {
        return ulw;
    }

    public void setUlw(Integer ulw) {
        this.ulw = ulw;
    }

    @XmlElement(name = "rc_seat_cap")
    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    @XmlElement(name = "stautsMessage")
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @XmlElement(name = "rc_regn_dt")
    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @XmlElement(name = "rc_owner_sr")
    public String getOwnerSr() {
        return ownerSr;
    }

    public void setOwnerSr(String ownerSr) {
        this.ownerSr = ownerSr;
    }

    @XmlElement(name = "rc_f_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = "rc_present_address")
    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    @XmlElement(name = "rc_body_type_desc")
    public String getBodyTypeDesc() {
        return bodyTypeDesc;
    }

    public void setBodyTypeDesc(String bodyTypeDesc) {
        this.bodyTypeDesc = bodyTypeDesc;
    }

    @XmlElement(name = "rc_fit_upto")
    public String getFitUpto() {
        return fitUpto;
    }

    public void setFitUpto(String fitUpto) {
        this.fitUpto = fitUpto;
    }

    @XmlElement(name = "rc_tax_upto")
    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    @XmlElement(name = "rc_financer")
    public String getFinancer() {
        return financer;
    }

    public void setFinancer(String financer) {
        this.financer = financer;
    }

    @XmlElement(name = "rc_insurance_comp")
    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @XmlElement(name = "rc_insurance_policy_no")
    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    @XmlElement(name = "rc_insurance_upto")
    public String getInsuranceUpto() {
        return insuranceUpto;
    }

    public void setInsuranceUpto(String insuranceUpto) {
        this.insuranceUpto = insuranceUpto;
    }

    @XmlElement(name = "rc_no_cyl")
    public String getNoCyl() {
        return noCyl;
    }

    public void setNoCyl(String noCyl) {
        this.noCyl = noCyl;
    }

    @XmlElement(name = "rc_cubic_cap")
    public String getCubicCapacity() {
        return cubicCapacity;
    }

    public void setCubicCapacity(String cubicCapacity) {
        this.cubicCapacity = cubicCapacity;
    }

    @XmlElement(name = "rc_regn_dt")
    public Integer getSleeperCapacity() {
        return sleeperCapacity;
    }

    public void setSleeperCapacity(Integer sleeperCapacity) {
        this.sleeperCapacity = sleeperCapacity;
    }

    @XmlElement(name = "rc_sleeper_cap")
    public Integer getStandCapacity() {
        return standCapacity;
    }

    public void setStandCapacity(Integer standCapacity) {
        this.standCapacity = standCapacity;
    }

    @XmlElement(name = "rc_wheelbase")
    public String getWheelBase() {
        return wheelBase;
    }

    public void setWheelBase(String wheelBase) {
        this.wheelBase = wheelBase;
    }

    @XmlElement(name = "rc_registered_at")
    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    @XmlElement(name = "rc_status_as_on")
    public String getStatusAsOn() {
        return statusAsOn;
    }

    public void setStatusAsOn(String statusAsOn) {
        this.statusAsOn = statusAsOn;
    }


}
