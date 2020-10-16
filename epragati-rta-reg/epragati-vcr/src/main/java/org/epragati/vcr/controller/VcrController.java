package org.epragati.vcr.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.epragati.vcr.model.VcrBookingData;
import org.epragati.vcr.service.VcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VcrController {

    static Logger log = Logger.getLogger(VcrController.class.getName());
    
    @Autowired
    private VcrService serveVcr;

    @RequestMapping(value = "/getdetails/{docType}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VcrBookingData>> getVcrData(@PathVariable("docType") String docType,
            @RequestParam(value="docid", required=true) String docNumber) {
        List<VcrBookingData> vcrData = serveVcr.getVCRDetails(docType, docNumber);
        if(null != vcrData) {
            log.info("<<<<<<<<<<<<<< Returning VCR service response >>>>>>>>>>>>>>>");
            return ResponseEntity.ok(vcrData);
        }
        log.info("<<<<<<<<<<<<<<  VCR details not found >>>>>>>>>>>>>>>");
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(value = "vcrDevopsTest", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public String getWarUpStatus() {
		return "Success";
	}
}
