/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author asus
 */
public interface IevenementService<T> {
    
       public void ajouterevenement(T t) throws SQLException;
    public void modifierevenement(T t) throws SQLException;
    public void supprimerevenement(T t) throws SQLException;
    public List<T> recupererevenement() throws SQLException;
    
}
