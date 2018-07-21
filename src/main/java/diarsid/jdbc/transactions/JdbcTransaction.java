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
            throws TransactionHandledSQLException, TransactionHandledException;
    
    boolean doesQueryHaveResultsVarargParams(
            String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    boolean doesQueryHaveResults(
            String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    boolean doesQueryHaveResults(
            String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int countQueryResults(
            String sql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int countQueryResultsVarargParams(
            String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int countQueryResults(
            String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int countQueryResults(
            String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQuery(
            RowOperation operation, String sql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQuery(
            RowOperation operation, String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQuery(
            RowOperation operation, String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQueryVarargParams(
            RowOperation operation, String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Stream<T> doQueryAndStream(
            RowConversion<T> conversion, String sql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Stream<T> doQueryAndStream(
            RowConversion<T> conversion, String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Stream<T> doQueryAndStream(
            RowConversion<T> conversion, String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Stream<T> doQueryAndStreamVarargParams(
            RowConversion<T> conversion, String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
        
    void doQueryAndProcessFirstRow(
            RowOperation operation, String sql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQueryAndProcessFirstRowVarargParams(
            RowOperation operation, String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQueryAndProcessFirstRow(
            RowOperation operation, String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void doQueryAndProcessFirstRow(
            RowOperation operation, String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Optional<T> doQueryAndConvertFirstRow(
            RowConversion<T> conversion, String sql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Optional<T> doQueryAndConvertFirstRowVarargParams(
            RowConversion<T> conversion, String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Optional<T> doQueryAndConvertFirstRow(
            RowConversion<T> conversion, String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    <T> Optional<T> doQueryAndConvertFirstRow(
            RowConversion<T> conversion, String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int doUpdate(
            String updateSql) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int doUpdate(
            String updateSql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int doUpdateVarargParams(
            String updateSql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int doUpdate(
            String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int[] doBatchUpdate(
            String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    int[] doBatchUpdateVarargParams(
            String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException;
            
    void commit() 
            throws TransactionHandledSQLException, TransactionHandledException;
    
    void rollbackAndTerminate() 
            throws TransactionTerminationException, TransactionHandledException;
    
    void rollbackAndProceed();
    
    JdbcTransaction ifTrue(boolean condition) throws TransactionHandledException;
    
    JdbcTransaction logHistoryAfterCommit();
    
    String getSqlHistory();
}
