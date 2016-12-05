/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diarsid.jdbc.transactions.core;

import java.sql.Connection;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 *
 * @author Diarsid
 */
class SqlConnectionProxyFactory {
    
    private static final SqlConnectionMethodsAnalyzer ANALYZER;
    private static final Logger logger = LoggerFactory.getLogger(SqlConnectionProxyFactory.class);
    
    static {
        ANALYZER = new SqlConnectionMethodsAnalyzer();
    }
    
    private SqlConnectionProxyFactory() {
    }
    
    private static Class[] getConnectionClasses(Connection connection) {
        Class[] classes;
        classes = connection.getClass().getInterfaces();
        if ( classes.length == 0 ) {
            logger.warn("Direct JDBC operation is used! Actual connection will be proxied.");
            logger.warn("java.sql.Connection is implemented by: " + connection.getClass().getCanonicalName());
            logger.warn("This class does not provide any information about its impleneted interfaces!");
            logger.warn("java.sql.Connection will be used instead.");
            return new Class[] { Connection.class };
        } else {
            return classes;
        }
    }
    
    static Connection createProxy(Connection connection, Collection<AutoCloseable> closeables) {        
        return (Connection) newProxyInstance(
                connection.getClass().getClassLoader(), 
                getConnectionClasses(connection), 
                new SqlConnectionProxy(connection, closeables, ANALYZER));
    }
}
