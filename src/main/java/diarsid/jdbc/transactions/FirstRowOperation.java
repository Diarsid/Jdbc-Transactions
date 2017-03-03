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

@FunctionalInterface
public interface FirstRowOperation {
    
    void process(Row row) throws TransactionHandledSQLException, Exception;
}
