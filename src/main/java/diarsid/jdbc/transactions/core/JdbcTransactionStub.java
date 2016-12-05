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
import diarsid.jdbc.transactions.FirstRowConversion;
import diarsid.jdbc.transactions.FirstRowOperation;
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.PerRowConversion;
import diarsid.jdbc.transactions.PerRowOperation;
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
    
    private Optional<Object> operationNotPerformedOptionalValue() {
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
    public int[] doBatchUpdate(String updateSql, Params... batchParams) 
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
    public boolean doesQueryHaveResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedBooleanValue();
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<Object> params) 
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
    public int countQueryResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int countQueryResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        // do nothing;
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedEmptyStream(type);
    }
    
    @Override
    public void doQueryAndProcessFirstRow(String sql, FirstRowOperation operation) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        // do nothing; 
    }
    
    @Override
    public void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledException {
        // do nothing;
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue();
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue();
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue();
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, Params params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedOptionalValue();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation) 
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
    public int doUpdate(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }

    @Override
    public int doUpdate(String updateSql, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.operationNotPerformedIntValue();
    }
    
    @Override
    public JdbcTransaction ifTrue(boolean condition) {
        // if this stub was created - there was ifTrue(FALSE) in previous calls;        
        return this;
    }

    @Override
    public String getSqlHistory() {
        return this.operationNotPerformedStringValue();
    }
}
