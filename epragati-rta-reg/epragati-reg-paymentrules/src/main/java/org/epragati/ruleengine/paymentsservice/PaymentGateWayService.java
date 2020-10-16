package org.epragati.ruleengine.paymentsservice;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.epragati.constants.OwnerTypeEnum;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxDetailsDTO;
import org.epragati.master.vo.StagingRegistrationDetailsVO;
import org.epragati.payments.vo.BreakPaymentsSaveVO;
import org.epragati.payments.vo.CFMSEodReportVO;
import org.epragati.payments.vo.CitizenPaymentReportVO;
import org.epragati.payments.vo.ClassOfVehiclesVO;
import org.epragati.payments.vo.FeeDetailInput;
import org.epragati.payments.vo.FeeDetailsVO;
import org.epragati.payments.vo.PaymentFailureResultVO;
import org.epragati.payments.vo.PaymentGateWayResponse;
import org.epragati.payments.vo.PaymentReqParams;
import org.epragati.payments.vo.TransactionDetailVO;
import org.epragati.regservice.vo.RegServiceVO;
import org.epragati.util.payment.PayStatusEnum;
import org.epragati.util.payment.ServiceEnum;

public interface PaymentGateWayService {

	Optional<?> prepareRequestObject(TransactionDetailVO transactionDetailVO, Object newParam, String collectionType);

	PaymentGateWayResponse processResponse(PaymentGateWayResponse paymentGateWayResponse, boolean isRequestFromCitizen);

	PaymentGateWayResponse processVerify(String appFormID, Boolean isAgreeToEnablePayment, String paymentTransactionNo);

	PaymentReqParams convertPayments(TransactionDetailVO vo, String appFormNo);

	TransactionDetailVO taxIntegration(StagingRegistrationDetailsDTO stagingRegistrationDetailsDTO,
			TransactionDetailVO transactionDetailVO);

	// void updatePaymentStatus(PayStatusEnum payStatus, String applicationNo);

	String dncryptSBIData(String data);

	FeeDetailsVO getServiceFee(FeeDetailInput feeDetailInput, TransactionDetailVO transactionDetailVO);

	Map<String, String[]> getSliptingofSbiValue(String value);

	BreakPaymentsSaveVO breakPayments(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum, String weightType,
			Long taxAmount, Long cesFee, String taxType, Boolean isRtoSecRejected, Boolean isRtoIvcnRejected,
			OwnerTypeEnum ownerType, String officeCode, String applicationNumber);

	void updateCitizenPaymentStatus(PayStatusEnum payStatus, String applicationNo, String moduleCode);

	Optional<StagingRegistrationDetailsVO> generateTrNoForPaymentSuccess(String applicationNo);

	TaxDetailsDTO saveCitizenTaxDetails(RegServiceVO regServiceDTO, boolean secoundVehicleDiffTaxPaid,
			boolean isChassisVehicle, String stateCode);

	Optional<PaymentFailureResultVO> getPaymentDetailForFailueTransaction(String applicationNumber);

	FeeDetailsVO getCitizenServiceFee(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum, String weightType,
			boolean isRequestToPay, String taxType, boolean isCalculateFc, boolean isRtoIvcnRejected,
			boolean isChassesVehicle, String officeCode, String applicationNumber, boolean isOtherState,
			String regApplicationNo, String permitType, LocalDate slotDate, String seatingCapacity, String routeCode,
			Boolean isWeightAlt);

	TransactionDetailVO getTransactionDetailsForPayments(StagingRegistrationDetailsDTO stagingDetails,
			TransactionDetailVO transactionDetailVO);

	String paymentsVerifiactionInStaging(StagingRegistrationDetailsDTO stagingDetails, boolean verifyPaymentFlag);

	void paymentIntiationVerification(String applicationStatus, String applicationNo);

	Object convertPaymentsForCFMS(TransactionDetailVO transactionDetailVO, String formNumber);

	void verifyPaymentStatus(String applicationNo, String paymentTransactionNo);

	/**
	 * CFMS EOD Report
	 * 
	 * @param fromDate
	 * @return
	 */
	CFMSEodReportVO getCfmsEodReportByDate(LocalDate fromDate);

	/**
	 * Get Payment details by Application Number
	 * 
	 * @param applicationNumber
	 * @return
	 */
	CitizenPaymentReportVO getPaymentDetailsByApplicationNumber(String applicationNumber);

	/**
	 * 
	 * @param covs
	 * @param serviceEnum
	 * @param weightType
	 * @param isRequestToPay
	 * @param taxType
	 * @param isCalculateFc
	 * @param isApplicationFromMvi
	 * @param isChassesVehicle
	 * @param officeCode
	 * @param applicationNumber
	 * @param isOtherState
	 * @param regApplicationNo
	 * @param permitTypeCode
	 * @param slotDate
	 * @param seatingCapacity
	 * @param routeCode
	 * @param isWeightAlt
	 * @return
	 * @throws FileNotFoundException 
	 */
	FeeDetailsVO getCitizenServiceFees(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum, String weightType,
			boolean isRequestToPay, String taxType, boolean isCalculateFc, boolean isApplicationFromMvi,
			boolean isChassesVehicle, String officeCode, String applicationNumber, boolean isOtherState,
			String regApplicationNo, String permitTypeCode, LocalDate slotDate, String seatingCapacity,
			String routeCode, Boolean isWeightAlt) throws FileNotFoundException;

}
