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
class JdbcTransactionDisposableWrapper implements JdbcTransaction {
    
    private final JdbcTransaction wrappedTransaction;
    private final SingleUsageMarker marker;
    private String sqlHistory;
    
    JdbcTransactionDisposableWrapper(JdbcTransaction actualTransaction) {
        this.wrappedTransaction = actualTransaction;
        this.marker = new SingleUsageMarker();
        this.sqlHistory = "[EMPTY]";
    }
    
    private void checkIfNotUsed() {
        if ( this.marker.isMarked() ) {
            throw new UnsupportedOperationException(
                    "This transaction is intended for single use only. " +
                            "It is not permitted to invoke more than one method.");
        }
    }
    
    private void commitTransactionAndMarkAsUsed() throws TransactionHandledSQLException {
        this.marker.markUsed();
        this.sqlHistory = this.wrappedTransaction.getSqlHistory();
        this.wrappedTransaction.commit();       
    }
    
    @Override
    public void close() {
        this.commit();
    }

    @Override
    public boolean doesQueryHaveResults(String sql) throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResultsVarargParams(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResultsVarargParams(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql, Params params) throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public void doQueryVarargParams(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryVarargParams(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(sql, conversion, type);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStreamVarargParams(
            String sql, PerRowConversion<T> conversion, Class<T> type, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStreamVarargParams(sql, conversion, type, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(sql, conversion, type, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            String sql, PerRowConversion<T> conversion, Class<T> type, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(sql, conversion, type, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.useJdbcDirectly(jdbcOperation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRowVarargParams(
            String sql, FirstRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRowVarargParams(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, FirstRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();        
    };
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Optional<Object> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(sql, conversion);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRowVarargParams(
            String sql, FirstRowConversion conversion, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Optional<Object> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRowVarargParams(sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Optional<Object> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public Optional<Object> doQueryAndConvertFirstRow(
            String sql, FirstRowConversion conversion, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        Optional<Object> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }

    @Override
    public int doUpdate(String updateSql) throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdateVarargParams(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdateVarargParams(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdate(String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int[] result = this.wrappedTransaction.doBatchUpdate(updateSql, batchParams);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int[] doBatchUpdateVarargParams(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int[] result = this.wrappedTransaction.doBatchUpdateVarargParams(updateSql, batchParams);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public void commit() {
        try {
            this.commitTransactionAndMarkAsUsed();
        } finally {
            throw new UnsupportedOperationException("Not intended for manual commit.");
        }
    }

    @Override
    public void rollbackAndTerminate() throws TransactionTerminationException {
        try {
            this.commitTransactionAndMarkAsUsed();
        } finally {
            throw new UnsupportedOperationException("Not intended for manual rollback.");
        }
    }

    @Override
    public void rollbackAndProceed() {
        try {
            this.commitTransactionAndMarkAsUsed();
        } finally {
            throw new UnsupportedOperationException("Not intended for manual rollback.");
        }
    }

    @Override
    public JdbcTransaction ifTrue(boolean condition) {
        if ( condition ) {
            return this;
        } else {
            try {
                this.commitTransactionAndMarkAsUsed();
            } catch (TransactionHandledSQLException e) {
                // Do nothing.
                // Actual exception has been processed already.
            }
            return new JdbcTransactionStub();
        }
    }
    
    @Override 
    public JdbcTransaction logHistoryAfterCommit() {
        this.wrappedTransaction.logHistoryAfterCommit();
        return this;
    }

    @Override
    public String getSqlHistory() {
        try {
            this.commitTransactionAndMarkAsUsed();
        } finally {
            return this.sqlHistory;
        }
    }
}
