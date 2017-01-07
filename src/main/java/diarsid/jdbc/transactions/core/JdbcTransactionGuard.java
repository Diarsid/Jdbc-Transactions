/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.Connection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.jdbc.transactions.exceptions.JdbcFailureException;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * @author Diarsid
 */
public class JdbcTransactionGuard {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionGuard.class);
    
    private final ScheduledThreadPoolExecutor scheduler;
    private final int transactionTimeout;
        
    public JdbcTransactionGuard(int notCommitedTransactionTeardownTimeout) {
        this.transactionTimeout = notCommitedTransactionTeardownTimeout;
        this.scheduler  = new ScheduledThreadPoolExecutor(10);
    }
    
    public JdbcTransactionGuard(int notCommitedTransactionTeardownTimeout, int schedulerSize) {
        this.transactionTimeout = notCommitedTransactionTeardownTimeout;
        this.scheduler  = new ScheduledThreadPoolExecutor(schedulerSize);
    }
    
    ScheduledFuture accept(
            Connection connection, JdbcTransactionSqlHistoryRecorder sqlHistory) {
        return this.scheduler.schedule(
                this.delayedTearDownOf(connection, sqlHistory), this.transactionTimeout, SECONDS);
    }
    
    ScheduledFuture accept(
            Connection connection, 
            JdbcTransactionSqlHistoryRecorder sqlHistory, 
            int timeout, 
            TimeUnit unit) {
        return this.scheduler.schedule(
                this.delayedTearDownOf(connection, sqlHistory), timeout, unit);
    }
    
    private Runnable delayedTearDownOf(
            Connection connection, JdbcTransactionSqlHistoryRecorder sqlHistory) {
        return () -> {
            try {
                logger.warn("Transaction has not been committed or rolled back properly.");
                logger.warn(sqlHistory.getHistory());
                sqlHistory.clear();
                connection.rollback();
                connection.setAutoCommit(true);
                connection.close();
                logger.warn("Transaction has been rolled back and closed by JdbcTransactionGuard!");                
            } catch (Exception ex) {
                logger.error("cannot teardown connection: ", ex);
                throw new JdbcFailureException(
                        format("%s cannot tear down JDBC Connection.", 
                               this.getClass().getCanonicalName()));
            }
        };
    }
    
    void stop() {
        this.scheduler.shutdown();
        logger.info("stopped.");
    }
}
