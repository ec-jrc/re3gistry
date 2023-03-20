/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package eu.europa.ec.re3gistry2.crudinterface;

import eu.europa.ec.re3gistry2.model.RegUserCodes;
import java.util.Date;
import java.util.List;

/**
 *
 * @author oriol
 */
public interface IRegUserCodesManager {
    public RegUserCodes get(String uuid) throws Exception;
    public List<RegUserCodes> getByRegUser(String regUser) throws Exception;
    public RegUserCodes getByCode(String code) throws Exception;
    public List<RegUserCodes> getByInsertDate(Date date) throws Exception;
    public List<RegUserCodes> getAll() throws Exception;
    
    public boolean add(RegUserCodes regUserCode) throws Exception;
    public boolean delete(RegUserCodes regUserCode) throws Exception;
}
