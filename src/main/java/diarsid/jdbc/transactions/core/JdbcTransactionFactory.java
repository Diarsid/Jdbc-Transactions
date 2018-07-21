/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.jdbc.transactions.JdbcConnectionsSource;
import diarsid.jdbc.transactions.JdbcTransaction;
import diarsid.jdbc.transactions.core.sqlhistory.FormattingAlgorithm;
import diarsid.jdbc.transactions.core.sqlhistory.SqlHistoryRecorder;
import diarsid.jdbc.transactions.exceptions.TransactionHandledSQLException;

import static diarsid.jdbc.transactions.core.sqlhistory.SqlHistory.getSqlHistoryRecorder;

/**
 *
 * @author Diarsid
 */
public class JdbcTransactionFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionFactory.class);
    
    private final JdbcConnectionsSource connectionsSource;
    private final JdbcTransactionGuard transactionGuard;
    private final JdbcPreparedStatementSetter argsSetter;
    private final Function<String, FormattingAlgorithm> formattingAlgorithmProducer;
    private boolean logHistory;
    
    JdbcTransactionFactory(
            JdbcConnectionsSource connectionsSource, 
            JdbcTransactionGuard transactionGuard,
            JdbcPreparedStatementSetter argsSetter, 
            Function<String, FormattingAlgorithm> formattingAlgorithmProducer) {
        this.connectionsSource = connectionsSource;
        this.transactionGuard = transactionGuard;
        this.argsSetter = argsSetter;
        this.formattingAlgorithmProducer = formattingAlgorithmProducer;
        this.logHistory = false;
    }
    
    public JdbcTransaction createTransaction() throws TransactionHandledSQLException {
        try {
            return this.setNewTransaction();
        } catch (SQLException e) {
            logger.error("SQLException occured during JDBC Connection obtaining: ", e);
            throw new TransactionHandledSQLException(e);
        }        
    }
    
    public JdbcTransaction createDisposableTransaction() throws TransactionHandledSQLException {
        try {
            return new JdbcTransactionDisposableWrapper(this.setNewTransaction());
        } catch (SQLException e) {
            logger.error("SQLException occured during JDBC Connection obtaining: ", e);
            throw new TransactionHandledSQLException(e);
        }        
    }
    
    public void logHistory(boolean b) {
        this.logHistory = b;
    }

    private JdbcTransaction setNewTransaction() throws SQLException {
        Connection connection = connectionsSource.getConnection();
        SqlHistoryRecorder sqlHistoryRecorder = 
                getSqlHistoryRecorder(this.formattingAlgorithmProducer);
        connection.setAutoCommit(false);
        Runnable connectionTearDown =
            this.transactionGuard.accept(connection, sqlHistoryRecorder);
        return new JdbcTransactionWrapper(
                connection, 
                connectionTearDown, 
                this.argsSetter, 
                sqlHistoryRecorder, 
                this.logHistory);
    }    
    
    public void close() {
        this.transactionGuard.stop();
        this.connectionsSource.closeSource();
        logger.info("closed.");
    }
}
