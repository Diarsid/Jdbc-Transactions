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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.jdbc.transactions.DirectJdbcOperation;
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.Row;
import diarsid.jdbc.transactions.RowConversion;
import diarsid.jdbc.transactions.RowOperation;
import diarsid.jdbc.transactions.exceptions.JdbcFailureException;
import diarsid.jdbc.transactions.exceptions.JdbcPreparedStatementParamsException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledException;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;
import diarsid.jdbc.transactions.exceptions.TransactionTerminationException;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;

import static diarsid.jdbc.transactions.core.SqlConnectionProxyFactory.createProxy;


class JdbcTransactionWrapper implements JdbcTransaction {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionWrapper.class);
    
    private final Connection connection;
    private final Runnable delayedTearDownCancel;
    private final JdbcPreparedStatementSetter paramsSetter;
    private final JdbcTransactionSqlHistoryRecorder sqlHistory;
    private boolean ifLogSqlHistoryAnyway;
    
    JdbcTransactionWrapper(
            Connection connection, 
            Runnable delayedTearDownCancel, 
            JdbcPreparedStatementSetter argsSetter,
            JdbcTransactionSqlHistoryRecorder sqlHistory, 
            boolean logHistory) {
        this.connection = connection;
        this.delayedTearDownCancel = delayedTearDownCancel;
        this.paramsSetter = argsSetter;
        this.sqlHistory = sqlHistory;
        this.ifLogSqlHistoryAnyway = logHistory;
    }
    
    /**
     * AutoCloseable interface method.
     * JdbcTransaction extends AutoCloseable in order to be legal
     * for try-with-resources usage.
     */
    @Override
    public void close() {
        try {
            if ( ! this.connection.isClosed() ) {
                this.commit();
            } else {
                logger.info("closing attempt: connection has already been closed.");
            }
        } catch (SQLException ex) {
            logger.error("exception during connection.isClosed()", ex);
            // attempt to commit anyway.
            this.commit();
        }
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
            if ( nonNull(this.delayedTearDownCancel) ) {
                this.delayedTearDownCancel.run();
            }
        } catch (SQLException e) {
            logger.error("cannot close connection: ", e);
            throw new JdbcFailureException(
                    "It is impossible to close the database connection. " +
                    "Program will be closed");
        } finally {
            if ( this.ifLogSqlHistoryAnyway ) {
                logger.info(this.getSqlHistory());
            }
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
    public boolean doesQueryHaveResults(String sql) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql);
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
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }       
    }
    
    @Override
    public boolean doesQueryHaveResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        return this.doesQueryHaveResultsStreamed(sql, stream(params));
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        return this.doesQueryHaveResultsStreamed(sql, params.stream());
    }
    
    @Override
    public boolean doesQueryHaveResults(String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params.list());
        return this.doesQueryHaveResultsStreamed(sql, params.stream());
    }
    
    private boolean doesQueryHaveResultsStreamed(String sql, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {        
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
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public int countQueryResults(String sql) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql);
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
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }         
    }
    
    @Override
    public int countQueryResultsVarargParams(String sql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        return this.countQueryResultsStreamed(sql, stream(params));
    }
    
    @Override
    public int countQueryResults(String sql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        return this.countQueryResultsStreamed(sql, params.stream());
    }
    
    @Override
    public int countQueryResults(String sql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {        
        this.sqlHistory.add(sql, params.list());
        return this.countQueryResultsStreamed(sql, params.stream());
    }
    
    private int countQueryResultsStreamed(String sql, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
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
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    private int count(ResultSet rs) throws SQLException {
        int count = 0;
        while ( rs.next() ) {            
            count++;
        }
        return count;
    }
    
    private String concatenateParams(List<Object> params) {
        return params.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
    
    @Override
    public void doQuery(String sql, RowOperation operation) 
            throws TransactionHandledSQLException, TransactionHandledException {
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
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public void doQuery(String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params.list());
        this.doQueryStreamed(sql, operation, params.stream());
    }
    
    @Override
    public void doQuery(String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        this.doQueryStreamed(sql, operation, params.stream());
    }
    
    @Override
    public void doQueryVarargParams(String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        this.doQueryStreamed(sql, operation, stream(params));
    }
    
    private void doQueryStreamed(
            String sql, RowOperation operation, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
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
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql);
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            Row row = this.wrapResultSetIntoRow(rs);
            Stream.Builder<T> builder = Stream.builder();
            while ( rs.next() ) {
                builder.accept(conversion.convert(row));
            }            
            st.close();
            rs.close();
            return builder.build();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        return this.doQueryAndStreamStreamed(type, sql, conversion, params.stream());
    }
    
    @Override
    public <T> Stream<T> doQueryAndStream(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params.list());
        return this.doQueryAndStreamStreamed(type, sql, conversion, params.stream());
    }
    
    @Override
    public <T> Stream<T> doQueryAndStreamVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        return this.doQueryAndStreamStreamed(type, sql, conversion, stream(params));
    }
    
    private <T> Stream<T> doQueryAndStreamStreamed(
            Class<T> type, String sql, RowConversion<T> conversion, Stream<? extends Object> params) 
                    throws TransactionHandledSQLException, TransactionHandledException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            Row row = this.wrapResultSetIntoRow(rs);
            Stream.Builder<T> builder = Stream.builder();
            while ( rs.next() ) {
                builder.accept(conversion.convert(row));
            }            
            ps.close();
            rs.close();
            return builder.build();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public void useJdbcDirectly(DirectJdbcOperation jdbcOperation) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(
                "[DIRECT JDBC OPERATION] " +
                "sql history is unreacheable for this operation.");
        try {
            List<AutoCloseable> openedCloseables = new ArrayList<>();
            Connection proxiedConnection = createProxy(this.connection, openedCloseables);
            jdbcOperation.operateJdbcDirectly(proxiedConnection);
            for ( AutoCloseable resource : openedCloseables ) {
                resource.close();
            }
        } catch (SQLException ex) {
            logger.error("Exception occured during directly performed JDBC operation: ");
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error(
                    "Exception occured during directly performed JDBC operation - " +
                    "exceptiond in AutoCloseable.close(): ");
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        } 
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql);
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if ( rs.first() ) {
                operation.process(this.wrapResultSetIntoRow(rs));
            } 
            rs.close();
            st.close();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        this.doQueryAndProcessFirstRowStreamed(sql, operation, params.stream());
    }
    
    @Override
    public void doQueryAndProcessFirstRow(
            String sql, RowOperation operation, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params.list());
        this.doQueryAndProcessFirstRowStreamed(sql, operation, params.stream());
    }
    
    @Override
    public void doQueryAndProcessFirstRowVarargParams(
            String sql, RowOperation operation, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        this.doQueryAndProcessFirstRowStreamed(sql, operation, stream(params));
    }
    
    private void doQueryAndProcessFirstRowStreamed(
            String sql, RowOperation operation, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            if ( rs.first() ) {
                operation.process(this.wrapResultSetIntoRow(rs));
            } 
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql);
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            Optional<T> optional;
            if ( rs.first() ) {
                Row row = this.wrapResultSetIntoRow(rs);
                optional = Optional.ofNullable((T) conversion.convert(row));
            } else {
                optional = empty();
            }
            rs.close();
            st.close();
            return optional;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params);
        return this.doQueryAndConvertFirstRowStreamed(type, sql, conversion, params.stream());
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRow(
            Class<T> type, String sql, RowConversion<T> conversion, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, params.list());
        return this.doQueryAndConvertFirstRowStreamed(type, sql, conversion, params.stream());
    }
    
    @Override
    public <T> Optional<T> doQueryAndConvertFirstRowVarargParams(
            Class<T> type, String sql, RowConversion<T> conversion, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(sql, asList(params));
        return this.doQueryAndConvertFirstRowStreamed(type, sql, conversion, stream(params));
    }
    
    private <T> Optional<T> doQueryAndConvertFirstRowStreamed(
            Class<T> type, String sql, RowConversion conversion, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            this.paramsSetter.setParameters(ps, params);
            ResultSet rs = ps.executeQuery();
            Optional<T> optional;
            if ( rs.first() ) {
                optional = Optional.ofNullable((T) conversion.convert(this.wrapResultSetIntoRow(rs)));
            } else {
                optional = empty();
            }
            rs.close();
            ps.close();
            return optional;
        } catch (SQLException ex) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during query: ");
            logger.error(sql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public int doUpdate(String updateSql) 
            throws TransactionHandledSQLException, TransactionHandledException {
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
        } catch (Exception e) {
            logger.error("Exception occured during update: ");
            logger.error(updateSql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public int doUpdate(String updateSql, Params params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(updateSql, params.list());
        return this.doUpdateStreamed(updateSql, params.stream());
    }
    
    @Override
    public int doUpdate(String updateSql, List<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(updateSql, params);
        return this.doUpdateStreamed(updateSql, params.stream());
    }
    
    @Override
    public int doUpdateVarargParams(String updateSql, Object... params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        this.sqlHistory.add(updateSql, asList(params));
        return this.doUpdateStreamed(updateSql, stream(params));
    }
    
    private int doUpdateStreamed(String updateSql, Stream<? extends Object> params) 
            throws TransactionHandledSQLException, TransactionHandledException {
        try {
            PreparedStatement ps = this.connection.prepareStatement(updateSql);
            this.paramsSetter.setParameters(ps, params);
            int x = ps.executeUpdate();
            ps.close();
            return x;
        } catch (SQLException ex) {
            logger.error("Exception occured during update: ");
            logger.error(updateSql);
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during update: ");
            logger.error(updateSql);
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }
    
    @Override
    public int[] doBatchUpdate(String updateSql, Set<Params> batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException {
        if ( batchParams.isEmpty() ) {
            return new int[0];
        }
        this.paramsMustHaveEqualQty(batchParams, updateSql);
        this.sqlHistory.add(updateSql, batchParams);
        try {
            PreparedStatement ps = this.connection.prepareStatement(updateSql);
            for (Params params : batchParams) {
                this.paramsSetter.setParameters(ps, params.stream());
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
                logger.error(this.concatenateParams(params.list()));
            }
            logger.error("", ex);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledSQLException(ex);
        } catch (Exception e) {
            logger.error("Exception occured during batch update: ");
            logger.error(updateSql);
            logger.error("...with params: ");
            for (Params params : batchParams) {
                logger.error(this.concatenateParams(params.list()));
            }
            logger.error("", e);
            this.rollbackAndFinishAfterException();
            throw new TransactionHandledException(e);
        }  
    }

    private void paramsMustHaveEqualQty(Set<Params> batchParams, String updateSql) {
        int paramsQty = batchParams.iterator().next().qty();
        batchParams
                .stream()
                .filter(params -> params.qty() != paramsQty)
                .findFirst()
                .ifPresent(params -> this.paramsQtyAreDifferent(updateSql));
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
    public int[] doBatchUpdateVarargParams(String updateSql, Params... batchParams) 
            throws TransactionHandledSQLException, TransactionHandledException {
        return this.doBatchUpdate(updateSql, new HashSet<>(asList(batchParams)));
    }

    private Row wrapResultSetIntoRow(ResultSet rs) {
        return new Row() {
            @Override
            public Object get(String columnLabel) throws TransactionHandledSQLException {
                try {
                    return rs.getObject(columnLabel);
                } catch (SQLException ex) {
                    logger.error(format(
                            "Exception occured during Row processing with column: %s: ", columnLabel));
                    logger.error("", ex);
                    rollbackAndFinishAfterException();
                    throw new TransactionHandledSQLException(ex);
                }
            }

            @Override
            public <T> T get(String columnLabel, Class<T> t) throws TransactionHandledSQLException {
                try {
                    return (T) rs.getObject(columnLabel);
                } catch (SQLException ex) {
                    logger.error(format(
                            "Exception occured during Row processing with column: %s: ", columnLabel));
                    logger.error("", ex);
                    rollbackAndFinishAfterException();
                    throw new TransactionHandledSQLException(ex);
                }
            }

            @Override
            public byte[] getBytes(String columnLabel) throws TransactionHandledSQLException {
                try {
                    return rs.getBytes(columnLabel);
                } catch (SQLException ex) {
                    logger.error(format(
                            "Exception occured during Row processing with column: %s: ", columnLabel));
                    logger.error("", ex);
                    rollbackAndFinishAfterException();
                    throw new TransactionHandledSQLException(ex);
                }
            }
        };
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
    public JdbcTransaction logHistoryAfterCommit() {
        this.ifLogSqlHistoryAnyway = true;
        return this;
    }
    
    @Override
    public String getSqlHistory() {
        return this.sqlHistory.getHistory();
    }
}
