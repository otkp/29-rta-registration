package org.epragati.ruleengine.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.epragati.constants.CovCategory;
import org.epragati.constants.MessageKeys;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.AlterationDAO;
import org.epragati.master.dao.ClassOfVehicleConversionDAO;
import org.epragati.master.dao.FcDetailsDAO;
import org.epragati.master.dao.GateWayDAO;
import org.epragati.master.dao.StagingRegistrationDetailsDAO;
import org.epragati.master.dto.FcDetailsDTO;
import org.epragati.master.dto.GateWayDTO;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TrailerChassisDetailsDTO;
import org.epragati.master.service.CovService;
import org.epragati.master.vo.RegistrationDetailsVO;
import org.epragati.payments.vo.CitizenPaymentReportVO;
import org.epragati.payments.vo.ClassOfVehiclesVO;
import org.epragati.payments.vo.FeeDetailsVO;
import org.epragati.payments.vo.PaymentGateWayResponse;
import org.epragati.regservice.CitizenTaxService;
import org.epragati.regservice.RegistrationService;
import org.epragati.regservice.dto.AlterationDTO;
import org.epragati.regservice.dto.CitizenFeeDetailsInput;
import org.epragati.regservice.dto.ClassOfVehicleConversion;
import org.epragati.regservice.vo.RegServiceVO;
import org.epragati.rta.service.impl.service.RTAService;
import org.epragati.ruleengine.paymentsservice.PaymentGateWayService;
import org.epragati.util.AppMessages;
import org.epragati.util.EncryptDecryptUtil;
import org.epragati.util.GateWayResponse;
import org.epragati.util.RequestMappingUrls;
import org.epragati.util.payment.ClassOfVehicleEnum;
import org.epragati.util.payment.GatewayTypeEnum;
import org.epragati.util.payment.GatewayTypeEnum.PayUParams;
import org.epragati.util.payment.GatewayTypeEnum.SBIParams;
import org.epragati.util.payment.ModuleEnum;
import org.epragati.util.payment.OtherStateApplictionType;
import org.epragati.util.payment.PayStatusEnum;
import org.epragati.util.payment.ServiceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(RequestMappingUrls.COD_CLASS_LEVEL_URL)
//@RequestMapping("citizenSer")

