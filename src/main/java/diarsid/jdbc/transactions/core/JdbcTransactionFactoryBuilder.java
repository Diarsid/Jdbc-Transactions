/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions.core;

import java.util.function.Function;

import diarsid.jdbc.transactions.JdbcConnectionsSource;
import diarsid.jdbc.transactions.core.sqlhistory.FormattingAlgorithm;
import diarsid.jdbc.transactions.core.sqlhistory.FormattingAlgorithmStandardImpl;

import static java.util.Objects.isNull;

/**
 *
 * @author Diarsid
 */
public class JdbcTransactionFactoryBuilder {
    
    private final JdbcConnectionsSource connectionsSource;
    private JdbcTransactionGuard transactionGuard;
    private JdbcPreparedStatementParamSetter[] additionalSetters;
    private Function<String, FormattingAlgorithm> formattingAlgorithmProducer;
    
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
            Function<String, FormattingAlgorithm> formattingAlgorithmProducer) {
        this.formattingAlgorithmProducer = formattingAlgorithmProducer;
        return this;
    }
    
    public JdbcTransactionFactory done() {
        if ( isNull(this.formattingAlgorithmProducer) ) {
            this.formattingAlgorithmProducer = 
                    (paramsLineTabSign) -> new FormattingAlgorithmStandardImpl(paramsLineTabSign);
        }
        if ( isNull(this.transactionGuard) ) {
            this.transactionGuard = new JdbcTransactionGuardMock();
        }
        
        JdbcPreparedStatementSetter setter = new JdbcPreparedStatementSetter(additionalSetters);
        SqlTypeToJavaTypeConverter typesConverter = new SqlTypeToJavaTypeConverter();
        return new JdbcTransactionFactory(
                this.connectionsSource, 
                this.transactionGuard, 
                setter, 
                typesConverter, 
                this.formattingAlgorithmProducer);
    }
}
