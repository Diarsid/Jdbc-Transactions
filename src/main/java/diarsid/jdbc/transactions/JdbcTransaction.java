/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import java.util.List;
import java.util.Set;

import diarsid.jdbc.transactions.core.Params;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

/**
 *
 * @author Diarsid
 */
public interface JdbcTransaction {
    
    boolean doesQueryHaveResults(String sql) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResults(String sql, Object... params) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(String sql) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(String sql, Object... params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException;
    
    void doQuery(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException;
    
    void doQuery(String sql, PerRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException;
    
    void doQuery(String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException;
    
    void doQuery(String sql, PerRowOperation operation) 
            throws TransactionHandledSQLException;     
    
    int doUpdate(String updateSql) 
            throws TransactionHandledSQLException;
    
    int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException;
    
    int doUpdate(String updateSql, Object... params) 
            throws TransactionHandledSQLException;
    
    int doUpdate(String updateSql, List<Object> params) 
            throws TransactionHandledSQLException;
    
    int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException;
    
    int[] doBatchUpdate(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException;
            
    void commit() 
            throws TransactionHandledSQLException;
    
    void rollbackAndTerminate() 
            throws TransactionTerminationException;
    
    void rollbackAndProceed();
    
    JdbcTransaction ifTrue(boolean condition);
    
    String getSqlHistory();
}
