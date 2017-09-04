/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;

/**
 *
 * @author Diarsid
 */
public interface Row {
    
    Object get(String columnLabel) throws TransactionHandledSQLException;
    
    <T> T get(String columnLabel, Class<T> t) throws TransactionHandledSQLException;
    
    byte[] getBytes(String columnLabel) throws TransactionHandledSQLException;
}
