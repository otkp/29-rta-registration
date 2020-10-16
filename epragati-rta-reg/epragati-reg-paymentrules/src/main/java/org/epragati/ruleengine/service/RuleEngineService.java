package org.epragati.ruleengine.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.epragati.master.dto.TaxHelper;
import org.epragati.rules.FeePartsVO;
import org.epragati.util.payment.ServiceEnum;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

/**
 * 
 * @author saikiran.kola
 *
 */
public interface RuleEngineService {

	/**
	 * Compile File
	 * 
	 * @return
	 */
	public Map<String, String> compile();

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	KieBase publish(String filePath) throws FileNotFoundException;

	/**
	 * load drl files
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	KieSession load() throws FileNotFoundException;

	/**
	 * 
	 * @param map
	 * @return
	 * @throws FileNotFoundException
	 */
	FeePartsVO getfeeDetails(FeePartsVO feeParts) throws FileNotFoundException;

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	KieSession loadAndSetGlobals() throws FileNotFoundException;

	
	KieBase publish() throws FileNotFoundException;

	
	TaxHelper execute(String applicationNo, boolean isApplicationFromMvi, boolean isChassesApplication, String taxType,
			boolean isOtherState, String CitizenapplicationNo, List<ServiceEnum> serviceEnum, String permitTypeCode,
			String routeCode, Boolean isWeightAlt) throws FileNotFoundException;

}
