/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diarsid.jdbc.transactions;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Diarsid
 */
public interface JdbcConnectionsSource {
    
    Connection getConnection() throws SQLException;
    
    int totalConnectionsQuantity();
    
    void closeSource();
}
