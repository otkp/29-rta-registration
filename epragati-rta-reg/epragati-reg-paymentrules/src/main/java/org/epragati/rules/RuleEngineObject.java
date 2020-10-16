package org.epragati.rules;

import java.time.LocalDate;
import java.util.List;

import org.epragati.constants.OwnerTypeEnum;
import org.epragati.master.dto.MasterAmountSecoundCovsDTO;
import org.epragati.master.dto.MasterPayperiodDTO;
import org.epragati.master.dto.MasterTax;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxHelper;
import org.epragati.master.dto.TrailerChassisDetailsDTO;
import org.epragati.regservice.dto.AlterationDTO;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.util.payment.OtherStateApplictionType;
import org.epragati.util.payment.ServiceEnum;

public class RuleEngineObject {

	private List<Integer> serviceIds;

	private String prNo;

	private String aadharNo;

	private String newCov;

	private String newVehicleType;

	private String oldVehicleType;

	private String taxType;

	private boolean isChassisApplication;

	private String trNo;

	private boolean isApplicationFromMvi;

	private boolean isOtherState;

	private String applicationNo;

	private String permitType= "INA";

	private Boolean isOtherStationFc = Boolean.FALSE;

	private String slotDate;

	private String routeCode;

	private Boolean aadhaarvalidationRequired = Boolean.TRUE;

	private Boolean isweightAlt = Boolean.FALSE;

	private AlterationDTO alterationDetails;

	private StagingRegistrationDetailsDTO stagingDetails;

	private RegServiceDTO regServiceDetails;

	private RegistrationDetailsDTO regDetails;

	private String payperiod;

	private List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO;

	private boolean requestFromcitizen;
	private Boolean goStatus = Boolean.FALSE;
	private Boolean needToTakeQuaterTax = Boolean.FALSE;

	private String classOfVehicle;
	private String vehicleType;

	private String seatingCapacity;
	private Integer gvw;
	private Integer ulw;
	private String stateCode;
	private String oldPermitcode = "INA";
	private String oldrouteCode;
	private String fuelType;
	private LocalDate trGeneratedDate;
	private String makersModel;
	private OwnerTypeEnum ownerType;
	private String taxCalBasedOn;
	private Boolean taxExcemption = Boolean.FALSE;
	private LocalDate currentQuaterTaxUpTo;
	private boolean isBelongToPermitService = Boolean.FALSE;
	private List<ServiceEnum> serviceEnum;
	private MasterTax masterTax;
	private Integer indexPosition;
	private Integer quaternNumber;
	private Double quaterTax;
	private List<TrailerChassisDetailsDTO> trailerDetails;
	private boolean isAnypendingQuaters;
	private LocalDate lastPaidTaxvalidityTo;
	private String lastPaidTaxType;
	private String oldClassOfVehicle;
	private Integer oldGvw;
	private Integer oldUlw;
	private String oldSeatingCapacity;
	private boolean isNeedtoCalOldCovTax = Boolean.TRUE; 
	private Double oldQuaterTax;
	private boolean isBelongToNewPermitService = Boolean.FALSE; 
	private LocalDate prGeneratedDate;
	private double vehicleAge;
	private LocalDate entryDate = LocalDate.now();
	private Double currentQuaterTax= 0d;
	private Double taxArrears= 0d;
	private Long penalty = 0l;
	private Long penaltyArrears= 0l;
	private Long quaterTaxForNewGo;
	private boolean isVehicleStoppageRevoked;
	private boolean isEibtVehicle;
	private boolean isVcr = Boolean.FALSE;
	private boolean isVehicleSized = Boolean.FALSE;
	private LocalDate sizedDate;
	private TaxHelper taxHelper;
	private boolean isNeedToCalObtTax;
	private String citizenapplicationNo;
	private boolean isOtherStateChassisVeh;
	private boolean ispaisdThroughVCR = Boolean.FALSE;
	private OtherStateApplictionType applicationType;
	private Double totalLifeTax;
	private boolean otherStateLifeTax = Boolean.FALSE;
	private Float Percent;
	private boolean penalityForotherState = Boolean.FALSE;
	private  Long roundTax = 0l;
	/**
	 * @return the serviceIds
	 */
	public List<Integer> getServiceIds() {
		return serviceIds;
	}

	/**
	 * @param serviceIds
	 *            the serviceIds to set
	 */
	public void setServiceIds(List<Integer> serviceIds) {
		this.serviceIds = serviceIds;
	}

	/**
	 * @return the prNo
	 */
	public String getPrNo() {
		return prNo;
	}

