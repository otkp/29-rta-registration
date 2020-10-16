package org.epragati.vahan.registered.service;

import java.io.IOException;

import org.epragati.vahan.registered.vo.VahanVehicleDetailsVO;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface VahanRegisteredService {

	VahanVehicleDetailsVO vahanVehicleBasedOnPrNo (String prNo) throws JsonParseException, JsonMappingException, IOException;
}
