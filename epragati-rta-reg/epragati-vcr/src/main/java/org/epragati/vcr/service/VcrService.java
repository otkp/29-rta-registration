package org.epragati.vcr.service;

import java.util.List;

import org.epragati.vcr.model.VcrBookingData;

public interface VcrService {

    public List<VcrBookingData> getVCRDetails(String docType, String docNumber);
}
