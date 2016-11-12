/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.util.List;
import java.util.Set;

import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.PerRowOperation;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

/**
 *
 * @author Diarsid
 */
class JdbcTransactionDisposableWrapper implements JdbcTransaction {
    
    private final JdbcTransaction wrappedTransaction;
    private final SingleUsageMarker marker;
    
    JdbcTransactionDisposableWrapper(JdbcTransaction actualTransaction) {
        this.wrappedTransaction = actualTransaction;
        this.marker = new SingleUsageMarker();
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
        this.wrappedTransaction.commit();       
    }

    @Override
    public boolean doesQueryHaveResults(String sql) throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        boolean result = this.wrappedTransaction.doesQueryHaveResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<Object> params) 
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
    public int countQueryResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.countQueryResults(sql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int countQueryResults(String sql, List<Object> params) 
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
    public void doQuery(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        this.wrappedTransaction.doQuery(sql, operation, params);
        this.commitTransactionAndMarkAsUsed();
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, List<Object> params) 
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
    public int doUpdate(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int result = this.wrappedTransaction.doUpdate(updateSql, params);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public int doUpdate(String updateSql, List<Object> params) 
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
    public int[] doBatchUpdate(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException {
        this.checkIfNotUsed();
        int[] result = this.wrappedTransaction.doBatchUpdate(updateSql, batchParams);
        this.commitTransactionAndMarkAsUsed();
        return result;
    }

    @Override
    public void commit() throws TransactionHandledSQLException {
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
        return new JdbcTransactionConditionalWrapper(this, condition);
    }

    @Override
    public String getSqlHistory() {
        try {
            this.commitTransactionAndMarkAsUsed();
        } finally {
            throw new UnsupportedOperationException("Not intended for obtaining SQL history.");
        }
    }
}