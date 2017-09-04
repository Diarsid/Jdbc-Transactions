/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import diarsid.jdbc.transactions.DirectJdbcOperation;
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.RowConversion;
import diarsid.jdbc.transactions.RowOperation;
import diarsid.jdbc.transactions.exceptions.TransactionHandledException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

/**
 *
 * @author Diarsid
 */
class JdbcTransactionStub implements JdbcTransaction {
        
    JdbcTransactionStub() {
    }

    private int operationNotPerformedIntValue() {
        return 0;
    }

    private int[] operationNotPerformedIntArrayValue() {
        return new int[0];
    }

    private boolean operationNotPerformedBooleanValue() {
        return false;
    }
    
    private String operationNotPerformedStringValue() {
        return "";
    }
    
    private <T> Optional<T> operationNotPerformedOptionalValue(Class<T> type) {
        return Optional.empty();
    }    
    
    private <T> Stream<T> operationNotPerformedEmptyStream(Class<T> type) {
        return Stream.empty();
    }
    
    @Override
    public void close() {
        this.commit();
    }

    @Override
    public boolean doesQueryHaveResults(String sql) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedBooleanValue();
    }

    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntArrayValue();
    }

    @Override
    public int[] doBatchUpdateVarargParams(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntArrayValue();
    }

    @Override
    public void commit() {
        // do nothing;
    }

    @Override
    public void rollbackAndTerminate() throws TransactionTerminationException {
        // do nothing;
    }

    @Override
    public void rollbackAndProceed() {
        // do nothing;
    }
    
    @Override
    public boolean doesQueryHaveResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedBooleanValue();
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedBooleanValue();
    }

    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedBooleanValue();
    }

    @Override
    public int countQueryResults(String sql) throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int countQueryResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int countQueryResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public void doQueryVarargParams(String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        // do nothing;
    }

    @Override
    public void doQuery(String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }

    @Override
    public void doQuery(String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStreamVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public void doQueryAndProcessFirstRow(String sql, RowOperation operation) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRowVarargParams(
            String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledException {
        // do nothing;
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue(type);
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRowVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue(type);
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue(type);
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue(type);
    }

    @Override
    public void doQuery(String sql, RowOperation operation) 
            throws TransactionHandledSQLException {
        // do nothing;
    }

    @Override
    public int doUpdate(String updateSql) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int doUpdateVarargParams(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int doUpdate(String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }
    
    @Override
    public JdbcTransaction ifTrue(boolean condition) {
        // if this stub was created - there was ifTrue(FALSE) in previous calls;        
        return this;
    }
    
    @Override 
    public JdbcTransaction logHistoryAfterCommit() {
        return this;
    }

    @Override
    public String getSqlHistory() {
        return this.operationNotPerformedStringValue();
    }
}
