package org.epragati.vahan.registered.model;

import org.epragati.vahan.ResponseType;
import org.epragati.vahan.VahanResponseModel;

public class RegisteredVahanResponseModel extends VahanResponseModel<RegisteredVehicleDetails> {

    private RegisteredVehicleDetails vehicleDetails;

    public RegisteredVahanResponseModel(ResponseType responseType, String errorDesc) {
        super(responseType, errorDesc);
    }
    
    public RegisteredVahanResponseModel(ResponseType responseType, String errorDesc, RegisteredVehicleDetails vehicleDetails) {
        super(responseType, errorDesc);
        this.vehicleDetails = vehicleDetails;
    }

    public void setResponseModel(RegisteredVehicleDetails vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    @Override
    public RegisteredVehicleDetails getResponseModel() {
        return vehicleDetails;
    }

}
