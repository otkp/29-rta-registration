package org.epragati.taxserviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.drools.core.spi.KnowledgeHelper;
import org.epragati.aadhaar.seed.service.AadharSeeding;
import org.epragati.common.dao.PropertiesDAO;
import org.epragati.common.dto.PropertiesDTO;
import org.epragati.constants.OwnerTypeEnum;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.AlterationDAO;
import org.epragati.master.dao.FinalTaxHelperDAO;
import org.epragati.master.dao.MasterAmountSecoundCovsDAO;
import org.epragati.master.dao.MasterGreenTaxDAO;
import org.epragati.master.dao.MasterGreenTaxFuelexcemptionDAO;
import org.epragati.master.dao.MasterNewGoTaxDetailsDAO;
import org.epragati.master.dao.MasterPayperiodDAO;
import org.epragati.master.dao.MasterTaxBasedDAO;
import org.epragati.master.dao.MasterTaxDAO;
import org.epragati.master.dao.MasterTaxExcemptionsDAO;
import org.epragati.master.dao.MasterTaxFuelTypeExcemptionDAO;
import org.epragati.master.dao.MasterWeightsForAltDAO;
import org.epragati.master.dao.RegServiceDAO;
import org.epragati.master.dao.RegistrationDetailDAO;
import org.epragati.master.dao.StagingRegistrationDetailsDAO;
import org.epragati.master.dao.TaxDetailsDAO;
import org.epragati.master.dto.FinalTaxHelper;
import org.epragati.master.dto.MasterAmountSecoundCovsDTO;
import org.epragati.master.dto.MasterGreenTax;
import org.epragati.master.dto.MasterGreenTaxFuelexcemption;
import org.epragati.master.dto.MasterNewGoTaxDetails;
import org.epragati.master.dto.MasterPayperiodDTO;
import org.epragati.master.dto.MasterTax;
import org.epragati.master.dto.MasterTaxBased;
import org.epragati.master.dto.MasterTaxExcemptionsDTO;
import org.epragati.master.dto.MasterTaxFuelTypeExcemptionDTO;
import org.epragati.master.dto.MasterWeightsForAlt;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxComponentDTO;
import org.epragati.master.dto.TaxDetailsDTO;
import org.epragati.master.dto.TaxHelper;
import org.epragati.master.dto.TrailerChassisDetailsDTO;
import org.epragati.permits.dao.PermitDetailsDAO;
import org.epragati.permits.dto.PermitDetailsDTO;
import org.epragati.regservice.RegistrationService;
import org.epragati.regservice.dto.AlterationDTO;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.regservice.mapper.RegServiceMapper;
import org.epragati.regservice.vo.RegServiceVO;
import org.epragati.rules.RuleEngineObject;
import org.epragati.tax.vo.TaxCalculationHelper;
import org.epragati.tax.vo.TaxTypeEnum;
import org.epragati.taxservice.CitizenDroolsTaxService;
import org.epragati.util.DateConverters;
import org.epragati.util.PermitsEnum;
import org.epragati.util.PermitsEnum.PermitType;
import org.epragati.util.payment.ClassOfVehicleEnum;
import org.epragati.util.payment.OtherStateApplictionType;
import org.epragati.util.payment.ServiceCodeEnum;
import org.epragati.util.payment.ServiceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class CitizenDroolsTaxServiceImpl implements CitizenDroolsTaxService {

	private static final Logger logger = LoggerFactory.getLogger(CitizenDroolsTaxServiceImpl.class);

	Optional<MasterPayperiodDTO> Payperiod = Optional.empty();

	String classOfVehicle;

	@Autowired
	private MasterPayperiodDAO masterPayperiodDAO;

	@Autowired
	private RegServiceDAO regServiceDAO;

	@Autowired
	private RegistrationDetailDAO registrationDetailDAO;

	@Autowired
	private MasterTaxBasedDAO masterTaxBasedDAO;

	@Autowired
	private MasterTaxDAO taxTypeDAO;

	@Autowired
	private TaxDetailsDAO taxDetailsDAO;
	@Autowired
	private StagingRegistrationDetailsDAO stagingRegistrationDetailsDAO;

	@Autowired
	private MasterTaxExcemptionsDAO masterTaxExcemptionsDAO;

	@Autowired

	private PropertiesDAO propertiesDAO;

	@Value("${reg.fresh.stateCode}")
	private String stateCode;

	@Value("${reg.fresh.status}")
	private String regStatus;

	@Value("${reg.fresh.vehicle.age}")
	private float freshVehicleAge;

	@Value("${reg.fresh.vehicle.amount}")
	private Integer amount;

	@Value("${reg.fresh.permitcode}")
	private String permitcode;

	@Value("${reg.fresh.reg.otherState}")
	private String otherState;

	@Value("${reg.fresh.reg.lifeTaxCode}")
	private String lifeTaxCode;

	@Value("${reg.fresh.reg.quarterlyCode}")
	private String quarterlyCode;

	@Value("${reg.fresh.reg.bothCode}")
	private String bothCode;

	@Value("${reg.fresh.reg.seatingCapacityCode}")
	private String seatingCapacityCode;

	@Value("${reg.fresh.reg.ulwCode}")
	private String ulwCode;

	@Value("${reg.fresh.reg.rlwCode}")
	private String rlwCode;

	@Value("${reg.fresh.reg.battery}")
	private String battery;
	@Value("${reg.fresh.reg.electric:ELECTRIC}")
	private String electric;

	@Autowired
	private AlterationDAO alterationDao;

	@Autowired
	private MasterAmountSecoundCovsDAO masterAmountSecoundCovsDAO;

	@Autowired
	private MasterTaxFuelTypeExcemptionDAO masterTaxFuelTypeExcemptionDAO;

	@Autowired
	private MasterGreenTaxDAO masterGreenTaxDAO;

	@Autowired
	private RegServiceMapper regServiceMapper;

	@Autowired
	private MasterGreenTaxFuelexcemptionDAO masterGreenTaxFuelexcemptionDAO;

	@Autowired
	private PermitDetailsDAO permitDetailsDAO;

	@Autowired
	private FinalTaxHelperDAO finalTaxHelperDAO;

	@Autowired
	private AadharSeeding aadharSeeding;

	@Autowired
	private MasterNewGoTaxDetailsDAO masterNewGoTaxDetailsDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private MasterWeightsForAltDAO masterWeightsForAltDAO;

	@Override
	public TaxHelper getTaxDetails(String applicationNo, boolean isApplicationFromMvi, boolean isChassesApplication,
			String taxType, boolean isOtherState, String CitizenapplicationNo, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, Boolean isWeightAlt) {

		Optional<MasterPayperiodDTO> Payperiod = Optional.empty();
		Optional<MasterTaxBased> taxCalBasedOn = Optional.empty();
		RegServiceDTO regServiceDTO = null;
		RegistrationDetailsDTO registrationDetails = null;
		StagingRegistrationDetailsDTO stagingRegistrationDetails = null;
		String classOfvehicle = null;
		TaxTypeEnum.TaxPayType payTaxType = TaxTypeEnum.TaxPayType.REG;
		if (isApplicationFromMvi || isChassesApplication) {
			payTaxType = TaxTypeEnum.TaxPayType.DIFF;
		}
		Optional<AlterationDTO> alterDetails = Optional.empty();
		if (isChassesApplication) {
			Optional<StagingRegistrationDetailsDTO> stagingOptional = stagingRegistrationDetailsDAO
					.findByApplicationNo(applicationNo);
			if (stagingOptional.isPresent()) {
				stagingRegistrationDetails = stagingOptional.get();
			} else {
				logger.error("No record found in Reg Service for:[{}] " + applicationNo);
				throw new BadRequestException("No record found in Reg Service for:[{}] " + applicationNo);
			}
			// need to call body builder cov
			alterDetails = alterationDao.findByApplicationNo(stagingRegistrationDetails.getApplicationNo());
			if (!alterDetails.isPresent()) {
				throw new BadRequestException(
						"No record found in master_tax for: " + stagingRegistrationDetails.getApplicationNo());
			}

			classOfvehicle = alterDetails.get().getCov();
			Payperiod = masterPayperiodDAO.findByCovcode(alterDetails.get().getCov());
		} else if (isApplicationFromMvi) {

			Optional<RegServiceDTO> regServiceOptional = regServiceDAO.findByApplicationNo(CitizenapplicationNo);
			if (regServiceOptional.isPresent()) {
				regServiceDTO = regServiceOptional.get();
			} else {
				logger.error("No record found in Reg Service for:[{}] " + CitizenapplicationNo);
				throw new BadRequestException("No record found in Reg Service for:[{}] " + CitizenapplicationNo);
			}
			if (regServiceDTO.getAlterationDetails() != null && regServiceDTO.getAlterationDetails().getCov() != null) {

				classOfvehicle = regServiceDTO.getAlterationDetails().getCov();
				Payperiod = masterPayperiodDAO.findByCovcode(regServiceDTO.getAlterationDetails().getCov());
			} else {
				classOfvehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
				Payperiod = masterPayperiodDAO
						.findByCovcode(regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			}
		} else if (isOtherState) {// OTHERSTATE
			Optional<RegServiceDTO> regServiceOptional = regServiceDAO.findByApplicationNo(applicationNo);
			if (!regServiceOptional.isPresent()) {
				logger.error("No record found in Reg Service for:[{}] " + applicationNo);
				throw new BadRequestException("No record found in Reg Service for:[{}] " + applicationNo);
			}
			regServiceDTO = regServiceOptional.get();
			classOfvehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
			// After MVI
			if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				alterDetails = alterationDao.findByApplicationNo(regServiceDTO.getApplicationNo());
				if (!alterDetails.isPresent()) {
					throw new BadRequestException(
							"No record found in alteration details for: " + regServiceDTO.getApplicationNo());
				}

				classOfvehicle = alterDetails.get().getCov();
			}
			Payperiod = masterPayperiodDAO.findByCovcode(classOfvehicle);

		} else {
			Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(applicationNo);
			if (!regOptional.isPresent()) {
				logger.error("No record found in Reg Service for:[{}] " + applicationNo);
				throw new BadRequestException("No record found in Reg Service for:[{}] " + applicationNo);
			}
			registrationDetails = regOptional.get();
			if (registrationDetails.getTrGeneratedDate() == null
					&& registrationDetails.getRegistrationValidity() != null
					&& registrationDetails.getRegistrationValidity().getTrGeneratedDate() != null) {
				registrationDetails.setTrGeneratedDate(
						registrationDetails.getRegistrationValidity().getTrGeneratedDate().atStartOfDay());
			}
			/*
			 * if (registrationDetails.getTrGeneratedDate() == null) { throw new
			 * BadRequestException( "trGenerated Date not found for appNo" +
			 * registrationDetails.getApplicationNo()); }
			 */
			if (StringUtils.isBlank(registrationDetails.getClassOfVehicle())) {
				logger.error("class of vehicle not found for :[{}] " + applicationNo);
				throw new BadRequestException("class of vehicle not found for :" + applicationNo);
			}
			classOfvehicle = registrationDetails.getClassOfVehicle();
			Payperiod = masterPayperiodDAO.findByCovcode(registrationDetails.getClassOfVehicle());
		}

		if (!Payperiod.isPresent()) {
			// throw error message
			logger.error("No record found in master_payperiod for:[{}] " + applicationNo);
			throw new BadRequestException("No record found in master_payperiod for: " + applicationNo);

		}
		Boolean gostatus = Boolean.FALSE;
		TaxHelper quaterTax = null;
		if (Payperiod.get().getPayperiod().equalsIgnoreCase(TaxTypeEnum.BOTH.getCode())) {

			if (isChassesApplication) {
				// need to call body builder cov
				Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
						alterDetails.get().getFromSeatingCapacity(),
						stagingRegistrationDetails.getVahanDetails().getGvw());
				gostatus = payperiodAndGoStatus.getSecond();

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null
						&& regServiceDTO.getAlterationDetails().getSeating() != null) {
					// need to chamge gvw
					Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
							regServiceDTO.getAlterationDetails().getSeating(),
							regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw());
					gostatus = payperiodAndGoStatus.getSecond();
				} else {
					// need to chamge gvw
					Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity(),
							regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw());
					gostatus = payperiodAndGoStatus.getSecond();
				}
			} else if (isOtherState) {// OTHERSTATE

				String seats = regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
				if (alterDetails != null && alterDetails.isPresent()
						&& StringUtils.isNoneBlank(alterDetails.get().getSeating())) {
					seats = alterDetails.get().getSeating();
				}
				getPayPeroidForBoth(Payperiod, seats,
						regServiceDTO.getRegistrationDetails().getVehicleDetails().getRlw());

			} else {

				Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
						registrationDetails.getVehicleDetails().getSeatingCapacity(),
						registrationDetails.getVahanDetails().getGvw());
				gostatus = payperiodAndGoStatus.getSecond();
			}

		}
		if (gostatus) {
			List<MasterNewGoTaxDetails> newGoDetesDetails = masterNewGoTaxDetailsDAO.findAll();
			if (newGoDetesDetails.isEmpty()) {
				throw new BadRequestException("new records found for new Gov go.");
			}
			MasterNewGoTaxDetails goDetails = newGoDetesDetails.stream().findFirst().get();

			List<TaxDetailsDTO> listOfTaxDetails = taxDetailsDAO
					.findFirst10ByApplicationNoAndPaymentPeriodInOrderByCreatedDateDesc(applicationNo,
							Arrays.asList(TaxTypeEnum.LifeTax.getDesc()));
			if (listOfTaxDetails != null && !listOfTaxDetails.isEmpty()) {
				for (TaxDetailsDTO dto : listOfTaxDetails) {
					if (registrationDetails.getClassOfVehicle().equalsIgnoreCase(dto.getClassOfVehicle())) {
						TaxHelper taxHelper = returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d,
								lifTaxValidityCal(), 0l, 0d, payTaxType, "");
						listOfTaxDetails.clear();
						return taxHelper;
					}
				}
			}
			LocalDate trGeneratedDate;
			if (isApplicationFromMvi) {
				if (regServiceDTO.getRegistrationDetails().getRegistrationValidity().getTrGeneratedDate() == null) {
					trGeneratedDate = regServiceDTO.getRegistrationDetails().getRegistrationValidity()
							.getPrGeneratedDate();
				} else {
					trGeneratedDate = regServiceDTO.getRegistrationDetails().getRegistrationValidity()
							.getTrGeneratedDate();
				}
			} else if (isChassesApplication) {
				trGeneratedDate = stagingRegistrationDetails.getRegistrationValidity().getTrGeneratedDate();
			} else if (isOtherState) {
				trGeneratedDate = regServiceDTO.getRegistrationDetails().getRegistrationValidity().getTrGeneratedDate();
			} else {

				if (registrationDetails.getRegistrationValidity().getTrGeneratedDate() != null) {
					trGeneratedDate = registrationDetails.getRegistrationValidity().getTrGeneratedDate();
				} else {
					trGeneratedDate = registrationDetails.getRegistrationValidity().getPrGeneratedDate();
				}

			}
			if (trGeneratedDate.isBefore(goDetails.getTaxEffectFrom())) {
				if (taxType.equalsIgnoreCase(TaxTypeEnum.QuarterlyTax.getDesc())
						|| taxType.equalsIgnoreCase(TaxTypeEnum.HalfyearlyTax.getDesc())
						|| taxType.equalsIgnoreCase(TaxTypeEnum.YearlyTax.getDesc())) {
					Payperiod.get().setPayperiod(TaxTypeEnum.QuarterlyTax.getCode());
				}
				if (goDetails.getOldTaxEffectUpTo().isBefore(LocalDate.now())) {
					if (taxType.equalsIgnoreCase(TaxTypeEnum.QuarterlyTax.getDesc())
							|| taxType.equalsIgnoreCase(TaxTypeEnum.HalfyearlyTax.getDesc())
							|| taxType.equalsIgnoreCase(TaxTypeEnum.YearlyTax.getDesc())) {
						Payperiod.get().setPayperiod(TaxTypeEnum.LifeTax.getCode());
					}
				}
				// taxType = TaxTypeEnum.LifeTax.getDesc();
				if (StringUtils.isNoneBlank(taxType) && taxType.equalsIgnoreCase(TaxTypeEnum.LifeTax.getDesc())) {
					if (taxType.equalsIgnoreCase(TaxTypeEnum.CESS.getDesc())) {
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}

					TaxHelper lastTaxTillDate = getLastPaidTax(registrationDetails, regServiceDTO, isApplicationFromMvi,
							validity(TaxTypeEnum.QuarterlyTax.getDesc()), stagingRegistrationDetails,
							isChassesApplication, taxTypes(), isOtherState);
					if (lastTaxTillDate == null || lastTaxTillDate.getTax() == null
							|| lastTaxTillDate.getValidityTo() == null) {
						throw new BadRequestException("TaxDetails not found");
					}
					if (lastTaxTillDate.isAnypendingQuaters()) {
						// taxType = TaxTypeEnum.QuarterlyTax.getDesc();
						quaterTax = quaterTaxCalculation(applicationNo, isApplicationFromMvi, isChassesApplication,
								taxType, isOtherState, serviceEnum, permitTypeCode, routeCode, taxCalBasedOn,
								regServiceDTO, registrationDetails, stagingRegistrationDetails, classOfvehicle,
								payTaxType, alterDetails, isWeightAlt);
					}
				}
			}
		}
		switch (TaxTypeEnum.getTaxTypeEnumByCode(Payperiod.get().getPayperiod())) {

		case LifeTax:
			Optional<MasterAmountSecoundCovsDTO> basedOnInvoice = Optional.empty();
			Optional<MasterAmountSecoundCovsDTO> basedOnsecoundVehicle = Optional.empty();
			Double totalLifeTax;
			if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, LocalDate.now(), 0l, 0d, payTaxType, "");
			}
			List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO = masterAmountSecoundCovsDAO.findAll();
			if (masterAmountSecoundCovsDTO.isEmpty()) {
				// TODO:need to Exception throw
			}
			if (isChassesApplication) {
				Optional<AlterationDTO> alterDetailsOptionla = alterationDao
						.findByApplicationNo(stagingRegistrationDetails.getApplicationNo());
				if (!alterDetailsOptionla.isPresent()) {
					throw new BadRequestException(
							"No record found in master_tax for: " + stagingRegistrationDetails.getApplicationNo());
				}

				Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
						.findByKeyvalueOrCovcode(stagingRegistrationDetails.getVahanDetails().getMakersModel(),
								stagingRegistrationDetails.getClassOfVehicle());
				if (optionalTaxExcemption.isPresent()) {
					//
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(),
							optionalTaxExcemption.get().getTaxvalue().longValue(), 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");
				}
				basedOnInvoice = masterAmountSecoundCovsDTO.stream()
						.filter(m -> m.getAmountcovcode().contains(alterDetailsOptionla.get().getCov())).findFirst();

				basedOnsecoundVehicle = masterAmountSecoundCovsDTO.stream()
						.filter(m -> m.getSecondcovcode().contains(alterDetailsOptionla.get().getCov())).findFirst();

				if (stagingRegistrationDetails.getOwnerType().getCode()
						.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
						|| stagingRegistrationDetails.getOwnerType().getCode()
								.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
					Optional<MasterTax> OptionalLifeTax = taxTypeDAO
							.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(
									alterDetails.get().getCov(), stagingRegistrationDetails.getOwnerType().getCode(),
									stateCode, regStatus, freshVehicleAge);
					if (!OptionalLifeTax.isPresent()) {
						logger.error("No record found in master_tax for: " + alterDetails.get().getCov() + "and"
								+ stagingRegistrationDetails.getOwnerType());
						// throw error message
						throw new BadRequestException("No record found in master_tax for: "
								+ alterDetails.get().getCov() + "and" + stagingRegistrationDetails.getOwnerType());
					}

					totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
							* OptionalLifeTax.get().getPercent() / 100);
					if (totalLifeTax.equals(Double.valueOf(0))) {
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}
					Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
							registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(), isApplicationFromMvi,
							isChassesApplication, isOtherState);

					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
							lifTaxValidityCal(), 0l, 0d, payTaxType, "");

				} else if (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue() > amount
						&& basedOnInvoice.isPresent() && stagingRegistrationDetails.getOwnerType().getCode()
								.equalsIgnoreCase(OwnerTypeEnum.Individual.getCode())) {

					totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
							* basedOnInvoice.get().getTaxpercentinvoice() / 100f);
					Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
							registrationDetails, totalLifeTax, basedOnInvoice.get().getTaxpercentinvoice(),
							isApplicationFromMvi, isChassesApplication, isOtherState);
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
							lifTaxValidityCal(), 0l, 0d, payTaxType, "");
				} else if (basedOnsecoundVehicle.isPresent() && !stagingRegistrationDetails.getIsFirstVehicle()) {

					totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
							* basedOnsecoundVehicle.get().getSecondvehiclepercent() / 100f);
					Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
							registrationDetails, totalLifeTax, basedOnsecoundVehicle.get().getSecondvehiclepercent(),
							isApplicationFromMvi, isChassesApplication, isOtherState);
					// stagingRegistrationDetails.setSecondVehicleTaxPaid(Boolean.TRUE);
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
							lifTaxValidityCal(), 0l, 0d, payTaxType, "");
				} else if (alterDetails.get().getCov().equalsIgnoreCase(ClassOfVehicleEnum.IVCN.getCovCode())) {

					// stagingRegistrationDetailsDAO.save(stagingRegDetails);
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");

				} else {
					Optional<MasterTax> OptionalLifeTax = taxTypeDAO
							.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(
									alterDetails.get().getCov(), stagingRegistrationDetails.getOwnerType().getCode(),
									stateCode, regStatus, freshVehicleAge);
					if (!OptionalLifeTax.isPresent()) {
						logger.error("No record found in master_tax for: " + alterDetails.get().getCov() + "and"
								+ stagingRegistrationDetails.getOwnerType());
						// throw error message
						throw new BadRequestException("No record found in master_tax for: "
								+ alterDetails.get().getCov() + "and" + stagingRegistrationDetails.getOwnerType());
					}
					totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
							* OptionalLifeTax.get().getPercent() / 100f);
					Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
							registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(), isApplicationFromMvi,
							isChassesApplication, isOtherState);
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
							lifTaxValidityCal(), 0l, 0d, payTaxType, "");
				}
			} else if (isApplicationFromMvi) {

				boolean isToSkipTak = this.checkIsToPayLifeTaxBefore(
						regServiceDTO.getRegistrationDetails().getApplicationNo(), regServiceDTO.getAlterationDetails(),
						regServiceDTO.getRegistrationDetails().getPrNo());
				if (!isToSkipTak) {
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");
				}
				if (regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
						.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
						|| regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
								.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");
				}
				Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
						.findByKeyvalueOrCovcode(
								regServiceDTO.getRegistrationDetails().getVahanDetails().getMakersModel(),
								regServiceDTO.getRegistrationDetails().getClassOfVehicle());
				if (optionalTaxExcemption.isPresent()) {
					//
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(),
							optionalTaxExcemption.get().getTaxvalue().longValue(), 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");
				}
				// calculate age of vehicle
				double vehicleAge = calculateAgeOfTheVehicle(
						regServiceDTO.getRegistrationDetails().getRegistrationValidity().getPrGeneratedDate(),
						LocalDate.now());
				String cov = regServiceDTO.getAlterationDetails().getCov() != null
						? regServiceDTO.getAlterationDetails().getCov()
						: regServiceDTO.getRegistrationDetails().getClassOfVehicle();
				Optional<MasterTax> OptionalLifeTax = taxTypeDAO
						.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndToageGreaterThanEqualAndFromageLessThanEqualAndTocostGreaterThanEqualAndFromcostLessThanEqual(
								cov, regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), stateCode,
								regStatus, vehicleAge, vehicleAge,
								regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue(),
								regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue());
				if (!OptionalLifeTax.isPresent()) {
					logger.error("No record found in master_tax for: " + regServiceDTO.getAlterationDetails().getCov()
							+ "and" + regServiceDTO.getRegistrationDetails().getOwnerType());
					// throw error message
					throw new BadRequestException(
							"No record found in master_tax for: " + regServiceDTO.getAlterationDetails().getCov()
									+ "and" + regServiceDTO.getRegistrationDetails().getOwnerType());
				}
				totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
						* OptionalLifeTax.get().getPercent() / 100f);
				Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
						registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(), isApplicationFromMvi,
						isChassesApplication, isOtherState);
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
						lifTaxValidityCal(), 0l, 0d, payTaxType, "");
			} else if (isOtherState) {
				// other state
				RegServiceVO vo = regServiceMapper.convertEntity(regServiceDTO);
				if (regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
						.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
						|| regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
								.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
					Optional<MasterTax> OptionalLifeTax = taxTypeDAO
							.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(
									regServiceDTO.getRegistrationDetails().getClassOfVehicle(),
									regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), stateCode,
									regStatus, freshVehicleAge);
					if (!OptionalLifeTax.isPresent()) {
						logger.error("No record found in master_tax for: " + alterDetails.get().getCov() + "and"
								+ regServiceDTO.getRegistrationDetails().getOwnerType());
						// throw error message
						throw new BadRequestException(
								"No record found in master_tax for: " + alterDetails.get().getCov() + "and"
										+ regServiceDTO.getRegistrationDetails().getOwnerType());
					}

					totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
							* OptionalLifeTax.get().getPercent() / 100);
					if (totalLifeTax.equals(Double.valueOf(0))) {
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}
					Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
							registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(), isApplicationFromMvi,
							isChassesApplication, isOtherState);

					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
							lifTaxValidityCal(), 0l, 0d, payTaxType, "");

				}
				if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
						|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
						&& regServiceDTO.isMviDone()) {
					Optional<AlterationDTO> alterDetailsOptionla = alterationDao
							.findByApplicationNo(regServiceDTO.getApplicationNo());
					if (!alterDetailsOptionla.isPresent()) {
						throw new BadRequestException(
								"No record found in alteration document for: " + regServiceDTO.getApplicationNo());
					}

					Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
							.findByKeyvalueOrCovcode(
									regServiceDTO.getRegistrationDetails().getVahanDetails().getMakersModel(),
									alterDetails.get().getCov());
					if (optionalTaxExcemption.isPresent()) {
						//
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(),
								optionalTaxExcemption.get().getTaxvalue().longValue(), 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}
					basedOnInvoice = masterAmountSecoundCovsDTO.stream()
							.filter(m -> m.getAmountcovcode().contains(alterDetailsOptionla.get().getCov()))
							.findFirst();

					basedOnsecoundVehicle = masterAmountSecoundCovsDTO.stream()
							.filter(m -> m.getSecondcovcode().contains(alterDetailsOptionla.get().getCov()))
							.findFirst();

					if (regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
							|| regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
									.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
						Optional<MasterTax> OptionalLifeTax = taxTypeDAO
								.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(
										alterDetails.get().getCov(),
										regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), stateCode,
										regStatus, freshVehicleAge);
						if (!OptionalLifeTax.isPresent()) {
							logger.error("No record found in master_tax for: " + alterDetails.get().getCov() + "and"
									+ regServiceDTO.getRegistrationDetails().getOwnerType());
							// throw error message
							throw new BadRequestException(
									"No record found in master_tax for: " + alterDetails.get().getCov() + "and"
											+ regServiceDTO.getRegistrationDetails().getOwnerType());
						}

						totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
								* OptionalLifeTax.get().getPercent() / 100);
						if (totalLifeTax.equals(Double.valueOf(0))) {
							return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
									payTaxType, "");
						}
						Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
								registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(),
								isApplicationFromMvi, isChassesApplication, isOtherState);

						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
								lifTaxValidityCal(), 0l, 0d, payTaxType, "");

					} else if (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue() > amount
							&& basedOnInvoice.isPresent() && regServiceDTO.getRegistrationDetails().getOwnerType()
									.getCode().equalsIgnoreCase(OwnerTypeEnum.Individual.getCode())) {

						totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
								* basedOnInvoice.get().getTaxpercentinvoice() / 100f);
						Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
								registrationDetails, totalLifeTax, basedOnInvoice.get().getTaxpercentinvoice(),
								isApplicationFromMvi, isChassesApplication, isOtherState);
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
								lifTaxValidityCal(), 0l, 0d, payTaxType, "");
					} else if (basedOnsecoundVehicle.isPresent()
							&& !regServiceDTO.getRegistrationDetails().getIsFirstVehicle()) {

						totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
								* basedOnsecoundVehicle.get().getSecondvehiclepercent() / 100f);
						Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
								registrationDetails, totalLifeTax,
								basedOnsecoundVehicle.get().getSecondvehiclepercent(), isApplicationFromMvi,
								isChassesApplication, isOtherState);
						// stagingRegistrationDetails.setSecondVehicleTaxPaid(Boolean.TRUE);
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
								lifTaxValidityCal(), 0l, 0d, payTaxType, "");
					} else if (alterDetails.get().getCov().equalsIgnoreCase(ClassOfVehicleEnum.IVCN.getCovCode())) {

						// stagingRegistrationDetailsDAO.save(stagingRegDetails);
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");

					} else {
						Optional<MasterTax> OptionalLifeTax = taxTypeDAO
								.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(
										alterDetails.get().getCov(),
										regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), stateCode,
										regStatus, freshVehicleAge);
						if (!OptionalLifeTax.isPresent()) {
							logger.error("No record found in master_tax for: " + alterDetails.get().getCov() + "and"
									+ regServiceDTO.getRegistrationDetails().getOwnerType());
							// throw error message
							throw new BadRequestException(
									"No record found in master_tax for: " + alterDetails.get().getCov() + "and"
											+ regServiceDTO.getRegistrationDetails().getOwnerType());
						}
						totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
								* OptionalLifeTax.get().getPercent() / 100f);
						Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
								registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(),
								isApplicationFromMvi, isChassesApplication, isOtherState);
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
								lifTaxValidityCal(), 0l, 0d, payTaxType, "");
					}
				}
				OtherStateApplictionType applicationType = getOtherStateVehicleStatus(vo);
				if (Arrays.asList(OtherStateApplictionType.ApplicationNO, OtherStateApplictionType.TrNo)
						.contains(applicationType)) {
					basedOnInvoice = masterAmountSecoundCovsDTO.stream()
							.filter(m -> m.getAmountcovcode().contains(vo.getRegistrationDetails().getClassOfVehicle()))
							.findFirst();
					basedOnsecoundVehicle = masterAmountSecoundCovsDTO.stream()
							.filter(m -> m.getSecondcovcode().contains(vo.getRegistrationDetails().getClassOfVehicle()))
							.findFirst();
					if (!vo.getRegistrationDetails().isTaxPaidByVcr()) {
						if (vo.getRegistrationDetails().getApplicantType() != null
								&& !vo.getRegistrationDetails().getApplicantType().equalsIgnoreCase("withinthestate")) {
							if (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue() > amount
									&& basedOnInvoice.isPresent()
									&& regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
											.equalsIgnoreCase(OwnerTypeEnum.Individual.getCode())) {

								totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails()
										.getInvoiceValue() * basedOnInvoice.get().getTaxpercentinvoice() / 100f);

								Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails,
										regServiceDTO, registrationDetails, totalLifeTax,
										basedOnInvoice.get().getTaxpercentinvoice(), isApplicationFromMvi,
										isChassesApplication, isOtherState);
								return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(),
										lifeTax.getSecond(), lifTaxValidityCal(), 0l, 0d, payTaxType, "");
							} else if (basedOnsecoundVehicle.isPresent()
									&& !vo.getRegistrationDetails().getIsFirstVehicle()) {

								totalLifeTax = (vo.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
										* basedOnsecoundVehicle.get().getSecondvehiclepercent() / 100f);
								Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails,
										regServiceDTO, registrationDetails, totalLifeTax,
										basedOnsecoundVehicle.get().getSecondvehiclepercent(), isApplicationFromMvi,
										isChassesApplication, isOtherState);
								// stagingRegistrationDetails.setSecondVehicleTaxPaid(Boolean.TRUE);
								return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(),
										lifeTax.getSecond(), lifTaxValidityCal(), 0l, 0d, payTaxType, "");
							} else {

								Pair<Long, Double> lifeTax = lifeTaxCalculation(isApplicationFromMvi,
										isChassesApplication, isOtherState, regServiceDTO, registrationDetails,
										stagingRegistrationDetails);
								return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(),
										lifeTax.getSecond(), lifTaxValidityCal(), 0l, 0d, payTaxType, "");
							}
						}
					} else {
						if (!vo.getRegistrationDetails().isTaxPaidByVcr()) {
							if (vo.getRegistrationDetails().getApplicantType() != null && !vo.getRegistrationDetails()
									.getApplicantType().equalsIgnoreCase("withinthestate")) {
								Pair<Long, Double> lifeTax = lifeTaxCalculation(isApplicationFromMvi,
										isChassesApplication, isOtherState, regServiceDTO, registrationDetails,
										stagingRegistrationDetails);
								return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(),
										lifeTax.getSecond(), lifTaxValidityCal(), 0l, 0d, payTaxType, "");
							}
						}
					}

				} else {
					if (!vo.getRegistrationDetails().isTaxPaidByVcr()) {
						if (vo.getRegistrationDetails().getApplicantType() != null
								&& !vo.getRegistrationDetails().getApplicantType().equalsIgnoreCase("withinthestate")) {
							Pair<Long, Double> lifeTax = lifeTaxCalculation(isApplicationFromMvi, isChassesApplication,
									isOtherState, regServiceDTO, registrationDetails, stagingRegistrationDetails);
							return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(),
									lifeTax.getSecond(), lifTaxValidityCal(), 0l, 0d, payTaxType, "");
						}
					}
				}
			} else {
				if (gostatus) {

					double vehicleAge = 0d;

					if (registrationDetails.getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
							|| registrationDetails.getOwnerType().getCode()
									.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}
					Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
							.findByKeyvalueOrCovcode(registrationDetails.getVahanDetails().getMakersModel(),
									registrationDetails.getClassOfVehicle());
					if (optionalTaxExcemption.isPresent()) {
						//
						return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(),
								optionalTaxExcemption.get().getTaxvalue().longValue(), 0d, lifTaxValidityCal(), 0l, 0d,
								payTaxType, "");
					}
					List<MasterNewGoTaxDetails> newGoDetesDetails = masterNewGoTaxDetailsDAO.findAll();
					if (newGoDetesDetails.isEmpty()) {
						throw new BadRequestException("new records found for new Gov go.");
					}
					MasterNewGoTaxDetails goDetails = newGoDetesDetails.stream().findFirst().get();
					if (goDetails.getTaxEffectFrom().isAfter(registrationDetails.getTrGeneratedDate().toLocalDate())) {
						if (registrationDetails.getRegistrationValidity() == null
								|| registrationDetails.getRegistrationValidity().getPrGeneratedDate() == null) {
							throw new BadRequestException(
									"pr issued  date not found for: " + registrationDetails.getPrNo());
						}
						vehicleAge = calculateAgeOfTheVehicle(
								registrationDetails.getRegistrationValidity().getPrGeneratedDate(), LocalDate.now());
					}
					Optional<MasterTax> OptionalLifeTax = taxTypeDAO
							.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndToageGreaterThanEqualAndFromageLessThanEqual(
									registrationDetails.getClassOfVehicle(),
									registrationDetails.getOwnerType().getCode(), stateCode, regStatus, vehicleAge,
									vehicleAge);
					if (!OptionalLifeTax.isPresent()) {
						logger.error("No record found in master tax for: " + registrationDetails.getClassOfVehicle()
								+ "and" + registrationDetails.getOwnerType());
						// throw error message
						throw new BadRequestException("No record found in master tax for: "
								+ registrationDetails.getClassOfVehicle() + "and" + registrationDetails.getOwnerType());
					}
					double tax = 0d;
					Long penality = 0l;
					Long reoundTaxArrears = 0l;
					Long penalityArrears = 0l;
					if (registrationDetails.getInvoiceDetails() == null) {
						throw new BadRequestException("Invoice Details Not found for appNo" + applicationNo);
					}
					totalLifeTax = (registrationDetails.getInvoiceDetails().getInvoiceValue()
							* OptionalLifeTax.get().getPercent() / 100f);
					if (quaterTax != null) {
						tax = quaterTax.getTaxAmountForPayments();
						penality = quaterTax.getPenalty();
						reoundTaxArrears = quaterTax.getTaxArrearsRound();
						penalityArrears = quaterTax.getPenaltyArrears();
					}
					return returnTaxForNewGo(TaxTypeEnum.LifeTax.getDesc(), roundUpperTen(totalLifeTax),
							penality.doubleValue(), lifTaxValidityCal(), reoundTaxArrears,
							penalityArrears.doubleValue(), payTaxType, "", roundUpperTen(tax));
				}
			}
			break;

		case QuarterlyTax:
			TaxHelper tax = quaterTaxCalculation(applicationNo, isApplicationFromMvi, isChassesApplication, taxType,
					isOtherState, serviceEnum, permitTypeCode, routeCode, taxCalBasedOn, regServiceDTO,
					registrationDetails, stagingRegistrationDetails, classOfvehicle, payTaxType, alterDetails,
					isWeightAlt);
			return tax;
		// break;
		default:
			break;

		}
		return returnTaxDetails(taxType, 0l, 0d, LocalDate.now(), 0l, 0d, TaxTypeEnum.TaxPayType.REG, "");
	}

	private TaxHelper quaterTaxCalculation(String applicationNo, boolean isApplicationFromMvi,
			boolean isChassesApplication, String taxType, boolean isOtherState, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, Optional<MasterTaxBased> taxCalBasedOn,
			RegServiceDTO regServiceDTO, RegistrationDetailsDTO registrationDetails,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, String classOfvehicle,
			TaxTypeEnum.TaxPayType payTaxType, Optional<AlterationDTO> alterDetails, Boolean isWeightAlt) {
		Optional<MasterTax> OptionalTax;
		Long totalQuarterlyTax;
		if (isChassesApplication) {
			// need to call body builder cov
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueOrCovcode(
					stagingRegistrationDetails.getVahanDetails().getMakersModel(),
					stagingRegistrationDetails.getClassOfVehicle());
			if (optionalTaxExcemption.isPresent()) {
				//
				return returnTaxDetails(taxType, optionalTaxExcemption.get().getTaxvalue().longValue(), 0d,
						validity(taxType), 0l, 0d, payTaxType, "");
			}
			if (stagingRegistrationDetails.getOwnerType().getCode().equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
					|| stagingRegistrationDetails.getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
				LocalDate TaxTill = validity(taxType);
				return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");

			}
			if (!(stagingRegistrationDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| stagingRegistrationDetails.getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode())
					|| stagingRegistrationDetails.getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode()))) {

				if (checkTaxUpToDateOrNote(isApplicationFromMvi, isChassesApplication, regServiceDTO,
						registrationDetails, stagingRegistrationDetails, taxType)) {
					LocalDate TaxTill = validity(taxType);
					return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");
				}
			}
			taxCalBasedOn = masterTaxBasedDAO.findByCovcode(alterDetails.get().getCov());

		} else if (isApplicationFromMvi) {
			if (taxType.equalsIgnoreCase(TaxTypeEnum.LifeTax.getDesc())) {
				taxType = TaxTypeEnum.QuarterlyTax.getDesc();
			}
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueOrCovcode(
					regServiceDTO.getRegistrationDetails().getVahanDetails().getMakersModel(),
					regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			if (optionalTaxExcemption.isPresent()) {
				//
				return returnTaxDetails(taxType, optionalTaxExcemption.get().getTaxvalue().longValue(), 0d,
						validity(taxType), 0l, 0d, payTaxType, "");
			}
			if (regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
					.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
					|| regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
				// regServiceDTO.setTaxAmount(0l);//need to retuen
				LocalDate TaxTill = validity(taxType);
				return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");
				// return registrationDetails;
			}
			if (regServiceDTO.getAlterationDetails() != null && regServiceDTO.getAlterationDetails().getCov() != null) {
				classOfvehicle = regServiceDTO.getAlterationDetails().getCov();
				taxCalBasedOn = masterTaxBasedDAO.findByCovcode(regServiceDTO.getAlterationDetails().getCov());
			} else {
				/*
				 * if (checkTaxUpToDateOrNote(isApplicationFromMvi, isChassesApplication,
				 * regServiceDTO, registrationDetails, stagingRegistrationDetails, taxType)) {
				 * LocalDate TaxTill = validity(taxType); return returnTaxDetails(taxType, 0l,
				 * 0d, TaxTill); }
				 */
				classOfvehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
				taxCalBasedOn = masterTaxBasedDAO
						.findByCovcode(regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			}
		} else if (isOtherState) {
			classOfvehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
			if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {

				classOfvehicle = alterDetails.get().getCov();
			}
			if (!regServiceDTO.getRegistrationDetails().isTaxPaidByVcr()) {
				if (regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
						.equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
						|| regServiceDTO.getRegistrationDetails().getOwnerType().getCode()
								.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
					// regServiceDTO.setTaxAmount(0l);//need to retuen
					LocalDate TaxTill = validity(taxType);
					return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");
					// return registrationDetails;
				}

				taxCalBasedOn = masterTaxBasedDAO.findByCovcode(classOfvehicle);
			} else {
				// classOfvehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
				taxCalBasedOn = masterTaxBasedDAO.findByCovcode(classOfvehicle);
			}
		} else {
			if (registrationDetails.getOwnerType().getCode().equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
					|| registrationDetails.getOwnerType().getCode().equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
				LocalDate TaxTill = validity(taxType);
				return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");

			}
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueOrCovcode(
					registrationDetails.getVahanDetails().getMakersModel(), registrationDetails.getClassOfVehicle());
			if (optionalTaxExcemption.isPresent()) {
				//
				return returnTaxDetails(taxType, optionalTaxExcemption.get().getTaxvalue().longValue(), 0d,
						validity(taxType), 0l, 0d, payTaxType, "");
			}
			// TODO need to skip newPermit and permit variation
			if (serviceEnum == null || serviceEnum.isEmpty()
					|| !serviceEnum.stream()
							.anyMatch(service -> Arrays.asList(ServiceEnum.NEWPERMIT, ServiceEnum.VARIATIONOFPERMIT)
									.stream().anyMatch(serviceName -> serviceName.equals(service)))) {
				if (isWeightAlt == null || !isWeightAlt) {
					if (checkTaxUpToDateOrNote(isApplicationFromMvi, isChassesApplication, regServiceDTO,
							registrationDetails, stagingRegistrationDetails, taxType)) {
						LocalDate TaxTill = validity(taxType);
						return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");
					}
				}

			}
			classOfvehicle = registrationDetails.getClassOfVehicle();
			taxCalBasedOn = masterTaxBasedDAO.findByCovcode(registrationDetails.getClassOfVehicle());
		}
		if (!taxCalBasedOn.isPresent()) {
			logger.error("No record found in master_taxbased for: " + classOfvehicle);
			// throw error message
			throw new BadRequestException("No record found in master_taxbased for: " + classOfvehicle);
		}
		if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(ulwCode)) {
			String permitType = permitcode;
			if (isChassesApplication) {
				// need to call body builder cov
				OptionalTax = getUlwTax(alterDetails.get().getCov(), alterDetails.get().getUlw(), stateCode,
						permitcode);

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null) {
					String cov = regServiceDTO.getAlterationDetails().getCov() != null
							? regServiceDTO.getAlterationDetails().getCov()
							: regServiceDTO.getRegistrationDetails().getClassOfVehicle();
					Integer ulw = regServiceDTO.getAlterationDetails().getUlw() != null
							? regServiceDTO.getAlterationDetails().getUlw()
							: regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw();
					OptionalTax = getUlwTax(cov, ulw, stateCode, permitcode);
				} else {
					OptionalTax = getUlwTax(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw(), stateCode, permitcode);
				}

			} else if (isOtherState) {
				Integer ulw = regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw();
				String staeCode = "OS";
				if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
						|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
						&& regServiceDTO.isMviDone()) {
					ulw = alterDetails.get().getUlw() != null ? alterDetails.get().getUlw()
							: regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw();
					staeCode = "AP";
				}
				OptionalTax = getUlwTax(classOfvehicle, ulw, staeCode, permitcode);
			} else {

				Pair<String, String> permitTypeAndRoutType = getPermitCode(registrationDetails);
				permitType = permitTypeCode;
				boolean isBelongToPermitService = false;
				isBelongToPermitService = getAmethodToGetPemritService(serviceEnum);
				if (isBelongToPermitService) {
					Optional<PropertiesDTO> propertiesOptional = propertiesDAO
							.findByCovsInAndPermitCodeTrue(classOfvehicle);
					if (!propertiesOptional.isPresent()) {
						permitTypeCode = permitcode;
						permitType = permitcode;
					}
				}
				OptionalTax = getUlwTax(registrationDetails.getClassOfVehicle(),
						registrationDetails.getVahanDetails().getUnladenWeight(), stateCode, permitType);

			}
			if (!OptionalTax.isPresent()) {
				logger.error("No record found in master_tax for: " + applicationNo);
				// throw error message
				throw new BadRequestException("No record found in master_tax for: " + applicationNo);
			}

			TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
					taxCalBasedOn.get().getBasedon(), registrationDetails, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, taxType, classOfvehicle, isOtherState,
					serviceEnum, permitTypeCode, routeCode, null, isWeightAlt);

			// registrationDetails.setTaxAmount(totalQuarterlyTax);
			// return stagingRegDetails;
			payTaxType = totalTaxAndValidity.getTaxPayType();
			if (isApplicationFromMvi || isChassesApplication) {
				payTaxType = TaxTypeEnum.TaxPayType.DIFF;
			}
			return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
					totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
					totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
		} else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(rlwCode)) {
			String permitType = permitcode;
			if (isChassesApplication) {
				// need to call body builder cov
				Integer gvw = getGvwWeight(stagingRegistrationDetails.getApplicationNo(),
						stagingRegistrationDetails.getVahanDetails().getGvw());
				OptionalTax = getRlwTax(alterDetails.get().getCov(), gvw, stateCode, permitcode);

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null) {
					String cov = regServiceDTO.getAlterationDetails().getCov() != null
							? regServiceDTO.getAlterationDetails().getCov()
							: regServiceDTO.getRegistrationDetails().getClassOfVehicle();
					Integer gvw = getGvwWeightForAlt(regServiceDTO);
					OptionalTax = getRlwTax(cov, gvw, stateCode, permitcode);
				} else {
					// need to chamge gvw
					// need to chek avt weight
					OptionalTax = getRlwTax(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getRlw(), stateCode, permitcode);
				}

			} else if (isOtherState) {
				Integer gvw = getGvwWeightForCitizen(regServiceDTO.getRegistrationDetails());
				String staeCode = "OS";
				if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
						|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
						&& regServiceDTO.isMviDone()) {
					gvw = getGvwWeight(regServiceDTO.getApplicationNo(),
							regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw());
					staeCode = "AP";
				}
				OptionalTax = getRlwTax(classOfvehicle, gvw, staeCode, permitcode);

			} else {
				Pair<String, String> permitTypeAndRoutType = getPermitCode(registrationDetails);
				permitType = permitTypeCode;
				boolean isBelongToPermitService = false;
				isBelongToPermitService = getAmethodToGetPemritService(serviceEnum);
				if (isBelongToPermitService) {
					Optional<PropertiesDTO> propertiesOptional = propertiesDAO
							.findByCovsInAndPermitCodeTrue(classOfvehicle);
					if (!propertiesOptional.isPresent()) {
						permitTypeCode = permitcode;
						permitType = permitcode;
					}
				}
				Integer gvw = getGvwWeightForCitizen(registrationDetails);
				OptionalTax = getRlwTax(registrationDetails.getVehicleDetails().getClassOfVehicle(), gvw, stateCode,
						permitType);

			}
			if (!OptionalTax.isPresent()) {
				logger.error("No record found in master_tax for: " + applicationNo);
				// throw error message
				throw new BadRequestException("No record found in master_tax for: " + applicationNo);
			}

			TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
					taxCalBasedOn.get().getBasedon(), registrationDetails, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, taxType, classOfvehicle, isOtherState,
					serviceEnum, permitTypeCode, routeCode, null, isWeightAlt);

			// stagingRegDetails.setTaxAmount(totalQuarterlyTax);
			// stagingRegistrationDetailsDAO.save(stagingRegDetails);
			// return stagingRegDetails;
			payTaxType = totalTaxAndValidity.getTaxPayType();
			if (isApplicationFromMvi || isChassesApplication) {
				payTaxType = TaxTypeEnum.TaxPayType.DIFF;
			}
			return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
					totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
					totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
		} else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(seatingCapacityCode)) {
			String permitType = StringUtils.EMPTY;
			if (isChassesApplication) {
				// need to call body builder cov
				OptionalTax = getSeatingCapacityTax(alterDetails.get().getCov(), alterDetails.get().getSeating(),
						stateCode, permitcode, null);

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null) {
					String cov = regServiceDTO.getAlterationDetails().getCov() != null
							? regServiceDTO.getAlterationDetails().getCov()
							: regServiceDTO.getRegistrationDetails().getClassOfVehicle();
					String seating = regServiceDTO.getAlterationDetails().getSeating() != null
							? regServiceDTO.getAlterationDetails().getSeating()
							: regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
					OptionalTax = getSeatingCapacityTax(cov, seating, stateCode, permitcode, null);
				} else {
					OptionalTax = getSeatingCapacityTax(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity(), stateCode,
							permitcode, null);
				}

			} else if (isOtherState) {
				String staeCode = "OS";
				String seating = regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
				if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
						|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
								.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
						&& regServiceDTO.isMviDone()) {
					seating = alterDetails.get().getSeating() != null ? alterDetails.get().getSeating()
							: regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
					staeCode = "AP";
				}
				OptionalTax = getSeatingCapacityTax(classOfvehicle, seating, staeCode, permitcode, null);

			} else {
				Pair<String, String> permitTypeAndRoutType = getPermitCode(registrationDetails);
				permitType = permitTypeAndRoutType.getFirst();
				String routeCodes = permitTypeAndRoutType.getSecond();
				boolean isBelongToPermitService = false;
				if (registrationDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARKT.getCovCode())) {
					permitType = this.permitcode;
				}
				isBelongToPermitService = getAmethodToGetPemritService(serviceEnum);
				if (isBelongToPermitService) {
					Optional<PropertiesDTO> propertiesOptional = propertiesDAO
							.findByCovsInAndPermitCodeTrue(classOfvehicle);
					if (!propertiesOptional.isPresent()) {
						permitTypeCode = permitcode;
						permitType = permitcode;
					}
				}
				OptionalTax = getSeatingCapacityTax(registrationDetails.getVehicleDetails().getClassOfVehicle(),
						registrationDetails.getVehicleDetails().getSeatingCapacity(), stateCode, permitType,
						routeCodes);
			}

			if (!OptionalTax.isPresent()) {
				// throw error message
				throw new BadRequestException("No record found in master_tax for: " + applicationNo);
			}

			TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
					taxCalBasedOn.get().getBasedon(), registrationDetails, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, taxType, classOfvehicle, isOtherState,
					serviceEnum, permitTypeCode, routeCode, permitType, isWeightAlt);
			// stagingRegDetails.setTaxAmount(totalQuarterlyTax);
			// stagingRegistrationDetailsDAO.save(stagingRegDetails);
			// return stagingRegDetails;
			if (totalTaxAndValidity != null && totalTaxAndValidity.getTaxPayType() != null)
				payTaxType = totalTaxAndValidity.getTaxPayType();
			if (isApplicationFromMvi || isChassesApplication) {
				payTaxType = TaxTypeEnum.TaxPayType.DIFF;
			}
			return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
					totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
					totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
		}
		return returnTaxDetails(taxType, 0l, 0d, null, 0l, 0d, payTaxType, "");
	}

	private boolean getAmethodToGetPemritService(List<ServiceEnum> serviceEnum) {
		List<ServiceEnum> payVerify = new ArrayList<>();
		payVerify.add(ServiceEnum.NEWPERMIT);
		payVerify.add(ServiceEnum.RENEWALOFPERMIT);

		payVerify.add(ServiceEnum.VARIATIONOFPERMIT);
		payVerify.add(ServiceEnum.EXTENSIONOFVALIDITY);

		payVerify.add(ServiceEnum.RENEWALOFAUTHCARD);
		boolean verifyPaymentIntiation = payVerify.stream().anyMatch(val -> serviceEnum.contains(val));
		return verifyPaymentIntiation;
	}

	private Pair<String, String> getPermitCode(RegistrationDetailsDTO registrationDetails) {
		List<PermitDetailsDTO> listOfPermits = permitDetailsDAO.findByPrNoAndPermitStatus(registrationDetails.getPrNo(),
				PermitsEnum.ACTIVE.getDescription());
		String permitType = permitcode;
		String routeCode = "";
		if (!listOfPermits.isEmpty()) {
			PermitDetailsDTO listOfPermsasits = listOfPermits.stream().filter(type -> type.getPermitType()
					.getTypeofPermit().equalsIgnoreCase(PermitsEnum.PermitType.PRIMARY.getPermitTypeCode())).findAny()
					.get();
			if (listOfPermsasits != null) {
				permitType = listOfPermsasits.getPermitType().getPermitType();
				if ((registrationDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.COCT.getCovCode())
						|| registrationDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.TOVT.getCovCode()))
						&& (listOfPermsasits.getRouteDetails().getRouteType() == null || StringUtils
								.isBlank(listOfPermsasits.getRouteDetails().getRouteType().getRouteCode()))) {
					throw new BadRequestException("route code not found for pr: " + registrationDetails.getPrNo());
				}
				if (listOfPermsasits.getRouteDetails() != null
						&& listOfPermsasits.getRouteDetails().getRouteType() != null
						&& listOfPermsasits.getRouteDetails().getRouteType().getRouteCode() != null) {
					routeCode = listOfPermsasits.getRouteDetails().getRouteType().getRouteCode();
				}

			}
		} else {
			Optional<PermitDetailsDTO> listOfInActivePermits = permitDetailsDAO
					.findByPrNoAndPermitStatusAndPermitTypeTypeofPermitOrderByCreatedDateDesc(
							registrationDetails.getPrNo(), PermitsEnum.INACTIVE.getDescription(),
							PermitType.PRIMARY.getPermitTypeCode());
			if (listOfInActivePermits.isPresent()) {
				if (listOfInActivePermits.get().getPermitSurrenderDate() != null) {
					LocalDate taxUpTo = this.validity(ServiceCodeEnum.QLY_TAX.getCode());
					LocalDate quaterStartDate = taxUpTo.minusMonths(3).plusDays(1);
					// quaterStartDate = quaterStartDate.
					if (listOfInActivePermits.get().getPermitSurrenderDate().isBefore(taxUpTo)
							&& listOfInActivePermits.get().getPermitSurrenderDate().isAfter(quaterStartDate)) {
						permitType = listOfInActivePermits.get().getPermitType().getPermitType();
						if ((registrationDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.COCT.getCovCode())
								|| registrationDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.TOVT.getCovCode()))
								&& (listOfInActivePermits.get().getRouteDetails().getRouteType() == null
										|| StringUtils.isBlank(listOfInActivePermits.get().getRouteDetails()
												.getRouteType().getRouteCode()))) {
							throw new BadRequestException(
									"route code not found for pr: " + registrationDetails.getPrNo());
						}
						if (listOfInActivePermits.get().getRouteDetails() != null
								&& listOfInActivePermits.get().getRouteDetails().getRouteType() != null
								&& listOfInActivePermits.get().getRouteDetails().getRouteType()
										.getRouteCode() != null) {
							routeCode = listOfInActivePermits.get().getRouteDetails().getRouteType().getRouteCode();
						}
					}
				}
			}
		}
		return Pair.of(permitType, routeCode);
	}

	private Pair<Long, Double> lifeTaxCalculation(boolean isApplicationFromMvi, boolean isChassesApplication,
			boolean isOtherState, RegServiceDTO regServiceDTO, RegistrationDetailsDTO registrationDetails,
			StagingRegistrationDetailsDTO stagingRegistrationDetails) {
		Double totalLifeTax;
		double vehicleAge;
		RegServiceVO vo = regServiceMapper.convertEntity(regServiceDTO);
		OtherStateApplictionType applicationType = getOtherStateVehicleStatus(vo);
		if (Arrays.asList(OtherStateApplictionType.ApplicationNO, OtherStateApplictionType.TrNo)
				.contains(applicationType)) {
			vehicleAge = 0;
		} else {
			LocalDate entryDate = getEarlerDate(regServiceDTO.getnOCDetails().getDateOfEntry(),
					regServiceDTO.getnOCDetails().getIssueDate());
			vehicleAge = calculateAgeOfTheVehicle(regServiceDTO.getRegistrationDetails().getPrIssueDate(), entryDate);

		}
		Optional<MasterTax> OptionalLifeTax = Optional.empty();
		if (regServiceDTO.getRegistrationDetails().getOwnerType().equals(OwnerTypeEnum.Individual) && !(regServiceDTO
				.getRegistrationDetails().getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.MCYN.getCovCode())
				|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.IVCN.getCovCode()))) {
			OptionalLifeTax = taxTypeDAO
					.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndToageGreaterThanEqualAndFromageLessThanEqualAndTocostGreaterThanEqualAndFromcostLessThanEqual(
							regServiceDTO.getRegistrationDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), "OS", regStatus,
							vehicleAge, vehicleAge,
							regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue(),
							regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue());

		} else {
			OptionalLifeTax = taxTypeDAO
					.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndToageGreaterThanEqualAndFromageLessThanEqual(
							regServiceDTO.getRegistrationDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getOwnerType().getCode(), "OS", regStatus,
							vehicleAge, vehicleAge);
		}
		if (!OptionalLifeTax.isPresent()) {
			logger.error(
					"No record found in master tax for " + regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							+ " cov with Ownership type " + regServiceDTO.getRegistrationDetails().getOwnerType());
			// throw error message
			throw new BadRequestException(
					"No record found in master tax for " + regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							+ " cov with Ownership type " + regServiceDTO.getRegistrationDetails().getOwnerType());
		}
		totalLifeTax = (regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue()
				* OptionalLifeTax.get().getPercent() / 100f);
		Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, regServiceDTO,
				registrationDetails, totalLifeTax, OptionalLifeTax.get().getPercent(), isApplicationFromMvi,
				isChassesApplication, isOtherState);
		return lifeTax;
	}

	@Override
	public Pair<Optional<MasterPayperiodDTO>, Boolean> getPayPeroidForBoth(Optional<MasterPayperiodDTO> Payperiod,
			String seatingCapacity, Integer gvw) {
		Boolean gostatus = Boolean.FALSE;
		if (Payperiod.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.OBPN.getCovCode())) {
			if (Integer.parseInt(seatingCapacity) > 10) {
				Payperiod.get().setPayperiod(TaxTypeEnum.QuarterlyTax.getCode());
			} else {
				// gostatus = Boolean.TRUE;
				Payperiod.get().setPayperiod(TaxTypeEnum.LifeTax.getCode());
			}
		} else if (Payperiod.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.ARKT.getCovCode())) {
			if (Integer.parseInt(seatingCapacity) <= 4) {
				gostatus = Boolean.TRUE;
				Payperiod.get().setPayperiod(TaxTypeEnum.LifeTax.getCode());
			} else {
				Payperiod.get().setPayperiod(TaxTypeEnum.QuarterlyTax.getCode());
			}
		} else if (Payperiod.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.TGVT.getCovCode())
				|| Payperiod.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.GCRT.getCovCode())) {
			if (gvw <= 3000) {
				gostatus = Boolean.TRUE;
				Payperiod.get().setPayperiod(TaxTypeEnum.LifeTax.getCode());
			} else {
				Payperiod.get().setPayperiod(TaxTypeEnum.QuarterlyTax.getCode());
			}
		}
		return Pair.of(Payperiod, gostatus);
	}

	public List<String> taxTypes() {
		List<String> taxTypes = new ArrayList<>();
		taxTypes.add(ServiceCodeEnum.QLY_TAX.getCode());
		taxTypes.add(ServiceCodeEnum.HALF_TAX.getCode());
		taxTypes.add(ServiceCodeEnum.YEAR_TAX.getCode());
		return taxTypes;
	}

	@Override
	public boolean checkTaxUpToDateOrNote(boolean isApplicationFromMvi, boolean isChassesApplication,
			RegServiceDTO regServiceDTO, RegistrationDetailsDTO registrationDetails,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, String taxType) {
		LocalDate currentTaxValidity = validity(taxType);

		TaxHelper lastTaxTillDate = getLastPaidTax(registrationDetails, regServiceDTO, isApplicationFromMvi,
				currentTaxValidity, stagingRegistrationDetails, isChassesApplication, taxTypes(), Boolean.FALSE);
		if (lastTaxTillDate != null && lastTaxTillDate.getTax() != null && lastTaxTillDate.getValidityTo() != null) {
			if (!lastTaxTillDate.isAnypendingQuaters()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private Optional<MasterTax> getSeatingCapacityTax(String classOfVehicle, String seatingCapacity, String stateCode,
			String permitcode, String routeCode) {
		Optional<MasterTax> OptionalTax;
		if (StringUtils.isNoneBlank(routeCode)) {
			OptionalTax = taxTypeDAO
					.findByCovcodeAndSeattoGreaterThanEqualAndSeatfromLessThanEqualAndStatecodeAndPermitcodeAndServType(
							classOfVehicle, Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity),
							stateCode, permitcode, routeCode);
		} else {
			OptionalTax = taxTypeDAO
					.findByCovcodeAndSeattoGreaterThanEqualAndSeatfromLessThanEqualAndStatecodeAndPermitcode(
							classOfVehicle, Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity),
							stateCode, permitcode);
		}

		return OptionalTax;
	}

	private Optional<MasterTax> getRlwTax(String cov, Integer rlw, String stateCode, String permitType) {
		Optional<MasterTax> OptionalTax;
		if (StringUtils.isBlank(permitType)) {
			permitType = permitcode;
		}
		OptionalTax = taxTypeDAO.findByCovcodeAndTorlwGreaterThanEqualAndFromrlwLessThanEqualAndStatecodeAndPermitcode(
				cov, rlw, rlw, stateCode, permitType);

		return OptionalTax;
	}

	private Optional<MasterTax> getUlwTax(String cov, Integer ulw, String stateCode, String permitType) {
		Optional<MasterTax> OptionalTax;
		if (StringUtils.isBlank(permitType)) {
			permitType = permitcode;
		}
		OptionalTax = taxTypeDAO.findByCovcodeAndToulwGreaterThanEqualAndFromulwLessThanEqualAndStatecodeAndPermitcode(
				cov, ulw, ulw, stateCode, permitType);
		return OptionalTax;
	}

	private TaxCalculationHelper quarterlyTaxCalculation(Optional<MasterTax> OptionalTax, String taxBasedon,
			RegistrationDetailsDTO regDetails, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication, String taxType,
			String classOfvehicle, boolean isOtherState, List<ServiceEnum> serviceEnum, String permitTypeCode,
			String routeCodes, String oldpermitType, Boolean isWeightAlt) {
		/*
		 * if (StringUtils.isEmpty(taxTypes)) {
		 * logger.error("Tax type is missing in staging details [{}]." ,
		 * stagingRegDetails.getApplicationNo()); throw new
		 * BadRequestException("Tax type is missing in staging details: " +
		 * stagingRegDetails.getApplicationNo()); }
		 */
		Double totalTax = 0d;
		Integer quaternNumber = 0;
		Integer indexPosition = 0;
		List<Integer> quaterOne = new ArrayList<>();
		List<Integer> quaterTwo = new ArrayList<>();
		List<Integer> quaterThree = new ArrayList<>();
		List<Integer> quaterFour = new ArrayList<>();
		quaterOne.add(0, 4);
		quaterOne.add(1, 5);
		quaterOne.add(2, 6);
		quaterTwo.add(0, 7);
		quaterTwo.add(1, 8);
		quaterTwo.add(2, 9);
		quaterThree.add(0, 10);
		quaterThree.add(1, 11);
		quaterThree.add(2, 12);
		quaterFour.add(0, 1);
		quaterFour.add(1, 2);
		quaterFour.add(2, 3);

		Double currentquaterTax = 0d;
		Double currentquaterTaxArrears = 0d;
		Double quaterTax = 0d;
		Float tax = null;
		LocalDate Validity = null;
		Double penalits = 0d;
		Double penalitArrears = 0d;
		Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = Optional.empty();
		Optional<AlterationDTO> alterDetails = Optional.empty();
		TaxTypeEnum.TaxPayType payTaxType = TaxTypeEnum.TaxPayType.REG;
		if (isApplicationFromMvi || isChassesApplication) {
			payTaxType = TaxTypeEnum.TaxPayType.DIFF;
		}
		if (isChassesApplication) {
			// need to call body builder cov
			alterDetails = alterationDao.findByApplicationNo(stagingRegistrationDetails.getApplicationNo());
			if (!alterDetails.isPresent()) {
				throw new BadRequestException(
						"No record found in alteration document for: " + stagingRegistrationDetails.getApplicationNo());
			}
		}
		if (isOtherState) {
			if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				alterDetails = alterationDao.findByApplicationNo(regServiceDTO.getApplicationNo());
				if (!alterDetails.isPresent()) {
					throw new BadRequestException(
							"No record found in alteration document for: " + regServiceDTO.getApplicationNo());
				}
			}
		}
		if (taxBasedon.equalsIgnoreCase(seatingCapacityCode)) {
			if (isChassesApplication) {
				// need to call body builder cov
				tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(alterDetails.get().getSeating()) - 1));

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null
						&& regServiceDTO.getAlterationDetails().getSeating() != null) {
					tax = (OptionalTax.get().getTaxamount()
							* (Integer.parseInt(regServiceDTO.getAlterationDetails().getSeating()) - 1));
				} else {
					tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity()) - 1));
				}
			} else if (isOtherState) {
				if (alterDetails != null && alterDetails.isPresent()
						&& StringUtils.isNoneBlank(alterDetails.get().getSeating())) {
					tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(alterDetails.get().getSeating()) - 1));
				} else {
					tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity()) - 1));
				}
			} else {
				if ((regDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.TOVT.getCovCode())
						|| regDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.COCT.getCovCode()))
						&& StringUtils.isNoneBlank(oldpermitType)
						&& oldpermitType.equalsIgnoreCase(PermitsEnum.PermitCodes.AITP.getPermitCode())) {

					tax = (OptionalTax.get().getTaxamount()
							* (Integer.parseInt(regDetails.getVehicleDetails().getSeatingCapacity()) - 1));
				} else {
					tax = (OptionalTax.get().getTaxamount()
							* (Integer.parseInt(regDetails.getVehicleDetails().getSeatingCapacity()) - 1));
				}
			}

			TaxCalculationHelper taxAndQuaternNumber = plainTaxCalculation(tax, quaterOne, quaterTwo, quaterThree,
					quaterFour, regDetails, currentquaterTax, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, classOfvehicle, isOtherState, taxType,
					serviceEnum, permitTypeCode, routeCodes, isWeightAlt);
			quaterTax = tax.doubleValue();
			if (taxAndQuaternNumber.getCurrentQuaterTax() != null && taxAndQuaternNumber.getCurrentQuaterTax() != 0) {
				payTaxType = TaxTypeEnum.TaxPayType.DIFF;
				quaterTax = taxAndQuaternNumber.getCurrentQuaterTax();
			}

			LocalDate currentTaxValidity = validity(taxType);
			if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
				Pair<Long, LocalDate> cessAndValidity = cessTaxCalculation(quaterTax, regDetails, currentTaxValidity,
						regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
						classOfvehicle, isOtherState);
				// getCesFee(tax.doubleValue(), classOfvehicle);
				TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
				// taxCalculationHelper.setQuaterTax(cessAndValidity.getFirst().doubleValue());
				taxCalculationHelper.setReoundTax(cessAndValidity.getFirst());
				taxCalculationHelper.setTaxTill(cessAndValidity.getSecond());
				return taxCalculationHelper;
			}
			currentquaterTax = taxAndQuaternNumber.getQuaterTax();
			quaternNumber = taxAndQuaternNumber.getQuaternNumber();
			indexPosition = taxAndQuaternNumber.getIndexPosition();
			penalits = taxAndQuaternNumber.getPenality();
			penalitArrears = taxAndQuaternNumber.getPenalityArrears();
			currentquaterTaxArrears = taxAndQuaternNumber.getQuaterTaxArrears();

			if (isChassesApplication) {
				// need to call body builder cov
				optionalTaxExcemption = getSeatingCapacityExcemptionsTax(alterDetails.get().getCov(),
						alterDetails.get().getSeating());

			} else if (isApplicationFromMvi) {
				if (regServiceDTO.getAlterationDetails() != null) {
					String seating = regServiceDTO.getAlterationDetails().getSeating() != null
							? regServiceDTO.getAlterationDetails().getSeating()
							: regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
					String cov = regServiceDTO.getAlterationDetails().getCov() != null
							? regServiceDTO.getAlterationDetails().getCov()
							: regServiceDTO.getRegistrationDetails().getClassOfVehicle();
					optionalTaxExcemption = getSeatingCapacityExcemptionsTax(cov, seating);
				} else {

					optionalTaxExcemption = getSeatingCapacityExcemptionsTax(
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
							regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity());
				}

			} else {
				if (isOtherState) {

					if (alterDetails != null && alterDetails.isPresent()) {
						String seating = alterDetails.get().getSeating() != null ? alterDetails.get().getSeating()
								: regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity();
						String cov = alterDetails.get().getCov();
						optionalTaxExcemption = getSeatingCapacityExcemptionsTax(cov, seating);
					} else {

						optionalTaxExcemption = getSeatingCapacityExcemptionsTax(
								regServiceDTO.getRegistrationDetails().getVahanDetails().getVehicleClass(),
								regServiceDTO.getRegistrationDetails().getVahanDetails().getSeatingCapacity());
					}

				} else {
					optionalTaxExcemption = getSeatingCapacityExcemptionsTax(
							regDetails.getVehicleDetails().getClassOfVehicle(),
							regDetails.getVehicleDetails().getSeatingCapacity());
				}
			}
			if (optionalTaxExcemption.isPresent()
					&& optionalTaxExcemption.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {
				Validity = calculateTaxUpTo(indexPosition, quaternNumber);
				TaxHelper taxAndPenality = currentQuaterTaxCalculation(
						optionalTaxExcemption.get().getTaxvalue().doubleValue(), indexPosition, regDetails, Validity,
						regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
						classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCodes, isWeightAlt);
				quaterTax = optionalTaxExcemption.get().getTaxvalue().doubleValue();
				penalits = taxAndQuaternNumber.getPenality();
				if (taxAndPenality.getQuaterAmount() != null && taxAndPenality.getQuaterAmount() != 0) {
					payTaxType = TaxTypeEnum.TaxPayType.DIFF;
					quaterTax = taxAndPenality.getQuaterAmount();
				}

				if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
					Pair<Long, LocalDate> cessAndValidity = cessTaxCalculation(quaterTax, regDetails,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, isOtherState);
					// getCesFee(optionalTaxExcemption.get().getTaxvalue().doubleValue(),
					// classOfvehicle);
					TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
					taxCalculationHelper.setReoundTax(cessAndValidity.getFirst());
					taxCalculationHelper.setTaxTill(cessAndValidity.getSecond());
					// penalits = taxAndQuaternNumber.getPenality();
					return taxCalculationHelper;
				}
				currentquaterTax = taxAndPenality.getTax();
				currentquaterTaxArrears = taxAndPenality.getTaxArrears();
				penalits = Double.valueOf(taxAndPenality.getPenalty());
				penalitArrears = Double.valueOf(taxAndPenality.getPenaltyArrears());
			}
		} else if (OptionalTax.get().getIncrementalweight() == 0) {

			TaxCalculationHelper taxAndQuaternNumber = plainTaxCalculation(OptionalTax.get().getTaxamount(), quaterOne,
					quaterTwo, quaterThree, quaterFour, regDetails, currentquaterTax, regServiceDTO,
					isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication, classOfvehicle,
					isOtherState, taxType, serviceEnum, permitTypeCode, routeCodes, isWeightAlt);
			quaterTax = OptionalTax.get().getTaxamount().doubleValue();
			penalits = taxAndQuaternNumber.getPenality();
			LocalDate currentTaxValidity = validity(taxType);
			if (taxAndQuaternNumber.getCurrentQuaterTax() != null && taxAndQuaternNumber.getCurrentQuaterTax() != 0) {
				payTaxType = TaxTypeEnum.TaxPayType.DIFF;
				quaterTax = taxAndQuaternNumber.getCurrentQuaterTax();
			}
			if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
				Pair<Long, LocalDate> cessAndValidity = cessTaxCalculation(quaterTax, regDetails, currentTaxValidity,
						regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
						classOfvehicle, isOtherState);
				// getCesFee(OptionalTax.get().getTaxamount().doubleValue(), classOfvehicle);
				TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
				taxCalculationHelper.setReoundTax(cessAndValidity.getFirst());
				taxCalculationHelper.setTaxTill(cessAndValidity.getSecond());
				penalits = taxAndQuaternNumber.getPenality();
				return taxCalculationHelper;
			}

			currentquaterTax = taxAndQuaternNumber.getQuaterTax();
			quaternNumber = taxAndQuaternNumber.getQuaternNumber();
			indexPosition = taxAndQuaternNumber.getIndexPosition();
			penalits = taxAndQuaternNumber.getPenality();
			currentquaterTaxArrears = taxAndQuaternNumber.getQuaterTaxArrears();
			penalitArrears = taxAndQuaternNumber.getPenalityArrears();

		} else {
			if (taxBasedon.equalsIgnoreCase(ulwCode)) {
				LocalDate currentTaxValidity = validity(taxType);
				if (quaterOne.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterOne.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 1;
					Validity = calculateTaxUpTo(indexPosition, quaternNumber);

					TaxCalculationHelper currentAndQuaterTax = ulwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
					// TODO:need to tall all quater

				} else if (quaterTwo.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterTwo.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 2;
					Validity = calculateTaxUpTo(indexPosition, quaternNumber);
					TaxCalculationHelper currentAndQuaterTax = ulwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				} else if (quaterThree.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterThree.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 3;
					Validity = calculateTaxUpTo(indexPosition, quaternNumber);
					TaxCalculationHelper currentAndQuaterTax = ulwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				} else if (quaterFour.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterFour.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 4;
					Validity = calculateTaxUpTo(indexPosition, quaternNumber);
					TaxCalculationHelper currentAndQuaterTax = ulwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				}

			} else if (taxBasedon.equalsIgnoreCase(rlwCode)) {
				LocalDate currentTaxValidity = validity(taxType);
				if (quaterOne.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterOne.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 1;
					TaxCalculationHelper currentAndQuaterTax = rlwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				} else if (quaterTwo.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterTwo.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 2;
					TaxCalculationHelper currentAndQuaterTax = rlwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				} else if (quaterThree.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterThree.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 3;
					TaxCalculationHelper currentAndQuaterTax = rlwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				} else if (quaterFour.contains(LocalDate.now().getMonthValue())) {
					indexPosition = quaterFour.indexOf(LocalDate.now().getMonthValue());
					quaternNumber = 4;
					TaxCalculationHelper currentAndQuaterTax = rlwQuaterTax(OptionalTax, regDetails, indexPosition,
							currentTaxValidity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails,
							isChassesApplication, classOfvehicle, taxType, isOtherState, serviceEnum, permitTypeCode,
							routeCodes, isWeightAlt);
					currentquaterTax = currentAndQuaterTax.getQuaterTax();
					quaterTax = currentAndQuaterTax.getCurrentQuaterTax();
					penalits = currentAndQuaterTax.getPenality();
					currentquaterTaxArrears = currentAndQuaterTax.getQuaterTaxArrears();
					penalitArrears = currentAndQuaterTax.getPenalityArrears();
					payTaxType = currentAndQuaterTax.getTaxPayType();
				}
			}
		}
		if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
			Pair<Long, LocalDate> cessAndValidity = cessTaxCalculation(quaterTax, regDetails, Validity, regServiceDTO,
					isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication, classOfvehicle,
					isOtherState);
			// getCesFee(result.doubleValue(), classOfvehicle);
			TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
			taxCalculationHelper.setReoundTax(cessAndValidity.getFirst());
			taxCalculationHelper.setTaxTill(cessAndValidity.getSecond());
			return taxCalculationHelper;
		}
		TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
		if (taxType.equalsIgnoreCase(TaxTypeEnum.QuarterlyTax.getDesc())) {
			LocalDate quarterlyValidity = calculateTaxUpTo(indexPosition, quaternNumber);
			if (isWeightAlt != null && isWeightAlt) {
				TaxHelper lastTaxTillDate = getLastPaidTax(regDetails, regServiceDTO, isApplicationFromMvi,
						quarterlyValidity, stagingRegistrationDetails, isChassesApplication, this.taxTypes(),
						isOtherState);
				quarterlyValidity = lastTaxTillDate.getValidityTo();
			}

			long roundtax = roundUpperTen(currentquaterTax);
			// TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
			taxCalculationHelper.setReoundTax(roundtax);
			taxCalculationHelper.setTaxTill(quarterlyValidity);
			taxCalculationHelper.setPenality(penalits);
			taxCalculationHelper.setReoundTaxArrears(roundUpperTen(currentquaterTaxArrears));
			taxCalculationHelper.setPenalityArrears(penalitArrears);
			taxCalculationHelper.setTaxPayType(payTaxType);
			return taxCalculationHelper;

		} else if (taxType.equalsIgnoreCase(TaxTypeEnum.HalfyearlyTax.getDesc())) {
			LocalDate halfyearValidity = calculateTaxUpTo(indexPosition, quaternNumber);
			halfyearValidity = (halfyearValidity.plusMonths(3));
			totalTax = quaterTax + currentquaterTax;
			/*
			 * Optional<MasterTaxExcemptionsDTO> excemptionPercentage =
			 * masterTaxExcemptionsDAO .findByKeyvalue(classOfvehicle); if
			 * (excemptionPercentage.isPresent() &&
			 * !excemptionPercentage.get().getValuetype().equalsIgnoreCase(
			 * seatingCapacityCode)) { // check percentage discount double discount =
			 * (totalTax * excemptionPercentage.get().getTaxvalue()) / 100; totalTax =
			 * totalTax - discount; }
			 */
			long roundtax = roundUpperTen(totalTax);
			if (serviceEnum != null && !serviceEnum.isEmpty()
					&& serviceEnum.stream()
							.anyMatch(service -> Arrays.asList(ServiceEnum.NEWPERMIT, ServiceEnum.VARIATIONOFPERMIT)
									.stream().anyMatch(serviceName -> serviceName.equals(service)))) {
				if (currentquaterTax <= 0) {
					roundtax = 0l;
				}
			}
			if (isWeightAlt != null && isWeightAlt) {
				TaxHelper lastTaxTillDate = getLastPaidTax(regDetails, regServiceDTO, isApplicationFromMvi,
						halfyearValidity, stagingRegistrationDetails, isChassesApplication, this.taxTypes(),
						isOtherState);
				halfyearValidity = lastTaxTillDate.getValidityTo();
				roundtax = roundUpperTen(currentquaterTax);
			}

			taxCalculationHelper.setReoundTax(roundtax);
			taxCalculationHelper.setTaxTill(halfyearValidity);
			taxCalculationHelper.setPenality(penalits);
			taxCalculationHelper.setReoundTaxArrears(roundUpperTen(currentquaterTaxArrears));
			taxCalculationHelper.setPenalityArrears(penalitArrears);
			taxCalculationHelper.setTaxPayType(payTaxType);
			if (isApplicationFromMvi) {
				List<String> listTaxType = this.taxTypes();
				listTaxType.add(ServiceCodeEnum.LIFE_TAX.getCode());
				TaxHelper oldTaxDetails = getIsGreenTaxPending(
						regServiceDTO.getRegistrationDetails().getApplicationNo(), listTaxType, LocalDate.now(),
						regServiceDTO.getRegistrationDetails().getPrNo());
				taxCalculationHelper.setReoundTax(roundUpperTen(currentquaterTax));
				if (oldTaxDetails.getValidityTo() != null) {
					taxCalculationHelper.setTaxTill(oldTaxDetails.getValidityTo());
				}
			}

			return taxCalculationHelper;
		} else if (taxType.equalsIgnoreCase(TaxTypeEnum.YearlyTax.getDesc())) {
			LocalDate yearValidity = calculateTaxUpTo(indexPosition, quaternNumber);
			yearValidity = (yearValidity.plusMonths(9));
			totalTax = (quaterTax * 3) + currentquaterTax;
			/*
			 * Optional<MasterTaxExcemptionsDTO> excemptionPercentage =
			 * masterTaxExcemptionsDAO .findByKeyvalue(classOfvehicle); if
			 * (excemptionPercentage.isPresent() &&
			 * !excemptionPercentage.get().getValuetype().equalsIgnoreCase(
			 * seatingCapacityCode)) { // check percentage discount double discount =
			 * (totalTax * excemptionPercentage.get().getTaxvalue()) / 100; totalTax =
			 * totalTax - discount; }
			 */
			long roundtax = roundUpperTen(totalTax);
			if (serviceEnum != null && !serviceEnum.isEmpty()
					&& serviceEnum.stream()
							.anyMatch(service -> Arrays.asList(ServiceEnum.NEWPERMIT, ServiceEnum.VARIATIONOFPERMIT)
									.stream().anyMatch(serviceName -> serviceName.equals(service)))) {
				if (currentquaterTax <= 0) {
					roundtax = 0l;
				}
			}
			if (isWeightAlt != null && isWeightAlt) {
				TaxHelper lastTaxTillDate = getLastPaidTax(regDetails, regServiceDTO, isApplicationFromMvi,
						yearValidity, stagingRegistrationDetails, isChassesApplication, this.taxTypes(), isOtherState);
				yearValidity = lastTaxTillDate.getValidityTo();
				roundtax = roundUpperTen(currentquaterTax);
			}

			// TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
			taxCalculationHelper.setReoundTax(roundtax);
			taxCalculationHelper.setTaxTill(yearValidity);
			taxCalculationHelper.setPenality(penalits);
			taxCalculationHelper.setReoundTaxArrears(roundUpperTen(currentquaterTaxArrears));
			taxCalculationHelper.setPenalityArrears(penalitArrears);
			taxCalculationHelper.setTaxPayType(payTaxType);
			if (isApplicationFromMvi) {
				List<String> listTaxType = this.taxTypes();
				listTaxType.add(ServiceCodeEnum.LIFE_TAX.getCode());
				TaxHelper oldTaxDetails = getIsGreenTaxPending(
						regServiceDTO.getRegistrationDetails().getApplicationNo(), listTaxType, LocalDate.now(),
						regServiceDTO.getRegistrationDetails().getPrNo());
				taxCalculationHelper.setReoundTax(roundUpperTen(currentquaterTax));
				if (oldTaxDetails.getValidityTo() != null) {
					taxCalculationHelper.setTaxTill(oldTaxDetails.getValidityTo());
				}
			}
			return taxCalculationHelper;
		} else if (taxType.equalsIgnoreCase(TaxTypeEnum.LifeTax.getDesc())) {
			LocalDate quarterlyValidity = calculateTaxUpTo(indexPosition, quaternNumber);
			String cov = regServiceDTO != null ? regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					: regDetails.getClassOfVehicle();
			String seats = regServiceDTO != null
					? regServiceDTO.getRegistrationDetails().getVahanDetails().getSeatingCapacity()
					: regDetails.getVahanDetails().getSeatingCapacity();
			Integer gvw = regServiceDTO != null ? regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw()
					: regDetails.getVahanDetails().getGvw();
			if ((cov.equalsIgnoreCase(ClassOfVehicleEnum.ARKT.getCovCode()) && Integer.parseInt(seats) <= 4)
					|| (((cov.equalsIgnoreCase(ClassOfVehicleEnum.TGVT.getCovCode()))
							|| cov.equalsIgnoreCase(ClassOfVehicleEnum.GCRT.getCovCode())) && gvw <= 3000)) {

				long roundtax = roundUpperTen(currentquaterTax);
				// TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
				taxCalculationHelper.setReoundTax(roundtax);
				taxCalculationHelper.setTaxTill(quarterlyValidity);
				taxCalculationHelper.setPenality(penalits);
				taxCalculationHelper.setReoundTaxArrears(roundUpperTen(currentquaterTaxArrears));
				taxCalculationHelper.setPenalityArrears(penalitArrears);
				taxCalculationHelper.setTaxPayType(payTaxType);
				return taxCalculationHelper;
			}
		}

		return taxCalculationHelper;
	}

	private Optional<MasterTaxExcemptionsDTO> getSeatingCapacityExcemptionsTax(String classOfVehicle,
			String seatingCapacity) {
		Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption;
		optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueAndSeattoGreaterThanEqualAndSeatfromLessThanEqual(
				classOfVehicle, Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity));

		return optionalTaxExcemption;
	}

	private TaxCalculationHelper ulwQuaterTax(Optional<MasterTax> OptionalTax, RegistrationDetailsDTO stagingRegDetails,
			Integer valu, LocalDate Validity, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, String taxType, boolean isOtherState, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, Boolean isWeightAlt) {
		Double quaterTax1;
		Float weight = null;
		TaxHelper currentquaterTaxAndPenality = new TaxHelper();
		TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
		TaxTypeEnum.TaxPayType payTaxType = TaxTypeEnum.TaxPayType.REG;
		if (isChassesApplication) {
			// need to call body builder cov
			// need to call body builder cov
			Optional<AlterationDTO> alterDetails = alterationDao
					.findByApplicationNo(stagingRegistrationDetails.getApplicationNo());
			if (!alterDetails.isPresent()) {
				throw new BadRequestException(
						"No record found in master_tax for: " + stagingRegistrationDetails.getApplicationNo());
			}
			weight = (float) ((alterDetails.get().getUlw() - ((OptionalTax.get().getFromulw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());

		} else if (isApplicationFromMvi) {
			if (regServiceDTO.getAlterationDetails() != null && regServiceDTO.getAlterationDetails().getUlw() != null) {
				weight = (float) ((regServiceDTO.getAlterationDetails().getUlw()
						- ((OptionalTax.get().getFromulw() - 1)))
						/ OptionalTax.get().getIncrementalweight().doubleValue());
			} else {
				weight = (float) ((regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw()
						- ((OptionalTax.get().getFromulw() - 1)))
						/ OptionalTax.get().getIncrementalweight().doubleValue());
			}
		} else if (isOtherState) {

			weight = (float) ((regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw()
					- ((OptionalTax.get().getFromulw() - 1))) / OptionalTax.get().getIncrementalweight().doubleValue());
		} else {
			weight = (float) ((stagingRegDetails.getVehicleDetails().getUlw() - ((OptionalTax.get().getFromulw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());
		}

		Float result = (float) ((Math.ceil(weight.doubleValue()) * OptionalTax.get().getIncrementalamount())
				+ OptionalTax.get().getTaxamount());
		quaterTax1 = result.doubleValue();
		currentquaterTaxAndPenality = currentQuaterTaxCalculation(result.doubleValue(), valu, stagingRegDetails,
				Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
				classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
		if (currentquaterTaxAndPenality.getQuaterAmount() != null
				&& currentquaterTaxAndPenality.getQuaterAmount() != 0) {
			payTaxType = TaxTypeEnum.TaxPayType.DIFF;
			quaterTax1 = currentquaterTaxAndPenality.getQuaterAmount();
		}

		taxCalculationHelper.setQuaterTax(currentquaterTaxAndPenality.getTax());
		taxCalculationHelper.setQuaterTaxArrears(currentquaterTaxAndPenality.getTaxArrears());
		taxCalculationHelper.setCurrentQuaterTax(quaterTax1);
		taxCalculationHelper.setPenality(Double.valueOf(currentquaterTaxAndPenality.getPenalty()));
		taxCalculationHelper.setPenalityArrears(Double.valueOf(currentquaterTaxAndPenality.getPenaltyArrears()));
		taxCalculationHelper.setTaxPayType(payTaxType);
		return taxCalculationHelper;
	}

	private TaxCalculationHelper rlwQuaterTax(Optional<MasterTax> OptionalTax, RegistrationDetailsDTO stagingRegDetails,
			Integer valu, LocalDate Validity, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, String taxType, boolean isOtherState, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, Boolean isWeightAlt) {
		TaxHelper currentquaterTaxAndPenality = new TaxHelper();
		TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
		TaxTypeEnum.TaxPayType payTaxType = TaxTypeEnum.TaxPayType.REG;
		Double quaterTax1;
		Double weight = null;
		if (isChassesApplication) {
			Integer gvw = getGvwWeight(stagingRegistrationDetails.getApplicationNo(),
					stagingRegistrationDetails.getVahanDetails().getGvw());
			// need to call body builder cov
			weight = (double) ((gvw - ((OptionalTax.get().getFromrlw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());

		} else if (isApplicationFromMvi) {
			// need to chek avt weight
			// need to chamge gvw
			Integer gvw = getGvwWeightForAlt(regServiceDTO);
			weight = (double) ((gvw - ((OptionalTax.get().getFromrlw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());
		} else if (isOtherState) {
			Integer gvw = getGvwWeightForCitizen(regServiceDTO.getRegistrationDetails());
			weight = (double) ((gvw - ((OptionalTax.get().getFromrlw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());
		} else {
			Integer gvw = getGvwWeightForCitizen(stagingRegDetails);
			weight = (double) ((gvw - ((OptionalTax.get().getFromrlw() - 1)))
					/ OptionalTax.get().getIncrementalweight().doubleValue());
		}

		Double result = ((Math.ceil(weight) * OptionalTax.get().getIncrementalamount())
				+ OptionalTax.get().getTaxamount());
		quaterTax1 = result.doubleValue();

		currentquaterTaxAndPenality = currentQuaterTaxCalculation(result.doubleValue(), valu, stagingRegDetails,
				Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
				classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
		if (currentquaterTaxAndPenality.getQuaterAmount() != null
				&& currentquaterTaxAndPenality.getQuaterAmount() != 0) {
			payTaxType = TaxTypeEnum.TaxPayType.DIFF;
			quaterTax1 = currentquaterTaxAndPenality.getQuaterAmount();
		}

		taxCalculationHelper.setQuaterTax(currentquaterTaxAndPenality.getTax());
		taxCalculationHelper.setQuaterTaxArrears(currentquaterTaxAndPenality.getTaxArrears());
		taxCalculationHelper.setCurrentQuaterTax(quaterTax1);
		taxCalculationHelper.setPenality(Double.valueOf(currentquaterTaxAndPenality.getPenalty()));
		taxCalculationHelper.setPenalityArrears(Double.valueOf(currentquaterTaxAndPenality.getPenaltyArrears()));
		taxCalculationHelper.setTaxPayType(payTaxType);
		return taxCalculationHelper;
	}

	private TaxCalculationHelper plainTaxCalculation(Float OptionalTax, List<Integer> quaterOne,
			List<Integer> quaterTwo, List<Integer> quaterThree, List<Integer> quaterFour,
			RegistrationDetailsDTO stagingRegDetails, Double currentquaterTax, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, StagingRegistrationDetailsDTO stagingRegistrationDetails,
			boolean isChassesApplication, String classOfvehicle, boolean isOtherState, String taxType,
			List<ServiceEnum> serviceEnum, String permitTypeCode, String routeCode, Boolean isWeightAlt) {
		TaxCalculationHelper taxCalculationHelper = new TaxCalculationHelper();
		Integer indexPosition = 0;
		Integer quaternNumber = 0;
		LocalDate Validity = null;
		TaxHelper taxAndPenality = new TaxHelper();
		;
		if (quaterOne.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterOne.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 1;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
			taxAndPenality = currentQuaterTaxCalculation(OptionalTax.doubleValue(), indexPosition, stagingRegDetails,
					Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
					classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
			currentquaterTax = taxAndPenality.getTax();
		} else if (quaterTwo.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterTwo.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 2;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
			taxAndPenality = currentQuaterTaxCalculation(OptionalTax.doubleValue(), indexPosition, stagingRegDetails,
					Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
					classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
			currentquaterTax = taxAndPenality.getTax();
		} else if (quaterThree.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterThree.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 3;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
			taxAndPenality = currentQuaterTaxCalculation(OptionalTax.doubleValue(), indexPosition, stagingRegDetails,
					Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
					classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
			currentquaterTax = taxAndPenality.getTax();
		} else if (quaterFour.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterFour.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 4;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
			taxAndPenality = currentQuaterTaxCalculation(OptionalTax.doubleValue(), indexPosition, stagingRegDetails,
					Validity, regServiceDTO, isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication,
					classOfvehicle, isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, isWeightAlt);
			currentquaterTax = taxAndPenality.getTax();
		}
		taxCalculationHelper.setQuaterTax(currentquaterTax);
		taxCalculationHelper.setQuaterTaxArrears(taxAndPenality.getTaxArrears());
		taxCalculationHelper.setIndexPosition(indexPosition);
		taxCalculationHelper.setQuaternNumber(quaternNumber);
		taxCalculationHelper.setTaxTill(Validity);
		taxCalculationHelper.setPenality(Double.valueOf(taxAndPenality.getPenalty()));
		taxCalculationHelper.setPenalityArrears(Double.valueOf(taxAndPenality.getPenaltyArrears()));
		taxCalculationHelper.setCurrentQuaterTax(taxAndPenality.getQuaterAmount());
		return taxCalculationHelper;
	}

	private TaxHelper getChassistax(Double OptionalTax, RegistrationDetailsDTO stagingRegDetails,
			LocalDate currentTaxTill, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, boolean isOtherState) {
		Long totalMonths = null;
		Double quaterTax = 0d;
		Double taxArr = 0d;
		Double newCovPenality = 0d;
		Optional<AlterationDTO> alterDetails = Optional.empty();
		TaxHelper lastTaxTillDate = null;
		String trNo = null;
		boolean ispaisdThroughVCR = Boolean.FALSE;
		if (isOtherState) {
			if (regServiceDTO != null
					&& (regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
							|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				trNo = regServiceDTO.getRegistrationDetails().getTrNo();
				alterDetails = alterationDao.findByApplicationNo(regServiceDTO.getApplicationNo());
				if (!alterDetails.isPresent()) {
					throw new BadRequestException(
							"No record found in alteration  for: " + regServiceDTO.getApplicationNo());
				}
				if (regServiceDTO.getRegistrationDetails().isTaxPaidByVcr()) {
					ispaisdThroughVCR = Boolean.TRUE;
					if (regServiceDTO.getTaxDetails() == null || regServiceDTO.getTaxDetails().getPaymentDAte() == null
							|| regServiceDTO.getTaxDetails().getCollectedAmount() == null) {
						throw new BadRequestException(
								"VCR tax details not found  for: " + regServiceDTO.getApplicationNo());
					}
					Pair<Integer, Integer> monthpositionInQuater = getMonthposition(
							regServiceDTO.getTaxDetails().getPaymentDAte());
					LocalDate taxUpTo = calculateChassisTaxUpTo(monthpositionInQuater.getFirst(),
							monthpositionInQuater.getSecond(), regServiceDTO.getTaxDetails().getPaymentDAte());
					if (taxUpTo.isBefore(currentTaxTill)) {
						lastTaxTillDate = addTaxDetails(TaxTypeEnum.QuarterlyTax.getDesc(),
								regServiceDTO.getTaxDetails().getCollectedAmount().doubleValue(), taxUpTo, Boolean.TRUE,
								regServiceDTO.getTaxDetails().getPaymentDAte().atStartOfDay());

					} else {
						lastTaxTillDate = addTaxDetails(TaxTypeEnum.QuarterlyTax.getDesc(),
								regServiceDTO.getTaxDetails().getCollectedAmount().doubleValue(), taxUpTo,
								Boolean.FALSE, regServiceDTO.getTaxDetails().getPaymentDAte().atStartOfDay());
					}
				}
			}
		} else {
			trNo = stagingRegistrationDetails.getTrNo();
			alterDetails = alterationDao.findByApplicationNo(stagingRegistrationDetails.getApplicationNo());
			if (!alterDetails.isPresent()) {
				throw new BadRequestException(
						"No record found in alteration  for: " + stagingRegistrationDetails.getApplicationNo());
			}
		}

		List<String> taxTypes = taxTypes();
		if (!ispaisdThroughVCR) {
			lastTaxTillDate = getLastPaidTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi, currentTaxTill,
					stagingRegistrationDetails, isChassesApplication, taxTypes, isOtherState);
		}

		if (lastTaxTillDate == null || lastTaxTillDate.getTax() == null || lastTaxTillDate.getValidityTo() == null) {
			throw new BadRequestException("TaxDetails not found");
		}
		if (lastTaxTillDate.isAnypendingQuaters()) {
			LocalDate lastTaxTill = lastTaxTillDate.getValidityTo();
			Pair<Integer, Integer> monthpositionInQuater = getMonthposition(alterDetails.get().getDateOfCompletion());
			Pair<Integer, Integer> currentMonthpositionInQuater = getMonthposition(LocalDate.now());
			// localDate = localDate.withDayOfMonth(localDate.getMonth().maxLength());;
			Double oldQuaterTax = getOldQuaterTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, isOtherState);
			Double penalityArrears = 0d;
			Double penalitys = 0d;
			Double oldCovPenalityArrears = 0d;
			Double oldCovTaxArr = 0d;
			Double lastTaxPaidPerMOnth = oldQuaterTax / 3;
			Double currentTaxPerMOnth = OptionalTax / 3;

			LocalDate bodyBuildFirstQuatreTaxUpTo = calculateChassisTaxUpTo(monthpositionInQuater.getFirst(),
					monthpositionInQuater.getSecond(), alterDetails.get().getDateOfCompletion());
			LocalDate chassisTaxUpTo = bodyBuildFirstQuatreTaxUpTo;
			if (!bodyBuildFirstQuatreTaxUpTo.equals(lastTaxTill)) {
				chassisTaxUpTo = bodyBuildFirstQuatreTaxUpTo.minusMonths(3);
			}
			chassisTaxUpTo = chassisTaxUpTo.withDayOfMonth(chassisTaxUpTo.getMonth().maxLength());

			totalMonths = ChronoUnit.MONTHS.between(lastTaxTill.withDayOfMonth(lastTaxTill.getMonth().maxLength()),
					chassisTaxUpTo);
			totalMonths = Math.abs(totalMonths);
			totalMonths = totalMonths + 1;
			double quaters = totalMonths / 3;
			LocalDate taxUpTo = calculateTaxUpTo(currentMonthpositionInQuater.getFirst(),
					currentMonthpositionInQuater.getSecond());
			Long newCovtotalMonths = ChronoUnit.MONTHS.between(chassisTaxUpTo, taxUpTo);
			newCovtotalMonths = newCovtotalMonths + 1;
			double newCovquaters = newCovtotalMonths / 3;

			if (quaters >= 1) {
				Pair<Double, Double> oldCovtaxArrAndPenality = chassisPenalitTax(oldQuaterTax, (quaters));
				oldCovTaxArr = oldCovtaxArrAndPenality.getFirst();
				oldCovPenalityArrears = oldCovtaxArrAndPenality.getSecond();
			}
			Pair<Double, Integer> totalException = Pair.of(0d, 0);
			if (monthpositionInQuater.getFirst() == 0) {

				if (currentMonthpositionInQuater.getFirst() == 0) {
					quaterTax = OptionalTax;
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					if (newCovquaters > 1) {
						taxArr = OptionalTax;
					}
				} else if (currentMonthpositionInQuater.getFirst() == 1) {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					penalitys = penalitys + ((OptionalTax) * 25) / 100;
					quaterTax = OptionalTax;
					if (newCovquaters > 1) {
						taxArr = OptionalTax;
					}

				} else {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					penalitys = penalitys + ((OptionalTax) * 50) / 100;
					quaterTax = OptionalTax;
					if (newCovquaters > 1) {
						taxArr = OptionalTax;
					}
				}
			} else if (monthpositionInQuater.getFirst() == 1) {

				if (currentMonthpositionInQuater.getFirst() == 0) {
					quaterTax = OptionalTax;
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = lastTaxPaidPerMOnth + (currentTaxPerMOnth * 2);
						}
					}
				} else if (currentMonthpositionInQuater.getFirst() == 1) {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);

					quaterTax = OptionalTax;
					if (currentMonthpositionInQuater.getSecond() == monthpositionInQuater.getSecond()
							&& alterDetails.get().getDateOfCompletion().getYear() == LocalDate.now().getYear()) {
						quaterTax = lastTaxPaidPerMOnth + (currentTaxPerMOnth * 2);
						;
					}
					penalitys = penalitys + ((quaterTax) * 25) / 100;
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = lastTaxPaidPerMOnth + (currentTaxPerMOnth * 2);
						}
					}
				} else {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);

					quaterTax = OptionalTax;
					if (currentMonthpositionInQuater.getSecond() == monthpositionInQuater.getSecond()
							&& alterDetails.get().getDateOfCompletion().getYear() == LocalDate.now().getYear()) {
						quaterTax = lastTaxPaidPerMOnth + (currentTaxPerMOnth * 2);
						;
					}
					penalitys = penalitys + ((quaterTax) * 50) / 100;
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = lastTaxPaidPerMOnth + (currentTaxPerMOnth * 2);
						}
					}
				}

			} else {

				if (currentMonthpositionInQuater.getFirst() == 0) {
					quaterTax = OptionalTax;
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = (lastTaxPaidPerMOnth * 2) + (currentTaxPerMOnth);
						}
					}
				} else if (currentMonthpositionInQuater.getFirst() == 1) {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					penalitys = penalitys + (OptionalTax * 25) / 100;
					quaterTax = OptionalTax;
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = (lastTaxPaidPerMOnth * 2) + (currentTaxPerMOnth);
						}
					}
				} else {
					totalException = chassExceptionTax(alterDetails, lastTaxTill, monthpositionInQuater, oldQuaterTax,
							lastTaxPaidPerMOnth);
					quaterTax = OptionalTax;
					if (currentMonthpositionInQuater.getSecond() == monthpositionInQuater.getSecond()
							&& alterDetails.get().getDateOfCompletion().getYear() == LocalDate.now().getYear()) {
						quaterTax = (lastTaxPaidPerMOnth * 2) + (currentTaxPerMOnth);
					}
					penalitys = penalitys + ((quaterTax) * 50) / 100;
					if (newCovquaters > 1) {
						if (totalException.getFirst() > 0) {
							taxArr = OptionalTax;
						} else {
							taxArr = (lastTaxPaidPerMOnth * 2) + (currentTaxPerMOnth);
						}
					}
				}

			}

			if (newCovquaters > 2) {
				Pair<Double, Double> taxArrAndPenality = chassisPenalitTax(OptionalTax, (newCovquaters - 2));
				taxArr = taxArr + taxArrAndPenality.getFirst();
				penalityArrears = penalityArrears + taxArrAndPenality.getSecond();
			}

			Double taxExceptionForChst = 0d;
			if (totalException.getFirst() > 0) {
				taxExceptionForChst = ((currentTaxPerMOnth * totalException.getSecond()) - totalException.getFirst());
				if (taxExceptionForChst < 0) {
					taxExceptionForChst = 0d;
				}
				// quaterTax = quaterTax - totalException.getFirst();
				// quaterTax = quaterTax + (currentTaxPerMOnth * totalException.getSecond());
			}
			TaxHelper currenTax = returnTaxDetails(quaterTax, taxArr + oldCovTaxArr + taxExceptionForChst,
					penalitys + newCovPenality, (taxArr / 2) + (oldCovTaxArr / 2), 0d);
			return this.overRideChassisTax(trNo, currenTax);

		} else {
			Pair<Integer, Integer> monthpositionInQuater = getMonthposition(alterDetails.get().getDateOfCompletion());
			LocalDate lastTaxTill = lastTaxTillDate.getValidityTo();
			if (lastTaxTillDate.getValidityTo().getMonthValue() == alterDetails.get().getDateOfCompletion()
					.getMonthValue()) {
				totalMonths = 1l;
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
			String date1 = "01-" + String.valueOf(alterDetails.get().getDateOfCompletion().getMonthValue()) + "-"
					+ String.valueOf(alterDetails.get().getDateOfCompletion().getYear());
			LocalDate localDate = LocalDate.parse(date1, formatter);
			/*
			 * LocalDate newDate =
			 * localDate.withDayOfMonth(localDate.getMonth().maxLength());
			 */

			if (totalMonths == null) {
				totalMonths = ChronoUnit.MONTHS.between(lastTaxTill.withDayOfMonth(lastTaxTill.getMonth().maxLength()),
						localDate);
				totalMonths = Math.abs(totalMonths);
				totalMonths = totalMonths + 1;
			}

			Double oldQuaterTax = getOldQuaterTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, isOtherState);

			Double lastTaxPaidPerMOnth = oldQuaterTax / 3 * 1;
			Double currentTaxPerMOnth = OptionalTax / 3 * 1;
			Double totalException = lastTaxPaidPerMOnth * totalMonths;
			if (monthpositionInQuater.getFirst() == 0) {
				quaterTax = (currentTaxPerMOnth * 3) - totalException;
			} else if (monthpositionInQuater.getFirst() == 1) {
				// penality =((oldQuaterTax * 25) / 100);
				quaterTax = (currentTaxPerMOnth * 2) - totalException;
				// quaterTax = quaterTax+oldQuaterTax;
			} else {
				// penality =((oldQuaterTax * 50) / 100);
				quaterTax = (currentTaxPerMOnth) - totalException;
				// quaterTax = quaterTax+oldQuaterTax;
			}
			// return Pair.of(quaterTax, 0d);
			return returnTaxDetails(quaterTax, 0d, 0d, 0d, 0d);

		}
	}

	public Pair<Double, Integer> chassExceptionTax(Optional<AlterationDTO> alterDetails, LocalDate lastTaxTill,
			Pair<Integer, Integer> monthpositionInQuater, Double oldQuaterTax, Double lastTaxPaidPerMOnth) {
		Double totalException = 0d;
		int months = 0;
		if (lastTaxTill.isAfter(alterDetails.get().getDateOfCompletion())) {
			if (monthpositionInQuater.getFirst() == 0) {
				totalException = oldQuaterTax;
				months = 3;
			} else if (monthpositionInQuater.getFirst() == 1) {
				totalException = lastTaxPaidPerMOnth * 2;
				months = 2;
			} else if (monthpositionInQuater.getFirst() == 2) {
				totalException = lastTaxPaidPerMOnth;
				months = 1;
			}
		}
		return Pair.of(totalException, months);
	}

	private TaxHelper returnTaxDetails(Double tax, Double taxArrears, Double penalty, Double penaltyArrears,
			Double currentTax) {
		TaxHelper taxHelper = new TaxHelper();

		taxHelper.setTax(tax);
		taxHelper.setTaxArrears(taxArrears);
		taxHelper.setPenalty(roundUpperTen(penalty));
		taxHelper.setPenaltyArrears(roundUpperTen(penaltyArrears));
		taxHelper.setQuaterAmount(currentTax);
		return taxHelper;
	}

	public Pair<Double, Double> chassisPenalitTax(Double oldQuaterTax, double quaters) {
		double penality = (((oldQuaterTax * 50) / 100) * (quaters));
		Double taxArr = (oldQuaterTax * (quaters));
		return Pair.of(taxArr, penality);
		// return taxArr;

	}

	private Double calculateOtherStateCurrentQuaterTax(Double OptionalTax,
			Pair<Integer, Integer> monthpositionInQuater) {
		Double penalityTax = 0d;
		if (monthpositionInQuater.getFirst() == 1) {
			penalityTax = ((OptionalTax * 25) / 100);
		} else if (monthpositionInQuater.getFirst() == 2) {

			penalityTax = ((OptionalTax * 50) / 100);
		}
		return penalityTax;
	}

	private TaxHelper getOtherStateTax(Double OptionalTax, RegistrationDetailsDTO stagingRegDetails,
			LocalDate currentTaxTill, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, boolean isOtherState) {
		LocalDate entryDate = null;
		if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
				.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
				|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
						.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
				&& regServiceDTO.isMviDone()) {
			return getChassistax(OptionalTax, stagingRegDetails, currentTaxTill, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, classOfvehicle, isOtherState);
		}
		if (regServiceDTO.getnOCDetails() != null) {
			entryDate = getEarlerDate(regServiceDTO.getnOCDetails().getDateOfEntry(),
					regServiceDTO.getnOCDetails().getIssueDate());
		} else {

			if (regServiceDTO.getOsDateofentry() != null) {
				entryDate = regServiceDTO.getOsDateofentry();
			} else {
				entryDate = LocalDate.now();
			}
		}

		Pair<Integer, Integer> entryMonthpositionInQuater = getMonthposition(entryDate);
		Pair<Integer, Integer> currentMonthpositionInQuater = getMonthposition(LocalDate.now());
		Double currentTaxPerMOnth = OptionalTax / 3;

		if (!(entryMonthpositionInQuater.getSecond().equals(currentMonthpositionInQuater.getSecond())
				&& entryDate.getYear() == LocalDate.now().getYear())) {
			Double taxArr = 0d;
			Pair<Double, Double> taxArr2AndPenality = null;
			Double penalityArr = 0d;
			// int monthsToSudtract=0;
			Long totalMonthsForPenality = 0l;

			LocalDate taxStartsFrom = calculateTaxFrom(entryMonthpositionInQuater.getFirst(),
					entryMonthpositionInQuater.getSecond());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
			String date1 = "01-" + String.valueOf(taxStartsFrom.getMonthValue()) + "-"
					+ String.valueOf(entryDate.getYear());
			LocalDate localDate = LocalDate.parse(date1, formatter);
			LocalDate taxEndDate = calculateTaxUpTo(currentMonthpositionInQuater.getFirst(),
					currentMonthpositionInQuater.getSecond());

			totalMonthsForPenality = ChronoUnit.MONTHS.between(localDate, taxEndDate);
			totalMonthsForPenality = Math.abs(totalMonthsForPenality);
			totalMonthsForPenality = totalMonthsForPenality + 1;
			double Penalityquaters = totalMonthsForPenality / 3d;

			if (entryMonthpositionInQuater.getFirst() == 0) {
				// penalityArr = penalityArr + (((OptionalTax * 50) / 100));
				taxArr = taxArr + (OptionalTax);
			} else if (entryMonthpositionInQuater.getFirst() == 1) {
				// OptionalTax = currentTaxPerMOnth * 2;
				// penalityArr = penalityArr + ((((currentTaxPerMOnth*2) * 50) / 100));
				taxArr = taxArr + (currentTaxPerMOnth * 2);
			} else {
				// OptionalTax = currentTaxPerMOnth;
				// penalityArr = penalityArr + (((currentTaxPerMOnth * 50) / 100));
				taxArr = taxArr + (currentTaxPerMOnth);
			}

			double noOfquaters = Penalityquaters - 2;
			if (noOfquaters > 0) {
				taxArr2AndPenality = chassisPenalitTax(OptionalTax, noOfquaters);
				taxArr = taxArr + taxArr2AndPenality.getFirst();
				penalityArr = penalityArr + taxArr2AndPenality.getSecond();
			}

			Double currentpenality = calculateOtherStateCurrentQuaterTax(OptionalTax, currentMonthpositionInQuater);
			return returnTaxDetails(OptionalTax, taxArr, currentpenality, penalityArr, 0d);

		} else {
			if (entryMonthpositionInQuater.getFirst() == 0) {
				// OptionalTax = OptionalTax;
			} else if (entryMonthpositionInQuater.getFirst() == 1) {
				OptionalTax = currentTaxPerMOnth * 2;
			} else {
				OptionalTax = currentTaxPerMOnth;
			}
			return returnTaxDetails(OptionalTax, 0d, 0d, 0d, 0d);
		}

	}

	public Pair<Integer, Integer> getMonthposition(LocalDate date) {

		List<Integer> quaterOne = new ArrayList<>();
		List<Integer> quaterTwo = new ArrayList<>();
		List<Integer> quaterThree = new ArrayList<>();
		List<Integer> quaterFour = new ArrayList<>();
		quaterOne.add(0, 4);
		quaterOne.add(1, 5);
		quaterOne.add(2, 6);
		quaterTwo.add(0, 7);
		quaterTwo.add(1, 8);
		quaterTwo.add(2, 9);
		quaterThree.add(0, 10);
		quaterThree.add(1, 11);
		quaterThree.add(2, 12);
		quaterFour.add(0, 1);
		quaterFour.add(1, 2);
		quaterFour.add(2, 3);
		Integer indexPosition = 0;
		Integer quaternNumber = 0;
		if (quaterOne.contains(date.getMonthValue())) {
			quaternNumber = 1;
			indexPosition = quaterOne.indexOf(date.getMonthValue());

		} else if (quaterTwo.contains(date.getMonthValue())) {
			quaternNumber = 2;
			indexPosition = quaterTwo.indexOf(date.getMonthValue());

		} else if (quaterThree.contains(date.getMonthValue())) {
			quaternNumber = 3;
			indexPosition = quaterThree.indexOf(date.getMonthValue());
		} else if (quaterFour.contains(date.getMonthValue())) {
			quaternNumber = 4;
			indexPosition = quaterFour.indexOf(date.getMonthValue());
		}
		return Pair.of(indexPosition, quaternNumber);
	}

	private TaxHelper currentQuaterTaxCalculation(Double OptionalTax, Integer valu,
			RegistrationDetailsDTO stagingRegDetails, LocalDate currentTaxTill, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, StagingRegistrationDetailsDTO stagingRegistrationDetails,
			boolean isChassesApplication, String classOfvehicle, boolean isOtherState, String taxType,
			List<ServiceEnum> serviceEnum, String permitTypeCode, String routeCode, Boolean isWeightAlt) {
		Double quaterTax = null;
		Double finalquaterTax = OptionalTax;
		Long totalMonths = null;
		Double penality = 0d;
		Double taxArrears = 0d;
		Double penaltyArrears = 0d;
		Double newPermitTax = 0d;
		List<String> taxTypes = taxTypes();
		Optional<MasterTaxExcemptionsDTO> excemptionPercentage = masterTaxExcemptionsDAO.findByKeyvalue(classOfvehicle);
		if (excemptionPercentage.isPresent()
				&& !excemptionPercentage.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {
			// check percentage discount
			double discount = (OptionalTax * excemptionPercentage.get().getTaxvalue()) / 100;
			OptionalTax = OptionalTax - discount;
		}
		if (isChassesApplication) {

			return getChassistax(OptionalTax, stagingRegDetails, currentTaxTill, regServiceDTO, isApplicationFromMvi,
					stagingRegistrationDetails, isChassesApplication, classOfvehicle, isOtherState);

		} else if (isOtherState) {
			if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {

				return getOtherStateTax(OptionalTax, stagingRegDetails, currentTaxTill, regServiceDTO,
						isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication, classOfvehicle,
						isOtherState);
			}
			if (!regServiceDTO.getRegistrationDetails().isTaxPaidByVcr()) {
				return getOtherStateTax(OptionalTax, stagingRegDetails, currentTaxTill, regServiceDTO,
						isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication, classOfvehicle,
						isOtherState);
			}

		} else if (isApplicationFromMvi) {
			return this.getOldTaxDetailsForAlterVehicle(regServiceDTO.getRegistrationDetails().getApplicationNo(),
					OptionalTax, valu, regServiceDTO, taxType, regServiceDTO.getRegistrationDetails().getPrNo());
			/*
			 * if (valu == 0) { quaterTax = OptionalTax; } else if (valu == 1) { quaterTax =
			 * (OptionalTax / 3) * 2; } else { quaterTax = (OptionalTax / 3) * 1; } return
			 * Pair.of(quaterTax, 0d);
			 */

		} else {
			TaxHelper lastTaxTillDate = getLastPaidTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi,
					currentTaxTill, stagingRegistrationDetails, isChassesApplication, taxTypes, isOtherState);
			if (lastTaxTillDate == null || lastTaxTillDate.getTax() == null
					|| lastTaxTillDate.getValidityTo() == null) {
				throw new BadRequestException("TaxDetails not found");
			}
			TaxHelper vcrDetails = vcrIntegration(stagingRegDetails);
			newPermitTax = getTaxForNewAndVariationPermitTax(stagingRegDetails, serviceEnum, permitTypeCode,
					newPermitTax, routeCode);
			double vehicleAge = 0d;
			if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.EIBT.getCovCode())) {
				vehicleAge = calculateAgeOfTheVehicle(stagingRegDetails.getRegistrationValidity().getPrGeneratedDate(),
						LocalDate.now());
			}
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO
					.findByCovsInAndObtTaxTrue(stagingRegDetails.getClassOfVehicle());
			if (propertiesOptional.isPresent()) {
				Pair<String, String> permitCodeAndRouytCode = this.getPermitCode(stagingRegDetails);
				if (permitCodeAndRouytCode.getFirst().equalsIgnoreCase("INA")) {
					OptionalTax = getOldCovTax(ClassOfVehicleEnum.OBT.getCovCode(),
							stagingRegDetails.getVahanDetails().getSeatingCapacity(),
							stagingRegDetails.getVahanDetails().getUnladenWeight(),
							stagingRegDetails.getVahanDetails().getGvw(), stateCode, "INA", null);
				}
			}
			if (isWeightAlt != null && isWeightAlt) {
				if (lastTaxTillDate.isAnypendingQuaters()) {
					throw new BadRequestException("Tax is pending. Please pay the tax. " + stagingRegDetails.getPrNo());
				}

				if (serviceEnum == null || serviceEnum.isEmpty()) {
					throw new BadRequestException("services type not found. " + stagingRegDetails.getPrNo());
				}
				/*
				 * if(!serviceEnum.stream().anyMatch(id->id.equals(ServiceEnum.
				 * ALTERATIONOFVEHICLE))) { throw new
				 * BadRequestException("For body type alteration please select service as alteratin service. "
				 * +stagingRegDetails.getPrNo()); }
				 */
				Integer gvw = stagingRegDetails.getVahanDetails().getGvw();
				if (stagingRegDetails.isWeightAltDone()) {
					gvw = stagingRegDetails.getVahanDetails().getOldGvw();
				}
				if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode())) {
					if (stagingRegDetails.getVahanDetails().getTrailerChassisDetailsDTO() != null
							&& !stagingRegDetails.getVahanDetails().getTrailerChassisDetailsDTO().isEmpty()) {

						Integer gtw = stagingRegDetails.getVahanDetails().getTrailerChassisDetailsDTO().stream()
								.findFirst().get().getGtw();
						for (TrailerChassisDetailsDTO trailerDetails : stagingRegDetails.getVahanDetails()
								.getTrailerChassisDetailsDTO()) {
							if (trailerDetails.getGtw() > gtw) {
								gtw = trailerDetails.getGtw();
							}
						}
						gvw = gvw + gtw;
					}
				}
				Optional<MasterWeightsForAlt> optionalWeigts = masterWeightsForAltDAO
						.findByToGvwGreaterThanEqualAndFromGvwLessThanEqualAndStatusIsTrue(gvw, gvw);
				if (!optionalWeigts.isPresent()) {
					throw new BadRequestException(
							"Vehicle not eligible to change weight: " + stagingRegDetails.getPrNo());
				}
				String classOfVehicle = stagingRegDetails.getClassOfVehicle();
				if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.EIBT.getCovCode())
						&& vehicleAge > 15) {
					classOfVehicle = ClassOfVehicleEnum.OBT.getCovCode();
					Double oldtax = getOldCovTax(classOfVehicle,
							stagingRegDetails.getVahanDetails().getSeatingCapacity(),
							stagingRegDetails.getVahanDetails().getUnladenWeight(),
							stagingRegDetails.getVahanDetails().getGvw(), stateCode, permitcode, routeCode);
					OptionalTax = oldtax;
				}
				Double obtTax = getOldCovTax(classOfVehicle, stagingRegDetails.getVahanDetails().getSeatingCapacity(),
						stagingRegDetails.getVahanDetails().getUnladenWeight(), optionalWeigts.get().getGvw(),
						stateCode, permitcode, routeCode);
				newPermitTax = obtTax;
				// OptionalTax = obtTax;
				Pair<Integer, Integer> monthpositionInQuater = getMonthposition(LocalDate.now());
				double taxPerMonthWithNewWeight = obtTax / 3d;
				double taxPerMonthWithOldWeight = OptionalTax / 3d;
				Long newCovtotalMonths = 0l;
				if (!lastTaxTillDate.getValidityTo().equals(currentTaxTill)) {
					newCovtotalMonths = ChronoUnit.MONTHS.between(currentTaxTill, lastTaxTillDate.getValidityTo());
				}
				if (monthpositionInQuater.getFirst() == 0) {
					quaterTax = obtTax - OptionalTax;
					if (newCovtotalMonths != 0) {
						quaterTax = quaterTax + ((taxPerMonthWithNewWeight * newCovtotalMonths)
								- (taxPerMonthWithOldWeight * newCovtotalMonths));
					}

				} else if (monthpositionInQuater.getFirst() == 1) {
					quaterTax = (taxPerMonthWithNewWeight * 2) - (taxPerMonthWithOldWeight * 2);
					if (newCovtotalMonths != 0) {
						quaterTax = quaterTax + ((taxPerMonthWithNewWeight * newCovtotalMonths)
								- (taxPerMonthWithOldWeight * newCovtotalMonths));
					}

				} else {
					quaterTax = (taxPerMonthWithNewWeight) - (taxPerMonthWithOldWeight);
					if (newCovtotalMonths != 0) {
						quaterTax = quaterTax + ((taxPerMonthWithNewWeight * newCovtotalMonths)
								- (taxPerMonthWithOldWeight * newCovtotalMonths));
					}
				}

			} else if (stagingRegDetails.isVehicleStoppageRevoked()) {
				if (stagingRegDetails.getVehicleStoppageRevokedDate() == null) {
					throw new BadRequestException(
							"Vheicle stoppage revokation date not found: " + stagingRegDetails.getPrNo());
				}

				TaxHelper revokedTax = vehicleRevokationTaxCalculation(stagingRegDetails, lastTaxTillDate, OptionalTax);
				quaterTax = revokedTax.getTax();
				taxArrears = revokedTax.getTaxArrears();
				penality = revokedTax.getPenalty().doubleValue();
				penaltyArrears = revokedTax.getPenaltyArrears().doubleValue();
				newPermitTax = revokedTax.getQuaterAmount();
			} else if (((stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.ARKT.getCovCode())
					&& Integer.parseInt(stagingRegDetails.getVahanDetails().getSeatingCapacity()) <= 4)
					|| ((stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.TGVT.getCovCode())
							|| stagingRegDetails.getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.GCRT.getCovCode()))
							&& stagingRegDetails.getVahanDetails().getGvw() <= 3000))
					&& taxType.equalsIgnoreCase(TaxTypeEnum.LifeTax.getDesc())) {

				totalMonths = ChronoUnit.MONTHS.between(lastTaxTillDate.getValidityTo(), currentTaxTill);
				totalMonths = Math.abs(totalMonths);
				totalMonths = totalMonths + 1;
				double totalQuaters = totalMonths / 3;
				double penalityArrearsQuaters = totalQuaters - 1;
				penaltyArrears = (((OptionalTax * 50) / 100) * penalityArrearsQuaters);
				taxArrears = (OptionalTax * penalityArrearsQuaters);
				Pair<Integer, Integer> monthpositionInQuater = getMonthposition(LocalDate.now());
				double taxPerMonth = OptionalTax / 3d;
				if (monthpositionInQuater.getFirst() == 0) {
					quaterTax = 0d;
				} else if (monthpositionInQuater.getFirst() == 1) {
					penality = (taxPerMonth * 25) / 100;
					quaterTax = taxPerMonth;

				} else {
					penality = ((taxPerMonth * 2) * 50) / 100;
					quaterTax = taxPerMonth * 2;
				}

			} else {
				if (lastTaxTillDate.isAnypendingQuaters()) {
					if (stagingRegDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.EIBT.getCovCode())
							&& vehicleAge > 15) {
						Double obtTax = getOldCovTax(ClassOfVehicleEnum.OBT.getCovCode(),
								stagingRegDetails.getVahanDetails().getSeatingCapacity(),
								stagingRegDetails.getVahanDetails().getUnladenWeight(),
								stagingRegDetails.getVahanDetails().getGvw(), stateCode, permitcode, routeCode);
						Pair<Double, Double> quaterTaxAndPenality = getpendingQuaters(currentTaxTill,
								lastTaxTillDate.getValidityTo(), OptionalTax, finalquaterTax, quaterTax, vcrDetails, 0d,
								0d, true, stagingRegDetails.getRegistrationValidity().getPrGeneratedDate(), obtTax);
						taxArrears = quaterTaxAndPenality.getFirst();
						penaltyArrears = quaterTaxAndPenality.getSecond();
						OptionalTax = obtTax;
						newPermitTax = obtTax;
					} else {
						Pair<Double, Double> quaterTaxAndPenality = getpendingQuaters(currentTaxTill,
								lastTaxTillDate.getValidityTo(), OptionalTax, finalquaterTax, quaterTax, vcrDetails, 0d,
								0d, false, stagingRegDetails.getRegistrationValidity().getPrGeneratedDate(),
								OptionalTax);
						taxArrears = taxArrears + quaterTaxAndPenality.getFirst();
						penaltyArrears = penaltyArrears + quaterTaxAndPenality.getSecond();
					}
					Pair<Double, Double> pairOfTax = getcurrenttaxWithPenality(vcrDetails, newPermitTax, quaterTax,
							penality, OptionalTax, valu);
					quaterTax = pairOfTax.getFirst();
					penality = pairOfTax.getSecond();
				} else {
					Pair<Double, Double> pairOfTax = getcurrenttaxWithOutPenality(vcrDetails, newPermitTax,
							finalquaterTax, penality, OptionalTax, valu);
					quaterTax = pairOfTax.getFirst();
					penality = pairOfTax.getSecond();
				}
			}
			if ((quaterTax != null && quaterTax < 1) || quaterTax == null) {
				quaterTax = 0d;
			}
			TaxHelper currenTax = returnTaxDetails(quaterTax, taxArrears, penality, penaltyArrears, newPermitTax);
			// if (lastTaxTillDate.isAnypendingQuaters()) {
			currenTax = this.overRideTheTax(OptionalTax, valu, stagingRegDetails, currentTaxTill, regServiceDTO,
					isApplicationFromMvi, stagingRegistrationDetails, isChassesApplication, classOfvehicle,
					isOtherState, taxType, serviceEnum, permitTypeCode, routeCode, lastTaxTillDate, vcrDetails,
					currenTax);
			// }
			return currenTax;
		}

		if ((quaterTax != null && quaterTax < 1) || quaterTax == null) {
			quaterTax = 0d;
		}

		return returnTaxDetails(quaterTax, taxArrears, penality, penaltyArrears, newPermitTax);

	}

	private TaxHelper vehicleRevokationTaxCalculation(RegistrationDetailsDTO stagingRegDetails,
			TaxHelper lastTaxTillDate, Double quaterTax) {
		Double penality = 0d;
		Double taxArrears = 0d;
		Double penaltyArrears = 0d;
		Pair<Integer, Integer> revokedMonthpositionInQuater = getMonthposition(
				stagingRegDetails.getVehicleStoppageRevokedDate());
		LocalDate firstQuaterUpTo = calculateTaxUpTo(revokedMonthpositionInQuater.getFirst(),
				revokedMonthpositionInQuater.getSecond());
		Pair<Integer, Integer> currentMonthpositionInQuater = getMonthposition(LocalDate.now());
		LocalDate currentQuaterUpTo = calculateTaxUpTo(currentMonthpositionInQuater.getFirst(),
				currentMonthpositionInQuater.getSecond());
		double taxPerMonth = quaterTax / 3d;
		if ((revokedMonthpositionInQuater.getSecond().equals(currentMonthpositionInQuater.getSecond())
				&& stagingRegDetails.getVehicleStoppageRevokedDate().getYear() == LocalDate.now().getYear())) {
			if (revokedMonthpositionInQuater.getFirst() == 0) {
				if (currentMonthpositionInQuater.getFirst() == 1) {
					penality = (quaterTax * 25) / 100;
				} else if (currentMonthpositionInQuater.getFirst() == 2) {
					penality = (quaterTax * 50) / 100;
				}
			} else if (revokedMonthpositionInQuater.getFirst() == 1) {
				quaterTax = taxPerMonth * 2;
				if (currentMonthpositionInQuater.getFirst() == 2) {
					penality = (quaterTax * 25) / 100;
				}
			} else {
				quaterTax = taxPerMonth;
			}
		} else {
			// not in same quater
			if (revokedMonthpositionInQuater.getFirst() == 0) {
				penaltyArrears = (quaterTax * 50) / 100;
				taxArrears = quaterTax;
			} else if (revokedMonthpositionInQuater.getFirst() == 1) {
				penaltyArrears = ((taxPerMonth * 2) * 50) / 100;
				taxArrears = taxPerMonth * 2;
			} else {
				penaltyArrears = (taxPerMonth * 50) / 100;
				taxArrears = taxPerMonth;
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
			String date1 = firstQuaterUpTo.getDayOfMonth() + "-" + firstQuaterUpTo.getMonthValue() + "-"
					+ stagingRegDetails.getVehicleStoppageRevokedDate().getYear();
			LocalDate firtQuaterValidity = LocalDate.parse(date1, formatter);
			Long totalMonthsForPenality = ChronoUnit.MONTHS.between(firtQuaterValidity, currentQuaterUpTo);
			totalMonthsForPenality = Math.abs(totalMonthsForPenality);
			totalMonthsForPenality = totalMonthsForPenality + 1;
			double Penalityquaters = totalMonthsForPenality / 3d;
			double PenalityQuatersRound = Math.ceil(totalMonthsForPenality / 3d);
			if (Penalityquaters == PenalityQuatersRound) {
				Pair<Double, Double> taxArr2AndPenality = chassisPenalitTax(quaterTax, (Penalityquaters - 1));
				taxArrears = taxArrears + taxArr2AndPenality.getFirst();
				penaltyArrears = penaltyArrears + taxArr2AndPenality.getSecond();
			}
			if (currentMonthpositionInQuater.getFirst() == 1) {
				penality = ((quaterTax * 25) / 100);
			} else if (currentMonthpositionInQuater.getFirst() == 2) {

				penality = ((quaterTax * 50) / 100);
			}
		}
		quaterTax = quaterTax - (taxPerMonth * stagingRegDetails.getTaxExemMonths());
		return returnTaxDetails(quaterTax, taxArrears, penality, penaltyArrears, 0d);

	}

	private Pair<Double, Double> getcurrenttaxWithPenality(TaxHelper vcrDetails, Double newPermitTax, Double quaterTax,
			Double penality, Double OptionalTax, Integer valu) {
		if (!vcrDetails.isVcr()) {
			// With out VCR
			if (valu == 0) {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 3, 0);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else if (valu == 1) {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 2, 25);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 1, 50);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			}
		} else {
			// With VCR
			if (valu == 0) {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 3, 0);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else if (valu == 1) {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 2, 100);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else {
				Pair<Double, Double> pairOfTax = getTaxWithVcr(quaterTax, penality, OptionalTax, newPermitTax, 1, 200);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			}
		}
		return Pair.of(quaterTax, penality);
	}

	private Pair<Double, Double> getTaxWithVcr(Double quaterTax, Double penality, Double OptionalTax,
			Double newPermitTax, int monthPosition, int percent) {
		quaterTax = getTaxWithNewPermit(OptionalTax, newPermitTax, monthPosition);
		penality = ((OptionalTax * percent) / 100);
		// quaterTax = OptionalTax;
		return Pair.of(quaterTax, penality);
	}

	private Pair<Double, Double> getTaxWithVcrWithOutPenality(Double quaterTax, Double penality, Double OptionalTax,
			Double newPermitTax, int monthPosition, int percent) {
		quaterTax = getTaxWithNewPermitWitOutPenality(OptionalTax, newPermitTax, monthPosition);
		penality = 0d;
		// quaterTax = OptionalTax;
		return Pair.of(quaterTax, penality);
	}

	private Double getTaxForNewAndVariationPermitTax(RegistrationDetailsDTO stagingRegDetails,
			List<ServiceEnum> serviceEnum, String permitTypeCode, Double newPermitTax, String routeCode) {
		Pair<String, String> permitAndRout = getPermitCode(stagingRegDetails);
		String permitType = permitTypeCode;
		String routeCodes = permitAndRout.getSecond();
		if (serviceEnum != null && !serviceEnum.isEmpty()
				&& serviceEnum.stream()
						.anyMatch(service -> Arrays.asList(ServiceEnum.NEWPERMIT, ServiceEnum.VARIATIONOFPERMIT)
								.stream().anyMatch(serviceName -> serviceName.equals(service)))) {

			if ((stagingRegDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.COCT.getCovCode())
					|| stagingRegDetails.getClassOfVehicle().equals(ClassOfVehicleEnum.TOVT.getCovCode()))
					&& StringUtils.isBlank(routeCode)) {
				throw new BadRequestException("rout code not found for pr: " + stagingRegDetails.getPrNo());
			}
			routeCodes = routeCode;
			boolean isBelongToPermitService = false;
			isBelongToPermitService = getAmethodToGetPemritService(serviceEnum);
			if (isBelongToPermitService) {
				Optional<PropertiesDTO> propertiesOptional = propertiesDAO
						.findByCovsInAndPermitCodeTrue(stagingRegDetails.getClassOfVehicle());
				if (!propertiesOptional.isPresent()) {
					permitTypeCode = permitcode;
					permitType = permitcode;
				}
			}

			Integer gvw = getGvwWeightForCitizen(stagingRegDetails);
			newPermitTax = getOldCovTax(stagingRegDetails.getClassOfVehicle(),
					stagingRegDetails.getVahanDetails().getSeatingCapacity(),
					stagingRegDetails.getVahanDetails().getUnladenWeight(), gvw, stateCode, permitType, routeCode);

		}
		return newPermitTax;
	}

	private TaxHelper vcrIntegration(RegistrationDetailsDTO stagingRegDetails) {
		TaxHelper taxHelper = new TaxHelper();
		boolean vcrFlag = Boolean.FALSE;
		/*
		 * VcrInputVo vcrInputVo = new VcrInputVo(); vcrInputVo.setDocumentType("RC");
		 * vcrInputVo.setRegNo(stagingRegDetails.getPrNo()); VcrBookingData entity =
		 * restGateWayService.getVcrDetailsCfst(vcrInputVo); if (entity != null) { if
		 * (entity.getVcrStatus().equalsIgnoreCase("O")) { vcrFlag = Boolean.TRUE; }
		 * //TODO need to check vehicle sized. }
		 */
		taxHelper.setVcr(vcrFlag);
		return taxHelper;
	}

	private Double getTaxWithNewPermit(Double OptionalTax, Double newPermitTax, int noOfMonths) {
		Double quaterTax;
		double oldTaxPerMonth = 0d;
		double newTaxPerMonth = 0d;
		quaterTax = OptionalTax;
		if (newPermitTax != null && newPermitTax != 0) {
			oldTaxPerMonth = OptionalTax / 3;
			newTaxPerMonth = newPermitTax / 3;
			if (noOfMonths == 1) {
				quaterTax = (oldTaxPerMonth * 2) + (newTaxPerMonth * 1);
			} else if (noOfMonths == 2) {
				quaterTax = (oldTaxPerMonth * 1) + (newTaxPerMonth * 2);
			} else {
				quaterTax = (oldTaxPerMonth * 0) + (newTaxPerMonth * 3);
			}
			// quaterTax = (oldTaxPerMonth * noOfMonths) - (newTaxPerMonth * noOfMonths);
		}
		return quaterTax;
	}

	private Double getTaxWithNewPermitWitOutPenality(Double OptionalTax, Double newPermitTax, int noOfMonths) {
		Double quaterTax;
		double oldTaxPerMonth = 0d;
		double newTaxPerMonth = 0d;
		quaterTax = OptionalTax;
		if (newPermitTax != null && newPermitTax != 0) {
			oldTaxPerMonth = OptionalTax / 3d;
			newTaxPerMonth = newPermitTax / 3d;
			quaterTax = (newTaxPerMonth * noOfMonths) - (oldTaxPerMonth * noOfMonths);
			return quaterTax;
		}
		return 0d;
	}

	private Pair<Long, LocalDate> cessTaxCalculation(Double OptionalTax, RegistrationDetailsDTO stagingRegDetails,
			LocalDate currentTaxTill, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, boolean isOtherState) {
		List<Integer> quaterFour = new ArrayList<>();
		LocalDate endDate = null;
		quaterFour.add(0, 1);
		quaterFour.add(1, 2);
		quaterFour.add(2, 3);
		if (isOtherState) {
			return Pair.of(0l, LocalDate.now());
		}

		TaxHelper lastTax = getLastPaidTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi,
				validity(ServiceCodeEnum.CESS_FEE.getCode()), stagingRegistrationDetails, isChassesApplication,
				taxTypes(), isOtherState);
		if ((lastTax != null || lastTax.getValidityTo() != null)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "31-03-2019";
			LocalDate cessEndDate = LocalDate.parse(date1, formatter);
			String date2 = "30-06-2018";
			LocalDate taxValidity = LocalDate.parse(date2, formatter);
			if ((lastTax.getValidityTo().equals(cessEndDate) || lastTax.getValidityTo().isBefore(cessEndDate))
					&& (taxValidity.equals(lastTax.getValidityTo()) || taxValidity.isBefore(lastTax.getValidityTo()))) {
				return Pair.of(0l, LocalDate.now());
			}
		}
		if (quaterFour.contains(LocalDate.now().getMonthValue())) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "31-03-" + String.valueOf(LocalDate.now().getYear());
			endDate = LocalDate.parse(date1, formatter);
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "31-03-" + String.valueOf(LocalDate.now().plusYears(1).getYear());
			endDate = LocalDate.parse(date1, formatter);
		}
		TaxHelper lastTaxTillDate = getLastPaidTax(stagingRegDetails, regServiceDTO, isApplicationFromMvi, endDate,
				stagingRegistrationDetails, isChassesApplication, Arrays.asList(ServiceCodeEnum.CESS_FEE.getCode()),
				isOtherState);
		/*
		 * if ((lastTaxTillDate == null || lastTaxTillDate.getAmount() == null)&&
		 * !isApplicationFromMvi) { throw new
		 * BadRequestException("TaxDetails not found"); }
		 */
		if ((lastTaxTillDate == null || lastTaxTillDate.getTax() == null)) {
			Pair<Long, LocalDate> cessfeesAndValidity = getCesFee(OptionalTax, classOfvehicle, null, classOfvehicle);
			return cessfeesAndValidity;
		}
		if (lastTaxTillDate.isAnypendingQuaters()) {
			// cal cess
			String applicatioNo = StringUtils.EMPTY;
			if (isApplicationFromMvi) {
				applicatioNo = regServiceDTO.getApplicationNo();
			} else {
				applicatioNo = stagingRegistrationDetails != null ? stagingRegistrationDetails.getApplicationNo()
						: stagingRegDetails.getApplicationNo();
			}
			// LocalDate lastTaxDate = getCessDetails(lastTaxTillDate, applicatioNo);
			Pair<Long, LocalDate> cessfeesAndValidity = getCesFee(OptionalTax, classOfvehicle, null, classOfvehicle);
			return cessfeesAndValidity;
		} else {
			return Pair.of(0l, LocalDate.now());
		}

	}

	private Pair<Double, Double> getpendingQuaters(LocalDate currentQuater, LocalDate lastTaxTillDate,
			Double OptionalTax, Double finalquaterTax, Double quaterTax, TaxHelper vcrDetails,
			Double excemtionpenalityArrears, Double excemtionTaxArrears, boolean isEibtVehicle,
			LocalDate prGeneratedDate, Double obtOptionalTax) {
		Double penality = 0d;
		Long totalMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, currentQuater);
		totalMonths = Math.abs(totalMonths);
		totalMonths = totalMonths + 1;
		double TotalQuaters = totalMonths / 3;
		TotalQuaters = Math.ceil(TotalQuaters);
		double penalityArrearsQuaters = TotalQuaters - 1;
		double taxArrearsQuaters = TotalQuaters - 1;
		if (excemtionpenalityArrears != 0) {
			penalityArrearsQuaters = penalityArrearsQuaters - excemtionpenalityArrears.intValue();
		}
		if (excemtionTaxArrears != 0) {
			taxArrearsQuaters = taxArrearsQuaters - excemtionTaxArrears.intValue();
		}
		if (isEibtVehicle) {
			return getPenalityForEibtAgeIsGreaterThan15(vcrDetails.isVcr(), vcrDetails.isVehicleSized(),
					vcrDetails.getSizedDate(), prGeneratedDate, lastTaxTillDate, currentQuater, OptionalTax,
					obtOptionalTax);
		}
		if (vcrDetails.isVcr()) {
			// TODO need to check suspend
			if (vcrDetails.isVehicleSized()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
				String date1 = "01-" + String.valueOf(vcrDetails.getSizedDate().getMonthValue()) + "-"
						+ String.valueOf(vcrDetails.getSizedDate().getYear());
				LocalDate localDate = LocalDate.parse(date1, formatter);
				Long sizedMonths = ChronoUnit.MONTHS.between(localDate, LocalDate.now());
				Long totalUnSizedMonths = sizedMonths - (totalMonths - 3);
				double quaters = totalUnSizedMonths / 3d;
				double quatersRound = Math.ceil(totalUnSizedMonths / 3);
				if (quaters == quatersRound) {
					penality = (((OptionalTax * 200) / 100) * quaters);
					finalquaterTax = (OptionalTax * quaters);
				} else {
					String numberD = String.valueOf(quaters);
					String number = numberD.substring(numberD.indexOf("."));
					String num = number.substring(1, 4);
					String val = numberD.substring(0, numberD.indexOf("."));
					Double taxPerMonth = OptionalTax / 3d;
					if (num.equalsIgnoreCase("666")) {
						penality = (((OptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (OptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 2) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 2);
					} else {
						penality = (((OptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (OptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 1) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 1);
					}
				}

			} else {
				penality = (((OptionalTax * 200) / 100) * penalityArrearsQuaters);
				finalquaterTax = (OptionalTax * taxArrearsQuaters);
			}
		} else {

			penality = (((OptionalTax * 50) / 100) * penalityArrearsQuaters);
			finalquaterTax = (OptionalTax * taxArrearsQuaters);
		}
		return Pair.of(finalquaterTax, penality);
	}

	private Pair<Double, Double> getPenalityForEibtAgeIsGreaterThan15(Boolean isVehicleSized, Boolean isVehicleHaveVcr,
			LocalDate vehicleSizedDate, LocalDate prGeneratedDate, LocalDate lastTaxTillDate, LocalDate currentQuater,
			Double eibtOptionalTax, Double obtOptionalTax) {
		LocalDate dateOfTheVehicle = prGeneratedDate.plusYears(15);

		Long obtMonths = 0l;
		Long eibtMonths = 0l;
		Double penality = 0d;
		Double finalquaterTax = 0d;
		if (lastTaxTillDate.isBefore(dateOfTheVehicle)) {
			eibtMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, dateOfTheVehicle);
			obtMonths = ChronoUnit.MONTHS.between(dateOfTheVehicle, currentQuater);
		} else {
			obtMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, currentQuater);
		}
		eibtMonths = Math.abs(eibtMonths);
		obtMonths = Math.abs(obtMonths);
		obtMonths = obtMonths + 1;
		double obtTotalQuaters = obtMonths / 3d;
		double obttTotalquatersRound = Math.ceil(obtMonths / 3);

		double eibtTotalQuaters = eibtMonths / 3d;
		double eibtTotalquatersRound = Math.ceil(eibtMonths / 3);

		if (isVehicleSized) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-" + String.valueOf(vehicleSizedDate.getMonthValue()) + "-"
					+ String.valueOf(vehicleSizedDate.getYear());
			LocalDate localDate = LocalDate.parse(date1, formatter);
			if (lastTaxTillDate.isBefore(dateOfTheVehicle)) {
				if (dateOfTheVehicle.isBefore(localDate)) {
					eibtMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, dateOfTheVehicle);
					obtMonths = ChronoUnit.MONTHS.between(dateOfTheVehicle, localDate);
				} else {
					obtMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, localDate);
				}
			} else {
				obtMonths = ChronoUnit.MONTHS.between(lastTaxTillDate, localDate);
			}

			if (eibtMonths != 0 && eibtMonths > 0) {
				double eibtquaters = eibtMonths / 3d;
				double eibtquatersRound = Math.ceil(eibtMonths / 3);
				if (eibtquaters == eibtquatersRound) {
					penality = (((eibtOptionalTax * 200) / 100) * eibtquaters);
					finalquaterTax = (eibtOptionalTax * eibtquaters);
				} else {
					String numberD = String.valueOf(eibtquaters);
					String number = numberD.substring(numberD.indexOf("."));
					String num = number.substring(1, 4);
					String val = numberD.substring(0, numberD.indexOf("."));
					Double taxPerMonth = eibtOptionalTax / 3d;
					if (num.equalsIgnoreCase("666")) {
						penality = (((eibtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 2) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 2);
					} else {
						penality = (((eibtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 1) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 1);
					}
				}
			}
			if (obtMonths != 0 && obtMonths > 0) {
				double obttquaters = obtMonths / 3d;
				double obttquatersRound = Math.ceil(obtMonths / 3);
				if (obttquaters == obttquatersRound) {
					penality = penality + (((obtOptionalTax * 200) / 100) * obttquaters);
					finalquaterTax = finalquaterTax + (obtOptionalTax * obttquaters);
				} else {
					String numberD = String.valueOf(obttquaters);
					String number = numberD.substring(numberD.indexOf("."));
					String num = number.substring(1, 4);
					String val = numberD.substring(0, numberD.indexOf("."));
					Double taxPerMonth = obtOptionalTax / 3d;
					if (num.equalsIgnoreCase("666")) {
						penality = (((obtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (obtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 2) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 2);
					} else {
						penality = (((obtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (obtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 1) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 1);
					}
				}
			}
		} else {
			if (isVehicleHaveVcr) {
				if (eibtTotalQuaters == eibtTotalquatersRound) {
					penality = (((eibtOptionalTax * 200) / 100) * eibtTotalQuaters);
					finalquaterTax = (eibtOptionalTax * eibtTotalQuaters);
				} else {
					String numberD = String.valueOf(eibtTotalQuaters);
					String number = numberD.substring(numberD.indexOf("."));
					String num = number.substring(1, 4);
					String val = numberD.substring(0, numberD.indexOf("."));
					Double taxPerMonth = eibtOptionalTax / 3d;
					if (num.equalsIgnoreCase("666")) {
						penality = (((eibtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 2) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 2);
					} else {
						penality = (((eibtOptionalTax * 200) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 1) * 200 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 1);
					}
				}

				if (obtMonths != 0 && obtMonths > 0) {

					if (obttTotalquatersRound == obtTotalQuaters) {
						penality = penality + (((obtOptionalTax * 200) / 100) * obtTotalQuaters);
						finalquaterTax = finalquaterTax + (obtOptionalTax * obtTotalQuaters);
					} else {
						String numberD = String.valueOf(obtTotalQuaters);
						String number = numberD.substring(numberD.indexOf("."));
						String num = number.substring(1, 4);
						String val = numberD.substring(0, numberD.indexOf("."));
						Double taxPerMonth = obtOptionalTax / 3d;
						if (num.equalsIgnoreCase("666")) {
							penality = (((obtOptionalTax * 200) / 100) * Double.valueOf(val));
							finalquaterTax = (obtOptionalTax * Double.valueOf(val));
							penality = penality + ((taxPerMonth * 2) * 200 / 100);
							finalquaterTax = finalquaterTax + (taxPerMonth * 2);
						} else {
							penality = (((obtOptionalTax * 200) / 100) * Double.valueOf(val));
							finalquaterTax = (obtOptionalTax * Double.valueOf(val));
							penality = penality + ((taxPerMonth * 1) * 200 / 100);
							finalquaterTax = finalquaterTax + (taxPerMonth * 1);
						}
					}
				}
			} else {
				if (eibtTotalQuaters == eibtTotalquatersRound) {
					penality = (((eibtOptionalTax * 50) / 100) * eibtTotalQuaters);
					finalquaterTax = (eibtOptionalTax * eibtTotalQuaters);
				} else {
					String numberD = String.valueOf(eibtTotalQuaters);
					String number = numberD.substring(numberD.indexOf("."));
					String num = number.substring(1, 4);
					String val = numberD.substring(0, numberD.indexOf("."));
					Double taxPerMonth = eibtOptionalTax / 3d;
					if (num.equalsIgnoreCase("666")) {
						penality = (((eibtOptionalTax * 50) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 2) * 50 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 2);
					} else {
						penality = (((eibtOptionalTax * 50) / 100) * Double.valueOf(val));
						finalquaterTax = (eibtOptionalTax * Double.valueOf(val));
						penality = penality + ((taxPerMonth * 1) * 50 / 100);
						finalquaterTax = finalquaterTax + (taxPerMonth * 1);
					}
				}

				if (obtMonths != 0 && obtMonths > 0) {

					if (obttTotalquatersRound == obtTotalQuaters) {
						penality = penality + (((obtOptionalTax * 50) / 100) * obtTotalQuaters);
						finalquaterTax = finalquaterTax + (obtOptionalTax * obtTotalQuaters);
					} else {
						String numberD = String.valueOf(obtTotalQuaters);
						String number = numberD.substring(numberD.indexOf("."));
						String num = number.substring(1, 4);
						String val = numberD.substring(0, numberD.indexOf("."));
						Double taxPerMonth = obtOptionalTax / 3d;
						if (num.equalsIgnoreCase("666")) {
							penality = (((obtOptionalTax * 50) / 100) * Double.valueOf(val));
							finalquaterTax = (obtOptionalTax * Double.valueOf(val));
							penality = penality + ((taxPerMonth * 2) * 50 / 100);
							finalquaterTax = finalquaterTax + (taxPerMonth * 2);
						} else {
							penality = (((obtOptionalTax * 50) / 100) * Double.valueOf(val));
							finalquaterTax = (obtOptionalTax * Double.valueOf(val));
							penality = penality + ((taxPerMonth * 1) * 50 / 100);
							finalquaterTax = finalquaterTax + (taxPerMonth * 1);
						}
					}
				}
			}

		}
		return Pair.of(finalquaterTax, penality);
	}

	@Override
	public TaxHelper getLastPaidTax(RegistrationDetailsDTO stagingRegDetails, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, LocalDate currentTaxTill,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			List<String> taxTypes, boolean isOtherState) {
		if (stagingRegDetails != null) {
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
					.findByKeyvalue(stagingRegDetails.getVahanDetails().getMakersModel());
			if (optionalTaxExcemption.isPresent()) {
				return this.addTaxDetails(TaxTypeEnum.QuarterlyTax.getDesc(), 0d,
						validity(TaxTypeEnum.QuarterlyTax.getDesc()), Boolean.FALSE, LocalDateTime.now());
			}
		}

		String applicationNo = StringUtils.EMPTY;
		if (isApplicationFromMvi) {
			applicationNo = regServiceDTO.getApplicationNo();
		} else if (isOtherState) {
			if (regServiceDTO != null
					&& (regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
							|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				applicationNo = regServiceDTO.getApplicationNo();
			}

		} else {
			applicationNo = stagingRegDetails != null ? stagingRegDetails.getApplicationNo()
					: stagingRegistrationDetails.getApplicationNo();
		}
		List<TaxDetailsDTO> listOfTax = getTaxDetails(stagingRegDetails, taxTypes, regServiceDTO, isApplicationFromMvi,
				stagingRegistrationDetails, isChassesApplication, isOtherState);
		if (listOfTax.isEmpty() && (!taxTypes.contains(ServiceCodeEnum.CESS_FEE.getCode()))) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		if (listOfTax.isEmpty()) {
			return this.addTaxDetails(null, null, null, Boolean.FALSE, null);
		}
		listOfTax.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		TaxDetailsDTO dto = listOfTax.stream().findFirst().get();
		if (dto.getTaxDetails() == null) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		for (Map<String, TaxComponentDTO> map : dto.getTaxDetails()) {

			for (Entry<String, TaxComponentDTO> entry : map.entrySet()) {
				if (taxTypes.stream().anyMatch(key -> key.equalsIgnoreCase(entry.getKey()))) {
					if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
						if (entry.getValue().getValidityTo() == null) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());

						} else if (entry.getValue().getValidityTo().isBefore(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());
						} else {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.FALSE, entry.getValue().getPaidDate());
						}
					} else {
						if (entry.getValue().getValidityTo().isBefore(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());

						} else if (entry.getValue().getValidityTo().equals(currentTaxTill)
								|| entry.getValue().getValidityTo().isAfter(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.FALSE, entry.getValue().getPaidDate());
						}
					}
				}
			}
		}
		return null;
	}

	private TaxHelper addTaxDetails(String taxType, Double taxAmount, LocalDate validityTo, boolean isAnypendingQuaters,
			LocalDateTime taxPaidDate) {

		TaxHelper tax = new TaxHelper();
		tax.setTaxName(taxType);
		tax.setTax(taxAmount);
		tax.setValidityTo(validityTo);
		tax.setTaxPaidDate(taxPaidDate);
		tax.setAnypendingQuaters(isAnypendingQuaters);

		return tax;
	}

	private List<TaxDetailsDTO> getTaxDetails(RegistrationDetailsDTO registrationOptional, List<String> taxType,
			RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			boolean isOtherState) {

		List<TaxDetailsDTO> listOfTaxDetails = new ArrayList<>();
		List<TaxDetailsDTO> listOfPaidTax = new ArrayList<>();
		String prNo = null;
		String trNo = null;
		String applicationNo = null;
		if (isChassesApplication) {
			trNo = stagingRegistrationDetails.getTrNo();
			applicationNo = stagingRegistrationDetails.getApplicationNo();
			/*
			 * listOfPaidTax = taxDetailsDAO.findByApplicationNoAndTrNoAndTaxStatus(
			 * stagingRegistrationDetails.getApplicationNo(),
			 * trNo,TaxStatusEnum.ACTIVE.getCode());
			 */
		} else if (isApplicationFromMvi) {
			prNo = regServiceDTO.getPrNo();
			applicationNo = regServiceDTO.getRegistrationDetails().getApplicationNo();
			// listOfPaidTax =
			// taxDetailsDAO.findByPrNoAndTaxStatus(prNo,TaxStatusEnum.ACTIVE.getCode());

		} else if (isOtherState) {
			if (regServiceDTO != null
					&& (regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
							|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				applicationNo = regServiceDTO.getRegistrationDetails().getApplicationNo();
			}
		} else {
			prNo = registrationOptional.getPrNo();
			applicationNo = registrationOptional.getApplicationNo();
			// listOfPaidTax
			// =taxDetailsDAO.findByPrNoAndTaxStatus(prNo,TaxStatusEnum.ACTIVE.getCode());
		}

		return taxDetails(applicationNo, taxType, prNo);
	}

	public LocalDate calculateTaxUpTo(Integer indexPosition, Integer quaternNumber) {
		if (quaternNumber == 1) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-04-" + String.valueOf(LocalDate.now().getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 2) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-07-" + String.valueOf(LocalDate.now().getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 3) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-10-" + String.valueOf(LocalDate.now().getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 4) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-01-" + String.valueOf(LocalDate.now().getYear());
			return validity(indexPosition, formatter, date1);
		}
		return null;
	}

	@Override
	public LocalDate validity(String taxType) {
		List<Integer> quaterOne = new ArrayList<>();
		List<Integer> quaterTwo = new ArrayList<>();
		List<Integer> quaterThree = new ArrayList<>();
		List<Integer> quaterFour = new ArrayList<>();
		quaterOne.add(0, 4);
		quaterOne.add(1, 5);
		quaterOne.add(2, 6);
		quaterTwo.add(0, 7);
		quaterTwo.add(1, 8);
		quaterTwo.add(2, 9);
		quaterThree.add(0, 10);
		quaterThree.add(1, 11);
		quaterThree.add(2, 12);
		quaterFour.add(0, 1);
		quaterFour.add(1, 2);
		quaterFour.add(2, 3);
		Integer indexPosition = 0;
		Integer quaternNumber = 0;
		LocalDate Validity = null;
		if (quaterOne.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterOne.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 1;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
		} else if (quaterTwo.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterTwo.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 2;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
		} else if (quaterThree.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterThree.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 3;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
		} else if (quaterFour.contains(LocalDate.now().getMonthValue())) {
			indexPosition = quaterFour.indexOf(LocalDate.now().getMonthValue());
			quaternNumber = 4;
			Validity = calculateTaxUpTo(indexPosition, quaternNumber);
		}
		/*
		 * if (taxType.equalsIgnoreCase(TaxTypeEnum.HalfyearlyTax.getDesc())) { return
		 * Validity.plusMonths(3); } else if
		 * (taxType.equalsIgnoreCase(TaxTypeEnum.HalfyearlyTax.getDesc())) { return
		 * Validity.plusMonths(9); } else {
		 * 
		 * }
		 */
		return Validity;
	}

	private LocalDate validity(Integer indexPosition, DateTimeFormatter formatter, String date1) {
		LocalDate localDate = LocalDate.parse(date1, formatter);
		LocalDate newDate = localDate.withDayOfMonth(localDate.getMonth().maxLength());
		LocalDate newDate1 = newDate.plusMonths(2);
		return newDate1.withDayOfMonth(newDate1.getMonth().maxLength());
	}

	public long roundUpperTen(Double totalTax) {
		if ((totalTax % 10f) == 0) {
			return (int) Math.round(totalTax);
		} else {
			int taxIntValue = totalTax.intValue();
			if (taxIntValue % 10 == 9) {
				Double tax = Math.ceil(totalTax);
				if ((tax % 10f) != 0) {
					tax = tax + 1;
				}
				return tax.longValue();
			} else {
				return ((Math.round(totalTax) / 10) * 10 + 10);
			}
		}
	}

	private Pair<Long, LocalDate> getCesFee(Double quarterTax, String cov, LocalDate cessLastvalidity,
			String classOfvehicle) {
		List<Integer> quaterFour = new ArrayList<>();
		quaterFour.add(0, 1);
		quaterFour.add(1, 2);
		quaterFour.add(2, 3);
		LocalDate endDate = null;
		Long cessFee = null;
		Long monthsBetween = null;
		Optional<MasterTaxExcemptionsDTO> excemptionPercentage = Optional.empty();
		Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalue(cov);
		if (optionalTaxExcemption.isPresent()
				&& optionalTaxExcemption.get().getValuetype().equalsIgnoreCase(TaxTypeEnum.CESS.getCode())) {
			cessFee = optionalTaxExcemption.get().getTaxvalue().longValue();
			return Pair.of(cessFee, endDate);
		} else {
			excemptionPercentage = masterTaxExcemptionsDAO.findByKeyvalue(classOfvehicle);

			Double discount = (double) ((quarterTax * 4) / 12);
			if (quaterFour.contains(LocalDate.now().getMonthValue())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
				String date1 = "31-03-" + String.valueOf(LocalDate.now().getYear());
				endDate = LocalDate.parse(date1, formatter);
				if (cessLastvalidity == null) {
					int currentMonth = 04;
					int monthUpTo = endDate.getMonthValue();
					int months = monthUpTo - currentMonth;
					monthsBetween = Long.parseLong(String.valueOf(months + 1));
					// long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), endDate);
					// monthsBetween = monthsBetween + 1;
				} else {
					monthsBetween = ChronoUnit.MONTHS
							.between(cessLastvalidity.withDayOfMonth(cessLastvalidity.getMonth().maxLength()), endDate);
				}
				Double totalCesFee = (discount * monthsBetween) * 10 / 100;
				if (excemptionPercentage.isPresent()
						&& !excemptionPercentage.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {
					// check percentage discount
					double cessdiscount = (totalCesFee * excemptionPercentage.get().getTaxvalue()) / 100;
					totalCesFee = totalCesFee - cessdiscount;
				}
				if (totalCesFee > 1500) {
					cessFee = 1500l;
					return Pair.of(cessFee, endDate);
				} else {
					cessFee = roundUpperTen(totalCesFee);
					return Pair.of(cessFee, endDate);
				}

			} else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
				String date1 = "31-03-" + String.valueOf(LocalDate.now().plusYears(1).getYear());
				endDate = LocalDate.parse(date1, formatter);
				if (cessLastvalidity == null) {
					int currentMonth = 04;
					// int monthUpTo = 12;
					int months = (12 - currentMonth) + 1;// year end month and add current month.
					// long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), endDate);
					monthsBetween = Long.parseLong(String.valueOf(months + endDate.getMonthValue()));
				} else {
					monthsBetween = ChronoUnit.MONTHS
							.between(cessLastvalidity.withDayOfMonth(cessLastvalidity.getMonth().maxLength()), endDate);
				}
				Double totalCesFee = (discount * monthsBetween) * 10 / 100;
				if (excemptionPercentage.isPresent()
						&& !excemptionPercentage.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {
					// check percentage discount
					double cessdiscount = (totalCesFee * excemptionPercentage.get().getTaxvalue()) / 100;
					totalCesFee = totalCesFee - cessdiscount;
				}
				if (totalCesFee > 1500) {
					cessFee = 1500l;
					return Pair.of(cessFee, endDate);
				} else {
					cessFee = roundUpperTen(totalCesFee);
					return Pair.of(cessFee, endDate);
				}
			}

		}
	}

	private Pair<Long, Double> finalLifeTaxCalculation(StagingRegistrationDetailsDTO stagingRegDetails,
			RegServiceDTO regServiceDTO, RegistrationDetailsDTO registrationOptional, Double totalLifeTax,
			Float Percent, boolean isApplicationFromMvi, boolean isChassesApplication, boolean isOtherState) {
		Double penality = 0d;
		List<MasterTaxFuelTypeExcemptionDTO> list = masterTaxFuelTypeExcemptionDAO.findAll();
		if (isChassesApplication) {
			if (stagingRegDetails.getOfficeDetails() == null
					|| stagingRegDetails.getOfficeDetails().getOfficeCode() == null) {
				logger.error("office details missing [{}].", stagingRegDetails.getApplicationNo());
				throw new BadRequestException("office details missing. " + stagingRegDetails.getApplicationNo());
			}
			if (list.stream().anyMatch(type -> type.getFuelType().stream()
					.anyMatch(fuel -> fuel.equalsIgnoreCase(stagingRegDetails.getVahanDetails().getFuelDesc())))) {
				totalLifeTax = batteryDiscount(stagingRegDetails.getInvoiceDetails().getInvoiceValue(), totalLifeTax,
						Percent);

				long tax = roundUpperTen(totalLifeTax);
				return Pair.of(tax, 0d);

			} else {
				long tax = roundUpperTen(totalLifeTax);
				return Pair.of(tax, 0d);

			}
		} else if (isApplicationFromMvi) {
			if (regServiceDTO.getOfficeDetails() == null || regServiceDTO.getOfficeDetails().getOfficeCode() == null) {
				logger.error("office details missing [{}].", regServiceDTO.getApplicationNo());
				throw new BadRequestException("office details missing. " + regServiceDTO.getApplicationNo());
			}

			long tax = roundUpperTen(totalLifeTax);
			return Pair.of(tax, 0d);

		} else if (isOtherState) {
			Double penalty = 0d;
			RegServiceVO vo = regServiceMapper.convertEntity(regServiceDTO);
			OtherStateApplictionType applicationType = getOtherStateVehicleStatus(vo);
			if (OtherStateApplictionType.ApplicationNO.equals(applicationType)) {
				if (list.stream().anyMatch(type -> type.getFuelType().stream().anyMatch(fuel -> fuel
						.equalsIgnoreCase(regServiceDTO.getRegistrationDetails().getVahanDetails().getFuelDesc())))) {
					totalLifeTax = batteryDiscount(
							regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue(), totalLifeTax,
							Percent);

					long tax = roundUpperTen(totalLifeTax);
					return Pair.of(tax, 0d);

				} else {
					long tax = roundUpperTen(totalLifeTax);
					return Pair.of(tax, 0d);

				}
			} else if (OtherStateApplictionType.TrNo.equals(applicationType)) {
				// VCR details is yes no need to collected taxi
				if (!regServiceDTO.getRegistrationDetails().isTaxPaidByVcr()) {
					if (list.stream()
							.anyMatch(type -> type.getFuelType().stream().anyMatch(fuel -> fuel.equalsIgnoreCase(
									regServiceDTO.getRegistrationDetails().getVahanDetails().getFuelDesc())))) {
						totalLifeTax = batteryDiscount(
								regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue(),
								totalLifeTax, Percent);
						penality = otherStateNeedToPayPenality(
								regServiceDTO.getRegistrationDetails().getTrGeneratedDate().toLocalDate(),
								totalLifeTax);
						long tax = roundUpperTen(totalLifeTax);
						return Pair.of(tax, penality);

					} else {

						if (vo.getRegistrationDetails().getTrIssueDate() != null
								&& regServiceDTO.getRegistrationDetails().getTrGeneratedDate() == null) {
							regServiceDTO.getRegistrationDetails().setTrGeneratedDate(DateConverters
									.convertLocalDateToLocalDateTime(vo.getRegistrationDetails().getTrIssueDate()));
						}
						penality = otherStateNeedToPayPenality(
								regServiceDTO.getRegistrationDetails().getTrGeneratedDate().toLocalDate(),
								totalLifeTax);
						long tax = roundUpperTen(totalLifeTax);
						return Pair.of(tax, penality);
					}
				} else {
					if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
							|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
							&& regServiceDTO.isMviDone()) {

						if (list.stream()
								.anyMatch(type -> type.getFuelType().stream().anyMatch(fuel -> fuel.equalsIgnoreCase(
										regServiceDTO.getRegistrationDetails().getVahanDetails().getFuelDesc())))) {
							totalLifeTax = batteryDiscount(
									regServiceDTO.getRegistrationDetails().getInvoiceDetails().getInvoiceValue(),
									totalLifeTax, Percent);

							long tax = roundUpperTen(totalLifeTax);
							return Pair.of(tax, 0d);

						} else {

							if (vo.getRegistrationDetails().getTrIssueDate() != null
									&& regServiceDTO.getRegistrationDetails().getTrGeneratedDate() == null) {
								regServiceDTO.getRegistrationDetails().setTrGeneratedDate(DateConverters
										.convertLocalDateToLocalDateTime(vo.getRegistrationDetails().getTrIssueDate()));
							}

							long tax = roundUpperTen(totalLifeTax);
							return Pair.of(tax, 0d);
						}
					}
				}
			} else {
				LocalDate entryDate = getEarlerDate(regServiceDTO.getnOCDetails().getDateOfEntry(),
						regServiceDTO.getnOCDetails().getIssueDate());
				penality = otherStateNeedToPayPenality(entryDate, totalLifeTax);
				long tax = roundUpperTen(totalLifeTax);
				return Pair.of(tax, penality);

			}

		}
		// return ;
		return Pair.of(0L, penality);

	}

	private Double otherStateNeedToPayPenality(LocalDate otherStateDate, Double tax) {
		Double penality = 0d;
		Pair<Integer, Integer> trMonthpositionInQuater = getMonthposition(otherStateDate);
		LocalDate firstQuaterUpTo = calculateTaxUpTo(trMonthpositionInQuater.getFirst(),
				trMonthpositionInQuater.getSecond());
		// Pair<Integer, Integer> toDayMonthpositionInQuater =
		// getMonthposition(LocalDate.now());
		if (otherStateDate.getMonthValue() == LocalDate.now().getMonthValue()
				&& otherStateDate.getYear() == LocalDate.now().getYear()) {
			// return false;
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
			String date1 = "01-" + String.valueOf(LocalDate.now().getMonthValue()) + "-"
					+ String.valueOf(LocalDate.now().getYear());
			LocalDate localDate = LocalDate.parse(date1, formatter);

			Long totalMonths = ChronoUnit.MONTHS.between(otherStateDate, localDate);
			double quaters = totalMonths / 3;
			if (quaters <= 1.0) {
				penality = tax * 1 / 100;
			} else {
				penality = tax * 2 / 100;
			}

		}
		return penality;

	}

	private Double batteryDiscount(Double invoiceValue, Double totalTax, Float Percent) {

		Float discount = (Percent / 12f) * 7f;
		return ((invoiceValue * discount) / 100f);
	}

	public LocalDate lifTaxValidityCal() {

		return LocalDate.now().minusDays(1).plusYears(12);

	}

	@Override
	public Double getOldCovTax(String cov, String seatingCapacity, Integer ulw, Integer gvw, String stateCode,
			String permitcode, String routeCode) {
		Double totalQuarterlyTax = 0d;
		Optional<MasterTaxBased> taxCalBasedOn = masterTaxBasedDAO.findByCovcode(cov);
		if (!taxCalBasedOn.isPresent()) {
			logger.error("No record found in master_taxbased for: " + cov);
			// throw error message
			throw new BadRequestException("No record found in master_taxbased for: " + cov);
		}
		if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(ulwCode)) {
			Optional<MasterTax> OptionalTax = taxTypeDAO
					.findByCovcodeAndToulwGreaterThanEqualAndFromulwLessThanEqualAndStatecodeAndPermitcode(cov, ulw,
							ulw, stateCode, permitcode);

			if (!OptionalTax.isPresent()) {
				logger.error("No record found in master_tax for: " + cov + "and ULW: " + ulw);
				// throw error message
				throw new BadRequestException("No record found in master_tax for: " + cov + "and ULW: " + ulw);
			}

			totalQuarterlyTax = quarterlyTaxCalculation(OptionalTax, taxCalBasedOn.get().getBasedon(), seatingCapacity,
					cov, ulw, gvw, permitcode);
			return totalQuarterlyTax;
		} else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(rlwCode)) {
			Optional<MasterTax> OptionalTax = taxTypeDAO
					.findByCovcodeAndTorlwGreaterThanEqualAndFromrlwLessThanEqualAndStatecodeAndPermitcode(cov, gvw,
							gvw, stateCode, permitcode);
			if (!OptionalTax.isPresent()) {
				logger.error("No record found in master_tax for: " + cov + "and rLW: " + gvw);
				// throw error message
				throw new BadRequestException("No record found in master_tax for: " + cov + "and rLW: " + gvw);
			}

			totalQuarterlyTax = quarterlyTaxCalculation(OptionalTax, taxCalBasedOn.get().getBasedon(), seatingCapacity,
					cov, ulw, gvw, permitcode);

		} else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(seatingCapacityCode)) {

			Optional<MasterTax> OptionalTax = Optional.empty();
			if (StringUtils.isNoneBlank(routeCode)) {
				OptionalTax = taxTypeDAO
						.findByCovcodeAndSeattoGreaterThanEqualAndSeatfromLessThanEqualAndStatecodeAndPermitcodeAndServType(
								cov, Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity), stateCode,
								permitcode, routeCode);
			} else {
				OptionalTax = taxTypeDAO
						.findByCovcodeAndSeattoGreaterThanEqualAndSeatfromLessThanEqualAndStatecodeAndPermitcode(cov,
								Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity), stateCode,
								permitcode);
			}
			if (!OptionalTax.isPresent()) {
				// throw error message
				throw new BadRequestException(
						"No record found in master_tax for: " + cov + "and seatingCapacity: " + seatingCapacity);
			}

			totalQuarterlyTax = quarterlyTaxCalculation(OptionalTax, taxCalBasedOn.get().getBasedon(), seatingCapacity,
					cov, ulw, gvw, permitcode);
		}
		Optional<MasterTaxExcemptionsDTO> excemptionPercentage = masterTaxExcemptionsDAO.findByKeyvalue(cov);
		if (excemptionPercentage.isPresent()
				&& !excemptionPercentage.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {
			// check percentage discount
			double discount = (totalQuarterlyTax * excemptionPercentage.get().getTaxvalue()) / 100;
			totalQuarterlyTax = totalQuarterlyTax - discount;
		}
		return totalQuarterlyTax;
	}

	private Double getOldQuaterTax(RegistrationDetailsDTO stagingRegDetails, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, StagingRegistrationDetailsDTO stagingRegistrationDetails,
			boolean isChassesApplication, boolean isOtherState) {
		Double OldQuaterTax = null;
		String cov = null;
		if (isChassesApplication) {
			OldQuaterTax = getOldCovTax(stagingRegistrationDetails.getClassOfVehicle(),
					stagingRegistrationDetails.getVahanDetails().getSeatingCapacity(),
					stagingRegistrationDetails.getVahanDetails().getUnladenWeight(),
					stagingRegistrationDetails.getVahanDetails().getGvw(), stateCode, permitcode, null);
			cov = stagingRegistrationDetails.getClassOfVehicle();
		} else if (isApplicationFromMvi) {
			OldQuaterTax = getOldCovTax(regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
					regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity(),
					regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw(),
					regServiceDTO.getRegistrationDetails().getVehicleDetails().getRlw(), stateCode, permitcode, null);
			cov = regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle();
		} else if (isOtherState) {
			if (regServiceDTO != null
					&& (regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
							|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
									.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				OldQuaterTax = getOldCovTax(
						regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle(),
						regServiceDTO.getRegistrationDetails().getVehicleDetails().getSeatingCapacity(),
						regServiceDTO.getRegistrationDetails().getVehicleDetails().getUlw(),
						regServiceDTO.getRegistrationDetails().getVehicleDetails().getRlw(), stateCode, permitcode,
						null);
				cov = regServiceDTO.getRegistrationDetails().getVehicleDetails().getClassOfVehicle();
			}

		} else {
			OldQuaterTax = getOldCovTax(stagingRegDetails.getVehicleDetails().getClassOfVehicle(),
					stagingRegDetails.getVehicleDetails().getSeatingCapacity(),
					stagingRegDetails.getVehicleDetails().getUlw(), stagingRegDetails.getVehicleDetails().getRlw(),
					stateCode, permitcode, null);
			cov = stagingRegDetails.getVehicleDetails().getClassOfVehicle();
		}

		return OldQuaterTax;
	}

	private Double quarterlyTaxCalculation(Optional<MasterTax> OptionalTax, String taxBasedon, String seatingCapacity,
			String cov, Integer ulw, Integer gvw, String permitcode) {

		if (taxBasedon.equalsIgnoreCase(seatingCapacityCode)) {

			Double quatertax = null;
			Float tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(seatingCapacity) - 1));
			if ((OptionalTax.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.COCT.getCovCode())
					|| OptionalTax.get().getCovcode().equalsIgnoreCase(ClassOfVehicleEnum.TOVT.getCovCode()))
					&& StringUtils.isNoneBlank(permitcode)
					&& permitcode.equalsIgnoreCase(PermitsEnum.PermitCodes.AITP.getPermitCode())) {
				tax = (OptionalTax.get().getTaxamount() * (Integer.parseInt(seatingCapacity) - 1));
			}

			quatertax = tax.doubleValue();
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
					.findByKeyvalueAndSeattoGreaterThanEqualAndSeatfromLessThanEqual(cov,
							Integer.parseInt(seatingCapacity), Integer.parseInt(seatingCapacity));
			if (optionalTaxExcemption.isPresent()
					&& optionalTaxExcemption.get().getValuetype().equalsIgnoreCase(seatingCapacityCode)) {

				quatertax = optionalTaxExcemption.get().getTaxvalue().doubleValue();

			}
			return quatertax;
		} else if (OptionalTax.get().getIncrementalweight() == 0) {

			Double quatertax = OptionalTax.get().getTaxamount().doubleValue();
			return quatertax;

		} else {
			if (taxBasedon.equalsIgnoreCase(ulwCode)) {
				Float weight = (float) ((ulw - ((OptionalTax.get().getFromulw() - 1)))
						/ OptionalTax.get().getIncrementalweight().doubleValue());
				Float result = (float) ((Math.ceil(weight.doubleValue()) * OptionalTax.get().getIncrementalamount())
						+ OptionalTax.get().getTaxamount());
				Double quatertax = result.doubleValue();
				return quatertax;
			} else if (taxBasedon.equalsIgnoreCase(rlwCode)) {
				Double weight = (double) ((gvw - ((OptionalTax.get().getFromrlw() - 1)))
						/ OptionalTax.get().getIncrementalweight().doubleValue());
				Double result = ((Math.ceil(weight) * OptionalTax.get().getIncrementalamount())
						+ OptionalTax.get().getTaxamount());
				Double quatertax = result.doubleValue();
				return quatertax;
			}
		}
		return null;

	}

	public TaxHelper returnTaxDetails(String taxType, Long tax, Double penality, LocalDate taxTill,
			Long reoundTaxArrears, Double penalityArrears, TaxTypeEnum.TaxPayType payTaxType, String permitType) {
		TaxHelper taxHelper = new TaxHelper();
		taxHelper.setTaxName(taxType);
		taxHelper.setValidityTo(taxTill);
		taxHelper.setTaxAmountForPayments(tax);
		if (penality != null) {
			taxHelper.setPenalty(roundUpperTen(penality));
		}
		if (penality != null) {
			taxHelper.setPenaltyArrears(roundUpperTen(penalityArrears));
		}
		if (penalityArrears != null) {
			taxHelper.setTaxArrearsRound(reoundTaxArrears);
		}
		taxHelper.setTaxPayType(payTaxType);
		taxHelper.setPermitType(permitType);
		return taxHelper;

	}

	private TaxHelper returnTaxForNewGo(String taxType, Long tax, Double penality, LocalDate taxTill,
			Long reoundTaxArrears, Double penalityArrears, TaxTypeEnum.TaxPayType payTaxType, String permitType,
			Long quaterTaxNewGotax) {
		TaxHelper taxHelper = new TaxHelper();
		taxHelper.setTaxName(taxType);
		taxHelper.setValidityTo(taxTill);
		taxHelper.setTaxAmountForPayments(tax);
		if (penality != null) {
			taxHelper.setPenalty(roundUpperTen(penality));
		}
		if (penality != null) {
			taxHelper.setPenaltyArrears(roundUpperTen(penalityArrears));
		}
		if (penalityArrears != null) {
			taxHelper.setTaxArrearsRound(reoundTaxArrears);
		}
		taxHelper.setTaxPayType(payTaxType);
		taxHelper.setPermitType(permitType);
		if (quaterTaxNewGotax != null) {
			taxHelper.setQuaterTaxForNewGo(quaterTaxNewGotax);
		}
		return taxHelper;

	}

	private Integer getGvwWeight(String applicationNo, Integer gvw) {
		Integer rlw = null;

		Optional<AlterationDTO> alterDetails = alterationDao.findByApplicationNo(applicationNo);
		if (!alterDetails.isPresent()) {
			throw new BadRequestException("No record found in alter collection for: " + applicationNo);
		}

		if (ClassOfVehicleEnum.ARVT.getCovCode().equalsIgnoreCase(alterDetails.get().getCov())) {

			if (alterDetails.get().getTrailers().isEmpty()) {
				throw new BadRequestException(
						"Trailers Details not found in Alteration collection for(ARVT) : " + applicationNo);
			}
			Integer gtw = alterDetails.get().getTrailers().stream().findFirst().get().getGtw();
			for (TrailerChassisDetailsDTO trailerDetails : alterDetails.get().getTrailers()) {
				if (trailerDetails.getGtw() > gtw) {
					gtw = trailerDetails.getGtw();
				}
			}
			rlw = gvw + gtw;
			return rlw;
		} else {
			rlw = gvw;
			return rlw;
		}

	}

	public double calculateAgeOfTheVehicle(LocalDate localDateTime, LocalDate entryDate) {

		double yeare = localDateTime.until(entryDate, ChronoUnit.DAYS) / 365.2425f;
		yeare = Math.abs(yeare);
		yeare = Math.ceil(yeare);
		return yeare;

	}

	private LocalDate getEarlerDate(LocalDate dateOfEnter, LocalDate nocIssueDate) {
		LocalDate entryDate;
		if (dateOfEnter.isAfter(nocIssueDate)) {
			entryDate = dateOfEnter;

		} else {
			entryDate = nocIssueDate;
		}
		return entryDate;
	}

	@Override
	public OtherStateApplictionType getOtherStateVehicleStatus(RegServiceVO regService) {
		OtherStateApplictionType applicationType = null;
		if (!regService.getRegistrationDetails().isRegVehicleWithPR()
				&& !regService.getRegistrationDetails().isRegVehicleWithTR()) {
			// application no
			applicationType = OtherStateApplictionType.ApplicationNO;
		} else if (regService.getRegistrationDetails().isRegVehicleWithTR()
				&& !regService.getRegistrationDetails().isRegVehicleWithPR()) {
			// TR no
			applicationType = OtherStateApplictionType.TrNo;
		} else {
			// PrNO
			applicationType = OtherStateApplictionType.PrNo;
		}
		return applicationType;
	}

	@Override
	public TaxHelper greenTaxCalculation(String applicationNo, List<ServiceEnum> serviceEnum) {
		Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(applicationNo);
		if (!regOptional.isPresent()) {
			logger.error("No record found in Reg Service for:[{}] " + applicationNo);
			throw new BadRequestException("No record found in Reg Service for:[{}] " + applicationNo);
		}
		RegistrationDetailsDTO regDTO = regOptional.get();
		Optional<MasterGreenTax> masterGreenTax = masterGreenTaxDAO.findByCovcode(regDTO.getClassOfVehicle());
		if (!masterGreenTax.isPresent()) {
			logger.error("No record found in MasterGreenTax for:[{}] " + applicationNo);
			throw new BadRequestException("No record found in MasterGreenTax for:[{}] " + applicationNo);
		}
		// TODO fuel exception
		Optional<MasterGreenTaxFuelexcemption> fuelException = masterGreenTaxFuelexcemptionDAO
				.findByFuelTypeIn(regDTO.getVahanDetails().getFuelDesc());
		if (fuelException.isPresent()) {
			return returnTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(), 0l, 0d, LocalDate.now(), 0l, 0d,
					TaxTypeEnum.TaxPayType.REG, "");
		}

		Pair<Long, LocalDate> taxAndValid = Pair.of(0l, LocalDate.now());
		if (regDTO.getPrGeneratedDate() == null && regDTO.getRegistrationValidity() != null
				&& regDTO.getRegistrationValidity().getPrGeneratedDate() != null) {
			regDTO.setPrGeneratedDate(regDTO.getRegistrationValidity().getPrGeneratedDate().atStartOfDay());
		}

		if (LocalDateTime.now()
				.isAfter(regDTO.getPrGeneratedDate().plusYears(masterGreenTax.get().getAgeOfVehicle().longValue()))) {
			TaxHelper taxHelper = getIsGreenTaxPending(applicationNo,
					Arrays.asList(ServiceCodeEnum.GREEN_TAX.getCode()), LocalDate.now(), regOptional.get().getPrNo());

			if (taxHelper == null || taxHelper.getTax() == null) {
				taxAndValid = finalGreenTaxCal(masterGreenTax, regDTO.getPrGeneratedDate()
						.plusYears(masterGreenTax.get().getAgeOfVehicle().longValue()).toLocalDate());
			} else if (taxHelper.isAnypendingQuaters()) {
				// TaxHelper taxHelper = new TaxHelper();
				LocalDate taxUpTo = getGreenTaxDetails(taxHelper, applicationNo);

				taxAndValid = finalGreenTaxCal(masterGreenTax, taxUpTo);

			}
			return returnTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(), taxAndValid.getFirst(), 0d,
					taxAndValid.getSecond(), 0l, 0d, TaxTypeEnum.TaxPayType.REG, "");
		} else if (serviceEnum != null && !serviceEnum.isEmpty() && serviceEnum.contains(ServiceEnum.RENEWAL)) {

			return returnTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(),
					masterGreenTax.get().getTaxamount().longValue(), 0d,
					regDTO.getRegistrationValidity().getRegistrationValidity().toLocalDate().minusDays(1).plusYears(
							masterGreenTax.get().getAgeOfIncrement()),
					0l, 0d, TaxTypeEnum.TaxPayType.REG, "");
		}
		return null;

	}

	private Pair<Long, LocalDate> finalGreenTaxCal(Optional<MasterGreenTax> masterGreenTax, LocalDate taxUpTo) {
		LocalDate date = taxUpTo;
		int incrementCount = 0;
		boolean status = Boolean.TRUE;
		do {

			date = date.plusYears(masterGreenTax.get().getAgeOfIncrement());
			if (date.isAfter(LocalDate.now())) {
				status = Boolean.FALSE;
			} else {
				incrementCount = incrementCount + 1;
			}
		} while (status);

		incrementCount = incrementCount + 1;

		Long taxAmount = masterGreenTax.get().getTaxamount().longValue() * incrementCount;
		return Pair.of(taxAmount, date);

	}

	private LocalDate getGreenTaxDetails(TaxHelper taxHelper, String applicationNo) {
		// TaxHelper taxHelper = new TaxHelper();
		List<RegServiceDTO> listofRegService = regServiceDAO.findByRegistrationDetailsApplicationNoAndServiceIds(
				applicationNo, ServiceEnum.ALTERATIONOFVEHICLE.getId());
		if (listofRegService.isEmpty()) {
			return taxHelper.getValidityTo();
		}
		listofRegService.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		// RegServiceDTO regCitizenDto = listofRegService.stream().findFirst().get();

		for (RegServiceDTO dto : listofRegService) {
			if (dto.getAlterationDetails().getVehicleTypeFrom() != null
					&& dto.getAlterationDetails().getVehicleTypeTo() != null) {
				if (dto.getAlterationDetails().getVehicleTypeFrom() != dto.getAlterationDetails().getVehicleTypeTo()) {
					if (dto.getSlotDetails().getSlotDate().isAfter(taxHelper.getValidityTo())) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
						String date1 = String.valueOf(taxHelper.getValidityTo().getDayOfMonth()) + "-"
								+ String.valueOf(taxHelper.getValidityTo().getMonthValue()) + "-"
								+ String.valueOf(dto.getSlotDetails().getSlotDate().getYear());
						LocalDate localDate = LocalDate.parse(date1, formatter);
						return localDate;
					} else {
						return taxHelper.getValidityTo();
					}

				}
			}
		}
		return taxHelper.getValidityTo();

	}

	private TaxHelper getIsGreenTaxPending(String applicationNo, List<String> taxType, LocalDate currentTaxTill,
			String prNo) {

		List<TaxDetailsDTO> taxDetailsList = getGreenTaxDetails(applicationNo, taxType, prNo);
		if (taxDetailsList == null || taxDetailsList.isEmpty()) {
			return this.addTaxDetails(null, null, null, Boolean.TRUE, null);
		}
		taxDetailsList.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		TaxDetailsDTO dto = taxDetailsList.stream().findFirst().get();
		if (dto.getTaxDetails() == null) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		for (Map<String, TaxComponentDTO> map : dto.getTaxDetails()) {

			for (Entry<String, TaxComponentDTO> entry : map.entrySet()) {
				if (taxType.stream().anyMatch(key -> key.equalsIgnoreCase(entry.getKey()))) {

					if (entry.getValue().getValidityTo().isBefore(currentTaxTill)) {
						return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
								entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());

					} else if (entry.getValue().getValidityTo().equals(currentTaxTill)
							|| entry.getValue().getValidityTo().isAfter(currentTaxTill)) {
						return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
								entry.getValue().getValidityTo(), Boolean.FALSE, entry.getValue().getPaidDate());
					}

				}
			}
		}
		return null;
	}

	public List<TaxDetailsDTO> taxDetails(String applicationNo, List<String> taxType, String prNo) {
		List<TaxDetailsDTO> listOfTaxDetails = new ArrayList<>();
		List<TaxDetailsDTO> taxDetailsList = new ArrayList<>();
		if (taxType.contains(ServiceCodeEnum.CESS_FEE.getCode())) {
			taxDetailsList = taxDetailsDAO.findFirst50ByApplicationNoOrderByCreatedDateDesc(applicationNo);
		} else {
			taxDetailsList = taxDetailsDAO
					.findFirst10ByApplicationNoAndPaymentPeriodInOrderByCreatedDateDesc(applicationNo, taxType);
		}
		if (taxDetailsList.isEmpty()) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		registrationService.updatePaidDateAsCreatedDate(taxDetailsList);
		taxDetailsList.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		for (String type : taxType) {
			for (TaxDetailsDTO taxDetails : taxDetailsList) {
				if (taxDetails.getTaxDetails() == null) {
					logger.error("TaxDetails map not found: [{}]", applicationNo);
					throw new BadRequestException("TaxDetails map not found:" + applicationNo);
				}
				if (taxDetails.getTaxDetails().stream().anyMatch(key -> key.keySet().contains(type))) {
					listOfTaxDetails.add(taxDetails);
					break;
				}
			}
		}
		taxDetailsList.clear();
		return listOfTaxDetails;
	}

	private List<TaxDetailsDTO> getGreenTaxDetails(String applicationNo, List<String> taxType, String prNo) {
		List<TaxDetailsDTO> listOfTaxDetails = new ArrayList<>();
		// TODO need to modify DB query
		List<TaxDetailsDTO> taxDetailsList = taxDetailsDAO
				.findFirst50ByApplicationNoOrderByCreatedDateDesc(applicationNo);
		if (taxDetailsList.isEmpty()) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		registrationService.updatePaidDateAsCreatedDate(taxDetailsList);
		taxDetailsList.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		for (String type : taxType) {
			for (TaxDetailsDTO taxDetails : taxDetailsList) {
				if (taxDetails.getTaxDetails() == null) {
					logger.error("TaxDetails not found: [{}]", applicationNo);
					throw new BadRequestException("TaxDetails not found:" + applicationNo);
				}
				if (taxDetails.getTaxDetails().stream().anyMatch(key -> key.keySet().contains(type))) {
					listOfTaxDetails.add(taxDetails);
					break;
				}
			}
		}
		taxDetailsList.clear();
		return listOfTaxDetails;
	}

	private TaxHelper getOldTaxDetailsForAlterVehicle(String applicationNo, Double OptionalTax, Integer valu,
			RegServiceDTO regServiceDTO, String taxType, String prNo) {
		Double penalityTax = 0d;
		Double quaterTax = 0d;
		List<String> listTaxType = this.taxTypes();
		listTaxType.add(ServiceCodeEnum.LIFE_TAX.getCode());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
		String date1 = "01-" + String.valueOf(LocalDate.now().getMonthValue()) + "-"
				+ String.valueOf(LocalDate.now().getYear());
		LocalDate localDate = LocalDate.parse(date1, formatter);
		TaxHelper oldTaxDetails = getIsGreenTaxPending(applicationNo, listTaxType, LocalDate.now(), prNo);
		if (oldTaxDetails.isAnypendingQuaters()
				&& !oldTaxDetails.getTaxName().equalsIgnoreCase(ServiceCodeEnum.LIFE_TAX.getCode())) {
			Long totalMonthsForPenality = ChronoUnit.MONTHS.between(oldTaxDetails.getValidityTo(), localDate);
			totalMonthsForPenality = Math.abs(totalMonthsForPenality);
			totalMonthsForPenality = totalMonthsForPenality + 1;
			double Penalityquaters = totalMonthsForPenality / 3d;
			double PenalityQuatersRound = Math.ceil(totalMonthsForPenality / 3);
			PenalityQuatersRound = PenalityQuatersRound - 1;
			Pair<Double, Double> taxArrAndPenality = chassisPenalitTax(OptionalTax, (PenalityQuatersRound));
			if (valu == 0) {
				// quaterTax = OptionalTax;
			} else if (valu == 1) {
				penalityTax = ((OptionalTax * 25) / 100);

			} else {
				penalityTax = ((OptionalTax * 50) / 100);

			}
			// quaterTax = taxArrAndPenality.getFirst() + OptionalTax;
			// penalityTax = penalityTax + taxArrAndPenality.getSecond();
			// return Pair.of(quaterTax, penalityTax);
			return returnTaxDetails(OptionalTax, taxArrAndPenality.getFirst(), penalityTax,
					taxArrAndPenality.getSecond(), 0d);
		} else {
			if (!oldTaxDetails.getTaxName().equalsIgnoreCase(ServiceCodeEnum.LIFE_TAX.getCode())) {

				Long totalMonths = ChronoUnit.MONTHS.between(localDate, oldTaxDetails.getValidityTo());
				totalMonths = totalMonths + 1;
				Double OldQuaterTax = getOldCovTax(regServiceDTO.getRegistrationDetails().getClassOfVehicle(),
						regServiceDTO.getRegistrationDetails().getVahanDetails().getSeatingCapacity(),
						regServiceDTO.getRegistrationDetails().getVahanDetails().getUnladenWeight(),
						regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw(), stateCode, permitcode, null);
				Double oldQuaterTaxPerMonth = OldQuaterTax / 3;
				Double newQuaterTaxPerMonth = OptionalTax / 3;
				Double newTax = newQuaterTaxPerMonth * totalMonths;
				Double oldTax = oldQuaterTaxPerMonth * totalMonths;
				newTax = newTax - oldTax;
				if (newTax < 1) {
					newTax = 0d;
				}
				taxType = oldTaxDetails.getTaxName();
				// return Pair.of(newTax, 0d);
				return returnTaxDetails(newTax, 0d, 0d, 0d, 0d);
			} else {
				if (valu == 0) {
					quaterTax = OptionalTax;
				} else if (valu == 1) {
					quaterTax = (OptionalTax / 3) * 2;
				} else {
					quaterTax = (OptionalTax / 3) * 1;
				}
				// return Pair.of(quaterTax, 0d);
				return returnTaxDetails(quaterTax, 0d, 0d, 0d, 0d);
			}
		}

	}

	private LocalDate getCessDetails(TaxHelper taxHelper, String applicationNo) {
		// TaxHelper taxHelper = new TaxHelper();
		List<RegServiceDTO> listofRegService = regServiceDAO.findByRegistrationDetailsApplicationNoAndServiceIds(
				applicationNo, ServiceEnum.ALTERATIONOFVEHICLE.getId());
		if (listofRegService.isEmpty()) {
			return taxHelper.getValidityTo();
		}
		listofRegService.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		// RegServiceDTO regCitizenDto = listofRegService.stream().findFirst().get();

		for (RegServiceDTO dto : listofRegService) {
			if (dto.getAlterationDetails().getVehicleTypeFrom() != null
					&& dto.getAlterationDetails().getVehicleTypeTo() != null) {
				if (dto.getAlterationDetails().getVehicleTypeFrom() == "N"
						&& dto.getAlterationDetails().getVehicleTypeTo() == "T") {
					if (dto.getSlotDetails().getSlotDate().isAfter(taxHelper.getValidityTo())) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
						String date1 = String.valueOf(taxHelper.getValidityTo().getDayOfMonth()) + "-"
								+ String.valueOf(taxHelper.getValidityTo().getMonthValue()) + "-"
								+ String.valueOf(dto.getSlotDetails().getSlotDate().getYear());
						LocalDate localDate = LocalDate.parse(date1, formatter);
						return localDate;
					} else {
						return taxHelper.getValidityTo();
					}

				}
			}
		}
		return null;

	}

	private boolean checkIsToPayLifeTaxBefore(String applicationNo, AlterationDTO altDto, String prNo) {
		List<TaxDetailsDTO> listTax = taxDetailsDAO.findFirst10ByApplicationNoAndPaymentPeriodInOrderByCreatedDateDesc(
				applicationNo, Arrays.asList(TaxTypeEnum.LifeTax.getDesc()));
		if (listTax.isEmpty()) {
			return Boolean.TRUE;

		}
		if (altDto.getFromCov() != null && altDto.getCov() != null
				&& altDto.getFromCov().equalsIgnoreCase(ClassOfVehicleEnum.IVCN.getCovCode())) {
			boolean flag = Boolean.FALSE;
			for (TaxDetailsDTO dto : listTax) {
				if (getListofTwoWeelerNotTransCovs().stream()
						.anyMatch(covs -> covs.equalsIgnoreCase(dto.getClassOfVehicle()))) {
					flag = Boolean.TRUE;
				}
			}
			if (!flag) {
				listTax.clear();
				return Boolean.TRUE;// true;
			}
		}
		for (TaxDetailsDTO dto : listTax) {
			for (Map<String, TaxComponentDTO> map : dto.getTaxDetails()) {
				for (Entry<String, TaxComponentDTO> entry : map.entrySet()) {
					if (TaxTypeEnum.LifeTax.getDesc().equalsIgnoreCase(entry.getKey())) {
						listTax.clear();
						return Boolean.FALSE;
					}
				}
			}
		}
		listTax.clear();
		return true;
	}

	public List<String> getListofTwoWeelerNotTransCovs() {
		List<String> list = new ArrayList<>();
		list.add(ClassOfVehicleEnum.MCYN.getCovCode());
		list.add(ClassOfVehicleEnum.MMCN.getCovCode());
		return list;
	}

	@Override
	public Integer getGvwWeightForCitizen(RegistrationDetailsDTO registrationDetails) {
		Integer rlw = null;

		if (ClassOfVehicleEnum.ARVT.getCovCode().equalsIgnoreCase(registrationDetails.getClassOfVehicle())) {
			Integer gtw = 0;
			if (registrationDetails.getVahanDetails() == null
					|| registrationDetails.getVahanDetails().getTrailerChassisDetailsDTO() == null) {
				// removed for migration data as per murthy sir input.
				// throw new BadRequestException("trailer details missed for ARVT : " +
				// registrationDetails.getPrNo());
			} else {
				gtw = registrationDetails.getVahanDetails().getTrailerChassisDetailsDTO().stream().findFirst().get()
						.getGtw();
				for (TrailerChassisDetailsDTO trailerDetails : registrationDetails.getVahanDetails()
						.getTrailerChassisDetailsDTO()) {
					if (trailerDetails.getGtw() > gtw) {
						gtw = trailerDetails.getGtw();
					}
				}
			}

			rlw = registrationDetails.getVahanDetails().getGvw() + gtw;
			return rlw;
		} else {
			if (registrationDetails.getVahanDetails() == null) {
				throw new BadRequestException("vehicle Details not found for: " + registrationDetails.getPrNo());
			}
			if (registrationDetails.getVahanDetails() != null
					&& registrationDetails.getVahanDetails().getGvw() == null) {
				throw new BadRequestException("vehicle GVW not found for: " + registrationDetails.getPrNo());
			}

			return registrationDetails.getVahanDetails().getGvw();
		}

	}

	private Pair<Double, Double> getcurrenttaxWithOutPenality(TaxHelper vcrDetails, Double newPermitTax,
			Double quaterTax, Double penality, Double OptionalTax, Integer valu) {
		if (!vcrDetails.isVcr()) {
			// With out VCR
			if (valu == 0) {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 3, 0);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else if (valu == 1) {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 2, 25);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 1, 50);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			}
		} else {
			// With VCR
			if (valu == 0) {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 3, 0);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else if (valu == 1) {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 2, 100);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			} else {
				Pair<Double, Double> pairOfTax = getTaxWithVcrWithOutPenality(quaterTax, penality, OptionalTax,
						newPermitTax, 1, 200);
				quaterTax = pairOfTax.getFirst();
				penality = pairOfTax.getSecond();
			}
		}
		return Pair.of(quaterTax, penality);
	}

	// Other state Green tax
	@Override
	public TaxHelper greenTaxCalculation(String applicationNo, List<ServiceEnum> serviceEnum, RegServiceDTO regDTO) {
		Optional<MasterGreenTax> masterGreenTax = masterGreenTaxDAO
				.findByCovcode(regDTO.getRegistrationDetails().getVahanDetails().getVehicleClass());
		if (!masterGreenTax.isPresent()) {
			logger.error("No record found in MasterGreenTax for:[{}] " + applicationNo);
			throw new BadRequestException("No record found in MasterGreenTax for:[{}] " + applicationNo);
		}
		// TODO fuel exception
		Optional<MasterGreenTaxFuelexcemption> fuelException = masterGreenTaxFuelexcemptionDAO
				.findByFuelTypeIn(regDTO.getRegistrationDetails().getVahanDetails().getFuelDesc());
		if (fuelException.isPresent()) {
			return returnTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(), 0l, 0d, LocalDate.now(), 0l, 0d,
					TaxTypeEnum.TaxPayType.REG, "");
		}

		Pair<Long, LocalDate> taxAndValid = Pair.of(0l, LocalDate.now());
		if (LocalDate.now().isAfter(regDTO.getRegistrationDetails().getPrIssueDate()
				.plusYears(masterGreenTax.get().getAgeOfVehicle().longValue()))) {
			TaxHelper taxHelper = null;
			// getIsGreenTaxPending(applicationNo,
			// Arrays.asList(ServiceCodeEnum.GREEN_TAX.getCode()), LocalDate.now());

			if (taxHelper == null || taxHelper.getTax() == null) {
				taxAndValid = finalGreenTaxCal(masterGreenTax, regDTO.getRegistrationDetails().getPrIssueDate()
						.plusYears(masterGreenTax.get().getAgeOfVehicle().longValue()));
			} else if (taxHelper.isAnypendingQuaters()) {
				// TaxHelper taxHelper = new TaxHelper();
				LocalDate taxUpTo = getGreenTaxDetails(taxHelper, applicationNo);

				taxAndValid = finalGreenTaxCal(masterGreenTax, taxUpTo);

			}
			return returnTaxDetails(ServiceCodeEnum.GREEN_TAX.getCode(), taxAndValid.getFirst(), 0d,
					taxAndValid.getSecond(), 0l, 0d, TaxTypeEnum.TaxPayType.REG, "");
		}
		return null;

	}

	private TaxHelper overRideTheTax(Double OptionalTax, Integer valu, RegistrationDetailsDTO stagingRegDetails,
			LocalDate currentTaxTill, RegServiceDTO regServiceDTO, boolean isApplicationFromMvi,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			String classOfvehicle, boolean isOtherState, String taxType, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, TaxHelper lastTaxTillDate, TaxHelper vcrDetails,
			TaxHelper currenTax) {

		Optional<FinalTaxHelper> optionalPrDocument = finalTaxHelperDAO
				.findByPrNoInAndStatusIsTrue(stagingRegDetails.getPrNo());
		if (optionalPrDocument.isPresent()) {
			return this.finalOverRideTax(stagingRegDetails, optionalPrDocument.get(), OptionalTax, currentTaxTill,
					lastTaxTillDate, vcrDetails, currenTax, valu);
		} else {
			if (serviceEnum != null && serviceEnum.stream().anyMatch(id -> id.equals(ServiceEnum.TAXATION))) {
				if (stagingRegDetails != null && stagingRegDetails.isWeightAltDone()) {
					Optional<FinalTaxHelper> optionaDocument = finalTaxHelperDAO
							.findByPrNoInAndWeightAltIsTrue(stagingRegDetails.getPrNo());
					if (optionaDocument.isPresent()) {
						return this.finalOverRideTax(stagingRegDetails, optionaDocument.get(), OptionalTax,
								currentTaxTill, lastTaxTillDate, vcrDetails, currenTax, valu);
					}
				}
			}
			Integer districCode = null;
			if (stagingRegDetails.getApplicantDetails().getPresentAddress() == null
					|| stagingRegDetails.getApplicantDetails().getPresentAddress().getDistrict() == null
					|| stagingRegDetails.getApplicantDetails().getPresentAddress().getDistrict()
							.getDistricCode() == null) {
				aadharSeeding.updateDistirctDetails(stagingRegDetails.getApplicantDetails(),
						stagingRegDetails.getOfficeDetails());

			} else {
				districCode = stagingRegDetails.getApplicantDetails().getPresentAddress().getDistrict()
						.getDistricCode();
			}
			Optional<FinalTaxHelper> optionalDistrictDocument = finalTaxHelperDAO
					.findByDistricCodeInAndCovInAndStatusIsTrue(districCode, stagingRegDetails.getClassOfVehicle());
			if (optionalDistrictDocument.isPresent()) {
				return this.finalOverRideTax(stagingRegDetails, optionalDistrictDocument.get(), OptionalTax,
						currentTaxTill, lastTaxTillDate, vcrDetails, currenTax, valu);
			} else {
				Optional<FinalTaxHelper> optionalOfficeDocument = finalTaxHelperDAO
						.findByOfficeCodeInAndCovInAndStatusIsTrue(stagingRegDetails.getOfficeDetails().getOfficeCode(),
								stagingRegDetails.getClassOfVehicle());
				if (optionalOfficeDocument.isPresent()) {
					return this.finalOverRideTax(stagingRegDetails, optionalOfficeDocument.get(), OptionalTax,
							currentTaxTill, lastTaxTillDate, vcrDetails, currenTax, valu);
				}
			}
		}
		return currenTax;

	}

	private TaxHelper finalOverRideTax(RegistrationDetailsDTO registrationDetails, FinalTaxHelper finalTaxHelper,
			Double OptionalTax, LocalDate currentTaxTill, TaxHelper lastTaxTillDate, TaxHelper vcrDetails,
			TaxHelper currenTax, Integer valu) {
		Double taxArrears = 0d;
		Double penaltyArrears = 0d;
		Double penalty = 0d;
		Double tax = 0d;
		double discount = 0d;
		Double finalquaterTax = OptionalTax;
		switch (finalTaxHelper.getExcemptionsType()) {
		case QUATER:
			Pair<Double, Double> quaterTaxAndPenality = getpendingQuaters(currentTaxTill,
					lastTaxTillDate.getValidityTo(), OptionalTax, finalquaterTax, OptionalTax, vcrDetails,
					finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTYARREARS),
					finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAXARREARS), false, null, OptionalTax);
			taxArrears = quaterTaxAndPenality.getFirst();
			penaltyArrears = quaterTaxAndPenality.getSecond();
			penalty = currenTax.getPenalty().doubleValue();
			tax = currenTax.getTax().doubleValue();
			break;
		case PERCENTAGE:
			discount = ((currenTax.getTaxArrears()
					* finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAXARREARS)) / 100);
			taxArrears = currenTax.getTaxArrears() - discount;
			discount = ((currenTax.getPenaltyArrears()
					* finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTYARREARS)) / 100);
			penaltyArrears = currenTax.getPenaltyArrears() - discount;
			discount = ((currenTax.getPenalty() * finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTY))
					/ 100);
			penalty = currenTax.getPenalty() - discount;
			discount = ((currenTax.getTax() * finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAX)) / 100);
			tax = currenTax.getTax() - discount;
			break;
		case DIRECTTAXAMOUNT:
			taxArrears = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAXARREARS);
			penaltyArrears = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTYARREARS);
			penalty = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTY);
			tax = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAX);
			break;
		case COVANDPERMIT:
			Integer gvw = getGvwWeightForCitizen(registrationDetails);
			Double newTax = this.getOldCovTax(finalTaxHelper.getCov().stream().findFirst().get(),
					registrationDetails.getVahanDetails().getSeatingCapacity(),
					registrationDetails.getVahanDetails().getUnladenWeight(), gvw, stateCode,
					finalTaxHelper.getPermitCode(), null);

			Pair<Double, Double> quaterTaxAndPenalitywithNewTax = getpendingQuaters(currentTaxTill,
					lastTaxTillDate.getValidityTo(), newTax, finalquaterTax, newTax, vcrDetails,
					finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTYARREARS),
					finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAXARREARS), false, null, OptionalTax);
			taxArrears = quaterTaxAndPenalitywithNewTax.getFirst();
			penaltyArrears = quaterTaxAndPenalitywithNewTax.getSecond();
			Pair<Double, Double> pairOfTax = getcurrenttaxWithPenality(vcrDetails, null, newTax, penalty, OptionalTax,
					valu);
			tax = pairOfTax.getFirst();
			penalty = pairOfTax.getSecond();

			break;

		default:

			break;
		}
		if (tax == null) {
			tax = currenTax.getTax().doubleValue();
		}
		if (penalty == null) {
			penalty = currenTax.getPenalty().doubleValue();
		}
		if (taxArrears == null) {
			taxArrears = currenTax.getTaxArrears();
		}
		if (penaltyArrears == null) {
			penaltyArrears = currenTax.getPenaltyArrears().doubleValue();
		}
		return returnTaxDetails(tax, taxArrears, penalty, penaltyArrears, null);
	}

	public LocalDate calculateTaxFrom(Integer indexPosition, Integer quaternNumber) {
		if (quaternNumber == 1) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-04-" + String.valueOf(LocalDate.now().getYear());
			return LocalDate.parse(date1, formatter);
		} else if (quaternNumber == 2) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-07-" + String.valueOf(LocalDate.now().getYear());
			return LocalDate.parse(date1, formatter);
		} else if (quaternNumber == 3) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-10-" + String.valueOf(LocalDate.now().getYear());
			return LocalDate.parse(date1, formatter);
		} else if (quaternNumber == 4) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-01-" + String.valueOf(LocalDate.now().getYear());
			return LocalDate.parse(date1, formatter);
		}
		return null;
	}

	public LocalDate calculateChassisTaxUpTo(Integer indexPosition, Integer quaternNumber, LocalDate date) {
		if (quaternNumber == 1) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-04-" + String.valueOf(date.getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 2) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-07-" + String.valueOf(date.getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 3) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-10-" + String.valueOf(date.getYear());
			return validity(indexPosition, formatter, date1);
		} else if (quaternNumber == 4) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "01-01-" + String.valueOf(date.getYear());
			return validity(indexPosition, formatter, date1);
		}
		return null;
	}

	private TaxHelper overRideChassisTax(String trNo, TaxHelper currenTax) {

		Optional<FinalTaxHelper> optionalPrDocument = finalTaxHelperDAO.findBytrNoInAndStatusIsTrue(trNo);
		if (optionalPrDocument.isPresent()) {
			return this.finalOverRideChassisTax(optionalPrDocument.get(), currenTax);
		}
		return currenTax;

	}

	private TaxHelper finalOverRideChassisTax(FinalTaxHelper finalTaxHelper, TaxHelper currenTax) {
		Double taxArrears = 0d;
		Double penaltyArrears = 0d;
		Double penalty = 0d;
		Double tax = 0d;
		double discount = 0d;
		switch (finalTaxHelper.getExcemptionsType()) {
		case DIRECTTAXAMOUNT:
			taxArrears = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAXARREARS);
			penaltyArrears = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTYARREARS);
			penalty = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.PENALTY);
			tax = finalTaxHelper.getTaxModeDetails().get(TaxTypeEnum.TaxModule.TAX);
			break;

		default:
			break;
		}
		if (tax == null) {
			tax = currenTax.getTax().doubleValue();
		}
		if (penalty == null) {
			penalty = currenTax.getPenalty().doubleValue();
		}
		if (taxArrears == null) {
			taxArrears = currenTax.getTaxArrears();
		}
		if (penaltyArrears == null) {
			penaltyArrears = currenTax.getPenaltyArrears().doubleValue();
		}
		return returnTaxDetails(tax, taxArrears, penalty, penaltyArrears, null);
	}

	private Integer getGvwWeightForAlt(RegServiceDTO regServiceDTO) {
		Integer rlw = null;
		Integer gtw = 0;
		if (regServiceDTO.getAlterationDetails().getCov() != null && ClassOfVehicleEnum.ARVT.getCovCode()
				.equalsIgnoreCase(regServiceDTO.getAlterationDetails().getCov())) {
			if (regServiceDTO.getAlterationDetails().getTrailers() == null
					|| regServiceDTO.getAlterationDetails().getTrailers().isEmpty()) {
				throw new BadRequestException("Trailers Details not found in Alteration collection for(ARVT) : "
						+ regServiceDTO.getApplicationNo());
			}
			gtw = regServiceDTO.getAlterationDetails().getTrailers().stream().findFirst().get().getGtw();
			for (TrailerChassisDetailsDTO trailerDetails : regServiceDTO.getAlterationDetails().getTrailers()) {
				if (trailerDetails.getGtw() > gtw) {
					gtw = trailerDetails.getGtw();
				}
			}
			rlw = regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw() + gtw;
			return rlw;
		} else {
			rlw = regServiceDTO.getRegistrationDetails().getVahanDetails().getGvw() + gtw;
			return rlw;
		}

	}

	/**
	 * find Staging By AppNo
	 * 
	 * @param appNo
	 * @return
	 */

	public StagingRegistrationDetailsDTO findStagingByAppNo(String appNo) {
		Optional<StagingRegistrationDetailsDTO> stagingOptional = stagingRegistrationDetailsDAO
				.findByApplicationNo(appNo);
		if (!stagingOptional.isPresent()) {
			logger.error("No record found in Reg Service for:[{}] ", appNo);
			throw new BadRequestException("No record found in Staging for:[{}] " + appNo);
		}
		return stagingOptional.get();
	}

	/**
	 * find RegService By AppNo
	 * 
	 * @param citizenapplicationNo
	 * @return
	 */

	public RegServiceDTO findRegServiceByAppNo(String citizenapplicationNo) {
		Optional<RegServiceDTO> regServiceOptional = regServiceDAO.findByApplicationNo(citizenapplicationNo);
		if (!regServiceOptional.isPresent()) {
			logger.error("No record found in Reg Service for:[{}] ", citizenapplicationNo);
			throw new BadRequestException("No record found in Reg Service for:[{}] " + citizenapplicationNo);
		}
		return regServiceOptional.get();
	}

	/**
	 * find RegistrationDetailsDTO based on appNo
	 * 
	 * @param appNo
	 * @return
	 */
	public RegistrationDetailsDTO findRegDetailsByAppNo(String appNo) {
		Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(appNo);
		if (!regOptional.isPresent()) {
			logger.error("No record found in Reg Service for:[{}] ", appNo);
			throw new BadRequestException("No record found in Reg Service for:[{}] " + appNo);
		}
		return regOptional.get();
	}

	public Optional<RegistrationDetailsDTO> findRegDetails(String appNo) {
		System.out.println("reg Details");
		Optional<RegistrationDetailsDTO> regOptional = registrationDetailDAO.findByApplicationNo(appNo);
		if (!regOptional.isPresent()) {
			logger.error("No record found in Reg Service for:[{}] ", appNo);
			throw new BadRequestException("No record found in Reg Service for:[{}] " + appNo);
		}
		return regOptional;
	}

	/**
	 * find AlterationDTO based on appNo
	 * 
	 * @param appNo
	 * @return
	 */
	public AlterationDTO findAltDetails(String appNo) {
		Optional<AlterationDTO> alterDetails = alterationDao.findByApplicationNo(appNo);
		if (!alterDetails.isPresent()) {
			throw new BadRequestException("No record found in master_tax for: " + appNo);
		}
		return alterDetails.get();
	}

	/**
	 * find class of vehicle and payPeriod if application is chassis
	 * 
	 * @param isChassesApplication
	 * @param applicationNo
	 */
	public void isChassisApplication(RuleEngineObject rule, Boolean isChassesApplication, String applicationNo) {
		logger.info("Chassis Application [{}] from Helper", applicationNo);
		if (isChassesApplication) {
			StagingRegistrationDetailsDTO stagingRegistrationDetails = findStagingByAppNo(applicationNo);
			AlterationDTO alterDetails = findAltDetails(stagingRegistrationDetails.getApplicationNo());
			this.classOfVehicle = alterDetails.getCov();
			this.Payperiod = masterPayperiodDAO.findByCovcode(alterDetails.getCov());
			logger.info("classOfVehicle [{}] and PayPeriod [{}]", classOfVehicle, Payperiod);
			rule.setAlterationDetails(alterDetails);
			rule.setStagingDetails(stagingRegistrationDetails);
		}
	}

	/**
	 * find Class Of Vehicle and payPeriod if application is from mvi
	 * 
	 * @param isApplicationFromMvi
	 * @param citizenapplicationNo
	 */
	public void isApplicationFromMvi(RuleEngineObject rule, Boolean isApplicationFromMvi, String citizenapplicationNo) {
		logger.info("MVI Application [{}] from Helper", citizenapplicationNo);
		if (isApplicationFromMvi) {
			RegServiceDTO regServiceDTO = findRegServiceByAppNo(citizenapplicationNo);
			if (regServiceDTO.getAlterationDetails() != null && regServiceDTO.getAlterationDetails().getCov() != null) {
				this.classOfVehicle = regServiceDTO.getAlterationDetails().getCov();
				this.Payperiod = masterPayperiodDAO.findByCovcode(regServiceDTO.getAlterationDetails().getCov());
			} else {
				this.classOfVehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
				this.Payperiod = masterPayperiodDAO
						.findByCovcode(regServiceDTO.getRegistrationDetails().getClassOfVehicle());
			}
			rule.setRegServiceDetails(regServiceDTO);
		}
		logger.info("classOfVehicle [{}] and PayPeriod [{}]", classOfVehicle, Payperiod);
	}

	public void test(RuleEngineObject ruleEngine) {
		System.out.println(ruleEngine.getTaxType());
		System.out.println("Test for quarterly Tax");
	}

	/**
	 * find Class Of vehicle and PayPeriod if application is OtherState
	 * 
	 * @param isOtherState
	 * @param applicationNo
	 */

	public void otherState(RuleEngineObject rule, Boolean isOtherState, String applicationNo) {
		logger.info("other State from helper [{}]", applicationNo);
		if (isOtherState) {
			RegServiceDTO regServiceDTO = findRegServiceByAppNo(applicationNo);
			this.classOfVehicle = regServiceDTO.getRegistrationDetails().getClassOfVehicle();
			if ((regServiceDTO.getRegistrationDetails().getClassOfVehicle()
					.equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| regServiceDTO.getRegistrationDetails().getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode()))
					&& regServiceDTO.isMviDone()) {
				AlterationDTO alterDetails = findAltDetails(regServiceDTO.getApplicationNo());
				this.classOfVehicle = alterDetails.getCov();
				rule.setAlterationDetails(alterDetails);
			}
			Payperiod = masterPayperiodDAO.findByCovcode(this.classOfVehicle);
			logger.info("classOfVehicle [{}] and PayPeriod [{}]", classOfVehicle, Payperiod);
			rule.setRegServiceDetails(regServiceDTO);

		}
	}

	/**
	 * if application is not chassis and not from MVI and not other State then
	 * reading cov from RegistrationDetailsDTO
	 * 
	 * @param appNo
	 */
	public void defualtCov(RuleEngineObject rule, String appNo) {
		RegistrationDetailsDTO registrationDetails = findRegDetailsByAppNo(appNo);
		if (registrationDetails.getTrGeneratedDate() == null && registrationDetails.getRegistrationValidity() != null
				&& registrationDetails.getRegistrationValidity().getTrGeneratedDate() != null) {
			registrationDetails.setTrGeneratedDate(
					registrationDetails.getRegistrationValidity().getTrGeneratedDate().atStartOfDay());
		}
		if (StringUtils.isBlank(registrationDetails.getClassOfVehicle())) {
			logger.error("class of vehicle not found for :[{}] ", appNo);
			throw new BadRequestException("class of vehicle not found for :" + appNo);
		}
		this.classOfVehicle = registrationDetails.getClassOfVehicle();
		this.Payperiod = masterPayperiodDAO.findByCovcode(registrationDetails.getClassOfVehicle());
		logger.info("classOfVehicle [{}] and PayPeriod [{}]", classOfVehicle, Payperiod);
		rule.setRegDetails(registrationDetails);
	}

	public void verifyPayPeriod(String applicationNo) {
		if (!Payperiod.isPresent()) {
			logger.error("No record found in master_payperiod for:[{}] ", applicationNo);
			throw new BadRequestException("No record found in master_payperiod for: " + applicationNo);
		}
	}

	public void taxTypeBoth(RuleEngineObject rule) {
		Boolean gostatus = Boolean.FALSE;
		TaxHelper quaterTax = null;
		if (Payperiod.get().getPayperiod().equalsIgnoreCase(TaxTypeEnum.BOTH.getCode())) {

			if (rule.isChassisApplication()) {
				logger.info("Chassis Application [{}] with Tax Type BOTH ", rule.getApplicationNo());
				// need to call body builder cov
				Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
						rule.getAlterationDetails().getFromSeatingCapacity(),
						rule.getStagingDetails().getVahanDetails().getGvw());
				gostatus = payperiodAndGoStatus.getSecond();

			} else if (rule.isApplicationFromMvi()) {
				logger.info("MVI Application [{}] with Tax Type BOTH ", rule.getApplicationNo());

				if (rule.getRegServiceDetails().getAlterationDetails() != null
						&& rule.getRegServiceDetails().getAlterationDetails().getSeating() != null) {
					// need to chamge gvw
					Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
							rule.getRegServiceDetails().getAlterationDetails().getSeating(),
							rule.getRegServiceDetails().getRegistrationDetails().getVahanDetails().getGvw());
					gostatus = payperiodAndGoStatus.getSecond();
				} else {
					// need to chamge gvw
					Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
							rule.getRegServiceDetails().getRegistrationDetails().getVehicleDetails()
									.getSeatingCapacity(),
							rule.getRegServiceDetails().getRegistrationDetails().getVahanDetails().getGvw());
					gostatus = payperiodAndGoStatus.getSecond();
				}
			} else if (rule.isOtherState()) {
				logger.info("OTHER_STATE Application [{}] with Tax Type BOTH ", rule.getApplicationNo());
				// OTHERSTATE

				String seats = rule.getRegServiceDetails().getRegistrationDetails().getVehicleDetails()
						.getSeatingCapacity();
				if (rule.getAlterationDetails() != null
						&& StringUtils.isNoneBlank(rule.getAlterationDetails().getSeating())) {
					seats = rule.getAlterationDetails().getSeating();
				}
				getPayPeroidForBoth(Payperiod, seats,
						rule.getRegServiceDetails().getRegistrationDetails().getVehicleDetails().getRlw());
			} else {
				logger.info("DEFAULT Application [{}] with Tax Type BOTH ", rule.getApplicationNo());
				Pair<Optional<MasterPayperiodDTO>, Boolean> payperiodAndGoStatus = getPayPeroidForBoth(Payperiod,
						rule.getRegDetails().getVehicleDetails().getSeatingCapacity(),
						rule.getRegDetails().getVahanDetails().getGvw());
				gostatus = payperiodAndGoStatus.getSecond();
			}
		}
	}

	public TaxHelper paymentType(boolean isChassesApplication, StagingRegistrationDetailsDTO stagingRegistrationDetails,
			String taxType, MasterPayperiodDTO payperiod, AlterationDTO alterDetails) {
		TaxTypeEnum.TaxPayType payTaxType = TaxTypeEnum.TaxPayType.REG;
		if (isChassesApplication) {
			payTaxType = TaxTypeEnum.TaxPayType.DIFF;
		}
		switch (TaxTypeEnum.getTaxTypeEnumByCode(payperiod.getPayperiod())) {
		case LifeTax:
			Optional<MasterAmountSecoundCovsDTO> basedOnInvoice = Optional.empty();
			Optional<MasterAmountSecoundCovsDTO> basedOnsecoundVehicle = Optional.empty();
			Double totalLifeTax;
			if (taxType.equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, LocalDate.now(), 0l, 0d, payTaxType, "");
			}
			List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO = masterAmountSecoundCovsDAO.findAll();
			if (masterAmountSecoundCovsDTO.isEmpty()) {
				// TODO:need to Exception throw
			}
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueOrCovcode(
					stagingRegistrationDetails.getVahanDetails().getMakersModel(),
					stagingRegistrationDetails.getClassOfVehicle());
			if (optionalTaxExcemption.isPresent()) {
				//
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(),
						optionalTaxExcemption.get().getTaxvalue().longValue(), 0d, lifTaxValidityCal(), 0l, 0d,
						payTaxType, "");
			}
			basedOnInvoice = masterAmountSecoundCovsDTO.stream()
					.filter(m -> m.getAmountcovcode().contains(alterDetails.getCov())).findFirst();

			basedOnsecoundVehicle = masterAmountSecoundCovsDTO.stream()
					.filter(m -> m.getSecondcovcode().contains(alterDetails.getCov())).findFirst();

			/**
			 * ownerType GOVERNMENT OR POLICE
			 */
			if (stagingRegistrationDetails.getOwnerType().getCode().equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
					|| stagingRegistrationDetails.getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
				Optional<MasterTax> OptionalLifeTax = taxTypeDAO
						.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(alterDetails.getCov(),
								stagingRegistrationDetails.getOwnerType().getCode(), stateCode, regStatus,
								freshVehicleAge);
				if (!OptionalLifeTax.isPresent()) {
					logger.error("No record found in master_tax for: " + alterDetails.getCov() + "and"
							+ stagingRegistrationDetails.getOwnerType());
					// throw error message
					throw new BadRequestException("No record found in master_tax for: " + alterDetails.getCov() + "and"
							+ stagingRegistrationDetails.getOwnerType());
				}

				totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
						* OptionalLifeTax.get().getPercent() / 100);
				if (totalLifeTax.equals(Double.valueOf(0))) {
					return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d,
							payTaxType, "");
				}

				Pair<Long, Double> lifeTax = lifeTaxCalculation(stagingRegistrationDetails, totalLifeTax,
						OptionalLifeTax.get().getPercent(), isChassesApplication);

				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
						lifTaxValidityCal(), 0l, 0d, payTaxType, "");

				/**
				 * Invoice greater than 1000000 and ownerType Individual
				 */

			} else if (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue() > amount
					&& basedOnInvoice.isPresent() && stagingRegistrationDetails.getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.Individual.getCode())) {
				totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
						* basedOnInvoice.get().getTaxpercentinvoice() / 100f);
				Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, null, null,
						totalLifeTax, basedOnInvoice.get().getTaxpercentinvoice(), false, isChassesApplication, false);
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
						lifTaxValidityCal(), 0l, 0d, payTaxType, "");
				/**
				 * for secound vehicle
				 */

			} else if (basedOnsecoundVehicle.isPresent() && !stagingRegistrationDetails.getIsFirstVehicle()) {
				totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
						* basedOnsecoundVehicle.get().getSecondvehiclepercent() / 100f);
				Pair<Long, Double> lifeTax = finalLifeTaxCalculation(stagingRegistrationDetails, null, null,
						totalLifeTax, basedOnsecoundVehicle.get().getSecondvehiclepercent(), false,
						isChassesApplication, false);
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
						lifTaxValidityCal(), 0l, 0d, payTaxType, "");
			}

			/**
			 * invalid carriage
			 */
			else if (alterDetails.getCov().equalsIgnoreCase(ClassOfVehicleEnum.IVCN.getCovCode())) {

				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), 0l, 0d, lifTaxValidityCal(), 0l, 0d, payTaxType,
						"");
			}

			else {
				Optional<MasterTax> OptionalLifeTax = taxTypeDAO
						.findByCovcodeAndOwnershiptypeIgnoreCaseAndStatecodeAndStatusAndFromage(alterDetails.getCov(),
								stagingRegistrationDetails.getOwnerType().getCode(), stateCode, regStatus,
								freshVehicleAge);
				if (!OptionalLifeTax.isPresent()) {
					logger.error("No record found in master_tax for: " + alterDetails.getCov() + "and"
							+ stagingRegistrationDetails.getOwnerType());
					// throw error message
					throw new BadRequestException("No record found in master_tax for: " + alterDetails.getCov() + "and"
							+ stagingRegistrationDetails.getOwnerType());
				}
				totalLifeTax = (stagingRegistrationDetails.getInvoiceDetails().getInvoiceValue()
						* OptionalLifeTax.get().getPercent() / 100f);
				Pair<Long, Double> lifeTax = lifeTaxCalculation(stagingRegistrationDetails, totalLifeTax,
						OptionalLifeTax.get().getPercent(), isChassesApplication);
				return returnTaxDetails(TaxTypeEnum.LifeTax.getDesc(), lifeTax.getFirst(), lifeTax.getSecond(),
						lifTaxValidityCal(), 0l, 0d, payTaxType, "");
			}

		}
		return null;
	}

	private TaxHelper calculateQuarterTax(String applicationNo, boolean isChassesApplication, String taxType,
			String permitTypeCode, String routeCode, Optional<MasterTaxBased> taxCalBasedOn,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, String classOfvehicle,
			TaxTypeEnum.TaxPayType payTaxType, Optional<AlterationDTO> alterDetails, Boolean isWeightAlt) {
		Optional<MasterTax> OptionalTax = Optional.empty();
		Long totalQuarterlyTax;
		if (isChassesApplication) {
			// need to call body builder cov
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO.findByKeyvalueOrCovcode(
					stagingRegistrationDetails.getVahanDetails().getMakersModel(),
					stagingRegistrationDetails.getClassOfVehicle());
			if (optionalTaxExcemption.isPresent()) {
				return returnTaxDetails(taxType, optionalTaxExcemption.get().getTaxvalue().longValue(), 0d,
						validity(taxType), 0l, 0d, payTaxType, "");
			}
			if (stagingRegistrationDetails.getOwnerType().getCode().equalsIgnoreCase(OwnerTypeEnum.Government.getCode())
					|| stagingRegistrationDetails.getOwnerType().getCode()
							.equalsIgnoreCase(OwnerTypeEnum.POLICE.getCode())) {
				LocalDate TaxTill = validity(taxType);
				return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");

			}
			if (!(stagingRegistrationDetails.getClassOfVehicle().equalsIgnoreCase(ClassOfVehicleEnum.CHSN.getCovCode())
					|| stagingRegistrationDetails.getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.CHST.getCovCode())
					|| stagingRegistrationDetails.getClassOfVehicle()
							.equalsIgnoreCase(ClassOfVehicleEnum.ARVT.getCovCode()))) {

				if (checkTaxUpToDateOrNote(false, isChassesApplication, null, null, stagingRegistrationDetails,
						taxType)) {
					LocalDate TaxTill = validity(taxType);
					return returnTaxDetails(taxType, 0l, 0d, TaxTill, 0l, 0d, payTaxType, "");
				}
			}
			taxCalBasedOn = masterTaxBasedDAO.findByCovcode(alterDetails.get().getCov());
			if (!taxCalBasedOn.isPresent()) {
				logger.error("No record found in master_taxbased for: [{}]", classOfvehicle);
				throw new BadRequestException("No record found in master_taxbased for: " + classOfvehicle);
			}
			if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(ulwCode)) {
				String permitType = permitcode;
				if (isChassesApplication) {
					OptionalTax = getUlwTax(alterDetails.get().getCov(), alterDetails.get().getUlw(), stateCode,
							permitcode);

				}
				if (!OptionalTax.isPresent()) {
					logger.error("No record found in master_tax for:[{}] ", applicationNo);
					throw new BadRequestException("No record found in master_tax for: " + applicationNo);
				}

				TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
						taxCalBasedOn.get().getBasedon(), null, null, false, stagingRegistrationDetails,
						isChassesApplication, taxType, classOfvehicle, false, null, permitTypeCode, routeCode, null,
						isWeightAlt);
				payTaxType = totalTaxAndValidity.getTaxPayType();
				if (isChassesApplication) {
					payTaxType = TaxTypeEnum.TaxPayType.DIFF;
				}
				return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
						totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
						totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
			}

			else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(rlwCode)) {
				String permitType = permitcode;
				if (isChassesApplication) {
					Integer gvw = getGvwWeight(stagingRegistrationDetails.getApplicationNo(),
							stagingRegistrationDetails.getVahanDetails().getGvw());
					OptionalTax = getRlwTax(alterDetails.get().getCov(), gvw, stateCode, permitcode);

				}
				if (!OptionalTax.isPresent()) {
					logger.error("No record found in master_tax for: [{}] ", applicationNo);
					throw new BadRequestException("No record found in master_tax for: " + applicationNo);
				}
				TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
						taxCalBasedOn.get().getBasedon(), null, null, false, stagingRegistrationDetails,
						isChassesApplication, taxType, classOfvehicle, false, null, permitTypeCode, routeCode,
						null, isWeightAlt);
				payTaxType = totalTaxAndValidity.getTaxPayType();
				if (isChassesApplication) {
					payTaxType = TaxTypeEnum.TaxPayType.DIFF;
				}
				return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
						totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
						totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
			} else if (taxCalBasedOn.get().getBasedon().equalsIgnoreCase(seatingCapacityCode)) {
				String permitType = StringUtils.EMPTY;
				if (isChassesApplication) {
					OptionalTax = getSeatingCapacityTax(alterDetails.get().getCov(), alterDetails.get().getSeating(),
							stateCode, permitcode, null);

				}

				if (!OptionalTax.isPresent()) {
					throw new BadRequestException("No record found in master_tax for: " + applicationNo);
				}

				TaxCalculationHelper totalTaxAndValidity = quarterlyTaxCalculation(OptionalTax,
						taxCalBasedOn.get().getBasedon(), null, null, false, stagingRegistrationDetails,
						isChassesApplication, taxType, classOfvehicle, false, null, permitTypeCode, routeCode,
						permitType, isWeightAlt);

				if (totalTaxAndValidity != null && totalTaxAndValidity.getTaxPayType() != null)
					payTaxType = totalTaxAndValidity.getTaxPayType();
				if (isChassesApplication) {
					payTaxType = TaxTypeEnum.TaxPayType.DIFF;
				}
				return returnTaxDetails(taxType, totalTaxAndValidity.getReoundTax(), totalTaxAndValidity.getPenality(),
						totalTaxAndValidity.getTaxTill(), totalTaxAndValidity.getReoundTaxArrears(),
						totalTaxAndValidity.getPenalityArrears(), payTaxType, permitType);
			}
		}
		return returnTaxDetails(taxType, 0l, 0d, null, 0l, 0d, payTaxType, "");

	}

	public Pair<Long, Double> lifeTaxCalculation(StagingRegistrationDetailsDTO stagingRegDetails, Double totalLifeTax,
			Float Percent, boolean isChassesApplication) {
		List<MasterTaxFuelTypeExcemptionDTO> list = masterTaxFuelTypeExcemptionDAO.findAll();
		if (isChassesApplication) {
			if (stagingRegDetails.getOfficeDetails() == null
					|| stagingRegDetails.getOfficeDetails().getOfficeCode() == null) {
				logger.error("office details missing [{}].", stagingRegDetails.getApplicationNo());
				throw new BadRequestException("office details missing. " + stagingRegDetails.getApplicationNo());
			}
			if (list.stream().anyMatch(type -> type.getFuelType().stream()
					.anyMatch(fuel -> fuel.equalsIgnoreCase(stagingRegDetails.getVahanDetails().getFuelDesc())))) {
				totalLifeTax = batteryDiscount(stagingRegDetails.getInvoiceDetails().getInvoiceValue(), totalLifeTax,
						Percent);
				long tax = roundUpperTen(totalLifeTax);
				return Pair.of(tax, 0d);
			} else {
				long tax = roundUpperTen(totalLifeTax);
				return Pair.of(tax, 0d);
			}
		}
		return null;
	}
	
