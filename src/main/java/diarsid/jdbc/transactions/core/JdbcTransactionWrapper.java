/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.PerRowOperation;
import diarsid.jdbc.transactions.Row;
import diarsid.jdbc.transactions.exceptions.JdbcFailureException;
import diarsid.jdbc.transactions.exceptions.JdbcPreparedStatementParamsException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;


class JdbcTransactionWrapper implements JdbcTransaction {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionWrapper.class);
    
    private final Connection connection;
    private final ScheduledFuture delayedTearDown;
    private final JdbcPreparedStatementSetter paramsSetter;
    private final JdbcTransactionSqlHistoryRecorder sqlHistory;
    
    JdbcTransactionWrapper(
            Connection connection, 
            ScheduledFuture delayedTearDown, 
            JdbcPreparedStatementSetter argsSetter,
            JdbcTransactionSqlHistoryRecorder sqlHistory) {
        this.connection = connection;
        this.delayedTearDown = delayedTearDown;
        this.paramsSetter = argsSetter;
        this.sqlHistory = sqlHistory;
    }
    
    private void restoreAutoCommit() {
        try {
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.warn("cannot restore connection autocommit mode: ", e);
            // no actions, just proceed and try to close
            // connection.
        }
    }
    
    private void rollbackTransaction() {
        try {
            this.connection.rollback();
        } catch (SQLException ex) {
            logger.warn("cannot rollback connection: ", ex);
            // no actions, just proceed and try to close
            // connection.
        }
    }
    
    private void closeConnectionAnyway() {
        try {
            this.connection.close();
            this.delayedTearDown.cancel(true);
        } catch (SQLException e) {
            logger.error("cannot close connection: ", e);
            throw new JdbcFailureException(
                    "It is impossible to close the database connection. " +
                    "Program will be closed");
        }
    }
    
    private void rollbackAndFinishAfterException() {
        this.rollbackTransaction();
        this.restoreAutoCommit();
        this.closeConnectionAnyway();  
        this.logger.error(this.sqlHistory.getHistory());
        this.sqlHistory.clear();
    }
    
    @Override
    public void rollbackAndTerminate() throws TransactionTerminationException {
        this.rollbackTransaction();
        this.restoreAutoCommit();
        this.closeConnectionAnyway();        
        this.sqlHistory.clear();
        throw new TransactionTerminationException("transaction has been terminated normally.");
    }
    
    @Override
    public void rollbackAndProceed() {
        this.rollbackTransaction();
        this.sqlHistory.rollback();
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql) throws TransactionHandledSQLException {
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            boolean hasResult = resultSet.first();
            resultSet.close();
            statement.close();
            return hasResult;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }        
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            boolean hasResult = rs.first();
            ps.close();
            rs.close();
            return hasResult;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("...with params: " + this.concatenateParams(params));
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.doesQueryHaveResults(sql, params.toArray());
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        return this.doesQueryHaveResults(sql, params.get());
    }
    
    @Override
    public int countQueryResults(String sql) 
            throws TransactionHandledSQLException {
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            int resultingRowsQty = this.count(resultSet);
            resultSet.close();
            statement.close();
            return resultingRowsQty;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }        
    }
    
    @Override
    public int countQueryResults(String sql, Object... params) 
            throws TransactionHandledSQLException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            int resultingRowsQty = this.count(rs);
            rs.close();
            ps.close();
            return resultingRowsQty;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("...with params: " + this.concatenateParams(params));
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    private int count(ResultSet rs) throws SQLException {
        int count = 0;
        while ( rs.next() ) {            
            count++;
        }
        return count;
    }
    
    @Override
    public int countQueryResults(String sql, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.countQueryResults(sql, params.toArray());
    }
    
    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException {
        return this.countQueryResults(sql, params.get());
    }
    
    @Override
    public void doQuery(String sql, PerRowOperation operation, Object... params) 
            throws TransactionHandledSQLException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            Row row = this.wrapResultSetIntoRow(rs);
            while ( rs.next() ) {
                operation.process(row);
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("...with params: " + this.concatenateParams(params));
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    private String concatenateParams(Object[] params) {
        return stream(params)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
    
    @Override
    public void doQuery(String sql, PerRowOperation operation, Params params) 
            throws TransactionHandledSQLException {
        this.doQuery(sql, operation, params.get());
    }
    
    @Override
    public void doQuery(String sql, PerRowOperation operation, List<Object> params) 
            throws TransactionHandledSQLException {
        this.doQuery(sql, operation, params.toArray());
    }
    
    @Override
    public void doQuery(String sql, PerRowOperation operation) 
            throws TransactionHandledSQLException {
        this.sqlHistory.add(sql);
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Row row = this.wrapResultSetIntoRow(rs);
            while ( rs.next() ) {
                operation.process(row);
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    @Override
    public int doUpdate(String updateSql) 
            throws TransactionHandledSQLException {
        this.sqlHistory.add(updateSql);
        try {
            Statement ps = this.connection.createStatement();
            int x = ps.executeUpdate(updateSql);
            ps.close();
            return x;
        } catch (SQLException ex) {
            logger.error("Exception occured during update: ");
            logger.error(updateSql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    @Override
    public int doUpdate(String updateSql, Object... params) 
            throws TransactionHandledSQLException {
        this.sqlHistory.add(updateSql, params);
        try {
            PreparedStatement ps = this.connection.prepareStatement(updateSql);
            this.paramsSetter.setParameters(ps, params);
            int x = ps.executeUpdate();
            ps.close();
            return x;
        } catch (SQLException ex) {
            logger.error("Exception occured during update: ");
            logger.error(updateSql);
            logger.error("...with params: " + this.concatenateParams(params));
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }
    
    @Override
    public int doUpdate(String updateSql, Params params) throws TransactionHandledSQLException {
        return this.doUpdate(updateSql, params.get());
    }
    
    @Override
    public int doUpdate(String updateSql, List<Object> params) 
            throws TransactionHandledSQLException {
        return this.doUpdate(updateSql, params.toArray());
    }
    
    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException {
        this.nonEmptyParamsOnly(batchParams, updateSql);
        this.paramsMustHaveEqualQty(batchParams, updateSql);
        this.sqlHistory.add(updateSql, batchParams);
        try {
            PreparedStatement ps = this.connection.prepareStatement(updateSql);
            for (Params params : batchParams) {
                this.paramsSetter.setParameters(ps, params.get());
                ps.addBatch();
            }           
            int[] x = ps.executeBatch();
            ps.close();
            return x;
        } catch (SQLException ex) {
            logger.error("Exception occured during batch update: ");
            logger.error(updateSql);
            logger.error("...with params: ");
            for (Params params : batchParams) {
                logger.error(this.concatenateParams(params.get()));
            }
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        }
    }

    private void paramsMustHaveEqualQty(Set<Params> batchParams, String updateSql) {
        int paramsQty = batchParams.iterator().next().qty();
        batchParams
                .stream()
                .filter(params -> params.qty() != paramsQty)
                .findFirst()
                .ifPresent(params -> {this.paramsQtyAreDifferent(updateSql);});
    }
    
    private void paramsQtyAreDifferent(String sql) {
        throw new JdbcPreparedStatementParamsException(
                format("PreparedStatement parameters qty differs for SQL: %s", sql));
    }

    private void nonEmptyParamsOnly(Set<Params> batchParams, String updateSql) {
        if ( batchParams.isEmpty() ) {
            throw new JdbcPreparedStatementParamsException(
                    format("PreparedStatement parameters are empty in SQL: %s", updateSql));
        }
    }
    
    @Override
    public int[] doBatchUpdate(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException {
        return this.doBatchUpdate(updateSql, new HashSet<>(asList(batchParams)));
    }

    private Row wrapResultSetIntoRow(ResultSet rs) {
        Row row = (columnLabel) -> {
            try {
                return rs.getObject(columnLabel);
            } catch (SQLException ex) {
                logger.error(format(
                        "Exception occured during Row processing with column: %s: ", columnLabel));
                logger.error("", ex);
                this.rollbackAndFinishAfterException();
                throw new TransactionHandledSQLException(ex);
            }
        };
        return row;
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException commitException) {
            logger.error("Exception occured during commiting: ");
            logger.error("", commitException);
            try {
                this.connection.rollback();
                logger.error("transaction has been rolled back.");
            } catch (SQLException rollbackException) {
                logger.error("Exception occured during rollback of connection failed to commit: ");
                logger.error("", rollbackException);
                // No actions after rollbackAndTerminate has failed.
                // Go to finally block and finish transaction.
            }
        } finally {
            this.restoreAutoCommit();
            this.closeConnectionAnyway();
            this.sqlHistory.clear();
        }        
    }
    
    @Override
    public JdbcTransaction ifTrue(boolean condition) {
        if ( condition ) {
            return this;
        } else {
            return new JdbcTransactionStub();
        }        
    }
    
    @Override
    public String getSqlHistory() {
        return this.sqlHistory.getHistory();
    }
}
