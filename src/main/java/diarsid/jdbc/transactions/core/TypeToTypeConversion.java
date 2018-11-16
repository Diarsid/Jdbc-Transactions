/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

/**
 *
 * @author Diarsid
 */
interface TypeToTypeConversion {
    
    boolean matchBothTypes(Class sqlType, Class javaType);
    
    Object convert(Object sqlObject);
}
