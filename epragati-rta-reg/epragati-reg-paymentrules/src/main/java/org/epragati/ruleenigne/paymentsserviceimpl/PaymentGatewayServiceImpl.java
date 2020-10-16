package org.epragati.ruleenigne.paymentsserviceimpl;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.aadhar.DateUtil;
import org.epragati.common.dao.PropertiesDAO;
import org.epragati.common.dto.PropertiesDTO;
import org.epragati.constants.AlterationTypeEnum;
import org.epragati.constants.CovCategory;
import org.epragati.constants.ExceptionDescEnum;
import org.epragati.constants.OwnerTypeEnum;
import org.epragati.constants.TransferType;
import org.epragati.exception.BadRequestException;
import org.epragati.hsrp.servic.IntegrationService;
import org.epragati.hsrp.vo.DataVO;
import org.epragati.jwt.JwtUser;
import org.epragati.master.dao.AlterationDAO;
import org.epragati.master.dao.FcDetailsDAO;
import org.epragati.master.dao.FinalTaxHelperDAO;
import org.epragati.master.dao.GateWayDAO;
import org.epragati.master.dao.MasterLateFeeDAO;
import org.epragati.master.dao.MasterLateeFeeForFCDAO;
import org.epragati.master.dao.MasterTaxExcemptionsDAO;
import org.epragati.master.dao.MasterUsersDAO;
import org.epragati.master.dao.MasterWeightsForAltDAO;
import org.epragati.master.dao.OfficeDAO;
import org.epragati.master.dao.RegServiceDAO;
import org.epragati.master.dao.RegistrationDetailDAO;
import org.epragati.master.dao.StageFcDetailsDAO;
import org.epragati.master.dao.StagingRegistrationDetailsDAO;
import org.epragati.master.dao.TaxDetailsDAO;
import org.epragati.master.dao.UserDAO;
import org.epragati.master.dto.FcDetailsDTO;
import org.epragati.master.dto.FinalTaxHelper;
import org.epragati.master.dto.GateWayDTO;
import org.epragati.master.dto.MasterLateFee;
import org.epragati.master.dto.MasterLateeFeeForFC;
import org.epragati.master.dto.MasterTaxExcemptionsDTO;
import org.epragati.master.dto.MasterUsersDTO;
import org.epragati.master.dto.OfficeDTO;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.RegistrationValidityDTO;
import org.epragati.master.dto.StagingFcDetailsDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxComponentDTO;
import org.epragati.master.dto.TaxDetailsDTO;
import org.epragati.master.dto.TaxHelper;
import org.epragati.master.dto.TrailerChassisDetailsDTO;
import org.epragati.master.dto.UserDTO;
import org.epragati.master.mappers.RegistrationDetailsMapper;
import org.epragati.master.mappers.StagingRegistrationDetailsMapper;
import org.epragati.master.mappers.UserMapper;
import org.epragati.master.service.CovService;
import org.epragati.master.service.LogMovingService;
import org.epragati.master.service.PermitsService;
import org.epragati.master.service.PrSeriesService;
import org.epragati.master.service.SlotsService;
import org.epragati.master.service.StagingRegistrationDetailsSerivce;
import org.epragati.master.service.TrSeriesService;
import org.epragati.master.vo.StagingRegistrationDetailsVO;
import org.epragati.master.vo.UserVO;
import org.epragati.payment.dto.FeesDTO;
import org.epragati.payment.dto.PayURefundResponse;
import org.epragati.payment.dto.PaymentTransactionDTO;
import org.epragati.payment.dto.PaymentTransactionRequestDTO;
import org.epragati.payment.dto.PaymentTransactionResponseDTO;
import org.epragati.payment.mapper.BreakPaymentsSaveMapper;
import org.epragati.payment.mapper.FeeDetailsMapper;
import org.epragati.payments.dao.PaymentFeesDeatailsDAO;
import org.epragati.payments.dao.PaymentTransactionDAO;
import org.epragati.payments.vo.BreakPayments;
import org.epragati.payments.vo.BreakPaymentsSaveVO;
import org.epragati.payments.vo.CFMSEodReportVO;
import org.epragati.payments.vo.CitizenPaymentReportVO;
import org.epragati.payments.vo.ClassOfVehiclesVO;
import org.epragati.payments.vo.FeeDetailInput;
import org.epragati.payments.vo.FeeDetailsVO;
import org.epragati.payments.vo.FeePartsDetailsVo;
import org.epragati.payments.vo.FeesVO;
import org.epragati.payments.vo.PaymentFailureResultVO;
import org.epragati.payments.vo.PaymentGateWayResponse;
import org.epragati.payments.vo.PaymentReqParams;
import org.epragati.payments.vo.TransactionDetailVO;
import org.epragati.permits.dao.PermitDetailsDAO;
import org.epragati.permits.dto.PermitDetailsDTO;
import org.epragati.registration.service.DealerService;
import org.epragati.regservice.CitizenTaxService;
import org.epragati.regservice.RegistrationService;
import org.epragati.regservice.dto.AlterationDTO;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.regservice.dto.SlotDetailsDTO;
import org.epragati.regservice.mapper.RegServiceMapper;
import org.epragati.regservice.vo.RegServiceVO;
import org.epragati.rta.service.impl.service.RTAService;
import org.epragati.rta.service.impl.service.RegistratrionServicesApprovals;
import org.epragati.ruleengine.paymentsservice.PaymentGateWayService;
import org.epragati.ruleengine.service.RuleEngineService;
import org.epragati.rules.FeePartsVO;
import org.epragati.rules.util.PaymentGateWay;
import org.epragati.rules.util.PaymentGatewayFactoryProvider;
import org.epragati.sequence.SequenceGenerator;
import org.epragati.service.notification.MessageTemplate;
import org.epragati.service.notification.NotificationUtil;
import org.epragati.tax.vo.TaxStatusEnum;
import org.epragati.tax.vo.TaxTypeEnum;
import org.epragati.tax.vo.TaxTypeEnum.TaxPayType;
import org.epragati.util.PermitsEnum;
import org.epragati.util.PermitsEnum.PermitType;
import org.epragati.util.RoleEnum;
import org.epragati.util.StatusRegistration;
import org.epragati.util.ValidityEnum;
import org.epragati.util.document.KeyValue;
import org.epragati.util.document.Sequence;
import org.epragati.util.payment.ClassOfVehicleEnum;
import org.epragati.util.payment.GatewayTypeEnum;
import org.epragati.util.payment.GatewayTypeEnum.CFMSParams;
import org.epragati.util.payment.ModuleEnum;
import org.epragati.util.payment.PayStatusEnum;
import org.epragati.util.payment.ServiceCodeEnum;
import org.epragati.util.payment.ServiceEnum;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Service
public class PaymentGatewayServiceImpl implements PaymentGateWayService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayServiceImpl.class);

	@Autowired
	private PaymentTransactionDAO paymentTransactionDAO;

	@Autowired
	private PaymentGatewayFactoryProvider paymentGatewayFactoryProvider;

	@Autowired
	private PaymentFeesDeatailsDAO feesDao;

	@Value("${isInTestPayment:}")
	private Boolean isInTestPayment;

	@Value("1.0")
	private Double testAmount;

	@Value("${sbi.key.location}")
	private String sbiKeyPath;

	@Autowired
	private GateWayDAO gatewayDao;

	@Autowired
	private TrSeriesService trSeriesService;

	@Autowired
	private StagingRegistrationDetailsSerivce stagingRegistrationDetailsSerivce;

	@Autowired
	private StagingRegistrationDetailsDAO stagingRegistrationDetails;

	@Autowired
	private BreakPaymentsSaveMapper breakPaymentsSaveMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private RuleEngineService ruleEngineService;

	@Autowired
	private NotificationUtil notifications;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RegistrationDetailsMapper regDetailsMapper;

	@Autowired
	private TaxDetailsDAO taxDetailsDAO;

	@Autowired

	private RegistrationService regService;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private OfficeDAO officeDAO;

	@Autowired
	private RTAService rtaService;

	@Autowired
	private RegistratrionServicesApprovals registratrionServicesApprovals;

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private PrSeriesService prSeriesService;

	@Autowired
	private RegServiceDAO regServiceDAO;

	@Value("${reg.fresh.permitcode}")
	private String permitcode;

	@Value("${rta.hoa.creditAccount}")
	private String rtaCreditAccount;

	@Value("${cess.hoa.creditAccount}")
	private String cessCreditAccount;

	@Value("${hsrp.hoa.creditAccount}")
	private String hsrpCreditAccount;

	@Value("${lifeTax.headOfAccount}")
	private String lifeTaxHoa;

	@Value("${qtlyTax.headOfAccount}")
	private String qutrelyTaxHoa;

	@Value("${greenTax.greenTaxHoa:0041001020005000000NVN}")
	private String greenTaxHoa;
	@Value("${greenTax.serviceFeeHoa:0041008000081001000NVN}")
	private String serviceFeeHoa;
	@Value("${greenTax.applicationFeeHoa:0041001010005000000NVN}")
	private String applicationFeeHoa;
	@Autowired
	private DealerService registrationService;

	@Autowired
	private FeeDetailsMapper feeDetailsMapper;

	@Autowired
	private SlotsService slotsService;

	@Autowired
	private CitizenTaxService citizenTaxService;

	@Autowired
	private AlterationDAO alterationDao;

	@Autowired
	private RegistrationService citizenRegistrationService;

	@Autowired
	private LogMovingService logMovingService;

	@Autowired
	private RegServiceMapper regServiceMapper;

	@Autowired
	private CovService covService;

	@Autowired
	private RegistrationDetailDAO registrationDetailDAO;

	@Autowired
	private MasterLateFeeDAO masterLateFeeDAO;

	@Autowired
	private PrSeriesService prService;

	@Autowired
	private PermitsService permitsService;

	@Autowired
	private StagingRegistrationDetailsMapper stagingRegistrationDetailsMapper;

	@Autowired
	private PropertiesDAO propertiesDAO;

	@Autowired
	private StageFcDetailsDAO stageFcDetailsDAO;

	@Autowired
	SequenceGenerator sequenceGenerator;

	@Autowired
	private MasterLateeFeeForFCDAO masterLateeFeeForFCDAO;

	@Autowired
	private MasterTaxExcemptionsDAO masterTaxExcemptionsDAO;

	@Autowired
	private PermitDetailsDAO permitDetailsDAO;

	@Autowired
	private FcDetailsDAO fcDetailsDAO;

	@Autowired
	private MasterUsersDAO masterUsersDAO;

	@Autowired
	private FinalTaxHelperDAO finalTaxHelperDAO;

	@Autowired
	private MasterWeightsForAltDAO masterWeightsForAltDAO;

	@Override
	public FeeDetailsVO getServiceFee(FeeDetailInput feeDetailInput, TransactionDetailVO transactionDetailVO) {
		logger.debug("getServiceFee Start");
		if (Objects.isNull(feeDetailInput) || feeDetailInput.getCovs() == null
				|| CollectionUtils.isEmpty(feeDetailInput.getServiceList())) {
			logger.error("covs and service enum/service enum List are getting null");
			throw new BadRequestException(ExceptionDescEnum.NULL_EMPTY_GATEWAY_COV_SERVICE.getDesciption());
		}
		Set<Integer> serviceIds = feeDetailInput.getServiceList().stream().map(service -> service.getId())
				.collect(Collectors.toSet());
		// Map<String, FeePartsDetailsVo> feePartsMap =
		// this.getFeePartsDetails(serviceIds, transactionDetailVO);
		return null;
	}

	@Override
	public Optional<TransactionDetailVO> prepareRequestObject(TransactionDetailVO transactionDetailVO, Object newParam,
			String collectionType) {
		logger.debug("prepareRequestObject start");

		if (transactionDetailVO != null) {
			Set<Integer> serviceIds = transactionDetailVO.getServiceEnumList().stream().map(service -> service.getId())
					.collect(Collectors.toSet());

			// For SPNR and SPNB (Special Number) services

			if (serviceIds.stream()
					.anyMatch(id -> id.equals(ServiceEnum.SPNR.getId()) || id.equals(ServiceEnum.SPNB.getId()))) {
				transactionDetailVO.setFeePartsMap(getFeePartsDetails(serviceIds,
						transactionDetailVO.getAmount() - transactionDetailVO.getServicesFeeAmt(),
						transactionDetailVO.getServicesFeeAmt(), transactionDetailVO.getModule()));
				transactionDetailVO.setFeeDetailsVO(preFeeDetailsVO(transactionDetailVO.getFeePartsMap()));
			} else if (transactionDetailVO.getModule().equalsIgnoreCase(ModuleEnum.CITIZEN.getCode())
					|| transactionDetailVO.getModule().equalsIgnoreCase(ModuleEnum.BODYBUILDER.getCode())
					|| transactionDetailVO.getModule().equalsIgnoreCase(ModuleEnum.ALTERVEHICLE.getCode())) {
				transactionDetailVO.setFeePartsMap(calculateTaxAndFee(transactionDetailVO.getCovs(),
						transactionDetailVO.getServiceEnumList(), transactionDetailVO.getWeightType(),
						transactionDetailVO.isRequestToPay(), transactionDetailVO.getTaxType(),
						transactionDetailVO.isCalculateFc(), transactionDetailVO.isRtoRejectedIvcn(),
						transactionDetailVO.isChassesVehicle(), transactionDetailVO.getOfficeCode(),
						transactionDetailVO.getFormNumber(), transactionDetailVO.isOtherState(),
						transactionDetailVO.getRegApplicationNo(), transactionDetailVO.getPermitType(),
						transactionDetailVO.getSlotDate(), transactionDetailVO.getSeatingCapacity(),
						transactionDetailVO.getRouteCode(), transactionDetailVO.getIsWeightAlt()));
				transactionDetailVO.setFeeDetailsVO(preFeeDetailsVO(transactionDetailVO.getFeePartsMap()));
				Map<String, FeePartsDetailsVo> feeParts = applicationFeeInFeeParts(
						transactionDetailVO.getFeePartsMap());

				transactionDetailVO.setFeePartsMap(feeParts);
			} else {
				if (Objects.isNull(transactionDetailVO.getGatewayTypeEnum()) || transactionDetailVO.getCovs() == null
						|| (CollectionUtils.isEmpty(transactionDetailVO.getServiceEnumList()))) {
					logger.error("GatewayTypeEnum ,covs and service enum are getting null");
					throw new BadRequestException(ExceptionDescEnum.NULL_EMPTY_GATEWAY_COV_SERVICE.getDesciption());
				}
				transactionDetailVO.setBreakPayments(getPaymentBrakUPDetails(transactionDetailVO.getCovs(),
						transactionDetailVO.getServiceEnumList(), transactionDetailVO.getWeightType(),
						transactionDetailVO.getTaxAmount(), transactionDetailVO.getCesFee(),
						transactionDetailVO.getTaxType(), transactionDetailVO.isRtoSecRejected(),
						transactionDetailVO.isRtoRejectedIvcn(), transactionDetailVO.getOwnerType(),
						transactionDetailVO.getOfficeCode(), transactionDetailVO.getFormNumber()));
				transactionDetailVO.setFeePartsMap(getFeePartsDetails(transactionDetailVO.getBreakPayments(),
						transactionDetailVO.getServiceEnumList()));

			}

			if (isInTestPayment != null && isInTestPayment) {
				// Double testAmount = transactionDetailVO.getCesFee() > 0 ? 3.0
				// : 2.0;
				transactionDetailVO.setAmount(1.0);
			} else {
				transactionDetailVO.setAmount(getTotalamount(transactionDetailVO.getFeePartsMap()));
			}

			if (transactionDetailVO.getGatewayTypeEnum() == null) {
				Optional<PaymentTransactionDTO> getGateType = this
						.getPayMentGateType(transactionDetailVO.getRegApplicationNo());
				if (getGateType.isPresent()) {
					// GatewayTypeEnum.getGatewayTypeEnumById(getGateType.get().getPaymentGatewayType());
					transactionDetailVO.setGatewayTypeEnum(
							GatewayTypeEnum.getGatewayTypeEnumById(getGateType.get().getPaymentGatewayType()));
				}
			}

			logger.debug(
					"transactionDetailVO.GatewayTypeEnum.Description : {} transactionDetailVO.getGatewayTypeEnum().getId()",
					transactionDetailVO.getGatewayTypeEnum().getDescription(),
					transactionDetailVO.getGatewayTypeEnum().getId());

			PaymentGateWay paymentGateWay = paymentGatewayFactoryProvider
					.getPaymentGateWayInstance(transactionDetailVO.getGatewayTypeEnum());
			transactionDetailVO = paymentGateWay.getRequestParameter(transactionDetailVO);
			logger.info("prepareRequestObject end");
			saveTransactionDetails(transactionDetailVO, newParam, collectionType);
			return Optional.of(transactionDetailVO);
		}
		logger.warn("Input transactionDetailVO not found");
		return Optional.empty();

	}

	public Optional<PaymentTransactionDTO> getPayMentGateType(String applicationFormRefNum) {
		List<String> applicationNo = new ArrayList<>();
		applicationNo.add(applicationFormRefNum);
		List<PaymentTransactionDTO> payList = paymentTransactionDAO.findByApplicationFormRefNumIn(applicationNo);
		if (!payList.isEmpty()) {
			// payList.sort((p2, p1) ->
			// p1.getCreatedDate().compareTo(p2.getCreatedDate()));
			payList.sort((o1, o2) -> o2.getRequest().getRequestTime().compareTo(o1.getRequest().getRequestTime()));
			PaymentTransactionDTO regDTO = payList.stream().findFirst().get();
			return Optional.of(regDTO);
		}
		return Optional.empty();
	}

	// private Map<String, FeePartsDetailsVo>
	// getFeePartsDetails(List<BreakPayments>
	// breakPayments) {
	private Map<String, FeePartsDetailsVo> applicationFeeInFeeParts(Map<String, FeePartsDetailsVo> feeParts) {

		Map<String, FeePartsDetailsVo> beakUpPayments = feeParts;
		for (Map.Entry<String, FeePartsDetailsVo> entry : beakUpPayments.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.FC_LATE_FEE.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.FITNESS_FEE.getTypeDesc())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.FITNESS_FEE.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getCode())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.CARD.getCode())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getCode())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.FITNESS_SERVICE_FEE.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.SERVICE_FEE.getTypeDesc())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.PENALTY.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (getTaxTypes().stream().anyMatch(type -> type.equalsIgnoreCase(entry2.getKey()))) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.PENALTYARREARS.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (getTaxTypes().stream().anyMatch(type -> type.equalsIgnoreCase(entry2.getKey()))) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.TAXARREARS.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (getTaxTypes().stream().anyMatch(type -> type.equalsIgnoreCase(entry2.getKey()))) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.LATE_FEE.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getCode())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			} else if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.TAXSERVICEFEE.getTypeDesc())) {
				for (Map.Entry<String, FeePartsDetailsVo> entry2 : feeParts.entrySet()) {
					if (entry2.getKey().equalsIgnoreCase(ServiceCodeEnum.SERVICE_FEE.getTypeDesc())) {
						entry2.getValue().setAmount(entry.getValue().getAmount() + entry2.getValue().getAmount());
						entry.setValue(null);
					}
				}
			}
		}

		return beakUpPayments;

	}

	private List<String> getTaxTypes() {
		List<String> list = new ArrayList<>();
		list.add(ServiceCodeEnum.QLY_TAX.getCode());
		list.add(ServiceCodeEnum.HALF_TAX.getCode());
		list.add(ServiceCodeEnum.YEAR_TAX.getCode());
		return list;
	}

	private Map<String, FeePartsDetailsVo> getFeePartsDetails(List<BreakPayments> breakPayments,
			List<ServiceEnum> servicesList) {
		Map<String, FeePartsDetailsVo> feePartsMap = new HashMap<>();
		Set<Integer> serviceId = servicesList.stream().map(service -> service.getId()).collect(Collectors.toSet());
		List<FeesDTO> feesDTOList = feesDao.findByServiceIdIn(serviceId);
		for (BreakPayments breakPayments2 : breakPayments) {
			for (Map.Entry<String, Double> entry : breakPayments2.getBreakup().entrySet()) {
				ServiceCodeEnum serviceCodeEnum = null;
				serviceCodeEnum = ServiceCodeEnum.getSubHeadCodeEnum(entry.getKey());
				if (serviceCodeEnum.equals(ServiceCodeEnum.CARD)) {
					serviceCodeEnum = ServiceCodeEnum.REGISTRATION;
				}
				switch (serviceCodeEnum) {

				case REGISTRATION:

					if (feePartsMap.containsKey(ServiceCodeEnum.REGISTRATION.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap
								.get(ServiceCodeEnum.REGISTRATION.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}

						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsMap.put(ServiceCodeEnum.REGISTRATION.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case HSRP_FEE:
					if (feePartsMap.containsKey(ServiceCodeEnum.HSRP_FEE.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.HSRP_FEE.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(hsrpCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.HSRP_FEE.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(hsrpCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.HSRP_FEE.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case FITNESS_FEE:

					break;
				case PERMIT_FEE:

					break;
				case OTHER_RECEIPTS:
					break;
				case QLY_TAX:
					if (feePartsMap.containsKey(ServiceCodeEnum.QLY_TAX.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.QLY_TAX.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.QLY_TAX.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.QLY_TAX.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case CESS_FEE:
					if (feePartsMap.containsKey(ServiceCodeEnum.CESS_FEE.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.CESS_FEE.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(cessCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsDetailsVo.setCredit_Account(cessCreditAccount);
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(cessCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.CESS_FEE.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case HALF_TAX:
					if (feePartsMap.containsKey(ServiceCodeEnum.HALF_TAX.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.HALF_TAX.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.QLY_TAX.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.HALF_TAX.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case YEAR_TAX:
					if (feePartsMap.containsKey(ServiceCodeEnum.YEAR_TAX.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.YEAR_TAX.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(qutrelyTaxHoa);
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.QLY_TAX.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.YEAR_TAX.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case LIFE_TAX:
					if (feePartsMap.containsKey(ServiceCodeEnum.LIFE_TAX.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.LIFE_TAX.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						feePartsDetailsVo.setHOA(lifeTaxHoa);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsDetailsVo.setHOA(lifeTaxHoa);
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.LIFE_TAX.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.LIFE_TAX.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case GREEN_TAX:
					break;
				case SERVICE_FEE:
					if (feePartsMap.containsKey(ServiceCodeEnum.SERVICE_FEE.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap
								.get(ServiceCodeEnum.SERVICE_FEE.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						feePartsDetailsVo.setAmount(entry.getValue());
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.SERVICE_FEE.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}
						feePartsMap.put(ServiceCodeEnum.SERVICE_FEE.getTypeDesc(), feePartsDetailsVo);
					}
					break;

				case POSTAL_FEE:
					if (feePartsMap.containsKey(ServiceCodeEnum.POSTAL_FEE.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.POSTAL_FEE.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.POSTAL_FEE.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}

						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsMap.put(ServiceCodeEnum.POSTAL_FEE.getTypeDesc(), feePartsDetailsVo);
					}
					break;

				case TEST_FEE:// It will add head one only
					break;
				case CARD:
					if (feePartsMap.containsKey(ServiceCodeEnum.CARD.getTypeDesc())) {
						FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.CARD.getTypeDesc());
						feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + entry.getValue());
						feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
					} else {
						FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						for (FeesDTO list : feesDTOList) {
							if (list.getFeesType().equalsIgnoreCase(ServiceCodeEnum.CARD.getTypeDesc())) {
								feePartsDetailsVo.setHOA(list.getHOA());
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
								break;
							}
						}

						feePartsDetailsVo.setAmount(entry.getValue());
						feePartsMap.put(ServiceCodeEnum.CARD.getTypeDesc(), feePartsDetailsVo);
					}
					break;
				case LATE_FEE:
					break;
				default:
					// logger.warn("SubHeadCode {} is not Found ",
					// feesDTO.getServiceCode());
					break;

				}
			}
		}
		return feePartsMap;
	}

	/**
	 * saveRequest used to save payment request object along with payeer details as
	 * form number
	 * 
	 * @param transactionDetailVO
	 */
	private void saveTransactionDetails(TransactionDetailVO transactionDetailVO, Object obj, String dbCollectionType) {
		try {

			PaymentTransactionDTO paymentTransactionDTO = new PaymentTransactionDTO();
			paymentTransactionDTO.setApplicationFormRefNum(transactionDetailVO.getFormNumber());
			paymentTransactionDTO.setOfficeCode(transactionDetailVO.getOfficeCode());
			paymentTransactionDTO.setModuleCode(transactionDetailVO.getModule());
			paymentTransactionDTO.setCreatedDateStr(LocalDateTime.now().toString());
			// paymentTransactionDTO.setChallanDetails(challanDetails);
			paymentTransactionDTO.setPaymentGatewayType(transactionDetailVO.getGatewayTypeEnum().getId());
			paymentTransactionDTO.setTransactioNo(transactionDetailVO.getTxnid());
			paymentTransactionDTO.setPayStatus(PayStatusEnum.PENDING.getDescription());
			if (transactionDetailVO.getSbiTransactionNumber() != null) {
				paymentTransactionDTO.setSbiTransactionNumber(transactionDetailVO.getSbiTransactionNumber());
			}
			PaymentTransactionRequestDTO request = new PaymentTransactionRequestDTO();
			request.setRequestDeatils(transactionDetailVO.getProductInfo());
			request.setRequestTime(LocalDateTime.now());

			paymentTransactionDTO.setRequest(request);
			// paymentTransactionDTO.setId(transactionDetailVO.getTxnid());
			if (transactionDetailVO.getServiceEnumList().stream().anyMatch(id -> id.equals(ServiceEnum.FCLATEFEE))) {
				Set<Integer> set = new HashSet<>();
				if (transactionDetailVO.isFcRenewal()) {
					set.add(ServiceEnum.RENEWALFC.getId());
				} else if (transactionDetailVO.isFcOtherStation()) {
					set.add(ServiceEnum.OTHERSTATIONFC.getId());
				} else {
					set.add(ServiceEnum.NEWFC.getId());
				}

				paymentTransactionDTO.setServiceIds(set);
			} else if (transactionDetailVO.getServiceEnumList().stream()
					.anyMatch(id -> id.equals(ServiceEnum.ALTDIFFTAX))) {
				Set<Integer> set = new HashSet<>();
				set.add(ServiceEnum.ALTERATIONOFVEHICLE.getId());
				paymentTransactionDTO.setServiceIds(set);
			} else {
				if (transactionDetailVO.getServiceEnumList().contains(ServiceEnum.TOSELLER)) {
					Collections.replaceAll(transactionDetailVO.getServiceEnumList(), ServiceEnum.TOSELLER,
							ServiceEnum.TRANSFEROFOWNERSHIP);
				}
				paymentTransactionDTO.setServiceIds(transactionDetailVO.getServiceEnumList().stream()
						.map(service -> service.getId()).collect(Collectors.toSet()));
			}

			// Prepare fee details DTO
			if (!Objects.isNull(transactionDetailVO.getBreakPayments())) {
				BreakPaymentsSaveVO breakPayments = preFeeDetails(transactionDetailVO.getBreakPayments());
				paymentTransactionDTO.setBreakPaymentsSave(breakPaymentsSaveMapper.convertVO(breakPayments));
			}
			if (!Objects.isNull(transactionDetailVO.getFeeDetailsVO())) {

				paymentTransactionDTO
						.setFeeDetailsDTO(feeDetailsMapper.convertVO(transactionDetailVO.getFeeDetailsVO()));
			}
			updatePaymentStatus(transactionDetailVO, paymentTransactionDTO, obj, dbCollectionType);

		} catch (Exception e) {
			logger.error("Exception while save payment request {}", e);
			throw new BadRequestException(ExceptionDescEnum.FAILD_SAVE_PAYMENT_REQUEST.getDesciption());
		}
		logger.debug("Payment request save Sucess");
	}

	private void updatePaymentStatus(TransactionDetailVO transactionDetailVO,
			PaymentTransactionDTO paymentTransactionDTO, Object obj, String dbCollectionType) {
		if (transactionDetailVO.getPaymentTransactionNo() != null
				&& StringUtils.isNoneBlank(transactionDetailVO.getPaymentTransactionNo()))
			paymentTransactionDTO.setPaymentTransactionNo(transactionDetailVO.getPaymentTransactionNo());

		Optional<StagingRegistrationDetailsDTO> registrationDetails = null;

		if (StringUtils.isNotBlank(dbCollectionType) && dbCollectionType.equals("STAGING")) {
			registrationDetails = (Optional<StagingRegistrationDetailsDTO>) obj;
		}

		if (registrationDetails != null) {
			if (registrationDetails.get().getApplicationStatus().equals(StatusRegistration.INITIATED.getDescription())
					|| registrationDetails.get().getApplicationStatus()
							.equals(StatusRegistration.PAYMENTFAILED.getDescription())) {
				updateTaxInStaging(registrationDetails, transactionDetailVO);
				registrationService.updatePaymentStatusOfApplicant(registrationDetails,
						transactionDetailVO.getPaymentTransactionNo(),
						StatusRegistration.PAYMENTPENDING.getDescription());
			}

			else if (registrationDetails.get().getApplicationStatus()
					.equals(StatusRegistration.REJECTED.getDescription())
					|| registrationDetails.get().getApplicationStatus()
							.equals(StatusRegistration.SECORINVALIDFAILED.getDescription())) {
				updateTaxInStaging(registrationDetails, transactionDetailVO);
				registrationService.updatePaymentStatusOfApplicant(registrationDetails,
						transactionDetailVO.getPaymentTransactionNo(),
						StatusRegistration.SECORINVALIDPENDING.getDescription());
			} else if (registrationDetails.get().getApplicationStatus()
					.equals(StatusRegistration.IVCNREJECTED.getDescription())) {
				updateTaxInStaging(registrationDetails, transactionDetailVO);
				registrationService.updatePaymentStatusOfApplicant(registrationDetails,
						transactionDetailVO.getPaymentTransactionNo(),
						StatusRegistration.IVCNPAYMENTPENDING.getDescription());
			} /*
				 * else if (registrationDetails.get().getApplicationStatus()
				 * .equals(StatusRegistration.PAYMENTFAILED.getDescription()) ||
				 * registrationDetails.get().getApplicationStatus()
				 * .equals(StatusRegistration.SECORINVALIDFAILED.getDescription( ))) {
				 * registrationService. updatePaymentTransactionNoFoFailedTransactions(
				 * registrationDetails, transactionDetailVO.getPaymentTransactionNo()); }
				 */
			logMovingService.moveStagingToLog(registrationDetails.get().getApplicationNo());
			stagingRegistrationDetails.save(registrationDetails.get());
		}
		logMovingService.movePaymnetsToLog(paymentTransactionDTO.getApplicationFormRefNum());

		paymentTransactionDAO.save(paymentTransactionDTO);
	}

	public List<FeesDTO> feeDetailsCommonMehod(String weightType, Set<String> codeSet, ServiceEnum id,
			String permitTypeCode, String seatingCapcity) {
		List<FeesDTO> feeDTOList = null;
		boolean verifyCOV = false;
		List<ServiceEnum> serviceName = new ArrayList<>();
		serviceName.add(ServiceEnum.TRANSFEROFOWNERSHIP);
		serviceName.add(ServiceEnum.CHANGEOFADDRESS);
		serviceName.add(ServiceEnum.HPA);
		serviceName.add(ServiceEnum.ALTERATIONOFVEHICLE);
		serviceName.add(ServiceEnum.ISSUEOFNOC);
		serviceName.add(ServiceEnum.DUPLICATE);
		serviceName.add(ServiceEnum.RENEWAL);
		serviceName.add(ServiceEnum.HIREPURCHASETERMINATION);
		if (StringUtils.isNotBlank(permitTypeCode) && !serviceName.contains(id)) {
			feeDTOList = methodForFetchingPermitFees(weightType, codeSet, id, permitTypeCode, seatingCapcity,
					verifyCOV);
		} else {
			feeDTOList = feesDao.findByServiceIdInAndCovcodeInAndWeighttype(id.getId(), codeSet, weightType);
		}

		return feeDTOList;
	}

	private List<FeesDTO> methodForFetchingPermitFees(String weightType, Set<String> codeSet, ServiceEnum id,
			String permitTypeCode, String seatingCapcity, boolean verifyCOV) {
		List<FeesDTO> feeDTOList;
		List<String> covList;
		Optional<PropertiesDTO> serviceCode = propertiesDAO.findByModule(ModuleEnum.PAYMENTS.getCode());
		if (serviceCode.isPresent()) {
			covList = serviceCode.get().getSeatingBasedCovList();
			verifyCOV = covList.stream().anyMatch(val -> codeSet.contains(val));
		}
		if (verifyCOV) {
			feeDTOList = feesDao
					.findByServiceIdInAndCovcodeInAndWeighttypeAndPermitCodeAndSeatToGreaterThanEqualAndSeatFromLessThanEqual(
							id.getId(), codeSet, weightType, permitTypeCode, Integer.parseInt(seatingCapcity),
							Integer.parseInt(seatingCapcity));
		} else {
			feeDTOList = feesDao.findByServiceIdInAndCovcodeInAndWeighttypeAndPermitCode(id.getId(), codeSet,
					weightType, permitTypeCode);
		}
		return feeDTOList;
	}

	private List<FeesDTO> methodForFetchingPermitFeesOnlyForApplicationFee(String weightType, Set<String> codeSet,
			ServiceEnum id, String permitTypeCode, String seatingCapcity, boolean verifyCOV) {
		List<FeesDTO> feeDTOList;
		List<String> covList;
		Optional<PropertiesDTO> serviceCode = propertiesDAO.findByModule(ModuleEnum.PAYMENTS.getCode());
		if (serviceCode.isPresent()) {
			covList = serviceCode.get().getSeatingBasedCovList();
			verifyCOV = covList.stream().anyMatch(val -> codeSet.contains(val));
		}
		if (verifyCOV) {
			feeDTOList = feesDao
					.findByServiceIdInAndCovcodeInAndWeighttypeAndPermitCodeAndSeatToGreaterThanEqualAndSeatFromLessThanEqualAndFeesTypeIgnoreCase(
							id.getId(), codeSet, weightType, permitTypeCode, Integer.parseInt(seatingCapcity),
							Integer.parseInt(seatingCapcity), ServiceCodeEnum.REGISTRATION.getCode());
		} else {
			feeDTOList = feesDao.findByServiceIdInAndCovcodeInAndWeighttypeAndPermitCodeAndFeesTypeIgnoreCase(
					id.getId(), codeSet, weightType, permitTypeCode, ServiceCodeEnum.REGISTRATION.getCode());
		}
		return feeDTOList;
	}

	@Override
	public PaymentGateWayResponse processResponse(PaymentGateWayResponse paymentGateWayResponse,
			boolean isRequestFromCitizen) {
		PaymentGateWay paymentGateWay = paymentGatewayFactoryProvider
				.getPaymentGateWayInstance(paymentGateWayResponse.getGatewayTypeEnum());
		GateWayDTO gatewayValue = gatewayDao.findByGateWayType(GatewayTypeEnum.PAYU);
		Map<String, String> gatewayDetails = gatewayValue.getGatewayDetails();
		paymentGateWayResponse = paymentGateWay.processResponse(paymentGateWayResponse, gatewayDetails);
		if (GatewayTypeEnum.SBI.equals(paymentGateWayResponse.getGatewayTypeEnum())) {
			paymentGateWayResponse = addApplicationTransactionNumberForSBI(paymentGateWayResponse);
		} else if (GatewayTypeEnum.CFMS.equals(paymentGateWayResponse.getGatewayTypeEnum())) {
			paymentGateWayResponse = addModuleCodeAndAppTransactionNumberForCFMS(paymentGateWayResponse);
		}
		updateTransactionDetails(paymentGateWayResponse);
		if (!isRequestFromCitizen) {
			if (paymentGateWayResponse.getModuleCode().equalsIgnoreCase(ModuleEnum.CITIZEN.getCode())) {
				updatePaymentStatusOfRegistrationDetailsForDataEntry(paymentGateWayResponse);
			} else {
				updatePaymentStatusOfRegistrationDetails(paymentGateWayResponse);
			}
		}
		logger.info("Payment processResponse Sucess :[{}]", paymentGateWayResponse.getAppTransNo());
		return paymentGateWayResponse;

	}

	private PaymentGateWayResponse addModuleCodeAndAppTransactionNumberForCFMS(
			PaymentGateWayResponse paymentGateWayResponse) {
		Optional<PaymentTransactionDTO> paymentDetails = paymentTransactionDAO
				.findByTransactioNo(paymentGateWayResponse.getCfmsResponce().getDtid());
		logger.info("Module [{}],ApplicationNo[{}]", paymentDetails.get().getModuleCode(),
				paymentDetails.get().getApplicationFormRefNum());
		paymentGateWayResponse.setModuleCode(paymentDetails.get().getModuleCode());
		paymentGateWayResponse.setAppTransNo(paymentDetails.get().getApplicationFormRefNum());
		paymentGateWayResponse.setTransactionNo(paymentGateWayResponse.getCfmsResponce().getDtid());

		return paymentGateWayResponse;
	}

	private void updatePaymentStatusOfRegistrationDetailsForDataEntry(PaymentGateWayResponse paymentGateWayResponse) {
		String applicationNumber = paymentGateWayResponse.getAppTransNo();
		PayStatusEnum payStatus = paymentGateWayResponse.getPaymentStatus();
		String moduleCode = paymentGateWayResponse.getModuleCode();
		Optional<RegServiceDTO> regDTOOPT = regServiceDAO.findByApplicationNo(applicationNumber);

		if (!regDTOOPT.isPresent()) {

			throw new BadRequestException(
					"Registration Details Is not Available with application number" + applicationNumber);
		}
		RegServiceDTO registrationDetailsDTO = regDTOOPT.get();

		if (moduleCode == null || moduleCode.equalsIgnoreCase(ModuleEnum.CITIZEN.getCode())) {
			if (PayStatusEnum.SUCCESS.getDescription().equals(payStatus.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.PAYMENTDONE);
				/*
				 * try { if(registrationDetailsDTO.getServiceIds().stream().anyMatch(
				 * id->id.equals( ServiceEnum.DATAENTRY.getId()))) { if
				 * (registrationDetailsDTO.getTrNo() == null) { String trNo =
				 * trSeriesService.geneateTrSeries(
				 * registrationDetailsDTO.getRegistrationDetails(). getApplicantDetails().
				 * getPresentAddress().getDistrict().getDistrictId());
				 * registrationDetailsDTO.setTrNo(trNo);
				 * registrationDetailsDTO.getRegistrationDetails().setTrNo(trNo) ; } } } catch
				 * (Exception e) { logger.error("Unable to update TR Number."); throw new
				 * BadRequestException(e.getMessage()); }
				 */
			}
			if (PayStatusEnum.PENDINGFROMBANK.getDescription().equals(payStatus.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.PAYMENTPENDING);
			}
			if (PayStatusEnum.FAILURE.getDescription().equals(payStatus.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.PAYMENTFAILED);
			}
		}
		logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
		regServiceDAO.save(registrationDetailsDTO);

	}

	private void updatePaymentStatusOfRegistrationDetails(PaymentGateWayResponse paymentGateWayResponse) {
		String applicationNumber = paymentGateWayResponse.getAppTransNo();
		PayStatusEnum payStatus = paymentGateWayResponse.getPaymentStatus();
		String moduleCode = paymentGateWayResponse.getModuleCode();
		Optional<StagingRegistrationDetailsDTO> regDTOOPT = stagingRegistrationDetailsSerivce
				.FindbBasedOnApplicationNo(applicationNumber);

		if (!regDTOOPT.isPresent()) {

			throw new BadRequestException(
					"Registration Details Is not Available with application number" + applicationNumber);
		}
		StagingRegistrationDetailsDTO registrationDetailsDTO = regDTOOPT.get();
		if (moduleCode == null || moduleCode.equalsIgnoreCase(ModuleEnum.REG.getCode())) {

			registrationDetailsDTO
					.setApplicationStatus(findStatusFromPayStatus(payStatus.getDescription(), registrationDetailsDTO));

		}
		logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
		stagingRegistrationDetails.save(registrationDetailsDTO);
		/*
		 * if(registrationDetailsDTO.getApplicationStatus().equalsIgnoreCase(
		 * StatusRegistration.SECORINVALIDDONE.getDescription())) {
		 * generateTrNoForPaymentSuccess(registrationDetailsDTO.getApplicationNo ()); }
		 */
	}

	private String findStatusFromPayStatus(String description, StagingRegistrationDetailsDTO registrationDetailsDTO) {
		if (PayStatusEnum.SUCCESS.getDescription().equals(description)) {
			if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.SECORINVALIDPENDING.getDescription())) {
				return StatusRegistration.SECORINVALIDDONE.getDescription();
			} else if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.IVCNPAYMENTPENDING.getDescription())) {
				TaxDetailsDTO taxDetailsDTO = saveTaxDetails(registrationDetailsDTO, Boolean.FALSE, Boolean.FALSE);
				taxDetailsDAO.save(taxDetailsDTO);
				return StatusRegistration.DEALERRESUBMISSION.getDescription();
			}
			return StatusRegistration.PAYMENTSUCCESS.getDescription();
		}
		if (PayStatusEnum.PENDING.getDescription().equals(description)
				|| PayStatusEnum.PENDINGFROMBANK.getDescription().equals(description)) {
			if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.SECORINVALIDPENDING.getDescription())) {
				return StatusRegistration.SECORINVALIDPENDING.getDescription();
			} else if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.IVCNPAYMENTPENDING.getDescription())) {
				return StatusRegistration.IVCNPAYMENTPENDING.getDescription();
			}
			return StatusRegistration.PAYMENTPENDING.getDescription();
		}
		if (PayStatusEnum.FAILURE.getDescription().equals(description)) {
			if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.SECORINVALIDPENDING.getDescription())) {
				return StatusRegistration.SECORINVALIDFAILED.getDescription();
			} else if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.IVCNPAYMENTPENDING.getDescription())) {
				return StatusRegistration.IVCNPAYMENTFAILED.getDescription();
			}
			return StatusRegistration.PAYMENTFAILED.getDescription();
		}
		return null;
	}

	private TaxDetailsDTO getTaxDetails(StagingRegistrationDetailsDTO registrationDetailsDTO) {
		registrationDetailsDTO.setSecondVehicleTaxPaid(Boolean.TRUE);
		TaxDetailsDTO taxDetailsDto = saveTaxDetails(registrationDetailsDTO, Boolean.TRUE, Boolean.FALSE);
		moveFlowDetailsToLog(registrationDetailsDTO);
		return taxDetailsDto;
	}

	private PaymentGateWayResponse addApplicationTransactionNumberForSBI(
			PaymentGateWayResponse paymentGateWayResponse) {
		if (paymentGateWayResponse != null) {
			Optional<PaymentTransactionDTO> paymentDetails = paymentTransactionDAO
					.findByTransactioNo(paymentGateWayResponse.getSbiResponce().getDepartment_TransID_1());
			paymentGateWayResponse.setAppTransNo(paymentDetails.get().getApplicationFormRefNum());
			paymentGateWayResponse.setTransactionNo(paymentGateWayResponse.getSbiResponce().getChallan_No_1());
			paymentGateWayResponse.setModuleCode(paymentDetails.get().getModuleCode());
		}
		return paymentGateWayResponse;
	}

	@Override
	public PaymentGateWayResponse processVerify(String appFormNo, Boolean isAgreeToEnablePayment,
			String paymentTransactionNo) {

		Optional<PaymentTransactionDTO> optionalDTO = Optional.empty();

		if (!Objects.isNull(appFormNo)) {

			PaymentGateWayResponse paymentGateWayResponse = null;
			if (paymentTransactionNo != null && StringUtils.isNoneBlank(paymentTransactionNo)) {
				optionalDTO = getLatestTransactionDateByTransactionRefNumber(appFormNo, paymentTransactionNo);
			} else {
				optionalDTO = getLatestTransactionDateByTransactionRefNumber(appFormNo);
			}
			if (optionalDTO.isPresent()) {
				PaymentTransactionDTO payTransctionDTO = optionalDTO.get();
				PaymentGateWay paymentGateWay = paymentGatewayFactoryProvider.getPaymentGateWayInstance(
						GatewayTypeEnum.getGatewayTypeEnumById(payTransctionDTO.getPaymentGatewayType()));
				paymentGateWayResponse = paymentGateWay.processVerify(payTransctionDTO);
				/*
				 * if (isAgreeToEnablePayment &&
				 * PayStatusEnum.PENDINGFROMBANK.equals(paymentGateWayResponse.
				 * getPaymentStatus( ))) {
				 * paymentGateWayResponse.setPaymentStatus(PayStatusEnum.FAILURE );
				 * paymentGateWayResponse.setIsAgreeToEnablePayment( isAgreeToEnablePayment);
				 * 
				 * }
				 */
				paymentGateWayResponse.setAppTransNo(appFormNo);
				updateTransactionDetails(paymentGateWayResponse);

				if (optionalDTO.get().getModuleCode() != null && (optionalDTO.get().getModuleCode()
						.equalsIgnoreCase(ModuleEnum.CITIZEN.getCode())
						|| optionalDTO.get().getModuleCode().equalsIgnoreCase(ModuleEnum.BODYBUILDER.getCode())
						|| optionalDTO.get().getModuleCode().equalsIgnoreCase(ModuleEnum.ALTERVEHICLE.getCode()))) {
					updateCitizenPaymentStatus(paymentGateWayResponse.getPaymentStatus(), appFormNo,
							optionalDTO.get().getModuleCode());
				} else {
					updatePaymentStatusOfRegistrationDetails(paymentGateWayResponse);
				}
				return paymentGateWayResponse;

			} else {
				throw new BadRequestException(
						"Applicantion payment transaction details not found : Application No [ " + appFormNo + "]");
			}
		} else {
			throw new BadRequestException(ExceptionDescEnum.NULL_APP_REF_NUMBER.getDesciption());
		}

	}

	public Optional<PaymentTransactionDTO> getLatestTransactionDateByTransactionRefNumber(String applicationFormNo) {

		List<PaymentTransactionDTO> paymentList = paymentTransactionDAO.findByApplicationFormRefNum(applicationFormNo);
		if (paymentList != null && paymentList.size() > 0) {
			paymentList.sort((o1, o2) -> o2.getRequest().getRequestTime().compareTo(o1.getRequest().getRequestTime()));
			return Optional.of(paymentList.get(0));
		}
		return Optional.empty();
	}

	public Optional<PaymentTransactionDTO> getLatestTransactionDateByTransactionRefNumber(String applicationFormNo,
			String paymentTransactionNo) {

		Optional<PaymentTransactionDTO> paymentOptional = paymentTransactionDAO
				.findByApplicationFormRefNumAndPaymentTransactionNo(applicationFormNo, paymentTransactionNo);
		return paymentOptional;
	}

	@Override
	public PaymentReqParams convertPayments(TransactionDetailVO vo, String appFormNo) {

		List<KeyValue<String, String>> keyValues = new ArrayList<>();
		if (vo.getGatewayTypeEnum().equals(GatewayTypeEnum.PAYU)) {
			keyValues.add(new KeyValue<>("key", vo.getKey()));
			keyValues.add(new KeyValue<>("productinfo", vo.getProductInfo()));
			keyValues.add(new KeyValue<>("hash", vo.getHash()));
			keyValues.add(new KeyValue<>("txnid", vo.getTxnid()));
			if (vo.getAmount() != null) {
				keyValues.add(new KeyValue<>("amount", vo.getAmount().toString()));
			}
			keyValues.add(new KeyValue<>("firstname", vo.getFirstName()));
			keyValues.add(new KeyValue<>("email", vo.getEmail()));
			keyValues.add(new KeyValue<>("phone", vo.getPhone()));
			keyValues.add(new KeyValue<>("surl", vo.getSucessUrl()));
			keyValues.add(new KeyValue<>("furl", vo.getFailureUrl()));
			keyValues.add(new KeyValue<>("service_provider", vo.getServiceProvider()));
			keyValues.add(new KeyValue<>("udf1", vo.getFormNumber()));
			keyValues.add(new KeyValue<>("udf2", vo.getModule()));

		} else if (vo.getGatewayTypeEnum().equals(GatewayTypeEnum.SBI)) {
			keyValues.add(new KeyValue<>("encdata", vo.getEncdata()));
			keyValues.add(new KeyValue<>("merchant_code", vo.getMarchantCode()));
		}

		PaymentReqParams params = new PaymentReqParams();
		params.setPgUrl(vo.getPaymentUrl());
		params.setKeyValues(keyValues);
		params.setAppFormNo(appFormNo);

		return params;
	}

	/**
	 * 
	 * updateTransactionDetails used to update payment collection based on payment
	 * response
	 * 
	 * as form number
	 * 
	 *
	 * @param paymentGateWayResponse
	 */
	private void updateTransactionDetails(PaymentGateWayResponse paymentGateWayResponse) {
		if (!Objects.isNull(paymentGateWayResponse) && !Objects.isNull(paymentGateWayResponse.getAppTransNo())) {
			Optional<PaymentTransactionDTO> optionalDTO = null;

			if (paymentGateWayResponse.getGatewayTypeEnum().equals(GatewayTypeEnum.SBI)) {
				optionalDTO = paymentTransactionDAO.findTopByApplicationFormRefNumAndSbiTransactionNumber(
						paymentGateWayResponse.getAppTransNo(), paymentGateWayResponse.getTransactionNo());
			} else {
				optionalDTO = paymentTransactionDAO.findTopByApplicationFormRefNumAndTransactioNo(
						paymentGateWayResponse.getAppTransNo(), paymentGateWayResponse.getTransactionNo());
			}

			if (optionalDTO.isPresent()) {
				PaymentTransactionDTO payTransctionDTO = optionalDTO.get();
				PaymentTransactionResponseDTO response = new PaymentTransactionResponseDTO();
				response.setBankTransactionRefNum(paymentGateWayResponse.getBankTranRefNumber());
				response.setResponseDateStr(LocalDateTime.now().toString());
				response.setResponseTime(getCurrentTime());
				response.setIsHashValidationSucess(paymentGateWayResponse.getIsHashValidationSucess());
				payTransctionDTO.setPayStatus(paymentGateWayResponse.getPaymentStatus().getDescription());
				if (paymentGateWayResponse.getGatewayTypeEnum().equals(GatewayTypeEnum.SBI)) {
					response.setResponseDeatils(paymentGateWayResponse.getSbiResponce().toString());
					response.setSbiResponce(paymentGateWayResponse.getSbiResponce());
				} else if (paymentGateWayResponse.getGatewayTypeEnum().equals(GatewayTypeEnum.CFMS)) {
					response.setResponseDeatils(paymentGateWayResponse.getCfmsResponce().toString());
					response.setCfmsResponce(paymentGateWayResponse.getCfmsResponce());
				}

				else {
					response.setResponseDeatils(paymentGateWayResponse.getPayUResponse().toString());
					response.setPayUResponse(paymentGateWayResponse.getPayUResponse());
				}
				if (payTransctionDTO.getResponse() != null) {
					List<PaymentTransactionResponseDTO> responseLog = payTransctionDTO.getResponseLog();
					if (responseLog == null)
						responseLog = new ArrayList<>();

					responseLog.add(payTransctionDTO.getResponse());
					payTransctionDTO.setResponseLog(responseLog);
				}
				payTransctionDTO.setResponse(response);
				payTransctionDTO.setIsAgreeToEnablePayment(paymentGateWayResponse.getIsAgreeToEnablePayment());
				logger.debug("Tranasction detetails {} ", payTransctionDTO);
				logMovingService.movePaymnetsToLog(payTransctionDTO.getApplicationFormRefNum());
				paymentTransactionDAO.save(payTransctionDTO);
			} else {
				logger.error(
						"Applicantion payment transaction details not found : based on  Application form number and Transaction No");
				throw new BadRequestException(ExceptionDescEnum.NOTFOUND_APP_REF_NUMBER.getDesciption());
			}
		} else {
			logger.error("Applicant form  number not found  for payment responce save");
			throw new BadRequestException(ExceptionDescEnum.NULL_APP_REF_NUMBER.getDesciption());
		}

	}

	private LocalDateTime getCurrentTime() {
		return LocalDateTime.now();
	}

	/**
	 * to prepare FeeDetailsVO
	 * 
	 * @param feePartsMap
	 * @return
	 */
	private BreakPaymentsSaveVO preFeeDetails(List<BreakPayments> feePartsMap) {

		BreakPaymentsSaveVO breakPayments = new BreakPaymentsSaveVO();
		Double totalFees = 0.0;
		for (BreakPayments entry : feePartsMap) {

			totalFees += entry.getTotalFee();

		}
		breakPayments.setBreakPayments(feePartsMap);
		breakPayments.setGrandTotalFees(totalFees);
		return breakPayments;
	}

	private FeeDetailsVO preFeeDetailsVO(Map<String, FeePartsDetailsVo> feePartsMap) {
		FeeDetailsVO feeDetailsVO = new FeeDetailsVO();
		List<FeesVO> feeVOList = new ArrayList<FeesVO>();
		Double totalFees = 0.0;
		for (Map.Entry<String, FeePartsDetailsVo> entry : feePartsMap.entrySet()) {
			FeesVO feesVO = new FeesVO();
			FeePartsDetailsVo feePartsDetailsVo = entry.getValue();
			feesVO.setFeesType(entry.getKey());
			feesVO.setAmount(feePartsDetailsVo.getAmount());
			totalFees += feePartsDetailsVo.getAmount();
			feeVOList.add(feesVO);
		}
		feeDetailsVO.setFeeDetails(feeVOList);
		feeDetailsVO.setTotalFees(totalFees);
		return feeDetailsVO;
	}

	/**
	 * caluclate total fee amount
	 * 
	 * @param feePartsMap
	 * @return
	 */
	private Double getTotalamount(Map<String, FeePartsDetailsVo> feePartsMap) {
		Double totalFees = 0.0;
		for (Map.Entry<String, FeePartsDetailsVo> entry : feePartsMap.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			totalFees += entry.getValue().getAmount();
		}
		// TODO: need to implement below code insteded of above.
		// totalFees=feePartsMap.entrySet().stream().map(entry->entry.getValue().getAmount()).collect(Collectors.summingDouble(mapper))
		return totalFees;
	}

	@Override
	public BreakPaymentsSaveVO breakPayments(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum,
			String weightType, Long taxAmount, Long cesFee, String taxType, Boolean isRtoSecRejected,
			Boolean isRtoIvcnRejected, OwnerTypeEnum ownerType, String officeCode, String applicationNumber) {
		List<BreakPayments> feePartsMap = getPaymentBrakUPDetails(covs, serviceEnum, weightType, taxAmount, cesFee,
				taxType, isRtoSecRejected, isRtoIvcnRejected, ownerType, officeCode, applicationNumber);

		return preFeeDetails(feePartsMap);
	}

	public List<BreakPayments> getPaymentBrakUPDetails(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum,
			String weightType, Long taxAmount, Long cesFee, String taxType, boolean isRtoSecRejected,
			boolean isRtoIvcnRejected, OwnerTypeEnum ownerType, String officeCode, String applicationNumber) {
		List<ClassOfVehiclesVO> classOfVehiclesList = covs;
		Set<String> codeSet = classOfVehiclesList.stream().map(h -> h.getCovCode()).collect(Collectors.toSet());
		List<String> fees = new ArrayList<>();
		fees.add(ServiceCodeEnum.POSTAL_FEE.getTypeDesc());
		fees.add(ServiceCodeEnum.CARD.getTypeDesc());

		List<OwnerTypeEnum> ownerTypecheck = new ArrayList<>();
		ownerTypecheck.add(OwnerTypeEnum.Government);
		ownerTypecheck.add(OwnerTypeEnum.POLICE);

		List<BreakPayments> list = new LinkedList<>();
		Optional<StagingRegistrationDetailsDTO> regDTOOPT = stagingRegistrationDetailsSerivce
				.FindbBasedOnApplicationNo(applicationNumber);

		if (!regDTOOPT.isPresent()) {

			throw new BadRequestException(
					"Registration Details Is not Available with application number" + applicationNumber);
		}
		boolean allowToPayOnlyTax = Boolean.FALSE;
		Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
				.findByChassisNosIn(regDTOOPT.get().getVahanDetails().getChassisNumber());
		if (optionalTaxExcemption.isPresent()) {
			allowToPayOnlyTax = Boolean.TRUE;
		}
		if (allowToPayOnlyTax) {
			if (taxAmount != null) {
				BreakPayments breakPayments = new BreakPayments();
				Map<String, Double> breakPaymentDetails = new HashMap<>();
				Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
				breakPayments.setFeeType(taxType);
				breakPayments.setTotalFee(taxAmount.doubleValue());
				breakPaymentDetails.put(taxType, taxAmount.doubleValue());
				breakPayments.setBreakup(breakPaymentDetails);
				list.add(breakPayments);
				set.addAll(list);
				list.clear();
				list.addAll(set);

			}
			/*
			 * if (cesFee != null && cesFee != 0) { BreakPayments breakPayments = new
			 * BreakPayments(); Map<String, Double> breakPaymentDetails = new HashMap<>();
			 * Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
			 * breakPayments.setFeeType(ServiceCodeEnum.CESS_FEE.getCode());
			 * breakPayments.setTotalFee(cesFee.doubleValue());
			 * breakPaymentDetails.put(ServiceCodeEnum.CESS_FEE.getCode(),
			 * cesFee.doubleValue()); breakPayments.setBreakup(breakPaymentDetails);
			 * list.add(breakPayments); set.addAll(list); list.clear(); list.addAll(set); }
			 */
		} else {
			if (!isRtoSecRejected) {
				for (ServiceEnum id : serviceEnum) {
					List<FeesDTO> feeDTOList = feeDetailsCommonMehod(weightType, codeSet, id, null, StringUtils.EMPTY);
					BreakPayments breakPayments = new BreakPayments();
					Map<String, Double> breakPaymentDetails = new HashMap<>();
					Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
					Map<String, Double> serviceMap = new HashMap<>();
					Double breakamount = 0d;
					Boolean status = false;
					Integer count = 0;
					Optional<FeesDTO> serviceFeeoptional = null;
					String serviceFee = ServiceCodeEnum.SERVICE_FEE.getCode();

					List<String> Frexcemption = new ArrayList<String>();
					Frexcemption.add(ClassOfVehicleEnum.CHST.getCovCode());
					Frexcemption.add(ClassOfVehicleEnum.CHSN.getCovCode());

					boolean match = Frexcemption.stream().anyMatch(s -> codeSet.contains(s));

					if (!match && !id.equals(ServiceEnum.HPA)) {
						serviceFeeoptional = feesDao.findByServiceIdInAndCovcodeInAndFeesType(id.getId(), codeSet,
								serviceFee);
					} else if (id.equals(ServiceEnum.TEMPORARYREGISTRATION)) {
						serviceFeeoptional = feesDao.findByServiceIdInAndCovcodeInAndFeesType(id.getId(), codeSet,
								serviceFee);
					}

					if (ownerTypecheck.contains(ownerType)) {
						for (FeesDTO fee : feeDTOList) {
							if (serviceFeeoptional != null) {
								status = (count <= 0) ? true : false;
								serviceMap.put(serviceFeeoptional.get().getFeesType(),
										serviceFeeoptional.get().getAmount());
							}
							if (serviceFeeoptional != null && status) {
								breakamount = breakamount + serviceFeeoptional.get().getAmount();
								status = false;
								++count;
							}
							if (!fee.getFeesType().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getTypeDesc())) {
								if (fee.getAmount() != 0) {
									breakamount = breakamount + fee.getAmount();
									breakPayments.setTotalFee(breakamount);
									breakPaymentDetails.put(fee.getFeesType(), fee.getAmount());
								}
							} else {
								breakPayments.setTotalFee(breakamount);
							}
							breakPayments.setFeeType(fee.getServicedescription());
							breakPaymentDetails.putAll(serviceMap);
							breakPayments.setBreakup(breakPaymentDetails);
							list.add(breakPayments);
						}
					} else if (officeCode.equalsIgnoreCase("OTHER")) {
						for (FeesDTO fee : feeDTOList) {
							if (id.equals(ServiceEnum.TEMPORARYREGISTRATION)
									|| id.equals(ServiceEnum.HPA) && !fees.contains(fee.getFeesType())) {
								if (serviceFeeoptional != null) {
									status = (count <= 0) ? true : false;
									serviceMap.put(serviceFeeoptional.get().getFeesType(),
											serviceFeeoptional.get().getAmount());
								}
								breakPayments.setFeeType(fee.getServicedescription());
								if (serviceFeeoptional != null && status) {
									breakamount = breakamount + serviceFeeoptional.get().getAmount();
									status = false;
									++count;
								}
								if (fee.getAmount() != 0) {
									breakamount = breakamount + fee.getAmount();
									breakPayments.setTotalFee(breakamount);
									breakPaymentDetails.put(fee.getFeesType(), fee.getAmount());
								}
								breakPaymentDetails.putAll(serviceMap);
								breakPayments.setBreakup(breakPaymentDetails);
								list.add(breakPayments);

							}
						}
					} else {
						for (FeesDTO fee : feeDTOList) {
							if (!id.equals(ServiceEnum.HPA) || !fees.contains(fee.getFeesType())) {
								if (serviceFeeoptional != null && serviceFeeoptional.isPresent()) {
									status = (count <= 0) ? true : false;
									serviceMap.put(serviceFeeoptional.get().getFeesType(),
											serviceFeeoptional.get().getAmount());
								}
								breakPayments.setFeeType(fee.getServicedescription());
								if (serviceFeeoptional != null && status) {
									breakamount = breakamount + serviceFeeoptional.get().getAmount();
									status = false;
									++count;
								}
								if (fee.getAmount() != 0) {
									breakamount = breakamount + fee.getAmount();
									breakPayments.setTotalFee(breakamount);
									breakPaymentDetails.put(fee.getFeesType(), fee.getAmount());
								}
								breakPaymentDetails.putAll(serviceMap);
								breakPayments.setBreakup(breakPaymentDetails);
								list.add(breakPayments);
							}
						}
					}
					set.addAll(list);
					list.clear();
					list.addAll(set);
				}
			}

			if (isRtoIvcnRejected) {
				Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
				List<BreakPayments> calDifference = calInvalidRejctFee(applicationNumber, list);
				set.addAll(calDifference);
				list.clear();
				list.addAll(set);
			}

			if (taxAmount != null) {
				BreakPayments breakPayments = new BreakPayments();
				Map<String, Double> breakPaymentDetails = new HashMap<>();
				Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
				breakPayments.setFeeType(taxType);
				breakPayments.setTotalFee(taxAmount.doubleValue());
				breakPaymentDetails.put(taxType, taxAmount.doubleValue());
				breakPayments.setBreakup(breakPaymentDetails);
				list.add(breakPayments);
				set.addAll(list);
				list.clear();
				list.addAll(set);

			}
			if (cesFee != null && cesFee != 0) {
				BreakPayments breakPayments = new BreakPayments();
				Map<String, Double> breakPaymentDetails = new HashMap<>();
				Set<BreakPayments> set = new LinkedHashSet<BreakPayments>();
				breakPayments.setFeeType(ServiceCodeEnum.CESS_FEE.getCode());
				breakPayments.setTotalFee(cesFee.doubleValue());
				breakPaymentDetails.put(ServiceCodeEnum.CESS_FEE.getCode(), cesFee.doubleValue());
				breakPayments.setBreakup(breakPaymentDetails);
				list.add(breakPayments);
				set.addAll(list);
				list.clear();
				list.addAll(set);
			}
		}
		return list;
	}

	private List<BreakPayments> calInvalidRejctFee(String applicationNo, List<BreakPayments> list) {

		List<PaymentTransactionDTO> paymentTransactionDTOList = paymentTransactionDAO
				.findByApplicationFormRefNumAndPayStatus(applicationNo, PayStatusEnum.SUCCESS.getDescription());
		paymentTransactionDTOList
				.sort((p1, p2) -> p1.getRequest().getRequestTime().compareTo(p2.getRequest().getRequestTime()));
		if (paymentTransactionDTOList.size() > 0 && paymentTransactionDTOList.get(0).getBreakPaymentsSave() != null) {
			PaymentTransactionDTO paymentTransactionDTO = paymentTransactionDTOList.get(0);

			for (BreakPayments paidFees : paymentTransactionDTO.getBreakPaymentsSave().getBreakPayments()) {
				for (BreakPayments breakPayments : list) {
					if (paidFees.getFeeType().equalsIgnoreCase(breakPayments.getFeeType())) {
						if (breakPayments.getTotalFee() > paidFees.getTotalFee()) {
							breakPayments.setTotalFee(breakPayments.getTotalFee() - paidFees.getTotalFee());
						} else {
							breakPayments.setTotalFee(0d);
						}
						for (Map.Entry<String, Double> entry1 : paidFees.getBreakup().entrySet()) {
							for (Map.Entry<String, Double> entry2 : breakPayments.getBreakup().entrySet()) {
								if (entry1.getKey().equalsIgnoreCase(entry2.getKey())) {
									if (entry2.getValue() > entry1.getValue()) {
										entry2.setValue(entry2.getValue() - entry1.getValue());
									} else if (entry2.getValue() < entry1.getValue()) {
										entry2.setValue(entry2.getValue() - entry1.getValue());
									} else {
										entry2.setValue(0d);
									}
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	public TransactionDetailVO taxIntegration(StagingRegistrationDetailsDTO stagingRegistrationDetailsDTO,
			TransactionDetailVO transactionDetailVO) {
		transactionDetailVO.setTaxAmount((stagingRegistrationDetailsDTO.getTaxAmount()));
		transactionDetailVO.setTaxType(stagingRegistrationDetailsDTO.getTaxType());
		transactionDetailVO.setTaxvalidity(stagingRegistrationDetailsDTO.getTaxvalidity());
		if (stagingRegistrationDetailsDTO.getCesFee() != null) {
			transactionDetailVO.setCesFee(stagingRegistrationDetailsDTO.getCesFee());
			// vo.setFeesType("ces Fee");
		}
		if (stagingRegistrationDetailsDTO.getTaxArrears() != null)
			transactionDetailVO.setTaxArrears(stagingRegistrationDetailsDTO.getTaxArrears());
		if (stagingRegistrationDetailsDTO.getPenalty() != null)
			transactionDetailVO.setPenalty(stagingRegistrationDetailsDTO.getPenalty());
		if (stagingRegistrationDetailsDTO.getPenaltyArrears() != null)
			transactionDetailVO.setPenaltyArrears(stagingRegistrationDetailsDTO.getPenaltyArrears());
		if (stagingRegistrationDetailsDTO.getCesValidity() != null)
			transactionDetailVO.setCesValidity(stagingRegistrationDetailsDTO.getCesValidity());
		transactionDetailVO.setSecondVehicleTaxPaid(stagingRegistrationDetailsDTO.isSecondVehicleTaxPaid());
		return transactionDetailVO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<StagingRegistrationDetailsVO> generateTrNoForPaymentSuccess(String applicationNo) {

		TaxDetailsDTO taxDetailsDTO = null;
		Optional<PaymentTransactionDTO> optionalDTO = Optional.empty();
		Optional<StagingRegistrationDetailsDTO> regDTOOPT = stagingRegistrationDetailsSerivce
				.FindbBasedOnApplicationNo(applicationNo);

		if (!regDTOOPT.isPresent()) {
			logger.info("Registration Details is not found with [{}] this application number ", applicationNo);
			throw new BadRequestException("Registration Details Is not Available.");
		}

		if (!(regDTOOPT.get().getApplicationStatus()
				.equalsIgnoreCase(StatusRegistration.PAYMENTSUCCESS.getDescription())
				|| regDTOOPT.get().getApplicationStatus()
						.equalsIgnoreCase(StatusRegistration.SECORINVALIDDONE.getDescription()))) {
			logger.info("Invalid application status [{}] ", regDTOOPT.get().getApplicationStatus());
			throw new BadRequestException("Invalid application status");
		}

		StagingRegistrationDetailsDTO registrationDetailsDTO = regDTOOPT.get();

		if (registrationDetailsDTO.getPaymentTransactionNo() != null
				&& StringUtils.isNoneBlank(registrationDetailsDTO.getPaymentTransactionNo())) {
			optionalDTO = getLatestTransactionDateByTransactionRefNumber(applicationNo,
					registrationDetailsDTO.getPaymentTransactionNo());
		} else {
			optionalDTO = getLatestTransactionDateByTransactionRefNumber(applicationNo);
		}

		if (!optionalDTO.isPresent()) {
			logger.error("No Payment Transctions Found for application No [{}]", applicationNo);
			throw new BadRequestException(
					String.format("No Payment Details Found for Application {0} ", applicationNo));
		}
		if (!optionalDTO.get().getPayStatus().equalsIgnoreCase(PayStatusEnum.SUCCESS.getDescription())) {
			logger.error("Success payment details is not found with [{}] this application number ", applicationNo);
			throw new BadRequestException("Please verify the Payment Details ");
		}

		if (StringUtils.isNoneBlank(registrationDetailsDTO.getTrNo())
				&& StringUtils.isNoneBlank(registrationDetailsDTO.getPrNo())) {
			if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.SECORINVALIDDONE.getDescription())) {
				taxDetailsDTO = getTaxDetails(registrationDetailsDTO);
			} else if (registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.PAYMENTSUCCESS.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.TRGENERATED.getDescription());
			}

		} else {
			registrationDetailsDTO = geneationTRAndPRGenerations(registrationDetailsDTO, taxDetailsDTO);
		}
		boolean needToSkipsaveStaging = Boolean.FALSE;
		if (registrationDetailsDTO.getApplicationStatus()
				.equalsIgnoreCase(StatusRegistration.SECORINVALIDDONE.getDescription())) {
			if (registrationDetailsDTO.getEnclosures().stream().anyMatch(valu -> valu.getValue().stream().anyMatch(
					status -> status.getImageStaus().equalsIgnoreCase(StatusRegistration.REUPLOAD.getDescription())))) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.DEALERRESUBMISSION.getDescription());
			} else {
				if (registrationDetailsDTO.getSpecialNumberRequired()
						&& StringUtils.isBlank(registrationDetailsDTO.getPrNo())) {
					registrationDetailsDTO.setApplicationStatus(StatusRegistration.SPECIALNOPENDING.getDescription());

				} else {
					try {
						rtaService.assignPR(registrationDetailsDTO);
						needToSkipsaveStaging = Boolean.TRUE;
					} catch (Exception e) {
						logger.error("Exception while assign PR: {}", e);
					}
				}
			}
		}

		if (taxDetailsDTO == null) {
			List<TaxDetailsDTO> listOfTaxDto = taxDetailsDAO
					.findFirst10ByApplicationNoOrderByCreatedDateDesc(registrationDetailsDTO.getApplicationNo());
			if (listOfTaxDto.isEmpty()) {
				taxDetailsDTO = saveTaxDetails(registrationDetailsDTO, Boolean.FALSE, Boolean.FALSE);
				saveStagingDetails(taxDetailsDTO, registrationDetailsDTO, needToSkipsaveStaging);
			} else {
				citizenRegistrationService.updatePaidDateAsCreatedDate(listOfTaxDto);
				listOfTaxDto.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
				if (listOfTaxDto.stream().findFirst().get().getTaxAmount() != null
						&& registrationDetailsDTO.getTaxAmount() != null && !listOfTaxDto.stream().findFirst().get()
								.getTaxAmount().equals(registrationDetailsDTO.getTaxAmount())) {
					taxDetailsDTO = saveTaxDetails(registrationDetailsDTO, Boolean.FALSE, Boolean.FALSE);
					saveStagingDetails(taxDetailsDTO, registrationDetailsDTO, needToSkipsaveStaging);
					listOfTaxDto.clear();
				}
			}
		} else {
			saveStagingDetails(taxDetailsDTO, registrationDetailsDTO, needToSkipsaveStaging);
		}
		if (!needToSkipsaveStaging) {
			logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
			stagingRegistrationDetails.save(registrationDetailsDTO);
		}

		StagingRegistrationDetailsVO vo = stagingRegistrationDetailsMapper.convertStageVO(registrationDetailsDTO);

		return Optional.of(vo);

	}

	private void saveStagingDetails(TaxDetailsDTO taxDetailsDTO, StagingRegistrationDetailsDTO registrationDetailsDTO,
			boolean needToSkipsaveStaging) {
		if (taxDetailsDTO != null) {
			taxDetailsDAO.save(taxDetailsDTO);
		}
		logger.info("TR : Saving Tr Generation Details  :Application No [{}] ",
				registrationDetailsDTO.getApplicationNo());
		if (!needToSkipsaveStaging) {
			logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
			stagingRegistrationDetails.save(registrationDetailsDTO);

			// taxDetailsDAO.save(taxDetailsDTO);
			logger.info("TR : Generated TR :Application No [{}] , trNo :[{}]",
					registrationDetailsDTO.getApplicationNo(), registrationDetailsDTO.getTrNo());

			logger.info("Generating TR  :SaveTax Det No [{}] ", registrationDetailsDTO.getApplicationNo());

			Integer templateId = MessageTemplate.NEW_REG_TR.getId();
			try {
				notifications.sendNotifications(templateId, registrationDetailsDTO);
			} catch (Exception e) {
				logger.error("TR : Unable to Send Notification For Application No [{}] & Exception Message is [{}] ",
						registrationDetailsDTO.getApplicationNo(), e.getMessage());
			}
			logger.info("TR : Processe Completed [{}] ", registrationDetailsDTO.getApplicationNo());
		}
	}

	private void setPaymentBreakups(RegistrationDetailsDTO registrationDetailsDTO) {

		// Capturing existing hsrp amount
		List<PaymentTransactionDTO> paymentTransactionDTOList = paymentTransactionDAO
				.findByApplicationFormRefNum(registrationDetailsDTO.getApplicationNo());

		if (paymentTransactionDTOList.isEmpty()) {

			logger.error("No Payment Details Found For Application [{}]", registrationDetailsDTO.getApplicationNo());
			throw new BadRequestException(
					"No Payment Details Found For Application [{}] " + registrationDetailsDTO.getApplicationNo());

		}

		paymentTransactionDTOList
				.sort((p1, p2) -> p1.getRequest().getRequestTime().compareTo(p2.getRequest().getRequestTime()));

		if (!paymentTransactionDTOList.isEmpty()
				&& paymentTransactionDTOList.stream().findFirst().get().getBreakPaymentsSave() != null) {

			PaymentTransactionDTO paymentTransactionDTO = paymentTransactionDTOList.stream().findFirst().get();

			Double hsrpAmount = 0.0;

			for (BreakPayments bpsave : paymentTransactionDTO.getBreakPaymentsSave().getBreakPayments()) {

				if (bpsave.getBreakup() != null
						&& bpsave.getBreakup().get(ServiceCodeEnum.HSRP_FEE.getTypeDesc()) != null) {
					hsrpAmount += bpsave.getBreakup().get(ServiceCodeEnum.HSRP_FEE.getTypeDesc());
				}
			}

			registrationDetailsDTO.setHsrpfee(hsrpAmount);
		}

	}

	private StagingRegistrationDetailsDTO geneationTRAndPRGenerations(
			StagingRegistrationDetailsDTO registrationDetailsDTO, TaxDetailsDTO taxDetailsDTO) {

		String trNo = StringUtils.EMPTY;
		String prNo = StringUtils.EMPTY;

		String applicationNo = registrationDetailsDTO.getApplicationNo();

		Optional<UserDTO> userDTO = userDAO.findByUserId(registrationDetailsDTO.getDealerDetails().getDealerId());

		try {
			if (!userDTO.isPresent()) {
				logger.error("Dealer [{}] details are not available for Application No [{}]..",
						registrationDetailsDTO.getDealerDetails().getDealerId(), applicationNo);
				throw new BadRequestException("Dealer details are not available.");
			}

			Optional<OfficeDTO> officeDTO = officeDAO.findByOfficeCode(userDTO.get().getOffice().getOfficeCode());

			if (!officeDTO.isPresent()) {
				logger.error("Office [{}] details are not available for Application No [{}].",
						userDTO.get().getOffice().getOfficeCode(), applicationNo);
				throw new BadRequestException(
						"Office details are not available. Office Code:{}" + userDTO.get().getOffice().getOfficeCode());
			}

			if (officeDTO.get().getDistrict() == null) {
				logger.error("District details are not found for office [{}]",
						userDTO.get().getOffice().getOfficeCode());
				throw new BadRequestException(
						"District details are not found for office {}" + userDTO.get().getOffice().getOfficeCode());
			}

			if (registrationDetailsDTO.getTrNo() != null) {

				trNo = registrationDetailsDTO.getTrNo();
			} else {
				trNo = trSeriesService.geneateTrSeries(officeDTO.get().getDistrict());
			}
		} catch (Exception e) {

			logger.error("Failed to generate TR number fo application No:[{}]",
					registrationDetailsDTO.getApplicationNo());
			throw new BadRequestException("Failed to generate TR number fo application No [" + e.getMessage() + "]");

		}

		if (null == registrationDetailsDTO.getTrNo()) {
			registrationDetailsDTO.setTrNo(trNo);
			registrationDetailsDTO.setTrGeneratedDate(LocalDateTime.now());
		}

		if (null == registrationDetailsDTO.getPrNo()) {
			// TODO: Move to Enum Other
			if (!(registrationDetailsDTO.getOfficeDetails().getOfficeCode().equalsIgnoreCase("other")
					|| registrationDetailsDTO.getSpecialNumberRequired())) {
				prNo = prSeriesService.geneatePrNo(registrationDetailsDTO.getApplicationNo(), null, Boolean.FALSE,
						StringUtils.EMPTY, null, Optional.empty());
				registrationDetailsDTO.setPrNo(prNo);
			}
		}

		RegistrationValidityDTO regValidity = registrationDetailsDTO.getRegistrationValidity();

		if (regValidity == null) {
			regValidity = new RegistrationValidityDTO();
		}

		regValidity.setTrValidity(
				registrationDetailsDTO.getTrGeneratedDate().plusDays(ValidityEnum.TRVALIDITY.getValidity()));

		registrationDetailsDTO.setRegistrationValidity(regValidity);

		if (Arrays
				.asList(ClassOfVehicleEnum.CHSN.getCovCode(), ClassOfVehicleEnum.CHST.getCovCode(),
						ClassOfVehicleEnum.ARVT.getCovCode(), ClassOfVehicleEnum.IVCN.getCovCode())
				.contains(registrationDetailsDTO.getClassOfVehicle())) {
			registrationDetailsDTO.setApplicationStatus(StatusRegistration.CHASSISTRGENERATED.getDescription());
			JwtUser jwtUser = new JwtUser(ModuleEnum.CITIZEN.name(), ModuleEnum.CITIZEN.name(), null, null, null, null,
					null, null, false, false,true);
			rtaService.processFlow(registrationDetailsDTO, jwtUser, StatusRegistration.APPROVED.getDescription(),
					applicationNo, RoleEnum.CCO.getName());
		} /*
			 * else if(trailerVehicleVerification(registrationDetailsDTO.
			 * getClassOfVehicle())){
			 * registrationDetailsDTO.setApplicationStatus(StatusRegistration.
			 * TRAILERTRGENERATED.getDescription()); JwtUser jwtUser=new
			 * JwtUser(ModuleEnum.CITIZEN.name(), ModuleEnum.CITIZEN.name(),
			 * null, null, null, null, null, null, false, false);
			 * rtaService.processFlow(registrationDetailsDTO, jwtUser,
			 * registrationDetailsDTO.getApplicationStatus(), applicationNo,
			 * RoleEnum.CCO.getName());
			 * 
			 * }
			 */else {
			if (!registrationDetailsDTO.getApplicationStatus()
					.equalsIgnoreCase(StatusRegistration.SECORINVALIDDONE.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.TRGENERATED.getDescription());
			}
		}
		registrationDetailsDTO.setCreatedDate(LocalDateTime.now());

		taxDetailsDTO = saveTaxDetails(registrationDetailsDTO, Boolean.FALSE, Boolean.FALSE);
		// TODO Need to Invoke HSRP

		try {
			// Process HSRP Details
			setPaymentBreakups(registrationDetailsDTO);

			try {

				DataVO dataVO = regDetailsMapper.convertRegDTOtoDataVO(registrationDetailsDTO);
				if (StringUtils.isNoneBlank(registrationDetailsDTO.getDealerDetails().getDealerId())) {

					if (userDTO.isPresent()) {
						UserVO userDetailsVo = userMapper.convertEntity(userDTO.get());
						dataVO.setDealerName(userDetailsVo.getFirstName());
						dataVO.setDealerMail(userDetailsVo.getEmail());
						dataVO.setDealerRtoCode(
								userDetailsVo.getOffice() != null ? userDetailsVo.getOffice().getOfficeCode() : "");
					}

				}

				dataVO.setAuthorizationRefNo(this.getAuthRefNo(dataVO));
				integrationService.createHSRPTRData(dataVO);
				logger.info("HSRP Save Method success for Application No  [{}]", applicationNo);

			} catch (Exception e) {
				logger.info("Failed to save hsrp for application : [{}] & Exception : [{}]", e, applicationNo);
			}
		} catch (Exception e) {
			throw new BadRequestException(e.getMessage());
		}

		return registrationDetailsDTO;
	}

	private void moveFlowDetailsToLog(StagingRegistrationDetailsDTO registrationDetailsDTO) {

		if (registrationDetailsDTO.getRejectionHistory() != null) {
			if (registrationDetailsDTO.getRejectionHistoryLog() != null) {
				registrationDetailsDTO.getRejectionHistoryLog().add(registrationDetailsDTO.getRejectionHistory());
			} else {
				registrationDetailsDTO
						.setRejectionHistoryLog(Arrays.asList(registrationDetailsDTO.getRejectionHistory()));
			}
		}
		registrationDetailsDTO.setRejectionHistory(null);
	}

	private String getAuthRefNo(DataVO dataVO) {

		Long currentDate = DateUtil.toCurrentUTCTimeStamp();
		String authRefNo = "HSRPRTA" + dataVO.getTransactionNo() + currentDate.toString();
		return authRefNo;
	}

	private void addTaxDetails(String taxType, Double taxAmount, LocalDateTime paidDate, LocalDate validityTo,
			LocalDate validityFrom, List<Map<String, TaxComponentDTO>> list, Long penalty, Double taxArrears,
			Long penaltyArrears) {

		Map<String, TaxComponentDTO> taxMap = new HashMap<>();

		TaxComponentDTO tax = new TaxComponentDTO();
		tax.setTaxName(taxType);
		tax.setAmount(taxAmount);
		tax.setPaidDate(paidDate);
		tax.setValidityFrom(validityFrom);
		tax.setValidityTo(validityTo);
		if (penalty != null && penalty != 0) {
			tax.setPenalty(penalty);
		}
		if (penaltyArrears != null && penaltyArrears != 0) {
			tax.setPenaltyArrears(penaltyArrears);
		}
		if (taxArrears != null && taxArrears != 0) {
			tax.setTaxArrears(taxArrears);
		}
		taxMap.put(taxType, tax);
		list.add(taxMap);

	}

	private TaxDetailsDTO saveTaxDetails(StagingRegistrationDetailsDTO registrationDetailsDTO,
			boolean secoundVehicleDiffTaxPaid, boolean isChassisVehicle) {
		// need to update pr and second vehicel tax and flag
		TaxDetailsDTO dto = new TaxDetailsDTO();
		List<Map<String, TaxComponentDTO>> taxDetails = new ArrayList<>();

		dto.setApplicationNo(registrationDetailsDTO.getApplicationNo());
		dto.setCovCategory(registrationDetailsDTO.getOwnerType());
		dto.setModule(ModuleEnum.REG.getCode());
		dto.setPaymentPeriod(registrationDetailsDTO.getTaxType());
		dto.setTaxAmount(registrationDetailsDTO.getTaxAmount());
		dto.setTrNo(registrationDetailsDTO.getTrNo());
		dto.setPrNo(registrationDetailsDTO.getPrNo());
		dto.setTaxPeriodEnd(registrationDetailsDTO.getTaxvalidity());
		dto.setTaxPeriodFrom(LocalDate.now());
		if (registrationDetailsDTO.getPayTaxType() != null) {
			dto.setPayTaxType(registrationDetailsDTO.getPayTaxType());
		}
		Double taxArrears = 0d;
		Long penalty = 0l;
		Long penaltyArrears = 0l;
		if (registrationDetailsDTO.getTaxArrears() != null)
			taxArrears = Double.valueOf(registrationDetailsDTO.getTaxArrears().toString());
		// Tax
		// TODO : Change to Enum
		if (registrationDetailsDTO.getPenalty() != null)
			penalty = registrationDetailsDTO.getPenalty();
		if (registrationDetailsDTO.getPenaltyArrears() != null)
			penaltyArrears = registrationDetailsDTO.getPenaltyArrears();
		this.addTaxDetails(registrationDetailsDTO.getTaxType(),
				Double.valueOf(registrationDetailsDTO.getTaxAmount().toString()), LocalDateTime.now(),
				registrationDetailsDTO.getTaxvalidity(), LocalDate.now(), taxDetails, penalty, taxArrears,
				penaltyArrears);

		dto.setPermitType(permitcode);

		if (registrationDetailsDTO.getCesFee() != null) {

			dto.setCessFee(registrationDetailsDTO.getCesFee());
			dto.setCessPeriodEnd(registrationDetailsDTO.getCesValidity());
			dto.setCessPeriodFrom(LocalDate.now());

			this.addTaxDetails(ServiceCodeEnum.CESS_FEE.getCode(),
					Double.valueOf(registrationDetailsDTO.getCesFee().toString()), LocalDateTime.now(),
					registrationDetailsDTO.getCesValidity(), LocalDate.now(), taxDetails, null, null, null);
		}

		dto.setCreatedDate(LocalDateTime.now());
		dto.setTaxPaidDate(LocalDate.now());
		if (isChassisVehicle) {
			Optional<AlterationDTO> alterDetails = alterationDao
					.findByApplicationNo(registrationDetailsDTO.getApplicationNo());
			if (!alterDetails.isPresent()) {
				throw new BadRequestException(
						"No record found in alteration for: " + registrationDetailsDTO.getApplicationNo());
			}
			dto.setClassOfVehicle(alterDetails.get().getCov());
		} else {
			dto.setClassOfVehicle(registrationDetailsDTO.getClassOfVehicle());
		}
		dto.setTaxDetails(taxDetails);

		if (registrationDetailsDTO.isSecondVehicleTaxPaid() && secoundVehicleDiffTaxPaid
				&& (!registrationDetailsDTO.getIsFirstVehicle())) {
			dto.setSecondVehicleDiffTaxPaid(Boolean.TRUE);
		} else if (registrationDetailsDTO.isSecondVehicleTaxPaid()
				&& (registrationDetailsDTO.getIsFirstVehicle() != null
						&& !registrationDetailsDTO.getIsFirstVehicle())) {
			dto.setSecondVehicleTaxPaid(Boolean.TRUE);
		}
		dto.setInvoiceValue(registrationDetailsDTO.getInvoiceDetails().getInvoiceValue());
		dto.setTaxStatus(TaxStatusEnum.ACTIVE.getCode());
		dto.setRemarks("");
		dto.setOfficeCode(registrationDetailsDTO.getOfficeDetails().getOfficeCode());
		dto.setStateCode("AP");
		return dto;
	}

	@Override
	public TaxDetailsDTO saveCitizenTaxDetails(RegServiceVO regServiceDTO, boolean secoundVehicleDiffTaxPaid,
			boolean isChassisVehicle, String stateCode) {
		// need to update pr and second vehicel tax and flag
		TaxDetailsDTO dto = new TaxDetailsDTO();
		if (regServiceDTO.getTaxAmount() != null && regServiceDTO.getTaxAmount() != 0
				|| regServiceDTO.getCesFee() != null && regServiceDTO.getCesFee() != 0
				|| regServiceDTO.getGreenTaxAmount() != null && regServiceDTO.getGreenTaxAmount() != 0) {

			List<Map<String, TaxComponentDTO>> taxDetails = new ArrayList<>();

			dto.setApplicationNo(regServiceDTO.getRegistrationDetails().getApplicationNo());
			dto.setCovCategory(regServiceDTO.getRegistrationDetails().getOwnerType());
			dto.setModule(ModuleEnum.REG.getCode());
			dto.setPaymentPeriod(regServiceDTO.getTaxType());

			dto.setTrNo(regServiceDTO.getRegistrationDetails().getTrNo());
			dto.setPrNo(regServiceDTO.getRegistrationDetails().getPrNo());

			dto.setTaxPeriodFrom(LocalDate.now());
			if (regServiceDTO.getPayTaxType() != null) {
				dto.setPayTaxType(regServiceDTO.getPayTaxType());
			}
			// Tax
			// TODO : Change to Enum
			if (regServiceDTO.getTaxAmount() != null && regServiceDTO.getTaxAmount() != 0) {
				if (regServiceDTO.getQuaterTaxForNewGo() != null) {
					dto.setTaxAmount(regServiceDTO.getTaxAmount());
					dto.setTaxPeriodEnd(regServiceDTO.getTaxvalidity());
					Double taxArrears = 0d;
					Long penalty = 0l;
					Long penaltyArrears = 0l;
					if (regServiceDTO.getTaxArrears() != null)
						taxArrears = Double.valueOf(regServiceDTO.getTaxArrears().toString());
					if (regServiceDTO.getPenalty() != null)
						penalty = regServiceDTO.getPenalty();
					if (regServiceDTO.getPenaltyArrears() != null)
						penaltyArrears = regServiceDTO.getPenaltyArrears();
					this.addTaxDetails(TaxTypeEnum.QuarterlyTax.getDesc(),
							Double.valueOf(regServiceDTO.getQuaterTaxForNewGo().toString()), LocalDateTime.now(),
							citizenTaxService.validity(TaxTypeEnum.QuarterlyTax.getDesc()), LocalDate.now(), taxDetails,
							penalty, taxArrears, penaltyArrears);
					this.addTaxDetails(regServiceDTO.getTaxType(),
							Double.valueOf(regServiceDTO.getTaxAmount().toString()), LocalDateTime.now(),
							regServiceDTO.getTaxvalidity(), LocalDate.now(), taxDetails, 0l, 0d, 0l);

				} else {
					dto.setTaxAmount(regServiceDTO.getTaxAmount());
					dto.setTaxPeriodEnd(regServiceDTO.getTaxvalidity());
					Double taxArrears = 0d;
					Long penalty = 0l;
					Long penaltyArrears = 0l;
					if (regServiceDTO.getTaxArrears() != null)
						taxArrears = Double.valueOf(regServiceDTO.getTaxArrears().toString());
					if (regServiceDTO.getPenalty() != null)
						penalty = regServiceDTO.getPenalty();
					if (regServiceDTO.getPenaltyArrears() != null)
						penaltyArrears = regServiceDTO.getPenaltyArrears();
					this.addTaxDetails(regServiceDTO.getTaxType(),
							Double.valueOf(regServiceDTO.getTaxAmount().toString()), LocalDateTime.now(),
							regServiceDTO.getTaxvalidity(), LocalDate.now(), taxDetails, penalty, taxArrears,
							penaltyArrears);
				}

			}

			if (StringUtils.isNoneBlank(regServiceDTO.getPermitCode())) {
				dto.setPermitType(regServiceDTO.getPermitCode());
			} else {
				dto.setPermitType(permitcode);
			}

			if (regServiceDTO.getCesFee() != null && regServiceDTO.getCesFee() != 0) {

				dto.setCessFee(regServiceDTO.getCesFee());
				dto.setCessPeriodEnd(regServiceDTO.getCesValidity());
				dto.setCessPeriodFrom(LocalDate.now());

				this.addTaxDetails(ServiceCodeEnum.CESS_FEE.getCode(),
						Double.valueOf(regServiceDTO.getCesFee().toString()), LocalDateTime.now(),
						regServiceDTO.getCesValidity(), LocalDate.now(), taxDetails, null, null, null);
			}
			if (regServiceDTO.getGreenTaxAmount() != null && regServiceDTO.getGreenTaxAmount() != 0) {

				dto.setGreenTaxAmount(regServiceDTO.getGreenTaxAmount());
				dto.setGreenTaxPeriodEnd(regServiceDTO.getGreenTaxvalidity());

				this.addTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(),
						Double.valueOf(regServiceDTO.getGreenTaxAmount().toString()), LocalDateTime.now(),
						regServiceDTO.getGreenTaxvalidity(), LocalDate.now(), taxDetails, null, null, null);
			}
			dto.setCreatedDate(LocalDateTime.now());
			dto.setTaxPaidDate(LocalDate.now());
			if (regServiceDTO.getServiceType().stream()
					.anyMatch(service -> service.equals(ServiceEnum.ALTERATIONOFVEHICLE))) {
				/*
				 * Optional<AlterationDTO> alterDetails = alterationDao
				 * .findByApplicationNo(regServiceDTO.getRegistrationDetails().
				 * getApplicationNo( )); if (!alterDetails.isPresent()) { throw new
				 * BadRequestException("No record found in alteration for: " +
				 * regServiceDTO.getRegistrationDetails().getApplicationNo()); }
				 */

				dto.setClassOfVehicle(
						regServiceDTO.getAlterationVO().getCov() != null ? regServiceDTO.getAlterationVO().getCov()
								: regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			} else {
				dto.setClassOfVehicle(regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			}
			dto.setTaxDetails(taxDetails);

			if (regServiceDTO.getRegistrationDetails().isSecondVehicleTaxPaid() && secoundVehicleDiffTaxPaid
					&& (!regServiceDTO.getRegistrationDetails().getIsFirstVehicle())) {
				dto.setSecondVehicleDiffTaxPaid(Boolean.TRUE);
			} else if (regServiceDTO.getRegistrationDetails().isSecondVehicleTaxPaid()
					&& (regServiceDTO.getRegistrationDetails().getIsFirstVehicle() != null
							&& !regServiceDTO.getRegistrationDetails().getIsFirstVehicle())) {
				dto.setSecondVehicleTaxPaid(Boolean.TRUE);
			}
			if (regServiceDTO.getRegistrationDetails().getInvoiceDetails() != null
					&& regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue() != null) {
				dto.setInvoiceValue(regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue());
			}

			dto.setTaxStatus(TaxStatusEnum.ACTIVE.getCode());
			dto.setRemarks("");
			dto.setOfficeCode(regServiceDTO.getRegistrationDetails().getOfficeDetails().getOfficeCode());
			dto.setStateCode(stateCode);
			taxDetailsDAO.save(dto);
		}
		return dto;
	}

	@Override
	public String dncryptSBIData(String data) {
		PaymentGateWay paymentGateWay = paymentGatewayFactoryProvider.getPaymentGateWayInstance(GatewayTypeEnum.SBI);
		String sbiDecrptData = paymentGateWay.dncryptSBIData(data);
		return sbiDecrptData;
	}

	private Map<String, FeePartsDetailsVo> getFeePartsDetails(Set<Integer> serviceIds, Double applicationFee,
			Double servicesFee, String module) {
		// Getting header split amount and credits accounts.

		// TODO: Need to change to any constant

		List<FeesDTO> feesDTOList = feesDao.findByServiceIdInAndModuleCode(serviceIds, module);
		Set<String> codeSet = new HashSet<>();

		/*
		 * To allow test fee or service tax or any other fee without dependency on class
		 * of vechiles
		 */

		codeSet.add("TEST");
		Map<String, FeePartsDetailsVo> feePartsMap = new HashMap<>();
		for (FeesDTO llrFeesDeatailsDTO : feesDTOList) {
			ServiceCodeEnum serviceCodeEnum = ServiceCodeEnum
					.getSubHeadCodeEnum(llrFeesDeatailsDTO.getHOADESCRIPTION());

			// if (codeSet.contains(llrFeesDeatailsDTO.getCovCode())) {
			switch (serviceCodeEnum) {
			case COMPOUNDING_FEE:
				break;
			case LICENSE_FEE:
				if (feePartsMap.containsKey(ServiceCodeEnum.LICENSE_FEE.getTypeDesc())) {
					FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.LICENSE_FEE.getTypeDesc());
					feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + llrFeesDeatailsDTO.getAmount());
				} else {
					FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
					feePartsDetailsVo.setAmount(llrFeesDeatailsDTO.getAmount());
					feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
					feePartsMap.put(ServiceCodeEnum.LICENSE_FEE.getTypeDesc(), feePartsDetailsVo);
				}
				break;
			case REGISTRATION:
				break;
			case FITNESS_FEE:
				break;
			case PERMIT_FEE:
				break;
			case OTHER_RECEIPTS:
				break;
			case QLY_TAX:
				break;
			case LIFE_TAX:
				break;
			case GREEN_TAX:
				break;
			case SP_APPLICATION_FEE: {
				FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
				feePartsDetailsVo.setAmount(applicationFee);
				feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
				feePartsMap.put(ServiceCodeEnum.SP_APPLICATION_FEE.getTypeDesc(), feePartsDetailsVo);
			}
				break;
			case SERVICE_FEE: {
				FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
				feePartsDetailsVo.setAmount(servicesFee);
				feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
				feePartsMap.put(ServiceCodeEnum.SERVICE_FEE.getTypeDesc(), feePartsDetailsVo);
			}
				break;
			case POSTAL_FEE:
				if (!feePartsMap.containsKey(ServiceCodeEnum.POSTAL_FEE.getTypeDesc())) {
					FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
					feePartsDetailsVo.setAmount(llrFeesDeatailsDTO.getAmount());
					feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
					feePartsMap.put(ServiceCodeEnum.POSTAL_FEE.getTypeDesc(), feePartsDetailsVo);
				}
				break;
			case TEST_FEE:// It will add head one only
				if (feePartsMap.containsKey(ServiceCodeEnum.TEST_FEE.getTypeDesc())) {
					FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.TEST_FEE.getTypeDesc());
					feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + llrFeesDeatailsDTO.getAmount());
				} else {
					FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
					feePartsDetailsVo.setAmount(llrFeesDeatailsDTO.getAmount());
					feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
					feePartsMap.put(ServiceCodeEnum.TEST_FEE.getTypeDesc(), feePartsDetailsVo);
				}
				break;

			case CARD:// It will add head 1 only
				if (!feePartsMap.containsKey(ServiceCodeEnum.CARD.getTypeDesc())) {
					FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
					feePartsDetailsVo.setAmount(llrFeesDeatailsDTO.getAmount());
					feePartsDetailsVo.setHOA(llrFeesDeatailsDTO.getHOA());
					feePartsMap.put(ServiceCodeEnum.CARD.getTypeDesc(), feePartsDetailsVo);
				}
				break;
			default:
				logger.warn("SubHeadCode {} is not Found ", llrFeesDeatailsDTO.getServiceCode());
				break;
			}
			// } else {
			// log.warn("Not required class of vehicle code: {} or is not
			// mapped to Service {}
			// ",llrFeesDeatailsDTO.getCovCode(),serviceEnum);
			// }

		}

		return feePartsMap;

	}

	public Optional<String> processRefundByPaymentId(String transactionNo, String paymentId, Double refundAmount) {

		Optional<PaymentTransactionDTO> optionalDTO = paymentTransactionDAO.findByTransactioNo(transactionNo);
		if (optionalDTO.isPresent()) {
			PaymentTransactionDTO payTransctionDTO = optionalDTO.get();
			logger.debug("Tranasction detetails {} ", payTransctionDTO);

			PayURefundResponse payUVerifyResponse = processPayURefundByPaymentId(paymentId, refundAmount);

			// Save the refund response
			if (payTransctionDTO.getPayURefundResponse() != null) {
				if (payTransctionDTO.getPayURefundResponseLog() == null) {
					payTransctionDTO.setPayURefundResponseLog(new ArrayList<>());
				}
				payTransctionDTO.getPayURefundResponseLog().add(payTransctionDTO.getPayURefundResponse());
			}
			payTransctionDTO.setPayURefundResponse(payUVerifyResponse);
			logMovingService.movePaymnetsToLog(payTransctionDTO.getApplicationFormRefNum());
			paymentTransactionDAO.save(payTransctionDTO);

			if (payUVerifyResponse.getStatus() == 0 && StringUtils.isNotBlank(payUVerifyResponse.getResult())) {
				return Optional.of(payUVerifyResponse.getResult());
			}

			return Optional.empty();

		} else {
			logger.error("Applicantion payment transaction details not found : Payment transaction number:{}",
					paymentId);
			throw new BadRequestException(ExceptionDescEnum.NOTFOUNF_TRAN_NUMBER.getDesciption());
		}
	}

	private PayURefundResponse processPayURefundByPaymentId(String paymentId, Double amount) {

		logger.info("Doing PayUVerifyProcess for payu paymentId id: {}", paymentId);

		MultiValueMap<String, String> multiValueMap = getPayUVerifyReqParams(paymentId, amount);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		requestHeaders.add("Authorization", "Authorization");

		HttpEntity<MultiValueMap<String, String>> payuRequest = new HttpEntity<>(multiValueMap, requestHeaders);

		try {
			ResponseEntity<PayURefundResponse> response = restTemplate.postForEntity("payURefundUrl", payuRequest,
					PayURefundResponse.class);
			logger.info("response status from payu verify: {}", response.getStatusCode());

			if (response.hasBody()) {
				logger.info("payU responce body: {}", response.getBody());
				return response.getBody();
			}
			logger.info("No respopnce body from PayU Server, PayU responce Body:{}", response.getBody());
			throw new BadRequestException("No respopnce body from PayU Server");
		} catch (RestClientException rce) {
			logger.error("RestTemplate Exception while payu verification.{}", rce);
			throw new BadRequestException("RestTemplate Exception while payu verification");

		} catch (Exception e) {
			logger.error("Exception while payU verification.{}", e);
			throw new BadRequestException("Opps.. There is an Exception in payU server..Please try later.");
		}

	}

	private MultiValueMap<String, String> getPayUVerifyReqParams(String paymentId, Double refundAmount) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("paymentId", paymentId);
		map.add("refundAmount", refundAmount.toString());
		map.add("merchantKey", "payUKey");
		return map;
	}

	@Override
	public Map<String, String[]> getSliptingofSbiValue(String value) {
		Map<String, String[]> map = new HashMap<>();
		if (value.contains("|")) {
			String[] keyValuePairs = value.split(Pattern.quote("|"));
			for (String pair : keyValuePairs) {
				String[] entry = pair.split(Pattern.quote("="));
				if (entry.length > 1) {
					map.put(entry[0].trim(), new String[] { entry[1].trim() });
				} else {
					map.put(entry[0].trim(), new String[] { "" });
				}

			}
		}
		return map;
	}

	@Override
	public void updateCitizenPaymentStatus(PayStatusEnum payStatus, String applicationNo, String moduleCode) {

		if (ModuleEnum.BODYBUILDER.getCode().equalsIgnoreCase(moduleCode)) {
			StagingRegistrationDetailsDTO registrationDetailsDTO = getStagingDetails(applicationNo);
			if (PayStatusEnum.SUCCESS.getDescription().equals(payStatus.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.TAXPAID.getDescription());
				// registrationDetailsDTO.setBodyBuilding(Boolean.TRUE);
				registrationDetailsDTO.setDiffTaxPaid(Boolean.TRUE);
				TaxDetailsDTO taxDetailsDTO = saveTaxDetails(registrationDetailsDTO, Boolean.FALSE, Boolean.TRUE);
				taxDetailsDAO.save(taxDetailsDTO);
				logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
				stagingRegistrationDetails.save(registrationDetailsDTO);
			} else if (PayStatusEnum.FAILURE.getDescription().equals(payStatus.getDescription())) {
				registrationDetailsDTO.setApplicationStatus(StatusRegistration.CITIZENPAYMENTFAILED.getDescription());
				logMovingService.moveStagingToLog(registrationDetailsDTO.getApplicationNo());
				stagingRegistrationDetails.save(registrationDetailsDTO);
			}
		} else {

			// TODO query with application no
			Optional<RegServiceDTO> regDTOOPT = regServiceDAO.findByApplicationNo(applicationNo);

			RegServiceDTO registrationDetailsDTO = null;
			if (regDTOOPT.isPresent()) {
				registrationDetailsDTO = regDTOOPT.get();
			} else {
				// Change to Mesg
				throw new BadRequestException("Registration Details Is not Available.");
			}

			if (PayStatusEnum.SUCCESS.getDescription().equals(payStatus.getDescription())) {

				registrationDetailsDTO.setApplicationStatus(StatusRegistration.PAYMENTDONE);

				sendCitizenNotifications(registrationDetailsDTO);

				// if (registrationDetailsDTO.getAlterationDetails() != null
				// &&
				// registrationDetailsDTO.getAlterationDetails().getVehicleTypeFrom()
				// != null
				// &&
				// registrationDetailsDTO.getAlterationDetails().getVehicleTypeTo()
				// != null)
				// {
				// if
				// ((!registrationDetailsDTO.getAlterationDetails().getVehicleTypeFrom()
				// .equalsIgnoreCase(registrationDetailsDTO.getAlterationDetails().getVehicleTypeTo()))
				// &&
				// !registrationDetailsDTO.getAlterationDetails().isSpecialNoRequired()
				// &&
				// StringUtils.isEmpty(registrationDetailsDTO.getAlterationDetails().getPrNo()))
				// {
				// String prNo =
				// prSeriesService.geneatePrNo(registrationDetailsDTO.getApplicationNo(),
				// null,
				// Boolean.FALSE, StringUtils.EMPTY, ModuleEnum.CITIZEN,
				// Optional.of(regDTOOPT.get().getOfficeDetails()));
				// registrationDetailsDTO.getAlterationDetails().setPrNo(prNo);
				// }
				// }
				if (moduleCode.equalsIgnoreCase(ModuleEnum.ALTERVEHICLE.getCode())
						|| registrationDetailsDTO.getServiceIds() != null && registrationDetailsDTO.getServiceIds()
								.stream().anyMatch(id -> id.equals(ServiceEnum.DATAENTRY.getId()))) {
					registratrionServicesApprovals.incrmentIndex(registrationDetailsDTO, RoleEnum.MVI.getName());

				} else if (isFinalHPtransactions(registrationDetailsDTO)) {
					registratrionServicesApprovals.initiateApprovalProcessFlow(registrationDetailsDTO);
				}

				if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HIREPURCHASETERMINATION.getId())
						&& registrationDetailsDTO.getFinanceDetails() != null) {
					registrationDetailsDTO.setHptStatus(StatusRegistration.HPTINITIATED.getDescription());
				}

				if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HIREPURCHASETERMINATION.getId())
						&& !registrationDetailsDTO.getServiceIds().contains(ServiceEnum.TRANSFEROFOWNERSHIP.getId())
						&& !registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HPA.getId())) {

					if ((registrationDetailsDTO.getFinanceDetails() != null
							&& registrationDetailsDTO.getFinanceDetails().getUserId() == null)
							|| registrationDetailsDTO.getFinanceDetails() != null
									&& registrationDetailsDTO.getFinanceDetails().getUserId() != null && !regService
											.isOnlineFinance(registrationDetailsDTO.getFinanceDetails().getUserId())) {
						registratrionServicesApprovals.initiateApprovalProcessFlow(registrationDetailsDTO);

						/*
						 * if (registrationDetailsDTO.getFinanceDetails() != null &&
						 * registrationDetailsDTO.getFinanceDetails().getUserId( ) == null)
						 */

					}
				}

				RegServiceVO vo = regServiceMapper.convertEntity(registrationDetailsDTO);

				RegistrationDetailsDTO registrationDetails = null;
				boolean isWeightAlt = Boolean.FALSE;

				PermitDetailsDTO permitsDtos = null;
				if (vo.getServiceIds().stream().anyMatch(id -> id.equals(ServiceEnum.TAXATION.getId()))) {
					updateTaxDetailsInRegCollection(registrationDetailsDTO);
					if (registrationDetailsDTO.isWeightAlt()) {
						vo.setPayTaxType(TaxPayType.DIFF);
						Optional<FinalTaxHelper> mastertaxHelper = finalTaxHelperDAO
								.findByPrNoInAndWeightAltIsTrue(registrationDetailsDTO.getPrNo());
						if (mastertaxHelper.isPresent()) {
							FinalTaxHelper helper = mastertaxHelper.get();
							helper.setWeightAlt(Boolean.FALSE);
							finalTaxHelperDAO.save(helper);
						}
						Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(
								registrationDetailsDTO.getRegistrationDetails().getApplicationNo());
						if (!regOptional.isPresent()) {
							throw new BadRequestException("No records found for : " + applicationNo);
						}
						registrationDetails = regOptional.get();
						chengeWeightsForArvt(registrationDetails, registrationDetailsDTO);
						List<PermitDetailsDTO> listOfPermits = permitDetailsDAO
								.findByPrNoInAndPermitClassCodeAndPermitStatus(
										Arrays.asList(registrationDetailsDTO.getPrNo()),
										PermitsEnum.PermitType.PRIMARY.getPermitTypeCode(),
										PermitsEnum.ACTIVE.getDescription());
						List<PermitDetailsDTO> list = new ArrayList<>();
						if (!listOfPermits.isEmpty()) {
							for (PermitDetailsDTO permits : listOfPermits) {
								if (permits.getCreatedDate() != null) {
									list.add(permits);
								}
							}
							list.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
							permitsDtos = list.stream().findFirst().get();
							if (permitsDtos.getRdto() != null) {
								if (permitsDtos.getRdto().getClassOfVehicle()
										.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
									changewehitinPermit(registrationDetailsDTO, permitsDtos, registrationDetails);
								}
							}
						}

						isWeightAlt = Boolean.TRUE;
					}
				}
				saveCitizenTaxDetails(vo, Boolean.FALSE, Boolean.FALSE, "AP");
				boolean permitCheck = permitsService.verifyForPermitServices(registrationDetailsDTO.getServiceIds());
				if (registrationDetailsDTO.getServiceType().stream()
						.anyMatch(type -> type.equals(ServiceEnum.ALTERATIONOFVEHICLE))) {
					if (registrationDetailsDTO.getAlterationDetails().getAlterationService().stream()
							.anyMatch(type -> type.equals(AlterationTypeEnum.WEIGHT))) {
						permitCheck = Boolean.FALSE;
						registrationDetailsDTO.setApplicationStatus(StatusRegistration.APPROVED);
						Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(
								registrationDetailsDTO.getRegistrationDetails().getApplicationNo());
						if (!regOptional.isPresent()) {
							throw new BadRequestException("No records found for : " + applicationNo);
						}
						registrationDetails = regOptional.get();
						if (registrationDetails.getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
							chengeWeightsForArvt(registrationDetails, registrationDetailsDTO);
						} else {
							registrationDetails.getVahanDetails()
									.setOldGvw(registrationDetails.getVahanDetails().getGvw());
							registrationDetails.getVahanDetails()
									.setGvw(registrationDetailsDTO.getAlterationDetails().getGvw());
							registrationDetails.getVehicleDetails()
									.setOldGvw(registrationDetails.getVehicleDetails().getRlw());
							registrationDetails.getVehicleDetails()
									.setRlw(registrationDetailsDTO.getAlterationDetails().getGvw());
						}
						registrationDetails.setWeightAltDone(Boolean.TRUE);
						// getting permit documents
						if (registrationDetailsDTO.getServiceType().stream()
								.anyMatch(type -> type.equals(ServiceEnum.VARIATIONOFPERMIT))) {
							List<PermitDetailsDTO> listOfPermits = permitDetailsDAO
									.findByPrNoInAndPermitClassCodeAndPermitStatus(
											Arrays.asList(registrationDetailsDTO.getPrNo()),
											PermitsEnum.PermitType.PRIMARY.getPermitTypeCode(),
											PermitsEnum.ACTIVE.getDescription());
							List<PermitDetailsDTO> list = new ArrayList<>();
							if (!listOfPermits.isEmpty()) {
								for (PermitDetailsDTO permits : listOfPermits) {
									if (permits.getCreatedDate() != null) {
										list.add(permits);
									}
								}
								list.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
								permitsDtos = list.stream().findFirst().get();
								if (permitsDtos.getRdto() != null) {
									if (permitsDtos.getRdto().getClassOfVehicle()
											.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
										changewehitinPermit(registrationDetailsDTO, permitsDtos, null);
									} else {
										permitsDtos.getRdto().getVahanDetails()
												.setOldGvw(permitsDtos.getRdto().getVahanDetails().getGvw());
										permitsDtos.getRdto().getVehicleDetails()
												.setOldGvw(permitsDtos.getRdto().getVehicleDetails().getRlw());
										permitsDtos.getRdto().getVahanDetails()
												.setGvw(registrationDetailsDTO.getAlterationDetails().getGvw());
										permitsDtos.getRdto().getVehicleDetails()
												.setRlw(registrationDetailsDTO.getAlterationDetails().getGvw());
									}
								}
							}
						}
						isWeightAlt = Boolean.TRUE;
					}
				}
				if (permitCheck) {
					permitsService.savePermitDetailsForNewPermit(registrationDetailsDTO);

				}
				if ((registrationDetailsDTO.getServiceIds().stream()
						.anyMatch(id -> id.equals(ServiceEnum.NEWFC.getId()))
						|| registrationDetailsDTO.getServiceIds().stream()
								.anyMatch(id -> id.equals(ServiceEnum.RENEWALFC.getId()))
						|| registrationDetailsDTO.getServiceIds().stream()
								.anyMatch(id -> id.equals(ServiceEnum.OTHERSTATIONFC.getId())))
						&& registrationDetailsDTO.getServiceIds().size() == 1) {

					if (!registrationDetailsDTO.isPaidPyamentsForFC()) {
						registrationDetailsDTO.setPaidPyamentsForFC(Boolean.TRUE);
					}
					// saveStageFCDetailsForNewFC(registrationDetailsDTO);
					if (registrationDetailsDTO.getFitnessOtherStation()) {
						registratrionServicesApprovals.initiateApprovalProcessFlow(registrationDetailsDTO);
						registrationDetailsDTO.setIterationCount(1);
					}

					if (registrationDetailsDTO.getSlotDetails() != null
							&& registrationDetailsDTO.getSlotDetails().getSlotDate() != null
							&& registrationDetailsDTO.getApplicationNo() != null) {
						notifications.sendNotifications(MessageTemplate.REG_FCSLOT.getId(), registrationDetailsDTO);

					}
				}
				if (registrationDetailsDTO.getServiceIds().stream()
						.anyMatch(id -> id.equals(ServiceEnum.RENEWAL.getId()))) {
					if (!registrationDetailsDTO.isPaidPyamentsForRenewal()) {
						registrationDetailsDTO.setPaidPyamentsForRenewal(Boolean.TRUE);
					}
				}
				if ((registrationDetailsDTO.getServiceIds().stream()
						.anyMatch(id -> id.equals(ServiceEnum.DATAENTRY.getId())))
						&& (registrationDetailsDTO.getRegistrationDetails() != null)
						&& !(registrationDetailsDTO.getRegistrationDetails().isRegVehicleWithPR())) {
					notifications.sendNotifications(MessageTemplate.REG_FCSLOT.getId(), registrationDetailsDTO);
				}
				// PR No & Tr No updation
				// updateNonRegVehicleDetailsForDataEntry(registrationDetailsDTO);
				if (isWeightAlt) {
					if (registrationDetails == null) {
						throw new BadRequestException("No records found for : " + applicationNo);
					}
					if (permitsDtos != null) {
						permitDetailsDAO.save(permitsDtos);
					}
					registrationDetailDAO.save(registrationDetails);
				}
				regServiceDAO.save(registrationDetailsDTO);

			} else if (PayStatusEnum.FAILURE.getDescription().equals(payStatus.getDescription())) {

				if (regDTOOPT.get().getMviOfficeCode() != null && regDTOOPT.get().getSlotDetails() != null
						&& regDTOOPT.get().getSlotDetails().getSlotDate() != null) {
					/*
					 * slotsService.releaseLock(regDTOOPT.get().getOfficeCode(),
					 * regDTOOPT.get().getSlotDetails().getSlotDate());
					 */
					if (!(regDTOOPT.get().getAlterationDetails() != null
							&& regDTOOPT.get().getAlterationDetails().isMVIDone()))
						slotsService.releaseSlot(ModuleEnum.REG.toString(), regDTOOPT.get().getMviOfficeCode(),
								regDTOOPT.get().getSlotDetails().getSlotDate());
					regDTOOPT.get().getSlotDetails().setPaymentStatus(StatusRegistration.PAYMENTFAILED);
					/*
					 * if ((registrationDetailsDTO.getServiceIds().stream() .anyMatch(id ->
					 * id.equals(ServiceEnum.NEWFC.getId())) ||
					 * registrationDetailsDTO.getServiceIds().stream() .anyMatch(id ->
					 * id.equals(ServiceEnum.RENEWALFC.getId())) ||
					 * registrationDetailsDTO.getServiceIds().stream() .anyMatch(id ->
					 * id.equals(ServiceEnum.OTHERSTATIONFC.getId()))) &&
					 * registrationDetailsDTO.getServiceIds().size() == 1) {
					 * regDTOOPT.get().getSlotDetails().setPaymentStatus( StatusRegistration.
					 * PAYMENTFAILED); }
					 */
				}

				registrationDetailsDTO.setApplicationStatus(StatusRegistration.CITIZENPAYMENTFAILED);
				regServiceDAO.save(registrationDetailsDTO);
			}
		}
	}

	private void changewehitinPermit(RegServiceDTO registrationDetailsDTO, PermitDetailsDTO permitsDtos,
			RegistrationDetailsDTO registrationDetails1) {
		Integer newRlw = null;
		if (registrationDetailsDTO.isWeightAlt()) {
			permitsDtos.getRdto().getVahanDetails().setGvw(permitsDtos.getRdto().getVahanDetails().getOldGvw());
			permitsDtos.getRdto().getVehicleDetails().setRlw(permitsDtos.getRdto().getVahanDetails().getOldGvw());
			newRlw = registrationDetailsDTO.getGvw();
		} else {
			newRlw = registrationDetailsDTO.getAlterationDetails().getGvw();
		}
		if ((permitsDtos.getRdto().getVahanDetails().getTrailerChassisDetailsDTO() != null
				&& !permitsDtos.getRdto().getVahanDetails().getTrailerChassisDetailsDTO().isEmpty())
				&& (permitsDtos.getRdto().getVehicleDetails().getTrailers() != null
						&& !permitsDtos.getRdto().getVehicleDetails().getTrailers().isEmpty())) {

			Integer tarilergtw = permitsDtos.getRdto().getVahanDetails().getTrailerChassisDetailsDTO().stream()
					.findFirst().get().getGtw();
			for (TrailerChassisDetailsDTO trailerDetails : permitsDtos.getRdto().getVahanDetails()
					.getTrailerChassisDetailsDTO()) {
				if (trailerDetails.getGtw() > tarilergtw) {
					tarilergtw = trailerDetails.getGtw();
				}
			}
			Integer gvw = permitsDtos.getRdto().getVahanDetails().getGvw() + tarilergtw;

			Integer newGvw = newRlw - gvw;
			for (TrailerChassisDetailsDTO trailerDetails : permitsDtos.getRdto().getVahanDetails()
					.getTrailerChassisDetailsDTO()) {
				Integer gtw = trailerDetails.getGtw();
				trailerDetails.setGtw(gtw + newGvw);
			}
			for (TrailerChassisDetailsDTO trailerDetails : permitsDtos.getRdto().getVehicleDetails().getTrailers()) {
				Integer gtw = trailerDetails.getGtw();
				trailerDetails.setGtw(gtw + newGvw);
			}
			permitsDtos.getRdto().getVahanDetails().setOldGvw(gvw);
			permitsDtos.getRdto().getVehicleDetails().setOldGvw(gvw);
		} else {
			permitsDtos.getRdto().getVahanDetails().setOldGvw(permitsDtos.getRdto().getVahanDetails().getGvw());
			permitsDtos.getRdto().getVehicleDetails().setOldGvw(permitsDtos.getRdto().getVehicleDetails().getRlw());
			permitsDtos.getRdto().getVahanDetails().setGvw(newRlw);
			permitsDtos.getRdto().getVehicleDetails().setRlw(newRlw);
		}
	}

	private void chengeWeightsForArvt(RegistrationDetailsDTO registrationDetails,
			RegServiceDTO registrationDetailsDTO) {
		Integer newRlw = null;
		if (registrationDetailsDTO.isWeightAlt()) {
			registrationDetails.getVahanDetails().setGvw(registrationDetails.getVahanDetails().getOldGvw());
			registrationDetails.getVehicleDetails().setRlw(registrationDetails.getVahanDetails().getOldGvw());
			newRlw = registrationDetailsDTO.getGvw();
		} else {
			newRlw = registrationDetailsDTO.getAlterationDetails().getGvw();
		}
		if ((registrationDetails.getVahanDetails().getTrailerChassisDetailsDTO() != null
				&& !registrationDetails.getVahanDetails().getTrailerChassisDetailsDTO().isEmpty())
				&& (registrationDetails.getVehicleDetails().getTrailers() != null
						&& !registrationDetails.getVehicleDetails().getTrailers().isEmpty())) {
			Integer tarilergtw = registrationDetails.getVahanDetails().getTrailerChassisDetailsDTO().stream()
					.findFirst().get().getGtw();
			for (TrailerChassisDetailsDTO trailerDetails : registrationDetails.getVahanDetails()
					.getTrailerChassisDetailsDTO()) {
				if (trailerDetails.getGtw() > tarilergtw) {
					tarilergtw = trailerDetails.getGtw();
				}
			}
			Integer gvw = registrationDetails.getVahanDetails().getGvw() + tarilergtw;
			Integer newGvw = newRlw - gvw;
			for (TrailerChassisDetailsDTO trailerDetails : registrationDetails.getVahanDetails()
					.getTrailerChassisDetailsDTO()) {
				Integer gtw = trailerDetails.getGtw();
				trailerDetails.setGtw(gtw + newGvw);
			}
			for (TrailerChassisDetailsDTO trailerDetails : registrationDetails.getVehicleDetails().getTrailers()) {
				Integer gtw = trailerDetails.getGtw();
				trailerDetails.setGtw(gtw + newGvw);
			}
			registrationDetails.getVahanDetails().setOldGvw(gvw);
			registrationDetails.getVehicleDetails().setOldGvw(gvw);
		} else {
			registrationDetails.getVahanDetails().setOldGvw(registrationDetails.getVahanDetails().getGvw());
			registrationDetails.getVahanDetails().setGvw(newRlw);
			registrationDetails.getVehicleDetails().setOldGvw(registrationDetails.getVehicleDetails().getRlw());
			registrationDetails.getVehicleDetails().setRlw(newRlw);
		}
	}

	private boolean isFinalHPtransactions(RegServiceDTO registrationDetailsDTO) {

		List<Integer> ids = new ArrayList<>();

		ids.add(ServiceEnum.NEWPERMIT.getId());
		ids.add(ServiceEnum.TRANSFEROFPERMIT.getId());
		ids.add(ServiceEnum.PERMITCOA.getId());
		ids.add(ServiceEnum.SURRENDEROFPERMIT.getId());
		ids.add(ServiceEnum.RENEWALOFPERMIT.getId());
		ids.add(ServiceEnum.VARIATIONOFPERMIT.getId());
		ids.add(ServiceEnum.RENEWALOFAUTHCARD.getId());
		ids.add(ServiceEnum.EXTENSIONOFVALIDITY.getId());
		ids.add(ServiceEnum.HPA.getId());
		ids.add(ServiceEnum.HIREPURCHASETERMINATION.getId());
		ids.add(ServiceEnum.REPLACEMENTOFVEHICLE.getId());
		ids.add(ServiceEnum.TAXATION.getId());
		ids.add(ServiceEnum.ISSUEOFRECOMMENDATIONLETTER.getId());
		if (registrationDetailsDTO.getServiceType().stream()
				.anyMatch(type -> type.equals(ServiceEnum.ALTERATIONOFVEHICLE))) {
			if (registrationDetailsDTO.getAlterationDetails().getAlterationService().stream()
					.anyMatch(type -> type.equals(AlterationTypeEnum.WEIGHT))) {
				return Boolean.FALSE;
			}
		}
		if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.CHANGEOFADDRESS.getId())) {
			if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HPA.getId())
					|| registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HIREPURCHASETERMINATION.getId())) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}

		if (registrationDetailsDTO.getServiceIds().stream().anyMatch((id -> (ids.contains(id))))
				&& !registrationDetailsDTO.getServiceIds().contains(ServiceEnum.TRANSFEROFOWNERSHIP.getId())) {
			return Boolean.FALSE;
		}

		return isFinalTOPayment(registrationDetailsDTO);

	}

	private boolean isFinalTOPayment(RegServiceDTO registrationDetailsDTO) {
		if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HPA.getId())) {
			return false;
		}
		if (null != registrationDetailsDTO.getBuyerDetails()
				&& registrationDetailsDTO.getBuyerDetails().getTransferType() != null) {
			if (registrationDetailsDTO.getBuyerDetails().getTransferType().equals(TransferType.SALE)) {
				if (registrationDetailsDTO.getBuyerDetails().getBuyer() == null) {
					registrationDetailsDTO.setApplicationStatus(StatusRegistration.SELLERCOMPLETED);
					if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HIREPURCHASETERMINATION.getId())) {
						if (registrationDetailsDTO.getRegistrationDetails().getFinanceDetails() != null) {
							if (registrationDetailsDTO.getRegistrationDetails().getFinanceDetails()
									.getUserId() == null) {
								return false;
							}
							MasterUsersDTO userDTO = masterUsersDAO.findByUserId(
									registrationDetailsDTO.getRegistrationDetails().getFinanceDetails().getUserId());
							if (userDTO != null) {
								registrationDetailsDTO.setApplicationStatus(StatusRegistration.TOWITHHPTINITIATED);
							}
							return false;
						}
					}
					return false;

				}
			}
			if (registrationDetailsDTO.getBuyerDetails().getTransferType().equals(TransferType.DEATH)) {
				if (registrationDetailsDTO.getServiceIds().contains(ServiceEnum.HIREPURCHASETERMINATION.getId())) {
					if (registrationDetailsDTO.getRegistrationDetails().getFinanceDetails() != null
							&& registrationDetailsDTO.getRegistrationDetails().getFinanceDetails()
									.getUserId() == null) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}

	private StagingRegistrationDetailsDTO getStagingDetails(String applicationNo) {
		Optional<StagingRegistrationDetailsDTO> regDTOOPT = stagingRegistrationDetailsSerivce
				.FindbBasedOnApplicationNo(applicationNo);
		if (!regDTOOPT.isPresent()) {
			throw new BadRequestException(
					"Registration Details Is not Available with application number" + applicationNo);
		}
		StagingRegistrationDetailsDTO registrationDetailsDTO = regDTOOPT.get();
		return registrationDetailsDTO;
	}

	@Override
	public FeeDetailsVO getCitizenServiceFee(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum,
			String weightType, boolean isRequestToPay, String taxType, boolean isCalculateFc,
			boolean isApplicationFromMvi, boolean isChassesVehicle, String officeCode, String applicationNumber,
			boolean isOtherState, String regApplicationNo, String permitTypeCode, LocalDate slotDate,
			String seatingCapacity, String routeCode, Boolean isWeightAlt) {
		logger.debug("getServiceFee Start");

		Map<String, FeePartsDetailsVo> feePartsMap = this.calculateTaxAndFee(covs, serviceEnum, weightType,
				isRequestToPay, taxType, isCalculateFc, isApplicationFromMvi, isChassesVehicle, officeCode,
				applicationNumber, isOtherState, regApplicationNo, permitTypeCode, slotDate, seatingCapacity, routeCode,
				isWeightAlt);

		return preFeeDetailsVO(feePartsMap);
	}

	private void saveCitizenTaxDetails(TaxHelper taxAndValidity, TaxHelper cessAndValidity, String applicationNumber,
			TaxHelper greenTaxAndValidity) {
		RegServiceDTO dto = citizenRegistrationService.getRegServiceDetails(applicationNumber);
		if (taxAndValidity != null && taxAndValidity.getTaxAmountForPayments() > 0) {
			dto.setTaxAmount(taxAndValidity.getTaxAmountForPayments());
			dto.setTaxvalidity(taxAndValidity.getValidityTo());
			dto.setTaxType(taxAndValidity.getTaxName());
			if (StringUtils.isNoneBlank(taxAndValidity.getPermitType())) {
				dto.setPermitCode(taxAndValidity.getPermitType());
			}
			if (taxAndValidity.getPenalty() != null) {
				dto.setPenalty(taxAndValidity.getPenalty());
			}
			if (taxAndValidity.getPenaltyArrears() != null && taxAndValidity.getPenaltyArrears() > 0) {
				dto.setPenaltyArrears(taxAndValidity.getPenaltyArrears());
			}
			if (taxAndValidity.getTaxArrearsRound() != null && taxAndValidity.getTaxArrearsRound() > 0) {
				dto.setTaxArrears(taxAndValidity.getTaxArrearsRound());
			}
			if (taxAndValidity.getQuaterTaxForNewGo() != null && taxAndValidity.getQuaterTaxForNewGo() > 0) {
				dto.setQuaterTaxForNewGo(taxAndValidity.getQuaterTaxForNewGo());
			}

		}
		if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() > 0) {
			dto.setCesFee(cessAndValidity.getTaxAmountForPayments());
			dto.setCesValidity(cessAndValidity.getValidityTo());

		}
		if (greenTaxAndValidity != null && greenTaxAndValidity.getTaxAmountForPayments() > 0) {
			dto.setGreenTaxAmount(greenTaxAndValidity.getTaxAmountForPayments());
			dto.setGreenTaxvalidity(greenTaxAndValidity.getValidityTo());

		}
		if (taxAndValidity.getTaxPayType() != null) {
			dto.setPayTaxType(taxAndValidity.getTaxPayType());
		}
		regServiceDAO.save(dto);
	}

	private void saveStagingTaxDetails(TaxHelper taxAndValidity, TaxHelper cessAndValidity,
			StagingRegistrationDetailsDTO stagingRegDetails) {
		if (taxAndValidity != null) {
			stagingRegDetails.setTaxAmount(taxAndValidity.getTaxAmountForPayments());
			stagingRegDetails.setTaxvalidity(taxAndValidity.getValidityTo());
			if (taxAndValidity.getPenalty() != null && taxAndValidity.getPenalty() != 0) {
				stagingRegDetails.setPenalty(taxAndValidity.getPenalty());
			}
			if (taxAndValidity.getPenaltyArrears() != null && taxAndValidity.getPenaltyArrears() != 0) {
				stagingRegDetails.setPenaltyArrears(taxAndValidity.getPenaltyArrears());
			}
			if (taxAndValidity.getTaxArrearsRound() != null && taxAndValidity.getTaxArrearsRound() != 0) {
				stagingRegDetails.setTaxArrears(taxAndValidity.getTaxArrearsRound());
			}
		}
		if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() != null
				&& cessAndValidity.getTaxAmountForPayments() != 0) {
			stagingRegDetails.setCesFee(cessAndValidity.getTaxAmountForPayments());
			stagingRegDetails.setCesValidity(cessAndValidity.getValidityTo());

		}
		if (taxAndValidity.getTaxPayType() != null) {
			stagingRegDetails.setPayTaxType(taxAndValidity.getTaxPayType());
		}
		logMovingService.moveStagingToLog(stagingRegDetails.getApplicationNo());
		stagingRegistrationDetails.save(stagingRegDetails);

	}

	private Map<String, FeePartsDetailsVo> calculateTaxAndFee(List<ClassOfVehiclesVO> covs,
			List<ServiceEnum> serviceEnum, String weightType, boolean isRequestToPay, String taxType,
			boolean isCalculateFc, boolean isApplicationFromMvi, boolean isChassesVehicle, String officeCode,
			String applicationNumber, boolean isOtherState, String regApplicationNo, String permitTypeCode,
			LocalDate slotDate, String seatingCapacity, String routeCode, Boolean isWeightAlt) {
		Long taxAmount = 0l;
		Long cesFee = 0l;
		Long greenTaxAmount = 0l;
		Long TaxArrears = 0l;
		Long penalityArrears = 0l;
		Long quaterTaxForNewGo = 0l;
		boolean isARVTVehicle = Boolean.FALSE;
		if (taxType == null || StringUtils.isBlank(taxType)) {
			taxType = ServiceCodeEnum.QLY_TAX.getCode();
		}
		StagingRegistrationDetailsDTO stagingRegDetails = null;
		RegServiceDTO regServiceDetials = null;
		TaxHelper taxAndValidity = null;
		TaxHelper cessAndValidity = null;
		TaxHelper greenTaxAndValidity = null;
		Long penality = null;
		boolean skipTaxInPermits = false;
		if (StringUtils.isNotBlank(permitTypeCode)) {
			Optional<PropertiesDTO> propertiesDetails = propertiesDAO.findByTaxExceptionPermitCodeIn(permitTypeCode);
			if (propertiesDetails.isPresent()) {
				List<String> permitTypeList = propertiesDetails.get().getTaxExceptionPermitCode();
				skipTaxInPermits = permitTypeList.stream().anyMatch(code -> code.equalsIgnoreCase(permitTypeCode));
			}

		}

		if (isChassesVehicle) {
			Optional<StagingRegistrationDetailsDTO> stagingRegDetailsOptional = stagingRegistrationDetails
					.findByApplicationNo(applicationNumber);
			if (!stagingRegDetailsOptional.isPresent()) {
				throw new BadRequestException("Registration Details Is not Available: " + applicationNumber);
			}

			stagingRegDetails = stagingRegDetailsOptional.get();
			if (isChassesVehicle) {

				Optional<PropertiesDTO> dto = propertiesDAO.findByTaxFromDroolsTrue();
				if(!dto.isPresent()) {
				taxAndValidity = citizenTaxService.getTaxDetails(applicationNumber, isApplicationFromMvi,
						isChassesVehicle, taxType, isOtherState, regApplicationNo, serviceEnum, permitTypeCode,
						routeCode, isWeightAlt,null);
				}else {
					try {
						taxAndValidity = ruleEngineService.execute(applicationNumber, isApplicationFromMvi,
								isChassesVehicle, taxType, isOtherState, applicationNumber, serviceEnum, permitTypeCode,
								routeCode, isWeightAlt);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						throw new BadRequestException("Files not found: " + applicationNumber);
					}catch (BadRequestException bad) {
						// TODO Auto-generated catch block
						throw new BadRequestException("Exception occured: " + bad.getMessage());
					}
				}
				cessAndValidity = citizenTaxService.getTaxDetails(applicationNumber, isApplicationFromMvi,
						isChassesVehicle, ServiceCodeEnum.CESS_FEE.getCode(), isOtherState, regApplicationNo,
						serviceEnum, permitTypeCode, routeCode, isWeightAlt,null);
			
			}
			if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
				isARVTVehicle = Boolean.TRUE;
			}
			saveStagingTaxDetails(taxAndValidity, cessAndValidity, stagingRegDetails);

		} else {
			if (!skipTaxInPermits) {
				Optional<PropertiesDTO> dto = propertiesDAO.findByTaxFromDroolsTrue();
				if(!dto.isPresent()) {
				taxAndValidity = citizenTaxService.getTaxDetails(regApplicationNo, isApplicationFromMvi,
						isChassesVehicle, taxType, isOtherState, applicationNumber, serviceEnum, permitTypeCode,
						routeCode, isWeightAlt,null);
				}else {
					try {
						taxAndValidity = ruleEngineService.execute(regApplicationNo, isApplicationFromMvi,
								isChassesVehicle, taxType, isOtherState, applicationNumber, serviceEnum, permitTypeCode,
								routeCode, isWeightAlt);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						throw new BadRequestException("Files not found: " + applicationNumber);
					}catch (BadRequestException bad) {
						// TODO Auto-generated catch block
						throw new BadRequestException("Exception occured: " + bad.getMessage());
					}
				}
				cessAndValidity = citizenTaxService.getTaxDetails(regApplicationNo, isApplicationFromMvi,
						isChassesVehicle, ServiceCodeEnum.CESS_FEE.getCode(), isOtherState, applicationNumber,
						serviceEnum, permitTypeCode, routeCode, isWeightAlt,null);
				/*
				 * if (isOtherState) { Optional<RegServiceDTO> regServiceOptional =
				 * regServiceDAO.findByApplicationNo(regApplicationNo); if
				 * (!regServiceOptional.isPresent()) {
				 * logger.error("No record found in Reg Service for:[{}] " + regApplicationNo);
				 * throw new BadRequestException("No record found in Reg Service for:[{}] " +
				 * regApplicationNo); } if (regServiceOptional.get().getGreenTaxDetails() !=
				 * null && regServiceOptional.get().getGreenTaxDetails().getChallanNo() != null)
				 * { greenTaxAndValidity =
				 * citizenTaxService.greenTaxCalculation(regApplicationNo, serviceEnum,
				 * regServiceOptional.get()); } }
				 */
				if (!isOtherState)
					greenTaxAndValidity = citizenTaxService.greenTaxCalculation(regApplicationNo, serviceEnum);
				if (greenTaxAndValidity != null && greenTaxAndValidity.getTaxAmountForPayments() != 0) {
					greenTaxAmount = greenTaxAndValidity.getTaxAmountForPayments();
				}
				/*
				 * if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() !=
				 * 0) { cesFee = cessAndValidity.getTaxAmountForPayments(); }
				 */
				if (taxAndValidity != null) {
					taxAmount = taxAndValidity.getTaxAmountForPayments();
					taxType = taxAndValidity.getTaxName();
					if (taxAmount > 0) {
						if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() != 0) {
							cesFee = cessAndValidity.getTaxAmountForPayments();
						}
					}
					if (taxAndValidity.getPenalty() != null) {
						penality = taxAndValidity.getPenalty();
					}
					if (taxAndValidity.getPenaltyArrears() != null && taxAndValidity.getPenaltyArrears() != 0) {
						penalityArrears = taxAndValidity.getPenaltyArrears();
					}
					if (taxAndValidity.getTaxArrearsRound() != null && taxAndValidity.getTaxArrearsRound() != 0) {
						TaxArrears = taxAndValidity.getTaxArrearsRound();
					}
					if (taxAndValidity.getQuaterTaxForNewGo() != null && taxAndValidity.getQuaterTaxForNewGo() != 0) {
						quaterTaxForNewGo = taxAndValidity.getQuaterTaxForNewGo();
					}
				}
				if (isRequestToPay) {
					saveCitizenTaxDetails(taxAndValidity, cessAndValidity, applicationNumber, greenTaxAndValidity);
				}

			}
		}

		if (taxAndValidity != null) {
			if (!skipTaxInPermits) {
				taxAmount = taxAndValidity.getTaxAmountForPayments();
				taxType = taxAndValidity.getTaxName();
				if (taxAmount > 0) {
					if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() != null
							&& cessAndValidity.getTaxAmountForPayments() != 0) {
						if (!skipTaxInPermits) {
							cesFee = cessAndValidity.getTaxAmountForPayments();
						}
					}
				}
				if (taxAndValidity.getPenalty() != null) {
					penality = taxAndValidity.getPenalty();
				}
				if (taxAndValidity.getPenaltyArrears() != null && taxAndValidity.getPenaltyArrears() != 0) {
					penalityArrears = taxAndValidity.getPenaltyArrears();
				}
				if (taxAndValidity.getTaxArrearsRound() != null && taxAndValidity.getTaxArrearsRound() != 0) {
					TaxArrears = taxAndValidity.getTaxArrearsRound();
				}
				if (taxAndValidity.getQuaterTaxForNewGo() != null && taxAndValidity.getQuaterTaxForNewGo() != 0) {
					quaterTaxForNewGo = taxAndValidity.getQuaterTaxForNewGo();
				}
			}
		}
		if (isOtherState) {
			Optional<RegServiceDTO> regServiceOptional = regServiceDAO.findByApplicationNo(applicationNumber);
			if (!regServiceOptional.isPresent()) {
				throw new BadRequestException("Registration Details Is not Available: " + applicationNumber);
			}

			regServiceDetials = regServiceOptional.get();
		}
		Map<String, FeePartsDetailsVo> feePartsMap = this.getCitizenFeePartsDetails(covs, serviceEnum, weightType,
				taxAmount, cesFee, greenTaxAmount, taxType, isCalculateFc, isApplicationFromMvi, isChassesVehicle,
				officeCode, applicationNumber, isARVTVehicle, stagingRegDetails, penality, regApplicationNo,
				penalityArrears, TaxArrears, slotDate, permitTypeCode, seatingCapacity, quaterTaxForNewGo,
				isRequestToPay, regServiceDetials, isOtherState);

		return feePartsMap;
	}

	private Map<String, FeePartsDetailsVo> getCitizenFeePartsDetails(List<ClassOfVehiclesVO> covs,
			List<ServiceEnum> serviceEnum, String weightType, Long taxAmount, Long cesFee, Long grenTaxAmount,
			String taxType, boolean isCalculateFc, boolean isApplicationFromMvi, boolean isChassesVehicle,
			String officeCode, String applicationNumber, boolean isARVTVehicle,
			StagingRegistrationDetailsDTO stagingRegDetails, Long penality, String regApplicationNo,
			Long penalityArrears, Long TaxArrears, LocalDate slotDate, String permitTypeCode, String seatingCapacity,
			Long quaterTaxForNewGo, boolean isRequestToPay, RegServiceDTO regServiceDetials, boolean isOtherState) {
		List<ServiceEnum> list = new ArrayList<>();
		list.addAll(serviceEnum);
		Double lateFee = 0d;
		Double lateFeeForFC = 0d;
		Double taxServiceFeeAmount = 0d;
		List<ClassOfVehiclesVO> classOfVehiclesList = covs;
		Set<String> codeSet = classOfVehiclesList.stream().map(h -> h.getCovCode()).collect(Collectors.toSet());
		boolean ignoreApplicationFee = false;
		if (isCalculateFc) {
			list.add(ServiceEnum.NEWFC);
		} else if (isChassesVehicle && !isARVTVehicle) {
			// need to call pr fee and FC and hsrp fee
			if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode())) {
				list.add(ServiceEnum.NEWFC);

			}
			if (stagingRegDetails.getOwnerType().equals(OwnerTypeEnum.Government)
					|| stagingRegDetails.getOwnerType().equals(OwnerTypeEnum.POLICE)) {
				ignoreApplicationFee = Boolean.TRUE;
			}

		}
		if (isOtherState && regServiceDetials != null) {
			if (regServiceDetials.getRegistrationDetails().getOwnerType().equals(OwnerTypeEnum.Government)
					|| regServiceDetials.getRegistrationDetails().getOwnerType().equals(OwnerTypeEnum.POLICE)) {
				ignoreApplicationFee = Boolean.TRUE;
			}
		}
		codeSet.add("TEST");
		Map<String, FeePartsDetailsVo> feePartsMap = new HashMap<>();
		System.out.println("service Ids from FeeParts " + list);
		ServiceCodeEnum subHeadCodeEnum = null;
		for (ServiceEnum id : list) {
			List<FeesDTO> feeDTOList = feeDetailsCommonMehod(weightType, codeSet, id, permitTypeCode, seatingCapacity);
			String serviceFee = ServiceCodeEnum.SERVICE_FEE.getCode();

			List<String> Frexcemption = new ArrayList<String>();
			Frexcemption.add(ClassOfVehicleEnum.CHST.getCovCode());
			Frexcemption.add(ClassOfVehicleEnum.CHSN.getCovCode());
			FeesDTO feeDtos = new FeesDTO();
			FeesDTO taxDetails = new FeesDTO();
			FeesDTO penalityFee = new FeesDTO();
			FeesDTO penalityArrearsFee = new FeesDTO();
			FeesDTO taxArrearsFee = new FeesDTO();
			FeesDTO greenTaxDetails = new FeesDTO();
			FeesDTO lateFeeDetails = new FeesDTO();
			FeesDTO lateFeeDetailsForFc = new FeesDTO();
			FeesDTO taxServiceFee = new FeesDTO();
			FeesDTO quaterTaxForNewGoDetails = new FeesDTO();
			boolean match = Frexcemption.stream().anyMatch(s -> codeSet.contains(s));
			Optional<FeesDTO> serviceFeeoptional = null;

			List<ServiceEnum> serviceList = Arrays.asList(ServiceEnum.RENEWAL, ServiceEnum.RENEWALLATEFEE,
					ServiceEnum.RENEWALOFPERMIT);

			/*
			 * if (serviceEnum != null && (serviceEnum.stream().anyMatch(serEnum ->
			 * serEnum.equals((ServiceEnum.RENEWAL))) ||
			 * serviceEnum.stream().anyMatch(serEnum ->
			 * serEnum.equals((ServiceEnum.RENEWALLATEFEE))) ||
			 * serviceEnum.stream().anyMatch(serEnum ->
			 * serEnum.equals((ServiceEnum.RENEWALOFPERMIT))))) { lateFee =
			 * this.calculateLateFee(regApplicationNo, serviceEnum, slotDate,
			 * permitTypeCode, seatingCapacity, weightType, codeSet); if (lateFee != null &&
			 * lateFee != 0) {
			 * lateFeeDetails.setFeesType(ServiceCodeEnum.LATE_FEE.getTypeDesc());
			 * lateFeeDetails.setCovcode(classOfVehiclesList.stream().findFirst().get().
			 * getCovCode()); feeDTOList.add(lateFeeDetails); } }
			 */

			if (serviceEnum != null && serviceEnum.stream().anyMatch(serviceList::contains)) {
				lateFee = this.calculateLateFee(regApplicationNo, serviceEnum, slotDate, permitTypeCode,
						seatingCapacity, weightType, codeSet);
				if (lateFee != null && lateFee != 0) {
					lateFeeDetails.setFeesType(ServiceCodeEnum.LATE_FEE.getTypeDesc());
					lateFeeDetails.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
					feeDTOList.add(lateFeeDetails);
				}
			}
			if (taxAmount != null && taxType != null && taxAmount > 0) {
				taxDetails.setFeesType(taxType);
				taxDetails.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(taxDetails);
				if (taxServiceFeeAmount == 0) {
					taxServiceFeeAmount = this.getTaxServiceFee(taxAmount, isChassesVehicle, serviceEnum);
				}
				taxServiceFee.setFeesType(ServiceCodeEnum.TAXSERVICEFEE.getTypeDesc());
				taxServiceFee.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(taxServiceFee);
			}
			if (quaterTaxForNewGo != null && quaterTaxForNewGo > 0) {
				quaterTaxForNewGoDetails.setFeesType(TaxTypeEnum.QuarterlyTax.getDesc());
				quaterTaxForNewGoDetails.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(quaterTaxForNewGoDetails);
			}

			if (cesFee != null && cesFee != 0) {
				feeDtos.setFeesType(ServiceCodeEnum.CESS_FEE.getTypeDesc());
				feeDtos.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(feeDtos);
			}
			if (penality != null && penality != 0 && penality > 0) {
				penalityFee.setFeesType(ServiceCodeEnum.PENALTY.getTypeDesc());
				penalityFee.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(penalityFee);
			}
			if (penalityArrears != null && penalityArrears != 0 && penalityArrears > 0) {
				penalityArrearsFee.setFeesType(ServiceCodeEnum.PENALTYARREARS.getCode());
				penalityArrearsFee.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(penalityArrearsFee);
			}
			if (TaxArrears != null && TaxArrears != 0 && TaxArrears > 0) {
				taxArrearsFee.setFeesType(ServiceCodeEnum.TAXARREARS.getCode());
				taxArrearsFee.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(taxArrearsFee);
			}
			if (grenTaxAmount != null && grenTaxAmount != 0) {
				greenTaxDetails.setFeesType(ServiceCodeEnum.GREEN_TAX.getTypeDesc());
				greenTaxDetails.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
				feeDTOList.add(greenTaxDetails);
			}

			List<ServiceEnum> servicesList = Arrays.asList(ServiceEnum.NEWFC, ServiceEnum.RENEWALFC,
					ServiceEnum.OTHERSTATIONFC, ServiceEnum.FCLATEFEE);

			if (serviceEnum != null && serviceEnum.stream().anyMatch(servicesList::contains) && !isCalculateFc
					&& serviceEnum.size() == 1) {
				lateFeeForFC = this.getFcLateFee(regApplicationNo, slotDate, isRequestToPay);
				if (lateFeeForFC != null && lateFeeForFC != 0) {
					lateFeeDetailsForFc.setFeesType(ServiceCodeEnum.FC_LATE_FEE.getTypeDesc());
					lateFeeDetailsForFc.setCovcode(classOfVehiclesList.stream().findFirst().get().getCovCode());
					feeDTOList.add(lateFeeDetailsForFc);
				}
			}

			serviceFeeoptional = feesDao.findByServiceIdInAndCovcodeInAndFeesType(id.getId(), codeSet, serviceFee);
			if (serviceFeeoptional.isPresent()) {
				feeDTOList.add(serviceFeeoptional.get());
			}

			List<String> feeCodes = Arrays.asList(ServiceCodeEnum.CESS_FEE.getTypeDesc(),
					ServiceCodeEnum.LATE_FEE.getTypeDesc(), ServiceCodeEnum.FC_LATE_FEE.getTypeDesc(),
					ServiceCodeEnum.GREEN_TAX.getTypeDesc(), ServiceCodeEnum.LATE_FEE.getTypeDesc(),
					ServiceCodeEnum.TAXSERVICEFEE.getTypeDesc());
System.out.println("feeDTO list"+feeDTOList+" for id"+id);
			for (FeesDTO feeDto : feeDTOList) {
				if (feeCodes.contains(feeDto.getFeesType())) {
						System.out.println("suheadCode for registration if ");

					subHeadCodeEnum = ServiceCodeEnum.getSubHeadCodeEnumByDesc(feeDto.getFeesType());
				} else {
						System.out.println("suheadCode for registration else");

					subHeadCodeEnum = ServiceCodeEnum.getSubHeadCodeEnum(feeDto.getFeesType());
				}
				if (ignoreApplicationFee) {
					System.out.println("ignore application No for registration ");

					if (feeDto.getFeesType().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getCode())) {
						continue;
					}
				}
				if (feeDto.getModuleCode() != null && feeDto.getModuleCode().equalsIgnoreCase("FC")
						&& feeDto.getFeesType().equalsIgnoreCase(ServiceCodeEnum.REGISTRATION.getCode())) {
					subHeadCodeEnum = ServiceCodeEnum.FITNESS_FEE;
				}
				if (feeDto.getModuleCode() != null && feeDto.getModuleCode().equalsIgnoreCase("FC")
						&& feeDto.getFeesType().equalsIgnoreCase(ServiceCodeEnum.SERVICE_FEE.getCode())) {
					subHeadCodeEnum = ServiceCodeEnum.FITNESS_SERVICE_FEE;
				}
				if (codeSet.contains(feeDto.getCovCode())) {
					switch (subHeadCodeEnum) {

					case REGISTRATION:
						if (feePartsMap.containsKey(ServiceCodeEnum.REGISTRATION.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = feePartsMap
									.get(ServiceCodeEnum.REGISTRATION.getCode());
							feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + feeDto.getAmount());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						} else {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.REGISTRATION.getCode(), feePartsDetailsVo);

						}
						break;

					case FITNESS_FEE:
						if (!feePartsMap.containsKey(ServiceCodeEnum.FITNESS_FEE.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.FITNESS_FEE.getTypeDesc(), feePartsDetailsVo);
						}
						break;
					case FITNESS_SERVICE_FEE:
						Double fcserviceAmount = feeDto.getAmount();

						if (feePartsMap.containsKey(ServiceCodeEnum.FITNESS_SERVICE_FEE.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = feePartsMap
									.get(ServiceCodeEnum.FITNESS_SERVICE_FEE.getTypeDesc());
							if (feePartsDetailsVo.getAmount() < fcserviceAmount) {
								feePartsDetailsVo.setAmount(fcserviceAmount);
								feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							}
						} else {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(fcserviceAmount);
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.FITNESS_SERVICE_FEE.getTypeDesc(), feePartsDetailsVo);
						}
						break;
					case PERMIT_FEE:
						break;
					case CESS_FEE:
						if (!feePartsMap.containsKey(ServiceCodeEnum.CESS_FEE.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(cesFee.doubleValue());
							feePartsDetailsVo.setCredit_Account(cessCreditAccount);
							feePartsMap.put(ServiceCodeEnum.CESS_FEE.getTypeDesc(), feePartsDetailsVo);
						}
						break;
					case QLY_TAX:
						if (!feePartsMap.containsKey(ServiceCodeEnum.QLY_TAX.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							if (quaterTaxForNewGo != null && quaterTaxForNewGo > 0) {
								feePartsDetailsVo.setAmount(quaterTaxForNewGo.doubleValue());
							} else {
								feePartsDetailsVo.setAmount(taxAmount.doubleValue());
							}
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.QLY_TAX.getCode(), feePartsDetailsVo);
						}
						break;
					case HALF_TAX:
						if (!feePartsMap.containsKey(ServiceCodeEnum.HALF_TAX.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(taxAmount.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.HALF_TAX.getCode(), feePartsDetailsVo);
						}
						break;
					case YEAR_TAX:
						if (!feePartsMap.containsKey(ServiceCodeEnum.YEAR_TAX.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(taxAmount.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.YEAR_TAX.getCode(), feePartsDetailsVo);
						}
						break;
					case LIFE_TAX:
						if (!feePartsMap.containsKey(ServiceCodeEnum.LIFE_TAX.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(taxAmount.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(lifeTaxHoa);
							feePartsMap.put(ServiceCodeEnum.LIFE_TAX.getTypeDesc(), feePartsDetailsVo);
						}
						break;
					case GREEN_TAX:
						if (!feePartsMap.containsKey(ServiceCodeEnum.GREEN_TAX.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(grenTaxAmount.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(greenTaxHoa);
							feePartsMap.put(ServiceCodeEnum.GREEN_TAX.getTypeDesc(), feePartsDetailsVo);
						}
						break;
					case SERVICE_FEE:
						Double serviceAmount = feeDto.getAmount();

						if (feePartsMap.containsKey(ServiceCodeEnum.SERVICE_FEE.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = feePartsMap
									.get(ServiceCodeEnum.SERVICE_FEE.getCode());
							if (feePartsDetailsVo.getAmount() < serviceAmount) {
								feePartsDetailsVo.setAmount(serviceAmount);
							}
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						} else {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(serviceAmount);
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.SERVICE_FEE.getCode(), feePartsDetailsVo);
						}
						break;
					case POSTAL_FEE:
						if (!feePartsMap.containsKey(ServiceCodeEnum.POSTAL_FEE.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.POSTAL_FEE.getCode(), feePartsDetailsVo);
						}
						break;
					case TEST_FEE:// It will add head one only
						if (feePartsMap.containsKey(ServiceCodeEnum.TEST_FEE.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = feePartsMap.get(ServiceCodeEnum.TEST_FEE.getCode());
							feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + feeDto.getAmount());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						} else {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.TEST_FEE.getCode(), feePartsDetailsVo);
						}
						break;

					case CARD:// It will add head 1 only
						if (!feePartsMap.containsKey(ServiceCodeEnum.CARD.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.CARD.getCode(), feePartsDetailsVo);
						}
						break;

					case HSRP_FEE:
						/*
						 * if (feePartsMap.containsKey(ServiceCodeEnum.HSRP_FEE. getTypeDesc())) {
						 * FeePartsDetailsVo feePartsDetailsVo = feePartsMap
						 * .get(ServiceCodeEnum.HSRP_FEE.getTypeDesc());
						 * feePartsDetailsVo.setAmount(feePartsDetailsVo. getAmount() +
						 * feeDto.getAmount()); feePartsDetailsVo.setHOA(feePartsDetailsVo.getHOA());
						 * feePartsDetailsVo.setCredit_Account(hsrpCreditAccount ); } else {
						 * FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
						 * feePartsDetailsVo.setAmount(feeDto.getAmount());
						 * feePartsDetailsVo.setHOA(feeDto.getHOA());
						 * feePartsDetailsVo.setCredit_Account(hsrpCreditAccount );
						 * feePartsMap.put(ServiceCodeEnum.HSRP_FEE.getTypeDesc( ), feePartsDetailsVo);
						 * }
						 */
						break;
					case LATE_FEE:

						if (!feePartsMap.containsKey(ServiceCodeEnum.LATE_FEE.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(lateFee);
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							// feePartsDetailsVo.setHOA(greenTaxHoa);
							feePartsMap.put(ServiceCodeEnum.LATE_FEE.getTypeDesc(), feePartsDetailsVo);
						}

						break;
					case PENALTY: // TODO: it's for some services of DL like DL
						if (!feePartsMap.containsKey(ServiceCodeEnum.PENALTY.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(penality.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.PENALTY.getTypeDesc(), feePartsDetailsVo);
						}

						break;
					case PENALTYARREARS:
						if (!feePartsMap.containsKey(ServiceCodeEnum.PENALTYARREARS.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(penalityArrears.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.PENALTYARREARS.getTypeDesc(), feePartsDetailsVo);
						}

						break;
					case TAXARREARS: // TODO: it's for some services of DL like
										// DL
						if (!feePartsMap.containsKey(ServiceCodeEnum.TAXARREARS.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(TaxArrears.doubleValue());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsDetailsVo.setHOA(qutrelyTaxHoa);
							feePartsMap.put(ServiceCodeEnum.TAXARREARS.getTypeDesc(), feePartsDetailsVo);
						}

						break;
					case AUTHORIZATION:
						if (feePartsMap.containsKey(ServiceCodeEnum.AUTHORIZATION.getCode())) {
							FeePartsDetailsVo feePartsDetailsVo = feePartsMap
									.get(ServiceCodeEnum.AUTHORIZATION.getCode());
							feePartsDetailsVo.setAmount(feePartsDetailsVo.getAmount() + feeDto.getAmount());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
						} else {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(feeDto.getAmount());
							feePartsDetailsVo.setHOA(feeDto.getHOA());
							feePartsDetailsVo.setCredit_Account(rtaCreditAccount);
							feePartsMap.put(ServiceCodeEnum.AUTHORIZATION.getCode(), feePartsDetailsVo);

						}
						break;
					case FC_LATE_FEE:
						if (!feePartsMap.containsKey(ServiceCodeEnum.FC_LATE_FEE.getTypeDesc())) {
							FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
							feePartsDetailsVo.setAmount(lateFeeForFC);
							feePartsDetailsVo.setHOA(applicationFeeHoa);
							feePartsMap.put(ServiceCodeEnum.FC_LATE_FEE.getTypeDesc(), feePartsDetailsVo);
						}

						break;

					case TAXSERVICEFEE:
						if (!feePartsMap.containsKey(ServiceCodeEnum.TAXSERVICEFEE.getTypeDesc())) {
							if (taxServiceFeeAmount != 0) {
								FeePartsDetailsVo feePartsDetailsVo = new FeePartsDetailsVo();
								feePartsDetailsVo.setAmount(taxServiceFeeAmount);
								feePartsDetailsVo.setHOA(serviceFeeHoa);
								feePartsMap.put(ServiceCodeEnum.TAXSERVICEFEE.getTypeDesc(), feePartsDetailsVo);
							}
						}

						break;
					default:
						logger.warn("SubHeadCode {} is not Found ", feeDto.getHOADESCRIPTION());
						break;
					}
				} else {
					// log.warn("Not required class of vehicle code: {} or is
					// not
					// mapped to Service {}
					// ",llrFeesDeatailsDTO.getCovCode(),serviceEnum);
				}

			}
		}
		return feePartsMap;

	}

	public Double calculateLateFee(String applicationNo, List<ServiceEnum> services, LocalDate slotDate,
			String permitTypeCode, String seatingCapacity, String weightType, Set<String> codeSet) {
		Double lateFee = 0d;
		Long totalMonths = 0l;
		boolean isOldLateFeeCal = Boolean.FALSE;
		Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(applicationNo);
		if (!regOptional.isPresent()) {
			throw new BadRequestException("No records found for : " + applicationNo);
		}
		RegistrationDetailsDTO registrationDetails = regOptional.get();

		if (services.stream().anyMatch(serEnum -> serEnum.equals((ServiceEnum.RENEWALOFPERMIT)))) {
			lateFee = getPermitLateFee(permitTypeCode, seatingCapacity, registrationDetails, weightType, codeSet,
					services);
		}
		if (!services.stream().anyMatch(serEnum -> serEnum.equals((ServiceEnum.RENEWALOFPERMIT)))) {
			if (registrationDetails.getVehicleType().equalsIgnoreCase(CovCategory.T.getCode())) {
				throw new BadRequestException("Application not eligible for renewal" + applicationNo);
			}
		}
		if (registrationDetails.getVehicleType().equalsIgnoreCase(CovCategory.N.getCode())) {
			if (registrationDetails.getRegistrationValidity().getRegistrationValidity().isBefore(LocalDateTime.now())) {
				Optional<MasterLateFee> masterLateFeeOptional = masterLateFeeDAO
						.findByCovCodesIn(registrationDetails.getClassOfVehicle());
				if (!masterLateFeeOptional.isPresent()) {
					throw new BadRequestException(
							"No records found in late fee for : " + registrationDetails.getClassOfVehicle());
				}
				if (registrationDetails.getRegistrationValidity().getRegistrationValidity().toLocalDate()
						.isBefore(masterLateFeeOptional.get().getOldLateFeeUpToDate())) {
					isOldLateFeeCal = Boolean.TRUE;
					totalMonths = ChronoUnit.MONTHS.between(
							registrationDetails.getRegistrationValidity().getRegistrationValidity().toLocalDate(),
							masterLateFeeOptional.get().getOldLateFeeUpToDate());
					if (registrationDetails.getRegistrationValidity().getRegistrationValidity().toLocalDate()
							.getDayOfMonth() != masterLateFeeOptional.get().getOldLateFeeUpToDate().getDayOfMonth()) {
						totalMonths = totalMonths + 1;
					}
					double quatersRound = Math.ceil(totalMonths / 3d);
					if (quatersRound >= masterLateFeeOptional.get().getMaxLateQuaters()) {
						lateFee = masterLateFeeOptional.get().getMaxLateFee().doubleValue();
					} else {
						lateFee = quatersRound * masterLateFeeOptional.get().getOldLateFeePerQuater();
					}
				}
				LocalDate regValidity = registrationDetails.getRegistrationValidity().getRegistrationValidity()
						.toLocalDate();
				if (isOldLateFeeCal) {
					regValidity = masterLateFeeOptional.get().getOldLateFeeUpToDate().plusDays(1);
				}

				LocalDate oldSlotDate = getRenewalOldSlotDate(applicationNo, slotDate);

				totalMonths = 0l;
				if (oldSlotDate != null) {
					/*
					 * if (slotDate.isEqual(oldSlotDate)) { throw new
					 * BadRequestException("Slot date must not be previous slot date" ); }
					 */
					lateFee = 0d;
					regValidity = oldSlotDate;
					if (slotDate.isAfter(regValidity) || slotDate.isEqual(regValidity)) {
						if (slotDate.getMonthValue() != LocalDate.now().getMonthValue()) {
							// totalMonths =
							// ChronoUnit.MONTHS.between(slotDate,
							// LocalDate.now());
							totalMonths = (long) (slotDate.getMonthValue() - LocalDate.now().getMonthValue());
							return (totalMonths * masterLateFeeOptional.get().getAmount().doubleValue());
						} else {
							return (totalMonths * masterLateFeeOptional.get().getAmount().doubleValue());
						}
					}
				} else {
					totalMonths = ChronoUnit.MONTHS.between(regValidity, LocalDate.now());
					if (registrationDetails.getRegistrationValidity().getRegistrationValidity().toLocalDate()
							.getDayOfMonth() != LocalDate.now().getDayOfMonth() && oldSlotDate == null) {
						totalMonths = totalMonths + 1;
					}
				}

				return (totalMonths * masterLateFeeOptional.get().getAmount().doubleValue()) + lateFee;
			}
		}

		return lateFee;
	}

	private Double getPermitLateFee(String permitTypeCode, String seatingCapacity,
			RegistrationDetailsDTO registrationDetails, String weightType, Set<String> codeSet,
			List<ServiceEnum> services) {
		Double lateFee = 0d;
		Double applicationFee = 0d;

		List<FeesDTO> feeDTOList = methodForFetchingPermitFeesOnlyForApplicationFee(weightType, codeSet,
				services.get(0), permitTypeCode, registrationDetails.getVehicleDetails().getSeatingCapacity(),
				Boolean.FALSE);
		applicationFee = feeDTOList.stream().mapToDouble(s -> s.getAmount()).sum();

		Optional<PermitDetailsDTO> permitDetailsDTO = permitDetailsDAO
				.findByPrNoAndPermitTypeTypeofPermitAndPermitStatus(registrationDetails.getPrNo(),
						PermitType.PRIMARY.getPermitTypeCode(), PermitsEnum.ACTIVE.getDescription());

		if (!permitDetailsDTO.isPresent()
				&& permitDetailsDTO.get().getPermitValidityDetails().getPermitAuthorizationValidTo() == null) {
			throw new BadRequestException(
					"Permit Validt Details are not present with the PR no [ " + registrationDetails.getPrNo() + " ]");
		}
		long months = ChronoUnit.MONTHS.between(permitDetailsDTO.get().getPermitValidityDetails().getPermitValidTo(),
				LocalDate.now());

		if (months <= 0) {
			return lateFee;
		}

		lateFee = (applicationFee / 10) * months;

		if (lateFee > applicationFee) {
			lateFee = applicationFee;
		}
		return lateFee;
	}

	public Double getTaxServiceFee(Long taxAmount, boolean isChassesVehicle, List<ServiceEnum> serviceEnum) {
		List<ServiceEnum> listOfStatus = new ArrayList<>();
		listOfStatus.add(ServiceEnum.FR);
		listOfStatus.add(ServiceEnum.TEMPORARYREGISTRATION);
		if (!isChassesVehicle && !CollectionUtils.containsAny(listOfStatus, serviceEnum)) {
			Optional<PropertiesDTO> optionalTAxServiceFee = propertiesDAO
					.findByModuleAndTaxAmountToGreaterThanEqualAndTaxAmountFromLessThanEqual(ModuleEnum.TAX.toString(),
							taxAmount, taxAmount);
			if (!optionalTAxServiceFee.isPresent()) {
				throw new BadRequestException("No tax Service Fee collection");
			}
			return optionalTAxServiceFee.get().getAmount();
		}
		return 0d;
	}

	public Double getFcLateFee(String applicationNo, LocalDate slotDate, boolean isRequestToPay) {
		Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(applicationNo);
		if (!regOptional.isPresent()) {
			throw new BadRequestException("No records found for : " + applicationNo);
		}
		RegistrationDetailsDTO registrationDetails = regOptional.get();
		if (StringUtils.isBlank(registrationDetails.getVehicleType())) {
			throw new BadRequestException("Vehicle type missing " + applicationNo);
		}
		if (registrationDetails.getVehicleType().equalsIgnoreCase(CovCategory.N.getCode())) {
			throw new BadRequestException("Application not eligible for FC " + applicationNo);
		}
		if (slotDate == null) {
			// slotDate = LocalDate.now();
			throw new BadRequestException("slot date missed " + applicationNo);
		}
		if (registrationDetails.getRegistrationValidity().getFcValidity() == null) {
			List<FcDetailsDTO> fcDetailsList = fcDetailsDAO
					.findFirst5ByStatusIsTrueAndPrNoOrderByCreatedDateDesc(registrationDetails.getPrNo());
			if (fcDetailsList.isEmpty()) {
				return 0d;
			}
			FcDetailsDTO fcDetailsDTO = fcDetailsList.stream().findFirst().get();
			if (fcDetailsDTO.getFcValidUpto() == null) {
				throw new BadRequestException("Fc validUpto not found for prNo :" + registrationDetails.getPrNo());
			}
			registrationDetails.getRegistrationValidity().setFcValidity(fcDetailsDTO.getFcValidUpto());
		}

		Long totalMonthsException = 0l;
		if (registrationDetails.getRegistrationValidity().getFcValidity() != null) {
			if (registrationDetails.getRegistrationValidity().getFcValidity().isBefore(slotDate)) {
				Optional<MasterLateeFeeForFC> lateFeeForFC = masterLateeFeeForFCDAO.findByStatusTrue();
				if (!lateFeeForFC.isPresent()) {
					throw new BadRequestException("No master data for late fee FC ");
				}
				Pair<LocalDate, Boolean> oldSlotDate = getFcOldSlotDate(applicationNo, slotDate, isRequestToPay);
				LocalDate fcDate = registrationDetails.getRegistrationValidity().getFcValidity();
				if (registrationDetails.getRegistrationValidity().getFcValidity()
						.isBefore(lateFeeForFC.get().getLateFeeFromDate()) && oldSlotDate == null) {
					fcDate = lateFeeForFC.get().getLateFeeFromDate();
				}
				if (oldSlotDate == null) {
					if (registrationDetails.getStoppageDate() != null
							&& fcDate.isAfter(registrationDetails.getStoppageDate())) {
						if (registrationDetails.getVehicleStoppageRevokedDate() == null) {
							throw new BadRequestException("Vehicle revokation date missing. ");
						}
						totalMonthsException = ChronoUnit.DAYS.between(fcDate,
								registrationDetails.getVehicleStoppageRevokedDate());
					}
				}
				if (oldSlotDate != null) {
					fcDate = oldSlotDate.getFirst();
					if (oldSlotDate.getSecond()) {
						if (registrationDetails.getStoppageDate() != null
								&& registrationDetails.getRegistrationValidity().getFcValidity()
										.isBefore(registrationDetails.getStoppageDate())) {
							if (registrationDetails.getVehicleStoppageRevokedDate() == null) {
								throw new BadRequestException("Vehicle revokation date missing. ");
							}
							totalMonthsException = ChronoUnit.DAYS.between(registrationDetails.getStoppageDate(),
									registrationDetails.getVehicleStoppageRevokedDate());
						}
					}
				}
				if (!fcDate.isAfter(registrationDetails.getRegistrationValidity().getFcValidity())) {
					fcDate = registrationDetails.getRegistrationValidity().getFcValidity();
				}
				Long totalMonths = ChronoUnit.DAYS.between(fcDate, slotDate);
				Long lateFee = (totalMonths - totalMonthsException) * lateFeeForFC.get().getAmount();
				if (lateFee < 0) {
					return 0d;
				}
				if ((registrationDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARKT.getCovCode())
						|| registrationDetails.getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.TGVT.getCovCode())
						|| registrationDetails.getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.TTTT.getCovCode()))
						&& lateFee > 1000) {

					return 1000d;

				}
				return lateFee.doubleValue();

			}
		}
		return 0d;
	}

	private Pair<LocalDate, Boolean> getFcOldSlotDate(String applicationNo, LocalDate slotDate,
			boolean isRequestToPay) {
		List<RegServiceDTO> serviceList = regServiceDAO.findByRegistrationDetailsApplicationNoAndServiceIdsIn(
				applicationNo, Arrays.asList(ServiceEnum.NEWFC.getId(), ServiceEnum.RENEWALFC.getId(),
						ServiceEnum.OTHERSTATIONFC.getId()));
		if (!serviceList.isEmpty()) {
			serviceList.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
			RegServiceDTO dto = serviceList.stream().findFirst().get();
			if (dto.getApplicationStatus().equals(StatusRegistration.APPROVED)) {
				return null;
			}
			if (dto.isPaidPyamentsForFC()) {
				if (dto.getSlotDetails().getSlotDate() != null && dto.getSlotDetailsLog() == null) {
					return Pair.of(dto.getSlotDetails().getSlotDate(), Boolean.FALSE);
				}
				LocalDate privesSlotDate = null;
				if (dto.getSlotDetailsLog() == null) {
					if (!isRequestToPay) {
						if (dto.getSlotDetails().getPaymentStatus() == null
								|| !dto.getSlotDetails().getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
							privesSlotDate = dto.getSlotDetails().getSlotDate();
						}
					}
				}
				// LocalDate privesSlotDate =
				// dto.getSlotDetails().getSlotDate();
				if (dto.getSlotDetailsLog() != null) {
					/*
					 * LocalDate privesSlotDate =
					 * dto.getSlotDetailsLog().stream().findFirst().get(). getSlotDate(); if
					 * (dto.getSlotDetails().getPaymentStatus() == null) { if (!isRequestToPay) {
					 * privesSlotDate = dto.getSlotDetails().getSlotDate(); }
					 * 
					 * } if (dto.getSlotDetails().getPaymentStatus() == null ||
					 * !dto.getSlotDetails().getPaymentStatus().equals( StatusRegistration.
					 * PAYMENTFAILED)) { if (!slotDate.equals(dto.getSlotDetails().getSlotDate())) {
					 * privesSlotDate = dto.getSlotDetails().getSlotDate(); } }
					 * 
					 * for (SlotDetailsDTO slots : dto.getSlotDetailsLog()) { if
					 * (!privesSlotDate.isAfter(slots.getSlotDate())) { if (slots.getPaymentStatus()
					 * != null && slots.getPaymentStatus().equals(StatusRegistration.
					 * PAYMENTFAILED)) { continue; } privesSlotDate = slots.getSlotDate(); } }
					 */
					/*
					 * if(dto.getSlotDetails().getSlotDate().isAfter( privesSlotDate)) { return
					 * dto.getSlotDetails().getSlotDate(); }else {
					 */

					if (isRequestToPay) {
						privesSlotDate = getOldSlotDate(dto, privesSlotDate);
					} else {
						if (dto.getSlotDetails().getPaymentStatus() == null
								|| !dto.getSlotDetails().getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
							privesSlotDate = dto.getSlotDetails().getSlotDate();
						}
						privesSlotDate = getOldSlotDate(dto, privesSlotDate);

					}
					return Pair.of(privesSlotDate, Boolean.FALSE);
					/* } */
				}
			}
		}
		return null;
	}

	private LocalDate getOldSlotDate(RegServiceDTO dto, LocalDate privesSlotDate1) {
		if (privesSlotDate1 == null) {
			for (SlotDetailsDTO slotDto : dto.getSlotDetailsLog()) {
				if (slotDto.getPaymentStatus() == null
						|| !slotDto.getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
					privesSlotDate1 = slotDto.getSlotDate();
				}
			}
		}

		if (privesSlotDate1 != null) {
			for (SlotDetailsDTO slots : dto.getSlotDetailsLog()) {
				if (!privesSlotDate1.isAfter(slots.getSlotDate())) {
					if (slots.getPaymentStatus() != null
							&& slots.getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
						continue;
					}
					privesSlotDate1 = slots.getSlotDate();
				}
			}
		}
		if (privesSlotDate1 == null) {
			if (dto.getRegistrationDetails().getRegistrationValidity().getFcValidity() == null) {
				List<FcDetailsDTO> fcDetailsList = fcDetailsDAO
						.findFirst5ByStatusIsTrueAndPrNoOrderByCreatedDateDesc(dto.getPrNo());
				if (fcDetailsList.isEmpty()) {
					return null;
				}
				FcDetailsDTO fcDetailsDTO = fcDetailsList.stream().findFirst().get();
				if (fcDetailsDTO.getFcValidUpto() == null) {
					throw new BadRequestException("Fc validUpto not found for prNo :" + dto.getPrNo());
				}
				dto.getRegistrationDetails().getRegistrationValidity().setFcValidity(fcDetailsDTO.getFcValidUpto());
			}
			privesSlotDate1 = dto.getRegistrationDetails().getRegistrationValidity().getFcValidity();
		}
		return privesSlotDate1;
	}

	@Override
	public Optional<PaymentFailureResultVO> getPaymentDetailForFailueTransaction(String applicationNumber) {
		PaymentFailureResultVO paymentFailureResultVO = new PaymentFailureResultVO();
		GatewayTypeEnum gateWay = null;
		String failDescription = null;
		Optional<PaymentTransactionDTO> optionalDTO = getLatestTransactionDateByTransactionRefNumber(applicationNumber);

		if (optionalDTO.isPresent()) {
			PaymentTransactionDTO payTransctionDTO = optionalDTO.get();
			gateWay = GatewayTypeEnum.getGatewayTypeEnumById(payTransctionDTO.getPaymentGatewayType());
			if (gateWay.equals(GatewayTypeEnum.SBI)) {
				failDescription = payTransctionDTO.getResponse().getSbiResponce().getStatus_desc();
			}
			if (gateWay.equals(GatewayTypeEnum.PAYU)) {
				if (StringUtils.isNotEmpty(payTransctionDTO.getResponse().getPayUResponse().getField9())) {
					failDescription = payTransctionDTO.getResponse().getPayUResponse().getField9();
				} else {
					failDescription = payTransctionDTO.getResponse().getPayUResponse().getError_Message();
				}

			}
			paymentFailureResultVO.setGateWay(gateWay.getDescription());
			paymentFailureResultVO.setFailueDescription(failDescription);
			return Optional.of(paymentFailureResultVO);
		} else {
			return Optional.empty();
		}

	}

	@Override
	public TransactionDetailVO getTransactionDetailsForPayments(StagingRegistrationDetailsDTO stagingDetails,
			TransactionDetailVO transactionDetailVO) {
		Boolean isSecondVehRejected = false;
		Boolean isIvcnRejected = false;
		String weightDetails = null;
		String covName = stagingDetails.getClassOfVehicle();
		weightDetails = covService.getWeightTypeDetails(stagingDetails.getVahanDetails().getUnladenWeight());
		logger.info("Weight Type [{}] for Application No :[{}]", weightDetails, stagingDetails.getApplicationNo());
		ClassOfVehiclesVO covDetails = covService.findByCovCode(covName);
		List<ServiceEnum> servicId = new ArrayList<>();
		servicId.add(ServiceEnum.TEMPORARYREGISTRATION);
		servicId.add(ServiceEnum.FR);
		FeeDetailInput feeDetailInput = new FeeDetailInput();
		feeDetailInput.setCovs(Arrays.asList(covDetails));
		if (!stagingDetails.getOwnerType().equals(OwnerTypeEnum.Government)) {
			if (covDetails.getCategory().equalsIgnoreCase("T")) {
				servicId.add(ServiceEnum.NEWFC);
				weightDetails = covService.getWeightTypeDetails(stagingDetails.getVahanDetails().getGvw());
			}
			if (null != stagingDetails.getIsFinancier() && stagingDetails.getIsFinancier() == true) {
				servicId.add(ServiceEnum.HPA);
			}
		}

		GatewayTypeEnum payGatewayTypeEnum = null;

		if (transactionDetailVO.getGatewayType() != null) {
			payGatewayTypeEnum = GatewayTypeEnum
					.getGatewayTypeEnumByDesc(transactionDetailVO.getGatewayType().getDescription());
		} else {
			payGatewayTypeEnum = GatewayTypeEnum.CFMS;
		}
		checkForActiveGateWay(payGatewayTypeEnum);
		transactionDetailVO.setGatewayTypeEnum(payGatewayTypeEnum);
		transactionDetailVO.setCovs(Arrays.asList(covDetails));
		transactionDetailVO.setServiceEnumList(servicId);
		transactionDetailVO.setWeightType(weightDetails);
		transactionDetailVO.setFirstName(stagingDetails.getApplicantDetails().getDisplayName());
		transactionDetailVO.setEmail(stagingDetails.getApplicantDetails().getContact().getEmail());
		transactionDetailVO.setPhone(stagingDetails.getApplicantDetails().getContact().getMobile());
		transactionDetailVO.setModule(ModuleEnum.REG.getCode());
		transactionDetailVO.setOwnerType(stagingDetails.getOwnerType());
		transactionDetailVO.setOfficeCode(stagingDetails.getOfficeDetails().getOfficeCode());
		if ((stagingDetails.getRejectionHistory() != null) || (stagingDetails.getRejectionHistory() != null)) {
			if ((stagingDetails.getRejectionHistory().getIsSecondVehicleRejected() != null
					&& stagingDetails.getRejectionHistory().getIsSecondVehicleRejected())) {
				isSecondVehRejected = true;

			}

			if ((stagingDetails.getRejectionHistory().getIsInvalidVehicleRejection() != null
					&& stagingDetails.getRejectionHistory().getIsInvalidVehicleRejection())) {
				isIvcnRejected = true;
			}
		}
		/*
		 * if (isSecondVehRejected || isIvcnRejected) {
		 * registrationService.updatePaymentStatusOfSecondOrInvalidTax(Optional. of(
		 * stagingDetails)); } else {
		 * registrationService.updatePaymentStatusOfApplicant(Optional.of(
		 * stagingDetails )); }
		 */
		transactionDetailVO.setPaymentTransactionNo(UUID.randomUUID().toString());
		transactionDetailVO.setRtoSecRejected(isSecondVehRejected);
		transactionDetailVO.setRtoRejectedIvcn(isIvcnRejected);
		transactionDetailVO.setTxnid(getTransactionNumber(transactionDetailVO));
		return transactionDetailVO;
	}

	private void checkForActiveGateWay(GatewayTypeEnum payGatewayTypeEnum) {
		Optional<GateWayDTO> gateWay = gatewayDao.findByGateWayTypeAndStatusTrue(payGatewayTypeEnum);
		if (!gateWay.isPresent()) {
			throw new BadRequestException("Please select CFMS gateway for Payment");
		}

	}

	@Override
	public String paymentsVerifiactionInStaging(StagingRegistrationDetailsDTO stagingDetails,
			boolean verifyPaymentFlag) {

		String paymentStatus = null;
		String applicationNo = stagingDetails.getApplicationNo();
		boolean flagVerification = false;
		String errorMessage = null;

		Optional<PaymentTransactionDTO> paymentDetails = Optional.empty();

		if ((stagingDetails.getRejectionHistory() != null) || (stagingDetails.getRejectionHistory() != null)) {
			if ((stagingDetails.getRejectionHistory().getIsSecondVehicleRejected() != null
					&& stagingDetails.getRejectionHistory().getIsSecondVehicleRejected())) {
				flagVerification = true;

			}

			if ((stagingDetails.getRejectionHistory().getIsInvalidVehicleRejection() != null
					&& stagingDetails.getRejectionHistory().getIsInvalidVehicleRejection())) {
				flagVerification = true;
			}
		}

		String moduleCode = StringUtils.EMPTY;
		if (stagingDetails.getApplicationStatus()
				.equalsIgnoreCase(StatusRegistration.CITIZENPAYMENTPENDING.getDescription())) {
			moduleCode = ModuleEnum.BODYBUILDER.getCode();
		} else {
			moduleCode = ModuleEnum.REG.getCode();
		}

		if (stagingDetails.getPaymentTransactionNo() != null
				&& StringUtils.isNoneBlank(stagingDetails.getPaymentTransactionNo())) {
			paymentDetails = this.getLatestTransactionDateByTransactionRefNumberByModuleCode(applicationNo, moduleCode,
					stagingDetails.getPaymentTransactionNo());
		} else {
			// TODO need to remove else part
			paymentDetails = getLatestTransactionDateByTransactionRefNumberByModuleCode(applicationNo, moduleCode);
		}

		if (paymentDetails.isPresent()) {
			paymentStatus = paymentDetails.get().getPayStatus();

			if (paymentStatus.equalsIgnoreCase(PayStatusEnum.SUCCESS.getDescription()) && !flagVerification
					&& !verifyPaymentFlag) {

				errorMessage = "Your previous payment is already Completed. Please use get TR to generate TR Number."
						+ " Application No [" + applicationNo + " ]";
				throw new BadRequestException(errorMessage);
			}

			if (paymentStatus.equalsIgnoreCase(PayStatusEnum.PENDINGFROMBANK.getDescription()) && !verifyPaymentFlag) {

				errorMessage = "Your previous payment is " + " Pending" + " please verify it." + " Application No ["
						+ applicationNo + "]";
				throw new BadRequestException(errorMessage);
			}
		}
		return errorMessage;
	}

	public Optional<PaymentTransactionDTO> getLatestTransactionDateByTransactionRefNumberByModuleCode(
			String applicationFormNo, String module) {

		List<PaymentTransactionDTO> paymentList = paymentTransactionDAO
				.findByApplicationFormRefNumAndModuleCode(applicationFormNo, module);
		if (paymentList != null && paymentList.size() > 0) {
			paymentList.sort((o1, o2) -> o2.getRequest().getRequestTime().compareTo(o1.getRequest().getRequestTime()));
			return Optional.of(paymentList.get(0));
		}
		return Optional.empty();
	}

	public Optional<PaymentTransactionDTO> getLatestTransactionDateByTransactionRefNumberByModuleCode(
			String applicationFormNo, String module, String paymentTransactionNo) {

		Optional<PaymentTransactionDTO> paymentOptional = paymentTransactionDAO
				.findByApplicationFormRefNumAndModuleCodeAndPaymentTransactionNo(applicationFormNo, module,
						paymentTransactionNo);
		/*
		 * if (paymentList != null && paymentList.size() > 0) { paymentList.sort((o1,
		 * o2) -> o2.getRequest().getRequestTime().compareTo(o1.getRequest().
		 * getRequestTime())) ; return Optional.of(paymentList.get(0)); }
		 */
		return paymentOptional;
	}

	/**
	 * @param regServiceDTO
	 */
	private void updateNonRegVehicleDetailsForDataEntry(RegServiceDTO regServiceDTO) {

		trGenerationForDataEntry(regServiceDTO);

		prGenerationForDataEntry(regServiceDTO);
	}

	/**
	 * @param regServiceDTO
	 */
	private void prGenerationForDataEntry(RegServiceDTO regServiceDTO) {
		try {
			if (null == regServiceDTO.getPrNo()) {
				String prNo = prService.geneatePrNo(regServiceDTO.getApplicationNo(), Integer.MIN_VALUE, Boolean.FALSE,
						StringUtils.EMPTY, ModuleEnum.CITIZEN, Optional.empty());
				regServiceDTO.setPrNo(prNo);
				regServiceDTO.getRegistrationDetails().setPrNo(prNo);
				regServiceDTO.getRegistrationDetails()
						.setPrGeneratedDate(regServiceDTO.getRegistrationDetails().getPrIssueDate().atStartOfDay());
			}
		} catch (Exception e) {

			logger.error("Failed to generate PR number fo application No:[{}]", regServiceDTO.getApplicationNo());
			throw new BadRequestException("Failed to generate PR number fo application No [" + e.getMessage() + "]");

		}
	}

	/**
	 * @param regServiceDTO
	 */
	private void trGenerationForDataEntry(RegServiceDTO regServiceDTO) {
		try {
			if (null == regServiceDTO.getTrNo()) {
				String trNo = trSeriesService.geneateTrSeries(regServiceDTO.getRegistrationDetails()
						.getApplicantDetails().getPresentAddress().getDistrict().getDistrictId());
				regServiceDTO.setTrNo(trNo);
				regServiceDTO.getRegistrationDetails().setTrNo(trNo);
				regServiceDTO.getRegistrationDetails().setTrGeneratedDate(LocalDateTime.now());
			} else {
				regServiceDTO.getRegistrationDetails()
						.setTrGeneratedDate(regServiceDTO.getRegistrationDetails().getTrIssueDate().atStartOfDay());
			}
		} catch (Exception e) {

			logger.error("Failed to generate TR number fo application No:[{}]", regServiceDTO.getApplicationNo());
			throw new BadRequestException("Failed to generate TR number fo application No [" + e.getMessage() + "]");

		}
	}

	@Override
	public void paymentIntiationVerification(String applicationStatus, String applicationNo) {
		/*
		 * if(applicationStatus.equalsIgnoreCase(StatusRegistration. PAYMENTPENDING.
		 * getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); } if(applicationStatus.equalsIgnoreCase(StatusRegistration.
		 * PAYMENTSUCCESS. getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); } if(applicationStatus.equalsIgnoreCase(StatusRegistration.
		 * SECORINVALIDDONE. getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); } if(applicationStatus.equalsIgnoreCase(StatusRegistration.
		 * SPECIALNOPENDING. getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); } if(applicationStatus.equalsIgnoreCase(StatusRegistration.
		 * SECORINVALIDINITIATED.getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); } if(applicationStatus.equalsIgnoreCase(StatusRegistration.
		 * CHASSISTRGENERATED. getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); }
		 * if(applicationStatus.equalsIgnoreCase(StatusRegistration.TRGENERATED.
		 * getDescription())){ throw new
		 * BadRequestException("Payment already Initiated fo this Transaction" +
		 * applicationNo); }
		 */
		Optional<PropertiesDTO> optionalDto = propertiesDAO.findByModule(ModuleEnum.REG.toString());
		if (!optionalDto.isPresent()) {
			logger.error("master properties document nissed:");
			throw new BadRequestException("master properties document nissed:");
		}
		if (optionalDto.get().getApplicationStatus().stream()
				.anyMatch(status -> status.equalsIgnoreCase(applicationStatus))) {
			throw new BadRequestException("Payment already Initiated fo this Transaction" + applicationNo);
		}
	}

	private void updateTaxInStaging(Optional<StagingRegistrationDetailsDTO> registrationDetails,
			TransactionDetailVO transactionDetailVO) {
		if (transactionDetailVO.getTaxAmount() != null)
			registrationDetails.get().setTaxAmount(transactionDetailVO.getTaxAmount());
		if (transactionDetailVO.getTaxvalidity() != null)
			registrationDetails.get().setTaxvalidity(transactionDetailVO.getTaxvalidity());
		if (transactionDetailVO.getTaxArrears() != null)
			registrationDetails.get().setTaxArrears(transactionDetailVO.getTaxArrears());
		if (transactionDetailVO.getPenalty() != null)
			registrationDetails.get().setPenalty(transactionDetailVO.getPenalty());
		if (transactionDetailVO.getPenaltyArrears() != null)
			registrationDetails.get().setPenaltyArrears(transactionDetailVO.getPenaltyArrears());
		if (transactionDetailVO.getCesFee() != null)
			registrationDetails.get().setCesFee(transactionDetailVO.getCesFee());
		if (transactionDetailVO.getCesValidity() != null)
			registrationDetails.get().setCesValidity(transactionDetailVO.getCesValidity());
		if (transactionDetailVO.getTaxType() != null)
			registrationDetails.get().setTaxType(transactionDetailVO.getTaxType());
		registrationDetails.get().setSecondVehicleTaxPaid(transactionDetailVO.isSecondVehicleTaxPaid());
	}

	private void saveStageFCDetailsForNewFC(RegServiceDTO registrationDetailsDTO) {
		// TODO Auto-generated method stub

		StagingFcDetailsDTO fcdetails = new StagingFcDetailsDTO();

		try {
			if (registrationDetailsDTO.getRegistrationDetails().getVehicleType().equals(CovCategory.T.getCode())) {
				// TODO : Need to generate Sequence Number
				Map<String, String> detail = new HashMap<String, String>();
				detail.put("officeCode", registrationDetailsDTO.getOfficeDetails().getOfficeCode());
				String fcNumber = sequenceGenerator.getSequence(String.valueOf(Sequence.FC.getSequenceId()), detail);
				fcdetails.setApplicationNo(registrationDetailsDTO.getApplicationNo());
				fcdetails.setOfficeCode(registrationDetailsDTO.getOfficeDetails().getOfficeCode());
				fcdetails.setOfficeName(registrationDetailsDTO.getOfficeDetails().getOfficeName());
				fcdetails.setVehicleNumber("");
				/*
				 * List<FlowDTO> listFlowDTO = staginDto.getFlowDetailsLog(); if
				 * (listFlowDTO.size() > 0) { for (FlowDTO flowDTO : listFlowDTO) {
				 * 
				 * Set<Integer> keys = flowDTO.getFlowDetails().keySet();
				 * 
				 * for (Integer indx : keys) { List<RoleActionDTO> roleActions =
				 * flowDTO.getFlowDetails().get(indx); for (RoleActionDTO roles : roleActions) {
				 * if (roles.getRole().equals(RoleEnum.MVI.getName()) && (roles.getAction()
				 * .equals(StatusRegistration.APPROVED.getDescription()) ||
				 * roles.getAction().equals(StatusRegistration.REJECTED. getDescription()))) {
				 * fcdetails.setInspectedMviName(roles.getActionBy());
				 * fcdetails.setInspectedDate(roles.getActionTime());
				 * fcdetails.setUserId(roles.getUserId()); } } } } }
				 */
				// fcdetails.setInspectedMviName(getMVIName(staginDto).get(RoleEnum.MVI.toString()).toString()
				// !=null?getMVIName(staginDto).get(RoleEnum.MVI.toString()).toString():"");
				// fcdetails.setInspectedDate(getMVIName(staginDto).get("DATE"));
				fcdetails.setChassisNo(
						registrationDetailsDTO.getRegistrationDetails().getVahanDetails().getChassisNumber());
				fcdetails.setEngineNo(
						registrationDetailsDTO.getRegistrationDetails().getVahanDetails().getEngineNumber());
				fcdetails.setClassOfVehicle(registrationDetailsDTO.getRegistrationDetails().getClassOfVehicle());
				/*
				 * if(true){ fcdetails.setFcValidUpto(LocalDate.now().minusDays(1).
				 * plusYears(ValidityEnum. FCVALIDITY.getValidity())); }
				 */

				fcdetails.setFcIssuedDate(LocalDateTime.now());
				fcdetails.setCreatedDate(LocalDateTime.now());
				fcdetails.setFcNumber(fcNumber);
				fcdetails.setTrNo(registrationDetailsDTO.getRegistrationDetails().getTrNo());
				fcdetails.setPrNo(registrationDetailsDTO.getRegistrationDetails().getPrNo());
				stageFcDetailsDAO.save(fcdetails);
			}
		} catch (Exception e) {
			logger.error("Error :{}", e.getMessage());
		}

	}

	@Override
	public Object convertPaymentsForCFMS(TransactionDetailVO transactionDetailVO, String formNumber) {
		List<KeyValue<String, List<String>>> keyValues = new ArrayList<>();
		if (transactionDetailVO.getGatewayTypeEnum().equals(GatewayTypeEnum.CFMS)) {
			keyValues.add(new KeyValue<>("DC", Arrays.asList(transactionDetailVO.getCfmsDc())));
			keyValues.add(new KeyValue<>("DTId", Arrays.asList(transactionDetailVO.getTxnid())));
			keyValues.add(new KeyValue<>("RN", Arrays.asList(transactionDetailVO.getCfmsRn())));
			keyValues.add(new KeyValue<>("RID", Arrays.asList(transactionDetailVO.getRid())));
			keyValues.add(new KeyValue<>("TA", Arrays.asList(transactionDetailVO.getCfmsTotal().toString())));
			keyValues.add(new KeyValue<>("Ch", transactionDetailVO.getChList()));
			if (CollectionUtils.isNotEmpty(transactionDetailVO.getOthList())) {
				keyValues.add(new KeyValue<>("Oth", transactionDetailVO.getOthList()));
			}
			keyValues.add(new KeyValue<>("Rurl", Arrays.asList(transactionDetailVO.getSucessUrl())));
		}
		PaymentReqParams params = new PaymentReqParams();
		params.setPgUrl(transactionDetailVO.getPaymentUrl());
		params.setCfmsKeyValues(keyValues);
		params.setAppFormNo(formNumber);
		return params;
	}

	private String getTransactionNumber(TransactionDetailVO transactionDetailVO) {
		String uuid = java.util.UUID.randomUUID().toString();

		if (GatewayTypeEnum.CFMS.equals(transactionDetailVO.getGatewayTypeEnum())) {
			byte[] uuidByteArray = uuid.getBytes();
			return ModuleEnum.REG + "_" + transactionDetailVO.getModule() + "_"
					+ java.nio.ByteBuffer.wrap(uuidByteArray).asLongBuffer().get();

		}
		return uuid;

	}

	@Override
	public void verifyPaymentStatus(String applicationFormRefNum, String paymentTransactionNo) {

		Optional<PaymentTransactionDTO> paymentTransactionDTO = paymentTransactionDAO
				.findByApplicationFormRefNumAndPaymentTransactionNo(applicationFormRefNum, paymentTransactionNo);
		if (paymentTransactionDTO.isPresent()) {
			if (paymentTransactionDTO.get().getPayStatus().equals(PayStatusEnum.PENDINGFROMBANK.getDescription())) {
				throw new BadRequestException(
						"Found Payment Transaction status is Pending, Please verify [" + applicationFormRefNum + "]");
			}
		}

	}

	private void updateTaxDetailsInRegCollection(RegServiceDTO regServiceDTO) {

		Optional<RegistrationDetailsDTO> regDetailsOptional = registrationDetailDAO
				.findByApplicationNo(regServiceDTO.getRegistrationDetails().getApplicationNo());
		if (!regDetailsOptional.isPresent()) {
			throw new BadRequestException(
					"Registration details not found: " + regServiceDTO.getRegistrationDetails().getApplicationNo());
		}
		RegistrationDetailsDTO staginDto = regDetailsOptional.get();
		if (staginDto.isVehicleStoppageRevoked()) {
			staginDto.setVehicleStoppageRevoked(Boolean.FALSE);
		}

		if (regServiceDTO.getTaxAmount() != null)
			staginDto.setTaxAmount(regServiceDTO.getTaxAmount());
		if (regServiceDTO.getTaxType() != null)
			staginDto.setTaxType(regServiceDTO.getTaxType());
		if (regServiceDTO.getTaxvalidity() != null && staginDto.getRegistrationValidity() != null) {
			staginDto.setTaxvalidity(regServiceDTO.getTaxvalidity());
			staginDto.getRegistrationValidity().setTaxValidity(regServiceDTO.getTaxvalidity());
		}

		if (regServiceDTO.getCesFee() != null)
			staginDto.setCesFee(regServiceDTO.getCesFee());

		if (regServiceDTO.getCesValidity() != null) {
			staginDto.setCesValidity(regServiceDTO.getCesValidity());
			staginDto.getRegistrationValidity().setCessValidity(regServiceDTO.getCesValidity());
		}
		registrationDetailDAO.save(staginDto);
	}

	/*
	 * private Double getRenewalLateFee(String applicationNo, LocalDate slotDate) {
	 * Optional<RegistrationDetailsDTO> regOptional =
	 * registrationDetailDAO.findByApplicationNo(applicationNo); if
	 * (!regOptional.isPresent()) { throw new
	 * BadRequestException("No records found for : " + applicationNo); }
	 * RegistrationDetailsDTO registrationDetails = regOptional.get(); if
	 * (registrationDetails.getVehicleType().equalsIgnoreCase(CovCategory.N.
	 * getCode( ))) { throw new
	 * BadRequestException("Application not eligible for FC " + applicationNo); } if
	 * (slotDate == null) { // slotDate = LocalDate.now(); throw new
	 * BadRequestException("slot date missed " + applicationNo); } if
	 * (registrationDetails.getRegistrationValidity().getRegistrationValidity() !=
	 * null) { if
	 * (registrationDetails.getRegistrationValidity().getRegistrationValidity().
	 * toLocalDate() .isBefore(slotDate)) { Optional<MasterLateeFeeForFC>
	 * lateFeeForFC = masterLateeFeeForFCDAO.findByStatusTrue(); if
	 * (!lateFeeForFC.isPresent()) { throw new
	 * BadRequestException("No master data for late fee FC "); } LocalDate
	 * oldSlotDate = getFcOldSlotDate(applicationNo, slotDate); LocalDate fcDate =
	 * registrationDetails.getRegistrationValidity().getFcValidity(); if
	 * (registrationDetails.getRegistrationValidity().getFcValidity()
	 * .isBefore(lateFeeForFC.get().getLateFeeFromDate()) && oldSlotDate == null) {
	 * fcDate = lateFeeForFC.get().getLateFeeFromDate(); } if (oldSlotDate != null)
	 * { fcDate = oldSlotDate; } if
	 * (!fcDate.isAfter(registrationDetails.getRegistrationValidity().
	 * getFcValidity( ))) { fcDate =
	 * registrationDetails.getRegistrationValidity().getFcValidity(); } Long
	 * totalMonths = ChronoUnit.DAYS.between(fcDate, slotDate); Long lateFee =
	 * totalMonths * lateFeeForFC.get().getAmount(); return lateFee.doubleValue();
	 * 
	 * } } return 0d; }
	 */

	private LocalDate getRenewalOldSlotDate(String applicationNo, LocalDate slotDate) {
		List<RegServiceDTO> serviceList = regServiceDAO.findByRegistrationDetailsApplicationNoAndServiceIdsIn(
				applicationNo, Arrays.asList(ServiceEnum.RENEWAL.getId()));
		if (!serviceList.isEmpty()) {
			serviceList.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
			RegServiceDTO dto = serviceList.stream().findFirst().get();
			if (dto.getApplicationStatus().equals(StatusRegistration.APPROVED)) {
				return null;
			}
			if (dto.isPaidPyamentsForRenewal()) {
				if (dto.getSlotDetails().getSlotDate() != null && dto.getSlotDetailsLog() == null) {
					return dto.getSlotDetails().getSlotDate();
				}
				// LocalDate privesSlotDate =
				// dto.getSlotDetails().getSlotDate();
				if (dto.getSlotDetailsLog() != null) {
					LocalDate privesSlotDate = dto.getSlotDetailsLog().stream().findFirst().get().getSlotDate();
					if (dto.getSlotDetails().getPaymentStatus() == null
							|| !dto.getSlotDetails().getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
						if (!slotDate.equals(dto.getSlotDetails().getSlotDate())) {
							privesSlotDate = dto.getSlotDetails().getSlotDate();
						}
					}

					for (SlotDetailsDTO slots : dto.getSlotDetailsLog()) {
						if (!privesSlotDate.isAfter(slots.getSlotDate())) {
							if (slots.getPaymentStatus() != null
									&& slots.getPaymentStatus().equals(StatusRegistration.PAYMENTFAILED)) {
								continue;
							}
							privesSlotDate = slots.getSlotDate();
						}
					}
					/*
					 * if(dto.getSlotDetails().getSlotDate().isAfter( privesSlotDate)) { return
					 * dto.getSlotDetails().getSlotDate(); }else {
					 */
					return privesSlotDate;
					/* } */
				}
			}
		}
		return null;
	}

	public void sendCitizenNotifications(RegServiceDTO regServiceDTO) {
		if (regServiceDTO.getServiceIds() != null) {

			for (Integer i : regServiceDTO.getServiceIds()) {
				try {
					Integer templateId = null;
					String serviceName = ServiceEnum.getServiceEnumById(i).toString();
					switch (serviceName) {
					case "ISSUEOFNOC":
						templateId = MessageTemplate.NOC_ISSUED.getId();
						break;
					case "RENEWAL":
						templateId = MessageTemplate.RENEWAL_PERMIT.getId();
						break;
					case "CHANGEOFADDRESS":
						templateId = MessageTemplate.CHANGE_OF_ADDRESS.getId();
						break;
					case "DUPLICATE":
						templateId = MessageTemplate.DUPLICATE_RC.getId();
						break;
					case "TRANSFEROFOWNERSHIP":
						if (regServiceDTO.getBuyerDetails() != null
								&& regServiceDTO.getBuyerDetails().getBuyer() != null) {
							templateId = MessageTemplate.TOW_BUYER.getId();
							break;
						}
						templateId = MessageTemplate.TOW_TOKEN_GENERATED.getId();
						break;
					case "HIREPURCHASETERMINATION":
						templateId = MessageTemplate.HPT.getId();
						break;
					case "NEWFC":
						templateId = MessageTemplate.NEW_FC.getId();
						break;
					case "HPA":
						templateId = MessageTemplate.HPA_TOKEN_GENERATED.getId();
						break;
					case "THEFTINTIMATION":
						templateId = MessageTemplate.THEFT_INTIMATION.getId();
						break;
					case "THEFTREVOCATION":
						templateId = MessageTemplate.THEFT_REVOCATION.getId();
						break;
					case "CANCELLATIONOFNOC":
						templateId = MessageTemplate.NOC_CANCELLED.getId();
						break;
					case "NEWPERMIT":
						templateId = MessageTemplate.PERMIT_NEW.getId();
						break;
					case "RENEWALOFPERMIT":
						templateId = MessageTemplate.RENEWAL_PERMIT.getId();
						break;
					case "SURRENDEROFPERMIT":
						templateId = MessageTemplate.SURRENDER_PERMIT.getId();
						break;
					case "TRANSFEROFPERMIT":
						templateId = MessageTemplate.TRANSFER_PERMIT.getId();
						break;
					case "VARIATIONOFPERMIT":
						templateId = MessageTemplate.VARIATION_PERMIT.getId();
						break;
					case "PERMITCOA":
						templateId = MessageTemplate.PERMIT_COA.getId();
						break;
					case "RENEWALOFAUTHCARD":
						templateId = MessageTemplate.RENEWAL_AUTH_CARD.getId();
						break;
					case "EXTENSIONOFVALIDITY":
						templateId = MessageTemplate.EXTENION_VALIDITY.getId();
						break;
					case "REPLACEMENTOFVEHICLE":
						templateId = MessageTemplate.REPLACEMENT_VEHICLE.getId();
						break;
					case "ALTERATIONOFVEHICLE":
						templateId = MessageTemplate.ALTERATION_VEHICLE.getId();
						break;
					}
					if (templateId != null) {
						notifications.sendNotifications(templateId, regServiceDTO);
					}

				} catch (Exception e) {
					logger.error(
							"CitizenService : Unable to Send Notification For Application No [{}] & Exception Message is [{}] ",
							regServiceDTO.getApplicationNo(), e.getMessage());
				}
				logger.info("CitizenService : Processe Completed [{}] ", regServiceDTO.getApplicationNo());
				break;
			}
		}

	}

	public void test() {
		System.out.println("test Quarterly Tax");
	}

	@SuppressWarnings("unchecked")
	@Override
	public CFMSEodReportVO getCfmsEodReportByDate(LocalDate fromDate) {
		Optional<GateWayDTO> gateWay = gatewayDao.findByGateWayTypeAndStatusTrue(GatewayTypeEnum.CFMS);
		Map<String, String> gateWayDetails = gateWay.get().getGatewayDetails();
		String username = gateWayDetails.get(CFMSParams.EODREPORTUSERNAME.getParamKey());
		String password = gateWayDetails.get(CFMSParams.EODREPORTPASSWORD.getParamKey());
		String dc = gateWayDetails.get(CFMSParams.DC.getParamKey());
		String recordSet = gateWayDetails.get(CFMSParams.RECORDSET.getParamKey());
		String row = gateWayDetails.get(CFMSParams.ROW.getParamKey());
		String others = gateWayDetails.get(CFMSParams.OTHERS.getParamKey());
		String challan = gateWayDetails.get(CFMSParams.CHALLAN.getParamKey());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = null;
		String fromDateString = fromDate.toString().replaceAll("-", "");
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"DEPTCODE\": \"" + dc + "\",");
		sb.append("\"DATE\": \"" + fromDateString + "\"");
		sb.append("}");

		logger.info("EOD Report", sb.toString());

		HttpEntity<String> httpEntity = new HttpEntity<>(sb.toString(), headers);
		RestTemplate restTmplate = new RestTemplate();

		restTmplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

		try {
			response = restTmplate.exchange(gateWayDetails.get(CFMSParams.EODREPORTURL.getParamKey()), HttpMethod.POST,
					httpEntity, String.class);

			if (response.hasBody()) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObj = (JSONObject) parser.parse(response.getBody());
				ArrayList<Map<String, String>> listOfOthers = new ArrayList<>();
				ArrayList<Map<String, String>> listOfChallans = new ArrayList<>();
				JSONObject jsonObjRecordset = (JSONObject) jsonObj.get(recordSet);
				List<JSONObject> jsonObjRowSet = (List<JSONObject>) jsonObjRecordset.get(row);

				for (JSONObject jsonObject : jsonObjRowSet) {
					addData(others, listOfOthers, jsonObject);
					addData(challan, listOfChallans, jsonObject);
				}

				Gson gson = new Gson();
				CFMSEodReportVO payVO = gson.fromJson(jsonObj.toString(), CFMSEodReportVO.class);
				logger.info("CFMS Verify Response for application No [{}] & Body [{}]", fromDate, jsonObj);
				return payVO;

			}
			throw new BadRequestException("No respopnce from CFMS Gateway for Application No [" + fromDate + "]");

		} catch (RestClientException rce) {
			throw new BadRequestException("Exception while Connecting to CFMS Gateway [" + rce.getMessage() + "]");

		} catch (Exception e) {
			throw new BadRequestException("Exception :[" + e.getMessage() + "]");
		}
	}

	private void addData(String others, ArrayList<Map<String, String>> listOfOthers, JSONObject jobj2)
			throws JSONException {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) jobj2.get(others);
			listOfOthers.add(map);
		} catch (Exception e) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> map = (List<Map<String, String>>) jobj2.get(others);
			listOfOthers.addAll(map);
		}
	}

	@Override
	public CitizenPaymentReportVO getPaymentDetailsByApplicationNumber(String applicationNumber) {
		Optional<PaymentTransactionDTO> paymentDTOOptional = Optional.empty();
		String paymentTransactionNo = StringUtils.EMPTY;
		Optional<RegServiceDTO> regServiceDTO = regServiceDAO.findByApplicationNo(applicationNumber);
		if (!regServiceDTO.isPresent()) {
			Optional<StagingRegistrationDetailsDTO> stgingDTo = stagingRegistrationDetailsSerivce
					.FindbBasedOnApplicationNo(applicationNumber);
			if (!stgingDTo.isPresent()) {
				Optional<RegistrationDetailsDTO> regDto = registrationDetailDAO.findByApplicationNo(applicationNumber);
				if (!regDto.isPresent()) {
					paymentTransactionNo = regServiceDTO.get().getPaymentTransactionNo();
				} else {
					throw new BadRequestException("No record found with this Application Number " + applicationNumber);
				}
			} else {
				paymentTransactionNo = regServiceDTO.get().getPaymentTransactionNo();
			}
		} else {
			paymentTransactionNo = regServiceDTO.get().getPaymentTransactionNo();
		}

		if (!Objects.isNull(applicationNumber)) {
			if (paymentTransactionNo != null && StringUtils.isNoneBlank(paymentTransactionNo)) {
				paymentDTOOptional = getLatestTransactionDateByTransactionRefNumber(applicationNumber,
						paymentTransactionNo);
			} else {
				paymentDTOOptional = getLatestTransactionDateByTransactionRefNumber(applicationNumber);
			}
		}
		if (!paymentDTOOptional.isPresent()) {
			throw new BadRequestException("No record found with this Application Number " + applicationNumber);
		}
		return setPaymentDetails(paymentDTOOptional.get());
	}

	private CitizenPaymentReportVO setPaymentDetails(PaymentTransactionDTO paymentDto) {
		CitizenPaymentReportVO citizenPaymentReportVO = new CitizenPaymentReportVO();
		citizenPaymentReportVO.setApplicationNumber(paymentDto.getApplicationFormRefNum());
		citizenPaymentReportVO.setStatus(PayStatusEnum.getPayStatusEnumByDescription(paymentDto.getPayStatus()));
		citizenPaymentReportVO.setGateWayType(
				GatewayTypeEnum.getGatewayTypeEnumById(paymentDto.getPaymentGatewayType()).getDescription());
		citizenPaymentReportVO.setTransactionNumber(paymentDto.getTransactioNo());
		return citizenPaymentReportVO;
	}

	@Override
	public FeeDetailsVO getCitizenServiceFees(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum,
			String weightType, boolean isRequestToPay, String taxType, boolean isCalculateFc,
			boolean isApplicationFromMvi, boolean isChassesVehicle, String officeCode, String applicationNumber,
			boolean isOtherState, String regApplicationNo, String permitTypeCode, LocalDate slotDate,
			String seatingCapacity, String routeCode, Boolean isWeightAlt) throws FileNotFoundException {
		logger.debug("getServiceFee Start");

		Map<String, FeePartsDetailsVo> feePartsMap = this.calculateTaxAndFees(covs, serviceEnum, weightType,
				isRequestToPay, taxType, isCalculateFc, isApplicationFromMvi, isChassesVehicle, officeCode,
				applicationNumber, isOtherState, regApplicationNo, permitTypeCode, slotDate, seatingCapacity, routeCode,
				isWeightAlt);

		return preFeeDetailsVO(feePartsMap);
	}

	private Map<String, FeePartsDetailsVo> calculateTaxAndFees(List<ClassOfVehiclesVO> covs,
			List<ServiceEnum> serviceEnum, String weightType, boolean isRequestToPay, String taxType,
			boolean isCalculateFc, boolean isApplicationFromMvi, boolean isChassesVehicle, String officeCode,
			String applicationNumber, boolean isOtherState, String regApplicationNo, String permitTypeCode,
			LocalDate slotDate, String seatingCapacity, String routeCode, Boolean isWeightAlt)
			throws FileNotFoundException {
		Long taxAmount = 0l;
		Long cesFee = 0l;
		Long greenTaxAmount = 0l;
		Long TaxArrears = 0l;
		Long penalityArrears = 0l;
		Long quaterTaxForNewGo = 0l;
		boolean isARVTVehicle = Boolean.FALSE;
		if (taxType == null || StringUtils.isBlank(taxType)) {
			taxType = ServiceCodeEnum.QLY_TAX.getCode();
		}
		StagingRegistrationDetailsDTO stagingRegDetails = null;
		RegServiceDTO regServiceDetials = null;
		TaxHelper taxAndValidity = null;
		TaxHelper cessAndValidity = null;
		TaxHelper greenTaxAndValidity = null;
		Long penality = null;
		boolean skipTaxInPermits = false;
		if (StringUtils.isNotBlank(permitTypeCode)) {
			Optional<PropertiesDTO> propertiesDetails = propertiesDAO.findByTaxExceptionPermitCodeIn(permitTypeCode);
			if (propertiesDetails.isPresent()) {
				List<String> permitTypeList = propertiesDetails.get().getTaxExceptionPermitCode();
				skipTaxInPermits = permitTypeList.stream().anyMatch(code -> code.equalsIgnoreCase(permitTypeCode));
			}

		}

		if (isChassesVehicle) {
			Optional<StagingRegistrationDetailsDTO> stagingRegDetailsOptional = stagingRegistrationDetails
					.findByApplicationNo(applicationNumber);
			if (!stagingRegDetailsOptional.isPresent()) {
				throw new BadRequestException("Registration Details Is not Available: " + applicationNumber);
			}

			stagingRegDetails = stagingRegDetailsOptional.get();
			if (isChassesVehicle) {
				taxAndValidity = citizenTaxService.getTaxDetails(applicationNumber, isApplicationFromMvi,
						isChassesVehicle, taxType, isOtherState, regApplicationNo, serviceEnum, permitTypeCode,
						routeCode, isWeightAlt,null);
				cessAndValidity = citizenTaxService.getTaxDetails(applicationNumber, isApplicationFromMvi,
						isChassesVehicle, ServiceCodeEnum.CESS_FEE.getCode(), isOtherState, regApplicationNo,
						serviceEnum, permitTypeCode, routeCode, isWeightAlt,null);
			}
			if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
				isARVTVehicle = Boolean.TRUE;
			}
			saveStagingTaxDetails(taxAndValidity, cessAndValidity, stagingRegDetails);

		}

		if (taxAndValidity != null) {
			if (!skipTaxInPermits) {
				taxAmount = taxAndValidity.getTaxAmountForPayments();
				taxType = taxAndValidity.getTaxName();
				if (taxAmount > 0) {
					if (cessAndValidity != null && cessAndValidity.getTaxAmountForPayments() != null
							&& cessAndValidity.getTaxAmountForPayments() != 0) {
						if (!skipTaxInPermits) {
							cesFee = cessAndValidity.getTaxAmountForPayments();
						}
					}
				}
				if (taxAndValidity.getPenalty() != null) {
					penality = taxAndValidity.getPenalty();
				}
				if (taxAndValidity.getPenaltyArrears() != null && taxAndValidity.getPenaltyArrears() != 0) {
					penalityArrears = taxAndValidity.getPenaltyArrears();
				}
				if (taxAndValidity.getTaxArrearsRound() != null && taxAndValidity.getTaxArrearsRound() != 0) {
					TaxArrears = taxAndValidity.getTaxArrearsRound();
				}
				if (taxAndValidity.getQuaterTaxForNewGo() != null && taxAndValidity.getQuaterTaxForNewGo() != 0) {
					quaterTaxForNewGo = taxAndValidity.getQuaterTaxForNewGo();
				}
			}
		}
		FeePartsVO feeInput = setFeeInputs(covs, serviceEnum, weightType, taxAmount, cesFee, greenTaxAmount, taxType,
				isCalculateFc, isApplicationFromMvi, isChassesVehicle, officeCode, applicationNumber, isARVTVehicle,
				stagingRegDetails, penality, regApplicationNo, penalityArrears, TaxArrears, slotDate, permitTypeCode,
				seatingCapacity, quaterTaxForNewGo, isRequestToPay, regServiceDetials, isOtherState);

		Map<String, FeePartsDetailsVo> feePartsMap = this.getCitizenFeePartsDetails(covs, serviceEnum, weightType,
				taxAmount, cesFee, greenTaxAmount, taxType, isCalculateFc, isApplicationFromMvi, isChassesVehicle,
				officeCode, applicationNumber, isARVTVehicle, stagingRegDetails, penality, regApplicationNo,
				penalityArrears, TaxArrears, slotDate, permitTypeCode, seatingCapacity, quaterTaxForNewGo,
				isRequestToPay, regServiceDetials, isOtherState);

		FeePartsVO feePartsVO = ruleEngineService.getfeeDetails(feeInput);
		return feePartsVO.getFeeParts();

		// return feePartsMap;
	}

	public FeePartsVO setFeeInputs(List<ClassOfVehiclesVO> covs, List<ServiceEnum> serviceEnum, String weightType,
			Long taxAmount, Long cesFee, Long greenTaxAmount, String taxType, boolean isCalculateFc,
			boolean isApplicationFromMvi, boolean isChassesVehicle, String officeCode, String applicationNumber,
			boolean isARVTVehicle, StagingRegistrationDetailsDTO stagingRegDetails, Long penality,
			String regApplicationNo, Long penalityArrears, Long taxArrears, LocalDate slotDate, String permitTypeCode,
			String seatingCapacity, Long quaterTaxForNewGo, boolean isRequestToPay, RegServiceDTO regServiceDetials,
			boolean isOtherState) {
		FeePartsVO feeInput = new FeePartsVO();
		feeInput.setCovs(covs);
		feeInput.setServiceEnum(serviceEnum);
		feeInput.setWeightType(weightType);
		feeInput.setTaxAmount(greenTaxAmount);
		feeInput.setCesFee(cesFee);
		feeInput.setGrenTaxAmount(greenTaxAmount);
		feeInput.setTaxType(taxType);
		feeInput.setCalculateFc(isCalculateFc);
		feeInput.setApplicationFromMvi(isApplicationFromMvi);
		feeInput.setChassesVehicle(isChassesVehicle);
		feeInput.setOfficeCode(officeCode);
		feeInput.setApplicationNumber(applicationNumber);
		feeInput.setARVTVehicle(isARVTVehicle);
		feeInput.setStagingRegDetails(stagingRegDetails);
		feeInput.setPenality(penality);
		feeInput.setRegApplicationNo(regApplicationNo);
		feeInput.setPenalityArrears(penalityArrears);
		feeInput.setTaxArrears(taxArrears);
		feeInput.setSlotDate(slotDate);
		feeInput.setPermitTypeCode(permitTypeCode);
		feeInput.setSeatingCapacity(seatingCapacity);
		feeInput.setQuaterTaxForNewGo(quaterTaxForNewGo);
		feeInput.setRequestToPay(isRequestToPay);
		feeInput.setRegServiceDetials(regServiceDetials);
		feeInput.setOtherState(isOtherState);
		return feeInput;
	}

	public Map<String, String> readPropforRules() {
		Map<String, String> prop = new HashMap<String, String>();
		prop.put("rtaCreditAccount", rtaCreditAccount);
		prop.put("lifeTaxHoa", lifeTaxHoa);
		prop.put("qutrelyTaxHoa", qutrelyTaxHoa);
		prop.put("cessCreditAccount", cessCreditAccount);
		prop.put("greenTaxHoa", greenTaxHoa);
		prop.put("serviceFeeHoa", serviceFeeHoa);
		prop.put("applicationFeeHoa", applicationFeeHoa);
		return prop;
	}

}
