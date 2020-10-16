package org.epragati.vahan.unregistered.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ChassisInfoDobj")
public class UnregisteredChassisInfo {

    private String bodyTypeDesc;
    private String chassisNumber;
    private String color;
    private Double cubicCapacity;
    private String engineNumber;
    private Double enginePower;
    private String frontAxleDesc;
    private Double frontAxleWeight;
    private String fuelDesc;
    private Long gvw;
    private Long height;
    private Long length;
    private String makersDesc;
    private String makersModel;
    private String manufacturedMonthYear;
    private Integer noCyl;
    private String pollutionNormsDesc;
    private String o1AxleDesc;
    private Integer o1AxleWeight;
    private String o2AxleDesc;
    private Integer o2AxleWeight;
    private String o3AxleDesc;
    private Integer o3AxleWeight;
    private String o4AxleDesc;
    private Integer o4AxleWeight;
    private String o5AxleDesc;
    private Integer o5AxleWeight;
    private String rearAxleDesc;
    private Integer rearAxleWeight;
    private Long unladenWeight;
    private String vehicleClass;
    private Integer wheelbase;
    private Long width;
    private Integer seatingCapacity;
    private Integer standCapacity;
    private String tandemAxelDescp;
    private Integer tandemAxelWeight;
    private String exShowroomPrice;

    @XmlElement(name = "body_type")
    public String getBodyTypeDesc() {
        return bodyTypeDesc;
    }

    public void setBodyTypeDesc(String bodyTypeDesc) {
        this.bodyTypeDesc = bodyTypeDesc;
    }

    @XmlElement(name = "chasi_no")
    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    @XmlElement(name = "color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @XmlElement(name = "cubic_cap")
    public Double getCubicCapacity() {
        return cubicCapacity;
    }

    public void setCubicCapacity(Double cubicCapacity) {
        this.cubicCapacity = cubicCapacity;
    }

    @XmlElement(name = "eng_no")
    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    @XmlElement(name = "engine_power")
    public Double getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Double enginePower) {
        this.enginePower = enginePower;
    }

    @XmlElement(name = "f_axle_descp")
    public String getFrontAxleDesc() {
        return frontAxleDesc;
    }

    public void setFrontAxleDesc(String frontAxleDesc) {
        this.frontAxleDesc = frontAxleDesc;
    }

    @XmlElement(name = "f_axle_weight")
    public Double getFrontAxleWeight() {
        return frontAxleWeight;
    }

    public void setFrontAxleWeight(Double frontAxleWeight) {
        this.frontAxleWeight = frontAxleWeight;
    }

    @XmlElement(name = "fuel_descr")
    public String getFuelDesc() {
        return fuelDesc;
    }

    public void setFuelDesc(String fuelDesc) {
        this.fuelDesc = fuelDesc;
    }

    @XmlElement(name = "gvw")
    public Long getGvw() {
        return gvw;
    }

    public void setGvw(Long gvw) {
        this.gvw = gvw;
    }

    @XmlElement(name = "height")
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @XmlElement(name = "length")
    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
    
    @XmlElement(name = "maker_descr")
    public String getMakersDesc() {
        return makersDesc;
    }

    public void setMakersDesc(String makersDesc) {
        this.makersDesc = makersDesc;
    }

    @XmlElement(name = "model_name")
    public String getMakersModel() {
        return makersModel;
    }

    public void setMakersModel(String makersModel) {
        this.makersModel = makersModel;
    }

    @XmlElement(name = "monthYear")
    public String getManufacturedMonthYear() {
        return manufacturedMonthYear;
    }

    public void setManufacturedMonthYear(String manufacturedMonthYear) {
        this.manufacturedMonthYear = manufacturedMonthYear;
    }

    @XmlElement(name = "no_cyl")
    public Integer getNoCyl() {
        return noCyl;
    }

    public void setNoCyl(Integer noCyl) {
        this.noCyl = noCyl;
    }

    @XmlElement(name = "norms_descr")
    public String getPollutionNormsDesc() {
        return pollutionNormsDesc;
    }

    public void setPollutionNormsDesc(String pollutionNormsDesc) {
        this.pollutionNormsDesc = pollutionNormsDesc;
    }

    @XmlElement(name = "o1_axle_descp")
    public String getO1AxleDesc() {
        return o1AxleDesc;
    }

    public void setO1AxleDesc(String o1AxleDesc) {
        this.o1AxleDesc = o1AxleDesc;
    }

    @XmlElement(name = "o1_axle_weight")
    public Integer getO1AxleWeight() {
        return o1AxleWeight;
    }

    public void setO1AxleWeight(Integer o1AxleWeight) {
        this.o1AxleWeight = o1AxleWeight;
    }

    @XmlElement(name = "o2_axle_descp")
    public String getO2AxleDesc() {
        return o2AxleDesc;
    }

    public void setO2AxleDesc(String o2AxleDesc) {
        this.o2AxleDesc = o2AxleDesc;
    }

    @XmlElement(name = "o2_axle_weight")
    public Integer getO2AxleWeight() {
        return o2AxleWeight;
    }

    public void setO2AxleWeight(Integer o2AxleWeight) {
        this.o2AxleWeight = o2AxleWeight;
    }

    @XmlElement(name = "o3_axle_descp")
    public String getO3AxleDesc() {
        return o3AxleDesc;
    }

    public void setO3AxleDesc(String o3AxleDesc) {
        this.o3AxleDesc = o3AxleDesc;
    }

