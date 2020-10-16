package org.epragati.taxservice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.epragati.master.dto.MasterPayperiodDTO;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxHelper;
import org.epragati.payments.vo.TransactionDetailVO;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.regservice.vo.RegServiceVO;
import org.epragati.util.payment.OtherStateApplictionType;
import org.epragati.util.payment.ServiceEnum;
import org.springframework.data.util.Pair;

public interface CitizenDroolsTaxService {

	TaxHelper getTaxDetails(String applicationNo, boolean isApplicationFromMvi, boolean isChassesApplication,
			String taxType,boolean isOtherState, String CitizenapplicationNo,List<ServiceEnum> serviceEnum,String permitType,String routeCode, Boolean isWeightAlt);

	Pair<Optional<MasterPayperiodDTO>, Boolean> getPayPeroidForBoth(Optional<MasterPayperiodDTO> Payperiod,
			String seatingCapacity, Integer gvw);

	boolean checkTaxUpToDateOrNote(boolean isApplicationFromMvi, boolean isChassesApplication,
			RegServiceDTO regServiceDTO, RegistrationDetailsDTO registrationDetails,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, String taxType);

	TaxHelper getLastPaidTax(RegistrationDetailsDTO stagingRegDetails, RegServiceDTO regServiceDTO,
			boolean isApplicationFromMvi, LocalDate currentTaxTill,
			StagingRegistrationDetailsDTO stagingRegistrationDetails, boolean isChassesApplication,
			List<String> taxTypes, boolean isOtherState);

	LocalDate validity(String taxType);

	Double getOldCovTax(String cov, String seatingCapacity, Integer ulw, Integer gvw, String stateCode,
			String permitcode, String routeCode);

	OtherStateApplictionType getOtherStateVehicleStatus(RegServiceVO regService);

	TaxHelper greenTaxCalculation(String applicationNo, List<ServiceEnum> serviceEnum);

	Integer getGvwWeightForCitizen(RegistrationDetailsDTO registrationDetails);

	TaxHelper greenTaxCalculation(String applicationNo, List<ServiceEnum> serviceEnum, RegServiceDTO regDTO);

}
