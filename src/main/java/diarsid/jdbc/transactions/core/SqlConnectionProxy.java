/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.jdbc.transactions.exceptions.ForbiddenTransactionOperation;
import diarsid.jdbc.transactions.exceptions.MethodTypeNotDefinedException;

/**
 *
 * @author Diarsid
 */
class SqlConnectionProxy implements InvocationHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlConnectionProxy.class);
    
    private final Connection connection;
    private final Collection<AutoCloseable> closeables;
    private final SqlConnectionMethodsAnalyzer analyzer;
    
    SqlConnectionProxy(
            Connection connection, 
            Collection<AutoCloseable> closeables, 
            SqlConnectionMethodsAnalyzer analyzer) {
        this.connection = connection;
        this.closeables = closeables;
        this.analyzer = analyzer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch ( this.analyzer.defineTypeOf(method) ) {
            case METHOD_OPENS_RESOURCE : {
                return this.registerResourceAndInvoke(method, args);
            }
            case METHOD_IS_FORBIDDEN : {
                throw new ForbiddenTransactionOperation(
                        method.getName() + " is forbidden for transaction managed Connection.");
            } 
            case METHOD_INVOCATION_IGNORED : {
                logger.warn(method.getName() + " is ignored by proxy!");
                return this.doNotInvokeAndReturnVoid();
            }
            case OTHER : {
                return this.justInvoke(method, args);
            }
            default : {
                throw new MethodTypeNotDefinedException();
            }
        }  
    }
    
    private Object doNotInvokeAndReturnVoid() {
        return null;
    }
    
    private Object registerResourceAndInvoke(Method method, Object[] args) throws Throwable {
        Object resource = method.invoke(this.connection, args);
        this.closeables.add( (AutoCloseable) resource );
        return resource;
    }
    
    private Object justInvoke(Method method, Object[] args) throws Throwable {
        return method.invoke(this.connection, args);
    }        
}
