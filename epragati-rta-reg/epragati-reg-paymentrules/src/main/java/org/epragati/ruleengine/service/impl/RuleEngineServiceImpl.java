package org.epragati.ruleengine.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.epragati.common.dao.PropertiesDAO;
import org.epragati.master.dao.AlterationDAO;
import org.epragati.master.dao.MasterAmountSecoundCovsDAO;
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
import org.epragati.master.dto.TaxHelper;
import org.epragati.payments.dao.PaymentFeesDeatailsDAO;
import org.epragati.permits.dao.PermitDetailsDAO;
import org.epragati.regservice.impl.CitizenTaxServiceImpl;
import org.epragati.ruleengine.service.RuleEngineService;
import org.epragati.ruleenigne.paymentsserviceimpl.PaymentGatewayServiceImpl;
import org.epragati.rules.DroolRuleBean;
import org.epragati.rules.FeePartsVO;
import org.epragati.rules.RuleEngineObject;
import org.epragati.taxserviceImpl.CitizenDroolsTaxServiceImpl;
import org.epragati.util.payment.ServiceEnum;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author saikiran.kola
 *
 */
@Service
public class RuleEngineServiceImpl implements RuleEngineService {

	@Value("${drl.files.path}")
	private String filePath;

	@Value("${fee.drl.files.path}")
	private String feeDrlsPath;

	@Autowired
	private PaymentGatewayServiceImpl paymentsHelper;

	@Autowired
	private PaymentFeesDeatailsDAO paymentFeesDeatailsDAO;

	private static final Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

	DroolRuleBean ruleBean = new DroolRuleBean();

	@Autowired
	private Gson gson;

	@Autowired
	private CitizenTaxServiceImpl ruleHelper;

	@Autowired
	private RegistrationDetailDAO registrationDetailDAO;

	@Autowired
	private StagingRegistrationDetailsDAO stagingRegistrationDetailsDAO;

	@Autowired
	private AlterationDAO alterationDAO;

	@Autowired
	private RegServiceDAO regServiceDAO;

	@Autowired
	private MasterPayperiodDAO masterPayperiodDAO;

	@Autowired
	private MasterTaxDAO masterTaxDAO;

	@Autowired
	private MasterAmountSecoundCovsDAO masterAmountSecoundCovsDAO;

	@Autowired
	private MasterNewGoTaxDetailsDAO masterNewGoTaxDetailsDAO;

	@Autowired
	private MasterTaxExcemptionsDAO masterTaxExcemptionsDAO;

	@Autowired
	private MasterTaxBasedDAO masterTaxBasedDAO;

	@Autowired
	private PermitDetailsDAO permitDetailsDAO;

	@Autowired
	private TaxDetailsDAO taxDetailsDAO;

	@Autowired
	private PropertiesDAO propertiesDAO;

	@Autowired
	private MasterWeightsForAltDAO masterWeightsForAltDAO;
	@Autowired
	private MasterTaxFuelTypeExcemptionDAO masterTaxFuelTypeExcemptionDAO;

	/**
	 * execute all drl files containing multiple rules for payments
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * 
	 */

