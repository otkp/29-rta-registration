package org.epragati.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.epragati.regservice.dto.CitizenFeeDetailsInput;
import org.epragati.ruleengine.service.RuleEngineService;
import org.epragati.util.GateWayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@CrossOrigin
@RestController
public class RuleController {

	@Autowired
	private RuleEngineService ruleEngineService;

	@PostMapping(value = "getPayments", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> executeRule(@RequestBody CitizenFeeDetailsInput feeDetailsInput)
			throws FileNotFoundException {
		ObjectMapper mapper = new ObjectMapper();
		String value = null;
		try {
			value = mapper.writeValueAsString(feeDetailsInput);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();
		map = gson.fromJson(value, map.getClass());
		// ruleEngineService.execute(map);
		return new GateWayResponse<>(HttpStatus.OK, "SUCCESS", "SUCCESS");
	}

	@GetMapping(value = "test", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> testController() {

		return new GateWayResponse<>(HttpStatus.OK, "test", "test");
	}

	@PostMapping(value = "breakPayments", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> breakPayments(@RequestBody FeePartsVO feeParts) throws FileNotFoundException {
		ObjectMapper mapper = new ObjectMapper();
		String value = null;
		try {
			value = mapper.writeValueAsString(feeParts);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Gson gson = new Gson();
		Map<String, String> map = new HashMap<>();
		map = gson.fromJson(value, map.getClass());
		// ruleEngineService.execute(map);
		return new GateWayResponse<>(HttpStatus.OK, "SUCCESS", "SUCCESS");
	}

}
