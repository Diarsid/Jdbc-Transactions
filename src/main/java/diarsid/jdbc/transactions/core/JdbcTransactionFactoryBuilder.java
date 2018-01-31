/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

import diarsid.jdbc.transactions.JdbcConnectionsSource;
import diarsid.jdbc.transactions.SqlHistoryFormattingAlgorithm;

import static java.util.Objects.isNull;

/**
 *
 * @author Diarsid
 */
public class JdbcTransactionFactoryBuilder {
    
    private final JdbcConnectionsSource connectionsSource;
    private JdbcTransactionGuard transactionGuard;
    private JdbcPreparedStatementParamSetter[] additionalSetters;
    private SqlHistoryFormattingAlgorithm algorithm;
    
    private JdbcTransactionFactoryBuilder(JdbcConnectionsSource connectionsSource) {
        this.connectionsSource = connectionsSource;
    }
    
    public static JdbcTransactionFactoryBuilder buildTransactionFactoryWith(
            JdbcConnectionsSource connectionsSource) {
        return new JdbcTransactionFactoryBuilder(connectionsSource);
    }
    
    public JdbcTransactionFactoryBuilder withGuardWaitingOnSeconds(
            int seconds) {
        this.transactionGuard = new JdbcTransactionGuardReal(
                seconds, this.connectionsSource.totalConnectionsQuantity());
        return this;
    }
    
    public JdbcTransactionFactoryBuilder withSetters(
            JdbcPreparedStatementParamSetter... additionalSetters) {
        this.additionalSetters = additionalSetters;
        return this;
    }
    
    public JdbcTransactionFactoryBuilder withSqlHistoryFormattingAlgorithm(
            SqlHistoryFormattingAlgorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }
    
    public JdbcTransactionFactory done() {
        if ( isNull(this.algorithm) ) {
            this.algorithm = new StandardSqlHistoryFormattingAlgorithm();
        }
        if ( isNull(this.transactionGuard) ) {
            this.transactionGuard = new JdbcTransactionGuardMock();
        }
        
        JdbcPreparedStatementSetter setter = new JdbcPreparedStatementSetter(additionalSetters);
        return new JdbcTransactionFactory(
                this.connectionsSource, this.transactionGuard, setter, this.algorithm);
    }
}