/*
	public TaxHelper getLastPaidTaxx(RegistrationDetailsDTO stagingRegDetails, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, LocalDate currentTaxTill,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			List<String> taxTypes, boolean isOtherState) {
		if (stagingRegDetails != null) {
			Optional<MasterTaxExcemptionsDTO> optionalTaxExcemption = masterTaxExcemptionsDAO
					.findByKeyvalue(stagingRegDetails.getVahanDetails().getMakersModel());
			if (optionalTaxExcemption.isPresent()) {
				return this.addTaxDetails(TaxTypeEnum.QuarterlyTax.getDesc(), 0d,
						validity(TaxTypeEnum.QuarterlyTax.getDesc()), Boolean.FALSE, LocalDateTime.now());
			}
		}

		String applicationNo = StringUtils.EMPTY;
		applicationNo = stagingRegDetails != null ? stagingRegDetails.getApplicationNo()
					: stagingRegistrationDetails.getApplicationNo();
	
		List<TaxDetailsDTO> listOfTax = getTaxDetails(stagingRegDetails, taxTypes, regServiceDTO, isApplicationFromMvi,
				stagingRegistrationDetails, isChassesApplication, isOtherState);
		if (listOfTax.isEmpty() && (!taxTypes.contains(ServiceCodeEnum.CESS_FEE.getCode()))) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		if (listOfTax.isEmpty()) {
			return this.addTaxDetails(null, null, null, Boolean.FALSE, null);
		}
		listOfTax.sort((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()));
		TaxDetailsDTO dto = listOfTax.stream().findFirst().get();
		if (dto.getTaxDetails() == null) {
			logger.error("TaxDetails not found: [{}]", applicationNo);
			throw new BadRequestException("TaxDetails not found:" + applicationNo);
		}
		for (Map<String, TaxComponentDTO> map : dto.getTaxDetails()) {

			for (Entry<String, TaxComponentDTO> entry : map.entrySet()) {
				if (taxTypes.stream().anyMatch(key -> key.equalsIgnoreCase(entry.getKey()))) {
					if (entry.getKey().equalsIgnoreCase(ServiceCodeEnum.CESS_FEE.getCode())) {
						if (entry.getValue().getValidityTo() == null) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());

						} else if (entry.getValue().getValidityTo().isBefore(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());
						} else {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.FALSE, entry.getValue().getPaidDate());
						}
					} else {
						if (entry.getValue().getValidityTo().isBefore(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.TRUE, entry.getValue().getPaidDate());

						} else if (entry.getValue().getValidityTo().equals(currentTaxTill)
								|| entry.getValue().getValidityTo().isAfter(currentTaxTill)) {
							return this.addTaxDetails(entry.getKey(), entry.getValue().getAmount(),
									entry.getValue().getValidityTo(), Boolean.FALSE, entry.getValue().getPaidDate());
						}
					}
				}
			}
		}
		return null;
	}
	*/

	public Optional<MasterAmountSecoundCovsDTO> verifyAmountCovs(
			List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO, AlterationDTO alterDetails) {
		return masterAmountSecoundCovsDTO.stream().filter(m -> m.getAmountcovcode().contains(alterDetails.getCov()))
				.findFirst();
	}

	public Optional<MasterAmountSecoundCovsDTO> verifySecondCovs(
			List<MasterAmountSecoundCovsDTO> masterAmountSecoundCovsDTO, AlterationDTO alterDetails) {
		return masterAmountSecoundCovsDTO.stream().filter(m -> m.getSecondcovcode().contains(alterDetails.getCov()))
				.findFirst();
	}

	public void getAlterationDetails(AlterationDTO altDetails) {
		System.out.println("verifying Alt Details" + altDetails.getApplicationNo());
	}

	public void getRegServiceDetails(RegistrationDetailsDTO regDetails) {
		System.out.println("verifying reg Details" + regDetails.getApplicationNo());
	}

	public void getRuleEngineDetails(RuleEngineObject ruleEngine) {
		System.out.println("verifying ruleEngine" + ruleEngine.getApplicationNo());
	}

	public static void helper(final KnowledgeHelper drools) {
		System.out.println("\nrule triggered: " + drools.getRule().getName());
	}
}