    @XmlElement(name = "o3_axle_weight")
    public Integer getO3AxleWeight() {
        return o3AxleWeight;
    }

    public void setO3AxleWeight(Integer o3AxleWeight) {
        this.o3AxleWeight = o3AxleWeight;
    }

    @XmlElement(name = "o4_axle_descp")
    public String getO4AxleDesc() {
        return o4AxleDesc;
    }

    public void setO4AxleDesc(String o4AxleDesc) {
        this.o4AxleDesc = o4AxleDesc;
    }

    @XmlElement(name = "o4_axle_weight")
    public Integer getO4AxleWeight() {
        return o4AxleWeight;
    }

    public void setO4AxleWeight(Integer o4AxleWeight) {
        this.o4AxleWeight = o4AxleWeight;
    }

    @XmlElement(name = "o5_axle_descp")
    public String getO5AxleDesc() {
        return o5AxleDesc;
    }

    public void setO5AxleDesc(String o5AxleDesc) {
        this.o5AxleDesc = o5AxleDesc;
    }

    @XmlElement(name = "o5_axle_weight")
    public Integer getO5AxleWeight() {
        return o5AxleWeight;
    }

    public void setO5AxleWeight(Integer o5AxleWeight) {
        this.o5AxleWeight = o5AxleWeight;
    }

    @XmlElement(name = "r_axle_descp")
    public String getRearAxleDesc() {
        return rearAxleDesc;
    }

    public void setRearAxleDesc(String rearAxleDesc) {
        this.rearAxleDesc = rearAxleDesc;
    }

    @XmlElement(name = "r_axle_weight")
    public Integer getRearAxleWeight() {
        return rearAxleWeight;
    }

    public void setRearAxleWeight(Integer rearAxleWeight) {
        this.rearAxleWeight = rearAxleWeight;
    }

    @XmlElement(name = "unld_wt")
    public Long getUnladenWeight() {
        return unladenWeight;
    }

    public void setUnladenWeight(Long unladenWeight) {
        this.unladenWeight = unladenWeight;
    }

    @XmlElement(name = "vh_class")
    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    @XmlElement(name = "wheelbase")
    public Integer getWheelbase() {
        return wheelbase;
    }

    public void setWheelbase(Integer wheelbase) {
        this.wheelbase = wheelbase;
    }

    @XmlElement(name = "width")
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @XmlElement(name = "seat_cap")
    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    @XmlElement(name = "stand_cap")
    public Integer getStandCapacity() {
        return standCapacity;
    }

    public void setStandCapacity(Integer standCapacity) {
        this.standCapacity = standCapacity;
    }

    @XmlElement(name = "t_axle_descp")
    public String getTandemAxelDescp() {
        return tandemAxelDescp;
    }

    public void setTandemAxelDescp(String tandemAxelDescp) {
        this.tandemAxelDescp = tandemAxelDescp;
    }

    @XmlElement(name = "t_axle_weight")
    public Integer getTandemAxelWeight() {
        return tandemAxelWeight;
    }

    public void setTandemAxelWeight(Integer tandemAxelWeight) {
        this.tandemAxelWeight = tandemAxelWeight;
    }
    
    @XmlElement(name = "ex_showroom_price")
    public String getExShowroomPrice() {
    	return exShowroomPrice;
    }
    
    public void setExShowroomPrice(String exShowroomPrice) {
    	this.exShowroomPrice = exShowroomPrice;
    }

	@Override
	public String toString() {
		return "UnregisteredChassisInfo [bodyTypeDesc=" + bodyTypeDesc + ", chassisNumber=" + chassisNumber + ", color="
				+ color + ", cubicCapacity=" + cubicCapacity + ", engineNumber=" + engineNumber + ", enginePower="
				+ enginePower + ", frontAxleDesc=" + frontAxleDesc + ", frontAxleWeight=" + frontAxleWeight
				+ ", fuelDesc=" + fuelDesc + ", gvw=" + gvw + ", height=" + height + ", length=" + length
				+ ", makersDesc=" + makersDesc + ", makersModel=" + makersModel + ", manufacturedMonthYear="
				+ manufacturedMonthYear + ", noCyl=" + noCyl + ", pollutionNormsDesc=" + pollutionNormsDesc
				+ ", o1AxleDesc=" + o1AxleDesc + ", o1AxleWeight=" + o1AxleWeight + ", o2AxleDesc=" + o2AxleDesc
				+ ", o2AxleWeight=" + o2AxleWeight + ", o3AxleDesc=" + o3AxleDesc + ", o3AxleWeight=" + o3AxleWeight
				+ ", o4AxleDesc=" + o4AxleDesc + ", o4AxleWeight=" + o4AxleWeight + ", o5AxleDesc=" + o5AxleDesc
				+ ", o5AxleWeight=" + o5AxleWeight + ", rearAxleDesc=" + rearAxleDesc + ", rearAxleWeight="
				+ rearAxleWeight + ", unladenWeight=" + unladenWeight + ", vehicleClass=" + vehicleClass
				+ ", wheelbase=" + wheelbase + ", width=" + width + ", seatingCapacity=" + seatingCapacity
				+ ", standCapacity=" + standCapacity + ", tandemAxelDescp=" + tandemAxelDescp + ", tandemAxelWeight="
				+ tandemAxelWeight + ", exShowroomPrice=" + exShowroomPrice + "]";
	}

}
