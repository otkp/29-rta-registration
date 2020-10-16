package org.epragati.vahan;

import java.util.Objects;

public enum StatusMessage {

    OK("OK"),
    VEHICLE_DATA_NOT_FOUND("Vehicle Data Not Found");
    
    private String label;
    
    private StatusMessage(String label) {
        this.label = label;
    }
    
    public static StatusMessage getStatusMessage(String label) {
        if (!Objects.isNull(label)) {
            if (OK.label.equals(label)) {
                return OK;
            }
        }
        return VEHICLE_DATA_NOT_FOUND;
    }
    
    public String label() {
        return this.label;
    }
    
}
