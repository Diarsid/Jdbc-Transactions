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
    public void commit() throws TransactionHandledSQLException {
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
