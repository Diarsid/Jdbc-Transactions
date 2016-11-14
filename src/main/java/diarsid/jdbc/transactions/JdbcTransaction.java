/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import java.util.List;
import java.util.Optional;
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
    
    void doQueryAndProcessFirstRow(String sql, FirstRowOperation operation) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRow(String sql, FirstRowOperation operation, Object... params) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRow(String sql, FirstRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRow(String sql, FirstRowOperation operation, Params params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(String sql, FirstRowConversion conversion) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(String sql, FirstRowConversion conversion, Object... params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(String sql, FirstRowConversion conversion, List<Object> params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(String sql, FirstRowConversion conversion, Params params) 
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