public class PaymentsController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

	@Autowired
	private PaymentGateWayService paymentGateWayService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private AppMessages appMessages;

	@Autowired
	private CovService covService;

	@Autowired
	private GateWayDAO gatewayDao;

	@Autowired
	private ClassOfVehicleConversionDAO classOfVehicleConversionDAO;

	@Autowired
	private StagingRegistrationDetailsDAO stagingRegistrationDetailsDAO;

	@Autowired
	private AlterationDAO alterationDAO;

	@Autowired
	private CitizenTaxService citizenTaxService;

	@Autowired
	private EncryptDecryptUtil encryptDecryptUtil;

	@Autowired
	private FcDetailsDAO fcDetailsDAO;

	@PostMapping(value = "getCitizenPaymentBraekup", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> getCitizenPaymentBraekup(@RequestBody @Validated CitizenFeeDetailsInput input) {
		logger.debug(" CitizebFeedDetailsInput : [{}]", input);

		String weightDetails = null;
		ClassOfVehiclesVO covDetails = null;
		RegistrationDetailsDTO regDetails = null;
		String applicationNo = null;
		String regApplicationNo = null;
		String officeCode = null;
		boolean isCalculateFc = false;
		boolean isChassesVehicle = false;
		boolean isOtherState = false;
		boolean isApplicationFromMvi = false;
		Optional<AlterationDTO> alterDetails = Optional.empty();
		List<ServiceEnum> serviceEnums = new ArrayList<>();
		FeeDetailsVO feeDetails = null;
		Integer rlw = null;
		LocalDate slotDate = null;
		Optional<StagingRegistrationDetailsDTO> stagingDetail = null;
		String seatingCapacity = null;
		try {
			serviceEnums = input.getServiceIds().stream().map(id -> {

				if (id == null) {
					throw new BadRequestException("Service Id should not be empty");
				}
				ServiceEnum se = ServiceEnum.getServiceEnumById(id);
				if (se == null) {
					throw new BadRequestException("Invalid Service Id + " + id);
				}
				return se;
			}).collect(Collectors.toList());

			if (input.getIsChassesVehicle() != null && input.getIsChassesVehicle()) {

				stagingDetail = findByTrNoFromStagingDetails(input);
				if (!stagingDetail.isPresent()) {

					throw new BadRequestException("Application not found: " + input.getTrNo());
				}
				if (!stagingDetail.get().getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
					serviceEnums.add(ServiceEnum.FR);
				}
				isChassesVehicle = Boolean.TRUE;
				if (!(stagingDetail.get().getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode())
						|| stagingDetail.get().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
						|| stagingDetail.get().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode()))) {
					throw new BadRequestException("Vehicle is not chassis: " + stagingDetail.get().getApplicationNo());
				}
				alterDetails = alterationDAO.findByApplicationNo(stagingDetail.get().getApplicationNo());
				if (!alterDetails.isPresent()) {
					throw new BadRequestException(
							"No record found in alter Details for: " + stagingDetail.get().getApplicationNo());
				}

				covDetails = covService.findByCovCode(alterDetails.get().getCov());
				officeCode = stagingDetail.get().getOfficeDetails().getOfficeCode();
				applicationNo = stagingDetail.get().getApplicationNo();
				if (stagingDetail.get().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {

					if (ClassOfVehicleEnum.ARVT.getCovCode().equalsIgnoreCase(alterDetails.get().getCov())) {
						if (alterDetails.get().getTrailers().isEmpty()) {
							throw new BadRequestException(
									"Trailers Details not found in Alteration collection for(ARVT) : "
											+ stagingDetail.get().getApplicationNo());
						}
						Integer gtw = alterDetails.get().getTrailers().stream().findFirst().get().getGtw();
						for (TrailerChassisDetailsDTO trailerDetails : alterDetails.get().getTrailers()) {
							if (trailerDetails.getGtw() > gtw) {
								gtw = trailerDetails.getGtw();
							}
						}
						rlw = stagingDetail.get().getVahanDetails().getGvw() + gtw;
					} else {
						rlw = stagingDetail.get().getVahanDetails().getGvw();
					}
					weightDetails = covService.getWeightTypeDetails(rlw);
				} else {
					weightDetails = covService.getWeightTypeDetails(alterDetails.get().getUlw());
				}

			} else if (input.getIsOtherState() != null && input.getIsOtherState()) {
				isOtherState = Boolean.TRUE;
				RegServiceVO vo = registrationService.findapplication(input.getApplicationNo());
				regApplicationNo = vo.getApplicationNo();
				covDetails = covService.findByCovCode(vo.getRegistrationDetails().getClassOfVehicle());
				isOtherState = Boolean.TRUE;
				applicationNo = vo.getApplicationNo();
				OtherStateApplictionType applicationType = citizenTaxService.getOtherStateVehicleStatus(vo);
				if (vo.getRegistrationDetails().getApplicantType() != null
						&& vo.getRegistrationDetails().getApplicantType().equalsIgnoreCase("WITHINTHESTATE")) {
					serviceEnums.add(ServiceEnum.FR);
					if (vo.getRegistrationDetails().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
						serviceEnums.add(ServiceEnum.NEWFC);
					}
				} else if (OtherStateApplictionType.ApplicationNO.equals(applicationType)) {
					serviceEnums.add(ServiceEnum.FR);
					serviceEnums.add(ServiceEnum.TEMPORARYREGISTRATION);
					if (vo.getRegistrationDetails().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
						serviceEnums.add(ServiceEnum.NEWFC);
					}
				} else if (OtherStateApplictionType.TrNo.equals(applicationType)) {
					serviceEnums.add(ServiceEnum.FR);
					if (vo.getRegistrationDetails().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
						serviceEnums.add(ServiceEnum.NEWFC);
					}
				}
				if (vo.getRegistrationDetails().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
					weightDetails = covService
							.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getRlw());
				} else {
					weightDetails = covService
							.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getUlw());
				}
			} else if (input.getIsApplicationFromMvi() != null && input.getIsApplicationFromMvi()) {
				isApplicationFromMvi = input.getIsApplicationFromMvi();
				RegServiceVO vo = registrationService.getRegServiceDetailsVo(input.getApplicationNo());
				regApplicationNo = vo.getApplicationNo();
				applicationNo = vo.getRegistrationDetails().getApplicationNo();
				if (vo.getServiceIds().stream().anyMatch(id -> id.equals(ServiceEnum.ALTERATIONOFVEHICLE.getId()))) {
					String cov = vo.getAlterationVO().getCov() != null ? vo.getAlterationVO().getCov()
							: vo.getRegistrationDetails().getClassOfVehicle();
					String vehicleType = vo.getAlterationVO().getVehicleTypeTo() != null
							? vo.getAlterationVO().getVehicleTypeTo()
							: vo.getRegistrationDetails().getVehicleType();
					Optional<ClassOfVehicleConversion> classOfVehicle = classOfVehicleConversionDAO
							.findByNewCovAndNewCategoryAndCovAndCategory(cov, vehicleType,
									vo.getRegistrationDetails().getClassOfVehicle(),
									vo.getRegistrationDetails().getVehicleType());
					if (classOfVehicle.isPresent()) {
						if (classOfVehicle.get().isFcFee()) {
							isCalculateFc = true;
						}
					}
					covDetails = covService.findByCovCode(vo.getRegistrationDetails().getClassOfVehicle());
					if (vo.getAlterationVO().getCov() != null) {
						covDetails = covService.findByCovCode(vo.getAlterationVO().getCov());
					}
					if (vo.getAlterationVO().getVehicleTypeTo() != null
							&& vo.getAlterationVO().getVehicleTypeTo().equalsIgnoreCase(CovCategory.T.getCode())) {

						weightDetails = covService
								.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getRlw());
					} else if (vo.getAlterationVO().getVehicleTypeTo() != null
							&& vo.getAlterationVO().getVehicleTypeTo().equalsIgnoreCase(CovCategory.N.getCode())) {
						weightDetails = covService
								.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getUlw());
						if (vo.getAlterationVO().getUlw() != null) {
							weightDetails = covService.getWeightTypeDetails(vo.getAlterationVO().getUlw());
						}

					} else if (vo.getRegistrationDetails().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
						weightDetails = covService
								.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getRlw());

					} else {
						weightDetails = covService
								.getWeightTypeDetails(vo.getRegistrationDetails().getVehicleDetails().getUlw());
					}
				}
			} else {
				if (serviceEnums != null
						&& serviceEnums.stream().anyMatch(type -> type.equals(ServiceEnum.BILLATERALTAX))) {
					if (StringUtils.isBlank(input.getPrNo())) {
						throw new BadRequestException("Please provide pr number for bilateral tax");
					}
					applicationNo = input.getPrNo();
					covDetails = covService.findByCovCode(input.getNewCov());
				} else {
					regDetails = registrationService.getRegDetails(input.getPrNo(), input.getAadharNo(),
							input.getAadhaarvalidationRequired());
					officeCode = regDetails.getOfficeDetails().getOfficeCode();
					applicationNo = regDetails.getApplicationNo();
					regApplicationNo = regDetails.getApplicationNo();
					if (input.getIsApplicationFromMvi() != null && input.getIsApplicationFromMvi()) {
						isApplicationFromMvi = input.getIsApplicationFromMvi();
					}
					if (serviceEnums != null
							&& serviceEnums.stream().anyMatch(service -> service.equals(ServiceEnum.NEWFC))
							&& serviceEnums.size() == 1) {
						if (input.getIsOtherStationFc() != null && input.getIsOtherStationFc()) {
							serviceEnums = Arrays.asList(ServiceEnum.OTHERSTATIONFC);
						} else {
							Optional<FcDetailsDTO> dto = fcDetailsDAO.findByPrNo(regDetails.getPrNo());
							if (dto.isPresent()) {
								serviceEnums = Arrays.asList(ServiceEnum.RENEWALFC);
							}
						}
					}
					if (input.getSlotDate() != null) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
						slotDate = LocalDate.parse(input.getSlotDate(), formatter);
					}
					if (serviceEnums != null && serviceEnums.stream()
							.anyMatch(service -> service.equals(ServiceEnum.ALTERATIONOFVEHICLE))) {
						if (input.getIsweightAlt() != null && input.getIsweightAlt()) {
							registrationService.getweights(regDetails.getApplicationNo());
							if (registrationService.isNeedtoAddVariationOfPermit(regDetails)) {
								serviceEnums.add(ServiceEnum.VARIATIONOFPERMIT);
							}
						}
					}

					if (regDetails.getVahanDetails() != null) {
						if (regDetails.getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
							weightDetails = covService
									.getWeightTypeDetails(citizenTaxService.getGvwWeightForCitizen(regDetails));
						} else {
							weightDetails = covService.getWeightTypeDetails(regDetails.getVehicleDetails().getUlw());
						}
					}
					if (regDetails.getVahanDetails().getSeatingCapacity() != null) {
						seatingCapacity = regDetails.getVahanDetails().getSeatingCapacity();
					}
				}
			}
			if (covDetails == null) {
				covDetails = covService.findByCovCode(regDetails.getClassOfVehicle());
			}
			feeDetails = paymentGateWayService.getCitizenServiceFee(Arrays.asList(covDetails), serviceEnums,
					weightDetails, Boolean.FALSE, input.getTaxType(), isCalculateFc, isApplicationFromMvi,
					isChassesVehicle, officeCode, applicationNo, isOtherState, regApplicationNo, input.getPermitType(),
					slotDate, seatingCapacity, input.getRouteCode(), input.getIsweightAlt());

			return new GateWayResponse<>(HttpStatus.OK, feeDetails,
					appMessages.getResponseMessage(MessageKeys.MESSAGE_SUCCESS));
		} catch (BadRequestException ex) {
			logger.error("{}", ex.getMessage());
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
		} catch (Exception e) {
			logger.error(appMessages.getLogMessage(MessageKeys.CITIZEN_SERVICE_COMBINATION_FAILURE), e);
			return new GateWayResponse<>(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
		}
	}

	private Optional<StagingRegistrationDetailsDTO> findByTrNoFromStagingDetails(CitizenFeeDetailsInput input) {
		return stagingRegistrationDetailsDAO.findByTrNo(input.getTrNo());

	}

	@PostMapping(path = "/payUPaymentSuccess", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public void doPaymentSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GateWayDTO gatewayValue = gatewayDao.findByGateWayType(GatewayTypeEnum.PAYU);
		@SuppressWarnings("unused")
		Map<String, String> gatewayDetails = null;
		try {
			Map<String, String[]> data = request.getParameterMap();
			PaymentGateWayResponse paymentGateWayResponse = new PaymentGateWayResponse();
			paymentGateWayResponse.setGatewayResponceMap(data);
			paymentGateWayResponse.setGatewayTypeEnum(GatewayTypeEnum.PAYU);
			paymentGateWayResponse = paymentGateWayService.processResponse(paymentGateWayResponse, Boolean.TRUE);
			paymentGateWayService.updateCitizenPaymentStatus(paymentGateWayResponse.getPaymentStatus(),
					paymentGateWayResponse.getAppTransNo(), paymentGateWayResponse.getModuleCode());
			String an = URLEncoder.encode(encryptDecryptUtil.encrypt(paymentGateWayResponse.getAppTransNo()), "UTF-8");
			if (paymentGateWayResponse.getGatewayTypeEnum().equals(GatewayTypeEnum.PAYU)) {
				GateWayDTO paymentGateValue = gatewayDao.findByGateWayType(GatewayTypeEnum.PAYU);
				Map<String, String> payUvalue = paymentGateValue.getGatewayDetails();
				response.sendRedirect(payUvalue.get(PayUParams.CITIZEN_SUCESS_URL_UI.getParamKey()) + "?an=" + an
						+ "&module=" + ModuleEnum.REG);

			}
		} catch (Exception e) {
			gatewayDetails = gatewayValue.getGatewayDetails();
			logger.error("Exception while processing the  payment :{e}", e);
			// response.sendRedirect(gatewayDetails.get(PayUParams.PAYMENT_PENDING_URL));
		}
	}

	@PostMapping(path = "/payUPaymentFailed", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public void doPaymentFailed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GateWayDTO gatewayValue = gatewayDao.findByGateWayType(GatewayTypeEnum.PAYU);

		@SuppressWarnings("unused")
		Map<String, String> gatewayDetails = null;
		try {
			Map<String, String[]> data = request.getParameterMap();
			PaymentGateWayResponse paymentGateWayResponse = new PaymentGateWayResponse();
			paymentGateWayResponse.setGatewayResponceMap(data);
			paymentGateWayResponse.setGatewayTypeEnum(GatewayTypeEnum.PAYU);
			paymentGateWayResponse = paymentGateWayService.processResponse(paymentGateWayResponse, Boolean.TRUE);
			paymentGateWayService.updateCitizenPaymentStatus(paymentGateWayResponse.getPaymentStatus(),
					paymentGateWayResponse.getAppTransNo(), paymentGateWayResponse.getModuleCode());
			String an = URLEncoder.encode(encryptDecryptUtil.encrypt(paymentGateWayResponse.getAppTransNo()), "UTF-8");
			if (paymentGateWayResponse.getGatewayTypeEnum().equals(GatewayTypeEnum.PAYU)) {
				GateWayDTO paymentGateValue = gatewayDao.findByGateWayType(GatewayTypeEnum.PAYU);
				Map<String, String> payUvalue = paymentGateValue.getGatewayDetails();
				response.sendRedirect(payUvalue.get(PayUParams.CITIZEN_FAILURE_URL_UI.getParamKey()) + "?an=" + an
						+ "&module=" + ModuleEnum.REG);
			}
		} catch (Exception e) {
			gatewayDetails = gatewayValue.getGatewayDetails();
			logger.error("Exception while processing the  payment :{e}", e);
			// response.sendRedirect(gatewayDetails.get(PayUParams.PAYMENT_PENDING_URL));
		}
	}

	/**
	 * SBI gateway to our site redirection URL
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @return
	 */
	@PostMapping(path = "/sbiRedirect", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public void sbiRedirectURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GateWayDTO paymentGateValue = gatewayDao.findByGateWayType(GatewayTypeEnum.SBI);
		Map<String, String> sbiGateWayDetails = paymentGateValue.getGatewayDetails();
		try {
			String data = request.getParameter("encdata");
			logger.debug("sbiEncData:[{}]", data);
			String value = paymentGateWayService.dncryptSBIData(data);
			Map<String, String[]> map = paymentGateWayService.getSliptingofSbiValue(value);
			PaymentGateWayResponse paymentGateWayResponse = new PaymentGateWayResponse();
			paymentGateWayResponse.setGatewayTypeEnum(GatewayTypeEnum.SBI);
			paymentGateWayResponse.setGatewayResponceMap(map);
			paymentGateWayResponse = paymentGateWayService.processResponse(paymentGateWayResponse, Boolean.TRUE);
			paymentGateWayService.updateCitizenPaymentStatus(paymentGateWayResponse.getPaymentStatus(),
					paymentGateWayResponse.getAppTransNo(), paymentGateWayResponse.getModuleCode());
			String an = URLEncoder.encode(encryptDecryptUtil.encrypt(paymentGateWayResponse.getAppTransNo()), "UTF-8");
			if (paymentGateWayResponse.getPaymentStatus().equals(PayStatusEnum.SUCCESS)) {
				response.sendRedirect(sbiGateWayDetails.get(SBIParams.CITIZEN_SUCESS_URL_UI.getParamKey()) + "?an=" + an
						+ "&module=" + ModuleEnum.REG);
			} else {
				response.sendRedirect(sbiGateWayDetails.get(SBIParams.CITIZEN_FAILURE_URL_UI.getParamKey()) + "?an="
						+ an + "&module=" + ModuleEnum.REG);
			}
		} catch (Exception e) {
			logger.error("Exception while processing the  payment :[{}]", e);
		}
	}

	@Autowired
	private RTAService rtaService;

	@GetMapping(value = "test/findBasedOnPrNo", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	private GateWayResponse<?> findBasedOnPrNo(@RequestParam(name = "prNo") String prNo) {
		try {
			RegistrationDetailsVO registrationDetailsVO = rtaService.findBasedOnPrNo(prNo);

			return new GateWayResponse<>(HttpStatus.OK, registrationDetailsVO, "succ");
		} catch (Exception ex) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
		}

	}

	@GetMapping(value = "test/findBasedOnPrNos", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	private GateWayResponse<?> findBasedOnPrNo(@RequestParam(name = "prNos") List<String> prNo) {
		try {
			RegistrationDetailsVO registrationDetailsVO = rtaService.findBasedOnPrNos(prNo);

			return new GateWayResponse<>(HttpStatus.OK, registrationDetailsVO, "succ");
		} catch (Exception ex) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
		}

	}

	@GetMapping(value = "/getPaymentDetailsByApplicationNo", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	private GateWayResponse<?> getPaymentDetailsByApplicationNumber(
			@RequestParam(name = "applicationNumber") String applicationNumber) {
		try {
			if (StringUtils.isBlank(applicationNumber)) {
				return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Application Number is Missing");
			}
			CitizenPaymentReportVO paymentVO = paymentGateWayService
					.getPaymentDetailsByApplicationNumber(applicationNumber);

			return new GateWayResponse<>(HttpStatus.OK, paymentVO, "succ");
		} catch (Exception ex) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
		}

	}

	@PostMapping(value = "getCitizenPayment", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> getCitizenPayment(@RequestBody @Validated CitizenFeeDetailsInput input) {
		logger.debug(" CitizebFeedDetailsInput : [{}]", input);
		String weightDetails = null;
		ClassOfVehiclesVO covDetails = null;
		RegistrationDetailsDTO regDetails = null;
		String applicationNo = null;
		String regApplicationNo = null;
		String officeCode = null;
		boolean isCalculateFc = false;
		boolean isChassesVehicle = false;
		boolean isOtherState = false;
		boolean isApplicationFromMvi = false;
		Optional<AlterationDTO> alterDetails = Optional.empty();
		List<ServiceEnum> serviceEnums = new ArrayList<>();
		FeeDetailsVO feeDetails = null;
		Integer rlw = null;
		LocalDate slotDate = null;
		Optional<StagingRegistrationDetailsDTO> stagingDetail = null;
		String seatingCapacity = null;
		try {
			serviceEnums = input.getServiceIds().stream().map(id -> {

				if (id == null) {
					throw new BadRequestException("Service Id should not be empty");
				}
				ServiceEnum se = ServiceEnum.getServiceEnumById(id);
				if (se == null) {
					throw new BadRequestException("Invalid Service Id + " + id);
				}
				return se;
			}).collect(Collectors.toList());

			if (input.getIsChassesVehicle() != null && input.getIsChassesVehicle()) {
				stagingDetail = findByTrNoFromStagingDetails(input);
				if (!stagingDetail.isPresent()) {
					throw new BadRequestException("Application not found: " + input.getTrNo());
				}
				if (!stagingDetail.get().getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
					serviceEnums.add(ServiceEnum.FR);
				}
				isChassesVehicle = Boolean.TRUE;

				List<String> chassisCovs = Arrays.asList(ClassOfVehicleEnum.CHST.getCovCode(),
						ClassOfVehicleEnum.CHSN.getCovCode(), ClassOfVehicleEnum.ARVT.getCovCode());

				if (!chassisCovs.contains(stagingDetail.get().getClassOfVehicle())) {
					throw new BadRequestException("Vehicle is not chassis: " + stagingDetail.get().getApplicationNo());
				}
				alterDetails = alterationDAO.findByApplicationNo(stagingDetail.get().getApplicationNo());
				if (!alterDetails.isPresent()) {
					throw new BadRequestException(
							"No record found in alter Details for: " + stagingDetail.get().getApplicationNo());
				}

				covDetails = covService.findByCovCode(alterDetails.get().getCov());
				officeCode = stagingDetail.get().getOfficeDetails().getOfficeCode();
				applicationNo = stagingDetail.get().getApplicationNo();
				if (stagingDetail.get().getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {

					if (ClassOfVehicleEnum.ARVT.getCovCode().equalsIgnoreCase(alterDetails.get().getCov())) {
						if (alterDetails.get().getTrailers().isEmpty()) {
							throw new BadRequestException(
									"Trailers Details not found in Alteration collection for(ARVT) : "
											+ stagingDetail.get().getApplicationNo());
						}
						alterDetails.get().getTrailers().sort((p2, p1) -> p1.getGtw().compareTo(p2.getGtw()));
						Integer gtw = alterDetails.get().getTrailers().stream().findFirst().get().getGtw();
						rlw = stagingDetail.get().getVahanDetails().getGvw() + gtw;
					} else {
						rlw = stagingDetail.get().getVahanDetails().getGvw();
					}
					weightDetails = covService.getWeightTypeDetails(rlw);
				} else {
					weightDetails = covService.getWeightTypeDetails(alterDetails.get().getUlw());
				}

			}
			feeDetails = paymentGateWayService.getCitizenServiceFees(Arrays.asList(covDetails), serviceEnums,
					weightDetails, Boolean.FALSE, input.getTaxType(), isCalculateFc, isApplicationFromMvi,
					isChassesVehicle, officeCode, applicationNo, isOtherState, regApplicationNo, input.getPermitType(),
					slotDate, seatingCapacity, input.getRouteCode(), input.getIsweightAlt());

			return new GateWayResponse<>(HttpStatus.OK, feeDetails,
					appMessages.getResponseMessage(MessageKeys.MESSAGE_SUCCESS));
		} catch (BadRequestException ex) {
			logger.error("{}", ex.getMessage());
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
		} catch (Exception e) {
			logger.error(appMessages.getLogMessage(MessageKeys.CITIZEN_SERVICE_COMBINATION_FAILURE), e);
			return new GateWayResponse<>(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
		}
	}
}
