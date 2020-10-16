package org.epragati.vahan.unregistered;

import java.io.StringReader;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.epragati.constants.MessageKeys;
import org.epragati.util.AppMessages;
import org.epragati.vahan.ResponseType;
import org.epragati.vahan.VahanResponseModel;
import org.epragati.vahan.exception.IllegalEngineNumberException;
import org.epragati.vahan.unregistered.model.UnregisteredChassisInfo;
import org.epragati.vahan.unregistered.model.UnregisteredVahanResponseModel;
import org.springframework.beans.factory.annotation.Autowired;

import nic.transport.homologation.web.service.Dataportws;
import nic.transport.homologation.web.service.Getdataws;

public class UnregisteredVahanClientImpl implements UnregisteredVahanClient {

	@Autowired
	private AppMessages appMsg;

	private static final Logger logger = Logger.getLogger(UnregisteredVahanClientImpl.class);
	public static final Integer NUMBER_FIVE = 5;

	private final String userId;
	private final String trasactionPassword;
	private final Dataportws dataportws;

	public UnregisteredVahanClientImpl(String userId, String trasactionPassword) {
		this.userId = userId;
		this.trasactionPassword = trasactionPassword;
		this.dataportws = new Getdataws().getDataportwsPort();
	}

	public VahanResponseModel<UnregisteredChassisInfo> getChassisInfo(String chassisNumber, String engineNumber)
			throws IllegalEngineNumberException {
		try {
			//engineNumber = StringsUtil.getLastChars(engineNumber, NUMBER_FIVE);
		} catch (IllegalArgumentException e) {
			//	logger.error(appMsg.getLogMessage(MessageKeys.FLOW_INVALID_ACTION_FOR_FLOW), flowDetails.getAction());
			//logger.error(appMsg.getLogMessage(MessageKeys.VAHAN_NOT_VALID_ENGINENO));
			throw new IllegalEngineNumberException("Invalid Engine No");
		}
		logger.info("before handleResponse.");
		return handleResponse(dataportws.getChassisInfo(userId, trasactionPassword, chassisNumber, engineNumber));
	}

	private VahanResponseModel<UnregisteredChassisInfo> handleResponse(String responseString) {
		UnregisteredChassisInfo vd = null;
		try {
			logger.info("enter handleResponse---------------.");
			logger.debug(responseString);
			
			if (Objects.isNull(responseString)) {
				return new UnregisteredVahanResponseModel(ResponseType.ERROR, responseString);
			}
			logger.info("before getUnmarshaller------------.");
			vd = (UnregisteredChassisInfo) getUnmarshaller(UnregisteredChassisInfo.class)
					.unmarshal(new StringReader(responseString));
			logger.info("after getUnmarshaller------------.");
			return new UnregisteredVahanResponseModel(ResponseType.SUCCESS, responseString, vd);
		
		} catch (JAXBException e) {
			logger.debug(e.getMessage());
			logger.debug("response string : " + responseString);
			return new UnregisteredVahanResponseModel(ResponseType.ERROR, responseString);
		} catch (IllegalArgumentException e) {
			logger.debug(e.getMessage());
			logger.debug("response string : " + responseString);
			return new UnregisteredVahanResponseModel(ResponseType.ERROR, responseString);
		}
	}

	private Unmarshaller getUnmarshaller(Class<?> clazz) throws JAXBException {
		logger.info("calling getUnmarshaller------------.");
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		return jaxbContext.createUnmarshaller();
	}

}