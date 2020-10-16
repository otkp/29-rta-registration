package org.epragati.aadhar.DAO;

import java.io.Serializable;

import org.epragati.aadhar.Entity.AadhaarTransactionDTO;
import org.springframework.stereotype.Repository;
@Repository
public interface AadhaarTransactionDAO extends BaseRepository<AadhaarTransactionDTO, Serializable> {

}
