package org.epragati.rules;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.payment.dto.FeesDTO;
import org.epragati.payments.vo.ClassOfVehiclesVO;
import org.epragati.payments.vo.FeePartsDetailsVo;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.util.payment.ServiceEnum;

public class FeePartsVO {

	private List<ClassOfVehiclesVO> covs;

	private List<ServiceEnum> serviceEnum;

	private String weightType;

	private Long taxAmount;

	private Long cesFee;

	private Long grenTaxAmount;

	private String taxType;

	boolean isCalculateFc;

	private boolean isApplicationFromMvi;

	private boolean isChassesVehicle;

	private String officeCode;

	private String applicationNumber;

	private boolean isARVTVehicle;

	private StagingRegistrationDetailsDTO stagingRegDetails;

	private Long penality;

	private String regApplicationNo;

	private Long penalityArrears;

	private Long taxArrears;

	private LocalDate slotDate;

	private String permitTypeCode;

	private String seatingCapacity;

	private Long quaterTaxForNewGo;

	private boolean isRequestToPay;

	private RegServiceDTO regServiceDetials;

	private boolean isOtherState;

	private boolean ignoreApplicationFee;

	private List<FeesDTO> feeList;

	private Map<String, FeePartsDetailsVo> feeParts;

	/**
	 * @return the covs
	 */
	public List<ClassOfVehiclesVO> getCovs() {
		return covs;
	}

	/**
	 * @param covs
	 *            the covs to set
	 */
	public void setCovs(List<ClassOfVehiclesVO> covs) {
		this.covs = covs;
	}

	/**
	 * @return the serviceEnum
	 */
	public List<ServiceEnum> getServiceEnum() {
		return serviceEnum;
	}

	/**
	 * @param serviceEnum
	 *            the serviceEnum to set
	 */
	public void setServiceEnum(List<ServiceEnum> serviceEnum) {
		this.serviceEnum = serviceEnum;
	}

	/**
	 * @return the weightType
	 */
	public String getWeightType() {
		return weightType;
	}

	/**
	 * @param weightType
	 *            the weightType to set
	 */
	public void setWeightType(String weightType) {
		this.weightType = weightType;
	}

