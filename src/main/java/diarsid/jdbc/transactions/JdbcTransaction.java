/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import diarsid.jdbc.transactions.core.Params;
import diarsid.jdbc.transactions.exceptions.TransactionHandledException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

/**
 *
 * @author Diarsid
 */
public interface JdbcTransaction extends AutoCloseable {
    
    @Override
    void close();
    
    boolean doesQueryHaveResults(
            String sql) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResultsVarargParams(
            String sql, Object... params) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResults(
            String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    boolean doesQueryHaveResults(
            String sql, Params params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(
            String sql) 
            throws TransactionHandledSQLException;
    
    int countQueryResultsVarargParams(
            String sql, Object... params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(
            String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    int countQueryResults(
            String sql, Params params) 
            throws TransactionHandledSQLException;
    
    void doQuery(
            String sql, PerRowOperation operation) 
            throws TransactionHandledSQLException;
    
    void doQuery(
            String sql, PerRowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    void doQuery(
            String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException;
    
    void doQueryVarargParams(
            String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException;
    
    void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledException;
    
    <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type) 
            throws TransactionHandledSQLException;
    
    <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, Params params) 
            throws TransactionHandledSQLException;
    
    <T> Stream<T> doQueryAndStreamVarargParams(
            String sql, PerRowConversion<T> conversion, Class<T> type, Object... params) 
            throws TransactionHandledSQLException;
        
    void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRowVarargParams(
            String sql, FirstRowOperation operation, Object... params) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, Params params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRowVarargParams(
            String sql, FirstRowConversion conversion, Object... params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, Params params) 
            throws TransactionHandledSQLException;
    
    int doUpdate(
            String updateSql) 
            throws TransactionHandledSQLException;
    
    int doUpdate(
            String updateSql, Params params) 
            throws TransactionHandledSQLException;
    
    int doUpdateVarargParams(
            String updateSql, Object... params) 
            throws TransactionHandledSQLException;
    
    int doUpdate(
            String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException;
    
    int[] doBatchUpdate(
            String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException;
    
    int[] doBatchUpdateVarargParams(
            String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException;
            
    void commit() 
            throws TransactionHandledSQLException;
    
    void rollbackAndTerminate() 
            throws TransactionTerminationException;
    
    void rollbackAndProceed();
    
    JdbcTransaction ifTrue(boolean condition);
    
    String getSqlHistory();
}