	@Override
	public TaxHelper execute(String applicationNo, boolean isApplicationFromMvi, boolean isChassesApplication,
			String taxType, boolean isOtherState, String CitizenapplicationNo, List<ServiceEnum> serviceEnum,
			String permitTypeCode, String routeCode, Boolean isWeightAlt) throws FileNotFoundException {
		// String json = toJson(map);
		// CitizenFeeDetailsInput inputObj = gson.fromJson(json,
		// CitizenFeeDetailsInput.class);
		KieSession kieSession = this.load();
		kieSession.insert(prepareRuleEngineObject(applicationNo, isApplicationFromMvi, isChassesApplication, taxType,
				isOtherState, CitizenapplicationNo, serviceEnum, permitTypeCode, routeCode, isWeightAlt));
		/*
		 * kieSession.getAgenda().getAgendaGroup("AGENDA1").setFocus();
		 * kieSession.getAgenda().getAgendaGroup("AGENDA2").setFocus();
		 */
		kieSession.fireAllRules();
		Collection<FactHandle> allHandles = kieSession.getFactHandles();
		for (FactHandle handle : allHandles) {
			if (kieSession.getObject(handle) instanceof RuleEngineObject) {
				logger.info("Final OutPut ---->   :[{}]", kieSession.getObject(handle));
				RuleEngineObject rIbj = (RuleEngineObject) kieSession.getObject(handle);
				if (rIbj.getTaxHelper() != null) {
					System.out.println("--------------------------------------------------");
					System.out.println("tax--------------------------------------------------");
					System.out.println("tax= " + rIbj.getTaxHelper().getTaxAmountForPayments());
					System.out.println("penality= " + rIbj.getTaxHelper().getPenalty());
					System.out.println("tax arrears= " + rIbj.getTaxHelper().getTaxArrearsRound());
					System.out.println("penality arrears= " + rIbj.getTaxHelper().getPenaltyArrears());
					System.out.println("validity= " + rIbj.getTaxHelper().getValidityTo());
					return rIbj.getTaxHelper();
				}
			}
		}
		kieSession.dispose();
		return null;
	}

	/**
	 * RuleEngineObject for data input to drl files
	 * 
	 * @param bean
	 * @return
	 */

	private RuleEngineObject prepareRuleEngineObject(String applicationNo, boolean isApplicationFromMvi,
			boolean isChassesApplication, String taxType, boolean isOtherState, String CitizenapplicationNo,
			List<ServiceEnum> serviceEnum, String permitTypeCode, String routeCode, Boolean isWeightAlt) {

		RuleEngineObject ruleEngineObj = new RuleEngineObject();
		// ruleEngineObj.setPrNo(bean.getPrNo());
		ruleEngineObj.setApplicationNo(applicationNo);
		// ruleEngineObj.setAadharNo(bean.getAadharNo());
		// ruleEngineObj.setServiceIds(bean.getServiceIds());
		ruleEngineObj.setTaxType(taxType);

		ruleEngineObj.setServiceEnum(serviceEnum);
		ruleEngineObj.setChassisApplication(isChassesApplication);
		ruleEngineObj.setApplicationFromMvi(isApplicationFromMvi);
		ruleEngineObj.setOtherState(isOtherState);
		// need to update
		if ((!isApplicationFromMvi) && (!isOtherState) && (!isChassesApplication)) {
			ruleEngineObj.setRequestFromcitizen(Boolean.TRUE);
		}
		ruleEngineObj.setPermitType(permitTypeCode);
		ruleEngineObj.setRouteCode(routeCode);
		ruleEngineObj.setIsweightAlt(isWeightAlt);
		ruleEngineObj.setCitizenapplicationNo(CitizenapplicationNo);
		return ruleEngineObj;
	}

	/**
	 * loading drl files and adding global variables
	 * 
	 * @throws FileNotFoundException
	 */

	@Override
	public KieSession load() throws FileNotFoundException {
		KieSession kiession = publish().newKieSession();
		kiession.setGlobal("ruleHelper", ruleHelper);
		kiession.setGlobal("registrationDetailDAO", registrationDetailDAO);
		kiession.setGlobal("stagingRegistrationDetailsDAO", stagingRegistrationDetailsDAO);
		kiession.setGlobal("alterationDAO", alterationDAO);
		kiession.setGlobal("regServiceDAO", regServiceDAO);
		kiession.setGlobal("masterPayperiodDAO", masterPayperiodDAO);
		kiession.setGlobal("masterTaxDAO", masterTaxDAO);
		kiession.setGlobal("masterAmountSecoundCovsDAO", masterAmountSecoundCovsDAO);
		kiession.setGlobal("masterNewGoTaxDetailsDAO", masterNewGoTaxDetailsDAO);
		kiession.setGlobal("masterTaxExcemptionsDAO", masterTaxExcemptionsDAO);
		kiession.setGlobal("masterTaxBasedDAO", masterTaxBasedDAO);
		kiession.setGlobal("permitDetailsDAO", permitDetailsDAO);
		kiession.setGlobal("taxDetailsDAO", taxDetailsDAO);
		kiession.setGlobal("propertiesDAO", propertiesDAO);
		kiession.setGlobal("masterWeightsForAltDAO", masterWeightsForAltDAO);
		kiession.setGlobal("masterTaxFuelTypeExcemptionDAO", masterTaxFuelTypeExcemptionDAO);

		// kiession.setGlobal("badrequest", badrequest);
		return kiession;
	}