	/**
	 * @param prNo
	 *            the prNo to set
	 */
	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}

	/**
	 * @return the aadharNo
	 */
	public String getAadharNo() {
		return aadharNo;
	}

	/**
	 * @param aadharNo
	 *            the aadharNo to set
	 */
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	/**
	 * @return the newCov
	 */
	public String getNewCov() {
		return newCov;
	}

	/**
	 * @param newCov
	 *            the newCov to set
	 */
	public void setNewCov(String newCov) {
		this.newCov = newCov;
	}

	/**
	 * @return the newVehicleType
	 */
	public String getNewVehicleType() {
		return newVehicleType;
	}

	/**
	 * @param newVehicleType
	 *            the newVehicleType to set
	 */
	public void setNewVehicleType(String newVehicleType) {
		this.newVehicleType = newVehicleType;
	}

	/**
	 * @return the oldVehicleType
	 */
	public String getOldVehicleType() {
		return oldVehicleType;
	}

	/**
	 * @param oldVehicleType
	 *            the oldVehicleType to set
	 */
	public void setOldVehicleType(String oldVehicleType) {
		this.oldVehicleType = oldVehicleType;
	}

	/**
	 * @return the taxType
	 */
	public String getTaxType() {
		return taxType;
	}

	/**
	 * @param taxType
	 *            the taxType to set
	 */
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	/**
	 * @return the isChassisApplication
	 */
	public boolean isChassisApplication() {
		return isChassisApplication;
	}

	/**
	 * @param isChassisApplication
	 *            the isChassisApplication to set
	 */
	public void setChassisApplication(boolean isChassisApplication) {
		this.isChassisApplication = isChassisApplication;
	}

	/**
	 * @return the trNo
	 */
	public String getTrNo() {
		return trNo;
	}

	/**
	 * @param trNo
	 *            the trNo to set
	 */
	public void setTrNo(String trNo) {
		this.trNo = trNo;
	}

	/**
	 * @return the isApplicationFromMvi
	 */
	public boolean isApplicationFromMvi() {
		return isApplicationFromMvi;
	}

	/**
	 * @param isApplicationFromMvi
	 *            the isApplicationFromMvi to set
	 */
	public void setApplicationFromMvi(boolean isApplicationFromMvi) {
		this.isApplicationFromMvi = isApplicationFromMvi;
	}

	/**
	 * @return the isOtherState
	 */
	public boolean isOtherState() {
		return isOtherState;
	}

	/**
	 * @param isOtherState
	 *            the isOtherState to set
	 */
	public void setOtherState(boolean isOtherState) {
		this.isOtherState = isOtherState;
	}

	/**
	 * @return the applicationNo
	 */
	public String getApplicationNo() {
		return applicationNo;
	}

	/**
	 * @param applicationNo
	 *            the applicationNo to set
	 */
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	/**
	 * @return the permitType
	 */
	public String getPermitType() {
		return permitType;
	}

	/**
	 * @param permitType
	 *            the permitType to set
	 */
	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	/**
	 * @return the isOtherStationFc
	 */
	public Boolean getIsOtherStationFc() {
		return isOtherStationFc;
	}

	/**
	 * @param isOtherStationFc
	 *            the isOtherStationFc to set
	 */
	public void setIsOtherStationFc(Boolean isOtherStationFc) {
		this.isOtherStationFc = isOtherStationFc;
	}

	/**
	 * @return the slotDate
	 */
	public String getSlotDate() {
		return slotDate;
	}

	/**
	 * @param slotDate
	 *            the slotDate to set
	 */
	public void setSlotDate(String slotDate) {
		this.slotDate = slotDate;
	}

	/**
	 * @return the routeCode
	 */
	public String getRouteCode() {
		return routeCode;
	}

	/**
	 * @param routeCode
	 *            the routeCode to set
	 */
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	/**
	 * @return the aadhaarvalidationRequired
	 */
	public Boolean getAadhaarvalidationRequired() {
		return aadhaarvalidationRequired;
	}

	/**
	 * @param aadhaarvalidationRequired
	 *            the aadhaarvalidationRequired to set
	 */
	public void setAadhaarvalidationRequired(Boolean aadhaarvalidationRequired) {
		this.aadhaarvalidationRequired = aadhaarvalidationRequired;
	}

	/**
	 * @return the isweightAlt
	 */
	public Boolean getIsweightAlt() {
		return isweightAlt;
	}

	/**
	 * @param isweightAlt
	 *            the isweightAlt to set
	 */
	public void setIsweightAlt(Boolean isweightAlt) {
		this.isweightAlt = isweightAlt;
	}

	/**
	 * @return the alterationDetails
	 */
	public AlterationDTO getAlterationDetails() {
		return alterationDetails;
	}

	/**
	 * @param alterationDetails
	 *            the alterationDetails to set
	 */
	public void setAlterationDetails(AlterationDTO alterationDetails) {
		this.alterationDetails = alterationDetails;
	}

	/**
	 * @return the stagingDetails
	 */
	public StagingRegistrationDetailsDTO getStagingDetails() {
		return stagingDetails;
	}

	/**
	 * @param stagingDetails
	 *            the stagingDetails to set
	 */
	public void setStagingDetails(StagingRegistrationDetailsDTO stagingDetails) {
		this.stagingDetails = stagingDetails;
	}

	/**
	 * @return the regServiceDetails
	 */
	public RegServiceDTO getRegServiceDetails() {
		return regServiceDetails;
	}

	/**
	 * @param regServiceDetails
	 *            the regServiceDetails to set
	 */
	public void setRegServiceDetails(RegServiceDTO regServiceDetails) {
		this.regServiceDetails = regServiceDetails;
	}

	/**
	 * @return the regDetails
	 */
	public RegistrationDetailsDTO getRegDetails() {
		return regDetails;
	}

	/**
	 * @param regDetails
	 *            the regDetails to set
	 */
	public void setRegDetails(RegistrationDetailsDTO regDetails) {
		this.regDetails = regDetails;
	}



	public String getPayperiod() {
		return payperiod;
	}

	public void setPayperiod(String payperiod) {
		this.payperiod = payperiod;
	}

	/**
	 * @return the goStatus
	 */
	public boolean isGoStatus() {
		return goStatus;
	}

	/**
	 * @param goStatus
	 *            the goStatus to set
	 */
	public void setGoStatus(boolean goStatus) {
		this.goStatus = goStatus;
	}

	/**
	 * @return the masterAmountSecoundCovsDTO
	 */
	public List<MasterAmountSecoundCovsDTO> getMasterAmountSecoundCovsDTO() {
		return masterAmountSecoundCovsDTO;
	}

	/**
	 * @param masterAmountSecoundCovsDTO
	 *            the masterAmountSecoundCovsDTO to set
	 */
	public void setMasterAmountSecoundCovsDTO(List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO) {
		this.masterAmountSecoundCovsDTO = masterAmountSecoundCovsDTO;
	}

	public boolean isRequestFromcitizen() {
		return requestFromcitizen;
	}

	public void setRequestFromcitizen(boolean requestFromcitizen) {
		this.requestFromcitizen = requestFromcitizen;
	}

	public Boolean getGoStatus() {
		return goStatus;
	}

	public void setGoStatus(Boolean goStatus) {
		this.goStatus = goStatus;
	}

	public Boolean getNeedToTakeQuaterTax() {
		return needToTakeQuaterTax;
	}

	public void setNeedToTakeQuaterTax(Boolean needToTakeQuaterTax) {
		this.needToTakeQuaterTax = needToTakeQuaterTax;
	}

	public String getClassOfVehicle() {
		return classOfVehicle;
	}

	public void setClassOfVehicle(String classOfVehicle) {
		this.classOfVehicle = classOfVehicle;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getSeatingCapacity() {
		return seatingCapacity;
	}

	public void setSeatingCapacity(String seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	public Integer getGvw() {
		return gvw;
	}

	public void setGvw(Integer gvw) {
		this.gvw = gvw;
	}

	public Integer getUlw() {
		return ulw;
	}

	public void setUlw(Integer ulw) {
		this.ulw = ulw;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getOldPermitcode() {
		return oldPermitcode;
	}

	public void setOldPermitcode(String oldPermitcode) {
		this.oldPermitcode = oldPermitcode;
	}

	public String getOldrouteCode() {
		return oldrouteCode;
	}

	public void setOldrouteCode(String oldrouteCode) {
		this.oldrouteCode = oldrouteCode;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public LocalDate getTrGeneratedDate() {
		return trGeneratedDate;
	}

	public void setTrGeneratedDate(LocalDate trGeneratedDate) {
		this.trGeneratedDate = trGeneratedDate;
	}

	public String getMakersModel() {
		return makersModel;
	}

	public void setMakersModel(String makersModel) {
		this.makersModel = makersModel;
	}

	public OwnerTypeEnum getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(OwnerTypeEnum ownerType) {
		this.ownerType = ownerType;
	}

	public String getTaxCalBasedOn() {
		return taxCalBasedOn;
	}

	public void setTaxCalBasedOn(String taxCalBasedOn) {
		this.taxCalBasedOn = taxCalBasedOn;
	}

	public Boolean getTaxExcemption() {
		return taxExcemption;
	}

	public void setTaxExcemption(Boolean taxExcemption) {
		this.taxExcemption = taxExcemption;
	}

	public LocalDate getCurrentQuaterTaxUpTo() {
		return currentQuaterTaxUpTo;
	}

	public void setCurrentQuaterTaxUpTo(LocalDate currentQuaterTaxUpTo) {
		this.currentQuaterTaxUpTo = currentQuaterTaxUpTo;
	}

	public boolean isBelongToPermitService() {
		return isBelongToPermitService;
	}

	public void setBelongToPermitService(boolean isBelongToPermitService) {
		this.isBelongToPermitService = isBelongToPermitService;
	}

	public List<ServiceEnum> getServiceEnum() {
		return serviceEnum;
	}

	public void setServiceEnum(List<ServiceEnum> serviceEnum) {
		this.serviceEnum = serviceEnum;
	}

	public MasterTax getMasterTax() {
		return masterTax;
	}

	public void setMasterTax(MasterTax masterTax) {
		this.masterTax = masterTax;
	}

	public Integer getIndexPosition() {
		return indexPosition;
	}

	public void setIndexPosition(Integer indexPosition) {
		this.indexPosition = indexPosition;
	}

	public Integer getQuaternNumber() {
		return quaternNumber;
	}

	public void setQuaternNumber(Integer quaternNumber) {
		this.quaternNumber = quaternNumber;
	}

	public Double getQuaterTax() {
		return quaterTax;
	}

	public void setQuaterTax(Double quaterTax) {
		this.quaterTax = quaterTax;
	}

	public List<TrailerChassisDetailsDTO> getTrailerDetails() {
		return trailerDetails;
	}

	public void setTrailerDetails(List<TrailerChassisDetailsDTO> trailerDetails) {
		this.trailerDetails = trailerDetails;
	}

	public boolean isAnypendingQuaters() {
		return isAnypendingQuaters;
	}

	public void setAnypendingQuaters(boolean isAnypendingQuaters) {
		this.isAnypendingQuaters = isAnypendingQuaters;
	}

	public LocalDate getLastPaidTaxvalidityTo() {
		return lastPaidTaxvalidityTo;
	}

	public void setLastPaidTaxvalidityTo(LocalDate lastPaidTaxvalidityTo) {
		this.lastPaidTaxvalidityTo = lastPaidTaxvalidityTo;
	}

	public String getLastPaidTaxType() {
		return lastPaidTaxType;
	}

	public void setLastPaidTaxType(String lastPaidTaxType) {
		this.lastPaidTaxType = lastPaidTaxType;
	}

	public String getOldClassOfVehicle() {
		return oldClassOfVehicle;
	}

	public void setOldClassOfVehicle(String oldClassOfVehicle) {
		this.oldClassOfVehicle = oldClassOfVehicle;
	}

	public Integer getOldGvw() {
		return oldGvw;
	}

	public void setOldGvw(Integer oldGvw) {
		this.oldGvw = oldGvw;
	}

	public Integer getOldUlw() {
		return oldUlw;
	}

	public void setOldUlw(Integer oldUlw) {
		this.oldUlw = oldUlw;
	}

	public String getOldSeatingCapacity() {
		return oldSeatingCapacity;
	}

	public void setOldSeatingCapacity(String oldSeatingCapacity) {
		this.oldSeatingCapacity = oldSeatingCapacity;
	}

	public boolean isNeedtoCalOldCovTax() {
		return isNeedtoCalOldCovTax;
	}

	public void setNeedtoCalOldCovTax(boolean isNeedtoCalOldCovTax) {
		this.isNeedtoCalOldCovTax = isNeedtoCalOldCovTax;
	}

	public Double getOldQuaterTax() {
		return oldQuaterTax;
	}

	public void setOldQuaterTax(Double oldQuaterTax) {
		this.oldQuaterTax = oldQuaterTax;
	}

	public boolean isBelongToNewPermitService() {
		return isBelongToNewPermitService;
	}

	public void setBelongToNewPermitService(boolean isBelongToNewPermitService) {
		this.isBelongToNewPermitService = isBelongToNewPermitService;
	}

	public LocalDate getPrGeneratedDate() {
		return prGeneratedDate;
	}

	public void setPrGeneratedDate(LocalDate prGeneratedDate) {
		this.prGeneratedDate = prGeneratedDate;
	}

	public double getVehicleAge() {
		return vehicleAge;
	}

	public void setVehicleAge(double vehicleAge) {
		this.vehicleAge = vehicleAge;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public Double getCurrentQuaterTax() {
		return currentQuaterTax;
	}

	public void setCurrentQuaterTax(Double currentQuaterTax) {
		this.currentQuaterTax = currentQuaterTax;
	}

	public Double getTaxArrears() {
		return taxArrears;
	}

	public void setTaxArrears(Double taxArrears) {
		this.taxArrears = taxArrears;
	}

	public Long getPenalty() {
		return penalty;
	}

	public void setPenalty(Long penalty) {
		this.penalty = penalty;
	}

	public Long getPenaltyArrears() {
		return penaltyArrears;
	}

	public void setPenaltyArrears(Long penaltyArrears) {
		this.penaltyArrears = penaltyArrears;
	}

	public Long getQuaterTaxForNewGo() {
		return quaterTaxForNewGo;
	}

	public void setQuaterTaxForNewGo(Long quaterTaxForNewGo) {
		this.quaterTaxForNewGo = quaterTaxForNewGo;
	}

	public boolean isVehicleStoppageRevoked() {
		return isVehicleStoppageRevoked;
	}

	public void setVehicleStoppageRevoked(boolean isVehicleStoppageRevoked) {
		this.isVehicleStoppageRevoked = isVehicleStoppageRevoked;
	}

	public boolean isEibtVehicle() {
		return isEibtVehicle;
	}

	public void setEibtVehicle(boolean isEibtVehicle) {
		this.isEibtVehicle = isEibtVehicle;
	}

	public boolean isVcr() {
		return isVcr;
	}

	public void setVcr(boolean isVcr) {
		this.isVcr = isVcr;
	}

	public boolean isVehicleSized() {
		return isVehicleSized;
	}

	public void setVehicleSized(boolean isVehicleSized) {
		this.isVehicleSized = isVehicleSized;
	}

	public LocalDate getSizedDate() {
		return sizedDate;
	}

	public void setSizedDate(LocalDate sizedDate) {
		this.sizedDate = sizedDate;
	}

	public TaxHelper getTaxHelper() {
		return taxHelper;
	}

	public void setTaxHelper(TaxHelper taxHelper) {
		this.taxHelper = taxHelper;
	}

	public boolean isNeedToCalObtTax() {
		return isNeedToCalObtTax;
	}

	public void setNeedToCalObtTax(boolean isNeedToCalObtTax) {
		this.isNeedToCalObtTax = isNeedToCalObtTax;
	}

	public String getCitizenapplicationNo() {
		return citizenapplicationNo;
	}

	public void setCitizenapplicationNo(String citizenapplicationNo) {
		this.citizenapplicationNo = citizenapplicationNo;
	}

	public boolean isOtherStateChassisVeh() {
		return isOtherStateChassisVeh;
	}

	public void setOtherStateChassisVeh(boolean isOtherStateChassisVeh) {
		this.isOtherStateChassisVeh = isOtherStateChassisVeh;
	}

	public boolean isIspaisdThroughVCR() {
		return ispaisdThroughVCR;
	}

	public void setIspaisdThroughVCR(boolean ispaisdThroughVCR) {
		this.ispaisdThroughVCR = ispaisdThroughVCR;
	}

	public OtherStateApplictionType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(OtherStateApplictionType applicationType) {
		this.applicationType = applicationType;
	}

	public Double getTotalLifeTax() {
		return totalLifeTax;
	}

	public void setTotalLifeTax(Double totalLifeTax) {
		this.totalLifeTax = totalLifeTax;
	}

	public boolean isOtherStateLifeTax() {
		return otherStateLifeTax;
	}

	public void setOtherStateLifeTax(boolean otherStateLifeTax) {
		this.otherStateLifeTax = otherStateLifeTax;
	}

	public Float getPercent() {
		return Percent;
	}

	public void setPercent(Float percent) {
		Percent = percent;
	}

	public boolean isPenalityForotherState() {
		return penalityForotherState;
	}

	public void setPenalityForotherState(boolean penalityForotherState) {
		this.penalityForotherState = penalityForotherState;
	}

	public Long getRoundTax() {
		return roundTax;
	}

	public void setRoundTax(Long roundTax) {
		this.roundTax = roundTax;
	}

       
}
