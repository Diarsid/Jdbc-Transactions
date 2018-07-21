/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;


import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import diarsid.jdbc.transactions.core.sqlhistory.SqlHistoryRecorder;


public class JdbcTransactionGuardMock implements JdbcTransactionGuard {

    @Override
    public Runnable accept(
            Connection connection, SqlHistoryRecorder sqlHistory) {
        return null;
    }

    @Override
    public Runnable accept(
            Connection connection, SqlHistoryRecorder sqlHistory, int timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public void stop() {
        // nothing to do
    }
    
}
