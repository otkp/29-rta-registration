package org.epragati.vahan.registered.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.epragati.exception.BadRequestException;
import org.epragati.vahan.registered.service.VahanRegisteredService;
import org.epragati.vahan.registered.vo.VahanVehicleDetailsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VahanRegisteredServiceImpl implements VahanRegisteredService {

	private static final Logger logger = Logger.getLogger(VahanRegisteredServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${vahan.vehicle.Url:}")
	private String vahanVehicleUrl;

	@Value("${vahan.merchantCode:}")
	private String merchantCode;

	@Override
	public VahanVehicleDetailsVO vahanVehicleBasedOnPrNo(String prNo) throws JsonParseException, JsonMappingException, IOException {
		MultiValueMap<String, String> map = null;
		ResponseEntity<String> response = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(prNo)) {
			map = new LinkedMultiValueMap<String, String>();
			map.add("merchant_code", merchantCode);
			map.add("regnNo", prNo);
		} else {
			throw new BadRequestException("PrNo Not Empty");
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		try {
			response = restTemplate.postForEntity(vahanVehicleUrl, request, String.class);

		} catch (HttpClientErrorException httpClientErrorException) {
			logger.debug(" vahanVehicleBasedOnPrNo HttpClientErrorException {}", httpClientErrorException);

		} catch (Exception e) {

			logger.debug("vahanVehicleBasedOnPrNo Exception {}:", e);
		}

		if (response == null || StringUtils.isBlank(response.getBody())) {
			return null;
		}
		String statusResponse = response.getBody();
		statusResponse = vahanDecryption(statusResponse);
		if (StringUtils.isEmpty(statusResponse)) {
			return null;
		}
		return getVahanStringToObjConvert(statusResponse);
		
	}

	private static String vahanDecryption(String strVal) {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < strVal.length() - 1; i += 2) {
				String output = strVal.substring(i, (i + 2));
				int decimal = Integer.parseInt(output, 16);
				sb.append((char) decimal);
			}
		} catch (NumberFormatException nfex) {
			logger.error("vahanDecryption NumberFormatException :{}", nfex);
		}
		return sb.toString();
	}

	public VahanVehicleDetailsVO getVahanStringToObjConvert(String response) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<VahanVehicleDetailsVO> registeredVehicleVO = mapper.readValue(response,
					new TypeReference<List<VahanVehicleDetailsVO>>() {
					});
			
			if(!CollectionUtils.isEmpty(registeredVehicleVO)&&registeredVehicleVO!=null){
				return registeredVehicleVO.stream().findFirst().get();	
			}
		} catch (Exception e) {
				VahanVehicleDetailsVO VahanVehicleDetails = mapper.readValue(response,VahanVehicleDetailsVO.class);
				if(StringUtils.isNotEmpty(VahanVehicleDetails.getResponseMessage())){
					throw new BadRequestException(VahanVehicleDetails.getResponseMessage());
				}
			logger.error("getVahanStringToObjConvert IOException :{}", e);
			throw new BadRequestException("Error Code 446");
		}
		return null;
	}
}
