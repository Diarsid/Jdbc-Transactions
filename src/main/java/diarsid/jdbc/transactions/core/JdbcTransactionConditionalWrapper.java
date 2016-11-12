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
class JdbcTransactionConditionalWrapper implements JdbcTransaction {
    
    private final boolean condition;
    private final JdbcTransaction wrappedTransaction;
    
    JdbcTransactionConditionalWrapper(JdbcTransaction trueTransaction, boolean condition) {
        this.condition = condition;
        this.wrappedTransaction = trueTransaction;
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

    @Override
    public boolean doesQueryHaveResults(String sql) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doesQueryHaveResults(sql);
        } else {
            return false;
        }
    }

    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doBatchUpdate(updateSql, batchParams);
        } else {
            return this.operationNotPerformedIntArrayValue();
        }
    }

    @Override
    public int[] doBatchUpdate(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doBatchUpdate(updateSql, batchParams);
        } else {
            return this.operationNotPerformedIntArrayValue();
        }
    }

    @Override
    public void commit() throws TransactionHandledSQLException {
        if ( this.condition ) {
            this.wrappedTransaction.commit();
        } 
    }

    @Override
    public void rollbackAndTerminate() throws TransactionTerminationException {
        if ( this.condition ) {
            this.wrappedTransaction.rollbackAndTerminate();
        } 
    }

    @Override
    public void rollbackAndProceed() {
        if ( this.condition ) {
            this.wrappedTransaction.rollbackAndProceed();
        } 
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doesQueryHaveResults(sql, params);
        } else {
            return this.operationNotPerformedBooleanValue();
        }
    }

    @Override
    public boolean doesQueryHaveResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doesQueryHaveResults(sql, params);
        } else {
            return this.operationNotPerformedBooleanValue();
        }
    }

    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doesQueryHaveResults(sql, params);
        } else {
            return this.operationNotPerformedBooleanValue();
        }
    }

    @Override
    public int countQueryResults(String sql) throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.countQueryResults(sql);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int countQueryResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.countQueryResults(sql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int countQueryResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.countQueryResults(sql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.countQueryResults(sql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            
        } 
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            this.wrappedTransaction.doQuery(sql, operation, params);
        } 
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            this.wrappedTransaction.doQuery(sql, operation, params);
        } 
    }

    @Override
    public void doQuery(String sql, PerRowOperation operation) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            this.wrappedTransaction.doQuery(sql, operation);
        } 
    }

    @Override
    public int doUpdate(String updateSql) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doUpdate(updateSql);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.wrappedTransaction.doUpdate(updateSql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int doUpdate(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.doUpdate(updateSql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }

    @Override
    public int doUpdate(String updateSql, List<Object> params) 
            throws TransactionHandledSQLException {
        if ( this.condition ) {
            return this.doUpdate(updateSql, params);
        } else {
            return this.operationNotPerformedIntValue();
        }
    }
    
    @Override
    public JdbcTransaction ifTrue(boolean condition) {
        throw new UnsupportedOperationException("It is not permitted to stack .ifTrue() calls!"); 
    }

    @Override
    public String getSqlHistory() {
        if ( this.condition ) {
            return this.wrappedTransaction.getSqlHistory();
        } else {
            return "";
        }
    }

}
