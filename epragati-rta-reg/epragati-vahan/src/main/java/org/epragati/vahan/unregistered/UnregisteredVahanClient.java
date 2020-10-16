package org.epragati.vahan.unregistered;

import org.epragati.vahan.VahanResponseModel;
import org.epragati.vahan.exception.IllegalEngineNumberException;
import org.epragati.vahan.unregistered.model.UnregisteredChassisInfo;

public interface UnregisteredVahanClient {

    public abstract VahanResponseModel<UnregisteredChassisInfo> getChassisInfo(String chassisNumber, String engineNumber) throws IllegalEngineNumberException;
    
}
