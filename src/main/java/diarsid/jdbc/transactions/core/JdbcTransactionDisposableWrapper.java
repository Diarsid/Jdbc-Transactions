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
    
    private void commitTransactionAndMarkAsUsed() 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.marker.markUsed();
        this.sqlHistory = this.wrappedTransaction.getSqlHistory();
        this.wrappedTransaction.commit();       
    }
    
    @Override
    public void close() {
        this.marker.markUsed();
        this.sqlHistory = this.wrappedTransaction.getSqlHistory();
        this.wrappedTransaction.close();
    }

    @Override
    public boolean doesQueryHaveResults(String sql) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResultsVarargParams(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResultsVarargParams(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public void doQueryVarargParams(String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryVarargParams(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, RowOperation operation) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(type, sql, conversion);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStreamVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction
                .doQueryAndStreamVarargParams(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Stream<T> stream = this.wrappedTransaction.doQueryAndStream(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return stream;
    }
    
    @Override
    public void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.useJdbcDirectly(jdbcOperation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRowVarargParams(
            String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRowVarargParams(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQueryAndProcessFirstRow(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();        
    };
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Optional<T> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(type, sql, conversion);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRowVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Optional<T> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRowVarargParams(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Optional<T> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        Optional<T> opt = this.wrappedTransaction
                .doQueryAndConvertFirstRow(type, sql, conversion, params);
        this.commitTransactionAndMarkAsUsed();
        return opt;
    }

    @Override
    public int doUpdate(String updateSql) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdateVarargParams(String updateSql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdateVarargParams(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdate(String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.checkIfNotUsed();
        int[] result = this.wrappedTransaction.doBatchUpdate(updateSql, batchParams);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int[] doBatchUpdateVarargParams(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException {
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
    public JdbcTransaction ifTrue(boolean condition) throws TransactionHandledException {
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