	/**
	 * read and build drl files
	 * 
	 * @throws FileNotFoundException
	 */
	@Override
	public KieBase publish() throws FileNotFoundException {

		InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		this.ruleBean.setFile(new ArrayList<>());
		File dir = new File(filePath);
		for (File file : dir.listFiles()) {
			this.ruleBean.getFile().add(file.toString());
		}
		this.ruleBean.getFile().forEach((fileName) -> {
			File file = new File(fileName);
			kBuilder.add(ResourceFactory.newFileResource(file), ResourceType.DRL);
		});
		KnowledgeBuilderErrors errors = kBuilder.getErrors();
		if (!errors.isEmpty()) {
			for (KnowledgeBuilderError error : errors) {
				logger.error("Error :[{}]", error);
				System.out.println(error);
			}
		}
		kBase.addPackages(kBuilder.getKnowledgePackages());
		logger.info("{}", kBuilder.getKnowledgePackages());
		return kBase;
	}

	public String toJson(Object userVo) {
		String resultJson = StringUtils.EMPTY;
		try {
			resultJson = gson.toJson(userVo);
		} catch (JsonSyntaxException jsonE) {
			logger.error("Json Syntax Failed  [{}]", jsonE.getMessage());
			logger.error("Detailed Exception  [{}]", jsonE);
		}
		return resultJson;
	}

	@Override
	public Map<String, String> compile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KieBase publish(String filePath) throws FileNotFoundException {
		InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		this.ruleBean.setFile(new ArrayList<>());
		File dir = new File(filePath);
		/*
		 * for (File file : dir.listFiles()) {
		 * this.ruleBean.getFile().add(file.toString()); }
		 */
		this.ruleBean.getFile().add("src\\main\\resources\\rules\\payments.drl");
		this.ruleBean.getFile().forEach((fileName) -> {
			File file = new File(fileName);
			kBuilder.add(ResourceFactory.newFileResource(file), ResourceType.DRL);
		});
		KnowledgeBuilderErrors errors = kBuilder.getErrors();
		if (!errors.isEmpty()) {
			for (KnowledgeBuilderError error : errors) {
				logger.error("Error :[{}]", error);
			}
		}
		kBase.addPackages(kBuilder.getKnowledgePackages());
		logger.info("{}", kBuilder.getKnowledgePackages());
		return kBase;
	}

	@Override
	public KieSession loadAndSetGlobals() throws FileNotFoundException {
		KieSession kiession = publish(feeDrlsPath).newKieSession();
		kiession.setGlobal("paymentsHelper", paymentsHelper);
		kiession.setGlobal("feesDao", paymentFeesDeatailsDAO);

		return kiession;
	}

	@Override
	public FeePartsVO getfeeDetails(FeePartsVO feeInputs) throws FileNotFoundException {
		KieSession kieSession = this.loadAndSetGlobals();
		kieSession.insert(feeInputs);
		kieSession.fireAllRules();
		Collection<FactHandle> allHandles = kieSession.getFactHandles();
		for (FactHandle handle : allHandles) {
			if (kieSession.getObject(handle) instanceof FeePartsVO) {
				logger.info("Final OutPut ---->   :[{}]", kieSession.getObject(handle));
				FeePartsVO feeParts = (FeePartsVO) kieSession.getObject(handle);
				return feeParts;
			}
		}
		kieSession.dispose();
		return null;
	}

	public void fire(KieBase k) {

	}
}