	/**
	 * @return the taxAmount
	 */
	public Long getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount
	 *            the taxAmount to set
	 */
	public void setTaxAmount(Long taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * @return the cesFee
	 */
	public Long getCesFee() {
		return cesFee;
	}

	/**
	 * @param cesFee
	 *            the cesFee to set
	 */
	public void setCesFee(Long cesFee) {
		this.cesFee = cesFee;
	}

	/**
	 * @return the grenTaxAmount
	 */
	public Long getGrenTaxAmount() {
		return grenTaxAmount;
	}

	/**
	 * @param grenTaxAmount
	 *            the grenTaxAmount to set
	 */
	public void setGrenTaxAmount(Long grenTaxAmount) {
		this.grenTaxAmount = grenTaxAmount;
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
	 * @return the isCalculateFc
	 */
	public boolean isCalculateFc() {
		return isCalculateFc;
	}

	/**
	 * @param isCalculateFc
	 *            the isCalculateFc to set
	 */
	public void setCalculateFc(boolean isCalculateFc) {
		this.isCalculateFc = isCalculateFc;
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
	 * @return the isChassesVehicle
	 */
	public boolean isChassesVehicle() {
		return isChassesVehicle;
	}

	/**
	 * @param isChassesVehicle
	 *            the isChassesVehicle to set
	 */
	public void setChassesVehicle(boolean isChassesVehicle) {
		this.isChassesVehicle = isChassesVehicle;
	}

	/**
	 * @return the officeCode
	 */
	public String getOfficeCode() {
		return officeCode;
	}

	/**
	 * @param officeCode
	 *            the officeCode to set
	 */
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	/**
	 * @return the applicationNumber
	 */
	public String getApplicationNumber() {
		return applicationNumber;
	}

	/**
	 * @param applicationNumber
	 *            the applicationNumber to set
	 */
	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	/**
	 * @return the isARVTVehicle
	 */
	public boolean isARVTVehicle() {
		return isARVTVehicle;
	}

	/**
	 * @param isARVTVehicle
	 *            the isARVTVehicle to set
	 */
	public void setARVTVehicle(boolean isARVTVehicle) {
		this.isARVTVehicle = isARVTVehicle;
	}

	/**
	 * @return the stagingRegDetails
	 */
	public StagingRegistrationDetailsDTO getStagingRegDetails() {
		return stagingRegDetails;
	}

	/**
	 * @param stagingRegDetails
	 *            the stagingRegDetails to set
	 */
	public void setStagingRegDetails(StagingRegistrationDetailsDTO stagingRegDetails) {
		this.stagingRegDetails = stagingRegDetails;
	}

	/**
	 * @return the penality
	 */
	public Long getPenality() {
		return penality;
	}

	/**
	 * @param penality
	 *            the penality to set
	 */
	public void setPenality(Long penality) {
		this.penality = penality;
	}

	/**
	 * @return the regApplicationNo
	 */
	public String getRegApplicationNo() {
		return regApplicationNo;
	}

	/**
	 * @param regApplicationNo
	 *            the regApplicationNo to set
	 */
	public void setRegApplicationNo(String regApplicationNo) {
		this.regApplicationNo = regApplicationNo;
	}

	/**
	 * @return the penalityArrears
	 */
	public Long getPenalityArrears() {
		return penalityArrears;
	}

	/**
	 * @param penalityArrears
	 *            the penalityArrears to set
	 */
	public void setPenalityArrears(Long penalityArrears) {
		this.penalityArrears = penalityArrears;
	}

	/**
	 * @return the slotDate
	 */
	public LocalDate getSlotDate() {
		return slotDate;
	}

	/**
	 * @param slotDate
	 *            the slotDate to set
	 */
	public void setSlotDate(LocalDate slotDate) {
		this.slotDate = slotDate;
	}

	/**
	 * @return the permitTypeCode
	 */
	public String getPermitTypeCode() {
		return permitTypeCode;
	}

	/**
	 * @param permitTypeCode
	 *            the permitTypeCode to set
	 */
	public void setPermitTypeCode(String permitTypeCode) {
		this.permitTypeCode = permitTypeCode;
	}

	/**
	 * @return the seatingCapacity
	 */
	public String getSeatingCapacity() {
		return seatingCapacity;
	}

	/**
	 * @param seatingCapacity
	 *            the seatingCapacity to set
	 */
	public void setSeatingCapacity(String seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	/**
	 * @return the quaterTaxForNewGo
	 */
	public Long getQuaterTaxForNewGo() {
		return quaterTaxForNewGo;
	}

	/**
	 * @param quaterTaxForNewGo
	 *            the quaterTaxForNewGo to set
	 */
	public void setQuaterTaxForNewGo(Long quaterTaxForNewGo) {
		this.quaterTaxForNewGo = quaterTaxForNewGo;
	}

	/**
	 * @return the isRequestToPay
	 */
	public boolean isRequestToPay() {
		return isRequestToPay;
	}

	/**
	 * @param isRequestToPay
	 *            the isRequestToPay to set
	 */
	public void setRequestToPay(boolean isRequestToPay) {
		this.isRequestToPay = isRequestToPay;
	}

	/**
	 * @return the regServiceDetials
	 */
	public RegServiceDTO getRegServiceDetials() {
		return regServiceDetials;
	}

	/**
	 * @param regServiceDetials
	 *            the regServiceDetials to set
	 */
	public void setRegServiceDetials(RegServiceDTO regServiceDetials) {
		this.regServiceDetials = regServiceDetials;
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
	 * @return the taxArrears
	 */
	public Long getTaxArrears() {
		return taxArrears;
	}

	/**
	 * @param taxArrears
	 *            the taxArrears to set
	 */
	public void setTaxArrears(Long taxArrears) {
		this.taxArrears = taxArrears;
	}

	/**
	 * @return the ignoreApplicationFee
	 */
	public boolean isIgnoreApplicationFee() {
		return ignoreApplicationFee;
	}

	/**
	 * @param ignoreApplicationFee
	 *            the ignoreApplicationFee to set
	 */
	public void setIgnoreApplicationFee(boolean ignoreApplicationFee) {
		this.ignoreApplicationFee = ignoreApplicationFee;
	}

	/**
	 * @return the feeList
	 */
	public List<FeesDTO> getFeeList() {
		return feeList;
	}

	/**
	 * @param feeList
	 *            the feeList to set
	 */
	public void setFeeList(List<FeesDTO> feeList) {
		this.feeList = feeList;
	}

	/**
	 * @return the feeParts
	 */
	public Map<String, FeePartsDetailsVo> getFeeParts() {
		return feeParts;
	}

	/**
	 * @param feeParts
	 *            the feeParts to set
	 */
	public void setFeeParts(Map<String, FeePartsDetailsVo> feeParts) {
		this.feeParts = feeParts;
	}

}
