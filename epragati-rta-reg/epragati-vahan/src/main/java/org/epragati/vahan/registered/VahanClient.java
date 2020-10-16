package org.epragati.vahan.registered;

import org.epragati.vahan.VahanResponseModel;
import org.epragati.vahan.registered.model.RegisteredVehicleDetails;

public abstract interface VahanClient
{
  public abstract VahanResponseModel<RegisteredVehicleDetails> getDetails(String paramString1, String paramString2);
  
  public abstract VahanResponseModel<RegisteredVehicleDetails> getChasisDetails(String paramString1, String paramString2);
}
