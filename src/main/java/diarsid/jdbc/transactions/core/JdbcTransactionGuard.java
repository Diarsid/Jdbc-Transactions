/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Diarsid
 */
interface JdbcTransactionGuard {

    Runnable accept(Connection connection, JdbcTransactionSqlHistoryRecorder sqlHistory);

    Runnable accept(
            Connection connection, 
            JdbcTransactionSqlHistoryRecorder sqlHistory, 
            int timeout, 
            TimeUnit unit);

    void stop();
    
}
