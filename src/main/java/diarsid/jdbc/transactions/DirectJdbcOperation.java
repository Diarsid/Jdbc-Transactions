/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import java.sql.Connection;
import java.sql.SQLException;

import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;

/**
 *
 * @author Diarsid
 */

@FunctionalInterface
public interface DirectJdbcOperation {
    
    void operateJdbcDirectly(Connection connection) 
            throws SQLException, TransactionHandledSQLException;
}
