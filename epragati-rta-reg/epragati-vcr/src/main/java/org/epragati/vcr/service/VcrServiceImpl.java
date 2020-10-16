package org.epragati.vcr.service;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.epragati.vcr.DocType;
import org.epragati.vcr.VcrStringUtils;
import org.epragati.vcr.model.VcrBookingData;
import org.epragati.vcr.model.VcrDataModel;
import org.epragati.vcr.model.VcrOffenseDetails;
import org.epragati.vcr.model.VcrResponseModel;
import org.epragati.vcr.model.VcrServiceResponse;
import org.springframework.stereotype.Service;
import org.tempuri.LicRegVCRDetailsLocator;
import org.tempuri.LicRegVCRDetailsSoap;

@Service
public class VcrServiceImpl implements VcrService {

    static Logger log = Logger.getLogger(VcrServiceImpl.class.getName());

    private LicRegVCRDetailsLocator vcrStub;

    public VcrServiceImpl() {
        vcrStub = new LicRegVCRDetailsLocator();
    }

    @Override
    public List<VcrBookingData> getVCRDetails(String docType, String docNumber) {
        try {
            LicRegVCRDetailsSoap soap = vcrStub.getLicRegVCRDetailsSoap();
            JAXBContext jContext = JAXBContext.newInstance(VcrServiceResponse.class, VcrDataModel.class);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            String responseStr = null;

            if (DocType.DL.name().equalsIgnoreCase(docType)) {
                responseStr = soap.getLicRegVCRDetails(DocType.DL.name(), docNumber, null);
            } else if (DocType.RC.name().equalsIgnoreCase(docType)) {
                responseStr = soap.getLicRegVCRDetails(DocType.RC.name(), null, docNumber);
            }

            if (null != responseStr) {
                if (!(responseStr.length() > 1) && VcrStringUtils.isNumeric(responseStr)) {
                    log.info("<<<<<<<<<<<<<< VCR service response came null OR 0 >>>>>>>>>>>>>>>");
                    return null;
                }
                VcrDataModel vcrData = (VcrDataModel) unmarshaller.unmarshal(new StringReader(responseStr));
                List<VcrBookingData> bookings = new ArrayList<VcrBookingData>();
                for (VcrBookingData booking : vcrData.getBookingData()) {
                    bookings.add(booking);
                    List<VcrOffenseDetails> offenseDetails = new ArrayList<VcrOffenseDetails>();
                    for (VcrOffenseDetails offense : vcrData.getOffenseDetails()) {
                        if (booking.getVcrNum() != null
                                && booking.getVcrNum().equalsIgnoreCase(offense.getVcrNumber())) {
                            offenseDetails.add(offense);
                        }
                    }
                    booking.setOffenseDetails(offenseDetails);
                }
                
                VcrResponseModel vcrResponse = new VcrResponseModel();
                vcrResponse.setBookingData(bookings);
                return bookings;
            }
        } catch (ServiceException e) {
            log.info(e.getMessage());
        } catch (RemoteException e) {
            log.info(e.getMessage());
        } catch (JAXBException e) {
            log.info(e.getMessage());
        }
        log.info("<<<<<<<<<<<<<< VCR service response came null OR 0 >>>>>>>>>>>>>>>");
        return null;
    }

}
